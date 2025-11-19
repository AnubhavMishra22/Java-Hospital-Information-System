package com.hospital.database;

import com.hospital.model.Doctor;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Doctor operations
 */
public class DoctorDAO {

    /**
     * Get all doctors
     */
    public static List<Doctor> getAllDoctors() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        List<Doctor> doctors = new ArrayList<>();

        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.createStatement();
            String query = "SELECT d.*, u.full_name, u.email, u.phone FROM doctors d " +
                          "INNER JOIN users u ON d.user_id = u.user_id " +
                          "WHERE u.status = 'ACTIVE' ORDER BY u.full_name";
            rs = stmt.executeQuery(query);

            while (rs.next()) {
                doctors.add(extractDoctorFromResultSet(rs));
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
        return doctors;
    }

    /**
     * Get doctor by ID
     */
    public static Doctor getDoctorById(int doctorId) {
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        Doctor doctor = null;

        try {
            conn = DatabaseConnection.getConnection();
            String query = "SELECT d.*, u.full_name, u.email, u.phone FROM doctors d " +
                          "INNER JOIN users u ON d.user_id = u.user_id " +
                          "WHERE d.doctor_id = ?";
            pst = conn.prepareStatement(query);
            pst.setInt(1, doctorId);

            rs = pst.executeQuery();
            if (rs.next()) {
                doctor = extractDoctorFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResultSet(rs);
            DatabaseConnection.closePreparedStatement(pst);
        }
        return doctor;
    }

    /**
     * Get doctor by User ID
     */
    public static Doctor getDoctorByUserId(int userId) {
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        Doctor doctor = null;

        try {
            conn = DatabaseConnection.getConnection();
            String query = "SELECT d.*, u.full_name, u.email, u.phone FROM doctors d " +
                          "INNER JOIN users u ON d.user_id = u.user_id " +
                          "WHERE d.user_id = ?";
            pst = conn.prepareStatement(query);
            pst.setInt(1, userId);

            rs = pst.executeQuery();
            if (rs.next()) {
                doctor = extractDoctorFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResultSet(rs);
            DatabaseConnection.closePreparedStatement(pst);
        }
        return doctor;
    }

    /**
     * Extract Doctor object from ResultSet
     */
    private static Doctor extractDoctorFromResultSet(ResultSet rs) throws SQLException {
        Doctor doctor = new Doctor();
        doctor.setDoctorId(rs.getInt("doctor_id"));
        doctor.setUserId(rs.getInt("user_id"));
        doctor.setSpecialization(rs.getString("specialization"));
        doctor.setLicenseNumber(rs.getString("license_number"));
        doctor.setDepartmentId(rs.getInt("department_id"));
        doctor.setQualification(rs.getString("qualification"));
        doctor.setExperienceYears(rs.getInt("experience_years"));
        doctor.setConsultationFee(rs.getDouble("consultation_fee"));
        doctor.setFullName(rs.getString("full_name"));
        doctor.setEmail(rs.getString("email"));
        doctor.setPhone(rs.getString("phone"));
        return doctor;
    }
}
