# Qualk - Social App Backend

A social media application (Instagram/Locket-style) built with Spring Boot and React. This repository contains the backend microservice.

## Project Overview

**Qualk** is a photo/video sharing social app where users can:
- Create and share posts with friends
- Discover trending content
- Like and comment on posts
- Follow other users

**Team:** Dang & Quang

---

## Development Roadmap

### **PHASE 1: MVP (2-3 months)**
**Goal:** Build core social features to launch MVP

#### Features:
1. **User Authentication & Profiles**
   - [ ] User registration (email/password)
   - [ ] User login/logout
   - [ ] User profile page (name, bio, avatar, follower count)
   - [ ] Edit profile (update bio, avatar, basic info)
   - [ ] Password reset

2. **Post Creation & Sharing**
   - [ ] Upload photo/video
   - [ ] Add caption/description
   - [ ] Create post (save to backend)
   - [ ] Delete own posts
   - [ ] View post details

3. **Feed & Discovery**
   - [ ] Home feed (posts from followed users)
   - [ ] Explore/Discover feed (trending/public posts)
   - [ ] View user's profile posts
   - [ ] Pagination/infinite scroll on feeds

4. **Social Interactions**
   - [ ] Like/Unlike posts
   - [ ] Comment on posts
   - [ ] View comments on a post
   - [ ] Delete own comments
   - [ ] Like count display

5. **Follow System**
   - [ ] Follow/Unfollow users
   - [ ] View follower list
   - [ ] View following list
   - [ ] Follow recommendations (optional)

6. **Search & Navigation**
   - [ ] Search users by username
   - [ ] Basic navigation (Home, Explore, Profile, Search)
   - [ ] Mobile-responsive UI

7. **Backend Infrastructure**
   - [ ] User service & authentication
   - [ ] Post/Media service (CRUD)
   - [ ] Follow relationship management
   - [ ] Like/Comment service
   - [ ] File storage/serving for images/videos

---

### **PHASE 2: Enhancements (2-3 months)**
**Goal:** Add advanced features and improve user engagement

#### Features:
- [ ] Real-time notifications (likes, comments, follows)
- [ ] Direct messaging / DMs
- [ ] Stories / Ephemeral content
- [ ] Image filters & basic editing
- [ ] Advanced search (hashtags, locations)
- [ ] User recommendations / discovery algorithm
- [ ] Trending posts/hashtags
- [ ] Share posts to external platforms
- [ ] User blocking / reporting
- [ ] Analytics dashboard

---

## Tech Stack

- **Backend:** Spring Boot (JHipster 8.11.0)
- **Frontend:** React
- **Database:** MySQL
- **Architecture:** Microservices

---

## Quick Start

This application was generated using JHipster 8.11.0, you can find documentation and help at [https://www.jhipster.tech/documentation-archive/v8.11.0](https://www.jhipster.tech/documentation-archive/v8.11.0).

This is a "microservice" application intended to be part of a microservice architecture, please refer to the [Doing microservices with JHipster][] page of the documentation for more information.

This application is configured for Service Discovery and Configuration with Consul. On launch, it will refuse to start if it is not able to connect to Consul at [http://localhost:8500](http://localhost:8500). For more information, read our documentation on [Service Discovery and Configuration with Consul][].

## Project Structure

Node is required for generation and recommended for development. `package.json` is always generated for a better development experience with prettier, commit hooks, scripts and so on.

In the project root, JHipster generates configuration files for tools like git, prettier, eslint, husky, and others that are well known and you can find references in the web.

`/src/*` structure follows default Java structure.

- `.yo-rc.json` - Yeoman configuration file
  JHipster configuration is stored in this file at `generator-jhipster` key. You may find `generator-jhipster-*` for specific blueprints configuration.
- `.yo-resolve` (optional) - Yeoman conflict resolver
  Allows to use a specific action when conflicts are found skipping prompts for files that matches a pattern. Each line should match `[pattern] [action]` with pattern been a [Minimatch](https://github.com/isaacs/minimatch#minimatch) pattern and action been one of skip (default if omitted) or force. Lines starting with `#` are considered comments and are ignored.
- `.jhipster/*.json` - JHipster entity configuration files
- `/src/main/docker` - Docker configurations for the application and services that the application depends on

## Development

To start your application in the dev profile, run:

```
./gradlew
```

For further instructions on how to develop with JHipster, have a look at [Using JHipster in development][].

### Doing API-First development using openapi-generator-cli

[OpenAPI-Generator]() is configured for this application. You can generate API code from the `src/main/resources/swagger/api.yml` definition file by running:

```bash
./gradlew openApiGenerate
```

Then implements the generated delegate classes with `@Service` classes.

To edit the `api.yml` definition file, you can use a tool such as [Swagger-Editor](). Start a local instance of the swagger-editor using docker by running: `docker compose -f src/main/docker/swagger-editor.yml up -d`. The editor will then be reachable at [http://localhost:7742](http://localhost:7742).

Refer to [Doing API-First development][] for more details.

## Building for production

### Packaging as jar

To build the final jar and optimize the QualkBackend application for production, run:

```
./gradlew -Pprod clean bootJar
```

To ensure everything worked, run:

```
java -jar build/libs/*.jar
```

Refer to [Using JHipster in production][] for more details.

### Packaging as war

To package your application as a war in order to deploy it to an application server, run:

```
./gradlew -Pprod -Pwar clean bootWar
```

### JHipster Control Center

JHipster Control Center can help you manage and control your application(s). You can start a local control center server (accessible on http://localhost:7419) with:

```
docker compose -f src/main/docker/jhipster-control-center.yml up
```

## Testing

### Spring Boot tests

To launch your application's tests, run:

```
./gradlew test integrationTest jacocoTestReport
```

### Gatling

Performance tests are run by [Gatling][] and written in Scala. They're located in [src/test/java/gatling/simulations](src/test/java/gatling/simulations).

You can execute all Gatling tests with

```
./gradlew gatlingRun.
```

## Others

### Code quality using Sonar

Sonar is used to analyse code quality. You can start a local Sonar server (accessible on http://localhost:9001) with:

```
docker compose -f src/main/docker/sonar.yml up -d
```

Note: we have turned off forced authentication redirect for UI in [src/main/docker/sonar.yml](src/main/docker/sonar.yml) for out of the box experience while trying out SonarQube, for real use cases turn it back on.

You can run a Sonar analysis with using the [sonar-scanner](https://docs.sonarqube.org/display/SCAN/Analyzing+with+SonarQube+Scanner) or by using the gradle plugin.

Then, run a Sonar analysis:

```
./gradlew -Pprod clean check jacocoTestReport sonarqube -Dsonar.login=admin -Dsonar.password=admin
```

Additionally, Instead of passing `sonar.password` and `sonar.login` as CLI arguments, these parameters can be configured from [sonar-project.properties](sonar-project.properties) as shown below:

```
sonar.login=admin
sonar.password=admin
```

For more information, refer to the [Code quality page][].

### Docker Compose support

JHipster generates a number of Docker Compose configuration files in the [src/main/docker/](src/main/docker/) folder to launch required third party services.

For example, to start required services in Docker containers, run:

```
docker compose -f src/main/docker/services.yml up -d
```

To stop and remove the containers, run:

```
docker compose -f src/main/docker/services.yml down
```

[Spring Docker Compose Integration](https://docs.spring.io/spring-boot/reference/features/dev-services.html) is enabled by default. It's possible to disable it in application.yml:

```yaml
spring:
  ...
  docker:
    compose:
      enabled: false
```

You can also fully dockerize your application and all the services that it depends on.
To achieve this, first build a Docker image of your app by running:

```sh
npm run java:docker
```

Or build a arm64 Docker image when using an arm64 processor os like MacOS with M1 processor family running:

```sh
npm run java:docker:arm64
```

Then run:

```sh
docker compose -f src/main/docker/app.yml up -d
```

For more information refer to [Using Docker and Docker-Compose][], this page also contains information on the Docker Compose sub-generator (`jhipster docker-compose`), which is able to generate Docker configurations for one or several JHipster applications.
