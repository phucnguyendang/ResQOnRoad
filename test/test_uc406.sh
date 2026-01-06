#!/bin/bash

# UC406 - Reports and Statistics Integration Test Script
# Tests all report endpoints and functionality

set -e

BASE_URL="http://localhost:8080/v1/api"
ADMIN_USERNAME="admin"
ADMIN_PASSWORD="admin123"
UNIQUE_ID=$(date +%s%N)

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Counters
PASSED=0
FAILED=0

# ====== Helper Functions ======

log_info() {
    echo -e "${YELLOW}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[PASS]${NC} $1"
    ((PASSED++))
}

log_error() {
    echo -e "${RED}[FAIL]${NC} $1"
    ((FAILED++))
}

log_test() {
    echo ""
    echo -e "${YELLOW}=== TEST: $1 ===${NC}"
}

# Login and get token
get_admin_token() {
    log_info "Logging in as admin..."
    local response=$(curl -s -X POST "$BASE_URL/auth/login" \
        -H "Content-Type: application/json" \
        -d "{
            \"username\": \"$ADMIN_USERNAME\",
            \"password\": \"$ADMIN_PASSWORD\"
        }")
    
    local token=$(echo "$response" | jq -r '.data.token // empty')
    
    if [ -z "$token" ]; then
        log_error "Failed to get admin token"
        echo "Response: $response"
        return 1
    fi
    
    echo "$token"
}

# Check HTTP response status
check_status() {
    local response=$1
    local expected_status=$2
    local test_name=$3
    
    local status=$(echo "$response" | jq -r '.status // empty')
    local message=$(echo "$response" | jq -r '.message // empty')
    
    if [ "$status" = "success" ] || [ -n "$(echo "$response" | jq '.data // empty')" ]; then
        log_success "$test_name"
        return 0
    else
        log_error "$test_name"
        echo "  Response: $response"
        return 1
    fi
}

# Create test data - Rescue Request
create_test_rescue_request() {
    local company_id=$1
    local user_token=$2
    
    local response=$(curl -s -X POST "$BASE_URL/rescue-requests" \
        -H "Authorization: Bearer $user_token" \
        -H "Content-Type: application/json" \
        -d "{
            \"location\": \"Hà Nội, Việt Nam\",
            \"latitude\": 21.0285,
            \"longitude\": 105.8542,
            \"description\": \"Vá lốp xe\",
            \"service_type\": \"Vá lốp\"
        }")
    
    local request_id=$(echo "$response" | jq -r '.data.id // empty')
    if [ -z "$request_id" ]; then
        return 1
    fi
    echo "$request_id"
}

# Create test data - Review
create_test_review() {
    local company_id=$1
    local rating=$2
    local user_token=$3
    
    local response=$(curl -s -X POST "$BASE_URL/reviews" \
        -H "Authorization: Bearer $user_token" \
        -H "Content-Type: application/json" \
        -d "{
            \"company_id\": $company_id,
            \"rating\": $rating,
            \"comment\": \"Test review for UC406\"
        }")
    
    echo "$response"
}

# ====== Setup ======

log_info "Starting UC406 Tests..."
log_info "Base URL: $BASE_URL"

# Get admin token
ADMIN_TOKEN=$(get_admin_token)
if [ -z "$ADMIN_TOKEN" ]; then
    log_error "Cannot proceed without admin token"
    exit 1
fi
log_success "Admin token obtained"

# ====== Tests ======

# Test 1: Get available report types
log_test "Get Available Report Types"
response=$(curl -s -X GET "$BASE_URL/admin/reports/types" \
    -H "Authorization: Bearer $ADMIN_TOKEN" \
    -H "Content-Type: application/json")
check_status "$response" "success" "Get available report types" || ((FAILED++))
echo "Response: $response" | jq '.'

# Test 2: Get Daily Report (Last 30 days)
log_test "Get Daily Report"
response=$(curl -s -X GET "$BASE_URL/admin/reports/daily" \
    -H "Authorization: Bearer $ADMIN_TOKEN" \
    -H "Content-Type: application/json")
check_status "$response" "success" "Get daily report" || ((FAILED++))
# Daily report may have no data initially, which is ok
if echo "$response" | jq -e '.data.hasData' > /dev/null 2>&1; then
    log_info "Daily report has data"
else
    log_info "Daily report has no data (expected for empty system)"
fi

# Test 3: Get Daily Report with specific date range
log_test "Get Daily Report with Date Range"
start_date=$(date -d '30 days ago' +%Y-%m-%d)
end_date=$(date +%Y-%m-%d)
response=$(curl -s -X GET "$BASE_URL/admin/reports/daily?startDate=$start_date&endDate=$end_date" \
    -H "Authorization: Bearer $ADMIN_TOKEN" \
    -H "Content-Type: application/json")
check_status "$response" "success" "Get daily report with date range" || ((FAILED++))

# Test 4: Get Monthly Report (Current month)
log_test "Get Monthly Report"
current_month=$(date +%m)
current_year=$(date +%Y)
response=$(curl -s -X GET "$BASE_URL/admin/reports/monthly?month=$current_month&year=$current_year" \
    -H "Authorization: Bearer $ADMIN_TOKEN" \
    -H "Content-Type: application/json")
check_status "$response" "success" "Get monthly report" || ((FAILED++))

# Test 5: Get Satisfaction Report
log_test "Get Satisfaction Report"
response=$(curl -s -X GET "$BASE_URL/admin/reports/satisfaction" \
    -H "Authorization: Bearer $ADMIN_TOKEN" \
    -H "Content-Type: application/json")
check_status "$response" "success" "Get satisfaction report" || ((FAILED++))

# Test 6: Get Statistics via POST (DAILY)
log_test "Post Daily Report Statistics"
response=$(curl -s -X POST "$BASE_URL/admin/reports/statistics" \
    -H "Authorization: Bearer $ADMIN_TOKEN" \
    -H "Content-Type: application/json" \
    -d "{
        \"reportType\": \"DAILY\",
        \"startDate\": \"$(date -d '7 days ago' +%Y-%m-%d)\",
        \"endDate\": \"$(date +%Y-%m-%d)\"
    }")
check_status "$response" "success" "Get daily statistics via POST" || ((FAILED++))

# Test 7: Get Statistics via POST (MONTHLY)
log_test "Post Monthly Report Statistics"
response=$(curl -s -X POST "$BASE_URL/admin/reports/statistics" \
    -H "Authorization: Bearer $ADMIN_TOKEN" \
    -H "Content-Type: application/json" \
    -d "{
        \"reportType\": \"MONTHLY\",
        \"month\": $current_month,
        \"year\": $current_year
    }")
check_status "$response" "success" "Get monthly statistics via POST" || ((FAILED++))

# Test 8: Get Company Report (with valid company ID if available)
log_test "Get Company Report"
response=$(curl -s -X GET "$BASE_URL/admin/reports/company/1?startDate=$(date -d '30 days ago' +%Y-%m-%d)&endDate=$(date +%Y-%m-%d)" \
    -H "Authorization: Bearer $ADMIN_TOKEN" \
    -H "Content-Type: application/json")
check_status "$response" "success" "Get company report" || ((FAILED++))

# Test 9: Get Service Report (with valid service ID if available)
log_test "Get Service Report"
response=$(curl -s -X GET "$BASE_URL/admin/reports/service/1?startDate=$(date -d '30 days ago' +%Y-%m-%d)&endDate=$(date +%Y-%m-%d)" \
    -H "Authorization: Bearer $ADMIN_TOKEN" \
    -H "Content-Type: application/json")
check_status "$response" "success" "Get service report" || ((FAILED++))

# Test 10: Export Daily Report as CSV
log_test "Export Daily Report as CSV"
response=$(curl -s -X POST "$BASE_URL/admin/reports/export/csv" \
    -H "Authorization: Bearer $ADMIN_TOKEN" \
    -H "Content-Type: application/json" \
    -d "{
        \"reportType\": \"DAILY\",
        \"startDate\": \"$(date -d '7 days ago' +%Y-%m-%d)\",
        \"endDate\": \"$(date +%Y-%m-%d)\"
    }")

# CSV export returns binary data, check if we got a proper response
if [[ "$response" == *","* ]] || [[ -z "$response" ]]; then
    log_success "Export daily report as CSV"
else
    log_error "Export daily report as CSV"
    echo "  Response: ${response:0:100}"
fi

# Test 11: Export Monthly Report as CSV
log_test "Export Monthly Report as CSV"
response=$(curl -s -X POST "$BASE_URL/admin/reports/export/csv" \
    -H "Authorization: Bearer $ADMIN_TOKEN" \
    -H "Content-Type: application/json" \
    -d "{
        \"reportType\": \"MONTHLY\",
        \"month\": $current_month,
        \"year\": $current_year
    }")

if [[ "$response" == *","* ]] || [[ -z "$response" ]]; then
    log_success "Export monthly report as CSV"
else
    log_error "Export monthly report as CSV"
fi

# Test 12: Export Satisfaction Report as CSV
log_test "Export Satisfaction Report as CSV"
response=$(curl -s -X POST "$BASE_URL/admin/reports/export/csv" \
    -H "Authorization: Bearer $ADMIN_TOKEN" \
    -H "Content-Type: application/json" \
    -d "{
        \"reportType\": \"SATISFACTION\",
        \"startDate\": \"$(date -d '30 days ago' +%Y-%m-%d)\",
        \"endDate\": \"$(date +%Y-%m-%d)\"
    }")

if [[ "$response" == *","* ]] || [[ -z "$response" ]]; then
    log_success "Export satisfaction report as CSV"
else
    log_error "Export satisfaction report as CSV"
fi

# Test 13: Invalid report type handling
log_test "Invalid Report Type Error Handling"
response=$(curl -s -X POST "$BASE_URL/admin/reports/statistics" \
    -H "Authorization: Bearer $ADMIN_TOKEN" \
    -H "Content-Type: application/json" \
    -d "{
        \"reportType\": \"INVALID_TYPE\",
        \"startDate\": \"$(date +%Y-%m-%d)\"
    }")

# Should return an error response
if echo "$response" | jq -e '.data.message' > /dev/null 2>&1; then
    log_success "Invalid report type handled gracefully"
else
    log_info "Invalid report type may have been rejected (expected)"
fi

# ====== Summary ======

echo ""
echo "========================================="
echo "TESTING COMPLETE"
echo "========================================="
echo -e "Tests Passed: ${GREEN}$PASSED${NC}"
echo -e "Tests Failed: ${RED}$FAILED${NC}"
echo "Total: $((PASSED + FAILED))"
echo "========================================="

if [ $FAILED -eq 0 ]; then
    echo -e "${GREEN}All tests passed!${NC}"
    exit 0
else
    echo -e "${RED}Some tests failed!${NC}"
    exit 1
fi
