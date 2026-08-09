# Job Application Tracker

[![CI](https://github.com/kapil0307/jobtracker/actions/workflows/ci.yml/badge.svg)](https://github.com/kapil0307/jobtracker/actions/workflows/ci.yml)

A secure REST API for tracking companies, job applications, interviews, reminder notifications, authentication, and personalized dashboard statistics.

The application is built with Spring Boot, PostgreSQL, Spring Security, JWT authentication, Flyway migrations, Maven, Docker, and GitHub Actions.

---

## Features

### Authentication and Security

- User registration and login
- BCrypt password hashing
- JWT access-token authentication
- Refresh-token support
- Logout from the current device
- Logout from all devices
- Role-based authorization
- `ROLE_USER`
- `ROLE_ADMIN`
- Custom `401 Unauthorized` responses
- Custom `403 Forbidden` responses
- Invalid and expired JWT handling
- Ownership-based data access
- Cross-user resource protection

### Company Management

- Create a company
- Get all companies
- Get a company by ID
- Update a company
- Delete a company
- Users can only access their own companies

### Job Application Management

- Create a job application
- Get paginated job applications
- Sort job applications
- Get a job application by ID
- Update a job application
- Delete a job application
- Link job applications with companies
- Prevent duplicate applications for the same company and job title
- Service-level and database-level duplicate protection
- Users can only access their own job applications

### Interview Management

- Create an interview
- Get paginated interviews
- Sort interviews by scheduled date
- Get an interview by ID
- Update an interview
- Delete an interview
- Filter interviews by status
- Get upcoming scheduled interviews
- Link interviews with job applications
- Interview ratings
- Interview notes and feedback
- Users can only access their own interviews

### Reminder Notifications

- Automatically create an interview reminder for eligible interviews
- Automatically update the existing reminder when an interview is rescheduled
- Schedule reminders 24 hours before the interview
- Process due reminders with a scheduler that runs every minute
- Move due reminders from `PENDING` to `SENT` and record `sentAt`
- Let users retrieve only their own notifications
- Let users mark their own notifications as read and record `readAt`
- Cancel reminders when interviews become `COMPLETED` or `CANCELLED`
- Do not create reminders for interviews scheduled in the past
- Prevent duplicate reminders with one notification per interview

> `SENT` currently means the in-app reminder has been processed by the scheduler. Actual email delivery is planned as a future improvement.

### Dashboard

- Current user name
- Total companies
- Total job applications
- Scheduled interview count
- Completed interview count
- Cancelled interview count
- User-specific statistics

### Validation and Error Handling

- Request validation
- Field-level validation messages
- Global exception handling
- Consistent error-response structure
- Duplicate-record handling
- Resource-not-found handling

### Database

- PostgreSQL
- Flyway migrations
- Hibernate schema validation
- Unique constraints
- Foreign-key relationships
- Cascade deletion for interviews
- Persistent Docker volume support

### Testing

- Authentication service unit tests
- Company service unit tests
- Job application service unit tests
- Interview service unit tests
- Notification service unit tests
- Dashboard service unit tests
- Refresh-token service unit tests
- Security integration tests
- Role authorization tests
- Invalid JWT tests
- Expired JWT tests
- Separate test-profile configuration

Current test suite:

```text
63 tests
0 failures
0 errors
```

---

## Tech Stack

- Java 17
- Spring Boot 4.1
- Spring Security
- Spring Data JPA
- Spring Scheduling
- Hibernate
- PostgreSQL 17
- Flyway
- JSON Web Token
- Maven
- Lombok
- Jakarta Validation
- JUnit
- Mockito
- Swagger / OpenAPI
- Docker
- Docker Compose
- GitHub Actions

---

## Project Structure

```text
src
├── main
│   ├── java
│   │   └── com.kapil.jobtracker
│   │       ├── auth
│   │       ├── company
│   │       ├── dashboard
│   │       ├── exception
│   │       ├── interview
│   │       ├── jobapplication
│   │       ├── notification
│   │       ├── security
│   │       └── user
│   │
│   └── resources
│       ├── db
│       │   └── migration
│       ├── application.properties
│       ├── application-dev.properties
│       └── application-prod.properties
│
└── test
    ├── java
    │   └── com.kapil.jobtracker
    │       ├── auth
    │       ├── company
    │       ├── dashboard
    │       ├── interview
    │       ├── jobapplication
    │       ├── notification
    │       └── security
    │
    └── resources
        └── application-test.properties
```

---

## Spring Profiles

The project uses separate Spring profiles for local development, testing, and production.

### Development (`dev`)

The development profile enables verbose Hibernate SQL logging and formatted SQL output for local debugging.

```powershell
$env:SPRING_PROFILES_ACTIVE="dev"
.\mvnw.cmd spring-boot:run
```

### Test (`test`)

The test profile uses the separate PostgreSQL database `job_tracker_test`.

### Production (`prod`)

The production profile is designed for Docker/cloud deployment and uses environment variables such as `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET`, and `PORT`. Production also disables SQL logging and Swagger/OpenAPI, hides detailed health information, enables forwarded-header handling and graceful shutdown, and suppresses detailed exception output.

Docker Compose runs the application with the `prod` profile.

---

## Getting Started

The recommended way to run the complete application is with Docker Compose.

Docker Compose starts:

- The Spring Boot application
- A PostgreSQL database
- A private Docker network
- A persistent PostgreSQL volume

---

## Prerequisites

### Running with Docker

Install:

- Git
- Docker Desktop

### Running without Docker

Install:

- Java 17
- PostgreSQL
- Git
- Maven, or use the included Maven Wrapper

---

## Clone the Repository

```bash
git clone https://github.com/kapil0307/jobtracker.git
cd jobtracker
```

---

## Environment Variables

Create a `.env` file in the project root.

You can copy the provided example file.

### Windows PowerShell

```powershell
Copy-Item .env.example .env
```

### Linux or macOS

```bash
cp .env.example .env
```

Example `.env` values:

```env
DB_USERNAME=postgres
DB_PASSWORD=your_database_password
JWT_SECRET=your_base64_jwt_secret
```

The `.env` file contains sensitive values and must never be committed to Git.

The `.env.example` file contains only placeholder values and can safely be committed.

---

## Run with Docker

### Build and Start the Application

```bash
docker compose up -d --build
```

This command:

- Builds the Spring Boot Docker image
- Downloads the PostgreSQL image
- Creates a Docker network
- Creates a persistent PostgreSQL volume
- Starts PostgreSQL
- Waits until PostgreSQL is healthy
- Starts the Spring Boot application

### Check Container Status

```bash
docker compose ps
```

Expected services:

```text
jobtracker-app        Up
jobtracker-postgres   Up (healthy)
```

### Check Application Health

Open:

```text
http://localhost:8080/actuator/health
```

Expected status:

```json
{
  "status": "UP"
}
```

### View Application Logs

```bash
docker compose logs -f app
```

Press `Ctrl + C` to stop viewing logs.

The containers will continue running in the background.

### View PostgreSQL Logs

```bash
docker compose logs -f postgres
```

### Stop the Application

```bash
docker compose down
```

This stops and removes the containers and Docker network.

The PostgreSQL data remains stored in the Docker volume.

### Restart the Application

```bash
docker compose up -d
```

### Rebuild After Code Changes

```bash
docker compose up -d --build
```

Run this command after changing:

- Java source code
- Maven dependencies
- The Dockerfile
- Application configuration included in the image

### Delete Containers and Database Data

```bash
docker compose down -v
```

> **Warning:** This command deletes the PostgreSQL Docker volume. All users, companies, applications, interviews, notifications, tokens, and other saved data will be permanently removed.

---

## Docker Ports

| Service     | Host Port | Container Port |
| ----------- | --------: | -------------: |
| Spring Boot |      8080 |           8080 |
| PostgreSQL  |      5433 |           5432 |

The Spring Boot container connects to PostgreSQL through the Docker network using:

```text
jdbc:postgresql://postgres:5432/job_tracker
```

The service name `postgres` acts as the database hostname inside the Docker network.

---

## Run without Docker

### Create the Databases

Create the main database:

```sql
CREATE DATABASE job_tracker;
```

Create the test database:

```sql
CREATE DATABASE job_tracker_test;
```

Flyway automatically creates and updates the required tables.

### Configure Environment Variables

Required:

```text
DB_PASSWORD=your_database_password
JWT_SECRET=your_base64_jwt_secret
```

Optional:

```text
DB_USERNAME=postgres
```

### Windows PowerShell

```powershell
$env:DB_USERNAME="postgres"
$env:DB_PASSWORD="your_database_password"
$env:JWT_SECRET="your_base64_jwt_secret"
$env:SPRING_PROFILES_ACTIVE="dev"

.\mvnw.cmd spring-boot:run
```

### Linux or macOS

```bash
export DB_USERNAME=postgres
export DB_PASSWORD=your_database_password
export JWT_SECRET=your_base64_jwt_secret
export SPRING_PROFILES_ACTIVE=dev

./mvnw spring-boot:run
```

The application runs at:

```text
http://localhost:8080
```

---

## Application Configuration

The application reads sensitive configuration from environment variables.

Example:

```properties
spring.application.name=jobtracker

spring.datasource.url=${DB_URL:jdbc:postgresql://localhost:5432/job_tracker}
spring.datasource.username=${DB_USERNAME:postgres}
spring.datasource.password=${DB_PASSWORD}

spring.jpa.hibernate.ddl-auto=validate

app.jwt.secret=${JWT_SECRET}
app.jwt.expiration=900000

app.refresh-token.expiration=604800000
```

Token expiration:

| Token         | Expiration |
| ------------- | ---------: |
| Access token  | 15 minutes |
| Refresh token |     7 days |

---

## Swagger Documentation

Start the application with the `dev` profile and open:

```text
http://localhost:8080/swagger-ui/index.html
```

For protected endpoints:

1. Register a user.
2. Log in.
3. Copy the access token.
4. Click the Swagger **Authorize** button.
5. Enter:

```text
Bearer your_access_token
```

Swagger/OpenAPI is disabled in the `prod` profile.

---

## Deployment

The application is deployed as a production web service using Render and uses Neon PostgreSQL as the production database.

### Production Application

The live production application is available at:

```text
https://jobtracker-shr7.onrender.com
```

The application runs using the Spring Boot `prod` profile.

Production deployment uses:

- Render Web Service
- Neon PostgreSQL
- Docker
- GitHub repository
- Render Auto-Deploy
- Java 17
- Spring Boot
- Flyway database migrations

### Deployment Flow

```text
GitHub Repository
       ↓
Push to main
       ↓
Render Auto-Deploy
       ↓
Docker Build
       ↓
Spring Boot Application
       ↓
Neon PostgreSQL
       ↓
Production Service
```

Whenever a new commit is pushed to the `main` branch, Render automatically starts a new deployment.

### Production Environment Variables

Production secrets and configuration are stored in Render environment variables.

```text
SPRING_PROFILES_ACTIVE=prod

DB_URL=<Neon PostgreSQL JDBC URL>
DB_USERNAME=<database username>
DB_PASSWORD=<database password>

JWT_SECRET=<JWT secret>

PORT=10000

APP_MAIL_FROM=<configured sender email>
MAIL_USERNAME=<SMTP username>
MAIL_PASSWORD=<SMTP password>
```

> Sensitive production values such as database passwords, JWT secrets, and SMTP credentials are stored as Render environment variables and are not committed to the repository.

### Production Database

The production application uses Neon PostgreSQL.

Flyway automatically validates and applies database migrations during application startup.

The production database currently uses schema migration version:

```text
12
```

A successful deployment contains logs similar to:

```text
Successfully validated 12 migrations
Schema "public" is up to date. No migration necessary.
```

### Production Port

Render provides the `PORT` environment variable to the application.

The Spring Boot application binds to:

```text
PORT=10000
```

The production server uses embedded Tomcat.

Example startup message:

```text
Tomcat started on port 10000 (http) with context path '/'
```

### Automatic Deployment

Render is connected to the GitHub repository:

```text
https://github.com/kapil0307/jobtracker
```

Deployments are automatically triggered when changes are pushed to `main`.

Example:

```bash
git add .
git commit -m "Update application"
git push origin main
```

After the push:

```text
GitHub
  ↓
Render detects new commit
  ↓
Docker build
  ↓
Application deployment
  ↓
Production service starts
  ↓
Service becomes live
```

### Production Startup Verification

A successful deployment is confirmed when the Render logs contain:

```text
Tomcat started on port 10000 (http) with context path '/'
```

followed by:

```text
Started JobtrackerApplication
```

and Render reports:

```text
Your service is live 🎉
```

The production deployment verifies that:

- Spring Boot starts successfully
- The `prod` profile is active
- PostgreSQL connection succeeds
- Flyway migrations are validated
- Hibernate initializes successfully
- Spring Security starts successfully
- Tomcat binds to the Render-provided port
- The application becomes publicly accessible

### Production Authentication

Protected endpoints require JWT authentication.

For example, accessing a protected endpoint without authentication returns:

```json
{
  "timestamp": "2026-08-09T15:07:16.436526982",
  "status": 401,
  "error": "Unauthorized",
  "message": "Authentication is required",
  "path": "/"
}
```

This confirms that Spring Security is active in the production environment.

---

## Main API Endpoints

### User Registration

```text
POST /api/users/register
```

### Authentication

```text
POST /api/auth/login
POST /api/auth/refresh
POST /api/auth/logout
POST /api/auth/logout-all
```

### Companies

```text
POST   /api/companies
GET    /api/companies
GET    /api/companies/{id}
PUT    /api/companies/{id}
DELETE /api/companies/{id}
```

### Job Applications

```text
POST   /api/applications
GET    /api/applications
GET    /api/applications/{id}
PUT    /api/applications/{id}
DELETE /api/applications/{id}
```

Pagination and sorting example:

```text
GET /api/applications?page=0&size=10&sort=appliedDate,desc
```

### Interviews

```text
POST   /api/interviews
GET    /api/interviews
GET    /api/interviews/{id}
PUT    /api/interviews/{id}
DELETE /api/interviews/{id}
GET    /api/interviews/status/{status}
GET    /api/interviews/upcoming
```

Pagination and sorting example:

```text
GET /api/interviews?page=0&size=10&sort=scheduledAt,asc
```

Supported interview statuses:

```text
SCHEDULED
COMPLETED
CANCELLED
RESCHEDULED
```

### Notifications

```text
GET   /api/notifications
PATCH /api/notifications/{notificationId}/read
```

`GET /api/notifications` returns only the currently authenticated user's notifications, ordered from newest to oldest.

`PATCH /api/notifications/{notificationId}/read` marks only the authenticated user's notification as read.

Notification statuses include:

```text
PENDING
SENT
FAILED
CANCELLED
```

### Dashboard

```text
GET /api/dashboard
```

---

## Interview Reminder Flow

When an eligible interview is created or updated:

```text
Interview create/update
        ↓
Notification created or updated
        ↓
scheduledFor = interview time - 24 hours
        ↓
Scheduler checks every minute
        ↓
Due PENDING notification
        ↓
Status becomes SENT
        ↓
sentAt is recorded
```

If the interview becomes completed or cancelled, the existing reminder becomes `CANCELLED` and the scheduler ignores it. If the interview date is already in the past, a new reminder is not created. A unique database constraint prevents multiple reminder records for the same interview.

---

## Example API Flow

### 1. Register a User

```http
POST /api/users/register
```

```json
{
  "name": "Kapil",
  "email": "kapil@example.com",
  "password": "password123"
}
```

### 2. Log In

```http
POST /api/auth/login
```

```json
{
  "email": "kapil@example.com",
  "password": "password123"
}
```

The response contains an access token and a refresh token.

### 3. Create a Company

```http
POST /api/companies
```

```json
{
  "name": "Google",
  "website": "https://www.google.com",
  "location": "Bengaluru, India",
  "notes": "Product-based company"
}
```

### 4. Create a Job Application

```http
POST /api/applications
```

```json
{
  "jobTitle": "Java Backend Developer",
  "status": "APPLIED",
  "appliedDate": "2026-07-25",
  "jobUrl": "https://careers.google.com/jobs/java-backend-developer",
  "jobLocation": "Bengaluru, India",
  "salaryRange": "12-18 LPA",
  "notes": "Applied through company careers page",
  "companyId": 1,
  "source": "COMPANY_WEBSITE"
}
```

### 5. Create an Interview

```http
POST /api/interviews
```

```json
{
  "jobApplicationId": 1,
  "type": "TECHNICAL",
  "status": "SCHEDULED",
  "scheduledAt": "2026-08-05T11:00:00",
  "meetingLink": "https://meet.google.com/example",
  "notes": "Prepare Java, Spring Boot, and PostgreSQL",
  "feedback": null,
  "rating": null
}
```

### 6. Get Notifications

```http
GET /api/notifications
```

### 7. Mark a Notification as Read

```http
PATCH /api/notifications/1/read
```

### 8. View the Dashboard

```http
GET /api/dashboard
```

Example response:

```json
{
  "userName": "Kapil",
  "totalCompanies": 3,
  "totalApplications": 8,
  "scheduledInterviews": 2,
  "completedInterviews": 1,
  "cancelledInterviews": 0
}
```

---

## Ownership Security

Every company, job application, interview, notification, and dashboard record belongs to the currently authenticated user.

A user cannot read, update, or delete another user's resources.

Example:

```text
User 1 creates interview ID 5
User 2 requests GET /api/interviews/5
Response: 404 Not Found
```

Returning `404 Not Found` prevents the API from revealing whether another user's resource exists.

Ownership protection applies to:

- Read operations
- Update operations
- Delete operations

---

## Duplicate Job Application Protection

The application prevents a user from creating duplicate job applications with the same:

```text
company + job title
```

The job-title comparison is case-insensitive.

These titles are treated as duplicates:

```text
Java Developer
java developer
JAVA DEVELOPER
```

Duplicate requests return:

```text
409 Conflict
```

Protection is enforced at both:

- Service level
- Database level

---

## Error Response Format

Errors use a consistent JSON response structure:

```json
{
  "timestamp": "2026-07-25T02:43:37",
  "status": 404,
  "error": "Not Found",
  "message": "Interview not found",
  "path": "/api/interviews/5"
}
```

Common HTTP status codes:

| Status | Meaning      |
| -----: | ------------ |
|    200 | OK           |
|    201 | Created      |
|    204 | No Content   |
|    400 | Bad Request  |
|    401 | Unauthorized |
|    403 | Forbidden    |
|    404 | Not Found    |
|    409 | Conflict     |

---

## Running Tests

### Windows

```powershell
$env:TEST_DB_PASSWORD="your_test_database_password"
.\mvnw.cmd test
```

### Linux or macOS

```bash
export TEST_DB_PASSWORD=your_test_database_password
./mvnw test
```

The tests use the `test` Spring profile and a separate PostgreSQL test database.

Current local result:

```text
Tests run: 63
Failures: 0
Errors: 0
Skipped: 0
BUILD SUCCESS
```

---

## Continuous Integration

GitHub Actions configuration is stored in `.github/workflows/ci.yml`. The workflow is configured for pushes to `main` and pull requests targeting `main`.

The CI job:

1. Checks out the repository.
2. Starts a PostgreSQL 17 service container.
3. Configures Java 17.
4. Uses Maven dependency caching.
5. Makes the Maven Wrapper executable.
6. Runs the complete Maven test suite.

The CI status badge appears at the top of this README.

---

## Security Notes

- Passwords are hashed with BCrypt.
- JWT secrets are loaded from environment variables.
- Database passwords are not stored in source code.
- `.env` is excluded from Git.
- Access tokens are short-lived.
- Refresh tokens can be revoked.
- Logout invalidates refresh tokens.
- Role-based authorization is enforced.
- Users can only access their own data.
- Cross-user access returns `404 Not Found`.

---

## Future Improvements

- Search and advanced filters
- Real email interview reminders
- Amazon SES integration
- Unread-notification count
- Mark all notifications as read
- Configurable reminder time
- Retry handling for failed notification delivery
- Time-zone-aware reminder scheduling
- Password-reset workflow
- Email verification
- Production CORS configuration
- AWS cloud deployment
- Automated CD pipeline
- Frontend application
- Audit logging
- Soft deletion
- Application-status analytics
- Interview calendar integration
- Testcontainers integration

---

## Author

**Kapil**

GitHub profile:

```text
https://github.com/kapil0307
```

---

## Repository

```text
https://github.com/kapil0307/jobtracker
```

---

## License

This project was created for learning, portfolio development, and interview preparation.