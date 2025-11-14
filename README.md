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
# Warehouse Management System - Spring Boot API

[![Java](https://img.shields.io/badge/Java-17-orange)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue)](https://www.mysql.com/)
[![Playwright](https://img.shields.io/badge/Playwright-1.40-green)](https://playwright.dev/)

Full-stack warehouse management system REST API with comprehensive end-to-end test automation.

## 🔗 Related Repository

- **Frontend**: [warehouse-react-app](https://github.com/YOUR_USERNAME/warehouse-react-app) - React application

---

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Getting Started](#getting-started)
- [API Documentation](#api-documentation)
- [Testing](#testing)
- [Database Schema](#database-schema)

---

## 🎯 Overview

REST API for managing warehouse inventory, orders, stores, and users. Built with Spring Boot and MySQL with JWT authentication and role-based access control.

**Key Features:**
- User authentication with JWT tokens
- Role-based authorization (ADMIN, LOCAL_ADMIN)
- CRUD operations for materials, orders, stores, and users
- Entity relationship management (stores → users → materials → orders)
- Greek language support

---

## ✨ Features

### Backend API
- ✅ JWT Authentication & Authorization
- ✅ RESTful API endpoints
- ✅ MySQL database with JPA/Hibernate
- ✅ Entity relationships and referential integrity
- ✅ Input validation
- ✅ Error handling
- ✅ CORS configuration

### Test Automation
- ✅ 20+ end-to-end test cases
- ✅ Page Object Model design pattern
- ✅ Multiple test suites (Smoke, Regression, Full Integration)
- ✅ Smart waiting strategies
- ✅ Cross-browser support

---

## 🛠️ Tech Stack

### Backend
| Technology | Version | Purpose |
|-----------|---------|---------|
| **Java** | 17 | Programming language |
| **Spring Boot** | 3.x | Application framework |
| **Spring Security** | 6.x | Authentication & authorization |
| **MySQL** | 8.0 | Database |
| **JPA/Hibernate** | - | ORM |
| **JWT** | - | Token-based auth |
| **Maven** | 3.8+ | Build tool |

### Testing
| Technology | Version | Purpose |
|-----------|---------|---------|
| **Playwright** | 1.40.0 | Browser automation |
| **JUnit 5** | 5.10.0 | Testing framework |
| **Maven Surefire** | - | Test execution |

---

## 🚀 Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.8+
- MySQL 8.0
- Node.js (for frontend)

### Installation

1. **Clone the repository**
```bash
