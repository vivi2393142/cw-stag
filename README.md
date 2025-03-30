# Game Configuration Parser

## Overview

This is a Java-based practice project that simulates a text-based adventure game engine by parsing configuration files (
XML and DOT) into in-memory data structures. It’s not a production-ready tool but a demonstration of my skills in Java,
parsing, testing, and scripting.

## Features

- **Actions Parser**: Parses actions.xml into GameAction objects (e.g., "open", "dig").
- **Entities Parser**: Parses entities.dot into game entities (locations, paths, objects).
- **Tests**: JUnit tests in src/test/java to validate parsing.

## Tech Stack

- Java
- Maven
- JUnit
- Shell Scripting

## How to Run

1. **Build & Test**: ./mvnw clean test
2. **Check Code**: ./check_files.sh src/main/java/edu/uob
3. **Zip & Validate**: ./zip_code.sh
