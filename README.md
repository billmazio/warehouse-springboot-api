# Warehouse Management System - Spring Boot API

[![Java](https://img.shields.io/badge/Java-17-orange)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.0-green)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue)](https://www.mysql.com/)
[![Playwright](https://img.shields.io/badge/Playwright-1.40-green)](https://playwright.dev/)

REST API for warehouse inventory management with comprehensive E2E test automation.

## 🔗 Related Repository
- **Frontend**: [warehouse-react-app](https://github.com/YOUR_USERNAME/warehouse-react-ui)

---

## 🎯 Overview

Full-stack warehouse management system for clothing inventory across multiple store locations. Built with Spring Boot, includes JWT authentication, role-based access control, and 20+ automated test cases using Playwright.

**Key Features:**
- JWT Authentication & Authorization
- CRUD operations for materials, orders, stores, and users
- Entity relationship management (stores → users → materials → orders)
- Greek language support
- Professional Page Object Model test automation

---

## 🛠️ Tech Stack

**Backend:** Java 17 • Spring Boot • Spring Security • JPA/Hibernate • MySQL • JWT  
**Testing:** Playwright • JUnit 5 • Maven • Page Object Model

---

## 🚀 Quick Start

### Prerequisites
- Java 17+
- Maven 3.8+
- MySQL 8.0

### Setup
```bash
# Clone repository
git clone https://github.com/billmazio/warehouse-springboot-api.git

```

---

## 🧪 Testing

Comprehensive E2E test suite with **20+ test cases** using Playwright and Page Object Model.

### Test Structure
```
src/test/java/
├── base/ 
├── components/  / 
├── constants/ 
├── pages/          # Page Object Model (LoginPage, MaterialsPage, etc.)
├── tests/          # Test cases by feature (login, materials, orders, etc.)
└── suites/         # Test suites (Smoke, Regression, Full Integration)
```

### Run Tests
```bash
# All tests
mvn test

# Specific suite
mvn test -Dtest=SmokeTestSuite          
mvn test -Dtest=RegressionTestSuite      
mvn test -Dtest=FullIntegrationTestSuite 
```

**Prerequisites:** Backend running on `:8080`, Frontend on `:3000`

### Test Coverage

| Feature | Tests | Coverage |
|---------|-------|----------|
| Authentication | 5 | Login, validation, errors |
| Materials | 6 | Create, edit, delete, search |
| Orders | 3 | Create, edit, delete |
| Stores | 3 | Create, edit, delete |
| Users | 2 | Create, delete |

**Total: 25+ automated tests** with Page Object Model design pattern

---


```
