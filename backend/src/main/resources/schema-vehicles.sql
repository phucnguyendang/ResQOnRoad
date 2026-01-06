-- ============================================
-- SCHEMA FOR UC303 - VEHICLE MANAGEMENT
-- Tables: vehicles
-- Stores information about rescue vehicles
-- ============================================

CREATE TABLE IF NOT EXISTS vehicles (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    company_id INTEGER NOT NULL,

    plate_number VARCHAR(20) NOT NULL,
    vehicle_type VARCHAR(50) NOT NULL,
    equipment_desc TEXT,

    status VARCHAR(20) NOT NULL
        CHECK (status IN ('Available', 'Busy'))
        DEFAULT 'Available',

    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_vehicle_company
        FOREIGN KEY (company_id)
        REFERENCES rescue_companies(id)
        ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_vehicles_company_id ON vehicles(company_id);
CREATE UNIQUE INDEX IF NOT EXISTS uk_vehicles_plate_number ON vehicles(plate_number);