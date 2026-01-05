# ============================================
# API TEST SCRIPT
# Run this script in Terminal 2 (after server is running)
# ============================================

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "   ResQOnRoad API Test Runner" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

$baseUrl = "http://localhost:8080/v1"

# Wait for server to be ready
Write-Host "`n[0] Checking if server is ready..." -ForegroundColor Yellow
$maxAttempts = 30
$attempts = 0
$serverReady = $false

while ($attempts -lt $maxAttempts -and -not $serverReady) {
    try {
        # Try root endpoint (/) to check if server is ready
        $response = Invoke-WebRequest -Uri "$baseUrl/" -Method Get -TimeoutSec 2 -ErrorAction Stop -UseBasicParsing
        if ($response.StatusCode -eq 200) {
            $serverReady = $true
            Write-Host "    Server is ready!" -ForegroundColor Green
        }
    } catch {
        $attempts++
        Write-Host "    Waiting for server... ($attempts/$maxAttempts)" -ForegroundColor Gray
        Start-Sleep -Seconds 2
    }
}

if (-not $serverReady) {
    Write-Host "    ERROR: Server is not responding after $maxAttempts attempts" -ForegroundColor Red
    Write-Host "    Please make sure to run 'start-server.ps1' in Terminal 1 first!" -ForegroundColor Yellow
    exit 1
}

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "   Starting API Tests" -ForegroundColor Cyan
Write-Host "========================================`n" -ForegroundColor Cyan

$testsPassed = 0
$testsFailed = 0

# Test 1: Welcome endpoint
Write-Host "[Test 1] GET / (Welcome)" -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/" -Method Get
    Write-Host "         Status: PASSED" -ForegroundColor Green
    Write-Host "         Response: $($response.message)" -ForegroundColor Gray
    $testsPassed++
} catch {
    Write-Host "         Status: FAILED - $($_.Exception.Message)" -ForegroundColor Red
    $testsFailed++
}

# Test 2: Health check (skip - actuator not exposed at /v1 path)
Write-Host "`n[Test 2] GET /actuator/health - SKIPPED (actuator at different path)" -ForegroundColor Yellow
Write-Host "         Status: SKIPPED" -ForegroundColor DarkGray
# Actuator endpoint is at root path, not under /v1 context

# Test 3: Register new user
Write-Host "`n[Test 3] POST /api/auth/register" -ForegroundColor Yellow
$randomUser = "testuser_$(Get-Random -Maximum 99999)"
$registerBody = @{
    username = $randomUser
    password = "Test@123"
    fullName = "Test User"
    phoneNumber = "0900000000"
    email = "$randomUser@test.com"
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/auth/register" -Method Post -Body $registerBody -ContentType "application/json"
    Write-Host "         Status: PASSED" -ForegroundColor Green
    Write-Host "         Response: $($response | ConvertTo-Json -Compress)" -ForegroundColor Gray
    $testsPassed++
} catch {
    Write-Host "         Status: FAILED - $($_.Exception.Message)" -ForegroundColor Red
    $testsFailed++
}

# Test 4: Login
Write-Host "`n[Test 4] POST /api/auth/login" -ForegroundColor Yellow
$loginBody = @{
    username = $randomUser
    password = "Test@123"
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/auth/login" -Method Post -Body $loginBody -ContentType "application/json"
    $token = $response.data.token
    if ($token) {
        Write-Host "         Status: PASSED" -ForegroundColor Green
        Write-Host "         Token received: $($token.Substring(0, [Math]::Min(50, $token.Length)))..." -ForegroundColor Gray
        $testsPassed++
    } else {
        Write-Host "         Status: FAILED - No token in response" -ForegroundColor Red
        $testsFailed++
    }
} catch {
    Write-Host "         Status: FAILED - $($_.Exception.Message)" -ForegroundColor Red
    $testsFailed++
    $token = $null
}

# Test 5: Search companies (no auth required)
Write-Host "`n[Test 5] GET /api/companies/search (near Ba Dinh)" -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/companies/search?lat=21.0285&lng=105.8542&maxDistance=50" -Method Get
    Write-Host "         Status: PASSED" -ForegroundColor Green
    Write-Host "         Found $($response.data.totalElements) companies" -ForegroundColor Gray
    $testsPassed++
} catch {
    Write-Host "         Status: FAILED - $($_.Exception.Message)" -ForegroundColor Red
    $testsFailed++
}

# Test 6: Get company by ID
Write-Host "`n[Test 6] GET /api/companies/1" -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/companies/1" -Method Get
    Write-Host "         Status: PASSED" -ForegroundColor Green
    Write-Host "         Company: $($response.data.name)" -ForegroundColor Gray
    $testsPassed++
} catch {
    Write-Host "         Status: FAILED - $($_.Exception.Message)" -ForegroundColor Red
    $testsFailed++
}

# Test 7: Search companies with different location (Tay Ho)
Write-Host "`n[Test 7] GET /api/companies/search (near Tay Ho)" -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/companies/search?lat=21.0583&lng=105.8200&maxDistance=30" -Method Get
    Write-Host "         Status: PASSED" -ForegroundColor Green
    Write-Host "         Found $($response.data.totalElements) companies near Tay Ho" -ForegroundColor Gray
    $testsPassed++
} catch {
    Write-Host "         Status: FAILED - $($_.Exception.Message)" -ForegroundColor Red
    $testsFailed++
}

# Summary
Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "   TEST SUMMARY" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "   Passed: $testsPassed" -ForegroundColor Green
Write-Host "   Failed: $testsFailed" -ForegroundColor $(if ($testsFailed -gt 0) { "Red" } else { "Green" })
Write-Host "   Total:  $($testsPassed + $testsFailed)" -ForegroundColor White
Write-Host "========================================`n" -ForegroundColor Cyan

if ($testsFailed -eq 0) {
    Write-Host "All tests passed!" -ForegroundColor Green
    exit 0
} else {
    Write-Host "Some tests failed. Please check the output above." -ForegroundColor Red
    exit 1
}
