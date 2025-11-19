package com.hospital.database;

import com.hospital.model.Appointment;
import com.hospital.utils.TestDataFactory;
import org.junit.Test;
import static org.junit.Assert.*;
import java.sql.Date;

/**
 * Unit tests for AppointmentDAO
 */
public class AppointmentDAOTest {

    @Test
    public void testAppointmentDAOExists() {
        assertNotNull("AppointmentDAO class should exist", AppointmentDAO.class);
    }

    @Test
    public void testAddAppointmentMethod() throws NoSuchMethodException {
        AppointmentDAO.class.getDeclaredMethod("addAppointment", Appointment.class);
    }

    @Test
    public void testUpdateAppointmentStatusMethod() throws NoSuchMethodException {
        AppointmentDAO.class.getDeclaredMethod("updateAppointmentStatus",
            int.class, Appointment.AppointmentStatus.class);
    }

    @Test
    public void testGetAppointmentsByDateMethod() throws NoSuchMethodException {
        AppointmentDAO.class.getDeclaredMethod("getAppointmentsByDate", Date.class);
    }

    @Test
    public void testGetAppointmentsByPatientMethod() throws NoSuchMethodException {
        AppointmentDAO.class.getDeclaredMethod("getAppointmentsByPatient", int.class);
    }

    @Test
    public void testGetAppointmentsByDoctorMethod() throws NoSuchMethodException {
        AppointmentDAO.class.getDeclaredMethod("getAppointmentsByDoctor", int.class);
    }

    @Test
    public void testGetUpcomingAppointmentsMethod() throws NoSuchMethodException {
        AppointmentDAO.class.getDeclaredMethod("getUpcomingAppointments");
    }

    @Test
    public void testAppointmentObjectIntegrity() {
        Appointment apt = TestDataFactory.createTestAppointment();
        assertNotNull("Appointment should not be null", apt);
        assertNotNull("Appointment date should not be null", apt.getAppointmentDate());
        assertNotNull("Appointment time should not be null", apt.getAppointmentTime());
        assertTrue("Patient ID should be positive", apt.getPatientId() > 0);
        assertTrue("Doctor ID should be positive", apt.getDoctorId() > 0);
    }
}
