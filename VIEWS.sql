use Gym_Fitness;
-- View 1: Active Members with their Subscriptions
CREATE VIEW vw_ActiveMemberships AS
SELECT m.member_id, m.full_name, m.email, ms.title AS membership_plan, s.end_date
FROM Members m
JOIN Subscriptions s ON m.member_id = s.member_id
JOIN Memberships ms ON s.membership_id = ms.membership_id
WHERE s.status = 'Active';

-- View 2: Class Schedule Details (With Trainer Names)
CREATE VIEW vw_ClassScheduleDetails AS
SELECT cs.schedule_id, c.class_name, t.full_name AS trainer_name, cs.start_time, cs.end_time, cs.day_of_week, cs.capacity
FROM ClassSchedules cs
JOIN Classes c ON cs.class_id = c.class_id
JOIN Trainers t ON cs.trainer_id = t.trainer_id;

-- View 3: Monthly Revenue Summary
CREATE VIEW vw_MonthlyRevenue AS
SELECT DATE_FORMAT(payment_date, '%Y-%m') AS month, SUM(amount) AS total_revenue, COUNT(payment_id) AS total_transactions
FROM Payments
GROUP BY DATE_FORMAT(payment_date, '%Y-%m');

-- View 4: Equipment Maintenance Alert (Broken ya Maintenance wale)
CREATE VIEW vw_EquipmentStatus AS
SELECT equipment_id, name, status, last_maintenance_date
FROM Equipment
WHERE status IN ('Under Maintenance', 'Broken');

-- View 5: Trainer Workload (Kis trainer ke paas kitni classes hain)
CREATE VIEW vw_TrainerWorkload AS
SELECT t.trainer_id, t.full_name, COUNT(cs.schedule_id) AS total_classes_assigned
FROM Trainers t
LEFT JOIN ClassSchedules cs ON t.trainer_id = cs.trainer_id
GROUP BY t.trainer_id, t.full_name;
SELECT * FROM vw_ActiveMemberships;