# ============================================
# TEST SCRIPT FOR UC101 - MESSAGING FEATURE
# Nhan tin giua nguoi dung va cong ty cuu ho
# ============================================

# Fix UTF-8 encoding
chcp 65001 > $null
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8

$BaseUrl = "http://localhost:8080/v1"

Write-Host "=========================================="
Write-Host "   TEST UC101 - HE THONG NHAN TIN"
Write-Host "=========================================="
Write-Host ""

# Variables to store tokens and IDs
$UserToken = $null
$CompanyToken = $null
$RequestId = $null
$ConversationId = $null

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
                $errorBody = $reader.ReadToEnd()
                $reader.Close()
                Write-Host "   Details: $errorBody"
            } catch {}
        }
        return $null
    }
}

# ============================================
# TEST 1: Login as USER
# ============================================
Write-Host ""
Write-Host "===== TEST 1: Dang nhap voi tai khoan USER ====="

$loginBody = @{
    username = "user1"
    password = "password123"
} | ConvertTo-Json

$UserToken = Test-ApiCall -Name "Login as User" -TestScript {
    $response = Invoke-RestMethod `
        -Uri "$BaseUrl/api/auth/login" `
        -Method POST `
        -ContentType "application/json; charset=utf-8" `
        -Body $loginBody `
        -ErrorAction Stop
    
    if ($response.data.token) {
        Write-Host "   Token received successfully"
        return $response.data.token
    }
    throw "No token received"
}

if (-not $UserToken) {
    Write-Host "[WARN] Cannot proceed without user token. Creating test user..."
    
    $registerBody = @{
        username = "testuser_uc101"
        password = "Test123456"
        fullName = "Test User UC101"
        phoneNumber = "0901234567"
        email = "testuser_uc101@test.com"
    } | ConvertTo-Json
    
    try {
        $regResponse = Invoke-RestMethod `
            -Uri "$BaseUrl/api/auth/register" `
            -Method POST `
            -ContentType "application/json; charset=utf-8" `
            -Body $registerBody `
            -ErrorAction Stop
        
        Write-Host "[PASS] Test user created"
        
        $loginBody = @{
            username = "testuser_uc101"
            password = "Test123456"
        } | ConvertTo-Json
        
        $loginResponse = Invoke-RestMethod `
            -Uri "$BaseUrl/api/auth/login" `
            -Method POST `
            -ContentType "application/json; charset=utf-8" `
            -Body $loginBody `
            -ErrorAction Stop
        
        $UserToken = $loginResponse.data.token
        Write-Host "[PASS] Logged in with test user"
    }
    catch {
        Write-Host "[FAIL] Failed to create/login test user: $($_.Exception.Message)"
    }
}

# ============================================
# TEST 2: Login as COMPANY
# ============================================
Write-Host ""
Write-Host "===== TEST 2: Dang nhap voi tai khoan COMPANY ====="

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

# ============================================
# TEST 3: Get Messages without Token (should fail)
# ============================================
Write-Host ""
Write-Host "===== TEST 3: Goi API messages khi CHUA dang nhap ====="

try {
    Invoke-RestMethod `
        -Uri "$BaseUrl/api/messages/1" `
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
# TEST 4: Get All Conversations (should work even if empty)
# ============================================
Write-Host ""
Write-Host "===== TEST 4: Lay danh sach tat ca cuoc hoi thoai ====="

if ($UserToken) {
    $headers = @{
        Authorization = "Bearer $UserToken"
    }
    
    Test-ApiCall -Name "Get all conversations" -TestScript {
        $response = Invoke-RestMethod `
            -Uri "$BaseUrl/api/messages/conversations" `
            -Method GET `
            -Headers $headers `
            -ErrorAction Stop
        
        Write-Host "   Total conversations: $($response.data.Count)"
        foreach ($conv in $response.data) {
            Write-Host "   - Request #$($conv.requestId): $($conv.status)"
        }
        return $response.data
    }
}

# ============================================
# TEST 5: Get Unread Count
# ============================================
Write-Host ""
Write-Host "===== TEST 5: Dem so tin nhan chua doc ====="

if ($UserToken) {
    $headers = @{
        Authorization = "Bearer $UserToken"
    }
    
    Test-ApiCall -Name "Get unread count" -TestScript {
        $response = Invoke-RestMethod `
            -Uri "$BaseUrl/api/messages/unread-count" `
            -Method GET `
            -Headers $headers `
            -ErrorAction Stop
        
        Write-Host "   Unread messages: $($response.data)"
        return $response.data
    }
}

# ============================================
# TEST 6: Create Rescue Request for Messaging
# ============================================
Write-Host ""
Write-Host "===== TEST 6: Tao yeu cau cuu ho de test messaging ====="

if ($UserToken) {
    $headers = @{
        Authorization = "Bearer $UserToken"
    }
    
    # Always create a new request for clean test state
    $createRequestBody = @{
        location = "123 Test Street, Hanoi"
        latitude = 21.0285
        longitude = 105.8542
        description = "Test rescue request for UC101 messaging"
        vehicleType = "CAR"
    } | ConvertTo-Json
    
    try {
        $createResponse = Invoke-RestMethod `
            -Uri "$BaseUrl/api/rescue-requests" `
            -Method POST `
            -ContentType "application/json; charset=utf-8" `
            -Headers $headers `
            -Body $createRequestBody `
            -ErrorAction Stop
        
        $RequestId = $createResponse.data.id
        Write-Host "[PASS] Created new request ID: $RequestId"
        Write-Host "   Status: $($createResponse.data.status)"
    }
    catch {
        Write-Host "[FAIL] Could not create request: $($_.Exception.Message)"
        # Try to get an existing request
        try {
            $requestsResponse = Invoke-RestMethod `
                -Uri "$BaseUrl/api/rescue-requests/my-requests" `
                -Method GET `
                -Headers $headers `
                -ErrorAction Stop
            
            if ($requestsResponse.data -and $requestsResponse.data.Count -gt 0) {
                $RequestId = $requestsResponse.data[0].id
                Write-Host "[INFO] Using existing request ID: $RequestId"
            }
        }
        catch {
            Write-Host "[FAIL] No requests available for testing"
        }
    }
}

# ============================================
# TEST 6.5: Company Accepts Rescue Request
# ============================================
Write-Host ""
Write-Host "===== TEST 6.5: Company chap nhan yeu cau cuu ho ====="

if ($CompanyToken -and $RequestId) {
    $headers = @{
        Authorization = "Bearer $CompanyToken"
    }
    
    Test-ApiCall -Name "Company accepts rescue request" -TestScript {
        $response = Invoke-RestMethod `
            -Uri "$BaseUrl/api/rescue-requests/$RequestId/accept" `
            -Method POST `
            -Headers $headers `
            -ErrorAction Stop
        
        Write-Host "   Request Status: $($response.data.status)"
        Write-Host "   Company assigned successfully"
        return $response.data
    }
}

# ============================================
# TEST 7: Create/Get Conversation
# ============================================
Write-Host ""
Write-Host "===== TEST 7: Tao/Lay cuoc hoi thoai (UC101 Step 1) ====="

if ($UserToken -and $RequestId) {
    $headers = @{
        Authorization = "Bearer $UserToken"
    }
    
    $ConversationId = Test-ApiCall -Name "Create/Get Conversation" -TestScript {
        $response = Invoke-RestMethod `
            -Uri "$BaseUrl/api/messages/conversation/$RequestId" `
            -Method POST `
            -Headers $headers `
            -ErrorAction Stop
        
        Write-Host "   Conversation ID: $($response.data.id)"
        Write-Host "   Status: $($response.data.status)"
        return $response.data.id
    }
}

# ============================================
# TEST 8: Send Message as User (UC101 Step 3)
# ============================================
Write-Host ""
Write-Host "===== TEST 8: User gui tin nhan (UC101 Step 3) ====="

if ($UserToken -and $RequestId) {
    $headers = @{
        Authorization = "Bearer $UserToken"
    }
    
    $messageBody = @{
        requestId = $RequestId
        content = "Xin chao, toi can ho tro cuu ho xe tai vi tri da gui."
    } | ConvertTo-Json -Depth 5
    
    Test-ApiCall -Name "User sends message" -TestScript {
        $response = Invoke-RestMethod `
            -Uri "$BaseUrl/api/messages" `
            -Method POST `
            -ContentType "application/json; charset=utf-8" `
            -Headers $headers `
            -Body $messageBody `
            -ErrorAction Stop
        
        Write-Host "   Message ID: $($response.data.id)"
        Write-Host "   Content: $($response.data.content)"
        return $response.data
    }
}

# ============================================
# TEST 9: Send Message as Company
# ============================================
Write-Host ""
Write-Host "===== TEST 9: Company gui tin nhan phan hoi ====="

if ($CompanyToken -and $RequestId) {
    $headers = @{
        Authorization = "Bearer $CompanyToken"
    }
    
    $messageBody = @{
        requestId = $RequestId
        content = "Chao ban, chung toi da nhan duoc yeu cau. Doi cuu ho se den trong 15 phut."
    } | ConvertTo-Json -Depth 5
    
    Test-ApiCall -Name "Company sends message" -TestScript {
        $response = Invoke-RestMethod `
            -Uri "$BaseUrl/api/messages" `
            -Method POST `
            -ContentType "application/json; charset=utf-8" `
            -Headers $headers `
            -Body $messageBody `
            -ErrorAction Stop
        
        Write-Host "   Message ID: $($response.data.id)"
        Write-Host "   Content: $($response.data.content)"
        return $response.data
    }
}

# ============================================
# TEST 10: Get Message History (UC101 Step 2)
# ============================================
Write-Host ""
Write-Host "===== TEST 10: Lay lich su tin nhan (UC101 Step 2) ====="

if ($UserToken -and $RequestId) {
    $headers = @{
        Authorization = "Bearer $UserToken"
    }
    
    Test-ApiCall -Name "Get message history" -TestScript {
        $response = Invoke-RestMethod `
            -Uri "$BaseUrl/api/messages/$RequestId" `
            -Method GET `
            -Headers $headers `
            -ErrorAction Stop
        
        Write-Host "   Conversation ID: $($response.data.id)"
        Write-Host "   Total messages: $($response.data.messages.Count)"
        Write-Host "   Status: $($response.data.status)"
        return $response.data
    }
}

# ============================================
# TEST 11: Update Agreed Cost (UC101 Step 6 & 7)
# ============================================
Write-Host ""
Write-Host "===== TEST 11: Thoa thuan chi phi (UC101 Step 6 & 7) ====="

if ($CompanyToken -and $RequestId) {
    $headers = @{
        Authorization = "Bearer $CompanyToken"
    }
    
    $costBody = @{
        requestId = $RequestId
        agreedCost = 500000
        costNotes = "Chi phi bao gom: cuu ho xe + xang dau"
    } | ConvertTo-Json -Depth 5
    
    Test-ApiCall -Name "Update agreed cost" -TestScript {
        $response = Invoke-RestMethod `
            -Uri "$BaseUrl/api/messages/cost" `
            -Method PUT `
            -ContentType "application/json; charset=utf-8" `
            -Headers $headers `
            -Body $costBody `
            -ErrorAction Stop
        
        Write-Host "   Agreed Cost: $($response.data.agreedCost) VND"
        Write-Host "   Status: $($response.data.status)"
        return $response.data
    }
}

# ============================================
# TEST 12: Mark Messages as Read
# ============================================
Write-Host ""
Write-Host "===== TEST 12: Danh dau tin nhan da doc ====="

if ($UserToken -and $ConversationId) {
    $headers = @{
        Authorization = "Bearer $UserToken"
    }
    
    Test-ApiCall -Name "Mark messages as read" -TestScript {
        $response = Invoke-RestMethod `
            -Uri "$BaseUrl/api/messages/$ConversationId/read" `
            -Method PUT `
            -Headers $headers `
            -ErrorAction Stop
        
        Write-Host "   Messages marked as read"
        return $response.data
    }
}

# ============================================
# SUMMARY
# ============================================
Write-Host ""
Write-Host "=========================================="
Write-Host "   TEST UC101 COMPLETED"
Write-Host "=========================================="
Write-Host ""
Write-Host "APIs tested:"
Write-Host "  POST /api/messages              - Send message"
Write-Host "  GET  /api/messages/{requestId}  - Get message history"
Write-Host "  POST /api/messages/conversation/{requestId} - Create/get conversation"
Write-Host "  PUT  /api/messages/cost         - Update agreed cost"
Write-Host "  PUT  /api/messages/{id}/read    - Mark as read"
Write-Host "  GET  /api/messages/conversations - Get all conversations"
Write-Host "  GET  /api/messages/unread-count - Get unread count"
Write-Host ""
