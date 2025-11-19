package com.hospital.database;

import com.hospital.model.Prescription;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Prescription operations
 */
public class PrescriptionDAO {

    /**
     * Add new prescription
     */
    public static int addPrescription(Prescription prescription) {
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        int prescriptionId = -1;

        try {
            conn = DatabaseConnection.getConnection();
            String query = "INSERT INTO prescriptions (diagnosis_id, patient_id, doctor_id, " +
                          "medication_name, dosage, frequency, duration, instructions) " +
                          "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            pst = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            pst.setInt(1, prescription.getDiagnosisId());
            pst.setInt(2, prescription.getPatientId());
            pst.setInt(3, prescription.getDoctorId());
            pst.setString(4, prescription.getMedicationName());
            pst.setString(5, prescription.getDosage());
            pst.setString(6, prescription.getFrequency());
            pst.setString(7, prescription.getDuration());
            pst.setString(8, prescription.getInstructions());

            int result = pst.executeUpdate();
            if (result > 0) {
                rs = pst.getGeneratedKeys();
                if (rs.next()) {
                    prescriptionId = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResultSet(rs);
            DatabaseConnection.closePreparedStatement(pst);
        }
        return prescriptionId;
    }

    /**
     * Get prescriptions by diagnosis
     */
    public static List<Prescription> getPrescriptionsByDiagnosis(int diagnosisId) {
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        List<Prescription> prescriptions = new ArrayList<>();

        try {
            conn = DatabaseConnection.getConnection();
            String query = "SELECT p.*, " +
                          "CONCAT(pat.first_name, ' ', pat.last_name) as patient_name, " +
                          "u.full_name as doctor_name " +
                          "FROM prescriptions p " +
                          "INNER JOIN patients pat ON p.patient_id = pat.patient_id " +
                          "INNER JOIN doctors d ON p.doctor_id = d.doctor_id " +
                          "INNER JOIN users u ON d.user_id = u.user_id " +
                          "WHERE p.diagnosis_id = ? " +
                          "ORDER BY p.prescription_date DESC";
            pst = conn.prepareStatement(query);
            pst.setInt(1, diagnosisId);

            rs = pst.executeQuery();
            while (rs.next()) {
                prescriptions.add(extractPrescriptionFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResultSet(rs);
            DatabaseConnection.closePreparedStatement(pst);
        }
        return prescriptions;
    }

    /**
     * Get prescriptions by patient
     */
    public static List<Prescription> getPrescriptionsByPatient(int patientId) {
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        List<Prescription> prescriptions = new ArrayList<>();

        try {
            conn = DatabaseConnection.getConnection();
            String query = "SELECT p.*, " +
                          "CONCAT(pat.first_name, ' ', pat.last_name) as patient_name, " +
                          "u.full_name as doctor_name " +
                          "FROM prescriptions p " +
                          "INNER JOIN patients pat ON p.patient_id = pat.patient_id " +
                          "INNER JOIN doctors d ON p.doctor_id = d.doctor_id " +
                          "INNER JOIN users u ON d.user_id = u.user_id " +
                          "WHERE p.patient_id = ? " +
                          "ORDER BY p.prescription_date DESC";
            pst = conn.prepareStatement(query);
            pst.setInt(1, patientId);

            rs = pst.executeQuery();
            while (rs.next()) {
                prescriptions.add(extractPrescriptionFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResultSet(rs);
            DatabaseConnection.closePreparedStatement(pst);
        }
        return prescriptions;
    }

    /**
     * Extract Prescription object from ResultSet
     */
    private static Prescription extractPrescriptionFromResultSet(ResultSet rs) throws SQLException {
        Prescription prescription = new Prescription();
        prescription.setPrescriptionId(rs.getInt("prescription_id"));
        prescription.setDiagnosisId(rs.getInt("diagnosis_id"));
        prescription.setPatientId(rs.getInt("patient_id"));
        prescription.setDoctorId(rs.getInt("doctor_id"));
        prescription.setPrescriptionDate(rs.getTimestamp("prescription_date"));
        prescription.setMedicationName(rs.getString("medication_name"));
        prescription.setDosage(rs.getString("dosage"));
        prescription.setFrequency(rs.getString("frequency"));
        prescription.setDuration(rs.getString("duration"));
        prescription.setInstructions(rs.getString("instructions"));
        prescription.setPatientName(rs.getString("patient_name"));
        prescription.setDoctorName(rs.getString("doctor_name"));
        return prescription;
    }
}
