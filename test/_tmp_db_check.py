import sqlite3
from pathlib import Path

DB = Path(r"d:\School\2025-1\software\Project\ResQOnRoad\backend\rescue.db")

print("db:", DB)
print("exists:", DB.exists())

con = sqlite3.connect(str(DB))
cur = con.cursor()

print("\n--- last rescue_companies (latest 20) ---")
for row in cur.execute(
    "SELECT id, name, is_active, is_verified, latitude, longitude, profile_status, created_at "
    "FROM rescue_companies ORDER BY id DESC LIMIT 20"
):
    print(row)

print("\n--- accounts with role ADMIN/COMPANY or company_id not null (latest 50) ---")
for row in cur.execute(
    "SELECT id, username, role, company_id "
    "FROM accounts "
    "WHERE company_id IS NOT NULL OR role='ADMIN' OR role='COMPANY' "
    "ORDER BY id DESC LIMIT 50"
):
    print(row)

print("\n--- companies missing COMPANY account (should be empty) ---")
for row in cur.execute(
    "SELECT c.id, c.name "
    "FROM rescue_companies c "
    "LEFT JOIN accounts a ON a.company_id=c.id AND a.role='COMPANY' "
    "WHERE a.id IS NULL "
    "ORDER BY c.id DESC LIMIT 50"
):
    print(row)

con.close()
