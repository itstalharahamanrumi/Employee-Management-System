-- ─────────────────────────────────────────────────────────────────────────────
-- Phase 11 — Leave Application Module
-- Run against the 'employeemanagement' database
-- ─────────────────────────────────────────────────────────────────────────────

USE employeemanagement;

-- ── 1. Leave types ────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS leave_type (
    id       INT AUTO_INCREMENT PRIMARY KEY,
    name     VARCHAR(50) NOT NULL,
    max_days INT         NOT NULL DEFAULT 30
);

-- Seed 5 standard leave types (INSERT IGNORE skips if already present)
INSERT IGNORE INTO leave_type (id, name, max_days) VALUES
    (1, 'Annual Leave',    30),
    (2, 'Sick Leave',      14),
    (3, 'Casual Leave',    10),
    (4, 'Maternity Leave', 90),
    (5, 'Emergency Leave',  5);

-- ── 2. Leave applications ─────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS leave_application (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    empid       VARCHAR(20)  NOT NULL,
    leave_type  INT          NOT NULL,
    from_date   DATE         NOT NULL,
    to_date     DATE         NOT NULL,
    days        INT          NOT NULL DEFAULT 1,
    reason      TEXT,
    status      ENUM('Pending','Approved','Rejected') NOT NULL DEFAULT 'Pending',
    applied_on  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    reviewed_on DATETIME     DEFAULT NULL,
    remarks     VARCHAR(255) DEFAULT NULL,
    FOREIGN KEY (empid)      REFERENCES employee(empid)   ON DELETE CASCADE,
    FOREIGN KEY (leave_type) REFERENCES leave_type(id)    ON DELETE RESTRICT
);

SELECT 'Phase 11 migration complete.' AS status;
