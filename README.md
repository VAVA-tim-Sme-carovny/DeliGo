# 🏦 DeliGo - Tracking system Application  

## 📌 Overview  

**DeliGo** is a Tracking system  designed to help staff track, order and manage their created orders. It is built with a **Java com.deligo.Backend (BE)**, a **Java FX springboot com.deligo.Frontend (FE)**, and a **REST API** for seamless communication between the two. The project follows a **modular architecture**, allowing for independent development of components.  

---

## 🔀 Branch Naming Convention  

When creating new branches, follow these naming patterns:  

- `feature/<feature-name>` → For new features (e.g., `feature/user-auth`)  
- `bugfix/<bug-description>` → For bug fixes (e.g., `bugfix/fix-login-error`)  
- `hotfix/<urgent-fix>` → For urgent patches (e.g., `hotfix/urgent-patch`)  

---

## 📦 Dependency & Environment Versioning  

DeliGo is built using **Maven** to manage dependencies and version control.

### 🔹 Maven Profiles
The `pom.xml` file defines four **build profiles**:  

1. **Development (Single Component)**  
   - Runs only BE or FE separately.  
   - Uses `maven-failsafe-plugin`, `maven-checkstyle-plugin`, and `findbugs-maven-plugin`.  

2. **Development (Full Application)**  
   - Runs both BE and FE simultaneously.  
   - Uses `maven-surefire-plugin` for testing.  

3. **BuildDev**  
   - Used for **internal development builds**.  
   - Includes `maven-compiler-plugin` and `jacoco-maven-plugin`.  

4. **BuildProd**  
   - Used for **production-ready builds**.  
   - Includes `maven-javadoc-plugin` and `maven-release-plugin`.  

### 🔹 Dependency Lock
All dependencies are locked using Maven’s dependency lock mechanism, generating a `dependency-lock.json` file that is committed to GitHub.  

### 🔹 Maven Wrapper
DeliGo is executed via Maven Wrapper (`mvnw`) to ensure consistent builds across environments. Various profiles can be selected using `.bat` and `.sh` scripts.  

---

## ✅ Testing  

DeliGo implements **unit and integration testing** using **JUnit** and **Mockito**.  

- **Unit tests** are located in `src/test/java/`  
- **Test endpoints**:
  - `POST /health`  
  - `GET /health`  

To run tests:  

```sh
mvn test
```

---

## 📝 com.deligo.Logging

DeliGo implements structured logging using Log4J.

### Log Categories
- ERROR - Critical issues
- WARNING – Potential issues
- SUCCESS – Successful operations

### com.deligo.Logging Priorities
- Low
- Mid
- High

### com.deligo.Logging Sources
- RestApi
- Persistence
- BE
- FE
- Maven

---

## 🔒 Security Measures

DeliGo follows security best practices, including:

- User authentication via JWT tokens
- Data encryption for sensitive transactions
- OWASP ESAPI (planned) for input validation
- Role-based access control for different user permissions

---

## 🛠️ Deployment Guide

### 1️⃣ Clone the Repository

```sh
git clone https://github.com/yourusername/DeliGo.git
cd DeliGo
```

### 2️⃣ Setup the Backend
```sh
cd src/main/java/com/deligo
mvn clean install
mvn spring-boot:run
```

### 3️⃣ Setup the Frontend
```sh
cd com.deligo.Frontend
npm install
npm start
```

---

## 👥 Team Members

| Role   | Name    |
|:-------------:|:---------------:|
| Project Manager      | Martin Stavrovsky                             |
| SW Testers           | Kristián Skočík, Samo Maliarik                |
| IT Architect         | Richard Nemeth                                |
| System Administrator | Jakub Kelemen                                 |
| Database Specialist  | Damian Parigal                                |
| UX/UI Designer       | Michal Kacinec                                |
| Programmers          | Benjamín Ptáček, Marko Govda, Radoslav Muntág |

---

## 📚 Documentation

📌 UML Diagrams – Class and sequence diagrams for the system <br>
📌 ArchiMate Diagrams – Business, application, and technology layers<br>
📌 Video Presentation – 15-20 min overview covering system architecture<br>

---

## 💡 Contributing

1.	Fork the repository
2.	Create a new feature branch (feature/your-feature)
3.	Commit your changes
4.	Push to GitHub and create a Pull Request (PR)

