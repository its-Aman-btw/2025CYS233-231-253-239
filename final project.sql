create database Gym_Fitness;
use Gym_Fitness;
-- 1. Users Table (For Authentication)
CREATE TABLE Users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('Admin', 'Trainer', 'Member') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Members Table (Min. 10 Entities Requirement)
CREATE TABLE Members (
    member_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(15),
    dob DATE,
    gender ENUM('Male', 'Female', 'Other'),
    join_date DATE DEFAULT (CURRENT_DATE),
    status ENUM('Active', 'Inactive') DEFAULT 'Active',
    FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE SET NULL,
    CONSTRAINT chk_member_email CHECK (email LIKE '%@%.%') -- Constraint 1
);

-- 3. Trainers Table
CREATE TABLE Trainers (
    trainer_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    full_name VARCHAR(100) NOT NULL,
    specialization VARCHAR(100),
    experience_years INT NOT NULL,
    salary DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE SET NULL,
    CONSTRAINT chk_trainer_salary CHECK (salary > 0), -- Constraint 2
    CONSTRAINT chk_trainer_exp CHECK (experience_years >= 0) -- Constraint 3
);

-- 4. Memberships Table
CREATE TABLE Memberships (
    membership_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(50) NOT NULL UNIQUE,
    duration_months INT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    CONSTRAINT chk_membership_price CHECK (price >= 0), -- Constraint 4
    CONSTRAINT chk_membership_duration CHECK (duration_months > 0) -- Constraint 5
);

-- 5. Subscriptions Table (Junction Table)
CREATE TABLE Subscriptions (
    subscription_id INT AUTO_INCREMENT PRIMARY KEY,
    member_id INT NOT NULL,
    membership_id INT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    status ENUM('Active', 'Expired') DEFAULT 'Active',
    FOREIGN KEY (member_id) REFERENCES Members(member_id) ON DELETE CASCADE,
    FOREIGN KEY (membership_id) REFERENCES Memberships(membership_id),
    CONSTRAINT chk_dates CHECK (end_date > start_date) -- Constraint 6
);

-- 6. Classes Table
CREATE TABLE Classes (
    class_id INT AUTO_INCREMENT PRIMARY KEY,
    class_name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT
);

-- 7. ClassSchedules Table
CREATE TABLE ClassSchedules (
    schedule_id INT AUTO_INCREMENT PRIMARY KEY,
    class_id INT NOT NULL,
    trainer_id INT NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    day_of_week ENUM('Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday') NOT NULL,
    capacity INT NOT NULL,
    FOREIGN KEY (class_id) REFERENCES Classes(class_id) ON DELETE CASCADE,
    FOREIGN KEY (trainer_id) REFERENCES Trainers(trainer_id),
    CONSTRAINT chk_capacity CHECK (capacity > 0) -- Constraint 7
);

-- 8. ClassEnrollments Table (Junction Table)
CREATE TABLE ClassEnrollments (
    enrollment_id INT AUTO_INCREMENT PRIMARY KEY,
    member_id INT NOT NULL,
    schedule_id INT NOT NULL,
    enrollment_date DATE DEFAULT (CURRENT_DATE),
    FOREIGN KEY (member_id) REFERENCES Members(member_id) ON DELETE CASCADE,
    FOREIGN KEY (schedule_id) REFERENCES ClassSchedules(schedule_id) ON DELETE CASCADE,
    CONSTRAINT unique_enrollment UNIQUE(member_id, schedule_id) -- Constraint 8
);

-- 9. Attendance Table
CREATE TABLE Attendance (
    attendance_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    date DATE NOT NULL DEFAULT (CURRENT_DATE),
    status ENUM('Present', 'Absent') NOT NULL,
    FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE
);

-- 10. Equipment Table
CREATE TABLE Equipment (
    equipment_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    status ENUM('Functional', 'Under Maintenance', 'Broken') DEFAULT 'Functional',
    purchase_date DATE,
    last_maintenance_date DATE
);

-- 11. Payments Table
CREATE TABLE Payments (
    payment_id INT AUTO_INCREMENT PRIMARY KEY,
    member_id INT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    payment_method ENUM('Cash', 'Card', 'Online') NOT NULL,
    FOREIGN KEY (member_id) REFERENCES Members(member_id),
    CONSTRAINT chk_payment_amount CHECK (amount > 0) -- Constraint 9
);

-- 12. DietPlans Table
CREATE TABLE DietPlans (
    plan_id INT AUTO_INCREMENT PRIMARY KEY,
    member_id INT NOT NULL,
    trainer_id INT NOT NULL,
    plan_details TEXT NOT NULL,
    created_at DATE DEFAULT (CURRENT_DATE),
    FOREIGN KEY (member_id) REFERENCES Members(member_id) ON DELETE CASCADE,
    FOREIGN KEY (trainer_id) REFERENCES Trainers(trainer_id)
);

-- 13. WorkoutPlans Table
CREATE TABLE WorkoutPlans (
    workout_id INT AUTO_INCREMENT PRIMARY KEY,
    member_id INT NOT NULL,
    trainer_id INT NOT NULL,
    workout_details TEXT NOT NULL,
    created_at DATE DEFAULT (CURRENT_DATE),
    FOREIGN KEY (member_id) REFERENCES Members(member_id) ON DELETE CASCADE,
    FOREIGN KEY (trainer_id) REFERENCES Trainers(trainer_id)
);
-- 14. ErrorLogs Table (For Error Logging Requirement)
CREATE TABLE ErrorLogs (
    log_id INT AUTO_INCREMENT PRIMARY KEY,
    error_message TEXT NOT NULL,
    stack_trace TEXT,
    logged_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 15. AuditLogs Table (For Trigger/History Tracking Requirement)
CREATE TABLE AuditLogs (
    audit_id INT AUTO_INCREMENT PRIMARY KEY,
    table_name VARCHAR(50) NOT NULL,
    action_performed ENUM('INSERT', 'UPDATE', 'DELETE') NOT NULL,
    record_id INT NOT NULL,
    old_value TEXT,
    new_value TEXT,
    action_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
-- 1. Insert Users (Admin, Trainers, Members)
INSERT INTO Users (username, password, role) VALUES 
('admin_sahar', 'admin123', 'Admin'),
('trainer_ali', 'trainer123', 'Trainer'),
('trainer_sana', 'trainer123', 'Trainer'),
('member_ahmed', 'member123', 'Member'),
('member_ayesha', 'member123', 'Member');

-- 2. Insert Membership Plans
INSERT INTO Memberships (title, duration_months, price) VALUES 
('Monthly Basic', 1, 3000.00),
('Quarterly Standard', 3, 8000.00),
('Yearly Premium', 12, 25000.00);

-- 3. Insert Trainers
INSERT INTO Trainers (user_id, full_name, specialization, experience_years, salary) VALUES 
(2, 'Ali Khan', 'Bodybuilding & Weight Loss', 5, 45000.00),
(3, 'Sana Ahmed', 'Yoga & Pilates', 3, 40000.00);

-- 4. Insert Members
INSERT INTO Members (user_id, full_name, email, phone, dob, gender, status) VALUES 
(4, 'Ahmed Raza', 'ahmed.raza@email.com', '03001234567', '1998-05-15', 'Male', 'Active'),
(5, 'Ayesha Khan', 'ayesha.khan@email.com', '03219876543', '2000-11-22', 'Female', 'Active');

-- 5. Insert Gym Classes
INSERT INTO Classes (class_name, description) VALUES 
('Yoga Morning', 'Relaxing yoga sessions for flexibility and peace.'),
('Heavy CrossFit', 'High-intensity interval training for strength.');

-- 6. Insert Class Schedules
INSERT INTO ClassSchedules (class_id, trainer_id, start_time, end_time, day_of_week, capacity) VALUES 
(1, 2, '07:00:00', '08:00:00', 'Monday', 20),
(2, 1, '18:00:00', '19:30:00', 'Wednesday', 15);

-- 7. Insert Subscriptions (Members taking plans)
INSERT INTO Subscriptions (member_id, membership_id, start_date, end_date, status) VALUES 
(1, 1, '2026-06-01', '2026-07-01', 'Active'),
(2, 3, '2026-06-15', '2027-06-15', 'Active');

-- 8. Insert Payments
INSERT INTO Payments (member_id, amount, payment_method) VALUES 
(1, 3000.00, 'Cash'),
(2, 25000.00, 'Card');

-- 9. Insert Equipment
INSERT INTO Equipment (name, status, purchase_date) VALUES 
('Treadmill Pro-X', 'Functional', '2025-01-10'),
('Dumbbell Set 5kg-20kg', 'Functional', '2025-03-15'),
('Bench Press Station', 'Under Maintenance', '2025-02-20');