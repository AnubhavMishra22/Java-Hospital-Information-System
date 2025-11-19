package com.hospital.database;

import com.hospital.model.Appointment;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Appointment operations
 */
public class AppointmentDAO {

    /**
     * Add new appointment
     */
    public static int addAppointment(Appointment appointment) {
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        int appointmentId = -1;

        try {
            conn = DatabaseConnection.getConnection();
            String query = "INSERT INTO appointments (patient_id, doctor_id, appointment_date, " +
                          "appointment_time, status, reason, notes, created_by) " +
                          "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            pst = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            pst.setInt(1, appointment.getPatientId());
            pst.setInt(2, appointment.getDoctorId());
            pst.setDate(3, appointment.getAppointmentDate());
            pst.setTime(4, appointment.getAppointmentTime());
            pst.setString(5, appointment.getStatus().name());
            pst.setString(6, appointment.getReason());
            pst.setString(7, appointment.getNotes());
            pst.setInt(8, appointment.getCreatedBy());

            int result = pst.executeUpdate();
            if (result > 0) {
                rs = pst.getGeneratedKeys();
                if (rs.next()) {
                    appointmentId = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResultSet(rs);
            DatabaseConnection.closePreparedStatement(pst);
        }
        return appointmentId;
    }

    /**
     * Update appointment status
     */
    public static boolean updateAppointmentStatus(int appointmentId, Appointment.AppointmentStatus status) {
        Connection conn = null;
        PreparedStatement pst = null;

        try {
            conn = DatabaseConnection.getConnection();
            String query = "UPDATE appointments SET status = ? WHERE appointment_id = ?";
            pst = conn.prepareStatement(query);
            pst.setString(1, status.name());
            pst.setInt(2, appointmentId);

            int result = pst.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DatabaseConnection.closePreparedStatement(pst);
        }
    }

    /**
     * Get appointments by date
     */
    public static List<Appointment> getAppointmentsByDate(Date date) {
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        List<Appointment> appointments = new ArrayList<>();

        try {
            conn = DatabaseConnection.getConnection();
            String query = "SELECT a.*, " +
                          "CONCAT(p.first_name, ' ', p.last_name) as patient_name, " +
                          "u.full_name as doctor_name " +
                          "FROM appointments a " +
                          "INNER JOIN patients p ON a.patient_id = p.patient_id " +
                          "INNER JOIN doctors d ON a.doctor_id = d.doctor_id " +
                          "INNER JOIN users u ON d.user_id = u.user_id " +
                          "WHERE a.appointment_date = ? " +
                          "ORDER BY a.appointment_time";
            pst = conn.prepareStatement(query);
            pst.setDate(1, date);

            rs = pst.executeQuery();
            while (rs.next()) {
                appointments.add(extractAppointmentFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResultSet(rs);
            DatabaseConnection.closePreparedStatement(pst);
        }
        return appointments;
    }

    /**
     * Get appointments by patient
     */
    public static List<Appointment> getAppointmentsByPatient(int patientId) {
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        List<Appointment> appointments = new ArrayList<>();

        try {
            conn = DatabaseConnection.getConnection();
            String query = "SELECT a.*, " +
                          "CONCAT(p.first_name, ' ', p.last_name) as patient_name, " +
                          "u.full_name as doctor_name " +
                          "FROM appointments a " +
                          "INNER JOIN patients p ON a.patient_id = p.patient_id " +
                          "INNER JOIN doctors d ON a.doctor_id = d.doctor_id " +
                          "INNER JOIN users u ON d.user_id = u.user_id " +
                          "WHERE a.patient_id = ? " +
                          "ORDER BY a.appointment_date DESC, a.appointment_time DESC";
            pst = conn.prepareStatement(query);
            pst.setInt(1, patientId);

            rs = pst.executeQuery();
            while (rs.next()) {
                appointments.add(extractAppointmentFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResultSet(rs);
            DatabaseConnection.closePreparedStatement(pst);
        }
        return appointments;
    }

    /**
     * Get appointments by doctor
     */
    public static List<Appointment> getAppointmentsByDoctor(int doctorId) {
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        List<Appointment> appointments = new ArrayList<>();

        try {
            conn = DatabaseConnection.getConnection();
            String query = "SELECT a.*, " +
                          "CONCAT(p.first_name, ' ', p.last_name) as patient_name, " +
                          "u.full_name as doctor_name " +
                          "FROM appointments a " +
                          "INNER JOIN patients p ON a.patient_id = p.patient_id " +
                          "INNER JOIN doctors d ON a.doctor_id = d.doctor_id " +
                          "INNER JOIN users u ON d.user_id = u.user_id " +
                          "WHERE a.doctor_id = ? " +
                          "ORDER BY a.appointment_date DESC, a.appointment_time DESC";
            pst = conn.prepareStatement(query);
            pst.setInt(1, doctorId);

            rs = pst.executeQuery();
            while (rs.next()) {
                appointments.add(extractAppointmentFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResultSet(rs);
            DatabaseConnection.closePreparedStatement(pst);
        }
        return appointments;
    }

    /**
     * Get all upcoming appointments
     */
    public static List<Appointment> getUpcomingAppointments() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        List<Appointment> appointments = new ArrayList<>();

        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.createStatement();
            String query = "SELECT a.*, " +
                          "CONCAT(p.first_name, ' ', p.last_name) as patient_name, " +
                          "u.full_name as doctor_name " +
                          "FROM appointments a " +
                          "INNER JOIN patients p ON a.patient_id = p.patient_id " +
                          "INNER JOIN doctors d ON a.doctor_id = d.doctor_id " +
                          "INNER JOIN users u ON d.user_id = u.user_id " +
                          "WHERE a.appointment_date >= CURDATE() AND a.status = 'SCHEDULED' " +
                          "ORDER BY a.appointment_date, a.appointment_time";
            rs = stmt.executeQuery(query);

            while (rs.next()) {
                appointments.add(extractAppointmentFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResultSet(rs);
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return appointments;
    }

    /**
     * Extract Appointment object from ResultSet
     */
    private static Appointment extractAppointmentFromResultSet(ResultSet rs) throws SQLException {
        Appointment appointment = new Appointment();
        appointment.setAppointmentId(rs.getInt("appointment_id"));
        appointment.setPatientId(rs.getInt("patient_id"));
        appointment.setDoctorId(rs.getInt("doctor_id"));
        appointment.setAppointmentDate(rs.getDate("appointment_date"));
        appointment.setAppointmentTime(rs.getTime("appointment_time"));
        appointment.setStatus(Appointment.AppointmentStatus.valueOf(rs.getString("status")));
        appointment.setReason(rs.getString("reason"));
        appointment.setNotes(rs.getString("notes"));
        appointment.setCreatedBy(rs.getInt("created_by"));
        appointment.setCreatedAt(rs.getTimestamp("created_at"));
        appointment.setPatientName(rs.getString("patient_name"));
        appointment.setDoctorName(rs.getString("doctor_name"));
        return appointment;
    }
}
