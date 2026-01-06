# ============================================
# TEST SCRIPT FOR UC405 - CONTENT MODERATION
# Kiem duyet noi dung (Admin)
# ============================================

# Fix UTF-8 encoding
chcp 65001 > $null
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8

$BaseUrl = "http://localhost:8080/v1"

Write-Host "=========================================="
Write-Host "   TEST UC405 - KIEM DUYET NOI DUNG"
Write-Host "=========================================="
Write-Host ""

# Variables to store tokens
$AdminToken = $null
$UserToken = $null
$CompanyToken = $null
$TestPostId = $null
$TestContentId = $null

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

function Test-ApiCallExpectFail {
    param(
        [string]$Name,
        [scriptblock]$TestScript,
        [int]$ExpectedStatusCode = 403
    )
    
    Write-Host "--- $Name ---"
    try {
        $result = & $TestScript
        Write-Host "[FAIL] $Name - Expected to fail but succeeded"
        return $false
    }
    catch {
        $statusCode = $_.Exception.Response.StatusCode.Value__
        if ($statusCode -eq $ExpectedStatusCode) {
            Write-Host "[PASS] $Name - Got expected status code $ExpectedStatusCode"
            return $true
        } else {
            Write-Host "[FAIL] $Name - Expected status $ExpectedStatusCode but got $statusCode"
            return $false
        }
    }
}

# ============================================
# SETUP: Admin account should already exist from DataInitializer
# ============================================
Write-Host ""
Write-Host "===== SETUP: Checking test accounts ====="
Write-Host "   Admin account 'admin' should be created by DataInitializer"
Write-Host "   User account 'user1' should be created by DataInitializer"

# ============================================
# TEST 1: Login as Admin
# ============================================
Write-Host ""
Write-Host "===== TEST 1: Login as ADMIN ====="

$adminLoginBody = @{
    username = "admin"
    password = "admin123"
} | ConvertTo-Json

$AdminToken = Test-ApiCall -Name "Login as Admin" -TestScript {
    $response = Invoke-RestMethod `
        -Uri "$BaseUrl/api/auth/login" `
        -Method POST `
        -ContentType "application/json; charset=utf-8" `
        -Body $adminLoginBody `
        -ErrorAction Stop
    
    if ($response.data.token) {
        Write-Host "   Token received successfully"
        Write-Host "   Role: $($response.data.role)"
        if ($response.data.role -ne "ADMIN") {
            Write-Host "   [WARN] Role is not ADMIN - some tests may fail"
            Write-Host "   Please restart backend to create admin account"
        }
        return $response.data.token
    }
    throw "No token received"
}

if (-not $AdminToken) {
    Write-Host "[ERROR] Cannot login as admin."
    Write-Host "Please make sure backend is running and admin account exists."
    exit 1
}

# ============================================
# TEST 2: Login as regular USER (for negative tests)
# ============================================
Write-Host ""
Write-Host "===== TEST 2: Login as USER (for negative tests) ====="

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
        Write-Host "   Token received, Role: $($response.data.role)"
        return $response.data.token
    }
    throw "No token received"
}

# ============================================
# TEST 2.5: Create a community post to generate moderation content
# ============================================
Write-Host ""
Write-Host "===== TEST 2.5: Create community post for moderation ====="

if ($UserToken) {
    $userHeaders = @{
        Authorization = "Bearer $UserToken"
    }

    $postBody = @{
        title = "Test Post for Moderation - $(Get-Date -Format 'HHmmss')"
        content = "This is a test post content to verify content moderation sync"
        incidentType = "ACCIDENT"
        location = "Test Location"
        latitude = 10.762622
        longitude = 106.660172
    } | ConvertTo-Json -Compress

    $TestPostId = Test-ApiCall -Name "POST /api/community/posts" -TestScript {
        $response = Invoke-RestMethod `
            -Uri "$BaseUrl/api/community/posts" `
            -Method POST `
            -Headers $userHeaders `
            -ContentType "application/json; charset=utf-8" `
            -Body $postBody `
            -ErrorAction Stop
        
        Write-Host "   Post created successfully"
        if ($response.id) {
            Write-Host "   Post ID: $($response.id)"
            return $response.id
        } elseif ($response.data -and $response.data.id) {
            Write-Host "   Post ID: $($response.data.id)"
            return $response.data.id
        }
        throw "No post ID received"
    }

    if ($TestPostId) {
        Write-Host "[INFO] Post created with ID: $TestPostId - should now appear in moderation queue"
    }
} else {
    Write-Host "[SKIP] No user token available"
}

# ============================================
# TEST 3: Access moderation API without token (Should fail)
# ============================================
Write-Host ""
Write-Host "===== TEST 3: Access moderation API without token ====="

Test-ApiCallExpectFail -Name "GET /api/admin/moderation without token" -TestScript {
    Invoke-RestMethod `
        -Uri "$BaseUrl/api/admin/moderation" `
        -Method GET `
        -ErrorAction Stop
} -ExpectedStatusCode 403

# ============================================
# TEST 4: Access moderation API with USER token (Should fail)
# ============================================
Write-Host ""
Write-Host "===== TEST 4: Access moderation API with USER token ====="

if ($UserToken) {
    $userHeaders = @{
        Authorization = "Bearer $UserToken"
    }

    Test-ApiCallExpectFail -Name "GET /api/admin/moderation with USER token" -TestScript {
        Invoke-RestMethod `
            -Uri "$BaseUrl/api/admin/moderation" `
            -Method GET `
            -Headers $userHeaders `
            -ErrorAction Stop
    } -ExpectedStatusCode 403
} else {
    Write-Host "[SKIP] No user token available"
}

# ============================================
# TEST 5: Get moderation list (Admin)
# ============================================
Write-Host ""
Write-Host "===== TEST 5: Get moderation list (Admin) ====="

if ($AdminToken) {
    $adminHeaders = @{
        Authorization = "Bearer $AdminToken"
    }

    Test-ApiCall -Name "GET /api/admin/moderation" -TestScript {
        $response = Invoke-RestMethod `
            -Uri "$BaseUrl/api/admin/moderation" `
            -Method GET `
            -Headers $adminHeaders `
            -ErrorAction Stop
        
        Write-Host "   Response received"
        if ($response.items) {
            Write-Host "   Total items: $($response.items.Count)"
        } elseif ($response.data) {
            Write-Host "   Data: $($response.data | ConvertTo-Json -Compress)"
        }
        return $response
    }
} else {
    Write-Host "[SKIP] No admin token available"
}

# ============================================
# TEST 6: Get pending content
# ============================================
Write-Host ""
Write-Host "===== TEST 6: Get pending content ====="

if ($AdminToken) {
    Test-ApiCall -Name "GET /api/admin/moderation/pending" -TestScript {
        $response = Invoke-RestMethod `
            -Uri "$BaseUrl/api/admin/moderation/pending" `
            -Method GET `
            -Headers $adminHeaders `
            -ErrorAction Stop
        
        Write-Host "   Pending content retrieved"
        if ($response.items -and $response.items.Count -gt 0) {
            $script:TestContentId = $response.items[0].id
            Write-Host "   Found $($response.items.Count) pending items"
            Write-Host "   First content ID: $TestContentId"
        }
        return $response
    }
}

# ============================================
# TEST 7: Get moderation statistics
# ============================================
Write-Host ""
Write-Host "===== TEST 7: Get moderation statistics ====="

if ($AdminToken) {
    Test-ApiCall -Name "GET /api/admin/moderation/statistics" -TestScript {
        $response = Invoke-RestMethod `
            -Uri "$BaseUrl/api/admin/moderation/statistics" `
            -Method GET `
            -Headers $adminHeaders `
            -ErrorAction Stop
        
        Write-Host "   Statistics retrieved"
        return $response
    }
}

# ============================================
# TEST 8: Search content
# ============================================
Write-Host ""
Write-Host "===== TEST 8: Search content ====="

if ($AdminToken) {
    Test-ApiCall -Name "GET /api/admin/moderation/search?keyword=test" -TestScript {
        $response = Invoke-RestMethod `
            -Uri "$BaseUrl/api/admin/moderation/search?keyword=test" `
            -Method GET `
            -Headers $adminHeaders `
            -ErrorAction Stop
        
        Write-Host "   Search completed"
        if ($response.items) {
            Write-Host "   Found $($response.items.Count) items matching 'test'"
        }
        return $response
    }
}

# ============================================
# TEST 9: Get moderation list with filters
# ============================================
Write-Host ""
Write-Host "===== TEST 9: Get moderation list with filters ====="

if ($AdminToken) {
    # Filter by status
    Test-ApiCall -Name "GET /api/admin/moderation?status=PENDING" -TestScript {
        $response = Invoke-RestMethod `
            -Uri "$BaseUrl/api/admin/moderation?status=PENDING" `
            -Method GET `
            -Headers $adminHeaders `
            -ErrorAction Stop
        
        Write-Host "   Filtered by status=PENDING"
        return $response
    }

    # Filter by type
    Test-ApiCall -Name "GET /api/admin/moderation?type=POST" -TestScript {
        $response = Invoke-RestMethod `
            -Uri "$BaseUrl/api/admin/moderation?type=POST" `
            -Method GET `
            -Headers $adminHeaders `
            -ErrorAction Stop
        
        Write-Host "   Filtered by type=POST"
        return $response
    }

    # Pagination
    Test-ApiCall -Name "GET /api/admin/moderation?page=0&size=5" -TestScript {
        $response = Invoke-RestMethod `
            -Uri "$BaseUrl/api/admin/moderation?page=0&size=5" `
            -Method GET `
            -Headers $adminHeaders `
            -ErrorAction Stop
        
        Write-Host "   Paginated results (page 0, size 5)"
        return $response
    }
}

# ============================================
# TEST 10: Create a test post for moderation testing
# ============================================
Write-Host ""
Write-Host "===== TEST 10: Create test post for moderation ====="

if ($UserToken) {
    $userHeaders = @{
        Authorization = "Bearer $UserToken"
    }
    
    $postBody = @{
        title = "Test Post for UC405 Moderation - $(Get-Date -Format 'yyyyMMdd_HHmmss')"
        content = "This is a test post created to test content moderation functionality."
    } | ConvertTo-Json

    $TestPostId = Test-ApiCall -Name "POST /api/community/posts" -TestScript {
        $response = Invoke-RestMethod `
            -Uri "$BaseUrl/api/community/posts" `
            -Method POST `
            -ContentType "application/json; charset=utf-8" `
            -Headers $userHeaders `
            -Body $postBody `
            -ErrorAction Stop
        
        if ($response.data -and $response.data.id) {
            Write-Host "   Post created with ID: $($response.data.id)"
            return $response.data.id
        }
        return $null
    }
}

# ============================================
# TEST 11: Get content detail (if we have a content ID)
# ============================================
Write-Host ""
Write-Host "===== TEST 11: Get content detail ====="

if ($AdminToken -and $TestContentId) {
    Test-ApiCall -Name "GET /api/admin/moderation/$TestContentId" -TestScript {
        $response = Invoke-RestMethod `
            -Uri "$BaseUrl/api/admin/moderation/$TestContentId" `
            -Method GET `
            -Headers $adminHeaders `
            -ErrorAction Stop
        
        Write-Host "   Content detail retrieved"
        Write-Host "   Content type: $($response.contentType)"
        Write-Host "   Status: $($response.status)"
        return $response
    }
} else {
    Write-Host "[SKIP] No content ID available for detail test"
}

# ============================================
# TEST 12: Approve content (if we have content)
# ============================================
Write-Host ""
Write-Host "===== TEST 12: Approve content ====="

if ($AdminToken -and $TestContentId) {
    Test-ApiCall -Name "POST /api/admin/moderation/$TestContentId/approve" -TestScript {
        $response = Invoke-RestMethod `
            -Uri "$BaseUrl/api/admin/moderation/$TestContentId/approve" `
            -Method POST `
            -Headers $adminHeaders `
            -ErrorAction Stop
        
        Write-Host "   Content approved"
        Write-Host "   New status: $($response.status)"
        return $response
    }
} else {
    Write-Host "[SKIP] No content ID available for approve test"
}

# ============================================
# TEST 13: Reject content (using PUT with action)
# ============================================
Write-Host ""
Write-Host "===== TEST 13: Test reject/remove flow ====="

if ($AdminToken -and $TestPostId) {
    # First get the moderation content ID for the test post
    Test-ApiCall -Name "Find and reject test post" -TestScript {
        # Search for content
        $searchResponse = Invoke-RestMethod `
            -Uri "$BaseUrl/api/admin/moderation/search?keyword=UC405" `
            -Method GET `
            -Headers $adminHeaders `
            -ErrorAction Stop
        
        if ($searchResponse.items -and $searchResponse.items.Count -gt 0) {
            $contentToReject = $searchResponse.items[0].id
            Write-Host "   Found content to reject: $contentToReject"
            
            # Reject the content
            $rejectBody = @{
                reason = "Test rejection for UC405"
            } | ConvertTo-Json
            
            $rejectResponse = Invoke-RestMethod `
                -Uri "$BaseUrl/api/admin/moderation/$contentToReject/reject" `
                -Method POST `
                -ContentType "application/json; charset=utf-8" `
                -Headers $adminHeaders `
                -Body $rejectBody `
                -ErrorAction Stop
            
            Write-Host "   Content rejected successfully"
            return $rejectResponse
        } else {
            Write-Host "   No content found to reject"
        }
        return $null
    }
}

# ============================================
# TEST 14: Test remove content
# ============================================
Write-Host ""
Write-Host "===== TEST 14: Test remove content ====="

if ($AdminToken) {
    # Create a new post to test removal
    if ($UserToken) {
        $postBody = @{
            title = "Post to be removed - $(Get-Date -Format 'yyyyMMdd_HHmmss')"
            content = "This post will be tested for removal."
        } | ConvertTo-Json

        $userHeaders = @{
            Authorization = "Bearer $UserToken"
        }

        try {
            $createResponse = Invoke-RestMethod `
                -Uri "$BaseUrl/api/community/posts" `
                -Method POST `
                -ContentType "application/json; charset=utf-8" `
                -Headers $userHeaders `
                -Body $postBody `
                -ErrorAction Stop
            
            if ($createResponse.data.id) {
                $postToRemove = $createResponse.data.id
                Write-Host "   Created post for removal test: $postToRemove"
                
                # Get content ID from moderation list
                Start-Sleep -Seconds 1
                
                $searchResp = Invoke-RestMethod `
                    -Uri "$BaseUrl/api/admin/moderation?type=POST&size=50" `
                    -Method GET `
                    -Headers $adminHeaders `
                    -ErrorAction SilentlyContinue
                
                if ($searchResp.items) {
                    $contentToRemove = $searchResp.items | Where-Object { $_.title -like "*removed*" } | Select-Object -First 1
                    if ($contentToRemove) {
                        $removeBody = @{
                            reason = "Removal test for UC405"
                        } | ConvertTo-Json
                        
                        Test-ApiCall -Name "POST /api/admin/moderation/$($contentToRemove.id)/remove" -TestScript {
                            $response = Invoke-RestMethod `
                                -Uri "$BaseUrl/api/admin/moderation/$($contentToRemove.id)/remove" `
                                -Method POST `
                                -ContentType "application/json; charset=utf-8" `
                                -Headers $adminHeaders `
                                -Body $removeBody `
                                -ErrorAction Stop
                            
                            Write-Host "   Content removed successfully"
                            return $response
                        }
                    }
                }
            }
        } catch {
            Write-Host "   Could not create test post for removal"
        }
    }
}

# ============================================
# TEST 15: Delete content permanently
# ============================================
Write-Host ""
Write-Host "===== TEST 15: Delete content permanently ====="

if ($AdminToken) {
    # Create another test post
    if ($UserToken) {
        $postBody = @{
            title = "Post to be deleted permanently - $(Get-Date -Format 'yyyyMMdd_HHmmss')"
            content = "This post will be deleted permanently."
        } | ConvertTo-Json

        $userHeaders = @{
            Authorization = "Bearer $UserToken"
        }

        try {
            $createResponse = Invoke-RestMethod `
                -Uri "$BaseUrl/api/community/posts" `
                -Method POST `
                -ContentType "application/json; charset=utf-8" `
                -Headers $userHeaders `
                -Body $postBody `
                -ErrorAction Stop
            
            if ($createResponse.data.id) {
                Write-Host "   Created post for deletion test"
                Start-Sleep -Seconds 1
                
                # Find and delete
                $searchResp = Invoke-RestMethod `
                    -Uri "$BaseUrl/api/admin/moderation?type=POST&size=50" `
                    -Method GET `
                    -Headers $adminHeaders `
                    -ErrorAction SilentlyContinue
                
                if ($searchResp.items) {
                    $contentToDelete = $searchResp.items | Where-Object { $_.title -like "*deleted permanently*" } | Select-Object -First 1
                    if ($contentToDelete) {
                        Test-ApiCall -Name "DELETE /api/admin/moderation/$($contentToDelete.id)" -TestScript {
                            $response = Invoke-RestMethod `
                                -Uri "$BaseUrl/api/admin/moderation/$($contentToDelete.id)" `
                                -Method DELETE `
                                -Headers $adminHeaders `
                                -ErrorAction Stop
                            
                            Write-Host "   Content deleted permanently"
                            return $response
                        }
                    }
                }
            }
        } catch {
            Write-Host "   Could not create test post for deletion"
        }
    }
}

# ============================================
# TEST 16: Invalid content ID
# ============================================
Write-Host ""
Write-Host "===== TEST 16: Invalid content ID ====="

if ($AdminToken) {
    Test-ApiCallExpectFail -Name "GET /api/admin/moderation/999999 (invalid ID)" -TestScript {
        Invoke-RestMethod `
            -Uri "$BaseUrl/api/admin/moderation/999999" `
            -Method GET `
            -Headers $adminHeaders `
            -ErrorAction Stop
    } -ExpectedStatusCode 404
}

# ============================================
# SUMMARY
# ============================================
Write-Host ""
Write-Host "=========================================="
Write-Host "   TEST UC405 COMPLETED"
Write-Host "=========================================="
Write-Host ""
Write-Host "Notes:"
Write-Host "- UC405 tests Content Moderation for Admin"
Write-Host "- Admin can view, approve, reject, remove and delete content"
Write-Host "- Regular users should not access admin moderation APIs"
Write-Host ""
Write-Host "If you see 403 errors on admin-only endpoints:"
Write-Host "  1. Make sure admin account has ADMIN role in database"
Write-Host "  2. Run: UPDATE accounts SET role='ADMIN' WHERE username='admin_test_uc405';"
Write-Host ""
