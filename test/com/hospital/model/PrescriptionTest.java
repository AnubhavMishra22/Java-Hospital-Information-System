package com.hospital.model;

import com.hospital.utils.TestDataFactory;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for Prescription model class
 */
public class PrescriptionTest {

    private Prescription prescription;

    @Before
    public void setUp() {
        prescription = TestDataFactory.createTestPrescription();
    }

    @Test
    public void testPrescriptionCreation() {
        assertNotNull("Prescription should not be null", prescription);
        assertEquals("Prescription ID should be 1", 1, prescription.getPrescriptionId());
        assertEquals("Diagnosis ID should be 1", 1, prescription.getDiagnosisId());
        assertEquals("Patient ID should be 1", 1, prescription.getPatientId());
        assertEquals("Doctor ID should be 1", 1, prescription.getDoctorId());
    }

    @Test
    public void testMedicationDetails() {
        assertEquals("Medication name should match", "Aspirin", prescription.getMedicationName());
        assertEquals("Dosage should match", "75mg", prescription.getDosage());
        assertEquals("Frequency should match", "Once daily", prescription.getFrequency());
        assertEquals("Duration should match", "30 days", prescription.getDuration());
    }

    @Test
    public void testInstructions() {
        assertEquals("Instructions should match", "Take with food", prescription.getInstructions());

        prescription.setInstructions("Take on empty stomach");
        assertEquals("Instructions should be updated", "Take on empty stomach",
                    prescription.getInstructions());
    }

    @Test
    public void testUpdateMedicationDetails() {
        prescription.setMedicationName("Ibuprofen");
        prescription.setDosage("200mg");
        prescription.setFrequency("Twice daily");
        prescription.setDuration("14 days");

        assertEquals("Medication name should be updated", "Ibuprofen",
                    prescription.getMedicationName());
        assertEquals("Dosage should be updated", "200mg", prescription.getDosage());
        assertEquals("Frequency should be updated", "Twice daily", prescription.getFrequency());
        assertEquals("Duration should be updated", "14 days", prescription.getDuration());
    }

    @Test
    public void testExtendedFields() {
        assertEquals("Patient name should match", "John Doe", prescription.getPatientName());
        assertEquals("Doctor name should match", "Dr. Test Doctor", prescription.getDoctorName());

        prescription.setPatientName("Jane Smith");
        prescription.setDoctorName("Dr. Johnson");

        assertEquals("Patient name should be updated", "Jane Smith", prescription.getPatientName());
        assertEquals("Doctor name should be updated", "Dr. Johnson", prescription.getDoctorName());
    }

    @Test
    public void testToString() {
        String result = prescription.toString();
        assertNotNull("toString should not return null", result);
        assertTrue("toString should contain medication name", result.contains("Aspirin"));
        assertTrue("toString should contain dosage", result.contains("75mg"));
        assertTrue("toString should contain frequency", result.contains("Once daily"));
    }

    @Test
    public void testParameterizedConstructor() {
        Prescription newRx = new Prescription(2, 3, 4, "Metformin", "500mg",
                                              "Twice daily", "90 days");

        assertEquals("Diagnosis ID should be 2", 2, newRx.getDiagnosisId());
        assertEquals("Patient ID should be 3", 3, newRx.getPatientId());
        assertEquals("Doctor ID should be 4", 4, newRx.getDoctorId());
        assertEquals("Medication name should match", "Metformin", newRx.getMedicationName());
        assertEquals("Dosage should match", "500mg", newRx.getDosage());
        assertEquals("Frequency should match", "Twice daily", newRx.getFrequency());
        assertEquals("Duration should match", "90 days", newRx.getDuration());
    }

    @Test
    public void testAllGettersAndSetters() {
        prescription.setPrescriptionId(123);
        assertEquals("Prescription ID should be 123", 123, prescription.getPrescriptionId());

        prescription.setDiagnosisId(456);
        assertEquals("Diagnosis ID should be 456", 456, prescription.getDiagnosisId());

        prescription.setPatientId(789);
        assertEquals("Patient ID should be 789", 789, prescription.getPatientId());

        prescription.setDoctorId(101);
        assertEquals("Doctor ID should be 101", 101, prescription.getDoctorId());
    }
}
