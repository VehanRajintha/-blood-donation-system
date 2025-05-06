-- Create database
CREATE DATABASE IF NOT EXISTS blood_donation_db;
USE blood_donation_db;

-- Users table
CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('ADMIN', 'USER') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Donor information
CREATE TABLE donors (
    donor_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT UNIQUE,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    date_of_birth DATE NOT NULL,
    blood_type ENUM('A+', 'A-', 'B+', 'B-', 'AB+', 'AB-', 'O+', 'O-') NOT NULL,
    gender ENUM('MALE', 'FEMALE', 'OTHER') NOT NULL,
    phone VARCHAR(15) NOT NULL,
    email VARCHAR(100),
    address TEXT NOT NULL,
    last_donation_date DATE,
    medical_conditions TEXT,
    is_eligible BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- Blood inventory
CREATE TABLE blood_inventory (
    inventory_id INT PRIMARY KEY AUTO_INCREMENT,
    blood_type ENUM('A+', 'A-', 'B+', 'B-', 'AB+', 'AB-', 'O+', 'O-') NOT NULL,
    units INT NOT NULL,
    collection_date DATE NOT NULL,
    expiry_date DATE NOT NULL,
    status ENUM('AVAILABLE', 'RESERVED', 'DISPOSED') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Blood requests
CREATE TABLE blood_requests (
    request_id INT PRIMARY KEY AUTO_INCREMENT,
    requester_name VARCHAR(100) NOT NULL,
    blood_type ENUM('A+', 'A-', 'B+', 'B-', 'AB+', 'AB-', 'O+', 'O-') NOT NULL,
    units_needed INT NOT NULL,
    hospital_name VARCHAR(100) NOT NULL,
    priority ENUM('HIGH', 'MEDIUM', 'LOW') NOT NULL,
    status ENUM('PENDING', 'APPROVED', 'COMPLETED', 'CANCELLED') NOT NULL,
    request_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    required_by_date DATE NOT NULL
);

-- Donation appointments
CREATE TABLE appointments (
    appointment_id INT PRIMARY KEY AUTO_INCREMENT,
    donor_id INT,
    appointment_date DATE NOT NULL,
    appointment_time TIME NOT NULL,
    status ENUM('PENDING', 'APPROVED', 'REJECTED', 'COMPLETED', 'CANCELLED') NOT NULL,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (donor_id) REFERENCES donors(donor_id)
);

-- Insert sample data

-- Admin and User accounts
INSERT INTO users (username, password, role) VALUES
('admin', 'admin123', 'ADMIN'),
('user1', 'user123', 'USER'),
('user2', 'user123', 'USER'),
('user3', 'user123', 'USER'),
('user4', 'user123', 'USER'),
('user5', 'user123', 'USER');

-- Sample donors
INSERT INTO donors (user_id, first_name, last_name, date_of_birth, blood_type, gender, phone, email, address, last_donation_date) VALUES
(2, 'John', 'Doe', '1990-05-15', 'O+', 'MALE', '555-0101', 'john.doe@email.com', '123 Main St, City', '2023-12-15'),
(3, 'Jane', 'Smith', '1988-08-22', 'A+', 'FEMALE', '555-0102', 'jane.smith@email.com', '456 Oak Ave, City', '2024-01-20'),
(4, 'Michael', 'Johnson', '1995-03-10', 'B-', 'MALE', '555-0103', 'michael.j@email.com', '789 Pine Rd, City', '2024-02-01'),
(5, 'Sarah', 'Williams', '1992-11-28', 'AB+', 'FEMALE', '555-0104', 'sarah.w@email.com', '321 Elm St, City', '2024-01-05'),
(6, 'David', 'Brown', '1985-07-03', 'O-', 'MALE', '555-0105', 'david.b@email.com', '654 Maple Dr, City', '2024-02-15');

-- Sample blood inventory
INSERT INTO blood_inventory (blood_type, units, collection_date, expiry_date, status) VALUES
('O+', 50, '2024-02-01', '2024-05-01', 'AVAILABLE'),
('A+', 30, '2024-02-01', '2024-05-01', 'AVAILABLE'),
('B-', 20, '2024-02-01', '2024-05-01', 'AVAILABLE'),
('AB+', 15, '2024-02-01', '2024-05-01', 'AVAILABLE'),
('O-', 25, '2024-02-01', '2024-05-01', 'AVAILABLE');

-- Sample blood requests
INSERT INTO blood_requests (requester_name, blood_type, units_needed, hospital_name, priority, status, required_by_date) VALUES
('City Hospital', 'O+', 5, 'City Hospital', 'HIGH', 'PENDING', '2024-03-01'),
('General Hospital', 'A+', 3, 'General Hospital', 'MEDIUM', 'APPROVED', '2024-03-05'),
('Medical Center', 'B-', 2, 'Medical Center', 'LOW', 'COMPLETED', '2024-02-28');

-- Sample appointments
INSERT INTO appointments (donor_id, appointment_date, appointment_time, status, notes) VALUES
(1, '2024-03-01', '10:00:00', 'PENDING', 'Regular donation'),
(2, '2024-03-02', '11:30:00', 'PENDING', 'First time donor'),
(3, '2024-03-03', '14:00:00', 'PENDING', 'Follow-up donation'),
(4, '2024-05-22', '14:00:00', 'PENDING', 'Regular check-up'); 