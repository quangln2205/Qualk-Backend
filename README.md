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

- **Backend:** Spring Boot 3.4.2 (JHipster 8.9.0)
- **Frontend:** React
- **Database:** MySQL
- **Architecture:** Monolith (scalable to microservices in Phase 2)

---

## Qualk-Backend Structure Overview

### **Root Level**
```
Qualk-Backend/
├── src/main/
│   ├── java/com/mycompany/myapp/          # Main source code
│   └── resources/                          # Config & static files
├── src/test/                               # Unit & integration tests
├── gradle/                                 # Gradle wrapper configs
├── build.gradle                            # Gradle build file
├── gradle.properties                       # Gradle properties
└── .yo-rc.json                             # JHipster configuration
```

### **Java Source Structure** (`src/main/java/com/mycompany/myapp/`)

```
myapp/
├── QualkBackendApp.java                    # Main Spring Boot application entry point
├── ApplicationWebXml.java                  # Web app configuration
├── GeneratedByJHipster.java                # JHipster marker
│
├── aop/                                    # Aspect-Oriented Programming
│   └── Logging, security aspects
│
├── config/                                 # Configuration classes
│   ├── SecurityConfiguration.java          # Spring Security setup
│   ├── SecurityJwtConfiguration.java       # JWT authentication
│   ├── DatabaseConfiguration.java          # DB connection
│   ├── CacheConfiguration.java             # Caching (Redis/Caffeine)
│   ├── AsyncConfiguration.java             # Async/threading
│   ├── JacksonConfiguration.java           # JSON serialization
│   ├── WebConfigurer.java                  # Web/CORS setup
│   └── Other Spring beans & configs
│
├── domain/                                 # Entity models
│   ├── AbstractAuditingEntity.java         # Base entity (created/modified tracking)
│   └── (User, Post, Comment, Follow entities will go here)
│
├── management/                             # Actuator endpoints (health, metrics)
│
├── repository/                             # Data access layer
│   └── (UserRepository, PostRepository, CommentRepository, etc.)
│
├── service/                                # Business logic layer
│   └── (UserService, PostService, CommentService, etc.)
│
├── security/                               # Security utilities
│   ├── JWT handling
│   ├── User authentication
│   └── Authorization checks
│
├── web/
│   └── rest/                               # REST API controllers
│       └── (UserResource, PostResource, CommentResource, etc.)
│
└── web/rest/vm/                            # View Models (DTOs)
    └── Request/Response objects
```

### **Resources** (`src/main/resources/`)

```
resources/
├── config/                                 # Environment configs
│   ├── application.yml                     # Default config
│   ├── application-dev.yml                 # Development profile
│   └── application-prod.yml                # Production profile
│
├── i18n/                                   # Internationalization (multi-language)
│   └── messages_*.properties
│
├── swagger/                                # OpenAPI/Swagger docs
│   └── api.yml                             # API definitions
│
├── logback-spring.xml                      # Logging configuration
├── banner.txt                              # Startup banner
│
└── static/                                 # Static assets (if needed)
```

### **Build Configuration**

- **`build.gradle`** - Main build file with dependencies (Spring Boot, JPA, Security, etc.)
- **`settings.gradle`** - Gradle project settings
- **`gradle.properties`** - Version properties
- **`gradle/libs.versions.toml`** - Central dependency management

### **Key JHipster Components**

| Component | Purpose |
|-----------|---------|
| **JWT** | Stateless authentication |
| **Liquibase** | Database schema migrations |
| **OpenAPI** | Auto-generated API documentation |
| **Spring Data JPA** | Database ORM |
| **Spring Security** | Authentication & Authorization |

---

## N-Tier Architecture Explanation

This backend follows a **Layered N-Tier Architecture** pattern. Each layer has a specific responsibility and communicates only with adjacent layers, making the codebase maintainable, testable, and scalable.

### **Architecture Layers (Data Flow: Top → Bottom)**

```
┌─────────────────────────────────────────────────────┐
│  PRESENTATION LAYER (web/rest/)                     │
│  ↓ HTTP Requests/Responses                          │
├─────────────────────────────────────────────────────┤
│  SERVICE LAYER (business logic)                     │
│  ↓ Business rules & transformations                 │
├─────────────────────────────────────────────────────┤
│  REPOSITORY LAYER (data access)                     │
│  ↓ Database queries (JPA)                           │
├─────────────────────────────────────────────────────┤
│  DOMAIN LAYER (entity models)                       │
│  ↓ ORM mappings to tables                           │
├─────────────────────────────────────────────────────┤
│  DATABASE                                           │
└─────────────────────────────────────────────────────┘
```

### **Each Layer Explained**

#### **1. PRESENTATION LAYER** (`web/rest/`)
**Purpose:** REST API endpoints & HTTP handlers

**Responsibilities:**
- Accept HTTP requests from frontend
- Parse request parameters & validate input
- Call service layer methods
- Return JSON responses with appropriate status codes

**Example:**
```java
@RestController
@RequestMapping("/api/users")
public class UserResource {
    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO user) {
        UserDTO created = userService.create(user);
        return ResponseEntity.ok(created);
    }
}
```

#### **2. SERVICE LAYER** (`service/`)
**Purpose:** Business logic & application rules

**Responsibilities:**
- Execute business rules (validation, calculations)
- Coordinate between multiple repositories
- Handle transactions
- Perform data transformations
- Implement Phase 1 features logic

**Example:**
```java
@Service
public class UserService {
    public UserDTO createUser(UserDTO userDTO) {
        // Hash password
        // Validate email uniqueness
        // Create user entity
        // Save to database
        return userRepository.save(user);
    }
}
```

#### **3. REPOSITORY LAYER** (`repository/`)
**Purpose:** Database CRUD operations

**Responsibilities:**
- Query database using JPA
- Save/Update/Delete entities
- Provide finder methods
- Handle Hibernate operations

**Example:**
```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    List<User> findByFollowedByUser(User user);
}
```

#### **4. DOMAIN LAYER** (`domain/`)
**Purpose:** Entity models & data structure definitions

**Responsibilities:**
- Define JPA entities (User, Post, Comment, Follow, Like)
- Specify database column mappings
- Define relationships between entities
- Extend `AbstractAuditingEntity` for audit tracking (createdDate, lastModifiedDate, createdBy, lastModifiedBy)

**Example:**
```java
@Entity
@Table(name = "user")
public class User extends AbstractAuditingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String username;
    private String email;
    private String passwordHash;
    
    @OneToMany(mappedBy = "follower")
    private Set<Follow> following;
}
```

#### **5. CONFIGURATION LAYER** (`config/`)
**Purpose:** Spring Bean definitions & framework setup

**Key Responsibilities:**
- **`SecurityConfiguration.java`** - Spring Security rules, JWT setup, CORS
- **`DatabaseConfiguration.java`** - JPA, Hibernate, Liquibase config
- **`CacheConfiguration.java`** - Redis/Caffeine caching
- **`WebConfigurer.java`** - Web server settings, CORS
- **`AsyncConfiguration.java`** - Thread pools, async task execution

#### **6. SECURITY LAYER** (`security/`)
**Purpose:** Authentication & Authorization utilities

**Responsibilities:**
- JWT token generation/validation
- Extract user info from token
- Permission checks
- Security utility methods

### **Data Flow Example: Creating a User**

```
1. FRONTEND sends POST /api/users with { email, password, username }
   ↓
2. PRESENTATION (UserResource)
   - Receives request
   - Validates input format
   - Calls userService.createUser()
   ↓
3. SERVICE (UserService)
   - Hash password
   - Check email uniqueness via repository
   - Apply business rules
   - Call userRepository.save()
   ↓
4. REPOSITORY (UserRepository)
   - Execute JPA save()
   - Generate SQL INSERT
   ↓
5. DOMAIN (User entity)
   - Map to database table columns
   - Persist with AbstractAuditingEntity tracking (createdDate, lastModifiedDate, createdBy)
   ↓
6. DATABASE
   - Store user record in "user" table
   ↓
7. RESPONSE flows back up through layers
   - Repository returns saved User entity
   - Service converts to UserDTO
   - Controller returns JSON response
   ↓
8. FRONTEND receives { id, email, username, createdDate, ... }
```

### **Benefits of This Architecture**

✅ **Testability** - Each layer can be tested independently by mocking adjacent layers  
✅ **Maintainability** - Changes isolated to one layer, easier to debug  
✅ **Scalability** - Add caching, async processing, or new features without refactoring  
✅ **Separation of Concerns** - Each layer has one clear responsibility  
✅ **Reusability** - Services can be called by different controllers or scheduled tasks

### **Phase 1 Implementation Plan**

To build MVP, we need to create:

| Layer | Phase 1 Entities |
|-------|------------------|
| **Domain** | User, Post, Comment, Follow, Like |
| **Repository** | UserRepository, PostRepository, CommentRepository, FollowRepository, LikeRepository |
| **Service** | UserService, PostService, CommentService, FollowService |
| **REST** | UserResource, PostResource, CommentResource, FollowResource |
| **Config** | JWT configuration, Security setup, Database migrations |

---

## Running Locally

### Prerequisites
- Java 17+
- Node.js 20+
- MySQL 8.0+
- Gradle wrapper (included)

### Setup
```bash
# 1. Start MySQL (Docker)
docker run --name qualk-mysql -e MYSQL_ALLOW_EMPTY_PASSWORD=yes -p 3306:3306 -d mysql:8.0.35

# 2. Start Spring Boot
./gradlew bootRun

# 3. Access the app
- Backend: http://localhost:8080/
- Swagger UI: http://localhost:8080/swagger-ui.html
- API Docs: http://localhost:8080/v3/api-docs
```
