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
├── base/          # BaseTest configuration
├── components/    # Reusable components
├── constants/     # Test constants (URLs, credentials, timeouts)
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
| Materials | 6 | Create, edit, delete, search |
| Orders | 3 | Create, edit, delete |
| Stores | 3 | Create, edit, delete |
| Users | 2 | Create, delete |

**Total: 20+ automated tests** with Page Object Model design pattern

---
