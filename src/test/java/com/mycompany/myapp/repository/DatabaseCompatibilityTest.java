package com.mycompany.myapp.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.domain.*;
import java.sql.DriverManager;
import java.time.Instant;
import java.util.concurrent.TimeUnit;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.PostgreSQLContainer;

class DatabaseCompatibilityTest {

    @ParameterizedTest
    @ValueSource(strings = { "mysql", "postgresql" })
    @Timeout(value = 5, unit = TimeUnit.MINUTES)
    void migratesTwiceAndRoundTripsBase64UsingRealDatabases(String engine) throws Exception {
        boolean postgres = engine.equals("postgresql");
        try (JdbcDatabaseContainer<?> container = postgres
            ? new PostgreSQLContainer<>("postgres:17-alpine")
            : new MySQLContainer<>("mysql:9.2.0")) {
            container.start();
            String schema = postgres ? "qualk" : container.getDatabaseName();
            try (var connection = DriverManager.getConnection(container.getJdbcUrl(), container.getUsername(), container.getPassword())) {
                if (postgres) {
                    try (var statement = connection.createStatement()) {
                        statement.execute("CREATE SCHEMA qualk");
                    }
                }
                var database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
                database.setDefaultSchemaName(schema);
                database.setLiquibaseSchemaName(schema);
                try (var liquibase = new Liquibase(
                    "config/liquibase/" + (postgres ? "master-postgresql.xml" : "master.xml"),
                    new ClassLoaderResourceAccessor(), database
                )) {
                    liquibase.update(new Contexts("prod"), new LabelExpression());
                    // A redeploy must not break Liquibase checksums or rerun migrations.
                    liquibase.update(new Contexts("prod"), new LabelExpression());
                }
            }
            Configuration configuration = new Configuration()
                .setProperty("hibernate.connection.url", container.getJdbcUrl())
                .setProperty("hibernate.connection.username", container.getUsername())
                .setProperty("hibernate.connection.password", container.getPassword())
                .setProperty("hibernate.cache.use_second_level_cache", "false")
                .setProperty("hibernate.hbm2ddl.auto", "none")
                .setProperty("hibernate.physical_naming_strategy", "org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy");
            if (postgres) configuration.setProperty("hibernate.default_schema", schema);
            for (Class<?> entity : new Class<?>[] { User.class, Authority.class, Post.class, Comment.class, Like.class, Share.class, Follow.class }) {
                configuration.addAnnotatedClass(entity);
            }
            try (var factory = configuration.buildSessionFactory(); var session = factory.openSession()) {
                var tx = session.beginTransaction();
                if (postgres) {
                    assertThat(session.createSelectionQuery("from User", User.class).getResultList()).isEmpty();
                }
                User user = new User();
                user.setLogin("database-smoke");
                user.setPassword("x".repeat(60));
                user.setActivated(true);
                user.setCreatedBy("system");
                user.setCreatedDate(Instant.now());
                session.persist(user);
                Post post = new Post();
                String image = "data:image/png;base64," + "QUJD".repeat(25000);
                post.setImageData(image);
                post.setUserId(user.getId());
                post.setUser(user);
                post.setCaption("Ảnh PostgreSQL và MySQL");
                post.setCreatedBy("system");
                post.setCreatedDate(Instant.now());
                session.persist(post);
                session.flush();
                session.clear();
                assertThat(session.find(Post.class, post.getId()).getImageData()).isEqualTo(image);
                assertThat(session.find(Post.class, post.getId()).getCaption()).isEqualTo(post.getCaption());
                tx.commit();
            }
        }
    }
}
