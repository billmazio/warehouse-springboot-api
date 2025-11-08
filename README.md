# Warehouse Management System - Backend Documentation

## Table of Contents
1. [Backend Overview](#backend-overview)
2. [Architecture](#architecture)
3. [Technology Stack](#technology-stack)


## Backend Overview

The backend of the Warehouse Management System provides RESTful API services for clothing inventory management across multiple store locations. It implements business logic, data validation, authentication, and database interaction.

### Key Objectives
- Implement secure RESTful API services
- Provide role-based access control
- Process and validate inventory data
- Manage database transactions and relationships
- Support multi-store operations

### Target Users
- Frontend application
- Potential future mobile applications
- System integrations

## Architecture

The backend follows a multi-layered architecture using Spring Boot:

```
┌─────────────────────────────────────────┐
│               Backend                   │
│                                         │
│  ┌─────────────┐    ┌─────────────────┐ │
│  │  Controllers│    │     Services    │ │
│  │  (REST API) │◄──►│  (Business Logic)│ │
│  └─────────────┘    └─────────────────┘ │
│          ▲                  ▲           │
│          │                  │           │
│          ▼                  ▼           │
│  ┌─────────────┐    ┌─────────────────┐ │
│  │   Security  │    │  Repositories   │ │
│  │             │    │     (JPA)       │ │
│  └─────────────┘    └─────────────────┘ │
│                          ▲              │
└──────────────────────────┼──────────────┘
                           │
                           ▼
                  ┌─────────────────┐
                  │    Database     │
                  │     (MySQL)     │
                  └─────────────────┘
```

### Key Components
- **Controllers Layer**: REST endpoints for client interaction
- **Services Layer**: Business logic implementation
- **Repositories Layer**: Data access abstraction
- **Security Layer**: Authentication and authorization
- **Database**: Persistent data storage

## Technology Stack

### Core Technologies
- **Spring Boot 3.4.0**: Enterprise Java framework
- **Spring Web**: RESTful API development
- **Spring Security**: Authentication and authorization framework
- **Spring Data JPA**: Data access abstraction layer
- **Hibernate ORM**: Object-relational mapping tool
- **MySQL Connector**: Database connectivity

### Security
- **JWT (JSON Web Tokens)**: Stateless authentication
- **BCrypt**: Password hashing
- **Role-based Access Control**: Permission management

### Development Tools
- **Java 17**: Latest LTS version of Java
- **Maven 3.6+**: Build automation and dependency management
- **Lombok**: Java annotation library for reducing boilerplate
- **JUnit 5**: Testing framework
- **Mockito**: Mocking framework for unit tests


The API will be available at `http://localhost:8080/api`

### Database Setup

1. Create a MySQL database:
```sql
CREATE DATABASE warehouse_db;
```
# End-to-End Testing with Playwright Java

## Overview

This project implements comprehensive end-to-end testing for our Warehouse Management System using Playwright, a modern automation framework that provides reliable end-to-end testing for web applications. Our test structure is designed to mirror the domain model of our application, ensuring that tests respect entity relationships and dependencies.

## Key Features

- **Cross-Browser Testing**: Tests run on Chromium, Firefox, and WebKit
- **Domain-Driven Test Organization**: Tests structured around business entities and their relationships
- **Automatic Wait**: Playwright automatically waits for elements to be actionable
- **Reliable Automation**: Tests are resilient against dynamic content and async operations
- **Page Object Pattern**: Improves test maintainability and readability

## Test Architecture

Our test architecture is organized to reflect the domain model of our application:
```
src/test/java/gr/clothesmanager/
├── BasePlaywrightTest.java            # Base class with common functionality
├── LoginTests.java                    # Authentication tests
├── DashboardTests.java                # Dashboard tests
├── StoreAndUserTests.java             # Tests for stores and users (related entities)
└── MaterialAndOrderTests.java         # Tests for materials and orders (related entities)
```

Tests are structured to respect entity relationships and dependencies, ensuring that:
1. Stores are created before users (since users belong to stores)
2. Materials are created with proper store references
3. Orders are created last (as they depend on users, materials, and stores)
4. Deletion happens in reverse order to respect referential integrity

## Test Base Class

Our `BasePlaywrightTest` class provides common functionality for all tests:
```java
@UsePlaywright
public abstract class BasePlaywrightTest {
    // Common methods for authentication, navigation, and test utilities
    
    protected void loginAsAdmin(Page page) {
        // Login implementation
    }
    
    protected void waitForDashboard(Page page) {
        // Dashboard wait implementation
    }
    
    protected void navigateToDashboardSection(Page page, String cardTestId, String expectedUrl) {
        // Navigation implementation
    }
}
```

## Running the Tests

Tests can be run using JUnit:
```bash
# Run all tests
mvn test

# Run a specific test class
mvn test -Dtest=StoreAndUserTests
```

## JUnit 5 Configuration

We use JUnit 5 with test ordering to ensure that tests run in the correct sequence:
```java
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StoreAndUserTests extends BasePlaywrightTest {
    // Tests with @Order annotations to control execution sequence
}
```

## Playwright Java Setup

Our test infrastructure uses:

- Playwright Java 1.xx.x
- JUnit 5 for test execution and assertions
- Maven for dependency management

Maven dependency:
```xml
<dependency>
    <groupId>com.microsoft.playwright</groupId>
    <artifactId>playwright</artifactId>
    <version>1.XX.0</version>
    <scope>test</scope>
</dependency>
```

## Best Practices Implemented

1. **Page Object Pattern**: Each page in the application has a corresponding Page class
2. **Base Test Class**: Common functionality is centralized in the base test class
3. **Test Independence**: Each test can run independently while respecting entity relationships
4. **Proper Cleanup**: Entities are deleted in reverse dependency order
5. **Meaningful Test Names**: Tests are named to clearly describe what they test
6. **Order Annotations**: Tests run in a specific order to respect entity dependencies

## Future Enhancements

- Implement parallel test execution for faster feedback
- Add visual testing capabilities
- Integrate with CI/CD pipeline
- Add reporting with screenshots and videos for failed tests
