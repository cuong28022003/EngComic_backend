#!/usr/bin/env bash

# Mã màu console
GREEN='\033[0;32m'
RED='\033[0;31m'
CYAN='\033[0;36m'
YELLOW='\033[1;33m'
NC='\033[0m'

PASS=0
FAIL=0
LAST_HTTP_CODE=""
LAST_BODY=""

log_info() { echo -e "${CYAN}[INFO]${NC} $*"; }
log_ok()   { echo -e "${GREEN}[PASS]${NC} $*"; }
log_fail() { echo -e "${RED}[FAIL]${NC} $*"; }

# Hàm gọi API và validate HTTP Code
# Cách dùng: http_step "Tên bước" METHOD PATH [JSON_BODY] [EXPECTED_HTTP_CODE] [TOKEN]
http_step() {
    local step_name="$1"
    local method="$2"
    local path="$3"
    local body="$4"
    local expected_code="${5:-200}"
    local token="$6"

    local auth_header=()
    if [[ -n "$token" ]]; then
        auth_header=(-H "Authorization: Bearer $token")
    fi

    local response
    if [[ -n "$body" ]]; then
        response=$(printf "%s" "$body" | curl.exe -s -w "\n%{http_code}" -X "$method" \
            "${BASE_URL}${path}" \
            -H "Content-Type: application/json" \
            "${auth_header[@]}" \
            -d @-)
    else
        response=$(curl.exe -s -w "\n%{http_code}" -X "$method" \
            "${BASE_URL}${path}" \
            -H "Content-Type: application/json" \
            "${auth_header[@]}")
    fi

    LAST_HTTP_CODE=$(echo "$response" | tail -n1 | tr -d '\r')
    LAST_BODY=$(echo "$response" | sed '$d')

    if [[ "$LAST_HTTP_CODE" == "$expected_code" ]]; then
        log_ok "$step_name (HTTP $LAST_HTTP_CODE)"
        PASS=$((PASS + 1))
    else
        log_fail "$step_name - Kì vọng HTTP $expected_code nhưng nhận về $LAST_HTTP_CODE"
        echo -e "${YELLOW}Response Body:${NC} $LAST_BODY"
        FAIL=$((FAIL + 1))
    fi
}
