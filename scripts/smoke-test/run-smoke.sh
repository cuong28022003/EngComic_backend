#!/usr/bin/env bash
set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Global vars
ACCESS_TOKEN=""
CURRENT_USER_ID=""

# Load configuration & helper libraries
source "$SCRIPT_DIR/config.env"
source "$SCRIPT_DIR/lib/common.sh"
source "$SCRIPT_DIR/lib/preflight.sh"

# Load test suites
source "$SCRIPT_DIR/suites/security.sh"
source "$SCRIPT_DIR/suites/feature-001.sh"

echo "================================================================="
echo "🚀 BẮT ĐẦU CHẠY AUTOMATED SMOKE TEST: EngComic_backend"
echo "Target: $BASE_URL"
echo "================================================================="

# 1. Preflight
check_server_ready
obtain_auth_token

# 2. Suites Execution
run_security_suite
run_feature_001_suite

# 3. Summary Report
echo ""
echo "================================================================="
if [[ $FAIL -eq 0 ]]; then
    echo -e "${GREEN}🎉 SMOKE TEST HOÀN TẤT THÀNH CÔNG: $PASS PASSED | 0 FAILED${NC}"
    echo "================================================================="
    exit 0
else
    echo -e "${RED}❌ SMOKE TEST THẤT BẠI: $PASS PASSED | $FAIL FAILED${NC}"
    echo "================================================================="
    exit 1
fi
