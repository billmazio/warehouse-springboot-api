# Warehouse Management System - Spring Boot API

[![Java](https://img.shields.io/badge/Java-17-orange)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.0-green)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue)](https://www.mysql.com/)
[![Playwright](https://img.shields.io/badge/Playwright-1.40-green)](https://playwright.dev/)
[![Tests](https://img.shields.io/badge/Tests-20%2B%20Passing-green)]()

REST API for warehouse inventory management with comprehensive E2E test automation.

## 🔗 Related Repository
- **Frontend**: [warehouse-react-ui](https://github.com/billmazio/warehouse-react-ui)

---

## 🎯 Overview

Full-stack warehouse management system for clothing inventory across multiple store locations. Built with Spring Boot, includes JWT authentication, role-based access control, and 20+ automated test cases using Playwright.

**Key Features:**
- JWT Authentication & Authorization
- CRUD operations for materials, orders, stores, and users
- Professional Page Object Model test automation

---

## 🛠️ Tech Stack

**Backend:** Java 17 • Spring Boot • Spring Security • JPA/Hibernate • MySQL • JWT

**Testing:** Playwright • JUnit 5 • AssertJ • Maven • Page Object Model

---

## 🗄️ Database

**Core Entities:** Store, User, Material, Order, Size, UserRole

**Key Relationships:**
- Store contains Users, Materials, Orders
- Order connects User + Material + Size + Store
- Users have Roles (many-to-many with UserRole)

**Dependency Order for Tests:** Store/Size/Role → User/Material → Order

## 🚀 Quick Start

### Prerequisites
- Java 17+
- Maven 3.8+
- MySQL 8.0

### Setup
```bash
# Clone repository
git clone https://github.com/billmazio/warehouse-springboot-api.git

# Configure database in application.properties
# spring.datasource.url=jdbc:mysql://localhost:3306/warehouse_db

# Run application
mvn spring-boot:run
```

---

## 🧪 Testing

Comprehensive E2E test suite with **20+ test cases** using Playwright and Page Object Model.

### Test Structure
```
src/test/java/gr/clothesmanager/
├── components/    # Reusable components
├── config/        # Browser config
├── constants/     # Test constants (URLs, credentials, timeouts)
├── helpers/       # Helper utility for reusable login flows
├── pages/         # 7 Page Objects (POM pattern)
│   ├── BasePage.java
│   ├── DashboardPage.java
│   ├── LoginPage.java
│   ├── MaterialsPage.java
│   ├── OrdersPage.java
│   ├── StoresPage.java
│   └── UsersPage.java
├── suites/        # 3 test suites
└── tests/         # 14 test classes, 20+ test cases
```

### Test Execution Strategy

Tests run **sequentially** to maintain referential integrity:
- `@TestMethodOrder(MethodOrderer.OrderAnnotation.class)` ensures dependency order
- Store/Size/Role entities created first, then Users/Materials, finally Orders
- Sequential execution prevents foreign key constraint violations
- AssertJ assertions provide clear, readable validation

### Run Tests
```bash
# All tests
mvn test

# Specific suite
mvn test -Dtest=SmokeTestSuite          
mvn test -Dtest=RegressionTestSuite      
mvn test -Dtest=FullIntegrationTestSuite
```

**Test Prerequisites:**
- Backend running on port `8080` (`mvn spring-boot:run`)
- Frontend running on port `3000` (`npm start`)
- MySQL running with test database configured

### Test Coverage

| Feature | Tests | Coverage |
|---------|-------|----------|
| Authentication | 5 | Login, validation, errors |
| Dashboard | 1 | Navigation, view |   
| Materials | 6 | Create, edit, delete, search |
| Orders | 3 | Create, edit, delete |
| Stores | 3 | Create, edit, delete |
| Users | 2 | Create, delete |

**Total: 20+ automated tests** with Page Object Model design pattern

---
# Continuous Integration & Deployment

This project uses GitHub Actions to automate test execution across three distinct test suites. All workflows are configured to run your Playwright tests with Java 17, Maven, and JUnit 5.

## Test Workflows Overview

### Smoke Tests
Lightweight, quick-running tests that verify core functionality. These run on every push to `main` and `develop` branches, as well as on pull requests to `main`. This provides fast feedback during development and code review.

**Triggers:**
- Push to `main` or `develop` branches
- Pull requests to `main`

**What happens:**
- Checks out your code
- Sets up JDK 17 with Maven caching
- Installs Playwright (chromium only for speed)
- Runs `gr.clothesmanager.suites.SmokeTestSuite`
- Uploads test results (retained 7 days)
- Uploads screenshots on failure (retained 7 days)

### Full Integration Tests
Comprehensive end-to-end tests covering all major user workflows and integrations. These run on push, pull requests, and daily at 2 AM UTC to catch issues in your main branch.

**Triggers:**
- Push to `main` branch
- Pull requests to `main`
- Daily schedule: 2 AM UTC

**What happens:**
- Checks out your code
- Sets up JDK 17 with Maven caching
- Installs all Playwright browsers
- Runs `gr.clothesmanager.suites.FullIntegrationTestSuite`
- Uploads test results to artifacts (retained 30 days)
- Uploads screenshots on failure (retained 7 days)
- Publishes formatted test report using dorny/test-reporter

### Regression Tests
Deep test suite that validates no existing functionality has broken. Runs weekly on Sunday at midnight UTC to provide a regular health check without blocking development.

**Triggers:**
- Weekly schedule: Every Sunday at midnight UTC
- Manual trigger (if configured)

**What happens:**
- Checks out your code
- Sets up JDK 17 with Maven caching
- Installs all Playwright browsers with system dependencies
- Runs `gr.clothesmanager.suites.RegressionTestSuite`
- Uploads test results to artifacts (retained 30 days)
- Uploads screenshots on failure

## Running Tests Locally

To run the same test suites locally before pushing:

```bash
# Run Smoke Tests
mvn test -Dtest=gr.clothesmanager.suites.SmokeTestSuite

# Run Full Integration Tests
mvn test -Dtest=gr.clothesmanager.suites.FullIntegrationTestSuite

# Run Regression Tests
mvn test -Dtest=gr.clothesmanager.suites.RegressionTestSuite
```

## Viewing Test Results

All workflows upload artifacts that you can download:

1. Go to the **Actions** tab in your GitHub repository
2. Click on the workflow run you want to inspect
3. Scroll to the **Artifacts** section
4. Download the relevant artifact (test results or screenshots)

For the Full Integration Tests workflow, a formatted test report is also published directly in the workflow summary.

## Browser Configuration

- **Smoke Tests:** Chromium only (faster execution)
- **Integration & Regression Tests:** All Playwright browsers (chromium, firefox, webkit)

All browsers are installed with system dependencies (`--with-deps`) to ensure stability on Ubuntu runners.

## Requirements

The workflows automatically handle setup, but locally you'll need:

- JDK 17 or later
- Maven 3.6+
- Playwright drivers (installed via Maven plugin)
- Node.js (optional, if using Node-based Playwright tools)

## Troubleshooting CI Failures

If a workflow fails, check the following:

1. **Test Results Artifact:** Download to see detailed test failures and logs
2. **Screenshots Artifact:** Review failure screenshots for visual debugging
3. **Workflow Logs:** Click the failed step in the Actions tab for detailed Maven/test output
4. **Local Reproduction:** Run the same test suite locally with `mvn test -Dtest=<SuiteClass>`

## Playwright & Java Configuration

These workflows use:
- **Playwright:** Latest version via Maven dependencies
- **Java:** JDK 17 (Temurin distribution)
- **Build Tool:** Maven with caching enabled for faster builds
- **Test Framework:** JUnit 5 with SureFire reports
