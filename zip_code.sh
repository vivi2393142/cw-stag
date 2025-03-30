#!/bin/bash

# Define the output ZIP file name (in the current directory)
OUTPUT_ZIP="cw-stag.zip"

# Check if zip command is available
if ! command -v zip >/dev/null 2>&1; then
    echo "Error: 'zip' command is not installed. Please install it first."
    echo "On Debian/Ubuntu: sudo apt-get install zip"
    echo "On Red Hat/Fedora: sudo dnf install zip"
    echo "On macOS: brew install zip"
    exit 1
fi

# Remove the output ZIP file if it already exists
if [ -f "$OUTPUT_ZIP" ]; then
    rm -f "$OUTPUT_ZIP"
    echo "Removed existing $OUTPUT_ZIP"
fi

# Create a temporary exclude file for patterns
EXCLUDE_FILE=".zip_exclude.txt"
echo ".gitignore" > "$EXCLUDE_FILE"
echo ".git/" >> "$EXCLUDE_FILE"
echo "target/" >> "$EXCLUDE_FILE"
echo "*.sh" >> "$EXCLUDE_FILE"
echo ".idea/" >> "$EXCLUDE_FILE"

# Zip all files in the current directory, excluding specified patterns
zip -r "$OUTPUT_ZIP" . -x@"$EXCLUDE_FILE"

# Check if the zip command succeeded
if [ $? -eq 0 ]; then
    echo "Successfully created $OUTPUT_ZIP"
    echo "Contents of $OUTPUT_ZIP:"
    unzip -l "$OUTPUT_ZIP"
else
    echo "Error: Failed to create $OUTPUT_ZIP"
    exit 1
fi

# Clean up the temporary exclude file
rm -f "$EXCLUDE_FILE"