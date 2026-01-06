param(
  [string]$DbPath = "$PSScriptRoot\..\rescue.db",
  [string]$SqlPath = "$PSScriptRoot\seed_rescue_companies_approved_north.sql"
)

$ErrorActionPreference = 'Stop'

function Require-Sqlite3 {
  $cmd = Get-Command sqlite3 -ErrorAction SilentlyContinue
  if (-not $cmd) {
    throw "sqlite3 not found in PATH. Install sqlite3 or run the SQL using DB Browser for SQLite."
  }
}

function Test-ColumnExists {
  param(
    [string]$Db,
    [string]$Table,
    [string]$Column
  )

  $rows = & sqlite3 $Db "PRAGMA table_info($Table);"
  foreach ($row in $rows) {
    # PRAGMA table_info returns: cid|name|type|notnull|dflt_value|pk
    $parts = $row -split '\|'
    if ($parts.Count -ge 2 -and $parts[1] -eq $Column) {
      return $true
    }
  }
  return $false
}

function Ensure-Column {
  param(
    [string]$Db,
    [string]$Table,
    [string]$Column,
    [string]$ColumnSql
  )

  if (-not (Test-ColumnExists -Db $Db -Table $Table -Column $Column)) {
    Write-Host "Adding column $Table.$Column ..."
    & sqlite3 $Db "ALTER TABLE $Table ADD COLUMN $ColumnSql;" | Out-Null
  }
}

Require-Sqlite3

$DbFull = (Resolve-Path $DbPath).Path
$SqlFull = (Resolve-Path $SqlPath).Path

# Ensure UC404 columns exist (works on older sqlite3)
Ensure-Column -Db $DbFull -Table 'rescue_companies' -Column 'profile_status'        -ColumnSql "profile_status VARCHAR(30) DEFAULT 'INCOMPLETE'"
Ensure-Column -Db $DbFull -Table 'rescue_companies' -Column 'tax_code'              -ColumnSql "tax_code VARCHAR(20)"
Ensure-Column -Db $DbFull -Table 'rescue_companies' -Column 'hotline'               -ColumnSql "hotline VARCHAR(20)"
Ensure-Column -Db $DbFull -Table 'rescue_companies' -Column 'operating_hours'       -ColumnSql "operating_hours VARCHAR(100)"
Ensure-Column -Db $DbFull -Table 'rescue_companies' -Column 'license_expiry_date'   -ColumnSql "license_expiry_date TIMESTAMP"
Ensure-Column -Db $DbFull -Table 'rescue_companies' -Column 'license_document_url'  -ColumnSql "license_document_url VARCHAR(500)"
Ensure-Column -Db $DbFull -Table 'rescue_companies' -Column 'rejection_reason'      -ColumnSql "rejection_reason TEXT"

Ensure-Column -Db $DbFull -Table 'accounts' -Column 'company_id' -ColumnSql "company_id INTEGER"

Write-Host "Running seed SQL: $SqlFull"
& sqlite3 $DbFull ".read $SqlFull" | Out-Host

Write-Host "Inserted/updated companies:"
& sqlite3 $DbFull "SELECT id,name,latitude,longitude,profile_status FROM rescue_companies WHERE id BETWEEN 101 AND 105 ORDER BY id;" | Out-Host
