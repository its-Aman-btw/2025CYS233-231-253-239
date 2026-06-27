use Gym_Fitness;
DELIMITER $$

-- Trigger 1: Log Member Deletion (Audit Log for Security)
CREATE TRIGGER trg_AfterMemberDelete
AFTER DELETE ON Members
FOR EACH ROW
BEGIN
    INSERT INTO AuditLogs (table_name, action_performed, record_id, old_value, new_value)
    VALUES (
        'Members', 
        'DELETE', 
        OLD.member_id, 
        CONCAT('Name: ', OLD.full_name, ', Email: ', OLD.email), 
        NULL
    );
END$$

-- Trigger 2: Log Equipment Status Changes
CREATE TRIGGER trg_AfterEquipmentUpdate
AFTER UPDATE ON Equipment
FOR EACH ROW
BEGIN
    IF OLD.status <> NEW.status THEN
        INSERT INTO AuditLogs (table_name, action_performed, record_id, old_value, new_value)
        VALUES (
            'Equipment', 
            'UPDATE', 
            NEW.equipment_id, 
            CONCAT('Old Status: ', OLD.status), 
            CONCAT('New Status: ', NEW.status)
        );
    END IF;
END$$

DELIMITER ;