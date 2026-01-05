# ============================================
# BACKEND SERVER STARTUP SCRIPT
# Run this script in Terminal 1
# ============================================

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "   ResQOnRoad Backend Server Startup" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

# Navigate to backend directory
Set-Location -Path "e:\Code\ResQOnRoad\backend"

Write-Host "`n[1/4] Cleaning old database..." -ForegroundColor Yellow
# Remove old SQLite database from BOTH locations to start fresh
$dbLocations = @("rescue.db", "..\rescue.db", "e:\Code\ResQOnRoad\rescue.db", "e:\Code\ResQOnRoad\backend\rescue.db")
$removed = $false
foreach ($dbPath in $dbLocations) {
    if (Test-Path $dbPath) {
        Remove-Item $dbPath -Force
        Write-Host "      Removed: $dbPath" -ForegroundColor Green
        $removed = $true
    }
}
if (-not $removed) {
    Write-Host "      No old database found." -ForegroundColor Gray
}

Write-Host "`n[2/4] Cleaning Maven build..." -ForegroundColor Yellow
mvn clean

Write-Host "`n[3/4] Building project..." -ForegroundColor Yellow
mvn package -DskipTests

Write-Host "`n[4/4] Starting server on port 8080..." -ForegroundColor Yellow
Write-Host "      API Base URL: http://localhost:8080/v1" -ForegroundColor Green
Write-Host "      Press Ctrl+C to stop the server" -ForegroundColor Gray
Write-Host "========================================`n" -ForegroundColor Cyan

# Start server
java -jar target\rescue-system-0.0.1-SNAPSHOT.jar
