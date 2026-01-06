Param(
  [string]$DbPath = ".\\rescue.db"
)

$ErrorActionPreference = "Stop"

if (-not (Test-Path $DbPath)) {
  throw "DB file not found: $DbPath"
}

Write-Host "Running email backfill on $DbPath" -ForegroundColor Cyan

# Requires sqlite3 available in PATH
sqlite3 $DbPath ".read scripts/backfill_company_email_to_accounts.sql"

Write-Host "Done." -ForegroundColor Green
