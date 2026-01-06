#!/bin/bash

# ============================================
# TEST SCRIPT FOR UC403 - COMPANY ADMIN MANAGEMENT
# Quan ly tai khoan cong ty cuu ho
# ============================================

BASE_URL="http://localhost:8080/v1"
PASS_COUNT=0
FAIL_COUNT=0

echo "==========================================="
echo "   TEST UC403 - QUAN LY TAI KHOAN CONG TY"
echo "==========================================="
echo ""

# Helper function to make API call and handle response
test_api_call() {
    local name=$1
    local method=$2
    local endpoint=$3
    local data=$4
    local token=$5
    
    echo "--- $name ---"
    
    if [ -z "$token" ]; then
        response=$(curl -s -X "$method" "$BASE_URL$endpoint" \
            -H "Content-Type: application/json" \
            -d "$data" 2>&1)
    else
        response=$(curl -s -X "$method" "$BASE_URL$endpoint" \
            -H "Content-Type: application/json" \
            -H "Authorization: Bearer $token" \
            -d "$data" 2>&1)
    fi
    
    echo "$response"
}

# ============================================
# SETUP: Login as Admin
# ============================================
echo ""
echo "===== SETUP: Login as ADMIN ====="

# Generate unique test identifiers
TIMESTAMP=$(date +%s)
UNIQUE_ID="uc403_${TIMESTAMP}"

login_response=$(curl -s -X POST "$BASE_URL/api/auth/login" \
    -H "Content-Type: application/json" \
    -d '{"username":"admin","password":"admin123"}')

ADMIN_TOKEN=$(echo "$login_response" | jq -r '.data.token' 2>/dev/null)

if [ -z "$ADMIN_TOKEN" ] || [ "$ADMIN_TOKEN" == "null" ]; then
    echo "❌ Admin login failed"
    exit 1
fi

echo "✅ Admin login successful"
echo "   Token: ${ADMIN_TOKEN:0:30}..."

# ============================================
# TEST 1: Get all companies (Step 2)
# ============================================
echo ""
echo "===== TEST 1: Get all companies (Step 2) ====="

companies_response=$(curl -s -X GET "$BASE_URL/api/admin/companies?page=0&size=10" \
    -H "Authorization: Bearer $ADMIN_TOKEN")

total_companies=$(echo "$companies_response" | jq -r '.data.totalElements' 2>/dev/null)

if [ -z "$total_companies" ] || [ "$total_companies" == "null" ]; then
    echo "[❌ FAIL] Get all companies"
    ((FAIL_COUNT++))
else
    echo "[✅ PASS] Get all companies"
    echo "   Total: $total_companies companies"
    ((PASS_COUNT++))
    
    # Store first company ID if exists
    FIRST_COMPANY_ID=$(echo "$companies_response" | jq -r '.data.content[0].id' 2>/dev/null)
    if [ -n "$FIRST_COMPANY_ID" ] && [ "$FIRST_COMPANY_ID" != "null" ]; then
        echo "   First company ID: $FIRST_COMPANY_ID"
    fi
fi

# ============================================
# TEST 2: Get company detail (Step 3-4)
# ============================================
echo ""
echo "===== TEST 2: Get company detail (Step 3-4) ====="

if [ -n "$FIRST_COMPANY_ID" ] && [ "$FIRST_COMPANY_ID" != "null" ]; then
    detail_response=$(curl -s -X GET "$BASE_URL/api/admin/companies/$FIRST_COMPANY_ID" \
        -H "Authorization: Bearer $ADMIN_TOKEN")
    
    company_name=$(echo "$detail_response" | jq -r '.data.name' 2>/dev/null)
    
    if [ -z "$company_name" ] || [ "$company_name" == "null" ]; then
        echo "[❌ FAIL] Get company detail"
        ((FAIL_COUNT++))
    else
        echo "[✅ PASS] Get company detail"
        echo "   Company Name: $company_name"
        ((PASS_COUNT++))
    fi
else
    echo "⚠️  SKIP: No company exists to test detail"
fi

# ============================================
# TEST 3: Create new company (Step 5 - Add new)
# ============================================
echo ""
echo "===== TEST 3: Create new company (Step 5 - Add) ====="

create_company_body="{
    \"name\": \"Cuu Ho Test Company UC403\",
    \"address\": \"123 Duong ABC, Ba Dinh, Ha Noi\",
    \"phone\": \"0243999999\",
    \"email\": \"test-uc403-${UNIQUE_ID}@cuuho.vn\",
    \"latitude\": 21.0285,
    \"longitude\": 105.8542,
    \"serviceRadius\": 50,
    \"taxCode\": \"0101999999\",
    \"hotline\": \"0243999999\",
    \"operatingHours\": \"24/7\",
    \"businessLicense\": \"03-05-2025\",
    \"licenseDocumentUrl\": \"https://example.com/license.pdf\",
    \"description\": \"Test company for UC403\",
    \"username\": \"test_company_${UNIQUE_ID}\",
    \"password\": \"testpass123\"
}"

create_response=$(curl -s -X POST "$BASE_URL/api/admin/companies" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $ADMIN_TOKEN" \
    -d "$create_company_body")

NEW_COMPANY_ID=$(echo "$create_response" | jq -r '.data.id' 2>/dev/null)

if [ -z "$NEW_COMPANY_ID" ] || [ "$NEW_COMPANY_ID" == "null" ]; then
    echo "[❌ FAIL] Create new company"
    ((FAIL_COUNT++))
else
    echo "[✅ PASS] Create new company"
    echo "   New Company ID: $NEW_COMPANY_ID"
    ((PASS_COUNT++))
fi

# ============================================
# TEST 4: Try create duplicate company (Error case)
# ============================================
echo ""
echo "===== TEST 4: Try create duplicate company (Error case) ====="

duplicate_body="{
    \"name\": \"Another Company\",
    \"address\": \"456 Duong XYZ\",
    \"phone\": \"0244000000\",
    \"email\": \"another-${UNIQUE_ID}@cuuho.vn\",
    \"latitude\": 21.0300,
    \"longitude\": 105.8550,
    \"serviceRadius\": 50,
    \"taxCode\": \"0102000000\",
    \"hotline\": \"0244000000\",
    \"operatingHours\": \"24/7\",
    \"businessLicense\": \"03-05-2025\",
    \"username\": \"test_company_${UNIQUE_ID}\",
    \"password\": \"testpass123\"
}"

dup_response=$(curl -s -X POST "$BASE_URL/api/admin/companies" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $ADMIN_TOKEN" \
    -d "$duplicate_body")

dup_error=$(echo "$dup_response" | jq -r '.message // .error.message' 2>/dev/null)

if echo "$dup_error" | grep -q "tồn tại\|trung lap\|duplicate\|Username.*tồn tại"; then
    echo "[✅ PASS] Duplicate username properly rejected"
    ((PASS_COUNT++))
else
    echo "[❌ FAIL] Duplicate username should be rejected"
    echo "   Response: $dup_response"
    ((FAIL_COUNT++))
fi

# ============================================
# TEST 5: Update company information (Step 5 - Edit)
# ============================================
echo ""
echo "===== TEST 5: Update company information (Step 5 - Edit) ====="

if [ -n "$NEW_COMPANY_ID" ] && [ "$NEW_COMPANY_ID" != "null" ]; then
    update_body='{
        "name": "Cuu Ho Test Company UC403 - Updated",
        "phone": "0243888888",
        "hotline": "0243888888",
        "operatingHours": "6:00-22:00",
        "serviceRadius": 60
    }'
    
    update_response=$(curl -s -X PUT "$BASE_URL/api/admin/companies/$NEW_COMPANY_ID" \
        -H "Content-Type: application/json" \
        -H "Authorization: Bearer $ADMIN_TOKEN" \
        -d "$update_body")
    
    updated_name=$(echo "$update_response" | jq -r '.data.name' 2>/dev/null)
    
    if echo "$updated_name" | grep -q "Updated"; then
        echo "[✅ PASS] Update company information"
        echo "   Updated Name: $updated_name"
        ((PASS_COUNT++))
    else
        echo "[❌ FAIL] Update company information"
        ((FAIL_COUNT++))
    fi
fi

# ============================================
# TEST 6: Verify company (Step 6 - Verify License)
# ============================================
echo ""
echo "===== TEST 6: Verify company license (Step 6 - Verify) ====="

if [ -n "$NEW_COMPANY_ID" ] && [ "$NEW_COMPANY_ID" != "null" ]; then
    verify_response=$(curl -s -X POST "$BASE_URL/api/admin/companies/$NEW_COMPANY_ID/verify" \
        -H "Authorization: Bearer $ADMIN_TOKEN")
    
    is_verified=$(echo "$verify_response" | jq -r '.data.isVerified' 2>/dev/null)
    
    if [ "$is_verified" == "true" ]; then
        echo "[✅ PASS] Verify company"
        echo "   Verified: $is_verified"
        ((PASS_COUNT++))
    else
        echo "[❌ FAIL] Verify company"
        ((FAIL_COUNT++))
    fi
fi

# ============================================
# TEST 7: Create another company for rejection test
# ============================================
echo ""
echo "===== TEST 7: Create another company for rejection test ====="

create_company_body2="{
    \"name\": \"Cuu Ho Reject Test Company\",
    \"address\": \"789 Duong DEF, Hai Ba Trung, Ha Noi\",
    \"phone\": \"0245000000\",
    \"email\": \"reject-test-${UNIQUE_ID}@cuuho.vn\",
    \"latitude\": 21.0150,
    \"longitude\": 105.8400,
    \"serviceRadius\": 40,
    \"taxCode\": \"0103000000\",
    \"hotline\": \"0245000000\",
    \"operatingHours\": \"24/7\",
    \"businessLicense\": \"03-05-2025\",
    \"licenseDocumentUrl\": \"https://example.com/license2.pdf\",
    \"description\": \"Company for rejection test\",
    \"username\": \"test_company_reject_${UNIQUE_ID}\",
    \"password\": \"testpass123\"
}"

create_response2=$(curl -s -X POST "$BASE_URL/api/admin/companies" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $ADMIN_TOKEN" \
    -d "$create_company_body2")

REJECT_COMPANY_ID=$(echo "$create_response2" | jq -r '.data.id' 2>/dev/null)

if [ -z "$REJECT_COMPANY_ID" ] || [ "$REJECT_COMPANY_ID" == "null" ]; then
    echo "[❌ FAIL] Create company for rejection test"
    ((FAIL_COUNT++))
else
    echo "[✅ PASS] Create company for rejection test"
    echo "   Created Company ID: $REJECT_COMPANY_ID"
    ((PASS_COUNT++))
fi

# ============================================
# TEST 8: Reject company verification (Step 6a - Reject)
# ============================================
echo ""
echo "===== TEST 8: Reject company verification (Step 6a - Reject) ====="

if [ -n "$REJECT_COMPANY_ID" ] && [ "$REJECT_COMPANY_ID" != "null" ]; then
    reject_response=$(curl -s -X POST "$BASE_URL/api/admin/companies/$REJECT_COMPANY_ID/reject?reason=Giay%20phep%20khong%20hop%20le" \
        -H "Authorization: Bearer $ADMIN_TOKEN")
    
    profile_status=$(echo "$reject_response" | jq -r '.data.profileStatus' 2>/dev/null)
    
    if echo "$profile_status" | grep -q -i "rejected\|reject"; then
        echo "[✅ PASS] Reject company verification"
        echo "   Profile Status: $profile_status"
        ((PASS_COUNT++))
    else
        echo "[❌ FAIL] Reject company verification"
        ((FAIL_COUNT++))
    fi
fi

# ============================================
# TEST 9: Update company status - Lock (Step 5 - Lock)
# ============================================
echo ""
echo "===== TEST 9: Update company status - Lock (Step 5 - Lock) ====="

if [ -n "$REJECT_COMPANY_ID" ] && [ "$REJECT_COMPANY_ID" != "null" ]; then
    lock_body='{
        "status": "LOCKED",
        "reason": "Vi pham dieu khoan dich vu"
    }'
    
    lock_response=$(curl -s -X PATCH "$BASE_URL/api/admin/companies/$REJECT_COMPANY_ID/status" \
        -H "Content-Type: application/json" \
        -H "Authorization: Bearer $ADMIN_TOKEN" \
        -d "$lock_body")
    
    is_active=$(echo "$lock_response" | jq -r '.data.isActive' 2>/dev/null)
    
    if [ "$is_active" == "false" ]; then
        echo "[✅ PASS] Lock company"
        echo "   Active: $is_active"
        ((PASS_COUNT++))
    else
        echo "[❌ FAIL] Lock company"
        ((FAIL_COUNT++))
    fi
fi

# ============================================
# TEST 10: Update company status - Active (Unlock)
# ============================================
echo ""
echo "===== TEST 10: Update company status - Active (Unlock) ====="

if [ -n "$REJECT_COMPANY_ID" ] && [ "$REJECT_COMPANY_ID" != "null" ]; then
    unlock_body='{
        "status": "ACTIVE"
    }'
    
    unlock_response=$(curl -s -X PATCH "$BASE_URL/api/admin/companies/$REJECT_COMPANY_ID/status" \
        -H "Content-Type: application/json" \
        -H "Authorization: Bearer $ADMIN_TOKEN" \
        -d "$unlock_body")
    
    is_active=$(echo "$unlock_response" | jq -r '.data.isActive' 2>/dev/null)
    
    if [ "$is_active" == "true" ]; then
        echo "[✅ PASS] Unlock company"
        echo "   Active: $is_active"
        ((PASS_COUNT++))
    else
        echo "[❌ FAIL] Unlock company"
        ((FAIL_COUNT++))
    fi
fi

# ============================================
# TEST 11: Delete company (Step 5 - Delete)
# ============================================
echo ""
echo "===== TEST 11: Delete company (Step 5 - Delete) ====="

if [ -n "$REJECT_COMPANY_ID" ] && [ "$REJECT_COMPANY_ID" != "null" ]; then
    delete_response=$(curl -s -X DELETE "$BASE_URL/api/admin/companies/$REJECT_COMPANY_ID" \
        -H "Authorization: Bearer $ADMIN_TOKEN")
    
    message=$(echo "$delete_response" | jq -r '.message' 2>/dev/null)
    
    if echo "$message" | grep -q "Xóa\|xoa"; then
        echo "[✅ PASS] Delete company"
        echo "   Message: $message"
        ((PASS_COUNT++))
    else
        echo "[❌ FAIL] Delete company"
        ((FAIL_COUNT++))
    fi
fi

# ============================================
# TEST 12: Verify deletion
# ============================================
echo ""
echo "===== TEST 12: Verify deletion ====="

if [ -n "$REJECT_COMPANY_ID" ] && [ "$REJECT_COMPANY_ID" != "null" ]; then
    get_response=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/api/admin/companies/$REJECT_COMPANY_ID" \
        -H "Authorization: Bearer $ADMIN_TOKEN")
    
    http_code=$(echo "$get_response" | tail -1)
    
    if [ "$http_code" == "404" ]; then
        echo "[✅ PASS] Deleted company not found (404)"
        ((PASS_COUNT++))
    else
        echo "[❌ FAIL] Expected 404, got $http_code"
        ((FAIL_COUNT++))
    fi
fi

# ============================================
# TEST 13: Get all companies again
# ============================================
echo ""
echo "===== TEST 13: Get all companies again (final state) ====="

final_response=$(curl -s -X GET "$BASE_URL/api/admin/companies?page=0&size=10" \
    -H "Authorization: Bearer $ADMIN_TOKEN")

final_count=$(echo "$final_response" | jq -r '.data.totalElements' 2>/dev/null)

if [ -n "$final_count" ] && [ "$final_count" != "null" ]; then
    echo "[✅ PASS] Get all companies (final)"
    echo "   Total companies: $final_count"
    ((PASS_COUNT++))
else
    echo "[❌ FAIL] Get all companies (final)"
    ((FAIL_COUNT++))
fi

# ============================================
# SUMMARY
# ============================================
echo ""
echo "==========================================="
echo "   TEST SUMMARY"
echo "==========================================="
echo "✅ PASS: $PASS_COUNT"
echo "❌ FAIL: $FAIL_COUNT"
echo "📊 Total: $((PASS_COUNT + FAIL_COUNT))"

if [ $FAIL_COUNT -eq 0 ]; then
    echo ""
    echo "🎉 All tests passed!"
    exit 0
else
    echo ""
    echo "⚠️  Some tests failed."
    exit 1
fi
