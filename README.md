# Warehouse Management System - Developer Documentation

## Table of Contents

1. [Project Overview](#project-overview)
2. [Architecture Overview](#architecture-overview)
3. [Project Structure](#project-structure)
4. [Technology Stack](#technology-stack)
5. [Data Models](#data-models)
6. [API Documentation](#api-documentation)
7. [Development Setup](#development-setup)
8. [Implementation Details](#implementation-details)
9. [Testing Procedures](#testing-procedures)
10. [Deployment Instructions](#deployment-instructions)
11. [Security](#security)
12. [Contributing Guidelines](#contributing-guidelines)

## Project Overview

The Warehouse Management System is a full-stack desktop application designed for clothing inventory management across multiple store locations. The system provides role-based access control and comprehensive inventory tracking capabilities for retail operations.

### Key Objectives
- Centralized clothing inventory management
- Multi-store operation support
- Role-based access control for different user levels
- Real-time inventory tracking and updates
- Size and quantity management for clothing items

### Target Users
- Store employees with LOCAL_ADMIN privileges
- System administrators with SUPER_ADMIN privileges
- Inventory managers and warehouse staff

## Architecture Overview

The application follows a modern three-tier architecture pattern:

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Frontend      │    │    Backend      │    │    Database     │
│   (React.js)    │◄──►│  (Spring Boot)  │◄──►│    (MySQL)      │
│   Port: 3000    │    │   Port: 8080    │    │   Port: 3306    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

### Frontend Layer (React.js)
- User interface and user experience management
- State management using React Hooks
- JWT token handling and route protection
- API communication via Axios
- Responsive design with custom CSS

### Backend Layer (Spring Boot)
- RESTful API services
- Business logic implementation
- JWT authentication and authorization
- Data validation and processing
- Database interaction through JPA/Hibernate

### Data Layer (MySQL)
- Persistent data storage
- Relational data modeling
- Transaction management
- Data integrity constraints

## Project Structure

### Frontend Structure (React.js)

### Backend Structure (Spring Boot)


## Technology Stack

### Frontend Technologies
- **React.js 18+**: Modern JavaScript library for building user interfaces
- **React Router**: Declarative routing for React applications
- **Axios**: Promise-based HTTP client for API communication
- **Custom CSS**: Responsive styling with consistent design system
- **React Hooks**: State management and lifecycle methods

### Backend Technologies
- **Spring Boot 3.4.0**: Enterprise Java framework
- **Spring Security**: Authentication and authorization framework
- **Spring Data JPA**: Data access abstraction layer
- **Hibernate ORM**: Object-relational mapping tool
- **MySQL Connector**: Database connectivity
- **JWT (JSON Web Tokens)**: Stateless authentication
- **Lombok**: Java annotation library for reducing boilerplate
- **Maven**: Build automation and dependency management

### Database
- **MySQL 8.0+**: Relational database management system

### Development Tools
- **Java 17**: Latest LTS version of Java
- **Node.js 16+**: JavaScript runtime for frontend development
- **npm**: Package manager for JavaScript

## Data Models

### User Entity
```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String username;
    
    @Column(nullable = false)
    private String password;
    
    @Enumerated(EnumType.STRING)
    private Role role; // SUPER_ADMIN, LOCAL_ADMIN
    
    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store assignedStore;
    
    // Additional fields: email, firstName, lastName, createdAt, updatedAt
}
```

### Store Entity
```java
@Entity
@Table(name = "stores")
public class Store {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String location;
    
    @Column(nullable = false)
    private String address;
    
    private String phone;
    private String email;
    
    @Enumerated(EnumType.STRING)
    private StoreStatus status; // ACTIVE, INACTIVE
    
    @OneToMany(mappedBy = "store")
    private List<Material> materials;
    
    // Additional fields: manager, createdAt, updatedAt
}
```

### Material Entity
```java
@Entity
@Table(name = "materials")
public class Material {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    private String description;
    private String category;
    private String brand;
    private String color;
    
    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;
    
    @OneToMany(mappedBy = "material", cascade = CascadeType.ALL)
    private List<MaterialSize> sizes;
    
    // Additional fields: sku, price, createdAt, updatedAt
}
```

### Size Entity
```java
@Entity
@Table(name = "sizes")
public class Size {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String name; // XS, S, M, L, XL, XXL
    
    private String description;
    private Integer sortOrder;
    
    // Additional fields: isActive, createdAt, updatedAt
}
```

### MaterialSize Entity (Junction Table)
```java
@Entity
@Table(name = "material_sizes")
public class MaterialSize {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "material_id")
    private Material material;
    
    @ManyToOne
    @JoinColumn(name = "size_id")
    private Size size;
    
    @Column(nullable = false)
    private Integer quantity;
    
    // Additional fields: reservedQuantity, createdAt, updatedAt
}
```

## API Documentation

### Authentication Endpoints

#### POST /api/auth/login
Authenticate user and return JWT token.

**Request Body:**
```json
{
    "username": "admin@example.com",
    "password": "securePassword123"
}
```

**Response (200 OK):**
```json
{
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "user": {
        "id": 1,
        "username": "admin@example.com",
        "role": "SUPER_ADMIN",
        "store": {
            "id": 1,
            "name": "Main Store"
        }
    }
}
```

#### POST /api/auth/logout
Logout user (client-side token removal).

### Store Management Endpoints

#### GET /api/stores
Retrieve all stores (SUPER_ADMIN) or assigned store (LOCAL_ADMIN).

**Query Parameters:**
- `page` (optional): Page number (default: 0)
- `size` (optional): Page size (default: 10)
- `search` (optional): Search term for store name
- `status` (optional): Filter by store status

**Response (200 OK):**
```json
{
    "content": [
        {
            "id": 1,
            "name": "Downtown Store",
            "location": "Downtown",
            "address": "123 Main St, City, State 12345",
            "phone": "+1-555-0123",
            "email": "downtown@example.com",
            "status": "ACTIVE",
            "materialCount": 150
        }
    ],
    "totalElements": 5,
    "totalPages": 1,
    "size": 10,
    "number": 0
}
```

#### POST /api/stores
Create a new store (SUPER_ADMIN only).

**Request Body:**
```json
{
    "name": "New Store Location",
    "location": "Suburb",
    "address": "456 Oak Ave, City, State 67890",
    "phone": "+1-555-0456",
    "email": "newstore@example.com",
    "status": "ACTIVE"
}
```

#### PUT /api/stores/{id}
Update existing store information.

#### DELETE /api/stores/{id}
Soft delete a store (SUPER_ADMIN only).

### Material Management Endpoints

#### GET /api/materials
Retrieve materials based on user role and store access.

**Query Parameters:**
- `page`, `size`: Pagination
- `search`: Search in name, description, category
- `category`: Filter by category
- `storeId`: Filter by store (SUPER_ADMIN only)
- `lowStock`: Show items with quantity below threshold

**Response (200 OK):**
```json
{
    "content": [
        {
            "id": 1,
            "name": "Cotton T-Shirt",
            "description": "Basic cotton t-shirt",
            "category": "Shirts",
            "brand": "BrandName",
            "color": "Blue",
            "sku": "TS-001-BLU",
            "store": {
                "id": 1,
                "name": "Downtown Store"
            },
            "sizes": [
                {
                    "size": {
                        "id": 1,
                        "name": "M"
                    },
                    "quantity": 25
                },
                {
                    "size": {
                        "id": 2,
                        "name": "L"
                    },
                    "quantity": 30
                }
            ],
            "totalQuantity": 55
        }
    ]
}
```

#### POST /api/materials
Add new material to inventory.

#### PUT /api/materials/{id}
Update material information and quantities.

#### DELETE /api/materials/{id}
Remove material from inventory.

### Size Reference Endpoints

#### GET /api/sizes
Get all available sizes.

**Response (200 OK):**
```json
[
    {
        "id": 1,
        "name": "XS",
        "description": "Extra Small",
        "sortOrder": 1
    },
    {
        "id": 2,
        "name": "S",
        "description": "Small",
        "sortOrder": 2
    }
]
```

## Development Setup

### Prerequisites
- Java 17 or higher
- Node.js 16+ and npm
- MySQL 8.0+
- Maven 3.6+
- Git

### Backend Setup

1. **Clone the repository:**
```bash
git clone <repository-url>
cd warehouse-management-system/backend
```

2. **Configure database connection:**
Edit `src/main/resources/application.properties`:
```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/warehouse_db
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

# JWT Configuration
jwt.secret=mySecretKey
jwt.expiration=86400000

# Server Configuration
server.port=8080
```

3. **Create database:**
```sql
CREATE DATABASE warehouse_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

4. **Install dependencies and run:**
```bash
mvn clean install
mvn spring-boot:run
```

### Frontend Setup

1. **Navigate to frontend directory:**
```bash
cd warehouse-management-system/frontend
```

2. **Install dependencies:**
```bash
npm install
```

3. **Configure API base URL:**
Edit `src/services/api.js`:
```javascript
const API_BASE_URL = 'http://localhost:8080/api';
```

4. **Start development server:**
```bash
npm start
```

The application will be available at:
- Frontend: http://localhost:3000
- Backend API: http://localhost:8080

### Development Database Setup

1. **Create initial admin user:**
```sql
INSERT INTO users (username, password, role, created_at, updated_at) 
VALUES ('admin@warehouse.com', '$2a$10$hash_of_password', 'SUPER_ADMIN', NOW(), NOW());
```

2. **Create sample store:**
```sql
INSERT INTO stores (name, location, address, phone, email, status, created_at, updated_at)
VALUES ('Main Store', 'Downtown', '123 Main St', '+1-555-0123', 'main@warehouse.com', 'ACTIVE', NOW(), NOW());
```

3. **Create default sizes:**
```sql
INSERT INTO sizes (name, description, sort_order, is_active, created_at, updated_at) VALUES
('XS', 'Extra Small', 1, true, NOW(), NOW()),
('S', 'Small', 2, true, NOW(), NOW()),
('M', 'Medium', 3, true, NOW(), NOW()),
('L', 'Large', 4, true, NOW(), NOW()),
('XL', 'Extra Large', 5, true, NOW(), NOW()),
('XXL', 'Double Extra Large', 6, true, NOW(), NOW());
```
