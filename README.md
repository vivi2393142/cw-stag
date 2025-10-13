# Game Configuration Parser

A Java project that simulates a **text-based adventure game engine** by parsing configuration files (`XML` and `DOT`) into in-memory data structures. This project demonstrates skills in **Java parsing**, **object-oriented design**, **testing**, and **build automation**.

### 🧩 Features
- **Action Parser** – Converts `actions.xml` into executable `GameAction` objects (e.g., *open*, *dig*).  
- **Entity Parser** – Reads `entities.dot` to build game entities such as locations, paths, and objects.  
- **Testing Suite** – Includes **JUnit** tests in `src/test/java` to validate parsing and game logic.

### 🧠 Highlights
- Designed modular parsers to transform XML and DOT input into structured objects.  
- Practiced error handling and validation to ensure robust parsing.  
- Automated verification using JUnit to confirm consistent parser behavior.

### ⚙️ Tech Stack
Java · Maven · JUnit · XML Parsing · DOT Graph Parsing

### 🚀 How to Run
```bash
# Build and test
./mvnw clean test

# Check source files
./check_files.sh src/main/java/edu/uob

# Zip and validate project
./zip_code.sh
```
