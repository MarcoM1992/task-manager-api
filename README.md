# Task Manager API

A Spring Boot RESTful API for managing users, roles, and tasks. The API supports JWT authentication, role-based access control, and task assignment to users.

---

## **Features**

- User management
  - Register, update, delete users
  - Role assignment and management (ADMIN, USER)
- Task management
  - Create, update, delete tasks
  - Assign tasks to users
- JWT-based authentication and authorization
- Secure endpoints with role-based access
- User account validation via email token
- Date and timezone-aware task handling
- Swagger documentation for API endpoints

---

## **Technologies Used**

- Java 17
- Spring Boot 3.x
- Spring Security
- JWT (JSON Web Tokens)
- JPA / Hibernate
- Maven
- PostgreSQL / MySQL (or any relational DB)
- Swagger OpenAPI 3
- SLF4J / Logback for logging

---

## **Project Structure**
src/
├─ main/
│  ├─ java/
│  │  └─ it/
│  │     └─ marmas/
│  │        └─ task/
│  │           └─ manager/
│  │              └─ api/
│  │                 ├─ auth/         # JWT utilities
│  │                 ├─ security/     # Spring Security config & filters
│  │                 ├─ service/      # Business logic
│  │                 ├─ repo/         # Repository layer (EntityManager based)
│  │                 ├─ model/        # JPA Entities
│  │                 ├─ dto/          # Data Transfer Objects
│  │                 ├─ mapper/       # Entity <-> DTO mappers
│  │                 └─ util/         # Utility classes
│  └─ resources/
│     ├─ application.properties
│     ├─ static/
│     └─ templates/
└─ test/


---

## **Getting Started**

### **Prerequisites**

- Java 17+
- Maven 3.8+
- PostgreSQL / MySQL / any supported RDBMS
- Git

---

### **Setup**

1. Clone the repository:

```bash
git clone https://github.com/MarcoM1992/task-manager-api.git

cd task-manager-api
Configure your database connection in application.properties:

properties
Copy code
spring.datasource.url=jdbc:postgresql://localhost:3306/taskmanager
spring.datasource.username=YOUR_DB_USERNAME
spring.datasource.password=YOUR_DB_PASSWORD
spring.jpa.hibernate.ddl-auto=update
jwt.secret=YOUR_JWT_SECRET_KEY
Build and run:

bash
Copy code
mvn clean install
mvn spring-boot:run

API Documentation

Swagger UI is available at:

http://localhost:8080/swagger-ui/index.html


This provides interactive documentation for all API endpoints, including request/response models.

Authentication
## **Endpoints Overview**

- **Auth endpoints** (`/auth/**`)
  - Register new users (`/auth/register`)
  - Login and obtain JWT token (`/auth/login`)
  - Token validation
  - Implementato in: `it.marmas.task.manager.api.auth`

- **User endpoints** (`/user/**`)
  - Create, update, delete, get users
  - Assign/remove tasks to users
  - Role assignment and management
  - Implementato in: `it.marmas.task.manager.api.service`, `it.marmas.task.manager.api.model`, `it.marmas.task.manager.api.dto`, `it.marmas.task.manager.api.mapper`

- **Task endpoints** (`/task/**`)
  - Create, update, delete tasks
  - Query tasks by username or date range
  - Assign tasks to users
  - Implementato in: `it.marmas.task.manager.api.service`, `it.marmas.task.manager.api.model`, `it.marmas.task.manager.api.dto`, `it.marmas.task.manager.api.mapper`

- **Admin endpoints** (`/ADMIN/**`)
  - Manage roles and users
  - Full access for ADMIN role
  - Implementato in: `it.marmas.task.manager.api.security`

- **Security & JWT**
  - JWT-based authentication and authorization
  - Role-based access control
  - Implementato in: `it.marmas.task.manager.api.security`, `it.marmas.task.manager.api.auth`

---

**Note:**  
- JWT Token: Include `Authorization: Bearer <token>` in request headers for protected endpoints.
- Role-based access:  
  - `ADMIN` → Full access  
  - `USER` → Limited access to own tasks
---

**API Documentation**

Swagger UI is available at:  
`http://localhost:8080/swagger-ui/index.html`

This provides interactive documentation for all API endpoints, including request/response models.
---

## Curl Examples:
# Register a new user
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"newuser","password":"password","email":"user@email.com"}'

# Login (get JWT token)
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"newuser","password":"password"}'

# Get user info (protected endpoint, replace <token> with your JWT)
curl -X GET http://localhost:8080/user/me \
  -H "Authorization: Bearer <token>"

# Create a new task (protected endpoint)
curl -X POST http://localhost:8080/task \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"title":"My Task","description":"Details","dueDate":"2025-09-30"}'

# Assign a task to a user (protected endpoint)
curl -X POST http://localhost:8080/user/{userId}/tasks/{taskId} \
  -H "Authorization: Bearer <token>"

# Get all tasks for current user (protected endpoint)
curl -X GET http://localhost:8080/task/my \
  -H "Authorization: Bearer <token>"

# Admin: get all users (ADMIN role required)
curl -X GET http://localhost:8080/ADMIN/users \
  -H "Authorization: Bearer <token>"

License

This project is licensed under the MIT License. See LICENSE file for details.

Contributing

Fork the repository

Create a feature branch (git checkout -b feature/my-feature)

Commit changes (git commit -am 'Add new feature')

Push to branch (git push origin feature/my-feature)

Open a pull request


---
 