package com.hospital.utils;

import com.hospital.model.*;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

/**
 * Factory class for creating test data
 */
public class TestDataFactory {

    // User Test Data
    public static User createTestUser() {
        User user = new User();
        user.setUserId(1);
        user.setUsername("testuser");
        user.setPassword("testpass123");
        user.setFullName("Test User");
        user.setEmail("test@hospital.com");
        user.setPhone("1234567890");
        user.setRole(User.UserRole.DOCTOR);
        user.setStatus(User.UserStatus.ACTIVE);
        return user;
    }

    public static User createTestAdmin() {
        User user = new User();
        user.setUserId(2);
        user.setUsername("admin");
        user.setPassword("admin123");
        user.setFullName("Admin User");
        user.setEmail("admin@hospital.com");
        user.setPhone("9876543210");
        user.setRole(User.UserRole.ADMIN);
        user.setStatus(User.UserStatus.ACTIVE);
        return user;
    }

    public static List<User> createTestUsers() {
        List<User> users = new ArrayList<>();
        users.add(createTestUser());
        users.add(createTestAdmin());

        User nurse = new User();
        nurse.setUserId(3);
        nurse.setUsername("nurse");
        nurse.setPassword("nurse123");
        nurse.setFullName("Nurse Test");
        nurse.setRole(User.UserRole.NURSE);
        nurse.setStatus(User.UserStatus.ACTIVE);
        users.add(nurse);

        return users;
    }

    // Patient Test Data
    public static Patient createTestPatient() {
        Patient patient = new Patient();
        patient.setPatientId(1);
        patient.setFirstName("John");
        patient.setLastName("Doe");
        patient.setDateOfBirth(Date.valueOf("1990-01-15"));
        patient.setGender(Patient.Gender.MALE);
        patient.setBloodGroup("O+");
        patient.setPhone("5551234567");
        patient.setEmail("john.doe@email.com");
        patient.setAddress("123 Test Street, Test City");
        patient.setEmergencyContact("Jane Doe");
        patient.setEmergencyPhone("5559876543");
        patient.setStatus(Patient.PatientStatus.ACTIVE);
        return patient;
    }

    public static Patient createTestPatient(String firstName, String lastName) {
        Patient patient = createTestPatient();
        patient.setFirstName(firstName);
        patient.setLastName(lastName);
        return patient;
    }

    public static List<Patient> createTestPatients() {
        List<Patient> patients = new ArrayList<>();
        patients.add(createTestPatient("John", "Doe"));
        patients.add(createTestPatient("Jane", "Smith"));
        patients.add(createTestPatient("Bob", "Johnson"));

        for (int i = 0; i < patients.size(); i++) {
            patients.get(i).setPatientId(i + 1);
        }

        return patients;
    }

    // Doctor Test Data
    public static Doctor createTestDoctor() {
        Doctor doctor = new Doctor();
        doctor.setDoctorId(1);
        doctor.setUserId(1);
        doctor.setSpecialization("Cardiology");
        doctor.setLicenseNumber("LIC-001");
        doctor.setDepartmentId(1);
        doctor.setQualification("MD, DM Cardiology");
        doctor.setExperienceYears(10);
        doctor.setConsultationFee(500.00);
        doctor.setFullName("Dr. Test Doctor");
        doctor.setEmail("doctor@hospital.com");
        doctor.setPhone("5551111111");
        return doctor;
    }

    public static List<Doctor> createTestDoctors() {
        List<Doctor> doctors = new ArrayList<>();

        Doctor doctor1 = createTestDoctor();
        doctor1.setDoctorId(1);
        doctor1.setSpecialization("Cardiology");
        doctors.add(doctor1);

        Doctor doctor2 = createTestDoctor();
        doctor2.setDoctorId(2);
        doctor2.setUserId(2);
        doctor2.setSpecialization("Neurology");
        doctor2.setLicenseNumber("LIC-002");
        doctor2.setFullName("Dr. Brain Specialist");
        doctors.add(doctor2);

        return doctors;
    }

    // Appointment Test Data
    public static Appointment createTestAppointment() {
        Appointment appointment = new Appointment();
        appointment.setAppointmentId(1);
        appointment.setPatientId(1);
        appointment.setDoctorId(1);
        appointment.setAppointmentDate(Date.valueOf("2024-12-01"));
        appointment.setAppointmentTime(Time.valueOf("10:00:00"));
        appointment.setStatus(Appointment.AppointmentStatus.SCHEDULED);
        appointment.setReason("Regular checkup");
        appointment.setNotes("Patient complaining of chest pain");
        appointment.setCreatedBy(1);
        appointment.setPatientName("John Doe");
        appointment.setDoctorName("Dr. Test Doctor");
        return appointment;
    }

    public static List<Appointment> createTestAppointments() {
        List<Appointment> appointments = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            Appointment apt = createTestAppointment();
            apt.setAppointmentId(i + 1);
            apt.setAppointmentDate(Date.valueOf("2024-12-" + String.format("%02d", i + 1)));
            appointments.add(apt);
        }

        return appointments;
    }

    // Diagnosis Test Data
    public static Diagnosis createTestDiagnosis() {
        Diagnosis diagnosis = new Diagnosis();
        diagnosis.setDiagnosisId(1);
        diagnosis.setAppointmentId(1);
        diagnosis.setPatientId(1);
        diagnosis.setDoctorId(1);
        diagnosis.setSymptoms("Chest pain, shortness of breath");
        diagnosis.setDiagnosis("Mild angina");
        diagnosis.setNotes("Prescribed medication, follow-up in 2 weeks");
        diagnosis.setFollowUpDate(Date.valueOf("2024-12-15"));
        diagnosis.setPatientName("John Doe");
        diagnosis.setDoctorName("Dr. Test Doctor");
        return diagnosis;
    }

    public static List<Diagnosis> createTestDiagnoses() {
        List<Diagnosis> diagnoses = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            Diagnosis diag = createTestDiagnosis();
            diag.setDiagnosisId(i + 1);
            diagnoses.add(diag);
        }

        return diagnoses;
    }

    // Prescription Test Data
    public static Prescription createTestPrescription() {
        Prescription prescription = new Prescription();
        prescription.setPrescriptionId(1);
        prescription.setDiagnosisId(1);
        prescription.setPatientId(1);
        prescription.setDoctorId(1);
        prescription.setMedicationName("Aspirin");
        prescription.setDosage("75mg");
        prescription.setFrequency("Once daily");
        prescription.setDuration("30 days");
        prescription.setInstructions("Take with food");
        prescription.setPatientName("John Doe");
        prescription.setDoctorName("Dr. Test Doctor");
        return prescription;
    }

    public static List<Prescription> createTestPrescriptions() {
        List<Prescription> prescriptions = new ArrayList<>();

        String[] medications = {"Aspirin", "Metformin", "Lisinopril"};
        String[] dosages = {"75mg", "500mg", "10mg"};

        for (int i = 0; i < medications.length; i++) {
            Prescription rx = createTestPrescription();
            rx.setPrescriptionId(i + 1);
            rx.setMedicationName(medications[i]);
            rx.setDosage(dosages[i]);
            prescriptions.add(rx);
        }

        return prescriptions;
    }

    // Message Test Data
    public static Message createTestMessage() {
        Message message = new Message();
        message.setMessageId(1);
        message.setSenderId(1);
        message.setReceiverId(2);
        message.setSubject("Test Message");
        message.setMessageText("This is a test message");
        message.setReadStatus(false);
        message.setSenderName("Test User");
        message.setReceiverName("Admin User");
        return message;
    }

    public static List<Message> createTestMessages() {
        List<Message> messages = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            Message msg = createTestMessage();
            msg.setMessageId(i + 1);
            msg.setSubject("Test Message " + (i + 1));
            msg.setReadStatus(i % 2 == 0);
            messages.add(msg);
        }

        return messages;
    }
}
