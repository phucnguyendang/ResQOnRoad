# ============================================
# UC202 API Test Script
# Tests all company search endpoints
# ============================================

$baseUrl = "http://localhost:8080/v1"
Write-Host "`n============================================" -ForegroundColor Cyan
Write-Host "UC202 Company Search API Tests" -ForegroundColor Cyan
Write-Host "============================================`n" -ForegroundColor Cyan

# Test 1: POST search with location
Write-Host "Test 1: POST /api/companies/search (with location)" -ForegroundColor Yellow
$body1 = @{
    latitude = 21.0285
    longitude = 105.8542
    maxDistance = 10.0
} | ConvertTo-Json

try {
    $response1 = Invoke-RestMethod -Uri "$baseUrl/api/companies/search" `
        -Method Post `
        -ContentType "application/json" `
        -Body $body1
    Write-Host "✓ Status: 200 OK" -ForegroundColor Green
    Write-Host "✓ Companies found: $($response1.data.Count)" -ForegroundColor Green
    $response1.data | ForEach-Object {
        Write-Host "  - $($_.name) (Distance: $([math]::Round($_.distance, 2)) km, Rating: $($_.averageRating))" -ForegroundColor White
    }
} catch {
    Write-Host "✗ Error: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n--------------------------------------------`n" -ForegroundColor Gray

# Test 2: POST search with service types filter
Write-Host "Test 2: POST /api/companies/search (with service types)" -ForegroundColor Yellow
$body2 = @{
    latitude = 21.0285
    longitude = 105.8542
    maxDistance = 15.0
    serviceTypes = @("TOW_TRUCK", "TIRE_CHANGE")
} | ConvertTo-Json

try {
    $response2 = Invoke-RestMethod -Uri "$baseUrl/api/companies/search" `
        -Method Post `
        -ContentType "application/json" `
        -Body $body2
    Write-Host "✓ Status: 200 OK" -ForegroundColor Green
    Write-Host "✓ Companies found: $($response2.data.Count)" -ForegroundColor Green
    $response2.data | ForEach-Object {
        Write-Host "  - $($_.name) (Services: $($_.serviceTypes -join ', '))" -ForegroundColor White
    }
} catch {
    Write-Host "✗ Error: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n--------------------------------------------`n" -ForegroundColor Gray

# Test 3: GET search with query parameters
Write-Host "Test 3: GET /api/companies/search (with query params)" -ForegroundColor Yellow
try {
    $response3 = Invoke-RestMethod -Uri "$baseUrl/api/companies/search?lat=21.0285&lng=105.8542&maxDistance=20"
    Write-Host "✓ Status: 200 OK" -ForegroundColor Green
    Write-Host "✓ Companies found: $($response3.data.Count)" -ForegroundColor Green
    $response3.data | ForEach-Object {
        Write-Host "  - $($_.name) (Phone: $($_.phone), Active: $($_.isActive))" -ForegroundColor White
    }
} catch {
    Write-Host "✗ Error: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n--------------------------------------------`n" -ForegroundColor Gray

# Test 4: GET company details by ID
Write-Host "Test 4: GET /api/companies/{id} (company details)" -ForegroundColor Yellow
try {
    $response4 = Invoke-RestMethod -Uri "$baseUrl/api/companies/1"
    Write-Host "✓ Status: 200 OK" -ForegroundColor Green
    Write-Host "✓ Company: $($response4.data.name)" -ForegroundColor Green
    Write-Host "  Address: $($response4.data.address)" -ForegroundColor White
    Write-Host "  Email: $($response4.data.email)" -ForegroundColor White
    Write-Host "  Rating: $($response4.data.averageRating) ($($response4.data.totalReviews) reviews)" -ForegroundColor White
    Write-Host "  Services:" -ForegroundColor White
    $response4.data.serviceTypes | ForEach-Object {
        Write-Host "    - $_" -ForegroundColor White
    }
} catch {
    Write-Host "✗ Error: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n============================================" -ForegroundColor Cyan
Write-Host "All tests completed!" -ForegroundColor Cyan
Write-Host "============================================`n" -ForegroundColor Cyan
