#!/usr/bin/env bash
set -euo pipefail

EXPECTED_DIR="test25/expected"
OUTPUT_DIR="test25/outputs"

echo "=== Validating tests (terminal output) ==="
fail=0

for expected_out in "$EXPECTED_DIR"/*.out.txt; do
  base="$(basename "$expected_out")"
  actual_out="$OUTPUT_DIR/$base"

  if [[ ! -f "$actual_out" ]]; then
    echo "[FAIL] Missing actual: $actual_out"
    fail=$((fail+1))
    continue
  fi

  tmp1="$(mktemp)"
  tmp2="$(mktemp)"
  cp "$expected_out" "$tmp1"
  cp "$actual_out" "$tmp2"

  # ensure both end with newline
  [[ -n "$(tail -c 1 "$tmp1")" ]] && echo >> "$tmp1"
  [[ -n "$(tail -c 1 "$tmp2")" ]] && echo >> "$tmp2"

  # ignore whitespace differences and blank-line-only differences
  if diff -u -b -B "$tmp1" "$tmp2" >/dev/null; then
    echo "[PASS] $base"
  else
    echo "[FAIL] $base"
    fail=$((fail+1))
  fi

  rm -f "$tmp1" "$tmp2"
done

echo "=== Total failures: $fail ==="
[[ "$fail" -eq 0 ]]
