# ============================================
# RUN ALL UC TESTS
# Script to run all Use Case tests in sequence
# ============================================

param(
    [string]$TestFilter = "*",
    [switch]$SkipServerCheck = $false
)

$BaseUrl = "http://localhost:8080/v1"
$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Definition

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "   ResQOnRoad - Use Case Test Runner" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Check if server is ready
if (-not $SkipServerCheck) {
    Write-Host "[0] Checking if server is ready..." -ForegroundColor Yellow
    $maxAttempts = 15
    $attempts = 0
    $serverReady = $false

    while ($attempts -lt $maxAttempts -and -not $serverReady) {
        try {
            $response = Invoke-WebRequest -Uri "$BaseUrl/" -Method Get -TimeoutSec 2 -ErrorAction Stop -UseBasicParsing
            if ($response.StatusCode -eq 200) {
                $serverReady = $true
                Write-Host "    Server is ready!" -ForegroundColor Green
            }
        } catch {
            $attempts++
            Write-Host "    Waiting for server... ($attempts/$maxAttempts)" -ForegroundColor Gray
            Start-Sleep -Seconds 2
        }
    }

    if (-not $serverReady) {
        Write-Host "    ERROR: Server is not responding!" -ForegroundColor Red
        Write-Host "    Please start the server first:" -ForegroundColor Yellow
        Write-Host "    cd backend && mvn spring-boot:run" -ForegroundColor Yellow
        Write-Host ""
        exit 1
    }
}

Write-Host ""

# Find all UC test files
$testFiles = Get-ChildItem -Path $ScriptDir -Filter "test_uc*.ps1" | Sort-Object Name

if ($TestFilter -ne "*") {
    $testFiles = $testFiles | Where-Object { $_.Name -like "*$TestFilter*" }
}

if ($testFiles.Count -eq 0) {
    Write-Host "No test files found matching filter: $TestFilter" -ForegroundColor Yellow
    exit 0
}

Write-Host "Found $($testFiles.Count) test file(s) to run:" -ForegroundColor Cyan
foreach ($file in $testFiles) {
    Write-Host "  - $($file.Name)" -ForegroundColor Gray
}
Write-Host ""

# Run each test file
$totalPassed = 0
$totalFailed = 0
$results = @()

foreach ($testFile in $testFiles) {
    Write-Host "========================================" -ForegroundColor Magenta
    Write-Host "   Running: $($testFile.Name)" -ForegroundColor Magenta
    Write-Host "========================================" -ForegroundColor Magenta
    Write-Host ""
    
    $startTime = Get-Date
    
    try {
        # Run the test script
        & $testFile.FullName
        $exitCode = $LASTEXITCODE
        
        $endTime = Get-Date
        $duration = ($endTime - $startTime).TotalSeconds
        
        $results += @{
            Name = $testFile.Name
            Status = if ($exitCode -eq 0 -or $null -eq $exitCode) { "PASSED" } else { "FAILED" }
            Duration = [math]::Round($duration, 2)
        }
        
        if ($exitCode -eq 0 -or $null -eq $exitCode) {
            $totalPassed++
        } else {
            $totalFailed++
        }
    }
    catch {
        $endTime = Get-Date
        $duration = ($endTime - $startTime).TotalSeconds
        
        $results += @{
            Name = $testFile.Name
            Status = "ERROR"
            Duration = [math]::Round($duration, 2)
            Error = $_.Exception.Message
        }
        $totalFailed++
    }
    
    Write-Host ""
}

# Print summary
Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "   TEST RESULTS SUMMARY" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

foreach ($result in $results) {
    $statusColor = switch ($result.Status) {
        "PASSED" { "Green" }
        "FAILED" { "Red" }
        "ERROR" { "Red" }
        default { "Yellow" }
    }
    
    $statusIcon = switch ($result.Status) {
        "PASSED" { "✅" }
        "FAILED" { "❌" }
        "ERROR" { "⚠️" }
        default { "?" }
    }
    
    Write-Host "  $statusIcon $($result.Name)" -ForegroundColor $statusColor
    Write-Host "     Status: $($result.Status) | Duration: $($result.Duration)s" -ForegroundColor Gray
    
    if ($result.Error) {
        Write-Host "     Error: $($result.Error)" -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "----------------------------------------" -ForegroundColor Cyan
Write-Host "  Total Tests: $($results.Count)" -ForegroundColor White
Write-Host "  Passed:      $totalPassed" -ForegroundColor Green
Write-Host "  Failed:      $totalFailed" -ForegroundColor $(if ($totalFailed -gt 0) { "Red" } else { "Green" })
Write-Host "----------------------------------------" -ForegroundColor Cyan
Write-Host ""

if ($totalFailed -eq 0) {
    Write-Host "All tests completed successfully! 🎉" -ForegroundColor Green
    exit 0
} else {
    Write-Host "Some tests failed. Please check the output above." -ForegroundColor Red
    exit 1
}
