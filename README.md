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

Comprehensive E2E test suite with 24 automated tests using Playwright and Page Object Model.

## Test Structure
```
src/test/java/gr/clothesmanager/
├── components/      # Reusable UI components
├── config/          # Browser configuration
├── constants/       # Test constants (URLs, credentials)
├── helpers/         # Reusable test utilities
├── pages/           # 7 Page Objects (POM pattern)
├── suites/          # TestSuites
└── tests/           # 13 test classes
```

## Test Coverage


- **Dashboard** (1 test) Layout and navigation verification
- **Login** (5 tests) Page title, validation, empty fields, invalid credentials, logout
- **Materials** (5 tests) - Create with different sizes (5), delete, edit, search, filter
- **Orders** (3 tests) - Create (includes material creation), delete, edit
- **Stores** (2 tests) - Delete, edit
- **Users** (2 tests) - Create (includes store creation), delete

**Total: 18 tests**

## Running Tests Locally

### Prerequisites
- JDK 17+
- Maven 3.6+
- Node.js 18+
- MySQL 8.0+

### Setup
```bash
# Start MySQL
docker run -d -p 3306:3306 \
  -e MYSQL_ROOT_PASSWORD=root \
  -e MYSQL_DATABASE=warehouse_db \
  mysql:8.0

# Seed test data
mysql -u root -proot warehouse_db < src/main/resources/data.sql

# Build and run backend
mvn clean package -DskipTests
java -jar target/clothes-manager-*.jar

# In new terminal: Start frontend
cd frontend
npm install
REACT_APP_API_URL=http://localhost:8080 npm start

# In new terminal: Run tests
mvn test -Dtest=FullIntegrationTestSuite
```

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

## CI/CD

Tests run automatically on:
- Push to `main` and `develop`
- Pull requests to `main` and `develop`

**Test Results:** Download from Actions > Artifacts
- `integration-test-results` - Test reports
- `integration-test-screenshots` - Failure screenshots

## Troubleshooting

**Tests fail locally?**
- Verify MySQL is running
- Verify backend is running on http://localhost:8080
- Verify frontend is running on http://localhost:3000

**Tests fail in CI?**
1. Download `integration-test-results` artifact
2. Check test failure details in HTML report
3. Download `integration-test-screenshots` for visual debugging
4. Run test locally to reproduce
