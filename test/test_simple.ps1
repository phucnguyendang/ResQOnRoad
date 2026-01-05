# ============================================
# SIMPLE TEST FOR UC101 & UC404
# Test messaging and company profile APIs
# ============================================

# Fix UTF-8 encoding for PowerShell
chcp 65001 > $null
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
$PSDefaultParameterValues['*:Encoding'] = 'utf8'

$BaseUrl = "http://localhost:8080/v1"

Write-Host "=========================================="
Write-Host "   SIMPLE API TESTS - UC101 & UC404"
Write-Host "=========================================="
Write-Host ""

# ============================================
# STEP 1: Register test accounts
# ============================================
Write-Host "===== STEP 1: Registering test accounts ====="

# Register User
$userRegister = @{
    username = "testuser_" + (Get-Random -Maximum 9999)
    password = "Test123456"
    fullName = "Test User"
    phoneNumber = "0901234567"
    email = "testuser@test.com"
} | ConvertTo-Json

try {
    $regResponse = Invoke-RestMethod `
        -Uri "$BaseUrl/api/auth/register" `
        -Method POST `
        -ContentType "application/json" `
        -Body $userRegister `
        -ErrorAction Stop
    
    Write-Host "User registered with ID: $($regResponse.data)"
    $script:UserId = $regResponse.data
}
catch {
    Write-Host "User registration failed: $($_.Exception.Message)"
    Write-Host "Continuing with existing accounts..."
}

Write-Host ""

# ============================================
# STEP 2: Login as User
# ============================================
Write-Host "===== STEP 2: Login as User ====="

$loginBody = @{
    username = ($userRegister | ConvertFrom-Json).username
    password = "Test123456"
} | ConvertTo-Json

try {
    $loginResponse = Invoke-RestMethod `
        -Uri "$BaseUrl/api/auth/login" `
        -Method POST `
        -ContentType "application/json" `
        -Body $loginBody `
        -ErrorAction Stop
    
    $UserToken = $loginResponse.data.token
    Write-Host "Login successful!"
    Write-Host "  Token: $($UserToken.Substring(0, 50))..."
    Write-Host "  Role: $($loginResponse.data.role)"
}
catch {
    Write-Host "Login failed: $($_.Exception.Message)"
    Write-Host ""
    Write-Host "Trying with seed data account (user1/password123)..."
    
    $loginBody = @{
        username = "user1"
        password = "password123"
    } | ConvertTo-Json
    
    try {
        $loginResponse = Invoke-RestMethod `
            -Uri "$BaseUrl/api/auth/login" `
            -Method POST `
            -ContentType "application/json" `
            -Body $loginBody `
            -ErrorAction Stop
        
        $UserToken = $loginResponse.data.token
        Write-Host "Login with user1 successful!"
    }
    catch {
        Write-Host "Login with user1 also failed."
        Write-Host "Please restart backend to apply seed data changes."
    }
}

Write-Host ""

# ============================================
# STEP 3: Test Messages API (UC101)
# ============================================
Write-Host "===== STEP 3: Test Messages API (UC101) ====="

if ($UserToken) {
    $headers = @{
        Authorization = "Bearer $UserToken"
    }
    
    # Test: Get conversations (should work even if empty)
    Write-Host "--- Get all conversations ---"
    try {
        $response = Invoke-RestMethod `
            -Uri "$BaseUrl/api/messages/conversations" `
            -Method GET `
            -Headers $headers `
            -ErrorAction Stop
        
        Write-Host "Conversations count: $($response.data.Count)"
        Write-Host "Status: PASS"
    }
    catch {
        Write-Host "Status: FAIL - $($_.Exception.Message)"
    }
    
    # Test: Get unread count
    Write-Host ""
    Write-Host "--- Get unread message count ---"
    try {
        $response = Invoke-RestMethod `
            -Uri "$BaseUrl/api/messages/unread-count" `
            -Method GET `
            -Headers $headers `
            -ErrorAction Stop
        
        Write-Host "Unread count: $($response.data)"
        Write-Host "Status: PASS"
    }
    catch {
        Write-Host "Status: FAIL - $($_.Exception.Message)"
    }
}
else {
    Write-Host "Skipping - no user token available"
}

Write-Host ""

# ============================================
# STEP 4: Test Company Profile API (UC404)
# ============================================
Write-Host "===== STEP 4: Test Company Profile API (UC404) ====="

# Login as company
$companyLoginBody = @{
    username = "company1"
    password = "password123"
} | ConvertTo-Json

try {
    $loginResponse = Invoke-RestMethod `
        -Uri "$BaseUrl/api/auth/login" `
        -Method POST `
        -ContentType "application/json" `
        -Body $companyLoginBody `
        -ErrorAction Stop
    
    $CompanyToken = $loginResponse.data.token
    Write-Host "Company login successful!"
    Write-Host "  Role: $($loginResponse.data.role)"
    
    $companyHeaders = @{
        Authorization = "Bearer $CompanyToken"
    }
    
    # Test: Get company profile
    Write-Host ""
    Write-Host "--- Get company profile ---"
    try {
        $response = Invoke-RestMethod `
            -Uri "$BaseUrl/api/companies/profile" `
            -Method GET `
            -Headers $companyHeaders `
            -ErrorAction Stop
        
        $companyName = [System.Text.Encoding]::UTF8.GetString([System.Text.Encoding]::Default.GetBytes($response.data.name))
        $address = [System.Text.Encoding]::UTF8.GetString([System.Text.Encoding]::Default.GetBytes($response.data.address))
        
        Write-Host "Company Name: $companyName"
        Write-Host "Address: $address"
        Write-Host "Profile Status: $($response.data.profileStatus)"
        Write-Host "Status: PASS"
    }
    catch {
        $statusCode = $_.Exception.Response.StatusCode.Value__
        Write-Host "Status: FAIL (HTTP $statusCode)"
        Write-Host "Error: $($_.Exception.Message)"
    }
    
    # Test: Update company profile
    Write-Host ""
    Write-Host "--- Update company profile ---"
    $updateBody = @{
        hotline = "0901888999"
        operatingHours = "24/7"
    } | ConvertTo-Json
    
    try {
        $response = Invoke-RestMethod `
            -Uri "$BaseUrl/api/companies/profile" `
            -Method PUT `
            -ContentType "application/json; charset=utf-8" `
            -Headers $companyHeaders `
            -Body $updateBody `
            -ErrorAction Stop
        
        Write-Host "Updated Hotline: $($response.data.hotline)"
        Write-Host "Updated Hours: $($response.data.operatingHours)"
        Write-Host "Status: PASS"
    }
    catch {
        $statusCode = $_.Exception.Response.StatusCode.Value__
        Write-Host "Status: FAIL (HTTP $statusCode)"
        
        # Try to get error details
        try {
            $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
            $errorBody = $reader.ReadToEnd()
            $reader.Close()
            Write-Host "Error details: $errorBody"
        }
        catch {
            Write-Host "Error: $($_.Exception.Message)"
        }
    }
}
catch {
    Write-Host "Company login failed: $($_.Exception.Message)"
    Write-Host ""
    Write-Host "NOTE: Company account 'company1' may not exist yet."
    Write-Host "Please restart backend to apply seed data with company accounts."
}

Write-Host ""

# ============================================
# STEP 5: Test Public Company Profile
# ============================================
Write-Host "===== STEP 5: Test Public Company Profile ====="

Write-Host "--- Get company profile by ID (public) ---"
try {
    $response = Invoke-RestMethod `
        -Uri "$BaseUrl/api/companies/1/profile" `
        -Method GET `
        -ErrorAction Stop
    
    $companyName = [System.Text.Encoding]::UTF8.GetString([System.Text.Encoding]::Default.GetBytes($response.data.name))
    
    Write-Host "Company: $companyName"
    Write-Host "Rating: $($response.data.averageRating)/5"
    Write-Host "Status: PASS"
}
catch {
    $statusCode = $_.Exception.Response.StatusCode.Value__
    Write-Host "Status: FAIL (HTTP $statusCode)"
}

Write-Host ""
Write-Host "=========================================="
Write-Host "   TESTS COMPLETED"
Write-Host "=========================================="
Write-Host ""
Write-Host "If tests failed due to missing accounts:"
Write-Host "1. Stop the backend server"
Write-Host "2. Delete the database file (if using H2/SQLite)"
Write-Host "3. Restart backend: cd backend && mvn spring-boot:run"
Write-Host "4. The seed data will create test accounts automatically"
Write-Host ""
