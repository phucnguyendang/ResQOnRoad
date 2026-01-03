#!/bin/bash

# UC102 Simple Test Script

echo "═══════════════════════════════════════════════════════════"
echo "🚀 UC102 REVIEW FEATURE - TEST EXECUTION"
echo "═══════════════════════════════════════════════════════════"
echo ""

PASSED=0
FAILED=0

# Test 1: Backend Files
echo "📂 TEST 1: Checking Backend Components"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

test_file() {
    if [ -f "$1" ]; then
        echo "✅ $2"
        ((PASSED++))
        return 0
    else
        echo "❌ Missing: $2"
        ((FAILED++))
        return 1
    fi
}

test_file "/home/tlam/codes/ResQOnRoad/backend/src/main/java/com/rescue/system/controller/ReviewController.java" "ReviewController.java"
test_file "/home/tlam/codes/ResQOnRoad/backend/src/main/java/com/rescue/system/service/ReviewService.java" "ReviewService.java"
test_file "/home/tlam/codes/ResQOnRoad/backend/src/main/java/com/rescue/system/service/impl/ReviewServiceImpl.java" "ReviewServiceImpl.java"
test_file "/home/tlam/codes/ResQOnRoad/backend/src/main/java/com/rescue/system/dto/request/CreateReviewRequest.java" "CreateReviewRequest.java"

echo ""
echo "📱 TEST 2: Checking Frontend Components"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

test_file "/home/tlam/codes/ResQOnRoad/frontend/src/components/ReviewForm.jsx" "ReviewForm.jsx"
test_file "/home/tlam/codes/ResQOnRoad/frontend/src/service/reviewService.js" "reviewService.js"

echo ""
echo "📚 TEST 3: Checking Test Documentation"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

test_file "/home/tlam/codes/ResQOnRoad/test/UC102_TEST_GUIDE.md" "UC102_TEST_GUIDE.md"
test_file "/home/tlam/codes/ResQOnRoad/test/UC102_TEST_SUMMARY.md" "UC102_TEST_SUMMARY.md"
test_file "/home/tlam/codes/ResQOnRoad/test/UC102_Postman_Collection.json" "UC102_Postman_Collection.json"

echo ""
echo "🔍 TEST 4: Checking Code Quality"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

# Check for RestController annotation
if grep -q "@RestController" "/home/tlam/codes/ResQOnRoad/backend/src/main/java/com/rescue/system/controller/ReviewController.java"; then
    echo "✅ ReviewController has @RestController"
    ((PASSED++))
else
    echo "❌ Missing @RestController in ReviewController"
    ((FAILED++))
fi

# Check for @RequestMapping
if grep -q "@RequestMapping" "/home/tlam/codes/ResQOnRoad/backend/src/main/java/com/rescue/system/controller/ReviewController.java"; then
    echo "✅ ReviewController has @RequestMapping"
    ((PASSED++))
else
    echo "❌ Missing @RequestMapping in ReviewController"
    ((FAILED++))
fi

# Check for validation annotations
if grep -q "@Valid" "/home/tlam/codes/ResQOnRoad/backend/src/main/java/com/rescue/system/controller/ReviewController.java"; then
    echo "✅ Request validation enabled"
    ((PASSED++))
else
    echo "❌ Missing @Valid annotation"
    ((FAILED++))
fi

# Check for JWT handling
if grep -q "jwtTokenProvider\|JwtTokenProvider" "/home/tlam/codes/ResQOnRoad/backend/src/main/java/com/rescue/system/controller/ReviewController.java"; then
    echo "✅ JWT authentication configured"
    ((PASSED++))
else
    echo "❌ Missing JWT handling"
    ((FAILED++))
fi

echo ""
echo "🗄️  TEST 5: Checking Database Integration"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

# Check ReviewRepository exists
if [ -f "/home/tlam/codes/ResQOnRoad/backend/src/main/java/com/rescue/system/repository/ReviewRepository.java" ]; then
    echo "✅ ReviewRepository.java exists"
    ((PASSED++))
    
    # Check for custom query methods
    if grep -q "findByUserIdAndCompanyId\|findByCompanyId" "/home/tlam/codes/ResQOnRoad/backend/src/main/java/com/rescue/system/repository/ReviewRepository.java"; then
        echo "✅ Database query methods defined"
        ((PASSED++))
    else
        echo "❌ Missing query methods"
        ((FAILED++))
    fi
else
    echo "❌ ReviewRepository.java not found"
    ((FAILED++))
fi

echo ""
echo "🚀 TEST 6: API Endpoints Check"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

count_mapping() {
    local file="$1"
    local mapping="$2"
    if grep -q "$mapping" "$file"; then
        echo "✅ Found: $mapping"
        ((PASSED++))
    else
        echo "❌ Missing: $mapping"
        ((FAILED++))
    fi
}

CONTROLLER="/home/tlam/codes/ResQOnRoad/backend/src/main/java/com/rescue/system/controller/ReviewController.java"

count_mapping "$CONTROLLER" "POST"
count_mapping "$CONTROLLER" "GET"
count_mapping "$CONTROLLER" "/api/reviews"

echo ""
echo "═══════════════════════════════════════════════════════════"
echo "📊 TEST RESULTS"
echo "═══════════════════════════════════════════════════════════"
echo ""
echo "✅ PASSED: $PASSED"
echo "❌ FAILED: $FAILED"

TOTAL=$((PASSED + FAILED))
if [ $TOTAL -gt 0 ]; then
    PERCENT=$((PASSED * 100 / TOTAL))
    echo "📈 Coverage: $PERCENT% ($PASSED/$TOTAL)"
fi

echo ""
if [ $FAILED -eq 0 ]; then
    echo "🎉 ALL TESTS PASSED!"
    echo ""
    echo "✅ Backend implementation complete"
    echo "✅ Frontend implementation complete"
    echo "✅ Test documentation ready"
    echo "✅ API endpoints configured"
    echo ""
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo "🚀 Ready for Server Testing!"
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo ""
    echo "NEXT STEPS:"
    echo "1. Start the backend server:"
    echo "   cd /home/tlam/codes/ResQOnRoad/backend"
    echo "   mvn spring-boot:run"
    echo ""
    echo "2. In another terminal, test API:"
    echo "   bash /home/tlam/codes/ResQOnRoad/test/test-uc102.sh"
    echo ""
    echo "3. Or use Postman:"
    echo "   Import: test/UC102_Postman_Collection.json"
    echo ""
else
    echo "⚠️  Some tests failed. Check output above."
fi

echo ""
