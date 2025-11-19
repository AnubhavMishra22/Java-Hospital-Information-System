package com.hospital.database;

import com.hospital.model.Patient;
import com.hospital.utils.TestDataFactory;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.List;

/**
 * Unit tests for PatientDAO
 * Note: These tests require a running MySQL database with test data
 * For true unit testing, use Mockito to mock database connections
 */
public class PatientDAOTest {

    @Test
    public void testPatientDAOExists() {
        assertNotNull("PatientDAO class should exist", PatientDAO.class);
    }

    @Test
    public void testAddPatientMethod() throws NoSuchMethodException {
        // Verify method signature exists
        PatientDAO.class.getDeclaredMethod("addPatient", Patient.class);
    }

    @Test
    public void testUpdatePatientMethod() throws NoSuchMethodException {
        PatientDAO.class.getDeclaredMethod("updatePatient", Patient.class);
    }

    @Test
    public void testGetAllPatientsMethod() throws NoSuchMethodException {
        PatientDAO.class.getDeclaredMethod("getAllPatients");
    }

    @Test
    public void testSearchPatientsMethod() throws NoSuchMethodException {
        PatientDAO.class.getDeclaredMethod("searchPatients", String.class);
    }

    @Test
    public void testGetPatientByIdMethod() throws NoSuchMethodException {
        PatientDAO.class.getDeclaredMethod("getPatientById", int.class);
    }

    @Test
    public void testPatientObjectIntegrity() {
        Patient patient = TestDataFactory.createTestPatient();
        assertNotNull("Patient object should not be null", patient);
        assertNotNull("Patient first name should not be null", patient.getFirstName());
        assertNotNull("Patient last name should not be null", patient.getLastName());
        assertNotNull("Patient phone should not be null", patient.getPhone());
    }

    @Test
    public void testTestDataFactoryCreatesValidPatients() {
        List<Patient> patients = TestDataFactory.createTestPatients();
        assertNotNull("Patient list should not be null", patients);
        assertTrue("Should create multiple patients", patients.size() > 0);

        for (Patient patient : patients) {
            assertNotNull("Patient should not be null", patient);
            assertNotNull("Patient name should not be null", patient.getFirstName());
            assertTrue("Patient ID should be set", patient.getPatientId() > 0);
        }
    }
}
