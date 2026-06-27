
use Gym_Fitness
DELIMITER $$

-- Procedure 1: New Member Registration (With Transaction Control)
-- Yeh procedure user account bhi banayega aur member profile bhi ek sath 
CREATE PROCEDURE sp_RegisterNewMember(
    IN p_username VARCHAR(50),
    IN p_password VARCHAR(255),
    IN p_full_name VARCHAR(100),
    IN p_email VARCHAR(100),
    IN p_phone VARCHAR(15),
    IN p_dob DATE,
    IN p_gender VARCHAR(10)
)
BEGIN
    DECLARE v_user_id INT;
    
    -- Transaction Start 
    START TRANSACTION;
    
    -- Insert into Users table
    INSERT INTO Users (username, password, role) 
    VALUES (p_username, p_password, 'Member');
    
    -- Get the last inserted user_id
    SET v_user_id = LAST_INSERT_ID();
    
    -- Insert into Members table
    INSERT INTO Members (user_id, full_name, email, phone, dob, gender)
    VALUES (v_user_id, p_full_name, p_email, p_phone, p_dob, p_gender);
    
    -- Commit if everything is fine 
    COMMIT;
END$$

-- Procedure 2: Process Member Payment & Renew Subscription
CREATE PROCEDURE sp_ProcessPayment(
    IN p_member_id INT,
    IN p_amount DECIMAL(10,2),
    IN p_method VARCHAR(20),
    IN p_membership_id INT
)
BEGIN
    START TRANSACTION; -- Transaction 2 
    
    -- 1. Insert Payment Record
    INSERT INTO Payments (member_id, amount, payment_method)
    VALUES (p_member_id, p_amount, p_method);
    
    -- 2. Renew or Insert Subscription
    INSERT INTO Subscriptions (member_id, membership_id, start_date, end_date, status)
    VALUES (p_member_id, p_membership_id, CURRENT_DATE, DATE_ADD(CURRENT_DATE, INTERVAL 1 MONTH), 'Active');
    
    COMMIT;
END$$

-- Procedure 3: Get Payments By Date Range (For Parametric PDF Reports) [cite: 28, 30]
CREATE PROCEDURE sp_GetPaymentsByDate(
    IN p_start_date DATE,
    IN p_end_date DATE
)
BEGIN
    SELECT p.payment_id, m.full_name, p.amount, p.payment_date, p.payment_method
    FROM Payments p
    JOIN Members m ON p.member_id = m.member_id
    WHERE DATE(p.payment_date) BETWEEN p_start_date AND p_end_date;
END$$

DELIMITER ;
DROP PROCEDURE IF EXISTS sp_ProcessPayment;
use Gym_Fitness;
-- 1. Sahi database select karein jo aapka Java use kar raha hai


-- 2. Stored Procedure ko yahan create karein
DELIMITER $$

DROP PROCEDURE IF EXISTS sp_RegisterMember$$

CREATE PROCEDURE sp_RegisterMember(
    IN p_name VARCHAR(100), 
    IN p_email VARCHAR(100), 
    IN p_phone VARCHAR(15), 
    IN p_dob DATE, 
    IN p_gender VARCHAR(10)
)
BEGIN
    INSERT INTO Members (full_name, email, phone, dob, gender, status) 
    VALUES (p_name, p_email, p_phone, p_dob, p_gender, 'Active');
END$$

DELIMITER ;

USE gym_fitness;

-- 1. Foreign key checks ko band karein taake table drop hone se na ruke
SET FOREIGN_KEY_CHECKS = 0;

-- 2. Purane mismatch table ko delete karein
DROP TABLE IF EXISTS Users;

-- 3. Naya table banayein jo aapke upar wale Java code se 100% match karta hai
CREATE TABLE Users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL, -- Java code isi column 'password_hash' mein data bhej raha hai
    role VARCHAR(20) CHECK (role IN ('Admin', 'Manager', 'Trainer'))
);

-- 4. Safety checks ko wapas on kar dein
SET FOREIGN_KEY_CHECKS = 1;