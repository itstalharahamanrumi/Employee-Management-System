-- ============================================================
-- Phase 12 — Payroll System  (run AFTER leave_tables.sql)
-- ============================================================

-- Payroll configuration: deduction & allowance rates
CREATE TABLE IF NOT EXISTS payroll_config (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    config_key   VARCHAR(50)  UNIQUE NOT NULL,
    config_value DECIMAL(6,4) NOT NULL,
    description  VARCHAR(100)
);

INSERT IGNORE INTO payroll_config (config_key, config_value, description) VALUES
    ('tax_rate',     0.1000, 'Income tax 10%'),
    ('pf_rate',      0.0500, 'Provident fund 5%'),
    ('house_rate',   0.1000, 'House allowance 10% of basic'),
    ('medical_rate', 0.0500, 'Medical allowance 5% of basic');

-- Monthly payroll records
CREATE TABLE IF NOT EXISTS payroll (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    empid           VARCHAR(20)   NOT NULL,
    pay_month       VARCHAR(7)    NOT NULL,          -- 'YYYY-MM'
    basic_salary    DECIMAL(12,2) NOT NULL,
    house_allowance DECIMAL(12,2) NOT NULL DEFAULT 0,
    medical_allow   DECIMAL(12,2) NOT NULL DEFAULT 0,
    bonus           DECIMAL(12,2) NOT NULL DEFAULT 0,
    gross_pay       DECIMAL(12,2) NOT NULL,
    tax_deduction   DECIMAL(12,2) NOT NULL DEFAULT 0,
    pf_deduction    DECIMAL(12,2) NOT NULL DEFAULT 0,
    other_deduction DECIMAL(12,2) NOT NULL DEFAULT 0,
    net_pay         DECIMAL(12,2) NOT NULL,
    status          ENUM('Draft','Processed','Paid') NOT NULL DEFAULT 'Draft',
    processed_on    DATETIME      DEFAULT NULL,
    paid_on         DATETIME      DEFAULT NULL,
    UNIQUE KEY uq_emp_month (empid, pay_month),
    FOREIGN KEY (empid) REFERENCES employee(empid) ON DELETE CASCADE
);
