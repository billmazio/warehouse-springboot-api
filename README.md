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

### Prerequisites
- Java 17 or higher
- Node.js 16+ and npm
- MySQL 8.0+
- Maven 3.6+
- Git


```
