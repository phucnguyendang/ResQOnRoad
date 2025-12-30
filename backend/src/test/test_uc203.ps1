# test_uc203.ps1
# UC203 test script
# Steps:
# 1) Login LongGey and create a rescue request
# 2) Login LongGey2 and create a rescue request
# 3) While LongGey2, try to cancel LongGey's request -> expect 403
# 4) While LongGey, try to cancel LongGey2's request -> expect 403
# 5) LongGey cancel own request -> expect 200
# 6) LongGey2 cancel own request -> expect 200

$ErrorActionPreference = 'Stop'

$BaseUrl = 'http://localhost:8080/v1'

function Send-Request {
    param(
        [Parameter(Mandatory=$true)] [string] $Method,
        [Parameter(Mandatory=$true)] [string] $Path,
        [string] $Token,
        $Body
    )

    $uri = "$BaseUrl$Path"
    $headers = @{}
    if ($Token) { $headers['Authorization'] = "Bearer $Token" }

    try {
        if ($Body -ne $null) {
            $json = $Body | ConvertTo-Json -Depth 10
            $resp = Invoke-WebRequest -Method $Method -Uri $uri -Headers $headers -Body $json -ContentType 'application/json' -UseBasicParsing -ErrorAction Stop
        } else {
            $resp = Invoke-WebRequest -Method $Method -Uri $uri -Headers $headers -UseBasicParsing -ErrorAction Stop
        }

        $content = $resp.Content
        $data = $null
        if ($content) { $data = $content | ConvertFrom-Json }
        return @{ StatusCode = [int]$resp.StatusCode; Content = $data; Raw = $content }
    } catch {
        $response = $_.Exception.Response
        if ($response -ne $null) {
            $status = [int]$response.StatusCode
            $text = ''
            try { $text = (New-Object System.IO.StreamReader($response.GetResponseStream())).ReadToEnd() } catch {}
            $data = $null
            try { $data = $text | ConvertFrom-Json } catch {}
            return @{ StatusCode = $status; Content = $data; Raw = $text; Error = $_.Exception.Message }
        } else {
            throw $_
        }
    }
}

function Login($username, $password) {
    $body = @{ username = $username; password = $password }
    $resp = Send-Request -Method 'POST' -Path '/api/auth/login' -Body $body
    if ($resp.StatusCode -ne 200) {
        Write-Host "Login failed for $username. Status: $($resp.StatusCode)"; exit 1
    }
    $token = $null
    try { $token = $resp.Content.data.token } catch {}
    if (-not $token) { Write-Host "Cannot extract token for $username"; exit 1 }
    return $token
}

function Assert-Status($resp, $expected, $msg) {
    if ($resp.StatusCode -ne $expected) {
        Write-Host "FAIL: $msg - expected $expected but got $($resp.StatusCode)"
        if ($resp.Raw) { Write-Host "Response body:`n$($resp.Raw)" }
        exit 1
    } else {
        Write-Host "OK: $msg (status $expected)"
    }
}

Write-Host "Starting UC203 test..."

# Step 1: LongGey create
$token1 = Login -username 'LongGey' -password '69696969'
$createBody1 = @{ location = 'Test Location LongGey'; latitude = 10.0; longitude = 106.0; description = 'UC203 test 1' }
$respCreate1 = Send-Request -Method 'POST' -Path '/api/rescue-requests' -Token $token1 -Body $createBody1
Assert-Status $respCreate1 201 'Create request by LongGey'
$id1 = $respCreate1.Content.data.id
Write-Host "LongGey request id: $id1"

# Step 2: LongGey2 create
$token2 = Login -username 'LongGey2' -password '696969'
$createBody2 = @{ location = 'Test Location LongGey2'; latitude = 11.0; longitude = 107.0; description = 'UC203 test 2' }
$respCreate2 = Send-Request -Method 'POST' -Path '/api/rescue-requests' -Token $token2 -Body $createBody2
Assert-Status $respCreate2 201 'Create request by LongGey2'
$id2 = $respCreate2.Content.data.id
Write-Host "LongGey2 request id: $id2"

# Step 3: While LongGey2, try to cancel LongGey's request -> expect 403
$resp = Send-Request -Method 'POST' -Path "/api/rescue-requests/$id1/cancel" -Token $token2
Assert-Status $resp 403 "LongGey2 cancelling LongGey's request should be forbidden"

# Step 4: While LongGey, try to cancel LongGey2's request -> expect 403
$resp = Send-Request -Method 'POST' -Path "/api/rescue-requests/$id2/cancel" -Token $token1
Assert-Status $resp 403 "LongGey cancelling LongGey2's request should be forbidden"

# Step 5: LongGey cancels own request -> expect 200
$resp = Send-Request -Method 'POST' -Path "/api/rescue-requests/$id1/cancel" -Token $token1
Assert-Status $resp 200 "LongGey cancels own request successfully"

# Step 6: LongGey2 cancels own request -> expect 200
$resp = Send-Request -Method 'POST' -Path "/api/rescue-requests/$id2/cancel" -Token $token2
Assert-Status $resp 200 "LongGey2 cancels own request successfully"

Write-Host 'UC203 test completed successfully.'
