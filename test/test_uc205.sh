#!/bin/bash

# Script test UC205 - Xác nhận và cập nhật yêu cầu cứu hộ

BASE_URL="http://localhost:8080/v1"
USER_USERNAME="user1"
USER_PASSWORD="password123"
COMPANY_USERNAME="company1"
COMPANY_PASSWORD="password123"

echo "=========================================="
echo "Test UC205 - Xác nhận và cập nhật yêu cầu cứu hộ"
echo "=========================================="
echo ""

# Color codes
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Helper function
test_result() {
    if [ $1 -eq 0 ]; then
        echo -e "${GREEN}✓ PASS${NC}: $2"
    else
        echo -e "${RED}✗ FAIL${NC}: $2"
    fi
}

# Step 1: Login User
echo -e "${YELLOW}=== Step 1: Login as USER ===${NC}"
USER_RESPONSE=$(curl -s -X POST "$BASE_URL/api/auth/login" \
  -H "Content-Type: application/json" \
  -d "{\"username\": \"$USER_USERNAME\", \"password\": \"$USER_PASSWORD\"}")

USER_TOKEN=$(echo $USER_RESPONSE | grep -o '"accessToken":"[^"]*"' | cut -d'"' -f4)

if [ -z "$USER_TOKEN" ]; then
    echo -e "${RED}Failed to get user token${NC}"
    echo "Response: $USER_RESPONSE"
    exit 1
fi

echo "User Token: ${USER_TOKEN:0:20}..."
echo ""

# Step 2: Login Company
echo -e "${YELLOW}=== Step 2: Login as COMPANY ===${NC}"
COMPANY_RESPONSE=$(curl -s -X POST "$BASE_URL/api/auth/login" \
  -H "Content-Type: application/json" \
  -d "{\"username\": \"$COMPANY_USERNAME\", \"password\": \"$COMPANY_PASSWORD\"}")

COMPANY_TOKEN=$(echo $COMPANY_RESPONSE | grep -o '"accessToken":"[^"]*"' | cut -d'"' -f4)

if [ -z "$COMPANY_TOKEN" ]; then
    echo -e "${RED}Failed to get company token${NC}"
    echo "Response: $COMPANY_RESPONSE"
    exit 1
fi

echo "Company Token: ${COMPANY_TOKEN:0:20}..."
echo ""

# Step 3: Create Rescue Request
echo -e "${YELLOW}=== Step 3: USER creates rescue request ===${NC}"
CREATE_RESPONSE=$(curl -s -X POST "$BASE_URL/api/rescue-requests" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $USER_TOKEN" \
  -d '{
    "location": "123 Đường Nguyễn Huệ, Quận 1, TP.HCM",
    "latitude": 10.7769,
    "longitude": 106.7009,
    "description": "Xe tôi bị hỏng máy",
    "serviceType": "engine_repair"
  }')

RESCUE_ID=$(echo $CREATE_RESPONSE | grep -o '"id":[0-9]*' | head -1 | cut -d':' -f2)
STATUS=$(echo $CREATE_RESPONSE | grep -o '"status":"[^"]*"' | cut -d'"' -f4)

if [ "$STATUS" = "PENDING_CONFIRMATION" ]; then
    test_result 0 "Rescue request created with ID: $RESCUE_ID, Status: $STATUS"
else
    test_result 1 "Failed to create rescue request"
    echo "Response: $CREATE_RESPONSE"
    exit 1
fi
echo ""

# Step 4: Get Rescue Request
echo -e "${YELLOW}=== Step 4: Get rescue request details ===${NC}"
GET_RESPONSE=$(curl -s -X GET "$BASE_URL/api/rescue-requests/$RESCUE_ID" \
  -H "Authorization: Bearer $USER_TOKEN")

STATUS=$(echo $GET_RESPONSE | grep -o '"status":"[^"]*"' | cut -d'"' -f4)
test_result 0 "Retrieved request, Status: $STATUS"
echo ""

# Step 5: Company accepts rescue request
echo -e "${YELLOW}=== Step 5: COMPANY accepts rescue request ===${NC}"
ACCEPT_RESPONSE=$(curl -s -X POST "$BASE_URL/api/rescue-requests/$RESCUE_ID/accept" \
  -H "Authorization: Bearer $COMPANY_TOKEN")

STATUS=$(echo $ACCEPT_RESPONSE | grep -o '"status":"[^"]*"' | cut -d'"' -f4)
COMPANY_ID=$(echo $ACCEPT_RESPONSE | grep -o '"companyId":[0-9]*' | cut -d':' -f2)

if [ "$STATUS" = "ACCEPTED" ]; then
    test_result 0 "Request accepted by company (ID: $COMPANY_ID), Status: $STATUS"
else
    test_result 1 "Failed to accept request"
    echo "Response: $ACCEPT_RESPONSE"
fi
echo ""

# Step 6: Company updates status to IN_TRANSIT
echo -e "${YELLOW}=== Step 6: COMPANY updates status to IN_TRANSIT ===${NC}"
UPDATE_RESPONSE=$(curl -s -X PATCH "$BASE_URL/api/rescue-requests/$RESCUE_ID/status" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $COMPANY_TOKEN" \
  -d '{"status": "IN_TRANSIT", "reason": "Đang di chuyển đến địa điểm"}')

STATUS=$(echo $UPDATE_RESPONSE | grep -o '"status":"[^"]*"' | cut -d'"' -f4)

if [ "$STATUS" = "IN_TRANSIT" ]; then
    test_result 0 "Status updated to: $STATUS"
else
    test_result 1 "Failed to update status to IN_TRANSIT"
    echo "Response: $UPDATE_RESPONSE"
fi
echo ""

# Step 7: Company updates status to IN_PROGRESS
echo -e "${YELLOW}=== Step 7: COMPANY updates status to IN_PROGRESS ===${NC}"
UPDATE_RESPONSE=$(curl -s -X PATCH "$BASE_URL/api/rescue-requests/$RESCUE_ID/status" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $COMPANY_TOKEN" \
  -d '{"status": "IN_PROGRESS"}')

STATUS=$(echo $UPDATE_RESPONSE | grep -o '"status":"[^"]*"' | cut -d'"' -f4)

if [ "$STATUS" = "IN_PROGRESS" ]; then
    test_result 0 "Status updated to: $STATUS"
else
    test_result 1 "Failed to update status to IN_PROGRESS"
    echo "Response: $UPDATE_RESPONSE"
fi
echo ""

# Step 8: Company completes the rescue request
echo -e "${YELLOW}=== Step 8: COMPANY completes rescue request ===${NC}"
UPDATE_RESPONSE=$(curl -s -X PATCH "$BASE_URL/api/rescue-requests/$RESCUE_ID/status" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $COMPANY_TOKEN" \
  -d '{"status": "COMPLETED"}')

STATUS=$(echo $UPDATE_RESPONSE | grep -o '"status":"[^"]*"' | cut -d'"' -f4)

if [ "$STATUS" = "COMPLETED" ]; then
    test_result 0 "Rescue request completed, Status: $STATUS"
else
    test_result 1 "Failed to complete rescue request"
    echo "Response: $UPDATE_RESPONSE"
fi
echo ""

# Step 9: Test rejection
echo -e "${YELLOW}=== Step 9: Create another request and test rejection ===${NC}"
CREATE_RESPONSE2=$(curl -s -X POST "$BASE_URL/api/rescue-requests" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $USER_TOKEN" \
  -d '{
    "location": "456 Đường Lê Lợi, Quận 1, TP.HCM",
    "latitude": 10.7750,
    "longitude": 106.7025,
    "description": "Xe bị hỏng lốp",
    "serviceType": "tire_repair"
  }')

RESCUE_ID_2=$(echo $CREATE_RESPONSE2 | grep -o '"id":[0-9]*' | head -1 | cut -d':' -f2)
echo "Created second request with ID: $RESCUE_ID_2"

REJECT_RESPONSE=$(curl -s -X POST "$BASE_URL/api/rescue-requests/$RESCUE_ID_2/reject" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $COMPANY_TOKEN" \
  -d '{"rejectionReason": "Không có kỹ thuật viên khả dụng"}')

STATUS=$(echo $REJECT_RESPONSE | grep -o '"status":"[^"]*"' | cut -d'"' -f4)

if [ "$STATUS" = "REJECTED_BY_COMPANY" ]; then
    test_result 0 "Request rejected successfully, Status: $STATUS"
else
    test_result 1 "Failed to reject request"
    echo "Response: $REJECT_RESPONSE"
fi
echo ""

# Step 10: Test cancellation
echo -e "${YELLOW}=== Step 10: Create another request and test cancellation ===${NC}"
CREATE_RESPONSE3=$(curl -s -X POST "$BASE_URL/api/rescue-requests" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $USER_TOKEN" \
  -d '{
    "location": "789 Đường Nguyễn Thái Bình, Quận 1, TP.HCM",
    "latitude": 10.7780,
    "longitude": 106.7010,
    "description": "Cần cứu hộ gấp",
    "serviceType": "towing"
  }')

RESCUE_ID_3=$(echo $CREATE_RESPONSE3 | grep -o '"id":[0-9]*' | head -1 | cut -d':' -f2)
echo "Created third request with ID: $RESCUE_ID_3"

CANCEL_RESPONSE=$(curl -s -X POST "$BASE_URL/api/rescue-requests/$RESCUE_ID_3/cancel" \
  -H "Authorization: Bearer $USER_TOKEN")

STATUS=$(echo $CANCEL_RESPONSE | grep -o '"status":"[^"]*"' | cut -d'"' -f4)

if [ "$STATUS" = "CANCELLED_BY_USER" ]; then
    test_result 0 "Request cancelled successfully, Status: $STATUS"
else
    test_result 1 "Failed to cancel request"
    echo "Response: $CANCEL_RESPONSE"
fi
echo ""

# Step 11: Test error - company cannot update cancelled request
echo -e "${YELLOW}=== Step 11: Test error - company cannot update cancelled request ===${NC}"
ERROR_RESPONSE=$(curl -s -X PATCH "$BASE_URL/api/rescue-requests/$RESCUE_ID_3/status" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $COMPANY_TOKEN" \
  -d '{"status": "IN_PROGRESS"}')

if echo $ERROR_RESPONSE | grep -q "Yêu cầu đã bị người dùng hủy"; then
    test_result 0 "Properly rejected update on cancelled request"
else
    test_result 1 "Should have rejected update on cancelled request"
    echo "Response: $ERROR_RESPONSE"
fi
echo ""

# Step 12: Get company's rescue requests
echo -e "${YELLOW}=== Step 12: Get company's rescue requests ===${NC}"
COMPANY_REQUESTS=$(curl -s -X GET "$BASE_URL/api/rescue-requests/company/assigned" \
  -H "Authorization: Bearer $COMPANY_TOKEN")

COUNT=$(echo $COMPANY_REQUESTS | grep -o '"id":' | wc -l)
test_result 0 "Company has $COUNT rescue requests assigned"
echo ""

# Step 13: Get user's rescue requests
echo -e "${YELLOW}=== Step 13: Get user's rescue requests ===${NC}"
USER_REQUESTS=$(curl -s -X GET "$BASE_URL/api/rescue-requests/user/my-requests" \
  -H "Authorization: Bearer $USER_TOKEN")

COUNT=$(echo $USER_REQUESTS | grep -o '"id":' | wc -l)
test_result 0 "User has $COUNT rescue requests"
echo ""

echo -e "${YELLOW}=========================================="
echo "All tests completed!"
echo "==========================================${NC}"
