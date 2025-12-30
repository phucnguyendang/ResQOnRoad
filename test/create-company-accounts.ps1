# Create company accounts by registering as USER then updating via database
# This is a workaround since we don't have company registration API yet

$BaseUrl = "http://localhost:8080/v1"

Write-Host "Creating company test accounts..."

# Create company1 as USER first
$company1Register = @{
    username = "company1"
    password = "password123"
    fullName = "Cuu Ho Ba Dinh 24/7"
    phoneNumber = "0243123456"
    email = "badinh247@rescue.vn"
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod `
        -Uri "$BaseUrl/api/auth/register" `
        -Method POST `
        -ContentType "application/json; charset=utf-8" `
        -Body $company1Register `
        -ErrorAction Stop
    
    Write-Host "Company1 registered with ID: $($response.data.account_id)"
}
catch {
    Write-Host "Company1 registration failed (may already exist): $($_.Exception.Message)"
}

# Create company2 as USER first
$company2Register = @{
    username = "company2"
    password = "password123"
    fullName = "Cuu Ho Hoan Kiem Express"
    phoneNumber = "0243234567"
    email = "hoankiem@rescue.vn"
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod `
        -Uri "$BaseUrl/api/auth/register" `
        -Method POST `
        -ContentType "application/json; charset=utf-8" `
        -Body $company2Register `
        -ErrorAction Stop
    
    Write-Host "Company2 registered with ID: $($response.data.account_id)"
}
catch {
    Write-Host "Company2 registration failed (may already exist): $($_.Exception.Message)"
}

Write-Host ""
Write-Host "NOTE: Accounts created as USER role."
Write-Host "You need to manually update them to COMPANY role in database:"
Write-Host "  UPDATE accounts SET role='COMPANY', company_id=1 WHERE username='company1';"
Write-Host "  UPDATE accounts SET role='COMPANY', company_id=2 WHERE username='company2';"
Write-Host ""
Write-Host "Or use SQLite CLI:"
Write-Host "  sqlite3 backend/rescue.db"
Write-Host "  UPDATE accounts SET role='COMPANY', company_id=1 WHERE username='company1';"
Write-Host "  UPDATE accounts SET role='COMPANY', company_id=2 WHERE username='company2';"
Write-Host "  .quit"
