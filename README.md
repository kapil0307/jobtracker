# Job Application Tracker

# Job Application Tracker

[![CI](https://github.com/kapil0307/jobtracker/actions/workflows/ci.yml/badge.svg)](https://github.com/kapil0307/jobtracker/actions/workflows/ci.yml)

A secure REST API for tracking companies, job applications, interviews, authentication, and personalized dashboard statistics.

The application is built with Spring Boot, PostgreSQL, Spring Security, JWT authentication, Flyway migrations, Maven, and Docker.

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
- Dashboard service unit tests
- Refresh-token service unit tests
- Security integration tests
- Role authorization tests
- Invalid JWT tests
- Expired JWT tests
- Separate test-profile configuration

Current test suite:

```text
52 tests
0 failures
0 errors
```

---

## Tech Stack

- Java 17
- Spring Boot 4.1
- Spring Security
- Spring Data JPA
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
│   │       ├── security
│   │       └── user
│   │
│   └── resources
│       ├── db
│       │   └── migration
│       └── application.properties
│
└── test
    ├── java
    │   └── com.kapil.jobtracker
    │       ├── auth
    │       ├── company
    │       ├── dashboard
    │       ├── interview
    │       ├── jobapplication
    │       └── security
    │
    └── resources
        └── application-test.properties
```

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

> **Warning:** This command deletes the PostgreSQL Docker volume. All users, companies, applications, interviews, tokens, and other saved data will be permanently removed.

---

## Docker Ports

| Service | Host Port | Container Port |
|---|---:|---:|
| Spring Boot | 8080 | 8080 |
| PostgreSQL | 5433 | 5432 |

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

.\mvnw.cmd spring-boot:run
```

### Linux or macOS

```bash
export DB_USERNAME=postgres
export DB_PASSWORD=your_database_password
export JWT_SECRET=your_base64_jwt_secret

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

| Token | Expiration |
|---|---:|
| Access token | 15 minutes |
| Refresh token | 7 days |

---

## Swagger Documentation

After starting the application, open:

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

### Dashboard

```text
GET /api/dashboard
```

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

### 6. View the Dashboard

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

Every company, job application, interview, and dashboard record belongs to the currently authenticated user.

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

| Status | Meaning |
|---:|---|
| 200 | OK |
| 201 | Created |
| 204 | No Content |
| 400 | Bad Request |
| 401 | Unauthorized |
| 403 | Forbidden |
| 404 | Not Found |
| 409 | Conflict |

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
- Email interview reminders
- Password-reset workflow
- Email verification
- Production CORS configuration
- CI/CD pipeline
- Cloud deployment
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