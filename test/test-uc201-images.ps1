param(
  [string]$BaseUrl = 'http://localhost:8080/v1',
  [string]$Username = 'user1',
  [string]$Password = 'password123',
  [int]$CompanyId = 1
)

$ErrorActionPreference = 'Stop'

function Invoke-Json {
  param(
    [Parameter(Mandatory=$true)][ValidateSet('GET','POST','PATCH','PUT','DELETE')][string]$Method,
    [Parameter(Mandatory=$true)][string]$Uri,
    [hashtable]$Headers,
    $Body
  )

  $jsonBody = $null
  if ($null -ne $Body) {
    $jsonBody = $Body | ConvertTo-Json -Depth 20
  }

  if ($null -ne $jsonBody) {
    return Invoke-RestMethod -Method $Method -Uri $Uri -Headers $Headers -ContentType 'application/json; charset=utf-8' -Body $jsonBody
  }

  return Invoke-RestMethod -Method $Method -Uri $Uri -Headers $Headers
}

Write-Host "BaseUrl=$BaseUrl" 

# 1) Login
$loginResp = Invoke-Json -Method 'POST' -Uri "$BaseUrl/api/auth/login" -Body @{ username=$Username; password=$Password }
$token = $loginResp.data.token
if (-not $token) {
  throw "Login succeeded but token missing. Response: $($loginResp | ConvertTo-Json -Depth 20)"
}
Write-Host "Logged in as $Username. TokenLength=$($token.Length)"

$headers = @{ Authorization = "Bearer $token" }

# 2) Create rescue request with images
$images = @('dGVzdDE=', 'dGVzdDI=')
$createResp = Invoke-Json -Method 'POST' -Uri "$BaseUrl/api/rescue-requests" -Headers $headers -Body @{
  company_id       = $CompanyId
  location_address = 'Hanoi Test Address'
  latitude         = 21.0285
  longitude        = 105.8542
  incident_desc    = 'Flat tire - integration test'
  service_type     = 'TOW_TRUCK'
  images_base64    = $images
}

$requestId = $createResp.data.id
if (-not $requestId) {
  throw "Create succeeded but id missing. Response: $($createResp | ConvertTo-Json -Depth 20)"
}
Write-Host "Created rescue request id=$requestId"

# 3) Fetch detail
$detailResp = Invoke-Json -Method 'GET' -Uri "$BaseUrl/api/rescue-requests/$requestId" -Headers $headers
$roundTrip = $detailResp.data.incident.images_base64

if ($null -eq $roundTrip) {
  throw "Detail response missing data.incident.images_base64. Response: $($detailResp | ConvertTo-Json -Depth 20)"
}

Write-Host "Detail images count=$($roundTrip.Count)"
Write-Host "Detail images: $($roundTrip -join ', ')"

# 4) Assertion
if ($roundTrip.Count -ne $images.Count -or ($roundTrip[0] -ne $images[0]) -or ($roundTrip[1] -ne $images[1])) {
  throw "Image round-trip FAILED. Expected: $($images -join ', ') ; Got: $($roundTrip -join ', ')"
}

Write-Host 'Image round-trip OK'
