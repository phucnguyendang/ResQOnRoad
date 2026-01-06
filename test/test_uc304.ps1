# ============================================
# TEST SCRIPT FOR UC304 - WORKFLOW MANAGEMENT
# Quan ly quy trinh cuu ho
# ============================================

# Fix UTF-8 encoding
chcp 65001 > $null
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8

$BaseUrl = "http://localhost:8080/v1"

Write-Host "=========================================="
Write-Host "   TEST UC304 - QUAN LY QUY TRINH"
Write-Host "=========================================="
Write-Host ""

# Variables to store tokens and IDs
$AdminToken = $null
$CompanyToken = $null
$UserToken = $null
$TestRequestId = $null
$TestWorkflowId = $null
$TestStepId = $null

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
# SETUP: All accounts should exist from DataInitializer
# ============================================
Write-Host ""
Write-Host "===== SETUP: Checking test accounts ====="
Write-Host "   Admin account 'admin' should be created by DataInitializer"
Write-Host "   User account 'user1' should be created by DataInitializer"
Write-Host "   Company account 'company1' should be created by DataInitializer"

# ============================================
# TEST 1: Login as User
# ============================================
Write-Host ""
Write-Host "===== TEST 1: Login as USER ====="

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
# TEST 2: Login as Company
# ============================================
Write-Host ""
Write-Host "===== TEST 2: Login as COMPANY ====="

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
        Write-Host "   Token received, Role: $($response.data.role)"
        return $response.data.token
    }
    throw "No token received"
}

# ============================================
# TEST 3: Login as Admin
# ============================================
Write-Host ""
Write-Host "===== TEST 3: Login as ADMIN ====="

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
        Write-Host "   Token received, Role: $($response.data.role)"
        if ($response.data.role -ne "ADMIN") {
            Write-Host "   [WARN] Role is not ADMIN - admin tests may fail"
        }
        return $response.data.token
    }
    throw "No token received"
}

# ============================================
# TEST 4: Access workflow API without token
# ============================================
Write-Host ""
Write-Host "===== TEST 4: Access workflow API without token ====="

Test-ApiCallExpectFail -Name "GET /api/admin/workflows without token" -TestScript {
    Invoke-RestMethod `
        -Uri "$BaseUrl/api/admin/workflows" `
        -Method GET `
        -ErrorAction Stop
} -ExpectedStatusCode 403

# ============================================
# TEST 5: User should not access admin workflow APIs
# ============================================
Write-Host ""
Write-Host "===== TEST 5: User access to admin workflow APIs ====="

if ($UserToken) {
    $userHeaders = @{
        Authorization = "Bearer $UserToken"
    }

    Test-ApiCallExpectFail -Name "GET /api/admin/workflows with USER token" -TestScript {
        Invoke-RestMethod `
            -Uri "$BaseUrl/api/admin/workflows" `
            -Method GET `
            -Headers $userHeaders `
            -ErrorAction Stop
    } -ExpectedStatusCode 403
}

# ============================================
# TEST 6: Company should not access admin workflow APIs
# ============================================
Write-Host ""
Write-Host "===== TEST 6: Company access to admin workflow APIs ====="

if ($CompanyToken) {
    $companyHeaders = @{
        Authorization = "Bearer $CompanyToken"
    }

    Test-ApiCallExpectFail -Name "GET /api/admin/workflows with COMPANY token" -TestScript {
        Invoke-RestMethod `
            -Uri "$BaseUrl/api/admin/workflows" `
            -Method GET `
            -Headers $companyHeaders `
            -ErrorAction Stop
    } -ExpectedStatusCode 403
}

# ============================================
# TEST 7: Admin - Get all workflows
# ============================================
Write-Host ""
Write-Host "===== TEST 7: Admin - Get all workflows ====="

if ($AdminToken) {
    $adminHeaders = @{
        Authorization = "Bearer $AdminToken"
    }

    Test-ApiCall -Name "GET /api/admin/workflows" -TestScript {
        $response = Invoke-RestMethod `
            -Uri "$BaseUrl/api/admin/workflows" `
            -Method GET `
            -Headers $adminHeaders `
            -ErrorAction Stop
        
        Write-Host "   Workflows retrieved"
        if ($response.data) {
            Write-Host "   Total workflows: $($response.data.Count)"
            if ($response.data.Count -gt 0) {
                $script:TestWorkflowId = $response.data[0].id
                Write-Host "   First workflow ID: $TestWorkflowId"
            }
        }
        return $response
    }
}

# ============================================
# TEST 8: Admin - Get active workflows
# ============================================
Write-Host ""
Write-Host "===== TEST 8: Admin - Get active workflows ====="

if ($AdminToken) {
    Test-ApiCall -Name "GET /api/admin/workflows/active" -TestScript {
        $response = Invoke-RestMethod `
            -Uri "$BaseUrl/api/admin/workflows/active" `
            -Method GET `
            -Headers $adminHeaders `
            -ErrorAction Stop
        
        Write-Host "   Active workflows retrieved"
        if ($response.data) {
            Write-Host "   Active count: $($response.data.Count)"
        }
        return $response
    }
}

# ============================================
# TEST 9: Admin - Get workflows by status
# ============================================
Write-Host ""
Write-Host "===== TEST 9: Admin - Get workflows by status ====="

if ($AdminToken) {
    $statuses = @("PENDING", "IN_PROGRESS", "COMPLETED", "CANCELLED")
    
    foreach ($status in $statuses) {
        Test-ApiCall -Name "GET /api/admin/workflows/status/$status" -TestScript {
            $response = Invoke-RestMethod `
                -Uri "$BaseUrl/api/admin/workflows/status/$status" `
                -Method GET `
                -Headers $adminHeaders `
                -ErrorAction Stop
            
            if ($response.data) {
                Write-Host "   Status $status : $($response.data.Count) workflows"
            }
            return $response
        }
    }
}

# ============================================
# TEST 10: Invalid status value
# ============================================
Write-Host ""
Write-Host "===== TEST 10: Invalid status value ====="

if ($AdminToken) {
    Test-ApiCallExpectFail -Name "GET /api/admin/workflows/status/INVALID_STATUS" -TestScript {
        Invoke-RestMethod `
            -Uri "$BaseUrl/api/admin/workflows/status/INVALID_STATUS" `
            -Method GET `
            -Headers $adminHeaders `
            -ErrorAction Stop
    } -ExpectedStatusCode 400
}

# ============================================
# TEST 11: Create rescue request first (needed for workflow)
# ============================================
Write-Host ""
Write-Host "===== TEST 11: Create rescue request (prerequisite) ====="

if ($UserToken) {
    $userHeaders = @{
        Authorization = "Bearer $UserToken"
    }
    
    $requestBody = @{
        companyId = 1
        description = "Test request for UC304 workflow - $(Get-Date -Format 'yyyyMMdd_HHmmss')"
        latitude = 21.0285
        longitude = 105.8542
        location = "123 Test Street, Hanoi"
    } | ConvertTo-Json

    $TestRequestId = Test-ApiCall -Name "POST /api/rescue-requests" -TestScript {
        $response = Invoke-RestMethod `
            -Uri "$BaseUrl/api/rescue-requests" `
            -Method POST `
            -ContentType "application/json; charset=utf-8" `
            -Headers $userHeaders `
            -Body $requestBody `
            -ErrorAction Stop
        
        if ($response.data -and $response.data.id) {
            Write-Host "   Request created with ID: $($response.data.id)"
            return $response.data.id
        }
        throw "No request ID returned"
    }
}

# ============================================
# TEST 12: Company - Get company workflows
# ============================================
Write-Host ""
Write-Host "===== TEST 12: Company - Get my workflows ====="

if ($CompanyToken) {
    $companyHeaders = @{
        Authorization = "Bearer $CompanyToken"
    }

    Test-ApiCall -Name "GET /api/workflows/company/my-workflows" -TestScript {
        $response = Invoke-RestMethod `
            -Uri "$BaseUrl/api/workflows/company/my-workflows" `
            -Method GET `
            -Headers $companyHeaders `
            -ErrorAction Stop
        
        Write-Host "   Company workflows retrieved"
        if ($response.data) {
            Write-Host "   Total: $($response.data.Count)"
        }
        return $response
    }
}

# ============================================
# TEST 13: Company - Get active workflows
# ============================================
Write-Host ""
Write-Host "===== TEST 13: Company - Get active workflows ====="

if ($CompanyToken) {
    Test-ApiCall -Name "GET /api/workflows/company/active" -TestScript {
        $response = Invoke-RestMethod `
            -Uri "$BaseUrl/api/workflows/company/active" `
            -Method GET `
            -Headers $companyHeaders `
            -ErrorAction Stop
        
        Write-Host "   Active company workflows retrieved"
        return $response
    }
}

# ============================================
# TEST 14: Company - Create workflow for rescue request
# ============================================
Write-Host ""
Write-Host "===== TEST 14: Company - Create workflow ====="

if ($CompanyToken -and $TestRequestId) {
    $TestWorkflowId = Test-ApiCall -Name "POST /api/workflows?rescueRequestId=$TestRequestId" -TestScript {
        $response = Invoke-RestMethod `
            -Uri "$BaseUrl/api/workflows?rescueRequestId=$TestRequestId" `
            -Method POST `
            -Headers $companyHeaders `
            -ErrorAction Stop
        
        if ($response.data -and $response.data.id) {
            Write-Host "   Workflow created with ID: $($response.data.id)"
            Write-Host "   Status: $($response.data.workflowStatus)"
            return $response.data.id
        }
        throw "No workflow ID returned"
    }
} else {
    Write-Host "[SKIP] No company token or request ID available"
}

# ============================================
# TEST 15: Get workflow by ID
# ============================================
Write-Host ""
Write-Host "===== TEST 15: Get workflow by ID ====="

if ($CompanyToken -and $TestWorkflowId) {
    Test-ApiCall -Name "GET /api/workflows/$TestWorkflowId" -TestScript {
        $response = Invoke-RestMethod `
            -Uri "$BaseUrl/api/workflows/$TestWorkflowId" `
            -Method GET `
            -Headers $companyHeaders `
            -ErrorAction Stop
        
        Write-Host "   Workflow details retrieved"
        Write-Host "   Status: $($response.data.workflowStatus)"
        return $response
    }
}

# ============================================
# TEST 16: Get workflow by rescue request ID
# ============================================
Write-Host ""
Write-Host "===== TEST 16: Get workflow by rescue request ID ====="

if ($CompanyToken -and $TestRequestId) {
    Test-ApiCall -Name "GET /api/workflows/request/$TestRequestId" -TestScript {
        $response = Invoke-RestMethod `
            -Uri "$BaseUrl/api/workflows/request/$TestRequestId" `
            -Method GET `
            -Headers $companyHeaders `
            -ErrorAction Stop
        
        Write-Host "   Workflow found for request $TestRequestId"
        return $response
    }
}

# ============================================
# TEST 17: Company - Add workflow step
# ============================================
Write-Host ""
Write-Host "===== TEST 17: Company - Add workflow step ====="

if ($CompanyToken -and $TestWorkflowId) {
    $stepBody = @{
        stepName = "Di chuyển đến vị trí"
        description = "Nhân viên đang di chuyển đến vị trí khách hàng"
        estimatedDuration = 15
    } | ConvertTo-Json

    $TestStepId = Test-ApiCall -Name "POST /api/workflows/$TestWorkflowId/steps" -TestScript {
        $response = Invoke-RestMethod `
            -Uri "$BaseUrl/api/workflows/$TestWorkflowId/steps" `
            -Method POST `
            -ContentType "application/json; charset=utf-8" `
            -Headers $companyHeaders `
            -Body $stepBody `
            -ErrorAction Stop
        
        if ($response.data -and $response.data.id) {
            Write-Host "   Step added with ID: $($response.data.id)"
            return $response.data.id
        }
        throw "No step ID returned"
    }
}

# ============================================
# TEST 18: Get workflow steps
# ============================================
Write-Host ""
Write-Host "===== TEST 18: Get workflow steps ====="

if ($CompanyToken -and $TestWorkflowId) {
    Test-ApiCall -Name "GET /api/workflows/$TestWorkflowId/steps" -TestScript {
        $response = Invoke-RestMethod `
            -Uri "$BaseUrl/api/workflows/$TestWorkflowId/steps" `
            -Method GET `
            -Headers $companyHeaders `
            -ErrorAction Stop
        
        Write-Host "   Steps retrieved"
        if ($response.data) {
            Write-Host "   Total steps: $($response.data.Count)"
        }
        return $response
    }
}

# ============================================
# TEST 19: Company - Update workflow step
# ============================================
Write-Host ""
Write-Host "===== TEST 19: Company - Update workflow step ====="

if ($CompanyToken -and $TestStepId) {
    $updateStepBody = @{
        stepStatus = "COMPLETED"
        notes = "Hoàn thành bước di chuyển"
        performedBy = "Test Staff"
    } | ConvertTo-Json

    Test-ApiCall -Name "PATCH /api/workflows/steps/$TestStepId" -TestScript {
        $response = Invoke-RestMethod `
            -Uri "$BaseUrl/api/workflows/steps/$TestStepId" `
            -Method PATCH `
            -ContentType "application/json; charset=utf-8" `
            -Headers $companyHeaders `
            -Body $updateStepBody `
            -ErrorAction Stop
        
        Write-Host "   Step updated successfully"
        return $response
    }
}

# ============================================
# TEST 20: Company - Update workflow status
# ============================================
Write-Host ""
Write-Host "===== TEST 20: Company - Update workflow status ====="

if ($CompanyToken -and $TestWorkflowId) {
    $statusBody = @{
        workflowStatus = "IN_PROGRESS"
        notes = "Đang xử lý yêu cầu cứu hộ"
    } | ConvertTo-Json

    Test-ApiCall -Name "PATCH /api/workflows/$TestWorkflowId/status" -TestScript {
        $response = Invoke-RestMethod `
            -Uri "$BaseUrl/api/workflows/$TestWorkflowId/status" `
            -Method PATCH `
            -ContentType "application/json; charset=utf-8" `
            -Headers $companyHeaders `
            -Body $statusBody `
            -ErrorAction Stop
        
        Write-Host "   Workflow status updated to: $($response.data.workflowStatus)"
        return $response
    }
}

# ============================================
# TEST 21: User - Get my workflows
# ============================================
Write-Host ""
Write-Host "===== TEST 21: User - Get my workflows ====="

if ($UserToken) {
    $userHeaders = @{
        Authorization = "Bearer $UserToken"
    }

    Test-ApiCall -Name "GET /api/workflows/user/my-workflows" -TestScript {
        $response = Invoke-RestMethod `
            -Uri "$BaseUrl/api/workflows/user/my-workflows" `
            -Method GET `
            -Headers $userHeaders `
            -ErrorAction Stop
        
        Write-Host "   User workflows retrieved"
        if ($response.data) {
            Write-Host "   Total: $($response.data.Count)"
        }
        return $response
    }
}

# ============================================
# TEST 22: User should not create workflow
# ============================================
Write-Host ""
Write-Host "===== TEST 22: User should not create workflow ====="

if ($UserToken -and $TestRequestId) {
    $userHeaders = @{
        Authorization = "Bearer $UserToken"
    }

    Test-ApiCallExpectFail -Name "POST /api/workflows with USER token" -TestScript {
        Invoke-RestMethod `
            -Uri "$BaseUrl/api/workflows?rescueRequestId=$TestRequestId" `
            -Method POST `
            -Headers $userHeaders `
            -ErrorAction Stop
    } -ExpectedStatusCode 403
}

# ============================================
# TEST 23: Admin - Update workflow status
# ============================================
Write-Host ""
Write-Host "===== TEST 23: Admin - Update workflow status ====="

if ($AdminToken -and $TestWorkflowId) {
    $adminHeaders = @{
        Authorization = "Bearer $AdminToken"
    }

    $adminStatusBody = @{
        workflowStatus = "IN_PROGRESS"
        notes = "Admin updated status"
    } | ConvertTo-Json

    Test-ApiCall -Name "PATCH /api/admin/workflows/$TestWorkflowId/status" -TestScript {
        $response = Invoke-RestMethod `
            -Uri "$BaseUrl/api/admin/workflows/$TestWorkflowId/status" `
            -Method PATCH `
            -ContentType "application/json; charset=utf-8" `
            -Headers $adminHeaders `
            -Body $adminStatusBody `
            -ErrorAction Stop
        
        Write-Host "   Admin updated workflow status"
        return $response
    }
}

# ============================================
# TEST 24: Company - Complete workflow
# ============================================
Write-Host ""
Write-Host "===== TEST 24: Company - Complete workflow ====="

if ($CompanyToken -and $TestWorkflowId) {
    Test-ApiCall -Name "POST /api/workflows/$TestWorkflowId/complete" -TestScript {
        $response = Invoke-RestMethod `
            -Uri "$BaseUrl/api/workflows/$TestWorkflowId/complete?completionNotes=Test%20completed%20successfully" `
            -Method POST `
            -Headers $companyHeaders `
            -ErrorAction Stop
        
        Write-Host "   Workflow completed"
        Write-Host "   Final status: $($response.data.workflowStatus)"
        return $response
    }
}

# ============================================
# TEST 25: Create another workflow to test cancel
# ============================================
Write-Host ""
Write-Host "===== TEST 25: Test cancel workflow ====="

if ($UserToken -and $CompanyToken) {
    # Create new request
    $userHeaders = @{
        Authorization = "Bearer $UserToken"
    }
    
    $requestBody = @{
        companyId = 1
        description = "Test request for cancel test - $(Get-Date -Format 'yyyyMMdd_HHmmss')"
        latitude = 21.0285
        longitude = 105.8542
        location = "Cancel Test Street"
    } | ConvertTo-Json

    try {
        $reqResponse = Invoke-RestMethod `
            -Uri "$BaseUrl/api/rescue-requests" `
            -Method POST `
            -ContentType "application/json; charset=utf-8" `
            -Headers $userHeaders `
            -Body $requestBody `
            -ErrorAction Stop
        
        if ($reqResponse.data.id) {
            $cancelRequestId = $reqResponse.data.id
            Write-Host "   Created request for cancel test: $cancelRequestId"
            
            # Create workflow
            $companyHeaders = @{
                Authorization = "Bearer $CompanyToken"
            }
            
            $wfResponse = Invoke-RestMethod `
                -Uri "$BaseUrl/api/workflows?rescueRequestId=$cancelRequestId" `
                -Method POST `
                -Headers $companyHeaders `
                -ErrorAction Stop
            
            if ($wfResponse.data.id) {
                $cancelWorkflowId = $wfResponse.data.id
                Write-Host "   Created workflow for cancel test: $cancelWorkflowId"
                
                # Cancel the workflow
                Test-ApiCall -Name "POST /api/workflows/$cancelWorkflowId/cancel" -TestScript {
                    $response = Invoke-RestMethod `
                        -Uri "$BaseUrl/api/workflows/$cancelWorkflowId/cancel?reason=Test%20cancellation" `
                        -Method POST `
                        -Headers $companyHeaders `
                        -ErrorAction Stop
                    
                    Write-Host "   Workflow cancelled"
                    Write-Host "   Final status: $($response.data.workflowStatus)"
                    return $response
                }
            }
        }
    } catch {
        Write-Host "   Could not complete cancel test"
    }
}

# ============================================
# TEST 26: Invalid workflow ID
# ============================================
Write-Host ""
Write-Host "===== TEST 26: Invalid workflow ID ====="

if ($CompanyToken) {
    Test-ApiCallExpectFail -Name "GET /api/workflows/999999 (invalid ID)" -TestScript {
        Invoke-RestMethod `
            -Uri "$BaseUrl/api/workflows/999999" `
            -Method GET `
            -Headers $companyHeaders `
            -ErrorAction Stop
    } -ExpectedStatusCode 404
}

# ============================================
# SUMMARY
# ============================================
Write-Host ""
Write-Host "=========================================="
Write-Host "   TEST UC304 COMPLETED"
Write-Host "=========================================="
Write-Host ""
Write-Host "Notes:"
Write-Host "- UC304 tests Workflow Management"
Write-Host "- Admin APIs: View all workflows, update status"
Write-Host "- Company APIs: Create, manage, complete/cancel workflows"
Write-Host "- User APIs: View own workflows only"
Write-Host ""
Write-Host "If you see 403 errors on admin-only endpoints:"
Write-Host "  1. Make sure admin account has ADMIN role in database"
Write-Host "  2. Run: UPDATE accounts SET role='ADMIN' WHERE username='admin_test_uc304';"
Write-Host ""
