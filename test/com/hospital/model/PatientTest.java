package com.hospital.model;

import com.hospital.utils.TestDataFactory;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.sql.Date;

/**
 * Unit tests for Patient model class
 */
public class PatientTest {

    private Patient patient;

    @Before
    public void setUp() {
        patient = TestDataFactory.createTestPatient();
    }

    @Test
    public void testPatientCreation() {
        assertNotNull("Patient should not be null", patient);
        assertEquals("Patient ID should be 1", 1, patient.getPatientId());
        assertEquals("First name should match", "John", patient.getFirstName());
        assertEquals("Last name should match", "Doe", patient.getLastName());
    }

    @Test
    public void testGetFullName() {
        String fullName = patient.getFullName();
        assertEquals("Full name should be 'John Doe'", "John Doe", fullName);

        patient.setFirstName("Jane");
        patient.setLastName("Smith");
        assertEquals("Full name should be updated", "Jane Smith", patient.getFullName());
    }

    @Test
    public void testPatientGender() {
        assertEquals("Gender should be MALE", Patient.Gender.MALE, patient.getGender());

        patient.setGender(Patient.Gender.FEMALE);
        assertEquals("Gender should be FEMALE", Patient.Gender.FEMALE, patient.getGender());

        patient.setGender(Patient.Gender.OTHER);
        assertEquals("Gender should be OTHER", Patient.Gender.OTHER, patient.getGender());
    }

    @Test
    public void testPatientStatus() {
        assertEquals("Status should be ACTIVE", Patient.PatientStatus.ACTIVE, patient.getStatus());

        patient.setStatus(Patient.PatientStatus.INACTIVE);
        assertEquals("Status should be INACTIVE", Patient.PatientStatus.INACTIVE, patient.getStatus());

        patient.setStatus(Patient.PatientStatus.DECEASED);
        assertEquals("Status should be DECEASED", Patient.PatientStatus.DECEASED, patient.getStatus());
    }

    @Test
    public void testBloodGroup() {
        assertEquals("Blood group should be O+", "O+", patient.getBloodGroup());

        patient.setBloodGroup("A-");
        assertEquals("Blood group should be A-", "A-", patient.getBloodGroup());
    }

    @Test
    public void testContactInformation() {
        assertEquals("Phone should match", "5551234567", patient.getPhone());
        assertEquals("Email should match", "john.doe@email.com", patient.getEmail());
        assertEquals("Address should match", "123 Test Street, Test City", patient.getAddress());
    }

    @Test
    public void testEmergencyContact() {
        assertEquals("Emergency contact should match", "Jane Doe", patient.getEmergencyContact());
        assertEquals("Emergency phone should match", "5559876543", patient.getEmergencyPhone());

        patient.setEmergencyContact("Bob Smith");
        patient.setEmergencyPhone("5551111111");

        assertEquals("Emergency contact should be updated", "Bob Smith", patient.getEmergencyContact());
        assertEquals("Emergency phone should be updated", "5551111111", patient.getEmergencyPhone());
    }

    @Test
    public void testDateOfBirth() {
        assertNotNull("Date of birth should not be null", patient.getDateOfBirth());
        assertEquals("Date of birth should match", Date.valueOf("1990-01-15"), patient.getDateOfBirth());
    }

    @Test
    public void testToString() {
        String result = patient.toString();
        assertNotNull("toString should not return null", result);
        assertTrue("toString should contain patient ID", result.contains("1"));
        assertTrue("toString should contain full name", result.contains("John Doe"));
    }

    @Test
    public void testAllGendersExist() {
        Patient.Gender[] genders = Patient.Gender.values();
        assertEquals("Should have 3 genders", 3, genders.length);
    }

    @Test
    public void testAllStatusesExist() {
        Patient.PatientStatus[] statuses = Patient.PatientStatus.values();
        assertEquals("Should have 3 statuses", 3, statuses.length);
    }

    @Test
    public void testParameterizedConstructor() {
        Patient newPatient = new Patient("Alice", "Brown", Date.valueOf("1995-05-20"),
                                        Patient.Gender.FEMALE, "B+", "5556789012",
                                        "alice@email.com", "456 Oak St",
                                        "Carol Brown", "5559999999");

        assertEquals("First name should be set", "Alice", newPatient.getFirstName());
        assertEquals("Last name should be set", "Brown", newPatient.getLastName());
        assertEquals("DOB should be set", Date.valueOf("1995-05-20"), newPatient.getDateOfBirth());
        assertEquals("Gender should be FEMALE", Patient.Gender.FEMALE, newPatient.getGender());
        assertEquals("Blood group should be B+", "B+", newPatient.getBloodGroup());
        assertEquals("Status should be ACTIVE by default", Patient.PatientStatus.ACTIVE, newPatient.getStatus());
    }

    @Test
    public void testSettersAndGetters() {
        patient.setPatientId(100);
        assertEquals("Patient ID should be 100", 100, patient.getPatientId());

        patient.setFirstName("Michael");
        assertEquals("First name should be Michael", "Michael", patient.getFirstName());

        patient.setLastName("Johnson");
        assertEquals("Last name should be Johnson", "Johnson", patient.getLastName());

        patient.setEmail("new@email.com");
        assertEquals("Email should be updated", "new@email.com", patient.getEmail());
    }
}
