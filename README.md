Clothing Management System
Project Description
This is a Spring Boot project built on version 3.4.0. The project incorporates various technologies for building a clothing inventory management system. The application allows for tracking, management, and distribution of clothing items across multiple store locations with role-based access.
Technologies Used:

Spring Boot 3.4.0
MySQL Database
JPA (Java Persistence API)
Hibernate
Spring Security
JWT Authentication
Lombok
React.js (Frontend)
Custom CSS

Key Features:

Multi-store Management: Track inventory across multiple store locations
Role-based Access Control: Different permissions for Super Admins and Local Admins
Material/Clothing Management: Add, edit, and remove clothing items with details
Size Management: Standardized size options for all clothing items
Quantity Tracking: Monitor stock levels across all locations
Responsive UI: Modern, mobile-friendly interface

Technical Implementation:

Spring Boot 3.4.0 provides the foundation for building the web application with ease and efficiency.
MySQL database is used to store and manage data.
JPA is utilized for database interaction with object-relational mapping.
Hibernate is the underlying ORM tool that handles the mapping between Java objects and the database.
Lombok is used to reduce boilerplate code in the entity classes, such as getters, setters, and constructors.
Thymeleaf template engine is used for server-side templating, rendering dynamic HTML views.
Spring Security with JWT handles authentication and authorization.
React.js is employed for the frontend user interface.
Custom CSS is used for styling the user interface, providing a visually appealing and responsive design.

Database Schema
Main Tables:

materials - Clothing items inventory
sizes - Standardized clothing sizes
stores - Store locations
users - System users with roles

User Roles

SUPER_ADMIN - Can manage all stores and materials
LOCAL_ADMIN - Can only manage their assigned store

Screenshots:
(Include screenshots of key interfaces here)
