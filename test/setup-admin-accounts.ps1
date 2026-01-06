# ============================================
# SETUP ADMIN ACCOUNTS FOR UC304 & UC405 TESTING
# Run this script BEFORE running UC304/UC405 tests
# ============================================

# Fix UTF-8 encoding
chcp 65001 > $null
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8

$BaseUrl = "http://localhost:8080/v1"

Write-Host "=========================================="
Write-Host "   SETUP ADMIN ACCOUNTS FOR TESTING"
Write-Host "=========================================="
Write-Host ""

# ============================================
# Step 1: Register admin accounts
# ============================================
Write-Host "===== Step 1: Registering admin accounts ====="

$adminAccounts = @(
    @{
        username = "admin_test_uc405"
        password = "password123"
        fullName = "Admin Test UC405"
        phoneNumber = "0900000405"
        email = "admin_uc405@test.com"
    },
    @{
        username = "admin_test_uc304"
        password = "password123"
        fullName = "Admin Test UC304"
        phoneNumber = "0900000304"
        email = "admin_uc304@test.com"
    },
    @{
        username = "admin"
        password = "admin123"
        fullName = "System Administrator"
        phoneNumber = "0900000001"
        email = "admin@resqonroad.vn"
    }
)

foreach ($admin in $adminAccounts) {
    $body = $admin | ConvertTo-Json
    try {
        $response = Invoke-RestMethod `
            -Uri "$BaseUrl/api/auth/register" `
            -Method POST `
            -ContentType "application/json; charset=utf-8" `
            -Body $body `
            -ErrorAction Stop
        
        Write-Host "[OK] Registered: $($admin.username)"
    } catch {
        Write-Host "[INFO] $($admin.username) may already exist or error: $($_.Exception.Message)"
    }
}

# ============================================
# Step 2: Instructions for setting admin role
# ============================================
Write-Host ""
Write-Host "=========================================="
Write-Host "   MANUAL STEP REQUIRED"
Write-Host "=========================================="
Write-Host ""
Write-Host "You need to manually update the role to ADMIN in the database."
Write-Host ""
Write-Host "Option 1: Use H2 Console (if enabled)"
Write-Host "   1. Open http://localhost:8080/h2-console"
Write-Host "   2. Connect to the database"
Write-Host "   3. Run these SQL commands:"
Write-Host ""
Write-Host "   UPDATE accounts SET role='ADMIN' WHERE username='admin_test_uc405';"
Write-Host "   UPDATE accounts SET role='ADMIN' WHERE username='admin_test_uc304';"
Write-Host "   UPDATE accounts SET role='ADMIN' WHERE username='admin';"
Write-Host ""
Write-Host "Option 2: Use SQLite CLI"
Write-Host "   sqlite3 backend/rescue.db"
Write-Host "   UPDATE accounts SET role='ADMIN' WHERE username='admin_test_uc405';"
Write-Host "   UPDATE accounts SET role='ADMIN' WHERE username='admin_test_uc304';"
Write-Host "   UPDATE accounts SET role='ADMIN' WHERE username='admin';"
Write-Host "   .quit"
Write-Host ""
Write-Host "Option 3: Use DBeaver or any SQLite GUI"
Write-Host "   1. Open backend/rescue.db"
Write-Host "   2. Run the UPDATE statements above"
Write-Host ""
Write-Host "=========================================="
Write-Host "After updating roles, run:"
Write-Host "   .\test_uc405.ps1"
Write-Host "   .\test_uc304.ps1"
Write-Host "=========================================="
