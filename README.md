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

### Integration Tests
Comprehensive end-to-end tests covering all major user workflows and integrations. These run on every push and pull request to ensure code quality.

**Triggers:**
- Push to `main` and `develop` branches
- Pull requests to `main` and `develop`

**What happens:**
- Checks out backend and frontend code
- Sets up JDK 17 and Node.js with Maven/npm caching
- Installs Playwright browsers and dependencies
- Starts MySQL database, backend (Spring Boot), and frontend (React) services
- Seeds test data with admin user and test store
- Runs `gr.clothesmanager.suites.FullIntegrationTestSuite` (24 tests)
- Uploads test results to artifacts (retained 7 days)
- Uploads screenshots on all runs (retained 7 days)

**Test Coverage (24 tests):**
- **Authentication** (6 tests) - Login validation, error handling, logout
- **Dashboard** (1 test) - Dashboard layout and menu cards
- **Store Management** (3 tests) - Create, edit, delete stores
- **User Management** (2 tests) - Create and delete users
- **Material Management** (8 tests) - Create, edit, delete, search materials
- **Order Management** (3 tests) - Create, edit, delete orders
- **Search Functionality** (2 tests) - Material search with filters

**Test Framework:**
- Playwright for browser automation
- Page Object Model architecture
- JUnit 5 with custom test suites
- Professional wait strategies for CI/CD stability

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
4. Download the relevant artifact:
    - `integration-test-results` - Detailed test reports and logs
    - `integration-test-screenshots` - Screenshots from all test runs (useful for debugging)

## Browser Configuration

- **Integration Tests:** Chromium only (optimized for CI/CD performance)

Browsers are installed with system dependencies (`--with-deps`) to ensure stability on Ubuntu runners.

## Local Setup Requirements

To run tests locally, you'll need:

- **JDK 17** or later
- **Maven 3.6+**
- **Node.js 18+** (for frontend)
- **MySQL 8.0** (for database)
- **Playwright drivers** (installed automatically via Maven)

### Running Tests Locally
```bash
# Build backend
cd backend
mvn clean package -DskipTests

# Start MySQL (or use Docker)
docker run -d -p 3306:3306 \
  -e MYSQL_ROOT_PASSWORD=root \
  -e MYSQL_DATABASE=clothes_manager \
  mysql:8.0

# Seed test data
mysql -u root -proot clothes_manager < src/main/resources/data.sql

# Start backend
java -jar target/clothes-manager-*.jar

# In another terminal, start frontend
cd frontend/frontend
npm install
REACT_APP_API_URL=http://localhost:8080 npm start

# In another terminal, run tests
cd backend
mvn test -Dtest=FullIntegrationTestSuite
```

## Troubleshooting CI Failures

If a workflow fails:

1. **Download Test Results** - Check `integration-test-results` artifact for detailed failures
2. **Review Screenshots** - Download `integration-test-screenshots` for visual debugging
3. **Check Workflow Logs** - Click the failed step in Actions tab for Maven/backend/frontend logs
4. **Verify Test Data** - Ensure `data.sql` includes all required test users and stores
5. **Local Reproduction** - Run the same test suite locally to isolate environment issues

## Test Data Setup

The integration tests use the following test users:

- **Admin User** (SUPER_ADMIN)
    - Username: `admin`
    - Password: `Admin!1234`
    - Can manage all entities (users, stores, materials, orders)

- **Test User** (LOCAL_ADMIN)
    - Username: `testuser`
    - Password: `Admin!1234` (same hash)
    - Assigned to test store, can be deleted in tests

The test store (id=2) is not marked as system entity, allowing delete operations during tests. The central store (id=1) is protected and cannot be deleted.

## Playwright & Java Configuration

The integration tests use:
- **Playwright:** Latest version via Maven dependencies
- **Java:** JDK 17 (Temurin distribution)
- **Build Tool:** Maven with dependency caching for faster builds
- **Test Framework:** JUnit 5 with Surefire reports and custom test suites
- **Page Object Model:** Professional test architecture for maintainability