package com.hospital.database;

import com.hospital.model.Diagnosis;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Diagnosis operations
 */
public class DiagnosisDAO {

    /**
     * Per-calling-thread last error from {@link #addDiagnosis(Diagnosis)} (avoids cross-thread leakage on shared DB access).
     * <p>If you call {@code addDiagnosis} on a background thread, you must read this on the <strong>same</strong> thread
     * (e.g. inside {@code doInBackground}). Do not call from the EDT after a background {@code addDiagnosis}.
     * </p>
     */
    private static final ThreadLocal<String> lastAddDiagnosisError = new ThreadLocal<>();

    /**
     * @see #addDiagnosis(Diagnosis) for thread semantics (only valid on the same thread that invoked {@code addDiagnosis})
     */
    public static String getLastAddDiagnosisError() {
        return lastAddDiagnosisError.get();
    }

    private static void setLastAddDiagnosisError(String message) {
        if (message == null || message.isEmpty()) {
            lastAddDiagnosisError.remove();
        } else {
            lastAddDiagnosisError.set(message);
        }
    }

    /**
     * Add new diagnosis
     */
    public static int addDiagnosis(Diagnosis diagnosis) {
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        int diagnosisId = -1;
        lastAddDiagnosisError.remove();

        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                setLastAddDiagnosisError("Database connection is null.");
                return -1;
            }
            String query = "INSERT INTO diagnoses (appointment_id, patient_id, doctor_id, " +
                          "symptoms, diagnosis, notes, follow_up_date) " +
                          "VALUES (?, ?, ?, ?, ?, ?, ?)";
            pst = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            pst.setInt(1, diagnosis.getAppointmentId());
            pst.setInt(2, diagnosis.getPatientId());
            pst.setInt(3, diagnosis.getDoctorId());
            pst.setString(4, diagnosis.getSymptoms());
            pst.setString(5, diagnosis.getDiagnosis());
            if (diagnosis.getNotes() != null) {
                pst.setString(6, diagnosis.getNotes());
            } else {
                pst.setNull(6, Types.VARCHAR);
            }
            if (diagnosis.getFollowUpDate() != null) {
                pst.setDate(7, diagnosis.getFollowUpDate());
            } else {
                pst.setNull(7, Types.DATE);
            }

            int result = pst.executeUpdate();
            if (result == 0) {
                setLastAddDiagnosisError("INSERT affected 0 rows (nothing was written). Check foreign keys and DB permissions.");
            } else {
                rs = pst.getGeneratedKeys();
                if (rs != null && rs.next()) {
                    diagnosisId = rs.getInt(1);
                }
                DatabaseConnection.closeResultSet(rs);
                rs = null;
                if (diagnosisId <= 0) {
                    setLastAddDiagnosisError(
                        "Insert ran but generated keys were not returned. Avoid LAST_INSERT_ID() on a shared connection; "
                            + "enable RETURN_GENERATED_KEYS for the insert or check the JDBC driver / MySQL configuration.");
                }
            }
        } catch (SQLException e) {
            setLastAddDiagnosisError(e.getMessage());
            System.err.println("DiagnosisDAO.addDiagnosis: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResultSet(rs);
            DatabaseConnection.closePreparedStatement(pst);
        }
        return diagnosisId;
    }

    /**
     * Get diagnoses by patient
     */
    public static List<Diagnosis> getDiagnosesByPatient(int patientId) {
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        List<Diagnosis> diagnoses = new ArrayList<>();

        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                return diagnoses;
            }
            String query = "SELECT d.diagnosis_id, d.appointment_id, d.patient_id, d.doctor_id, " +
                          "d.diagnosis_date, d.symptoms, d.diagnosis, d.notes, d.follow_up_date, " +
                          "CONCAT(p.first_name, ' ', p.last_name) AS patient_name, " +
                          "u.full_name AS doctor_name " +
                          "FROM diagnoses d " +
                          "INNER JOIN patients p ON d.patient_id = p.patient_id " +
                          "LEFT JOIN doctors doc ON d.doctor_id = doc.doctor_id " +
                          "LEFT JOIN users u ON doc.user_id = u.user_id " +
                          "WHERE d.patient_id = ? " +
                          "ORDER BY d.diagnosis_date DESC";
            pst = conn.prepareStatement(query);
            pst.setInt(1, patientId);

            rs = pst.executeQuery();
            while (rs.next()) {
                diagnoses.add(extractDiagnosisFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("DiagnosisDAO.getDiagnosesByPatient: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResultSet(rs);
            DatabaseConnection.closePreparedStatement(pst);
        }
        return diagnoses;
    }

    /**
     * Get all diagnoses ordered by most recent first.
     */
    public static List<Diagnosis> getAllDiagnoses() {
        List<Diagnosis> diagnoses = new ArrayList<>();
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return diagnoses;
        }
        String query = "SELECT d.diagnosis_id, d.appointment_id, d.patient_id, d.doctor_id, " +
                      "d.diagnosis_date, d.symptoms, d.diagnosis, d.notes, d.follow_up_date, " +
                      "CONCAT(p.first_name, ' ', p.last_name) AS patient_name, " +
                      "u.full_name AS doctor_name " +
                      "FROM diagnoses d " +
                      "INNER JOIN patients p ON d.patient_id = p.patient_id " +
                      "LEFT JOIN doctors doc ON d.doctor_id = doc.doctor_id " +
                      "LEFT JOIN users u ON doc.user_id = u.user_id " +
                      "ORDER BY d.diagnosis_date DESC";
        try (PreparedStatement pst = conn.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                diagnoses.add(extractDiagnosisFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("DiagnosisDAO.getAllDiagnoses: " + e.getMessage());
            e.printStackTrace();
        }
        return diagnoses;
    }

    /**
     * Get diagnosis by ID
     */
    public static Diagnosis getDiagnosisById(int diagnosisId) {
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        Diagnosis diagnosis = null;

        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                return null;
            }
            String query = "SELECT d.diagnosis_id, d.appointment_id, d.patient_id, d.doctor_id, " +
                          "d.diagnosis_date, d.symptoms, d.diagnosis, d.notes, d.follow_up_date, " +
                          "CONCAT(p.first_name, ' ', p.last_name) AS patient_name, " +
                          "u.full_name AS doctor_name " +
                          "FROM diagnoses d " +
                          "INNER JOIN patients p ON d.patient_id = p.patient_id " +
                          "LEFT JOIN doctors doc ON d.doctor_id = doc.doctor_id " +
                          "LEFT JOIN users u ON doc.user_id = u.user_id " +
                          "WHERE d.diagnosis_id = ?";
            pst = conn.prepareStatement(query);
            pst.setInt(1, diagnosisId);

            rs = pst.executeQuery();
            if (rs.next()) {
                diagnosis = extractDiagnosisFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("DiagnosisDAO.getDiagnosisById: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResultSet(rs);
            DatabaseConnection.closePreparedStatement(pst);
        }
        return diagnosis;
    }

    /**
     * Extract Diagnosis object from ResultSet
     */
    private static Diagnosis extractDiagnosisFromResultSet(ResultSet rs) throws SQLException {
        Diagnosis diagnosis = new Diagnosis();
        diagnosis.setDiagnosisId(rs.getInt("diagnosis_id"));
        diagnosis.setAppointmentId(rs.getInt("appointment_id"));
        diagnosis.setPatientId(rs.getInt("patient_id"));
        diagnosis.setDoctorId(rs.getInt("doctor_id"));
        diagnosis.setDiagnosisDate(rs.getTimestamp("diagnosis_date"));
        diagnosis.setSymptoms(rs.getString("symptoms"));
        diagnosis.setDiagnosis(rs.getString("diagnosis"));
        diagnosis.setNotes(rs.getString("notes"));
        diagnosis.setFollowUpDate(rs.getDate("follow_up_date"));
        diagnosis.setPatientName(rs.getString("patient_name"));
        diagnosis.setDoctorName(rs.getString("doctor_name"));
        return diagnosis;
    }
}
