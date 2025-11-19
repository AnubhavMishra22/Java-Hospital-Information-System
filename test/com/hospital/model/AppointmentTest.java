package com.hospital.model;

import com.hospital.utils.TestDataFactory;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.sql.Date;
import java.sql.Time;

/**
 * Unit tests for Appointment model class
 */
public class AppointmentTest {

    private Appointment appointment;

    @Before
    public void setUp() {
        appointment = TestDataFactory.createTestAppointment();
    }

    @Test
    public void testAppointmentCreation() {
        assertNotNull("Appointment should not be null", appointment);
        assertEquals("Appointment ID should be 1", 1, appointment.getAppointmentId());
        assertEquals("Patient ID should be 1", 1, appointment.getPatientId());
        assertEquals("Doctor ID should be 1", 1, appointment.getDoctorId());
    }

    @Test
    public void testAppointmentDateTime() {
        assertNotNull("Appointment date should not be null", appointment.getAppointmentDate());
        assertNotNull("Appointment time should not be null", appointment.getAppointmentTime());

        Date newDate = Date.valueOf("2024-12-15");
        Time newTime = Time.valueOf("14:30:00");

        appointment.setAppointmentDate(newDate);
        appointment.setAppointmentTime(newTime);

        assertEquals("Date should be updated", newDate, appointment.getAppointmentDate());
        assertEquals("Time should be updated", newTime, appointment.getAppointmentTime());
    }

    @Test
    public void testAppointmentStatus() {
        assertEquals("Status should be SCHEDULED", Appointment.AppointmentStatus.SCHEDULED,
                    appointment.getStatus());

        appointment.setStatus(Appointment.AppointmentStatus.COMPLETED);
        assertEquals("Status should be COMPLETED", Appointment.AppointmentStatus.COMPLETED,
                    appointment.getStatus());

        appointment.setStatus(Appointment.AppointmentStatus.CANCELLED);
        assertEquals("Status should be CANCELLED", Appointment.AppointmentStatus.CANCELLED,
                    appointment.getStatus());

        appointment.setStatus(Appointment.AppointmentStatus.NO_SHOW);
        assertEquals("Status should be NO_SHOW", Appointment.AppointmentStatus.NO_SHOW,
                    appointment.getStatus());
    }

    @Test
    public void testAllStatusesExist() {
        Appointment.AppointmentStatus[] statuses = Appointment.AppointmentStatus.values();
        assertEquals("Should have 4 statuses", 4, statuses.length);
    }

    @Test
    public void testReasonAndNotes() {
        assertEquals("Reason should match", "Regular checkup", appointment.getReason());
        assertTrue("Notes should contain 'chest pain'",
                  appointment.getNotes().contains("chest pain"));

        appointment.setReason("Follow-up visit");
        appointment.setNotes("Patient feeling better");

        assertEquals("Reason should be updated", "Follow-up visit", appointment.getReason());
        assertEquals("Notes should be updated", "Patient feeling better", appointment.getNotes());
    }

    @Test
    public void testExtendedFields() {
        assertEquals("Patient name should match", "John Doe", appointment.getPatientName());
        assertEquals("Doctor name should match", "Dr. Test Doctor", appointment.getDoctorName());

        appointment.setPatientName("Jane Smith");
        appointment.setDoctorName("Dr. New Doctor");

        assertEquals("Patient name should be updated", "Jane Smith", appointment.getPatientName());
        assertEquals("Doctor name should be updated", "Dr. New Doctor", appointment.getDoctorName());
    }

    @Test
    public void testCreatedByField() {
        assertEquals("Created by should be 1", 1, appointment.getCreatedBy());

        appointment.setCreatedBy(5);
        assertEquals("Created by should be updated", 5, appointment.getCreatedBy());
    }

    @Test
    public void testParameterizedConstructor() {
        Appointment newApt = new Appointment(2, 3, Date.valueOf("2024-12-20"),
                                            Time.valueOf("15:00:00"), "Annual checkup", 4);

        assertEquals("Patient ID should be 2", 2, newApt.getPatientId());
        assertEquals("Doctor ID should be 3", 3, newApt.getDoctorId());
        assertEquals("Date should match", Date.valueOf("2024-12-20"), newApt.getAppointmentDate());
        assertEquals("Time should match", Time.valueOf("15:00:00"), newApt.getAppointmentTime());
        assertEquals("Reason should match", "Annual checkup", newApt.getReason());
        assertEquals("Created by should be 4", 4, newApt.getCreatedBy());
        assertEquals("Default status should be SCHEDULED", Appointment.AppointmentStatus.SCHEDULED,
                    newApt.getStatus());
    }

    @Test
    public void testAllGettersAndSetters() {
        appointment.setAppointmentId(100);
        assertEquals("Appointment ID should be 100", 100, appointment.getAppointmentId());

        appointment.setPatientId(200);
        assertEquals("Patient ID should be 200", 200, appointment.getPatientId());

        appointment.setDoctorId(300);
        assertEquals("Doctor ID should be 300", 300, appointment.getDoctorId());
    }
}
