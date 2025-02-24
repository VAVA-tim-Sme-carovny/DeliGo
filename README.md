# Financial Manager Application

## Overview

This application is designed as part of a school project in the Java development course. It aims to provide an efficient and intuitive interface for managing financial data. The project is built using Java SE (Standard Edition) and incorporates various features such as logging, localization, JDBC integration, and regular expressions.

## Key Features
* Financial Management: Manage and track financial data, including transactions, budgets, and reports.
* Data Logging: Implement robust logging for both business logic and error tracking.
* Localization: Support for multiple languages (English and Slovak).
* Database Integration: Uses JDBC to connect to a database (MySQL/PostgreSQL/SQLite).
* Graphical User Interface: Built using Swing/JavaFX for an interactive, user-friendly experience.
* Regular Expressions: Applied for validating user inputs and filtering data.

## Technologies Used
* Java SE (Java 17): The core platform for application development.
* Swing/JavaFX: For creating the GUI.
* JDBC: For database connection and data handling.
* MySQL/PostgreSQL/SQLite: Relational databases for storing data.
* ArchiMate: For enterprise architecture modeling.
* JUnit: For unit testing.
* Log4j: For logging application events and errors.

## Project Setup

### Prerequisites
* Java 17 or higher
* MySQL/PostgreSQL/SQLite database (depending on your choice)
* Maven or Gradle for dependency management

### Clone the Repository

git clone https://github.com/YourUsername/financial-manager-app.git
cd financial-manager-app

### Running the Application
1. Set up your database and configure the connection in the `database.properties` file.
2. Compile and run the application:

mvn clean install
mvn exec:java

### Branching Conventions
When creating new branches, follow these naming patterns:
* `feature/<feature-name>` (e.g., feature/user-auth)
* `bugfix/<bug-description>` (e.g., bugfix/fix-login-error)
* `hotfix/<urgent-fix>` (e.g., hotfix/urgent-patch)

## Testing

Unit tests are located in the `src/test/java` directory. You can run the tests using Maven:

mvn test

## Team Members
* Project Manager: Martin Stavrovsky
* SW Testers: Kristián Skočík, Samo Maliarik
* IT Architect: Richard Nemeth
* System Administrator: Jakub Kelemen
* Database Specialist: Damian Parigal
* UX/UI Designer: Michal Kacinec
* Programmers: Benjamín Ptáček, Marko Govda, Radoslav Muntág

## Documentation
* UML diagrams: Class diagrams and sequence diagrams for the system.
* ArchiMate diagrams: Business, application, and technological layers of the system.
* Video Presentation: A 15-20 minute video demonstrating the application, architecture, and features.