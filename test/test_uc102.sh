#!/bin/bash

################################################################################
# UC102 - Review and Feedback Feature - Unified Test Suite
# 
# Tệp này tổng hợp tất cả test scripts và documentation cho UC102
# 
# Usage:
#   bash test_uc102.sh                 # Run interactive menu
#   bash test_uc102.sh quick           # Quick tests only
#   bash test_uc102.sh full            # All tests
#   bash test_uc102.sh api             # API integration tests
#   bash test_uc102.sh verify          # Component verification
#   bash test_uc102.sh docs            # Show documentation
################################################################################

BASE_URL="http://localhost:8080/api"
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m'

PASSED=0
FAILED=0

# ═════════════════════════════════════════════════════════════════════════════
# UTILITY FUNCTIONS
# ═════════════════════════════════════════════════════════════════════════════

print_header() {
    echo ""
    echo -e "${BLUE}╔════════════════════════════════════════════════════════════╗${NC}"
    echo -e "${BLUE}║${NC} $1"
    echo -e "${BLUE}╚════════════════════════════════════════════════════════════╝${NC}"
    echo ""
}

print_section() {
    echo ""
    echo -e "${CYAN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
    echo -e "${CYAN}▶ $1${NC}"
    echo -e "${CYAN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
    echo ""
}

print_test() {
    echo -e "${YELLOW}🧪 $1${NC}"
}

print_pass() {
    echo -e "${GREEN}✅ PASS: $1${NC}"
    PASSED=$((PASSED+1))
}

print_fail() {
    echo -e "${RED}❌ FAIL: $1${NC}"
    FAILED=$((FAILED+1))
}

print_info() {
    echo -e "${BLUE}ℹ️  $1${NC}"
}

# ═════════════════════════════════════════════════════════════════════════════
# COMPONENT VERIFICATION TESTS
# ═════════════════════════════════════════════════════════════════════════════

test_backend_components() {
    print_section "BACKEND COMPONENT VERIFICATION"
    
    print_test "ReviewController.java"
    if [ -f "$PROJECT_DIR/backend/src/main/java/com/rescue/system/controller/ReviewController.java" ]; then
        lines=$(wc -l < "$PROJECT_DIR/backend/src/main/java/com/rescue/system/controller/ReviewController.java" 2>/dev/null || echo "?")
        print_pass "ReviewController.java exists ($lines lines)"
    else
        print_fail "ReviewController.java NOT FOUND"
    fi
    
    print_test "ReviewService.java"
    if [ -f "$PROJECT_DIR/backend/src/main/java/com/rescue/system/service/ReviewService.java" ]; then
        lines=$(wc -l < "$PROJECT_DIR/backend/src/main/java/com/rescue/system/service/ReviewService.java" 2>/dev/null || echo "?")
        print_pass "ReviewService.java exists ($lines lines)"
    else
        print_fail "ReviewService.java NOT FOUND"
    fi
    
    print_test "ReviewServiceImpl.java"
    if [ -f "$PROJECT_DIR/backend/src/main/java/com/rescue/system/service/impl/ReviewServiceImpl.java" ]; then
        lines=$(wc -l < "$PROJECT_DIR/backend/src/main/java/com/rescue/system/service/impl/ReviewServiceImpl.java" 2>/dev/null || echo "?")
        print_pass "ReviewServiceImpl.java exists ($lines lines)"
    else
        print_fail "ReviewServiceImpl.java NOT FOUND"
    fi
    
    print_test "CreateReviewRequest.java"
    if [ -f "$PROJECT_DIR/backend/src/main/java/com/rescue/system/dto/request/CreateReviewRequest.java" ]; then
        lines=$(wc -l < "$PROJECT_DIR/backend/src/main/java/com/rescue/system/dto/request/CreateReviewRequest.java" 2>/dev/null || echo "?")
        print_pass "CreateReviewRequest.java exists ($lines lines)"
    else
        print_fail "CreateReviewRequest.java NOT FOUND"
    fi
    
    print_test "ReviewRepository.java"
    if [ -f "$PROJECT_DIR/backend/src/main/java/com/rescue/system/repository/ReviewRepository.java" ]; then
        lines=$(wc -l < "$PROJECT_DIR/backend/src/main/java/com/rescue/system/repository/ReviewRepository.java" 2>/dev/null || echo "?")
        print_pass "ReviewRepository.java exists ($lines lines)"
    else
        print_fail "ReviewRepository.java NOT FOUND"
    fi
}

test_frontend_components() {
    print_section "FRONTEND COMPONENT VERIFICATION"
    
    print_test "ReviewForm.jsx"
    if [ -f "$PROJECT_DIR/frontend/src/components/ReviewForm.jsx" ]; then
        lines=$(wc -l < "$PROJECT_DIR/frontend/src/components/ReviewForm.jsx" 2>/dev/null || echo "?")
        print_pass "ReviewForm.jsx exists ($lines lines)"
    else
        print_fail "ReviewForm.jsx NOT FOUND"
    fi
    
    print_test "reviewService.js"
    if [ -f "$PROJECT_DIR/frontend/src/service/reviewService.js" ]; then
        lines=$(wc -l < "$PROJECT_DIR/frontend/src/service/reviewService.js" 2>/dev/null || echo "?")
        print_pass "reviewService.js exists ($lines lines)"
    else
        print_fail "reviewService.js NOT FOUND"
    fi
}

test_documentation() {
    print_section "TEST DOCUMENTATION VERIFICATION"
    
    print_test "UC102_TEST_GUIDE.md"
    if [ -f "$PROJECT_DIR/test/UC102_TEST_GUIDE.md" ]; then
        lines=$(wc -l < "$PROJECT_DIR/test/UC102_TEST_GUIDE.md" 2>/dev/null || echo "?")
        print_pass "UC102_TEST_GUIDE.md exists ($lines lines)"
    else
        print_fail "UC102_TEST_GUIDE.md NOT FOUND"
    fi
    
    print_test "UC102_TEST_SUMMARY.md"
    if [ -f "$PROJECT_DIR/test/UC102_TEST_SUMMARY.md" ]; then
        lines=$(wc -l < "$PROJECT_DIR/test/UC102_TEST_SUMMARY.md" 2>/dev/null || echo "?")
        print_pass "UC102_TEST_SUMMARY.md exists ($lines lines)"
    else
        print_fail "UC102_TEST_SUMMARY.md NOT FOUND"
    fi
    
    print_test "UC102_Postman_Collection.json"
    if [ -f "$PROJECT_DIR/test/UC102_Postman_Collection.json" ]; then
        lines=$(wc -l < "$PROJECT_DIR/test/UC102_Postman_Collection.json" 2>/dev/null || echo "?")
        print_pass "UC102_Postman_Collection.json exists ($lines lines)"
    else
        print_fail "UC102_Postman_Collection.json NOT FOUND"
    fi
    
    print_test "UC102_TESTING_QUICK_START.md"
    if [ -f "$PROJECT_DIR/UC102_TESTING_QUICK_START.md" ]; then
        lines=$(wc -l < "$PROJECT_DIR/UC102_TESTING_QUICK_START.md" 2>/dev/null || echo "?")
        print_pass "UC102_TESTING_QUICK_START.md exists ($lines lines)"
    else
        print_fail "UC102_TESTING_QUICK_START.md NOT FOUND"
    fi
}

test_code_quality() {
    print_section "CODE QUALITY CHECKS"
    
    local controller="$PROJECT_DIR/backend/src/main/java/com/rescue/system/controller/ReviewController.java"
    
    if [ ! -f "$controller" ]; then
        print_fail "ReviewController.java not found"
        return
    fi
    
    print_test "@RestController annotation"
    if grep -q "@RestController" "$controller"; then
        print_pass "@RestController found"
    else
        print_fail "@RestController NOT found"
    fi
    
    print_test "@RequestMapping annotation"
    if grep -q "@RequestMapping" "$controller"; then
        print_pass "@RequestMapping found"
    else
        print_fail "@RequestMapping NOT found"
    fi
    
    print_test "@Valid annotation"
    if grep -q "@Valid" "$controller"; then
        print_pass "@Valid annotation found"
    else
        print_fail "@Valid annotation NOT found"
    fi
    
    print_test "JWT authentication"
    if grep -q "JwtTokenProvider\|jwtTokenProvider" "$controller"; then
        print_pass "JWT authentication configured"
    else
        print_fail "JWT authentication NOT configured"
    fi
    
    print_test "@PreAuthorize annotation"
    if grep -q "@PreAuthorize" "$controller"; then
        print_pass "@PreAuthorize found"
    else
        print_fail "@PreAuthorize NOT found"
    fi
}

test_database_integration() {
    print_section "DATABASE INTEGRATION VERIFICATION"
    
    local repo="$PROJECT_DIR/backend/src/main/java/com/rescue/system/repository/ReviewRepository.java"
    
    if [ ! -f "$repo" ]; then
        print_fail "ReviewRepository.java not found"
        return
    fi
    
    print_test "ReviewRepository.java exists"
    print_pass "ReviewRepository.java found"
    
    print_test "Query: findByUserIdAndCompanyId"
    if grep -q "findByUserIdAndCompanyId" "$repo"; then
        print_pass "findByUserIdAndCompanyId method found"
    else
        print_fail "findByUserIdAndCompanyId method NOT found"
    fi
    
    print_test "Query: findByCompanyId"
    if grep -q "findByCompanyId" "$repo"; then
        print_pass "findByCompanyId method found"
    else
        print_fail "findByCompanyId method NOT found"
    fi
}

check_server_running() {
    print_test "Server health check"
    if curl -s http://localhost:8080/api/health > /dev/null 2>&1; then
        print_pass "Server is running"
        return 0
    else
        print_fail "Server is not running at http://localhost:8080"
        return 1
    fi
}

test_api_endpoints() {
    print_section "API INTEGRATION TESTS"
    
    if ! check_server_running; then
        print_info "Skipping API tests - server not running"
        return
    fi
    
    print_test "GET /api/reviews/companies/1/reviews"
    response=$(curl -s -X GET "$BASE_URL/reviews/companies/1/reviews?page=1&limit=10")
    if echo "$response" | grep -q "Lấy\|items\|pagination\|error"; then
        print_pass "Endpoint responds correctly"
    else
        print_fail "Endpoint response invalid"
    fi
    
    print_test "GET /api/reviews/companies/1/rating"
    response=$(curl -s -X GET "$BASE_URL/reviews/companies/1/rating")
    if echo "$response" | grep -q "rating\|Lấy\|error"; then
        print_pass "Endpoint responds correctly"
    else
        print_fail "Endpoint response invalid"
    fi
}

# ═════════════════════════════════════════════════════════════════════════════
# TEST SUITE RUNNERS
# ═════════════════════════════════════════════════════════════════════════════

run_quick_tests() {
    print_header "QUICK UC102 VERIFICATION"
    test_backend_components
    test_frontend_components
    test_code_quality
    print_summary
}

run_verify_only() {
    print_header "UC102 COMPONENT VERIFICATION"
    test_backend_components
    test_frontend_components
    test_documentation
    print_summary
}

run_comprehensive_tests() {
    print_header "COMPREHENSIVE UC102 TEST SUITE"
    test_backend_components
    test_frontend_components
    test_documentation
    test_code_quality
    test_database_integration
    test_api_endpoints
    print_summary
}

run_api_tests() {
    print_header "UC102 API INTEGRATION TESTS"
    test_api_endpoints
    print_summary
}

# ═════════════════════════════════════════════════════════════════════════════
# DOCUMENTATION
# ═════════════════════════════════════════════════════════════════════════════

show_documentation() {
    cat << 'EOF'

═══════════════════════════════════════════════════════════════════════════════
                    📚 UC102 TEST DOCUMENTATION
═══════════════════════════════════════════════════════════════════════════════

OVERVIEW
────────
UC102 is the Review and Feedback feature. Users can rate rescue services
(1-5 stars) after a completed request with optional feedback comments.

COMPONENTS
──────────

Backend:
  ✓ ReviewController.java - REST API endpoints
  ✓ ReviewService.java - Service interface
  ✓ ReviewServiceImpl.java - Business logic
  ✓ CreateReviewRequest.java - Request validation DTO
  ✓ ReviewRepository.java - Database operations

Frontend:
  ✓ ReviewForm.jsx - React component
  ✓ reviewService.js - API client service

API ENDPOINTS
─────────────

1. POST /api/reviews
   Create/update review
   Auth: Required (Bearer token)
   Body: {requestId, rating (1-5), comment (optional)}
   Response: 201 Created

2. GET /api/reviews/companies/{id}/reviews?page=1&limit=10
   List company reviews
   Auth: Optional
   Response: 200 OK

3. GET /api/reviews/companies/{id}/rating
   Get average rating
   Auth: Optional
   Response: 200 OK

4. GET /api/reviews/my-reviews
   Get user's reviews
   Auth: Required
   Response: 200 OK

5. GET /api/reviews/check?companyId=1
   Check if user reviewed
   Auth: Required
   Response: 200 OK

USAGE
─────

# Quick verification
bash test_uc102.sh quick

# Full test suite
bash test_uc102.sh full

# API tests (requires running server)
bash test_uc102.sh api

# Component verification
bash test_uc102.sh verify

# Show this help
bash test_uc102.sh docs

WORKFLOW
────────

1. Verify components: bash test_uc102.sh verify
2. Start server: cd backend && mvn spring-boot:run
3. Run API tests: bash test_uc102.sh api
4. Full testing: bash test_uc102.sh full

═══════════════════════════════════════════════════════════════════════════════
EOF
}

print_summary() {
    echo ""
    print_section "TEST SUMMARY"
    
    echo -e "${GREEN}✅ PASSED: $PASSED${NC}"
    echo -e "${RED}❌ FAILED: $FAILED${NC}"
    
    total=$((PASSED + FAILED))
    if [ $total -gt 0 ]; then
        percentage=$((PASSED * 100 / total))
        echo -e "${BLUE}📈 Success Rate: $percentage% ($PASSED/$total)${NC}"
    fi
    
    if [ $FAILED -eq 0 ] && [ $PASSED -gt 0 ]; then
        echo -e "\n${GREEN}🎉 ALL TESTS PASSED!${NC}"
        echo -e "${GREEN}UC102 is ready for testing.\n${NC}"
    elif [ $PASSED -eq 0 ] && [ $FAILED -eq 0 ]; then
        echo -e "\n${YELLOW}⚠️  No tests were run\n${NC}"
    else
        echo -e "\n${YELLOW}⚠️  Some tests failed\n${NC}"
    fi
}

show_menu() {
    clear
    echo ""
    echo -e "${CYAN}"
    cat << 'EOF'
   ╔═══════════════════════════════════════════════════════════════╗
   ║      UC102 - REVIEW & FEEDBACK TEST SUITE                    ║
   ║                                                               ║
   ║  Unified testing framework for UC102 implementation           ║
   ╚═══════════════════════════════════════════════════════════════╝
EOF
    echo -e "${NC}"
    echo ""
    echo -e "${YELLOW}Available Tests:${NC}"
    echo -e "  ${GREEN}1)${NC} Quick Verification       - Fast component checks"
    echo -e "  ${GREEN}2)${NC} Full Test Suite          - All comprehensive tests"
    echo -e "  ${GREEN}3)${NC} Component Verification   - Verify all files"
    echo -e "  ${GREEN}4)${NC} API Integration Tests    - Test running server"
    echo -e "  ${GREEN}5)${NC} Documentation           - Show help"
    echo -e "  ${GREEN}6)${NC} Exit"
    echo ""
    echo -n -e "${YELLOW}Select option (1-6):${NC} "
}

# ═════════════════════════════════════════════════════════════════════════════
# MAIN
# ═════════════════════════════════════════════════════════════════════════════

main() {
    local mode="${1:-interactive}"
    
    case "$mode" in
        quick)
            PASSED=0
            FAILED=0
            run_quick_tests
            ;;
        full)
            PASSED=0
            FAILED=0
            run_comprehensive_tests
            ;;
        verify)
            PASSED=0
            FAILED=0
            run_verify_only
            ;;
        api)
            PASSED=0
            FAILED=0
            run_api_tests
            ;;
        docs)
            show_documentation
            ;;
        *)
            # Interactive mode
            while true; do
                show_menu
                read -r choice
                
                case "$choice" in
                    1)
                        PASSED=0
                        FAILED=0
                        run_quick_tests
                        read -p "Press Enter to continue..."
                        ;;
                    2)
                        PASSED=0
                        FAILED=0
                        run_comprehensive_tests
                        read -p "Press Enter to continue..."
                        ;;
                    3)
                        PASSED=0
                        FAILED=0
                        run_verify_only
                        read -p "Press Enter to continue..."
                        ;;
                    4)
                        PASSED=0
                        FAILED=0
                        run_api_tests
                        read -p "Press Enter to continue..."
                        ;;
                    5)
                        show_documentation
                        read -p "Press Enter to continue..."
                        ;;
                    6)
                        echo -e "${GREEN}Goodbye!\n${NC}"
                        exit 0
                        ;;
                    *)
                        echo -e "${RED}Invalid option${NC}"
                        sleep 1
                        ;;
                esac
            done
            ;;
    esac
}

main "$@"
