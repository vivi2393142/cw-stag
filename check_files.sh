#!/bin/bash

# Check if a directory path is provided
if [ $# -ne 1 ]; then
    echo "Usage: $0 <directory_path>"
    echo "Example: $0 src/main/java/edu/uob"
    exit 1
fi

# Assign the provided directory path to a variable
DIR="$1"

# Check if the directory exists
if [ ! -d "$DIR" ]; then
    echo "Error: Directory '$DIR' does not exist."
    exit 1
fi

# Find all .java files recursively in the directory and its subdirectories
# Using 'find' to handle multi-level directories
JAVA_FILES=$(find "$DIR" -type f -name "*.java")

# Check if any .java files were found
if [ -z "$JAVA_FILES" ]; then
    echo "No .java files found in '$DIR' or its subdirectories."
    exit 0
fi

# Counter for tracking processed files
TOTAL_FILES=0
FAILED_FILES=0

# Iterate over each .java file and run the Maven command
while IFS= read -r file; do
    echo "Checking: $file"
    ./mvnw exec:java@strange -Dexec.args="$file"
    # Check the exit status of the last command
    if [ $? -ne 0 ]; then
        echo "Error: Failed to check $file"
        FAILED_FILES=$((FAILED_FILES + 1))
    fi
    TOTAL_FILES=$((TOTAL_FILES + 1))
done <<< "$JAVA_FILES"

# Summary
echo "--------------------------------"
echo "Total files checked: $TOTAL_FILES"
if [ $FAILED_FILES -eq 0 ]; then
    echo "All files checked successfully."
else
    echo "Number of failed checks: $FAILED_FILES"
    exit 1
fi