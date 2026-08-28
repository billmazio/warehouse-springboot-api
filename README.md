# Warehouse Management System 

[![Java](https://img.shields.io/badge/Java-17-orange)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.0-green)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.2.0-blue)](https://www.mysql.com/)
[![Playwright](https://img.shields.io/badge/Playwright-1.48-green)](https://playwright.dev/)
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

**DevOps:** GitHub Actions • MySQL • Allure Reports

---

## 🗄️ Database

**Core Entities:** Users, Materials, Orders, Roles, Sizes, UserRoles, Stores

**Key Relationships:**

- Store → Users / Materials / Orders (One-to-Many)
- Size → Materials (One-to-Many)
- User → Orders (One-to-Many)
- User ↔ Roles (Many-to-Many)
- Material → Size / Store (Many-to-One)
- Order → Material / Size / Store / User (Many-to-One)

## 🚀 Quick Start

### Prerequisites
- Java 17+
- Maven 3.8+
- MySQL 8.2

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

Comprehensive UI and API test suites using Playwright, JUnit 5, Page Objects, and fluent builders.

## Test Structure
```
src/test/java/gr/clothesmanager/
├── builders/        # Fluent request builders
├── components/      # Reusable UI components
├── config/          # Browser configuration
├── constants/       # Test constants (URLs, credentials)
├── helpers/         # Reusable test utilities
├── pages/           # UI and API Page Objects (POM pattern)
├── suites/          # UI and API test suites
└── tests/           # UI and API test classes
```

## Test Coverage


- **Dashboard** (1 test) Layout and navigation verification
- **Login** (6 tests) Page title, validation, empty fields (2), invalid credentials, logout
- **Materials** (9 tests) - Create with different sizes (5), delete, edit, search, filter
- **Materials API** - Authentication, CRUD, filtering, pagination, and distribution
- **Orders** (3 tests) - Create (includes material creation), delete, edit
- **Stores** (2 tests) - Delete, edit
- **Users** (2 tests) - Create (includes store creation), delete

**Total: 23 tests**

## Running Tests Locally

### Prerequisites
- JDK 17+
- Maven 3.8+
- Node.js 22+
- MySQL 8.2

## REST API Docs (OpenAPI / Swagger)

[Live Swagger UI](https://billmazio.github.io/warehouse-springboot-api/swagger/)

Docs include:
- JWT authentication endpoints (login/logout/change-password)
- Users & role management
- Stores, materials/inventory, and orders (CRUD + pagination)
- Request/response schemas and validation errors


## Test Data

The `data.sql` file automatically seeds the database with:

**Test Users:**
- `admin` (SUPER_ADMIN) - Can manage all entities, cannot be deleted
- `testuser` (LOCAL_ADMIN) - Can be deleted in tests

**Test Stores:**
- Store 1: ΚΕΝΤΡΙΚΑ (Central) - Protected, cannot be deleted
- Store 2: TEST_STORE - Can be deleted in tests

**Test Roles:**
- SUPER_ADMIN - Full system access
- LOCAL_ADMIN - Store-level access

**Other Test Data:**
- 5 Size options (XS, S, M, L, XL)
- Sample materials and orders

The test data is automatically loaded when the backend starts with:
```bash
--spring.sql.init.mode=always
```

This ensures tests always have consistent data to work with.

## 🔄 CI/CD Pipeline

Automated testing on every push using GitHub Actions.

**Triggered on:**
- Push to `main` and `develop` branches
- Pull requests to `main` and `develop`

**Pipeline includes:**
- Maven build and compilation
- Playwright test execution (23 tests in ~29 seconds)
- Allure Report generation
- Auto-deployment to GitHub Pages

**Artifacts:**
- `integration-test-results` - Test reports
- `integration-test-screenshots` - Failure screenshots for debugging
- Live Allure Report - Published automatically to GitHub Pages

---

## 🛠️ Troubleshooting

**Tests fail locally?**
1. Verify MySQL is running and database exists
2. Verify backend is running on http://localhost:8080
3. Verify frontend is running on http://localhost:3000
4. Check that all services started correctly in console

**Tests fail in CI/CD?**
1. Check GitHub Actions logs for error messages
2. Download `integration-test-results` artifact for detailed reports
3. Reproduce the issue locally to identify root cause

---

## 📊 Test Results & Allure Report

[Live Allure Report](https://billmazio.github.io/warehouse-springboot-api/)

**Report includes:**
- 23 test cases with 100% pass rate
- 13 test suites covering all modules
- Complete execution timeline and statistics
- Failure screenshots and detailed logs  
