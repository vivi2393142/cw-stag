# Game Configuration Parser

A Java-based mini engine that parses configuration files (`XML` and `DOT`) into in-memory game objects for a text-based adventure simulation. This project demonstrates practical experience in **Java**, **parsing**, **testing**, and **build automation**.

### 🧩 Features
- **Action Parser** – Converts `actions.xml` into executable `GameAction` objects (e.g., *open*, *dig*).  
- **Entity Parser** – Reads `entities.dot` to build game entities such as locations, paths, and objects.  
- **JUnit Testing** – Comprehensive tests in `src/test/java` to verify parsing logic and data mapping.

### ⚙️ Tech Stack
Java · Maven · JUnit · Shell Scripting

### 🚀 How to Run
```bash
  # Build and test
  ./mvnw clean test
  
  # Check source files
  ./check_files.sh src/main/java/edu/uob
  
  # Zip and validate project
  ./zip_code.sh
```
