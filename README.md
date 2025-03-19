# 🏦 DeliGo - Tracking system Application  

## 📌 Overview  

**DeliGo** is a Tracking system  designed to help staff track, order and manage their created orders. It is built with a **Java com.deligo.Backend (BE)**, a **Java FX (FE)**, and a **REST API** for seamless communication between the two. The project follows a **modular architecture**, allowing for independent development of components.  

**For following project you will have to use explicitly Open JDK 21 or Oracle JDK 21  - other versions like Amazon Coretto wont work !!! Set all settings for you running Java version**

---

## Requiremetns
   
- Java openJDK 21 (not correto or any other version)
  
 - Setup java to enviroment variable JAVA_HOME

- Java FX 21 needs to be downloaded and it's path needs to be placed in jvm.conf

- Make sure your Idea Intelij uses 21 version even in run configurations

---

## 🔀 Branch Naming Convention  

When creating new branches, follow these naming patterns:  

- `feature/<feature-name>` → For new features (e.g., `feature/user-auth`)  
- `bugfix/<bug-description>` → For bug fixes (e.g., `bugfix/fix-login-error`)  
- `hotfix/<urgent-fix>` → For urgent patches (e.g., `hotfix/urgent-patch`)  

---

## Build profiles
There are 3 main profiles:
### - Development Build - Starts frontend and backend
### - BackEnd Build - starts backend without frontend
### - FrontEnd Build - starts frontend without backend

---

## 📦 Dependency & Environment Versioning  

DeliGo is built using **Maven** to manage dependencies and version control.

# 🛠 Java version setup
To check current version use:
- java --version
OR
- /usr/libexec/java_home -V

If you are using OpenJKD23 or other use:
- brew uninstall openjdk
- brew install openjdk@21

To set correct version as default
- export JAVA_HOME=$(/usr/libexec/java_home -v 21)
- echo 'export JAVA_HOME=$(/usr/libexec/java_home -v 21)' >> ~/.zshrc
- source ~/.zshrc


### 🔹 Dependency Lock
All dependencies are locked using Maven’s dependency lock mechanism, generating a `dependency-lock.json` file that is committed to GitHub.  

### 🔹 Maven Wrapper
DeliGo is executed via Maven Wrapper (`mvnw`) to ensure consistent builds across environments.

# Configure jvm.config
Open jvm,config and correct path to your JavaFX (in system):

```sh
--module-path
"/Users/user/Desktop/skola/FIIT/4.semester/vava/DeliGo/lib"
--add-modules
javafx.controls,javafx.fxml
```

# GitHub tutorial
If you are trying to pull a branch, first push your current changes so you wont lose any progress and then pull the new one.

### PLEASE DO NOT PUSH ANYTHING TO MAIN BRANCH & ALWAYS MAKE SURE YOU ARE IN RIGHT BRANCH 
check branch
```sh
git status
```
pull the lastest changes
```sh
git fetch
git pull
```
switch to main branch
```sh
git checkout main
```
merge latest changes
```sh
git merge origin/main
```
add and push changes
```sh
git add .
git commit -m "Updated configurations and removed unnecessary files"
git push origin feature/XXX
```


# Problems with wrapper
check mvn skript issues
```sh
chmod -x mvnw
chmod +x .mvn/wrapper/maven-wrapper.jar
```
test check
```sh
./mvnw -v
```

## 🔄 GitHub & Git Configuration commands
GitHub now does not support password authentication for git fetch and git push. You must use SSH:
```sh
ssh-keygen -t rsa -b 4096 -C "your_email@example.com"
```
Save it in : ~/.ssh/id_rsa

Now add SSH key to your GitHub (https://docs.github.com/en/authentication/connecting-to-github-with-ssh/generating-a-new-ssh-key-and-adding-it-to-the-ssh-agent):
```sh
pbcopy < ~/.ssh/id_rsa.pub
```

-Go to GitHub → Settings → SSH & GPG keys → New SSH Key
-Paste the copied key and save it.

Now check connection:
-ssh -T git@github.com

Change remote to SHH:
-git remote set-url origin git@github.com:VAVA-tim-Sme-carovny/DeliGo.git


### 🛠️ Deployment Guide

1️⃣ Clone the Repository

```sh
git clone https://github.com/yourusername/DeliGo.git
cd DeliGo
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

