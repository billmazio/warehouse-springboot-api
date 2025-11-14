# Warehouse Management System - Spring Boot API

[![Java](https://img.shields.io/badge/Java-17-orange)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.0-green)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue)](https://www.mysql.com/)
[![Playwright](https://img.shields.io/badge/Playwright-1.40-green)](https://playwright.dev/)

Full-stack warehouse management system REST API with comprehensive end-to-end test automation.

## 🔗 Related Repository

- **Frontend**: [warehouse-react-app](https://github.com/YOUR_USERNAME/warehouse-react-app) - React application

---

## 📋 Table of Contents

- [Backend Overview](#backend-overview)
- [Architecture](#architecture)
- [Features](#features)
- [Technology Stack](#technology-stack)
- [Getting Started](#getting-started)
- [API Documentation](#api-documentation)
- [Testing](#testing)
- [Database Schema](#database-schema)
- [What I Learned](#what-i-learned)

---

## 🎯 Backend Overview

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

---

## 🏗️ Architecture

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

---

## ✨ Features

### Backend API
- ✅ JWT Authentication & Authorization
- ✅ RESTful API endpoints
- ✅ MySQL database with JPA/Hibernate
- ✅ Entity relationships and referential integrity
- ✅ Input validation and error handling
- ✅ CORS configuration for frontend integration
- ✅ Greek language support

### Test Automation
- ✅ 20+ end-to-end test cases
- ✅ Page Object Model design pattern
- ✅ Multiple test suites (Smoke, Regression, Full Integration)
- ✅ Smart waiting strategies (no hardcoded delays)
- ✅ Cross-browser support (Chromium, Firefox, WebKit)
- ✅ Domain-driven test organization

---

## 🛠️ Technology Stack

### Core Backend Technologies
| Technology | Version | Purpose |
|-----------|---------|---------|
| **Java** | 17 | Programming language |
| **Spring Boot** | 3.4.0 | Application framework |
| **Spring Web** | - | RESTful API development |
| **Spring Security** | 6.x | Authentication & authorization |
| **Spring Data JPA** | - | Data access abstraction |
| **Hibernate ORM** | - | Object-relational mapping |
| **MySQL** | 8.0 | Database |
| **MySQL Connector** | - | Database connectivity |
| **JWT** | - | Token-based authentication |
| **BCrypt** | - | Password hashing |
| **Lombok** | - | Reduce boilerplate code |
| **Maven** | 3.8+ | Build & dependency management |

### Testing Technologies
| Technology | Version | Purpose |
|-----------|---------|---------|
| **Playwright** | 1.40.0 | Browser automation |
| **JUnit 5** | 5.10.0 | Testing framework |
| **Maven Surefire** | - | Test execution |
| **Mockito** | - | Mocking framework (unit tests) |

---

## 🚀 Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.8+
- MySQL 8.0
- Node.js 16+ (for frontend)

### Installation

1. **Clone the repository**
```bash
git clone https://github.com/YOUR_USERNAME/warehouse-springboot-api.git
cd warehouse-springboot-api
```

2. **Configure database**

Create MySQL database:
```sql
CREATE DATABASE warehouse_db;
```

3. **Update application.properties**

Edit `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/warehouse_db
spring.datasource.username=your_username
spring.datasource.password=your_password

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Server configuration
server.port=8080
```

4. **Build and run**
```bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

API will be available at `http://localhost:8080/api`

---

## 📚 API Documentation

### Authentication Endpoints

**Login**
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}

Response:
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "admin",
  "role": "ADMIN"
}
```

### Materials Endpoints
```http
GET    /api/materials              # Get all materials
POST   /api/materials              # Create material
GET    /api/materials/{id}         # Get material by ID
PUT    /api/materials/{id}         # Update material
DELETE /api/materials/{id}         # Delete material
```

**Create Material Example:**
```json
POST /api/materials
{
  "name": "Μπλούζα Polo",
  "size": "MEDIUM",
  "quantity": 10,
  "storeId": 1
}
```

### Orders Endpoints
```http
GET    /api/orders                 # Get all orders
POST   /api/orders                 # Create order
PUT    /api/orders/{id}            # Update order
DELETE /api/orders/{id}            # Delete order
```

**Create Order Example:**
```json
POST /api/orders
{
  "materialId": 1,
  "userId": 2,
  "quantity": 5,
  "date": "2025-12-31",
  "status": "PENDING"
}
```

### Stores Endpoints
```http
GET    /api/stores                 # Get all stores
POST   /api/stores                 # Create store
PUT    /api/stores/{id}            # Update store
DELETE /api/stores/{id}            # Delete store
```

### Users Endpoints
```http
GET    /api/users                  # Get all users
POST   /api/users                  # Create user
DELETE /api/users/{id}             # Delete user
```

**Note:** All endpoints except `/api/auth/login` require JWT token in Authorization header:
```
Authorization: Bearer <your_jwt_token>
```

---

## 🧪 Testing

This project includes comprehensive end-to-end test automation using Playwright with Page Object Model design pattern.

### Test Architecture
```
src/test/java/gr/clothesmanager/
├── base/
│   └── BaseTest.java                    # Test configuration & common utilities
├── pages/                               # Page Object Model
│   ├── BasePage.java                    # Base page with common methods
│   ├── LoginPage.java                   # Login page object
│   ├── DashboardPage.java               # Dashboard page object
│   ├── MaterialsPage.java               # Materials page object
│   ├── OrdersPage.java                  # Orders page object
│   ├── StoresPage.java                  # Stores page object
│   └── UsersPage.java                   # Users page object
├── components/
│   └── ConfirmationDialog.java          # Reusable dialog component
├── tests/                               # Test cases organized by feature
│   ├── login/
│   │   └── LoginTests.java
│   ├── dashboard/
│   │   └── DashboardTests.java
│   ├── materials/
│   │   ├── MaterialCreateTests.java
│   │   ├── MaterialEditTests.java
│   │   ├── MaterialSearchTests.java
│   │   └── MaterialDeleteTests.java
│   ├── orders/
│   │   ├── OrderCreateTests.java
│   │   ├── OrderEditTests.java
│   │   └── OrderDeleteTests.java
│   ├── stores/
│   │   ├── StoreCreateTests.java
│   │   ├── StoreEditTests.java
│   │   └── StoreDeleteTests.java
│   └── users/
│       ├── UserCreateTests.java
│       └── UserDeleteTests.java
├── suites/                              # Test suites
│   ├── SmokeTestSuite.java
│   ├── RegressionTestSuite.java
│   └── FullIntegrationTestSuite.java
├── constants/
│   └── TestConstants.java
└── utils/
    └── TestDataFactory.java
```

### Running Tests

**Prerequisites:**
1. Backend API running on `http://localhost:8080`
2. Frontend running on `http://localhost:3000`
3. Install Playwright browsers:
```bash
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install"
```

**Run all tests:**
```bash
mvn test
```

**Run specific test suite:**
```bash
# Smoke tests (2-3 minutes) - Quick validation of critical functionality
mvn test -Dtest=SmokeTestSuite

# Regression tests (5-7 minutes) - Comprehensive CRUD testing
mvn test -Dtest=RegressionTestSuite

# Full integration tests (7-10 minutes) - Complete end-to-end coverage
mvn test -Dtest=FullIntegrationTestSuite
```

**Run specific test class:**
```bash
mvn test -Dtest=LoginTests
mvn test -Dtest=MaterialCreateTests
```

**Run specific test method:**
```bash
mvn test -Dtest=LoginTests#shouldLoginSuccessfully
```

### Test Coverage

| Feature | Test Cases | Operations Covered |
|---------|-----------|-------------------|
| **Authentication** | 5 | Login validation, field validation, error handling, logout |
| **Dashboard** | 1 | Navigation to all sections |
| **Materials** | 6 | Create, Edit, Delete, Search by name, Filter by size |
| **Orders** | 3 | Create with dependencies, Edit, Delete |
| **Stores** | 3 | Create, Edit address, Delete |
| **Users** | 2 | Create with store association, Delete |
| **Total** | **20+** | Complete CRUD operations with validations |

### Test Design Principles

✅ **Page Object Model Pattern**
- Clean separation of test logic and page interactions
- Reusable page components
- Easy maintenance when UI changes

✅ **Domain-Driven Organization**
- Tests respect entity relationships (Stores → Users → Materials → Orders)
- Tests execute in correct dependency order
- Proper cleanup maintains referential integrity

✅ **Smart Waiting Strategies**
- Dynamic waits based on actual conditions
- No hardcoded `Thread.sleep()` calls
- Network idle detection for API calls
- Element visibility checks

✅ **Professional Code Structure**
- Public methods for test workflows
- Private methods for implementation details
- Comprehensive JavaDoc documentation
- Consistent naming conventions

### Example Test
```java
@Test
@DisplayName("Should create material successfully")
public void shouldCreateMaterial(Page page) {
    // Arrange
    DashboardPage dashboard = loginAsAdmin(page);
    MaterialsPage materials = dashboard.navigateToMaterials().waitForLoad();
    int initialCount = materials.getMaterialCount();
    
    // Act
    materials.addMaterial("Μπλούζα Polo", "MEDIUM", "10", "ΚΕΝΤΡΙΚΑ");
    
    // Assert
    assertEquals(initialCount + 1, materials.getMaterialCount());
}
```

### Page Object Model Example
```java
public class MaterialsPage extends BasePage {
    
    // Private helper methods (implementation details)
    private void fillMaterialName(String name) {
        fillByTestId(MATERIAL_NAME_INPUT, name);
    }
    
    // Public workflow method (what tests use)
    public void addMaterial(String name, String size, String quantity, String store) {
        clickAddMaterial();
        fillMaterialName(name);
        selectMaterialSize(size);
        fillMaterialQuantity(quantity);
        selectMaterialStore(store);
        submitAddMaterial();
    }
}
```

---

## 🗄️ Database Schema

### Entity Relationships
```
Store (1) ────────→ (N) User
  │
  └──────────────→ (N) Material
                      │
                      └──────→ (N) Order ←────── (N) User
```

### Dependency Order

**Creation Phase:**
```
1. Stores (no dependencies)
   ↓
2. Users (depend on stores)
   ↓
3. Materials (depend on stores)
   ↓
4. Orders (depend on materials, users, stores)
```

**Deletion Phase (reverse order):**
```
1. Orders (first - has most dependencies)
   ↓
2. Materials (orders depend on them)
   ↓
3. Users (depend on stores)
   ↓
4. Stores (last - users depend on them)
```

This structure ensures referential integrity throughout the application lifecycle.

---

## 📖 What I Learned

Building this full-stack project with comprehensive test automation helped me develop:

### Backend Development
- ✅ Spring Boot REST API architecture
- ✅ JWT authentication and authorization implementation
- ✅ Database design with complex entity relationships
- ✅ JPA/Hibernate ORM configuration
- ✅ Input validation and error handling
- ✅ CORS configuration for frontend integration

### Test Automation
- ✅ Page Object Model design pattern implementation
- ✅ Professional test automation architecture
- ✅ Playwright browser automation
- ✅ Domain-driven test organization
- ✅ Smart waiting strategies for reliable tests
- ✅ Test suite organization (Smoke, Regression, Full Integration)
- ✅ Cross-browser testing strategies

### Software Engineering
- ✅ Clean code principles and documentation
- ✅ Separation of concerns in layered architecture
- ✅ Professional project organization
- ✅ Git version control best practices

---

## 🔮 Future Enhancements

### Backend
- [ ] API documentation with Swagger/OpenAPI
- [ ] Unit tests for service layer
- [ ] Integration tests with TestContainers
- [ ] Docker containerization
- [ ] Monitoring and logging (ELK stack)
- [ ] Performance optimization
- [ ] API rate limiting

### Testing
- [ ] API test automation with RestAssured
- [ ] Parallel test execution for faster feedback
- [ ] Visual regression testing
- [ ] CI/CD integration with GitHub Actions
- [ ] Test reporting with Allure
- [ ] Performance testing with JMeter

---

## 👨‍💻 Author

**Bill Papadopoulos**
- 2 years Java development experience
- 1 year manual testing experience
- Specializing in full-stack development and test automation
- Seeking Junior SDET opportunities

---

## 📝 License

This project is part of a portfolio demonstrating full-stack development and test automation skills.

---

## 🙏 Acknowledgments

Built using modern software development best practices and design patterns from the Spring Boot and test automation communities.
