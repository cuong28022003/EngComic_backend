#!/usr/bin/env bash

check_server_ready() {
    log_info "1. Kiểm tra trạng thái máy chủ..."
    local retry_count=0
    local max_retries=10
    local is_ready=0

    while [[ $retry_count -lt $max_retries ]]; do
        local code
        code=$(curl.exe -s -o /dev/null -w "%{http_code}" "${BASE_URL}/swagger-ui.html" || true)
        code=$(echo "$code" | tr -d '\r')
        if [[ "$code" == "200" || "$code" == "302" || "$code" == "404" ]]; then
            is_ready=1
            break
        fi
        log_info "Đang chờ server khởi động... (thử lại $((retry_count + 1))/$max_retries)"
        sleep 2
        retry_count=$((retry_count + 1))
    done

    if [[ $is_ready -eq 1 ]]; then
        log_ok "Máy chủ đã sẵn sàng tại ${BASE_URL}"
    else
        log_fail "Máy chủ chưa sẵn sàng sau $max_retries lần thử"
        exit 1
    fi
}

obtain_auth_token() {
    log_info "2. Đăng nhập hoặc tạo người dùng thử nghiệm để lấy JWT Access Token..."
    
    local timestamp
    timestamp=$(date +%s)
    local test_user="user${timestamp}"
    local test_email="u${timestamp}@engcomic.com"

    log_info "Thực hiện đăng ký tài khoản mới: $test_user..."
    local register_payload="{\"username\":\"$test_user\",\"password\":\"$TEST_PASSWORD\",\"email\":\"$test_email\"}"
    
    local reg_response
    reg_response=$(printf "%s" "$register_payload" | curl.exe -s -w "\n%{http_code}" -X POST "${BASE_URL}/api/auth/register" \
        -H "Content-Type: application/json" \
        -d @-)

    local reg_code
    reg_code=$(echo "$reg_response" | tail -n1 | tr -d '\r')
    local reg_body
    reg_body=$(echo "$reg_response" | sed '$d')

    local login_user="$test_user"
    if [[ "$reg_code" != "200" ]]; then
        log_info "Đăng ký trả về HTTP $reg_code ($reg_body), thử dùng tài khoản mặc định..."
        login_user="$TEST_USERNAME"
    fi

    # Đăng nhập lấy token
    local login_payload="{\"username\":\"$login_user\",\"password\":\"$TEST_PASSWORD\"}"
    local response
    response=$(printf "%s" "$login_payload" | curl.exe -s -w "\n%{http_code}" -X POST "${BASE_URL}/api/auth/login" \
        -H "Content-Type: application/json" \
        -d @-)

    local code
    code=$(echo "$response" | tail -n1 | tr -d '\r')
    local body
    body=$(echo "$response" | sed '$d')

    if [[ "$code" == "200" ]]; then
        ACCESS_TOKEN=$(echo "$body" | grep -o '"accessToken":"[^"]*' | cut -d'"' -f4)
        CURRENT_USER_ID=$(echo "$body" | grep -o '"id":"[^"]*' | head -n1 | cut -d'"' -f4)
        if [[ -n "$ACCESS_TOKEN" ]]; then
            log_ok "Đăng nhập thành công với user [$login_user], User ID: [$CURRENT_USER_ID]"
            return 0
        fi
    fi

    log_fail "Đăng nhập thất bại (HTTP $code). Response: $body"
}
