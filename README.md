# Store Management Tool

A lightweight Spring Boot application for managing store inventory and operations.
The application provides product management, user authentication, role-based authorization, and REST API endpoints for managing store data.

## Features

- Product inventory management

    - Create products
    - Update product information
    - Delete products
    - Search and filter products
    - Manage product details
- User authentication using JWT
- Role-based authorization
    - Admin access control
- Request validation using Jakarta Validation
- REST API
- MariaDB database integration
- Automated integration testing

## Tech stack

- Language: Java 21
- Framework: Spring Boot
- Security: Spring Security + JWT
- ORM: Hibernate / Spring Data JPA
- Database: MariaDB
- Build Tool: Maven
- Testing:
    - JUnit 5
    - Spring Boot Test
    - MockMvc

## Quickstart

### Prerequisites

Make sure you have installed:

- Java 21+
- Maven
- MariaDB

### Clone the repository

```bash
git clone https://github.com/Kelaryon/store-management-tool.git

cd store-management-tool
```

### Configure the database

Create the databases:

```sql
CREATE DATABASE store_management_tool_db;
CREATE DATABASE store_management_tool_db_test;
```

Update the database configuration in:

```
src/main/resources/application.properties
```

Example:

```properties
spring.datasource.url=jdbc:mariadb://localhost:3306/store_management_tool_db
spring.datasource.username=root
spring.datasource.password=your_password
```

### Configure JWT

Add your JWT secret:

```properties
jwt.secret=your_secret_key
```

For production environments, use environment variables instead of storing secrets directly in configuration files.

### Run the application

Using Maven:

```bash
./mvnw spring-boot:run
```

or:

```bash
mvn spring-boot:run
```

The application will start on:

```
http://localhost:8080
```

## Usage

The application exposes REST API endpoints.

### Authentication

Register a new account:

```
POST /auth/signup
```

Login:

```
POST /auth/login
```

The login endpoint returns a JWT token that must be included in authenticated requests:

```
Authorization: Bearer <token>
```

### Products

Create a product:

```
POST /admin/products
```

Update a product:

```
PUT /admin/products/{id}
```

Delete a product:

```
DELETE /admin/products/{id}
```

Get products:

```
GET /admin/products
```

Search products:

```
GET /admin/products/search
```

## Configuration

Main application configuration:

```
src/main/resources/application.properties
```

Test configuration:

```
src/test/resources/application-test.properties
```

Important properties:

```properties
spring.datasource.url=jdbc:mariadb://localhost:3306/store_management_tool_db
spring.datasource.username=root
spring.datasource.password=your_password

spring.jpa.hibernate.ddl-auto=update

jwt.secret=your_secret_key
```

## Testing

The project uses JUnit 5 and Spring Boot Test with MockMvc for integration testing.

Tests cover:

- Product CRUD operations
- Product searching
- Authentication and authorization
- JWT authentication flow
- Request validation
- Controller endpoint behavior

Before running tests:

- Make sure MariaDB is running.
- Create the test database:

```sql
CREATE DATABASE store_management_tool_db_test;
```

The test environment uses the Spring `test` profile and loads configuration from:

```
src/test/resources/application-test.properties
```

The database schema is automatically created/updated during tests.

Run all tests:

```bash
./mvnw test
```

Run a specific test class:

```bash
./mvnw test -Dtest=AdminControllerTest
```

## Contributing

Contributions are welcome.

Steps:

1. Fork the repository
2. Create a feature branch:

```bash
git checkout -b feature/new-feature
```

3. Implement your changes
4. Add or update tests
5. Run the test suite:

```bash
./mvnw test
```

6. Create a pull request describing your changes

## Troubleshooting

### Application cannot connect to MariaDB

Check:

- MariaDB is running
- Database exists
- Credentials in `application.properties` are correct

### Tests fail with database errors

Check:

- `store_management_tool_db_test` exists
- Test database credentials are correct
- MariaDB service is running

### Authentication returns 403 Forbidden

Check:

- JWT token is included:

```
Authorization: Bearer <token>
```

- The authenticated user has the required role/authority.

## License

This project currently does not have a license.

## Contact

Maintainer: Kelaryon

Repository:
https://github.com/Kelaryon/store-management-tool

## Project Status

This project is actively developed and serves as a backend inventory management application.