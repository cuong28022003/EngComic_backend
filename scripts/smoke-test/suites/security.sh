#!/usr/bin/env bash

run_security_suite() {
    echo ""
    log_info "=== SUITE: SECURITY & PERMISSIONS ==="

    # 1. Truy cập API yêu cầu xác thực nhưng không truyền token -> mong đợi 401 hoặc 403
    local response
    response=$(curl.exe -s -w "\n%{http_code}" -X GET "${BASE_URL}/api/card/dashboard" \
        -H "Content-Type: application/json")
    local code
    code=$(echo "$response" | tail -n1 | tr -d '\r')

    if [[ "$code" == "401" || "$code" == "403" ]]; then
        log_ok "Chặn truy cập trái phép khi không có token (HTTP $code)"
        PASS=$((PASS + 1))
    else
        log_fail "Không chặn truy cập trái phép, nhận HTTP $code"
        FAIL=$((FAIL + 1))
    fi
}
