#!/bin/bash
INPUT_DIR="test25/inputs"
OUTPUT_DIR="test25/outputs"

ACCOUNTS_FILE="currentaccounts.txt"
RENTAL_FILE="rentalunits.txt"

mkdir -p "$OUTPUT_DIR"
javac *.java

for inputfile in "$INPUT_DIR"/*.in.txt
do
  filename=$(basename "$inputfile")
  name="${filename%.in.txt}"

  echo "Running test: $filename"

  java Main "$ACCOUNTS_FILE" "$RENTAL_FILE" "$OUTPUT_DIR/$name.atf" \
    < "$inputfile" \
    > "$OUTPUT_DIR/$name.out.txt"
done

echo "All tests completed."