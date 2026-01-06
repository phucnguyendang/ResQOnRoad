-- Backfill missing accounts.email for COMPANY accounts using rescue_companies.email
-- Safe/idempotent: only fills when accounts.email is NULL/empty and company email is non-empty.
--
-- Usage (from backend/):
--   sqlite3 .\rescue.db ".read scripts/backfill_company_email_to_accounts.sql"

PRAGMA foreign_keys = ON;

BEGIN IMMEDIATE;

-- Preview how many rows would be updated
SELECT
  COUNT(*) AS would_update
FROM accounts a
JOIN rescue_companies rc ON rc.id = a.company_id
WHERE a.role = 'COMPANY'
  AND a.company_id IS NOT NULL
  AND (a.email IS NULL OR TRIM(a.email) = '')
  AND rc.email IS NOT NULL AND TRIM(rc.email) <> '';

-- Perform the backfill
UPDATE accounts
SET email = (
  SELECT rc.email
  FROM rescue_companies rc
  WHERE rc.id = accounts.company_id
)
WHERE role = 'COMPANY'
  AND company_id IS NOT NULL
  AND (email IS NULL OR TRIM(email) = '')
  AND EXISTS (
    SELECT 1
    FROM rescue_companies rc
    WHERE rc.id = accounts.company_id
      AND rc.email IS NOT NULL AND TRIM(rc.email) <> ''
  );

-- Verify updates
SELECT changes() AS updated_rows;

COMMIT;
