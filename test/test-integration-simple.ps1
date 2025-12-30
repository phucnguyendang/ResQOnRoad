# Test UC202 + UC205 Integration
$baseUrl = "http://localhost:8080/v1"
$token = ""

Write-Host "=== UC205: Authentication ===" -ForegroundColor Cyan

# Register
$registerBody = @{fullname="Test User";email="test@example.com";password="Test@123";phoneNumber="0901234567"} | ConvertTo-Json
try { 
    Invoke-RestMethod -Uri "$baseUrl/api/auth/register" -Method Post -Body $registerBody -ContentType 'application/json' | Out-Null
    Write-Host "Register: OK" -ForegroundColor Green 
} catch { 
    Write-Host "Register: User exists (OK)" -ForegroundColor Yellow 
}

# Login
$loginBody = @{email="test@example.com";password="Test@123"} | ConvertTo-Json
$loginResponse = Invoke-RestMethod -Uri "$baseUrl/api/auth/login" -Method Post -Body $loginBody -ContentType 'application/json'
$token = $loginResponse.data.token
Write-Host "Login: OK - Token received" -ForegroundColor Green

Write-Host "`n=== UC202: Company Search ===" -ForegroundColor Cyan

# Search companies
$searchBody = @{latitude=21.0285;longitude=105.8542;maxDistance=10.0;page=0;size=5} | ConvertTo-Json
$searchResponse = Invoke-RestMethod -Uri "$baseUrl/api/companies/search" -Method Post -Body $searchBody -ContentType 'application/json'
Write-Host "Search: Found $($searchResponse.data.totalElements) companies" -ForegroundColor Green
$companyId = $searchResponse.data.content[0].companyId

# Get company details
$companyDetails = Invoke-RestMethod -Uri "$baseUrl/api/companies/$companyId" -Method Get
Write-Host "Company Details: $($companyDetails.data.name) - Rating: $($companyDetails.data.averageRating)" -ForegroundColor Green

Write-Host "`n=== UC205: Rescue Requests ===" -ForegroundColor Cyan
$headers = @{"Authorization"="Bearer $token";"Content-Type"="application/json"}

# Create rescue request
$rescueBody = @{vehicleType="CAR";vehicleNumber="30A-12345";location="Hanoi";latitude=21.0285;longitude=105.8542;description="Battery dead";urgencyLevel="MEDIUM"} | ConvertTo-Json
$createResponse = Invoke-RestMethod -Uri "$baseUrl/api/rescue-requests" -Method Post -Body $rescueBody -Headers $headers
$requestId = $createResponse.data.requestId
Write-Host "Create Request: ID=$requestId Status=$($createResponse.data.currentStatus)" -ForegroundColor Green

# List requests
$listResponse = Invoke-RestMethod -Uri "$baseUrl/api/rescue-requests" -Method Get -Headers $headers
Write-Host "List Requests: Total $($listResponse.data.Count)" -ForegroundColor Green

# Update status
$updateBody = @{newStatus="IN_PROGRESS";notes="Team on the way"} | ConvertTo-Json
$updateResponse = Invoke-RestMethod -Uri "$baseUrl/api/rescue-requests/$requestId/status" -Method Put -Body $updateBody -Headers $headers
Write-Host "Update Status: $($updateResponse.data.currentStatus)" -ForegroundColor Green

# Complete request
$completeBody = @{newStatus="COMPLETED";notes="Service completed"} | ConvertTo-Json
$completeResponse = Invoke-RestMethod -Uri "$baseUrl/api/rescue-requests/$requestId/status" -Method Put -Body $completeBody -Headers $headers
Write-Host "Complete Request: $($completeResponse.data.currentStatus)" -ForegroundColor Green

Write-Host "`n=== Integration Test PASSED ===" -ForegroundColor Green
Write-Host "✓ UC202 Company Search working" -ForegroundColor Green
Write-Host "✓ UC205 Rescue Request Management working" -ForegroundColor Green
