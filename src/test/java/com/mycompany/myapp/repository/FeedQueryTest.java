package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.*;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.Query;

/** Hibernate parses the actual repository queries without connecting to an application database. */
class FeedQueryTest {

    @Test
    void validatesProjectionConstructorsAndQueries() {
        Configuration configuration = new Configuration()
            .setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect")
            .setProperty("hibernate.boot.allow_jdbc_metadata_access", "false")
            .setProperty("hibernate.cache.use_second_level_cache", "false")
            .setProperty("hibernate.hbm2ddl.auto", "none");
        for (Class<?> entity : new Class<?>[] { Post.class, User.class, Authority.class, Like.class, Share.class, Comment.class }) {
            configuration.addAnnotatedClass(entity);
        }
        try (var factory = configuration.buildSessionFactory(); var session = factory.openSession()) {
            for (Class<?> repository : new Class<?>[] {
                PostRepository.class,
                LikeRepository.class,
                ShareRepository.class,
                CommentRepository.class,
            }) {
                for (var method : repository.getDeclaredMethods()) {
                    Query query = method.getAnnotation(Query.class);
                    if (query != null && !query.nativeQuery()) {
                        session.createQuery(query.value());
                        if (!query.countQuery().isEmpty()) session.createQuery(query.countQuery());
                    }
                }
            }
        }
    }
}
