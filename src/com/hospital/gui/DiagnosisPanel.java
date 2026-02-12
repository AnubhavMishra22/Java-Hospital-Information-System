package com.hospital.gui;

import com.hospital.database.DiagnosisDAO;
import com.hospital.database.PatientDAO;
import com.hospital.database.DoctorDAO;
import com.hospital.database.AppointmentDAO;
import com.hospital.model.Diagnosis;
import com.hospital.model.Patient;
import com.hospital.model.Doctor;
import com.hospital.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.util.List;

/**
 * Diagnosis Management Panel
 */
public class DiagnosisPanel extends JPanel {

    private User currentUser;
    private JTable diagnosisTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;

    public DiagnosisPanel(User user) {
        this.currentUser = user;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title Panel
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(Color.WHITE);
        JLabel titleLabel = new JLabel("Diagnosis Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titlePanel.add(titleLabel);

        // Control Panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.setBackground(Color.WHITE);

        JLabel searchLabel = new JLabel("Search by Patient ID:");
        searchLabel.setFont(new Font("Arial", Font.BOLD, 14));
        searchField = new JTextField(10);

        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> searchDiagnoses());

        JButton addButton = new JButton("Add Diagnosis");
        addButton.setBackground(Color.GREEN);
        addButton.setForeground(Color.BLACK);
        addButton.setOpaque(true);
        addButton.setBorderPainted(true);
        addButton.addActionListener(e -> showAddDiagnosisDialog());

        controlPanel.add(searchLabel);
        controlPanel.add(searchField);
        controlPanel.add(searchButton);
        controlPanel.add(addButton);

        // Table
        String[] columns = {"ID", "Patient", "Doctor", "Date", "Symptoms", "Diagnosis", "Follow-up"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        diagnosisTable = new JTable(tableModel);
        diagnosisTable.setFont(new Font("Arial", Font.PLAIN, 12));
        diagnosisTable.setRowHeight(25);
        diagnosisTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        JScrollPane scrollPane = new JScrollPane(diagnosisTable);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.WHITE);

        JButton viewButton = new JButton("View Details");
        viewButton.addActionListener(e -> viewDiagnosisDetails());

        buttonPanel.add(viewButton);

        // Add components
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.add(titlePanel, BorderLayout.NORTH);
        topPanel.add(controlPanel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void searchDiagnoses() {
        String patientIdStr = searchField.getText().trim();
        if (patientIdStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a patient ID");
            return;
        }

        try {
            int patientId = Integer.parseInt(patientIdStr);
            tableModel.setRowCount(0);
            List<Diagnosis> diagnoses = DiagnosisDAO.getDiagnosesByPatient(patientId);

            for (Diagnosis diagnosis : diagnoses) {
                Object[] row = {
                    diagnosis.getDiagnosisId(),
                    diagnosis.getPatientName(),
                    diagnosis.getDoctorName(),
                    diagnosis.getDiagnosisDate(),
                    truncateText(diagnosis.getSymptoms(), 30),
                    truncateText(diagnosis.getDiagnosis(), 30),
                    diagnosis.getFollowUpDate()
                };
                tableModel.addRow(row);
            }

            if (diagnoses.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No diagnoses found for this patient");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid patient ID", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAddDiagnosisDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Diagnosis", true);
        dialog.setSize(600, 550);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Get patients and doctors
        List<Patient> patients = PatientDAO.getAllPatients();
        List<Doctor> doctors = DoctorDAO.getAllDoctors();

        JTextField appointmentIdField = new JTextField(15);
        JComboBox<Patient> patientCombo = new JComboBox<>(patients.toArray(new Patient[0]));
        JComboBox<Doctor> doctorCombo = new JComboBox<>(doctors.toArray(new Doctor[0]));
        JTextArea symptomsArea = new JTextArea(4, 20);
        JTextArea diagnosisArea = new JTextArea(4, 20);
        JTextArea notesArea = new JTextArea(3, 20);
        JTextField followUpDateField = new JTextField(15); // Format: YYYY-MM-DD

        int row = 0;
        addFormField(panel, gbc, row++, "Appointment ID:", appointmentIdField);
        addFormField(panel, gbc, row++, "Patient:", patientCombo);
        addFormField(panel, gbc, row++, "Doctor:", doctorCombo);
        addFormField(panel, gbc, row++, "Symptoms:", new JScrollPane(symptomsArea));
        addFormField(panel, gbc, row++, "Diagnosis:", new JScrollPane(diagnosisArea));
        addFormField(panel, gbc, row++, "Notes:", new JScrollPane(notesArea));
        addFormField(panel, gbc, row++, "Follow-up Date (YYYY-MM-DD):", followUpDateField);

        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> {
            try {
                Patient selectedPatient = (Patient) patientCombo.getSelectedItem();
                Doctor selectedDoctor = (Doctor) doctorCombo.getSelectedItem();

                if (selectedPatient == null || selectedDoctor == null) {
                    JOptionPane.showMessageDialog(dialog, "Please select patient and doctor");
                    return;
                }

                // Validate appointment ID
                String appointmentIdText = appointmentIdField.getText().trim();
                if (appointmentIdText.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please enter Appointment ID");
                    return;
                }

                int appointmentId;
                try {
                    appointmentId = Integer.parseInt(appointmentIdText);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "Invalid Appointment ID. Please enter a number.");
                    return;
                }

                // Validate required fields
                if (symptomsArea.getText().trim().isEmpty() || diagnosisArea.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Symptoms and Diagnosis fields are required");
                    return;
                }

                Diagnosis diagnosis = new Diagnosis(
                    appointmentId,
                    selectedPatient.getPatientId(),
                    selectedDoctor.getDoctorId(),
                    symptomsArea.getText().trim(),
                    diagnosisArea.getText().trim()
                );
                diagnosis.setNotes(notesArea.getText().trim());

                String followUpDateStr = followUpDateField.getText().trim();
                if (!followUpDateStr.isEmpty()) {
                    try {
                        diagnosis.setFollowUpDate(Date.valueOf(followUpDateStr));
                    } catch (IllegalArgumentException ex) {
                        JOptionPane.showMessageDialog(dialog, "Invalid date format. Use YYYY-MM-DD (e.g., 2026-03-15)");
                        return;
                    }
                }

                int diagnosisId = DiagnosisDAO.addDiagnosis(diagnosis);
                if (diagnosisId > 0) {
                    JOptionPane.showMessageDialog(dialog, "Diagnosis added successfully!");
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to add diagnosis. Check if Appointment ID exists.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                // Show both exception type and message
                String errorMsg = ex.getClass().getSimpleName() + ": " +
                                 (ex.getMessage() != null ? ex.getMessage() : "Unknown error");
                JOptionPane.showMessageDialog(dialog, errorMsg, "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace(); // Print to console for debugging
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        dialog.add(new JScrollPane(panel));
        dialog.setVisible(true);
    }

    private void viewDiagnosisDetails() {
        int selectedRow = diagnosisTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a diagnosis");
            return;
        }

        int diagnosisId = (int) tableModel.getValueAt(selectedRow, 0);
        Diagnosis diagnosis = DiagnosisDAO.getDiagnosisById(diagnosisId);

        if (diagnosis != null) {
            JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Diagnosis Details", true);
            dialog.setSize(500, 450);
            dialog.setLocationRelativeTo(this);

            JPanel panel = new JPanel(new BorderLayout(10, 10));
            panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            JTextArea detailsArea = new JTextArea();
            detailsArea.setEditable(false);
            detailsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            detailsArea.setText(String.format(
                "Diagnosis ID: %d\nPatient: %s\nDoctor: %s\nDate: %s\n\n" +
                "SYMPTOMS:\n%s\n\nDIAGNOSIS:\n%s\n\nNOTES:\n%s\n\nFollow-up Date: %s",
                diagnosis.getDiagnosisId(), diagnosis.getPatientName(), diagnosis.getDoctorName(),
                diagnosis.getDiagnosisDate(), diagnosis.getSymptoms(), diagnosis.getDiagnosis(),
                diagnosis.getNotes(), diagnosis.getFollowUpDate()
            ));

            panel.add(new JScrollPane(detailsArea), BorderLayout.CENTER);

            JButton closeButton = new JButton("Close");
            closeButton.addActionListener(e -> dialog.dispose());
            JPanel buttonPanel = new JPanel();
            buttonPanel.add(closeButton);
            panel.add(buttonPanel, BorderLayout.SOUTH);

            dialog.add(panel);
            dialog.setVisible(true);
        }
    }

    private String truncateText(String text, int length) {
        if (text == null) return "";
        return text.length() > length ? text.substring(0, length) + "..." : text;
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, int row, String label, Component field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        JLabel jLabel = new JLabel(label);
        jLabel.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(jLabel, gbc);

        gbc.gridx = 1;
        panel.add(field, gbc);
    }
}
