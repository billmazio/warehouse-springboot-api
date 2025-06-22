# Clothing Management System - Backend

## Project Description

This is a Spring Boot backend service built on version 3.4.0 for a clothing inventory management system. It provides REST APIs for tracking, managing, and distributing clothing items across multiple store locations with role-based access control.

## Technologies Used:

- Spring Boot 3.4.0
- MySQL Database
- JPA (Java Persistence API)
- Hibernate ORM
- Spring Security
- JWT Authentication
- Lombok
- Java 17

## Key Features:

- Multi-store Management API
- Role-based Access Control (SUPER_ADMIN, LOCAL_ADMIN)
- Clothing Inventory Management
- Size Management
- JWT-based Authentication & Authorization
- RESTful API Design

## Technical Implementation:

- Spring Boot 3.4.0 provides the foundation for building the REST API service.
- MySQL database stores all inventory and user data.
- JPA with Hibernate handles object-relational mapping for database operations.
- Lombok reduces boilerplate code in entity classes and DTOs.
- Spring Security with JWT manages authentication and authorization.
- RESTful API endpoints for all CRUD operations on materials, stores, and sizes.

## Database Mysql

## Main API Endpoints

The service provides RESTful endpoints for:
- Authentication
- Store management
- Material/inventory management
- Size reference data

## Getting Started

1. Ensure you have Java 17 and Maven installed
2. Configure MySQL database connection in `application.properties`
3. Run with `mvn spring-boot:run`

---

*Backend documentation for GitHub repository*
