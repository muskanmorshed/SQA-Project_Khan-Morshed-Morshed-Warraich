#!/bin/bash
set -e

INPUT_DIR="test25/inputs"
OUTPUT_DIR="test25/outputs"

RENTAL_FILE="rentalunits.txt"

mkdir -p "$OUTPUT_DIR"
javac *.java

for inputfile in "$INPUT_DIR"/*.in.txt
do
  filename=$(basename "$inputfile")
  name="${filename%.in.txt}"

  accounts_to_use="accounts_base.txt"
  case "$name" in
    TC10a_deposit_zero_amount|TC10b_deposit_negative_amount|TC10c_deposit_non_numeric|TC12_withdraw_valid_standard|TC14_withdraw_insufficient_funds|TC15_withdraw_max_500_standard_limit|TC16c_withdraw_non_numeric)
      accounts_to_use="accounts_with_01256.txt"
      ;;
  esac

  echo "Running test: $filename"
  echo "  using accounts: $accounts_to_use"

  java Main "$accounts_to_use" "$RENTAL_FILE" "$OUTPUT_DIR/$name.atf" \
    < "$inputfile" \
    > "$OUTPUT_DIR/$name.out.txt"
done

echo "All tests completed."