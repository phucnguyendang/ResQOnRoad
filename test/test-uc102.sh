#!/bin/bash

# UC102 Review Feature Test Script
# Test the Review and Feedback functionality

BASE_URL="http://localhost:8080/api"
TOKEN="your_jwt_token_here"  # Replace with actual JWT token

echo "=========================================="
echo "UC102: Review and Feedback - API Tests"
echo "=========================================="
echo ""

# Color codes for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Test 1: Create a Review
echo -e "${YELLOW}Test 1: Create/Update Review${NC}"
echo "POST /api/reviews"
curl -X POST \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "requestId": 1,
    "rating": 5,
    "comment": "Tới nhanh, xử lý chuyên nghiệp."
  }' \
  "$BASE_URL/reviews"
echo -e "\n\n"

# Test 2: Get Company Reviews
echo -e "${YELLOW}Test 2: Get Company Reviews (with pagination)${NC}"
echo "GET /api/companies/{companyId}/reviews?page=1&limit=10"
curl -X GET \
  "$BASE_URL/reviews/companies/1/reviews?page=1&limit=10"
echo -e "\n\n"

# Test 3: Get Company Average Rating
echo -e "${YELLOW}Test 3: Get Company Average Rating${NC}"
echo "GET /api/companies/{companyId}/rating"
curl -X GET \
  "$BASE_URL/reviews/companies/1/rating"
echo -e "\n\n"

# Test 4: Get User's Reviews
echo -e "${YELLOW}Test 4: Get User's Reviews${NC}"
echo "GET /api/reviews/my-reviews"
curl -X GET \
  -H "Authorization: Bearer $TOKEN" \
  "$BASE_URL/reviews/my-reviews"
echo -e "\n\n"

# Test 5: Check if User Reviewed Company
echo -e "${YELLOW}Test 5: Check if User Reviewed Company${NC}"
echo "GET /api/reviews/check?companyId=1"
curl -X GET \
  -H "Authorization: Bearer $TOKEN" \
  "$BASE_URL/reviews/check?companyId=1"
echo -e "\n\n"

echo -e "${GREEN}=========================================="
echo "All tests completed!"
echo "=========================================${NC}"
