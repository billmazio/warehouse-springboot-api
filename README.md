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
