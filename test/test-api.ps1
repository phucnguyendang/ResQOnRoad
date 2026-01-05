# Test UC202 Company Search API

Write-Host "=== Testing ResQOnRoad UC202 Company Search API ===" -ForegroundColor Cyan
Write-Host ""

$baseUrl = "http://localhost:8080/v1"

# Test 1: POST /api/companies/search - Search with location
Write-Host "Test 1: POST /api/companies/search (Search with location)" -ForegroundColor Yellow
$body1 = @{
    latitude = 21.0285
    longitude = 105.8542
    maxDistance = 10.0
    page = 0
    size = 20
} | ConvertTo-Json

try {
    $response1 = Invoke-RestMethod -Uri "$baseUrl/api/companies/search" -Method Post -Body $body1 -ContentType 'application/json'
    Write-Host "Status: SUCCESS" -ForegroundColor Green
    Write-Host "Response:" -ForegroundColor Green
    $response1 | ConvertTo-Json -Depth 10
    Write-Host ""
} catch {
    Write-Host "Status: FAILED" -ForegroundColor Red
    Write-Host "Error: $_" -ForegroundColor Red
    Write-Host ""
}

# Test 2: POST /api/companies/search - Search with service types
Write-Host "Test 2: POST /api/companies/search (Search with service types)" -ForegroundColor Yellow
$body2 = @{
    latitude = 21.0285
    longitude = 105.8542
    maxDistance = 50.0
    serviceTypes = @("TOW_TRUCK", "BATTERY_JUMP_START")
    page = 0
    size = 10
} | ConvertTo-Json

try {
    $response2 = Invoke-RestMethod -Uri "$baseUrl/api/companies/search" -Method Post -Body $body2 -ContentType 'application/json'
    Write-Host "Status: SUCCESS" -ForegroundColor Green
    Write-Host "Response:" -ForegroundColor Green
    $response2 | ConvertTo-Json -Depth 10
    Write-Host ""
} catch {
    Write-Host "Status: FAILED" -ForegroundColor Red
    Write-Host "Error: $_" -ForegroundColor Red
    Write-Host ""
}

# Test 3: GET /api/companies/search - Search with query parameters
Write-Host "Test 3: GET /api/companies/search (Query parameters)" -ForegroundColor Yellow
try {
    $response3 = Invoke-RestMethod -Uri "$baseUrl/api/companies/search?latitude=21.0285&longitude=105.8542&maxDistance=5" -Method Get
    Write-Host "Status: SUCCESS" -ForegroundColor Green
    Write-Host "Response:" -ForegroundColor Green
    $response3 | ConvertTo-Json -Depth 10
    Write-Host ""
} catch {
    Write-Host "Status: FAILED" -ForegroundColor Red
    Write-Host "Error: $_" -ForegroundColor Red
    Write-Host ""
}

# Test 4: GET /api/companies/{companyId} - Get company details
Write-Host "Test 4: GET /api/companies/1 (Company details)" -ForegroundColor Yellow
try {
    $response4 = Invoke-RestMethod -Uri "$baseUrl/api/companies/1" -Method Get
    Write-Host "Status: SUCCESS" -ForegroundColor Green
    Write-Host "Response:" -ForegroundColor Green
    $response4 | ConvertTo-Json -Depth 10
    Write-Host ""
} catch {
    Write-Host "Status: FAILED" -ForegroundColor Red
    Write-Host "Error: $_" -ForegroundColor Red
    Write-Host ""
}

Write-Host "=== All Tests Completed ===" -ForegroundColor Cyan
