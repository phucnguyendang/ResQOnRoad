$BaseUrl = "http://localhost:8080/v1"

Write-Host "===== TEST 1: Gọi profile khi CHƯA đăng nhập ====="

try {
    Invoke-RestMethod `
        -Uri "$BaseUrl/api/users/profile" `
        -Method GET `
        -ErrorAction Stop

    Write-Host "❌ FAIL: API cho phép truy cập khi chưa đăng nhập"
}
catch {
    if ($_.Exception.Response.StatusCode.Value__ -eq 403) {
        Write-Host "✅ PASS: Nhận đúng 403 Forbidden"
    } else {
        Write-Host "❌ FAIL: Nhận status code khác 403"
        Write-Host $_.Exception.Response.StatusCode.Value__
    }
}

Write-Host ""
Write-Host "===== TEST 2: Login rồi gọi profile ====="

# Login
$loginBody = @{
    username = "LongGey"
    password = "69696969"
} | ConvertTo-Json

try {
    $loginResponse = Invoke-RestMethod `
        -Uri "$BaseUrl/api/auth/login" `
        -Method POST `
        -ContentType "application/json" `
        -Body $loginBody `
        -ErrorAction Stop

    $Token = $loginResponse.data.token

    if (-not $Token) {
        throw "Không lấy được Token"
    }

    Write-Host "✅ Login thành công, đã lấy Token"
}
catch {
    Write-Host "❌ FAIL: Login thất bại"
    throw
}

# Call profile với token
$headers = @{
    Authorization = "Bearer $Token"
}

try {
    $profileResponse = Invoke-RestMethod `
        -Uri "$BaseUrl/api/users/profile" `
        -Method GET `
        -Headers $headers `
        -ErrorAction Stop

    Write-Host "✅ PASS: Gọi profile thành công"
    Write-Host $profileResponse.data
}
catch {
    Write-Host "❌ FAIL: Gọi profile thất bại"
    throw
}
