# ============================================
# TEST RESULTS SUMMARY - UC304 & UC405
# ============================================

## BUGS/ISSUES FOUND

### UC304 - Workflow Management

1. **BUG: User creating workflow returns 500 instead of 403**
   - File: `WorkflowController.java`, Line 232-246
   - When a USER tries to create a workflow (POST /api/workflows), 
     the server returns 500 Internal Server Error instead of 403 Forbidden.
   - Expected: 403 Forbidden with clear error message
   - Actual: 500 Internal Server Error
   - Root Cause: The `@PreAuthorize("hasRole('COMPANY')")` annotation might 
     throw an AccessDeniedException that is being caught and converted to 
     500 by the generic exception handler.
   - Fix: Add proper exception handling for AccessDeniedException in the 
     GlobalExceptionHandler or ensure Spring Security properly returns 403.

2. **Potential Issue: Error handling in WorkflowController**
   - The catch blocks convert all exceptions to ApiException with 
     INTERNAL_SERVER_ERROR, which may mask security-related errors.

### UC405 - Content Moderation

1. **No admin account in seed data**
   - The system does not automatically create an admin account for testing.
   - Requires manual database update to set role='ADMIN'.
   - Recommendation: Add admin account creation in DataInitializer.java

## TEST RESULTS SUMMARY

### UC304 Tests:

| Test | Description | Status |
|------|-------------|--------|
| 1 | Login as USER | ✅ PASS |
| 2 | Login as COMPANY | ✅ PASS |
| 3 | Login as ADMIN | ✅ PASS (but role is USER) |
| 4 | Access workflow API without token | ✅ PASS (403) |
| 5 | User access to admin workflow APIs | ✅ PASS (403) |
| 6 | Company access to admin workflow APIs | ✅ PASS (403) |
| 7-10 | Admin APIs | ⏸️ SKIP (need ADMIN role) |
| 11 | Create rescue request | ✅ PASS |
| 12 | Company - Get my workflows | ✅ PASS |
| 13 | Company - Get active workflows | ✅ PASS |
| 14 | Company - Create workflow | ✅ PASS |
| 15 | Get workflow by ID | ✅ PASS |
| 16 | Get workflow by rescue request ID | ✅ PASS |
| 17 | Company - Add workflow step | ✅ PASS |
| 18 | Get workflow steps | ✅ PASS |
| 19 | Company - Update workflow step | ✅ PASS |
| 20 | Company - Update workflow status | ✅ PASS |
| 21 | User - Get my workflows | ✅ PASS |
| 22 | User should not create workflow | ❌ FAIL (500 vs 403) - **BUG** |
| 23 | Admin - Update workflow status | ⏸️ SKIP (need ADMIN role) |
| 24 | Company - Complete workflow | ✅ PASS |
| 25 | Test cancel workflow | ✅ PASS |
| 26 | Invalid workflow ID | ✅ PASS (404) |

### UC405 Tests:

| Test | Description | Status |
|------|-------------|--------|
| 1 | Login as Admin | ✅ PASS (but role is USER) |
| 2 | Login as User | ✅ PASS |
| 3 | Access moderation API without token | ✅ PASS (403) |
| 4 | Access moderation API with USER token | ✅ PASS (403) |
| 5-9 | Admin moderation APIs | ⏸️ SKIP (need ADMIN role) |
| 10 | Create test post | ✅ PASS |
| 11-15 | Admin moderation actions | ⏸️ SKIP (need ADMIN role) |
| 16 | Invalid content ID | ⏸️ SKIP (need ADMIN role) |

## HOW TO RUN FULL TESTS

1. Setup admin accounts:
   ```powershell
   .\setup-admin-accounts.ps1
   ```

2. Manually update roles in database:
   ```sql
   UPDATE accounts SET role='ADMIN' WHERE username='admin_test_uc405';
   UPDATE accounts SET role='ADMIN' WHERE username='admin_test_uc304';
   ```

3. Run tests:
   ```powershell
   .\test_uc304.ps1
   .\test_uc405.ps1
   ```

## RECOMMENDATIONS

1. **Add admin account in DataInitializer.java**:
   ```java
   if (!accountRepository.existsByUsername("admin")) {
       Account admin = new Account();
       admin.setUsername("admin");
       admin.setPasswordHash(passwordEncoder.encode("admin123"));
       admin.setFullName("System Administrator");
       admin.setPhoneNumber("0900000001");
       admin.setEmail("admin@resqonroad.vn");
       admin.setRole(Role.ADMIN);
       accountRepository.save(admin);
       System.out.println("Created admin account: admin");
   }
   ```

2. **Fix 500 error for unauthorized workflow creation**:
   Add proper exception handling for AccessDeniedException in GlobalExceptionHandler.

3. **Improve error messages**:
   When a user without proper role tries to access restricted endpoints,
   the error message should clearly indicate the required role.
