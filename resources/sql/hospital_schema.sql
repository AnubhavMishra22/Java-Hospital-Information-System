-- Hospital Information System Database Schema
-- MySQL Database

DROP DATABASE IF EXISTS hospital_management;
CREATE DATABASE hospital_management;
USE hospital_management;

-- Users/Staff Table
CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(20),
    role ENUM('ADMIN', 'DOCTOR', 'NURSE', 'RECEPTIONIST') NOT NULL,
    status ENUM('ACTIVE', 'INACTIVE') DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP NULL
);

-- Patients Table
CREATE TABLE patients (
    patient_id INT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    date_of_birth DATE NOT NULL,
    gender ENUM('MALE', 'FEMALE', 'OTHER') NOT NULL,
    blood_group VARCHAR(5),
    phone VARCHAR(20) NOT NULL,
    email VARCHAR(100),
    address TEXT,
    emergency_contact VARCHAR(100),
    emergency_phone VARCHAR(20),
    registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('ACTIVE', 'INACTIVE', 'DECEASED') DEFAULT 'ACTIVE'
);

-- Departments Table
CREATE TABLE departments (
    department_id INT PRIMARY KEY AUTO_INCREMENT,
    department_name VARCHAR(100) NOT NULL,
    description TEXT,
    head_doctor_id INT,
    phone VARCHAR(20),
    FOREIGN KEY (head_doctor_id) REFERENCES users(user_id)
);

-- Doctors Table (Extended User Information)
CREATE TABLE doctors (
    doctor_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT UNIQUE NOT NULL,
    specialization VARCHAR(100) NOT NULL,
    license_number VARCHAR(50) UNIQUE NOT NULL,
    department_id INT,
    qualification VARCHAR(255),
    experience_years INT,
    consultation_fee DECIMAL(10, 2),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (department_id) REFERENCES departments(department_id)
);

-- Appointments Table
CREATE TABLE appointments (
    appointment_id INT PRIMARY KEY AUTO_INCREMENT,
    patient_id INT NOT NULL,
    doctor_id INT NOT NULL,
    appointment_date DATE NOT NULL,
    appointment_time TIME NOT NULL,
    status ENUM('SCHEDULED', 'COMPLETED', 'CANCELLED', 'NO_SHOW') DEFAULT 'SCHEDULED',
    reason TEXT,
    notes TEXT,
    created_by INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(patient_id),
    FOREIGN KEY (doctor_id) REFERENCES doctors(doctor_id),
    FOREIGN KEY (created_by) REFERENCES users(user_id)
);

-- Diagnoses Table
CREATE TABLE diagnoses (
    diagnosis_id INT PRIMARY KEY AUTO_INCREMENT,
    appointment_id INT NOT NULL,
    patient_id INT NOT NULL,
    doctor_id INT NOT NULL,
    diagnosis_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    symptoms TEXT NOT NULL,
    diagnosis TEXT NOT NULL,
    notes TEXT,
    follow_up_date DATE,
    FOREIGN KEY (appointment_id) REFERENCES appointments(appointment_id),
    FOREIGN KEY (patient_id) REFERENCES patients(patient_id),
    FOREIGN KEY (doctor_id) REFERENCES doctors(doctor_id)
);

-- Prescriptions Table
CREATE TABLE prescriptions (
    prescription_id INT PRIMARY KEY AUTO_INCREMENT,
    diagnosis_id INT NOT NULL,
    patient_id INT NOT NULL,
    doctor_id INT NOT NULL,
    prescription_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    medication_name VARCHAR(200) NOT NULL,
    dosage VARCHAR(100) NOT NULL,
    frequency VARCHAR(100) NOT NULL,
    duration VARCHAR(100) NOT NULL,
    instructions TEXT,
    FOREIGN KEY (diagnosis_id) REFERENCES diagnoses(diagnosis_id),
    FOREIGN KEY (patient_id) REFERENCES patients(patient_id),
    FOREIGN KEY (doctor_id) REFERENCES doctors(doctor_id)
);

-- Medical Records Table
CREATE TABLE medical_records (
    record_id INT PRIMARY KEY AUTO_INCREMENT,
    patient_id INT NOT NULL,
    record_type VARCHAR(50) NOT NULL,
    record_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    description TEXT NOT NULL,
    file_path VARCHAR(255),
    uploaded_by INT,
    FOREIGN KEY (patient_id) REFERENCES patients(patient_id),
    FOREIGN KEY (uploaded_by) REFERENCES users(user_id)
);

-- Messages Table (Internal Communication)
CREATE TABLE messages (
    message_id INT PRIMARY KEY AUTO_INCREMENT,
    sender_id INT NOT NULL,
    receiver_id INT NOT NULL,
    subject VARCHAR(200),
    message_text TEXT NOT NULL,
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    read_status BOOLEAN DEFAULT FALSE,
    read_at TIMESTAMP NULL,
    FOREIGN KEY (sender_id) REFERENCES users(user_id),
    FOREIGN KEY (receiver_id) REFERENCES users(user_id)
);

-- File Transfers Table
CREATE TABLE file_transfers (
    transfer_id INT PRIMARY KEY AUTO_INCREMENT,
    sender_id INT NOT NULL,
    receiver_id INT NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_size BIGINT,
    transfer_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    description TEXT,
    FOREIGN KEY (sender_id) REFERENCES users(user_id),
    FOREIGN KEY (receiver_id) REFERENCES users(user_id)
);

-- Billing Table
CREATE TABLE billing (
    bill_id INT PRIMARY KEY AUTO_INCREMENT,
    patient_id INT NOT NULL,
    appointment_id INT,
    bill_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total_amount DECIMAL(10, 2) NOT NULL,
    paid_amount DECIMAL(10, 2) DEFAULT 0,
    payment_status ENUM('PENDING', 'PARTIAL', 'PAID') DEFAULT 'PENDING',
    payment_method VARCHAR(50),
    description TEXT,
    FOREIGN KEY (patient_id) REFERENCES patients(patient_id),
    FOREIGN KEY (appointment_id) REFERENCES appointments(appointment_id)
);

-- Insert Sample Data
INSERT INTO users (username, password, full_name, email, phone, role) VALUES
('admin', 'admin123', 'Administrator', 'admin@hospital.com', '1234567890', 'ADMIN'),
('dr.smith', 'doctor123', 'Dr. John Smith', 'john.smith@hospital.com', '1234567891', 'DOCTOR'),
('dr.jones', 'doctor123', 'Dr. Sarah Jones', 'sarah.jones@hospital.com', '1234567892', 'DOCTOR'),
('nurse.mary', 'nurse123', 'Mary Johnson', 'mary.j@hospital.com', '1234567893', 'NURSE'),
('reception', 'reception123', 'Robert Brown', 'robert.b@hospital.com', '1234567894', 'RECEPTIONIST');

INSERT INTO departments (department_name, description, phone) VALUES
('Cardiology', 'Heart and cardiovascular system treatment', '1111111111'),
('Neurology', 'Brain and nervous system treatment', '2222222222'),
('Pediatrics', 'Child healthcare', '3333333333'),
('Orthopedics', 'Bone and muscle treatment', '4444444444'),
('General Medicine', 'General health consultation', '5555555555');

INSERT INTO doctors (user_id, specialization, license_number, department_id, qualification, experience_years, consultation_fee) VALUES
(2, 'Cardiology', 'LIC-CARD-001', 1, 'MD, DM Cardiology', 15, 500.00),
(3, 'Neurology', 'LIC-NEUR-001', 2, 'MD, DM Neurology', 10, 600.00);

INSERT INTO patients (first_name, last_name, date_of_birth, gender, blood_group, phone, email, address, emergency_contact, emergency_phone) VALUES
('Alice', 'Williams', '1985-03-15', 'FEMALE', 'O+', '9876543210', 'alice.w@email.com', '123 Main St, City', 'Bob Williams', '9876543211'),
('Michael', 'Davis', '1990-07-22', 'MALE', 'A+', '9876543212', 'michael.d@email.com', '456 Oak Ave, Town', 'Emma Davis', '9876543213'),
('Emma', 'Taylor', '1978-11-30', 'FEMALE', 'B+', '9876543214', 'emma.t@email.com', '789 Pine Rd, Village', 'James Taylor', '9876543215');
