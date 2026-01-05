-- ============================================
-- SCHEMA UPDATE FOR UC404 - COMPANY PROFILE MANAGEMENT
-- Adds new columns to rescue_companies table
-- ============================================

-- Add new columns to rescue_companies table
ALTER TABLE rescue_companies ADD COLUMN IF NOT EXISTS profile_status VARCHAR(30) DEFAULT 'INCOMPLETE';
ALTER TABLE rescue_companies ADD COLUMN IF NOT EXISTS tax_code VARCHAR(20);
ALTER TABLE rescue_companies ADD COLUMN IF NOT EXISTS hotline VARCHAR(20);
ALTER TABLE rescue_companies ADD COLUMN IF NOT EXISTS operating_hours VARCHAR(100);
ALTER TABLE rescue_companies ADD COLUMN IF NOT EXISTS license_expiry_date TIMESTAMP;
ALTER TABLE rescue_companies ADD COLUMN IF NOT EXISTS license_document_url VARCHAR(500);
ALTER TABLE rescue_companies ADD COLUMN IF NOT EXISTS rejection_reason TEXT;

-- Add company_id column to accounts table for linking company accounts
ALTER TABLE accounts ADD COLUMN IF NOT EXISTS company_id INTEGER REFERENCES rescue_companies(id);

-- Create index for faster lookups
CREATE INDEX IF NOT EXISTS idx_rescue_companies_profile_status ON rescue_companies(profile_status);
CREATE INDEX IF NOT EXISTS idx_accounts_company_id ON accounts(company_id);

-- ============================================
-- SAMPLE DATA FOR TESTING UC404
-- ============================================

-- Update existing company accounts with company_id
-- (Assuming company accounts have role = 'COMPANY' and correspond to rescue_companies)

-- Example: Link account with id=10 to rescue company with id=1
-- UPDATE accounts SET company_id = 1 WHERE id = 10 AND role = 'COMPANY';

-- Example company profile with full details
-- UPDATE rescue_companies 
-- SET 
--     profile_status = 'APPROVED',
--     tax_code = '0123456789',
--     hotline = '1900123456',
--     operating_hours = '24/7',
--     license_expiry_date = '2025-12-31 23:59:59'
-- WHERE id = 1;
