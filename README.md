# Job Application Tracker

[![CI](https://github.com/kapil0307/jobtracker/actions/workflows/ci.yml/badge.svg)](https://github.com/kapil0307/jobtracker/actions/workflows/ci.yml)

**Live Application:** [https://jobtracker-shr7.onrender.com](https://jobtracker-shr7.onrender.com)  
**Live Swagger UI:** [https://jobtracker-shr7.onrender.com/swagger-ui/index.html](https://jobtracker-shr7.onrender.com/swagger-ui/index.html)

A production-ready REST API for tracking job applications, companies, interviews, and automated reminder notifications.

Built with **Spring Boot**, **PostgreSQL**, **JWT Authentication**, **Flyway**, **Docker**, and **GitHub Actions**.

---

## Overview

Job Application Tracker helps users manage their complete job search process in one place:

- Track companies and job applications
- Schedule and manage interviews
- Receive automated interview reminders
- View personalized dashboard statistics
- Secure multi-user system with strict ownership isolation

The project focuses on clean architecture, security, data integrity, testing, and real production deployment.

---

## Key Features

### Authentication & Security
- User registration and login
- BCrypt password hashing
- JWT access token + refresh token authentication
- Logout from current device / all devices
- Role-based authorization (`ROLE_USER`, `ROLE_ADMIN`)
- Ownership-based access control (users can only access their own data)
- Custom `401 Unauthorized` and `403 Forbidden` handling
- Invalid and expired JWT handling

### Core Modules
- **Companies** – Full CRUD with ownership protection
- **Job Applications** – Paginated, sortable, with duplicate prevention (same company + job title)
- **Interviews** – Scheduling, status tracking, ratings, notes & feedback
- **Notifications** – Automated interview reminders (24 hours before)
- **Dashboard** – Personalized statistics (total applications, interviews, etc.)

### Technical Highlights
- Global exception handling with consistent error responses
- Request validation with field-level messages
- Flyway database migrations
- Scheduler for processing due reminders every minute
- Comprehensive test suite (**63 tests**, 0 failures)
- Docker + Docker Compose support
- CI pipeline with GitHub Actions
- Production deployment on **Render** + **Neon PostgreSQL**

---

## Tech Stack

| Category            | Technology                            |
|---------------------|---------------------------------------|
| Language            | Java 17                               |
| Framework           | Spring Boot 4.1                       |
| Security            | Spring Security + JWT                 |
| Database            | PostgreSQL 17 + Flyway                |
| ORM                 | Spring Data JPA + Hibernate           |
| Build Tool          | Maven                                 |
| API Documentation   | Swagger / OpenAPI                     |
| Containerization    | Docker + Docker Compose               |
| CI/CD               | GitHub Actions + Render Auto-Deploy   |
| Testing             | JUnit + Mockito                       |

---

## Live Deployment

| Item                | Details                                              |
|---------------------|------------------------------------------------------|
| **Live URL**        | [https://jobtracker-shr7.onrender.com](https://jobtracker-shr7.onrender.com) |
| **Swagger UI**      | [https://jobtracker-shr7.onrender.com/swagger-ui/index.html](https://jobtracker-shr7.onrender.com/swagger-ui/index.html) |
| Platform            | Render (Web Service)                                 |
| Database            | Neon PostgreSQL                                      |
| Profile             | `prod`                                               |
| Auto Deploy         | Triggered on every push to `main`                    |

---

## Getting Started

### Recommended: Run with Docker

```bash
git clone https://github.com/kapil0307/jobtracker.git
cd jobtracker
cp .env.example .env
# Update DB_PASSWORD and JWT_SECRET in .env
docker compose up -d --build
```

Application will be available at: `http://localhost:8080`  
Health check: `http://localhost:8080/actuator/health`

### Run without Docker

1. Create databases: `job_tracker` and `job_tracker_test`
2. Set environment variables (`DB_PASSWORD`, `JWT_SECRET`)
3. Run:

```bash
./mvnw spring-boot:run
```

---

## API Documentation (Swagger)

**Live Swagger:**  
[https://jobtracker-shr7.onrender.com/swagger-ui/index.html](https://jobtracker-shr7.onrender.com/swagger-ui/index.html)

Local (with `dev` profile):  
`http://localhost:8080/swagger-ui/index.html`

**How to use protected endpoints:**

1. Register a user
2. Login and copy the access token
3. Click **Authorize** in Swagger
4. Enter: `Bearer <your_access_token>`

---

## Main API Endpoints

| Module               | Endpoints |
|----------------------|-----------|
| **Auth**             | `POST /api/users/register`<br>`POST /api/auth/login`<br>`POST /api/auth/refresh`<br>`POST /api/auth/logout`<br>`POST /api/auth/logout-all` |
| **Companies**        | `POST /api/companies`<br>`GET /api/companies`<br>`GET /api/companies/{id}`<br>`PUT /api/companies/{id}`<br>`DELETE /api/companies/{id}` |
| **Job Applications** | `POST /api/applications`<br>`GET /api/applications`<br>`GET /api/applications/{id}`<br>`PUT /api/applications/{id}`<br>`DELETE /api/applications/{id}` |
| **Interviews**       | `POST /api/interviews`<br>`GET /api/interviews`<br>`GET /api/interviews/{id}`<br>`PUT /api/interviews/{id}`<br>`DELETE /api/interviews/{id}`<br>`GET /api/interviews/status/{status}`<br>`GET /api/interviews/upcoming` |
| **Notifications**    | `GET /api/notifications`<br>`PATCH /api/notifications/{id}/read` |
| **Dashboard**        | `GET /api/dashboard` |

---

## Security Highlights

- Every resource is strictly owned by the authenticated user
- Cross-user access returns `404 Not Found` (prevents information leakage)
- Duplicate job applications (same company + job title) are blocked at both service and database level
- Passwords are hashed with BCrypt
- Secrets are loaded from environment variables (never committed)
- Access tokens are short-lived (15 minutes)
- Refresh tokens can be revoked

---

## Testing

```bash
./mvnw test
```

**Current Status:**

```text
Tests run: 63
Failures: 0
Errors: 0
Skipped: 0
BUILD SUCCESS
```

Includes unit tests and security integration tests.

---

## Project Structure

```text
src/main/java/com.kapil.jobtracker
├── auth
├── company
├── dashboard
├── exception
├── interview
├── jobapplication
├── notification
├── security
└── user
```

---

## Future Improvements

- Real email delivery for interview reminders
- Advanced search and filters
- Password reset + email verification
- Frontend application
- Soft deletes + audit logging
- Application status analytics
- Time-zone aware reminder scheduling

---

## Author

**Kapil**  
GitHub: [https://github.com/kapil0307](https://github.com/kapil0307)

**Repository:** [https://github.com/kapil0307/jobtracker](https://github.com/kapil0307/jobtracker)

---

This project was built for learning, portfolio development, and interview preparation.
```
