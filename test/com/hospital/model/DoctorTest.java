package com.hospital.model;

import com.hospital.utils.TestDataFactory;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for Doctor model class
 */
public class DoctorTest {

    private Doctor doctor;

    @Before
    public void setUp() {
        doctor = TestDataFactory.createTestDoctor();
    }

    @Test
    public void testDoctorCreation() {
        assertNotNull("Doctor should not be null", doctor);
        assertEquals("Doctor ID should be 1", 1, doctor.getDoctorId());
        assertEquals("User ID should be 1", 1, doctor.getUserId());
    }

    @Test
    public void testSpecialization() {
        assertEquals("Specialization should match", "Cardiology", doctor.getSpecialization());

        doctor.setSpecialization("Neurology");
        assertEquals("Specialization should be updated", "Neurology", doctor.getSpecialization());
    }

    @Test
    public void testLicenseNumber() {
        assertEquals("License number should match", "LIC-001", doctor.getLicenseNumber());

        doctor.setLicenseNumber("LIC-999");
        assertEquals("License number should be updated", "LIC-999", doctor.getLicenseNumber());
    }

    @Test
    public void testDepartment() {
        assertEquals("Department ID should be 1", 1, doctor.getDepartmentId());

        doctor.setDepartmentId(5);
        assertEquals("Department ID should be updated", 5, doctor.getDepartmentId());
    }

    @Test
    public void testQualification() {
        assertEquals("Qualification should match", "MD, DM Cardiology", doctor.getQualification());

        doctor.setQualification("MBBS, MD");
        assertEquals("Qualification should be updated", "MBBS, MD", doctor.getQualification());
    }

    @Test
    public void testExperience() {
        assertEquals("Experience should be 10 years", 10, doctor.getExperienceYears());

        doctor.setExperienceYears(15);
        assertEquals("Experience should be updated", 15, doctor.getExperienceYears());
    }

    @Test
    public void testConsultationFee() {
        assertEquals("Consultation fee should be 500.00", 500.00, doctor.getConsultationFee(), 0.01);

        doctor.setConsultationFee(750.50);
        assertEquals("Consultation fee should be updated", 750.50, doctor.getConsultationFee(), 0.01);
    }

    @Test
    public void testExtendedFields() {
        assertEquals("Full name should match", "Dr. Test Doctor", doctor.getFullName());
        assertEquals("Email should match", "doctor@hospital.com", doctor.getEmail());
        assertEquals("Phone should match", "5551111111", doctor.getPhone());

        doctor.setFullName("Dr. New Name");
        assertEquals("Full name should be updated", "Dr. New Name", doctor.getFullName());
    }

    @Test
    public void testToString() {
        String result = doctor.toString();
        assertNotNull("toString should not return null", result);
        assertTrue("toString should contain full name", result.contains("Dr. Test Doctor"));
        assertTrue("toString should contain specialization", result.contains("Cardiology"));
    }

    @Test
    public void testParameterizedConstructor() {
        Doctor newDoctor = new Doctor(5, "Pediatrics", "LIC-PED-001", 2,
                                     "MBBS, MD Pediatrics", 8, 450.00);

        assertEquals("User ID should be 5", 5, newDoctor.getUserId());
        assertEquals("Specialization should be Pediatrics", "Pediatrics", newDoctor.getSpecialization());
        assertEquals("License number should match", "LIC-PED-001", newDoctor.getLicenseNumber());
        assertEquals("Department ID should be 2", 2, newDoctor.getDepartmentId());
        assertEquals("Qualification should match", "MBBS, MD Pediatrics", newDoctor.getQualification());
        assertEquals("Experience should be 8", 8, newDoctor.getExperienceYears());
        assertEquals("Consultation fee should be 450.00", 450.00, newDoctor.getConsultationFee(), 0.01);
    }

    @Test
    public void testAllSettersAndGetters() {
        doctor.setDoctorId(99);
        assertEquals("Doctor ID should be 99", 99, doctor.getDoctorId());

        doctor.setUserId(88);
        assertEquals("User ID should be 88", 88, doctor.getUserId());

        doctor.setEmail("newemail@hospital.com");
        assertEquals("Email should be updated", "newemail@hospital.com", doctor.getEmail());

        doctor.setPhone("9998887777");
        assertEquals("Phone should be updated", "9998887777", doctor.getPhone());
    }

    @Test
    public void testConsultationFeeValidRange() {
        doctor.setConsultationFee(0.00);
        assertEquals("Fee can be 0.00", 0.00, doctor.getConsultationFee(), 0.01);

        doctor.setConsultationFee(10000.00);
        assertEquals("Fee can be high", 10000.00, doctor.getConsultationFee(), 0.01);
    }
}
