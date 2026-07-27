# Job Application Tracker

A secure backend application for managing companies, job applications, interviews, authentication, and dashboard statistics.

This project is built using Spring Boot, PostgreSQL, Spring Security, JWT, Flyway, and Maven.

---

## Features

### Authentication and Security

- User registration and login
- BCrypt password hashing
- JWT access token authentication
- Refresh token support
- Logout from current device
- Logout from all devices
- Role-based authorization
- `ROLE_USER`
- `ROLE_ADMIN`
- Custom `401 Unauthorized` response
- Custom `403 Forbidden` response
- Ownership-based data security
- Cross-user access protection
- Invalid JWT handling
- Expired JWT handling

### Company Management

- Create company
- Get all companies
- Get company by ID
- Update company
- Delete company
- Users can only access their own companies

### Job Application Management

- Create job application
- Get all job applications
- Get job application by ID
- Update job application
- Delete job application
- Link job applications with companies
- Prevent duplicate applications for the same company and job title
- Database-level duplicate protection
- Users can only access their own job applications

### Interview Management

- Create interview
- Get all interviews
- Get interview by ID
- Update interview
- Delete interview
- Filter interviews by status
- Sort interviews by scheduled date
- Link interviews with job applications
- Interview rating support
- Interview notes and feedback
- Users can only access their own interviews

### Dashboard

- Current user name
- Total companies
- Total job applications
- Scheduled interview count
- Completed interview count
- Cancelled interview count
- User-specific dashboard statistics

### Validation and Error Handling

- Request validation
- Global exception handling
- Consistent error response structure
- Duplicate record handling
- Not-found exception handling
- Field-level validation messages

### Database

- PostgreSQL database
- Flyway database migrations
- Database schema validation
- Unique constraints
- Foreign key relationships
- Cascade delete for interviews

### Testing

- Authentication service unit tests
- Refresh token service unit tests
- Security integration tests
- Invalid JWT tests
- Expired JWT tests
- Role authorization tests
- Interview service unit tests
- Dashboard service unit tests
- Separate test database configuration

---

## Tech Stack

- Java 25
- Spring Boot 4.1
- Spring Security 7
- Spring Data JPA
- Hibernate
- PostgreSQL
- Flyway
- JSON Web Token
- Maven
- Lombok
- Jakarta Validation
- JUnit
- Mockito
- Swagger
- OpenAPI

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
    │       ├── dashboard
    │       ├── interview
    │       └── security
    │
    └── resources
        └── application-test.properties
```

---

## Prerequisites

Before running the project, install:

- Java 25
- PostgreSQL
- Maven, or use the included Maven Wrapper
- Git
- IntelliJ IDEA or another Java IDE

---

## Database Setup

Create the main PostgreSQL database:

```sql
CREATE DATABASE job_tracker;
```

Create a separate test database:

```sql
CREATE DATABASE job_tracker_test;
```

Database tables are created and updated automatically using Flyway migrations.

---

## Environment Variables

The application uses environment variables for sensitive values.

Required variables:

```text
DB_PASSWORD=your_database_password
JWT_SECRET=your_secure_jwt_secret
```

Optional variable:

```text
DB_USERNAME=postgres
```

The `application.properties` file uses:

```properties
spring.datasource.username=${DB_USERNAME:postgres}
spring.datasource.password=${DB_PASSWORD}

app.jwt.secret=${JWT_SECRET}
```

Never commit real passwords or secrets to GitHub.

---

## Application Configuration

Example `application.properties`:

```properties
spring.application.name=jobtracker

spring.datasource.url=jdbc:postgresql://localhost:5432/job_tracker
spring.datasource.username=${DB_USERNAME:postgres}
spring.datasource.password=${DB_PASSWORD}

spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true

app.jwt.secret=${JWT_SECRET}
app.jwt.expiration=900000

app.refresh-token.expiration=604800000
```

Token expiry configuration:

```text
Access token: 15 minutes
Refresh token: 7 days
```

---

## Running the Application

Using Maven Wrapper on Windows:

```powershell
mvnw.cmd spring-boot:run
```

Using Maven Wrapper on Linux or macOS:

```bash
./mvnw spring-boot:run
```

Using installed Maven:

```bash
mvn spring-boot:run
```

The application runs on:

```text
http://localhost:8080
```

---

## Swagger Documentation

After starting the application, open:

```text
http://localhost:8080/swagger-ui/index.html
```

Use the Swagger **Authorize** button and provide the JWT access token.

Example:

```text
Bearer your_access_token
```

---

## Main API Endpoints

### Authentication

```text
POST /api/auth/register
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

### Interviews

```text
POST   /api/interviews
GET    /api/interviews
GET    /api/interviews/{id}
PUT    /api/interviews/{id}
DELETE /api/interviews/{id}
GET    /api/interviews/status/{status}
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

### 1. Register User

```http
POST /api/auth/register
```

Example request:

```json
{
  "name": "Kapil",
  "email": "kapil@example.com",
  "password": "password123"
}
```

### 2. Login

```http
POST /api/auth/login
```

Example request:

```json
{
  "email": "kapil@example.com",
  "password": "password123"
}
```

The login response contains an access token and refresh token.

### 3. Create Company

```http
POST /api/companies
```

Example request:

```json
{
  "name": "Google",
  "website": "https://www.google.com",
  "location": "Bengaluru, India",
  "notes": "Product-based company"
}
```

### 4. Create Job Application

```http
POST /api/applications
```

Example request:

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

### 5. Create Interview

```http
POST /api/interviews
```

Example request:

```json
{
  "jobApplicationId": 1,
  "type": "TECHNICAL",
  "status": "SCHEDULED",
  "scheduledAt": "2026-08-05T11:00:00",
  "meetingLink": "https://meet.google.com/example",
  "notes": "Prepare Java, Spring Boot and PostgreSQL",
  "feedback": null,
  "rating": null
}
```

### 6. View Dashboard

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

A user cannot access another user's resources.

Example:

```text
User 1 creates interview ID 5
User 2 requests GET /api/interviews/5
Response: 404 Not Found
```

The same ownership protection applies to:

- Read
- Update
- Delete

Returning `404 Not Found` prevents exposing whether another user's record exists.

---

## Duplicate Job Application Protection

The application prevents the same user from creating duplicate job applications for the same:

```text
company + job title
```

The comparison is case-insensitive.

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

Protection exists at both:

- Service level
- Database level

---

## Error Response Format

Errors use a consistent JSON structure:

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

```text
200 OK
201 Created
204 No Content
400 Bad Request
401 Unauthorized
403 Forbidden
404 Not Found
409 Conflict
```

---

## Running Tests

Run all tests using installed Maven:

```bash
mvn test
```

Using Maven Wrapper on Windows:

```powershell
mvnw.cmd test
```

Using Maven Wrapper on Linux or macOS:

```bash
./mvnw test
```

Tests use the test profile and a separate PostgreSQL test database.

---

## Security Notes

- Passwords are hashed using BCrypt.
- JWT secrets are loaded from environment variables.
- Database passwords are not stored in source code.
- Access tokens are short-lived.
- Refresh tokens can be revoked.
- Logout invalidates refresh tokens.
- Role-based access is enforced.
- Users can only access their own data.
- Cross-user resource access returns `404 Not Found`.

---

## Future Improvements

Possible future additions:

- Pagination
- Search and advanced filters
- Upcoming interviews endpoint
- Email interview reminders
- Password reset
- Email verification
- CORS configuration
- Docker support
- CI/CD pipeline
- Deployment configuration
- Frontend application
- Audit logging
- Soft delete
- Application status analytics
- Interview calendar integration

---

## Author

**Kapil**

GitHub:

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

This project is created for learning, portfolio, and interview preparation purposes.