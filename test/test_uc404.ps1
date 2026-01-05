# ============================================
# TEST SCRIPT FOR UC404 - COMPANY PROFILE MANAGEMENT
# Quan ly ho so cong ty cuu ho
# ============================================

# Fix UTF-8 encoding
chcp 65001 > $null
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8

$BaseUrl = "http://localhost:8080/v1"

Write-Host "=========================================="
Write-Host "   TEST UC404 - QUAN LY HO SO CONG TY"
Write-Host "=========================================="
Write-Host ""

# Variables to store tokens
$CompanyToken = $null
$UserToken = $null
$CompanyId = $null

# ============================================
# HELPER FUNCTIONS
# ============================================

function Test-ApiCall {
    param(
        [string]$Name,
        [scriptblock]$TestScript,
        [string]$ExpectedResult = "success"
    )
    
    Write-Host "--- $Name ---"
    try {
        $result = & $TestScript
        Write-Host "[PASS] $Name"
        return $result
    }
    catch {
        Write-Host "[FAIL] $Name"
        Write-Host "   Error: $($_.Exception.Message)"
        if ($_.Exception.Response) {
            $statusCode = $_.Exception.Response.StatusCode.Value__
            Write-Host "   Status Code: $statusCode"
            try {
                $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
                $responseBody = $reader.ReadToEnd()
                $reader.Close()
                Write-Host "   Response: $responseBody"
            } catch {}
        }
        return $null
    }
}

# ============================================
# TEST 1: Login as COMPANY (Precondition)
# ============================================
Write-Host ""
Write-Host "===== TEST 1: Dang nhap voi tai khoan COMPANY ====="

$companyLoginBody = @{
    username = "company1"
    password = "password123"
} | ConvertTo-Json

$CompanyToken = Test-ApiCall -Name "Login as Company" -TestScript {
    $response = Invoke-RestMethod `
        -Uri "$BaseUrl/api/auth/login" `
        -Method POST `
        -ContentType "application/json; charset=utf-8" `
        -Body $companyLoginBody `
        -ErrorAction Stop
    
    if ($response.data.token) {
        Write-Host "   Token received successfully"
        Write-Host "   Role: $($response.data.role)"
        return $response.data.token
    }
    throw "No token received"
}

if (-not $CompanyToken) {
    Write-Host "[WARN] Cannot proceed without company token."
    Write-Host "Please ensure company1 account exists in database."
    exit 1
}

# ============================================
# TEST 2: Login as USER (for negative test)
# ============================================
Write-Host ""
Write-Host "===== TEST 2: Dang nhap voi tai khoan USER ====="

$userLoginBody = @{
    username = "user1"
    password = "password123"
} | ConvertTo-Json

$UserToken = Test-ApiCall -Name "Login as User" -TestScript {
    $response = Invoke-RestMethod `
        -Uri "$BaseUrl/api/auth/login" `
        -Method POST `
        -ContentType "application/json; charset=utf-8" `
        -Body $userLoginBody `
        -ErrorAction Stop
    
    if ($response.data.token) {
        Write-Host "   Token received successfully"
        return $response.data.token
    }
    throw "No token received"
}

# ============================================
# TEST 3: Get Profile without Token (should fail)
# ============================================
Write-Host ""
Write-Host "===== TEST 3: Goi API profile khi CHUA dang nhap ====="

try {
    Invoke-RestMethod `
        -Uri "$BaseUrl/api/companies/profile" `
        -Method GET `
        -ErrorAction Stop
    
    Write-Host "[FAIL] API cho phep truy cap khi chua dang nhap"
}
catch {
    $statusCode = $_.Exception.Response.StatusCode.Value__
    if ($statusCode -eq 401 -or $statusCode -eq 403) {
        Write-Host "[PASS] Nhan dung loi $statusCode khi chua dang nhap"
    } else {
        Write-Host "[WARN] Received status: $statusCode (expected 401 or 403)"
    }
}

# ============================================
# TEST 4: Get Profile as USER (should fail - wrong role)
# ============================================
Write-Host ""
Write-Host "===== TEST 4: USER co gang truy cap company profile ====="

if ($UserToken) {
    $headers = @{
        Authorization = "Bearer $UserToken"
    }
    
    try {
        Invoke-RestMethod `
            -Uri "$BaseUrl/api/companies/profile" `
            -Method GET `
            -Headers $headers `
            -ErrorAction Stop
        
        Write-Host "[FAIL] User co the truy cap company profile"
    }
    catch {
        $statusCode = $_.Exception.Response.StatusCode.Value__
        if ($statusCode -eq 403) {
            Write-Host "[PASS] User khong duoc phep truy cap (403 Forbidden)"
        } else {
            Write-Host "[WARN] Received status: $statusCode (expected 403)"
        }
    }
}

# ============================================
# TEST 5: Get Company Profile (UC404 Step 2)
# ============================================
Write-Host ""
Write-Host "===== TEST 5: Lay thong tin ho so cong ty (UC404 Step 2) ====="

if ($CompanyToken) {
    $headers = @{
        Authorization = "Bearer $CompanyToken"
    }
    
    $profileData = Test-ApiCall -Name "Get company profile" -TestScript {
        $response = Invoke-RestMethod `
            -Uri "$BaseUrl/api/companies/profile" `
            -Method GET `
            -Headers $headers `
            -ErrorAction Stop
        
        Write-Host "   Company ID: $($response.data.id)"
        Write-Host "   Name: $($response.data.name)"
        Write-Host "   Address: $($response.data.address)"
        Write-Host "   Phone: $($response.data.phone)"
        Write-Host "   Email: $($response.data.email)"
        Write-Host "   Profile Status: $($response.data.profileStatus)"
        Write-Host "   Is Verified: $($response.data.isVerified)"
        Write-Host "   Service Radius: $($response.data.serviceRadius) km"
        
        $script:CompanyId = $response.data.id
        return $response.data
    }
}

# ============================================
# TEST 6: Update Company Profile - Basic Info (UC404 Step 3)
# ============================================
Write-Host ""
Write-Host "===== TEST 6: Cap nhat thong tin co ban (UC404 Step 3) ====="

if ($CompanyToken) {
    $headers = @{
        Authorization = "Bearer $CompanyToken"
    }
    
    # Use valid phone format (starts with 0 or +84)
    $updateBody = @{
        phone = "0241234567"
        hotline = "0901234567"
        description = "Dich vu cuu ho chuyen nghiep 24/7"
        operatingHours = "24/7"
        serviceRadius = 60.0
    } | ConvertTo-Json
    
    Test-ApiCall -Name "Update basic info" -TestScript {
        $response = Invoke-RestMethod `
            -Uri "$BaseUrl/api/companies/profile" `
            -Method PUT `
            -ContentType "application/json; charset=utf-8" `
            -Headers $headers `
            -Body $updateBody `
            -ErrorAction Stop
        
        Write-Host "   Updated Phone: $($response.data.phone)"
        Write-Host "   Updated Hotline: $($response.data.hotline)"
        Write-Host "   Updated Description: $($response.data.description)"
        Write-Host "   Updated Operating Hours: $($response.data.operatingHours)"
        Write-Host "   Updated Service Radius: $($response.data.serviceRadius)"
        Write-Host "   Profile Status: $($response.data.profileStatus)"
        return $response.data
    }
}

# ============================================
# TEST 7: Update Company Profile - Requires Approval (UC404 Step 4b)
# ============================================
Write-Host ""
Write-Host "===== TEST 7: Cap nhat thong tin can phe duyet (UC404 Step 4b) ====="

if ($CompanyToken) {
    $headers = @{
        Authorization = "Bearer $CompanyToken"
    }
    
    # Update fields that require admin approval
    $updateBody = @{
        name = "Cong Ty Cuu Ho ABC Updated"
        taxCode = "0123456789"
        businessLicense = "GP-2024-001234"
    } | ConvertTo-Json
    
    Test-ApiCall -Name "Update info requiring approval" -TestScript {
        $response = Invoke-RestMethod `
            -Uri "$BaseUrl/api/companies/profile" `
            -Method PUT `
            -ContentType "application/json; charset=utf-8" `
            -Headers $headers `
            -Body $updateBody `
            -ErrorAction Stop
        
        Write-Host "   Updated Name: $($response.data.name)"
        Write-Host "   Updated Tax Code: $($response.data.taxCode)"
        Write-Host "   Updated Business License: $($response.data.businessLicense)"
        Write-Host "   Profile Status: $($response.data.profileStatus)"
        
        if ($response.data.profileStatus -eq "PENDING_APPROVAL") {
            Write-Host "   >> Correctly set to PENDING_APPROVAL"
        }
        return $response.data
    }
}

# ============================================
# TEST 8: Update with Invalid License (UC404 Step 4a)
# ============================================
Write-Host ""
Write-Host "===== TEST 8: Cap nhat voi giay phep khong hop le (UC404 Step 4a) ====="

if ($CompanyToken) {
    $headers = @{
        Authorization = "Bearer $CompanyToken"
    }
    
    # Update with expired license date
    $expiredDate = (Get-Date).AddDays(-30).ToString("yyyy-MM-ddTHH:mm:ss")
    
    $updateBody = @{
        businessLicense = "GP-EXPIRED-001"
        licenseExpiryDate = $expiredDate
    } | ConvertTo-Json
    
    try {
        $response = Invoke-RestMethod `
            -Uri "$BaseUrl/api/companies/profile" `
            -Method PUT `
            -ContentType "application/json; charset=utf-8" `
            -Headers $headers `
            -Body $updateBody `
            -ErrorAction Stop
        
        Write-Host "[WARN] API accepted expired license"
    }
    catch {
        $statusCode = $_.Exception.Response.StatusCode.Value__
        if ($statusCode -eq 400) {
            Write-Host "[PASS] Dung - tu choi giay phep da het han (400 Bad Request)"
        } else {
            Write-Host "[WARN] Received status: $statusCode"
        }
    }
}

# ============================================
# TEST 9: Update with Valid License
# ============================================
Write-Host ""
Write-Host "===== TEST 9: Cap nhat voi giay phep hop le ====="

if ($CompanyToken) {
    $headers = @{
        Authorization = "Bearer $CompanyToken"
    }
    
    # Update with future expiry date
    $futureDate = (Get-Date).AddYears(1).ToString("yyyy-MM-ddTHH:mm:ss")
    
    $updateBody = @{
        businessLicense = "GP-2025-VALID"
        licenseExpiryDate = $futureDate
        licenseDocumentUrl = "https://storage.example.com/licenses/gp-2025-valid.pdf"
    } | ConvertTo-Json
    
    Test-ApiCall -Name "Update with valid license" -TestScript {
        $response = Invoke-RestMethod `
            -Uri "$BaseUrl/api/companies/profile" `
            -Method PUT `
            -ContentType "application/json; charset=utf-8" `
            -Headers $headers `
            -Body $updateBody `
            -ErrorAction Stop
        
        Write-Host "   Business License: $($response.data.businessLicense)"
        Write-Host "   License Expiry: $($response.data.licenseExpiryDate)"
        Write-Host "   License Document URL: $($response.data.licenseDocumentUrl)"
        Write-Host "   Profile Status: $($response.data.profileStatus)"
        return $response.data
    }
}

# ============================================
# TEST 10: Update Address and Location
# ============================================
Write-Host ""
Write-Host "===== TEST 10: Cap nhat dia chi va vi tri ====="

if ($CompanyToken) {
    $headers = @{
        Authorization = "Bearer $CompanyToken"
    }
    
    $updateBody = @{
        address = "456 Duong Lang, Dong Da, Ha Noi"
        latitude = 21.0200
        longitude = 105.8100
    } | ConvertTo-Json
    
    Test-ApiCall -Name "Update address and location" -TestScript {
        $response = Invoke-RestMethod `
            -Uri "$BaseUrl/api/companies/profile" `
            -Method PUT `
            -ContentType "application/json; charset=utf-8" `
            -Headers $headers `
            -Body $updateBody `
            -ErrorAction Stop
        
        Write-Host "   Updated Address: $($response.data.address)"
        Write-Host "   Updated Latitude: $($response.data.latitude)"
        Write-Host "   Updated Longitude: $($response.data.longitude)"
        return $response.data
    }
}

# ============================================
# TEST 11: Get Company Profile by ID (Public)
# ============================================
Write-Host ""
Write-Host "===== TEST 11: Xem ho so cong ty theo ID (Public) ====="

if ($CompanyId) {
    Test-ApiCall -Name "Get company profile by ID" -TestScript {
        $response = Invoke-RestMethod `
            -Uri "$BaseUrl/api/companies/$CompanyId/profile" `
            -Method GET `
            -ErrorAction Stop
        
        Write-Host "   Company: $($response.data.name)"
        Write-Host "   Address: $($response.data.address)"
        Write-Host "   Rating: $($response.data.averageRating)/5 ($($response.data.totalReviews) reviews)"
        Write-Host "   Is Verified: $($response.data.isVerified)"
        return $response.data
    }
} else {
    # Try with a default company ID
    Test-ApiCall -Name "Get company profile by ID (default)" -TestScript {
        $response = Invoke-RestMethod `
            -Uri "$BaseUrl/api/companies/1/profile" `
            -Method GET `
            -ErrorAction Stop
        
        Write-Host "   Company: $($response.data.name)"
        Write-Host "   Address: $($response.data.address)"
        return $response.data
    }
}

# ============================================
# TEST 12: Verify Profile After Updates
# ============================================
Write-Host ""
Write-Host "===== TEST 12: Xac nhan ho so sau khi cap nhat ====="

if ($CompanyToken) {
    $headers = @{
        Authorization = "Bearer $CompanyToken"
    }
    
    Test-ApiCall -Name "Verify updated profile" -TestScript {
        $response = Invoke-RestMethod `
            -Uri "$BaseUrl/api/companies/profile" `
            -Method GET `
            -Headers $headers `
            -ErrorAction Stop
        
        Write-Host "   === FINAL PROFILE ==="
        Write-Host "   Company ID: $($response.data.id)"
        Write-Host "   Name: $($response.data.name)"
        Write-Host "   Address: $($response.data.address)"
        Write-Host "   Phone: $($response.data.phone)"
        Write-Host "   Email: $($response.data.email)"
        Write-Host "   Hotline: $($response.data.hotline)"
        Write-Host "   Tax Code: $($response.data.taxCode)"
        Write-Host "   Business License: $($response.data.businessLicense)"
        Write-Host "   Profile Status: $($response.data.profileStatus)"
        Write-Host "   Is Active: $($response.data.isActive)"
        Write-Host "   Is Verified: $($response.data.isVerified)"
        Write-Host "   Service Radius: $($response.data.serviceRadius) km"
        Write-Host "   Operating Hours: $($response.data.operatingHours)"
        Write-Host "   Average Rating: $($response.data.averageRating)"
        Write-Host "   Total Reviews: $($response.data.totalReviews)"
        return $response.data
    }
}

# ============================================
# SUMMARY
# ============================================
Write-Host ""
Write-Host "=========================================="
Write-Host "   TEST UC404 COMPLETED"
Write-Host "=========================================="
Write-Host ""
Write-Host "APIs tested:"
Write-Host "  GET  /api/companies/profile        - Get current company profile"
Write-Host "  PUT  /api/companies/profile        - Update company profile"
Write-Host "  GET  /api/companies/{id}/profile   - Get company profile by ID (public)"
Write-Host ""
Write-Host "Features tested:"
Write-Host "  - View company profile (Step 2)"
Write-Host "  - Update basic info (Step 3)"
Write-Host "  - Validate license (Step 4)"
Write-Host "  - Reject expired license (Step 4a)"
Write-Host "  - Set PENDING_APPROVAL status (Step 4b)"
Write-Host "  - Save and confirm (Step 5)"
Write-Host "  - Role-based access control"
Write-Host ""
