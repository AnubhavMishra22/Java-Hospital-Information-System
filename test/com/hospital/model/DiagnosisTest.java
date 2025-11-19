package com.hospital.model;

import com.hospital.utils.TestDataFactory;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.sql.Date;

/**
 * Unit tests for Diagnosis model class
 */
public class DiagnosisTest {

    private Diagnosis diagnosis;

    @Before
    public void setUp() {
        diagnosis = TestDataFactory.createTestDiagnosis();
    }

    @Test
    public void testDiagnosisCreation() {
        assertNotNull("Diagnosis should not be null", diagnosis);
        assertEquals("Diagnosis ID should be 1", 1, diagnosis.getDiagnosisId());
        assertEquals("Appointment ID should be 1", 1, diagnosis.getAppointmentId());
        assertEquals("Patient ID should be 1", 1, diagnosis.getPatientId());
        assertEquals("Doctor ID should be 1", 1, diagnosis.getDoctorId());
    }

    @Test
    public void testSymptomsAndDiagnosis() {
        assertTrue("Symptoms should contain 'Chest pain'",
                  diagnosis.getSymptoms().contains("Chest pain"));
        assertEquals("Diagnosis should match", "Mild angina", diagnosis.getDiagnosis());

        diagnosis.setSymptoms("Headache, nausea");
        diagnosis.setDiagnosis("Migraine");

        assertEquals("Symptoms should be updated", "Headache, nausea", diagnosis.getSymptoms());
        assertEquals("Diagnosis should be updated", "Migraine", diagnosis.getDiagnosis());
    }

    @Test
    public void testNotes() {
        assertNotNull("Notes should not be null", diagnosis.getNotes());
        assertTrue("Notes should mention follow-up", diagnosis.getNotes().contains("follow-up"));

        diagnosis.setNotes("Patient recovering well");
        assertEquals("Notes should be updated", "Patient recovering well", diagnosis.getNotes());
    }

    @Test
    public void testFollowUpDate() {
        assertNotNull("Follow-up date should not be null", diagnosis.getFollowUpDate());
        assertEquals("Follow-up date should match", Date.valueOf("2024-12-15"),
                    diagnosis.getFollowUpDate());

        Date newDate = Date.valueOf("2024-12-30");
        diagnosis.setFollowUpDate(newDate);
        assertEquals("Follow-up date should be updated", newDate, diagnosis.getFollowUpDate());
    }

    @Test
    public void testExtendedFields() {
        assertEquals("Patient name should match", "John Doe", diagnosis.getPatientName());
        assertEquals("Doctor name should match", "Dr. Test Doctor", diagnosis.getDoctorName());

        diagnosis.setPatientName("Alice Brown");
        diagnosis.setDoctorName("Dr. Smith");

        assertEquals("Patient name should be updated", "Alice Brown", diagnosis.getPatientName());
        assertEquals("Doctor name should be updated", "Dr. Smith", diagnosis.getDoctorName());
    }

    @Test
    public void testParameterizedConstructor() {
        Diagnosis newDiag = new Diagnosis(2, 3, 4, "Fever, cough",
                                         "Common cold");

        assertEquals("Appointment ID should be 2", 2, newDiag.getAppointmentId());
        assertEquals("Patient ID should be 3", 3, newDiag.getPatientId());
        assertEquals("Doctor ID should be 4", 4, newDiag.getDoctorId());
        assertEquals("Symptoms should match", "Fever, cough", newDiag.getSymptoms());
        assertEquals("Diagnosis should match", "Common cold", newDiag.getDiagnosis());
    }

    @Test
    public void testAllGettersAndSetters() {
        diagnosis.setDiagnosisId(999);
        assertEquals("Diagnosis ID should be 999", 999, diagnosis.getDiagnosisId());

        diagnosis.setAppointmentId(888);
        assertEquals("Appointment ID should be 888", 888, diagnosis.getAppointmentId());

        diagnosis.setPatientId(777);
        assertEquals("Patient ID should be 777", 777, diagnosis.getPatientId());

        diagnosis.setDoctorId(666);
        assertEquals("Doctor ID should be 666", 666, diagnosis.getDoctorId());
    }
}
