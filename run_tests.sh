#!/bin/bash

# Where test inputs are stored
INPUT_DIR="test/inputs"

# Where we want outputs to go
OUTPUT_DIR="test/outputs"

# Required program input files
ACCOUNTS_FILE="currentaccounts.txt"
RENTAL_FILE="rentalunits.txt"

# Make sure output folder exists
mkdir -p "$OUTPUT_DIR"

# Compile Java files
javac *.java

# Loop through every file inside test/inputs
for inputfile in "$INPUT_DIR"/*.in.txt
do
    # Extract filename only (remove path)
    filename=$(basename "$inputfile")

    # Remove file extension
    name="${filename%.in.txt}"

    echo "Running test: $filename"

    # Run your program
    java Main "$ACCOUNTS_FILE" "$RENTAL_FILE" "$OUTPUT_DIR/$name.atf" \
        < "$inputfile" \
        > "$OUTPUT_DIR/$name.out.txt"

done

echo "All tests completed."