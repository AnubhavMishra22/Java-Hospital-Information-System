package com.hospital.database;

import com.hospital.model.Patient;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Patient operations
 */
public class PatientDAO {

    /**
     * Add new patient
     */
    public static int addPatient(Patient patient) {
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        int patientId = -1;

        try {
            conn = DatabaseConnection.getConnection();
            String query = "INSERT INTO patients (first_name, last_name, date_of_birth, gender, " +
                          "blood_group, phone, email, address, emergency_contact, emergency_phone, status) " +
                          "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            pst = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            pst.setString(1, patient.getFirstName());
            pst.setString(2, patient.getLastName());
            pst.setDate(3, patient.getDateOfBirth());
            pst.setString(4, patient.getGender().name());
            pst.setString(5, patient.getBloodGroup());
            pst.setString(6, patient.getPhone());
            pst.setString(7, patient.getEmail());
            pst.setString(8, patient.getAddress());
            pst.setString(9, patient.getEmergencyContact());
            pst.setString(10, patient.getEmergencyPhone());
            pst.setString(11, patient.getStatus().name());

            int result = pst.executeUpdate();
            if (result > 0) {
                rs = pst.getGeneratedKeys();
                if (rs.next()) {
                    patientId = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResultSet(rs);
            DatabaseConnection.closePreparedStatement(pst);
        }
        return patientId;
    }

    /**
     * Update patient
     */
    public static boolean updatePatient(Patient patient) {
        Connection conn = null;
        PreparedStatement pst = null;

        try {
            conn = DatabaseConnection.getConnection();
            String query = "UPDATE patients SET first_name=?, last_name=?, date_of_birth=?, " +
                          "gender=?, blood_group=?, phone=?, email=?, address=?, " +
                          "emergency_contact=?, emergency_phone=?, status=? WHERE patient_id=?";
            pst = conn.prepareStatement(query);
            pst.setString(1, patient.getFirstName());
            pst.setString(2, patient.getLastName());
            pst.setDate(3, patient.getDateOfBirth());
            pst.setString(4, patient.getGender().name());
            pst.setString(5, patient.getBloodGroup());
            pst.setString(6, patient.getPhone());
            pst.setString(7, patient.getEmail());
            pst.setString(8, patient.getAddress());
            pst.setString(9, patient.getEmergencyContact());
            pst.setString(10, patient.getEmergencyPhone());
            pst.setString(11, patient.getStatus().name());
            pst.setInt(12, patient.getPatientId());

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
     * Get all patients
     */
    public static List<Patient> getAllPatients() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        List<Patient> patients = new ArrayList<>();

        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM patients WHERE status != 'INACTIVE' ORDER BY first_name, last_name");

            while (rs.next()) {
                patients.add(extractPatientFromResultSet(rs));
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
        return patients;
    }

    /**
     * Count active patients (same filter as {@link #getAllPatients()}).
     */
    public static int countActivePatients() {
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                return 0;
            }
            String query = "SELECT COUNT(*) FROM patients WHERE status != 'INACTIVE'";
            pst = conn.prepareStatement(query);
            rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("PatientDAO.countActivePatients: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResultSet(rs);
            DatabaseConnection.closePreparedStatement(pst);
        }
        return 0;
    }

    /**
     * Search patients by name or phone
     */
    public static List<Patient> searchPatients(String keyword) {
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        List<Patient> patients = new ArrayList<>();

        try {
            conn = DatabaseConnection.getConnection();
            String query = "SELECT * FROM patients WHERE " +
                          "(first_name LIKE ? OR last_name LIKE ? OR phone LIKE ?) " +
                          "AND status != 'INACTIVE' ORDER BY first_name, last_name";
            pst = conn.prepareStatement(query);
            String searchPattern = "%" + keyword + "%";
            pst.setString(1, searchPattern);
            pst.setString(2, searchPattern);
            pst.setString(3, searchPattern);

            rs = pst.executeQuery();
            while (rs.next()) {
                patients.add(extractPatientFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResultSet(rs);
            DatabaseConnection.closePreparedStatement(pst);
        }
        return patients;
    }

    /**
     * Get patient by ID
     */
    public static Patient getPatientById(int patientId) {
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        Patient patient = null;

        try {
            conn = DatabaseConnection.getConnection();
            String query = "SELECT * FROM patients WHERE patient_id = ?";
            pst = conn.prepareStatement(query);
            pst.setInt(1, patientId);

            rs = pst.executeQuery();
            if (rs.next()) {
                patient = extractPatientFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResultSet(rs);
            DatabaseConnection.closePreparedStatement(pst);
        }
        return patient;
    }

    /**
     * Extract Patient object from ResultSet
     */
    private static Patient extractPatientFromResultSet(ResultSet rs) throws SQLException {
        Patient patient = new Patient();
        patient.setPatientId(rs.getInt("patient_id"));
        patient.setFirstName(rs.getString("first_name"));
        patient.setLastName(rs.getString("last_name"));
        patient.setDateOfBirth(rs.getDate("date_of_birth"));
        patient.setGender(Patient.Gender.valueOf(rs.getString("gender")));
        patient.setBloodGroup(rs.getString("blood_group"));
        patient.setPhone(rs.getString("phone"));
        patient.setEmail(rs.getString("email"));
        patient.setAddress(rs.getString("address"));
        patient.setEmergencyContact(rs.getString("emergency_contact"));
        patient.setEmergencyPhone(rs.getString("emergency_phone"));
        patient.setRegistrationDate(rs.getTimestamp("registration_date"));
        patient.setStatus(Patient.PatientStatus.valueOf(rs.getString("status")));
        return patient;
    }
}
