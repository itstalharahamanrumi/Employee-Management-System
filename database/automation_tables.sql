-- ============================================================
-- Phase 14 — Automation: Leave Balance View
-- Creates a reusable VIEW for real-time leave balance per employee/type.
-- ============================================================

CREATE OR REPLACE VIEW vw_leave_balance AS
SELECT
    e.empid,
    e.name,
    lt.id              AS leave_type_id,
    lt.name            AS leave_type_name,
    lt.max_days,
    COALESCE(SUM(CASE WHEN la.status = 'Approved' THEN la.days ELSE 0 END), 0)
                       AS approved_days,
    COALESCE(SUM(CASE WHEN la.status = 'Pending'  THEN la.days ELSE 0 END), 0)
                       AS pending_days,
    lt.max_days - COALESCE(SUM(CASE WHEN la.status = 'Approved' THEN la.days ELSE 0 END), 0)
                       AS remaining_days
FROM employee e
CROSS JOIN leave_type lt
LEFT JOIN leave_application la
       ON la.empid = e.empid AND la.leave_type = lt.id
GROUP BY e.empid, e.name, lt.id, lt.name, lt.max_days;
