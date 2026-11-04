# Deploy Qualk Backend on Render

Use Docker runtime. Render has no native Java/Gradle runtime: https://render.com/docs/language-support.

For the standalone Qualk-Backend repository:

| Setting | Value |
|---|---|
| Branch | main |
| Root Directory | empty |
| Dockerfile Path | ./Dockerfile |
| Docker Build Context | . |
| Docker Command | empty (image ENTRYPOINT) |
| Health Check Path | /management/health/readiness |

`render.yaml` describes a Blueprint; it does not automatically update a manually created service.

## Environment

```dotenv
SPRING_PROFILES_ACTIVE=prod,render,supabase
SUPABASE_DB_URL=jdbc:postgresql://<session-pooler-host>:5432/postgres?sslmode=require
SUPABASE_DB_USERNAME=postgres.<project-ref>
SUPABASE_DB_PASSWORD=<database password>
JHIPSTER_SECURITY_AUTHENTICATION_JWT_BASE64_SECRET=<Base64 of 64 random bytes>
```

Generate the JWT secret locally, save only in Render Environment, and never commit it:

```powershell
python -c "import base64,secrets; print(base64.b64encode(secrets.token_bytes(64)).decode())"
```

All instances need the same key. Rotation invalidates existing JWTs.
Docker defaults to `prod,render,supabase`, binds `0.0.0.0:${PORT:10000}`, and disables Spring Docker Compose.
Use the session pooler on 5432 for persistent Spring Boot connections.
For AI, configure the existing AWS credential provider, region and model through secrets/environment. Configure production SMTP before enabling registration emails.

## MySQL local and PostgreSQL on Render

Both JDBC drivers remain available. Local `dev` still uses MySQL. For MySQL production, set `SPRING_PROFILES_ACTIVE=prod,render` and the three `SPRING_DATASOURCE_*` variables instead.
The `supabase` profile uses the private `qualk` schema. Create it once as database owner before first boot:

```sql
CREATE SCHEMA IF NOT EXISTS qualk;
REVOKE ALL ON SCHEMA qualk FROM PUBLIC;
```

Keep `qualk` outside Supabase Data API exposed schemas. Hibernate and Liquibase both use this schema; no Auth/Storage tables are changed.
The separate PostgreSQL changelog preserves existing MySQL migration checksums. PostgreSQL stores Base64 images as TEXT; JDBC String binding also supports the existing MySQL LONGBLOB columns.
PostgreSQL initializes roles only, without default-password accounts. Existing MySQL users/posts are not copied or automatically synchronized. Data migration is a separate operation; existing MySQL data stays intact.
`.env.supabase` is not auto-loaded or included in Docker. Configure Render Environment explicitly.

## Validate and deploy

```powershell
.\gradlew.bat -Pprod test --tests '*DatabaseCompatibilityTest' --tests '*FeedQueryTest'
docker build -t qualk-backend:render .
```

The compatibility test uses disposable MySQL/PostgreSQL containers, runs Liquibase twice, and round-trips a large image through Hibernate.
Image build needs no DB secrets and runs no remote migration. Runtime uses Liquibase migrations; do not enable ddl-auto=update.
`.dockerignore` excludes local credentials, AWS files, logs and build output. The final image runs only the executable bootJar under UID 10001.
Commit the reviewed deploy changes and push the branch Render follows. Do not blindly stage unrelated work or secrets. Then use Manual Deploy -> Deploy latest commit and verify startup logs plus readiness.
A successful image build alone does not prove the database and JWT environment are configured correctly.

## Rollback

Select the previous known-good deploy in Render when one exists. Keep MySQL available during migration. Do not drop or reset either database as a rollback step. PostgreSQL application data stays in its dedicated schema.
