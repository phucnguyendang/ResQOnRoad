# Test Search API
$body = @{
    latitude = 21.0285
    longitude = 105.8542
    maxDistance = 50
    page = 0
    size = 20
} | ConvertTo-Json

$headers = @{
    "Content-Type" = "application/json"
}

Write-Host "Testing POST /api/companies/search..."
Write-Host "Body: $body"

Invoke-WebRequest -Uri "http://localhost:8080/v1/api/companies/search" `
    -Method POST `
    -Headers $headers `
    -Body $body | ConvertTo-Json
