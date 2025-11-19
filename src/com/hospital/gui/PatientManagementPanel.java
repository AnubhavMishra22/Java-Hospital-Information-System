package com.hospital.gui;

import com.hospital.database.PatientDAO;
import com.hospital.model.Patient;
import com.hospital.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.util.List;

/**
 * Patient Management Panel
 */
public class PatientManagementPanel extends JPanel {

    private User currentUser;
    private JTable patientTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;

    public PatientManagementPanel(User user) {
        this.currentUser = user;
        initComponents();
        loadPatients();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title Panel
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(Color.WHITE);
        JLabel titleLabel = new JLabel("Patient Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titlePanel.add(titleLabel);

        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(Color.WHITE);

        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font("Arial", Font.BOLD, 14));
        searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> searchPatients());

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadPatients());

        JButton addButton = new JButton("Add New Patient");
        addButton.setBackground(new Color(34, 139, 34));
        addButton.setForeground(Color.WHITE);
        addButton.addActionListener(e -> showAddPatientDialog());

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(refreshButton);
        searchPanel.add(addButton);

        // Table
        String[] columns = {"ID", "Name", "DOB", "Gender", "Blood Group", "Phone", "Email", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        patientTable = new JTable(tableModel);
        patientTable.setFont(new Font("Arial", Font.PLAIN, 12));
        patientTable.setRowHeight(25);
        patientTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        JScrollPane scrollPane = new JScrollPane(patientTable);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.WHITE);

        JButton viewButton = new JButton("View Details");
        viewButton.addActionListener(e -> viewPatientDetails());

        JButton editButton = new JButton("Edit Patient");
        editButton.addActionListener(e -> editPatient());

        buttonPanel.add(viewButton);
        buttonPanel.add(editButton);

        // Add components
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.add(titlePanel, BorderLayout.NORTH);
        topPanel.add(searchPanel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadPatients() {
        tableModel.setRowCount(0);
        List<Patient> patients = PatientDAO.getAllPatients();

        for (Patient patient : patients) {
            Object[] row = {
                patient.getPatientId(),
                patient.getFullName(),
                patient.getDateOfBirth(),
                patient.getGender(),
                patient.getBloodGroup(),
                patient.getPhone(),
                patient.getEmail(),
                patient.getStatus()
            };
            tableModel.addRow(row);
        }
    }

    private void searchPatients() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            loadPatients();
            return;
        }

        tableModel.setRowCount(0);
        List<Patient> patients = PatientDAO.searchPatients(keyword);

        for (Patient patient : patients) {
            Object[] row = {
                patient.getPatientId(),
                patient.getFullName(),
                patient.getDateOfBirth(),
                patient.getGender(),
                patient.getBloodGroup(),
                patient.getPhone(),
                patient.getEmail(),
                patient.getStatus()
            };
            tableModel.addRow(row);
        }
    }

    private void showAddPatientDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New Patient", true);
        dialog.setSize(500, 600);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField firstNameField = new JTextField(20);
        JTextField lastNameField = new JTextField(20);
        JTextField dobField = new JTextField(20); // Format: YYYY-MM-DD
        JComboBox<Patient.Gender> genderCombo = new JComboBox<>(Patient.Gender.values());
        JTextField bloodGroupField = new JTextField(10);
        JTextField phoneField = new JTextField(20);
        JTextField emailField = new JTextField(20);
        JTextArea addressArea = new JTextArea(3, 20);
        JTextField emergencyContactField = new JTextField(20);
        JTextField emergencyPhoneField = new JTextField(20);

        int row = 0;
        addFormField(panel, gbc, row++, "First Name:", firstNameField);
        addFormField(panel, gbc, row++, "Last Name:", lastNameField);
        addFormField(panel, gbc, row++, "Date of Birth (YYYY-MM-DD):", dobField);
        addFormField(panel, gbc, row++, "Gender:", genderCombo);
        addFormField(panel, gbc, row++, "Blood Group:", bloodGroupField);
        addFormField(panel, gbc, row++, "Phone:", phoneField);
        addFormField(panel, gbc, row++, "Email:", emailField);
        addFormField(panel, gbc, row++, "Address:", new JScrollPane(addressArea));
        addFormField(panel, gbc, row++, "Emergency Contact:", emergencyContactField);
        addFormField(panel, gbc, row++, "Emergency Phone:", emergencyPhoneField);

        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> {
            try {
                Patient patient = new Patient(
                    firstNameField.getText().trim(),
                    lastNameField.getText().trim(),
                    Date.valueOf(dobField.getText().trim()),
                    (Patient.Gender) genderCombo.getSelectedItem(),
                    bloodGroupField.getText().trim(),
                    phoneField.getText().trim(),
                    emailField.getText().trim(),
                    addressArea.getText().trim(),
                    emergencyContactField.getText().trim(),
                    emergencyPhoneField.getText().trim()
                );

                int patientId = PatientDAO.addPatient(patient);
                if (patientId > 0) {
                    JOptionPane.showMessageDialog(dialog, "Patient added successfully!");
                    dialog.dispose();
                    loadPatients();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to add patient", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

    private void viewPatientDetails() {
        int selectedRow = patientTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a patient");
            return;
        }

        int patientId = (int) tableModel.getValueAt(selectedRow, 0);
        Patient patient = PatientDAO.getPatientById(patientId);

        if (patient != null) {
            String details = String.format(
                "Patient ID: %d\nName: %s\nDate of Birth: %s\nGender: %s\nBlood Group: %s\n" +
                "Phone: %s\nEmail: %s\nAddress: %s\nEmergency Contact: %s (%s)\nStatus: %s",
                patient.getPatientId(), patient.getFullName(), patient.getDateOfBirth(),
                patient.getGender(), patient.getBloodGroup(), patient.getPhone(),
                patient.getEmail(), patient.getAddress(), patient.getEmergencyContact(),
                patient.getEmergencyPhone(), patient.getStatus()
            );

            JOptionPane.showMessageDialog(this, details, "Patient Details", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void editPatient() {
        int selectedRow = patientTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a patient");
            return;
        }

        JOptionPane.showMessageDialog(this, "Edit functionality - Implementation similar to Add Patient");
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
