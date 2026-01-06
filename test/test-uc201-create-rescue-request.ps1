# ============================================
# UC201 - Create Rescue Request (end-to-end)
# Requires backend running at http://localhost:8080/v1
# Logs in as seeded user 'user1' (password: password123) and creates a rescue request.
# ============================================

Write-Host "=== Testing UC201 Create Rescue Request ===" -ForegroundColor Cyan

$baseUrl = "http://localhost:8080/v1"

# 1) Login
Write-Host "[1] Login as user1" -ForegroundColor Yellow
$loginBody = @{
  username = "user1"
  password = "password123"
} | ConvertTo-Json

try {
  $login = Invoke-RestMethod -Uri "$baseUrl/api/auth/login" -Method Post -Body $loginBody -ContentType "application/json"
  $token = $login.data.token
  if (-not $token) { throw "No token returned" }
  Write-Host "    OK - token received" -ForegroundColor Green
} catch {
  Write-Host "    FAILED - $($_.Exception.Message)" -ForegroundColor Red
  exit 1
}

# 2) Find companies (ensure 1:1 filter returns results)
Write-Host "[2] GET /api/companies/search" -ForegroundColor Yellow
try {
  $companiesRes = Invoke-RestMethod -Uri "$baseUrl/api/companies/search?lat=21.0285&lng=105.8542&maxDistance=50" -Method Get
  $companies = $companiesRes.data.content
  if (-not $companies -or $companies.Count -eq 0) { throw "No companies returned" }
  $companyId = $companies[0].id
  Write-Host "    OK - picked companyId=$companyId ($($companies[0].name))" -ForegroundColor Green
} catch {
  Write-Host "    FAILED - $($_.Exception.Message)" -ForegroundColor Red
  exit 1
}

# 3) Create rescue request
Write-Host "[3] POST /api/rescue-requests" -ForegroundColor Yellow
$reqBody = @{
  company_id = [int]$companyId
  incident_desc = "Test UC201 incident"
  location_address = "Test address"
  latitude = 21.0285
  longitude = 105.8542
  images_base64 = @()
} | ConvertTo-Json

try {
  $headers = @{ Authorization = "Bearer $token" }
  $created = Invoke-RestMethod -Uri "$baseUrl/api/rescue-requests" -Method Post -Headers $headers -Body $reqBody -ContentType "application/json"
  Write-Host "    OK - created rescue request" -ForegroundColor Green
  $created | ConvertTo-Json -Depth 10
  exit 0
} catch {
  Write-Host "    FAILED - $($_.Exception.Message)" -ForegroundColor Red
  if ($_.ErrorDetails -and $_.ErrorDetails.Message) {
    Write-Host "    Details: $($_.ErrorDetails.Message)" -ForegroundColor DarkGray
  }
  exit 1
}
