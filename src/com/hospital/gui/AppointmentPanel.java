package com.hospital.gui;

import com.hospital.database.AppointmentDAO;
import com.hospital.database.PatientDAO;
import com.hospital.database.DoctorDAO;
import com.hospital.model.Appointment;
import com.hospital.model.Patient;
import com.hospital.model.Doctor;
import com.hospital.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.sql.Time;
import java.util.List;

/**
 * Appointment Management Panel
 */
public class AppointmentPanel extends JPanel {

    private User currentUser;
    private JTable appointmentTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> filterCombo;

    public AppointmentPanel(User user) {
        this.currentUser = user;
        initComponents();
        loadAppointments();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title Panel
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(Color.WHITE);
        JLabel titleLabel = new JLabel("Appointment Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titlePanel.add(titleLabel);

        // Control Panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.setBackground(Color.WHITE);

        JLabel filterLabel = new JLabel("Filter:");
        filterLabel.setFont(new Font("Arial", Font.BOLD, 14));
        filterCombo = new JComboBox<>(new String[]{"All Upcoming", "Today", "This Week"});

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadAppointments());

        JButton addButton = new JButton("Schedule Appointment");
        addButton.setBackground(new Color(34, 139, 34));
        addButton.setForeground(Color.WHITE);
        addButton.addActionListener(e -> showAddAppointmentDialog());

        controlPanel.add(filterLabel);
        controlPanel.add(filterCombo);
        controlPanel.add(refreshButton);
        controlPanel.add(addButton);

        // Table
        String[] columns = {"ID", "Patient", "Doctor", "Date", "Time", "Reason", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        appointmentTable = new JTable(tableModel);
        appointmentTable.setFont(new Font("Arial", Font.PLAIN, 12));
        appointmentTable.setRowHeight(25);
        appointmentTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        JScrollPane scrollPane = new JScrollPane(appointmentTable);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.WHITE);

        JButton completeButton = new JButton("Mark Completed");
        completeButton.addActionListener(e -> markCompleted());

        JButton cancelButton = new JButton("Cancel Appointment");
        cancelButton.setBackground(new Color(220, 20, 60));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.addActionListener(e -> cancelAppointment());

        buttonPanel.add(completeButton);
        buttonPanel.add(cancelButton);

        // Add components
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.add(titlePanel, BorderLayout.NORTH);
        topPanel.add(controlPanel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadAppointments() {
        tableModel.setRowCount(0);
        List<Appointment> appointments = AppointmentDAO.getUpcomingAppointments();

        for (Appointment appointment : appointments) {
            Object[] row = {
                appointment.getAppointmentId(),
                appointment.getPatientName(),
                appointment.getDoctorName(),
                appointment.getAppointmentDate(),
                appointment.getAppointmentTime(),
                appointment.getReason(),
                appointment.getStatus()
            };
            tableModel.addRow(row);
        }
    }

    private void showAddAppointmentDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Schedule Appointment", true);
        dialog.setSize(500, 450);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Get patients and doctors
        List<Patient> patients = PatientDAO.getAllPatients();
        List<Doctor> doctors = DoctorDAO.getAllDoctors();

        JComboBox<Patient> patientCombo = new JComboBox<>(patients.toArray(new Patient[0]));
        JComboBox<Doctor> doctorCombo = new JComboBox<>(doctors.toArray(new Doctor[0]));
        JTextField dateField = new JTextField(15); // Format: YYYY-MM-DD
        JTextField timeField = new JTextField(15); // Format: HH:MM:SS
        JTextArea reasonArea = new JTextArea(4, 20);
        JTextArea notesArea = new JTextArea(3, 20);

        int row = 0;
        addFormField(panel, gbc, row++, "Patient:", patientCombo);
        addFormField(panel, gbc, row++, "Doctor:", doctorCombo);
        addFormField(panel, gbc, row++, "Date (YYYY-MM-DD):", dateField);
        addFormField(panel, gbc, row++, "Time (HH:MM:SS):", timeField);
        addFormField(panel, gbc, row++, "Reason:", new JScrollPane(reasonArea));
        addFormField(panel, gbc, row++, "Notes:", new JScrollPane(notesArea));

        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Schedule");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> {
            try {
                Patient selectedPatient = (Patient) patientCombo.getSelectedItem();
                Doctor selectedDoctor = (Doctor) doctorCombo.getSelectedItem();

                if (selectedPatient == null || selectedDoctor == null) {
                    JOptionPane.showMessageDialog(dialog, "Please select patient and doctor");
                    return;
                }

                Appointment appointment = new Appointment(
                    selectedPatient.getPatientId(),
                    selectedDoctor.getDoctorId(),
                    Date.valueOf(dateField.getText().trim()),
                    Time.valueOf(timeField.getText().trim()),
                    reasonArea.getText().trim(),
                    currentUser.getUserId()
                );
                appointment.setNotes(notesArea.getText().trim());

                int appointmentId = AppointmentDAO.addAppointment(appointment);
                if (appointmentId > 0) {
                    JOptionPane.showMessageDialog(dialog, "Appointment scheduled successfully!");
                    dialog.dispose();
                    loadAppointments();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to schedule appointment", "Error", JOptionPane.ERROR_MESSAGE);
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

    private void markCompleted() {
        int selectedRow = appointmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an appointment");
            return;
        }

        int appointmentId = (int) tableModel.getValueAt(selectedRow, 0);
        boolean success = AppointmentDAO.updateAppointmentStatus(appointmentId, Appointment.AppointmentStatus.COMPLETED);

        if (success) {
            JOptionPane.showMessageDialog(this, "Appointment marked as completed");
            loadAppointments();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update appointment", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cancelAppointment() {
        int selectedRow = appointmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an appointment");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to cancel this appointment?",
            "Confirm Cancellation",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            int appointmentId = (int) tableModel.getValueAt(selectedRow, 0);
            boolean success = AppointmentDAO.updateAppointmentStatus(appointmentId, Appointment.AppointmentStatus.CANCELLED);

            if (success) {
                JOptionPane.showMessageDialog(this, "Appointment cancelled");
                loadAppointments();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to cancel appointment", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
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
