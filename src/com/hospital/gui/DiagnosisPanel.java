package com.hospital.gui;

import com.hospital.database.DiagnosisDAO;
import com.hospital.database.PatientDAO;
import com.hospital.database.DoctorDAO;
import com.hospital.database.AppointmentDAO;
import com.hospital.model.Diagnosis;
import com.hospital.model.Patient;
import com.hospital.model.Doctor;
import com.hospital.model.Appointment;
import com.hospital.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

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

        // Show recent diagnoses by default so panel is never blank on open (DB work off EDT).
        loadRecentDiagnosesAsync();
    }

    private void searchDiagnoses() {
        String patientIdStr = searchField.getText().trim();
        if (patientIdStr.isEmpty()) {
            loadRecentDiagnosesAsync();
            return;
        }

        try {
            final int patientId = Integer.parseInt(patientIdStr);
            SwingWorker<List<Diagnosis>, Void> worker = new SwingWorker<List<Diagnosis>, Void>() {
                @Override
                protected List<Diagnosis> doInBackground() {
                    return DiagnosisDAO.getDiagnosesByPatient(patientId);
                }

                @Override
                protected void done() {
                    try {
                        List<Diagnosis> diagnoses = get();
                        populateDiagnosisTable(diagnoses);
                        if (diagnoses.isEmpty()) {
                            JOptionPane.showMessageDialog(DiagnosisPanel.this, "No diagnoses found for this patient");
                        }
                    } catch (InterruptedException | ExecutionException ex) {
                        JOptionPane.showMessageDialog(DiagnosisPanel.this,
                            "Search failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    }
                }
            };
            worker.execute();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid patient ID", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadRecentDiagnosesAsync() {
        SwingWorker<List<Diagnosis>, Void> worker = new SwingWorker<List<Diagnosis>, Void>() {
            @Override
            protected List<Diagnosis> doInBackground() {
                return DiagnosisDAO.getAllDiagnoses();
            }

            @Override
            protected void done() {
                try {
                    populateDiagnosisTable(get());
                } catch (InterruptedException | ExecutionException ex) {
                    JOptionPane.showMessageDialog(DiagnosisPanel.this,
                        "Could not load diagnoses: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void populateDiagnosisTable(List<Diagnosis> diagnoses) {
        tableModel.setRowCount(0);
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
    }

    private void showAddDiagnosisDialog() {
        Window owner = SwingUtilities.getWindowAncestor(this);
        if (owner == null) {
            JOptionPane.showMessageDialog(this, "Cannot open dialog.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JDialog dialog = new JDialog(owner, "Add Diagnosis", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setLayout(new BorderLayout());
        JLabel loading = new JLabel("Loading patients and doctors…", SwingConstants.CENTER);
        loading.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        dialog.add(loading, BorderLayout.CENTER);
        dialog.pack();
        dialog.setLocationRelativeTo(this);

        SwingWorker<Object[], Void> loader = new SwingWorker<Object[], Void>() {
            @Override
            protected Object[] doInBackground() {
                return new Object[] { PatientDAO.getAllPatients(), DoctorDAO.getAllDoctors() };
            }

            @Override
            protected void done() {
                try {
                    Object[] data = get();
                    @SuppressWarnings("unchecked")
                    List<Patient> patients = (List<Patient>) data[0];
                    @SuppressWarnings("unchecked")
                    List<Doctor> doctors = (List<Doctor>) data[1];
                    installAddDiagnosisForm(dialog, patients, doctors);
                } catch (InterruptedException | ExecutionException ex) {
                    JOptionPane.showMessageDialog(dialog,
                        "Could not load form data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                    dialog.dispose();
                }
            }
        };
        loader.execute();
        dialog.setVisible(true);
    }

    private void installAddDiagnosisForm(JDialog dialog, List<Patient> patients, List<Doctor> doctors) {
        dialog.getContentPane().removeAll();
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JComboBox<Patient> patientCombo = new JComboBox<>(patients.toArray(new Patient[0]));
        JComboBox<Appointment> appointmentCombo = new JComboBox<>();
        JComboBox<Doctor> doctorCombo = new JComboBox<>(doctors.toArray(new Doctor[0]));
        doctorCombo.setEnabled(false);
        JTextArea symptomsArea = new JTextArea(4, 20);
        JTextArea diagnosisArea = new JTextArea(4, 20);
        JTextArea notesArea = new JTextArea(3, 20);
        JTextField followUpDateField = new JTextField(15);

        Runnable syncDoctorToAppointment = () -> {
            Appointment a = (Appointment) appointmentCombo.getSelectedItem();
            if (a == null) {
                return;
            }
            for (int i = 0; i < doctorCombo.getItemCount(); i++) {
                Doctor d = doctorCombo.getItemAt(i);
                if (d.getDoctorId() == a.getDoctorId()) {
                    doctorCombo.setSelectedIndex(i);
                    break;
                }
            }
        };

        Runnable loadAppointmentsForPatient = () -> {
            Patient p = (Patient) patientCombo.getSelectedItem();
            appointmentCombo.removeAllItems();
            if (p == null) {
                return;
            }
            appointmentCombo.setEnabled(false);
            SwingWorker<List<Appointment>, Void> w = new SwingWorker<List<Appointment>, Void>() {
                @Override
                protected List<Appointment> doInBackground() {
                    return AppointmentDAO.getAppointmentsByPatient(p.getPatientId());
                }

                @Override
                protected void done() {
                    appointmentCombo.setEnabled(true);
                    try {
                        List<Appointment> apps = get();
                        for (Appointment a : apps) {
                            appointmentCombo.addItem(a);
                        }
                        if (!apps.isEmpty()) {
                            appointmentCombo.setSelectedIndex(0);
                        }
                        syncDoctorToAppointment.run();
                    } catch (InterruptedException | ExecutionException ex) {
                        JOptionPane.showMessageDialog(dialog,
                            "Could not load appointments: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    }
                }
            };
            w.execute();
        };

        patientCombo.addActionListener(e -> loadAppointmentsForPatient.run());
        appointmentCombo.addActionListener(e -> syncDoctorToAppointment.run());

        int row = 0;
        addFormField(panel, gbc, row++, "Patient:", patientCombo);
        JLabel apptHint = new JLabel("Appointment:");
        apptHint.setToolTipText("Only shows appointments for the selected patient. Create one under Appointments if empty.");
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        panel.add(apptHint, gbc);
        gbc.gridx = 1;
        panel.add(appointmentCombo, gbc);
        row++;

        addFormField(panel, gbc, row++, "Doctor:", doctorCombo);
        addFormField(panel, gbc, row++, "Symptoms:", new JScrollPane(symptomsArea));
        addFormField(panel, gbc, row++, "Diagnosis:", new JScrollPane(diagnosisArea));
        addFormField(panel, gbc, row++, "Notes:", new JScrollPane(notesArea));
        addFormField(panel, gbc, row++, "Follow-up Date (YYYY-MM-DD):", followUpDateField);

        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        if (patientCombo.getItemCount() > 0) {
            patientCombo.setSelectedIndex(0);
            loadAppointmentsForPatient.run();
        }

        saveButton.addActionListener(e -> {
            try {
                Patient selectedPatient = (Patient) patientCombo.getSelectedItem();
                Doctor selectedDoctor = (Doctor) doctorCombo.getSelectedItem();

                if (selectedPatient == null || selectedDoctor == null) {
                    JOptionPane.showMessageDialog(dialog, "Please select patient and doctor");
                    return;
                }

                Appointment appt = (Appointment) appointmentCombo.getSelectedItem();
                if (appt == null) {
                    JOptionPane.showMessageDialog(dialog,
                        "No appointment for this patient. Open the Appointments tab and schedule one, then try again.",
                        "No appointment",
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if (appt.getPatientId() != selectedPatient.getPatientId()) {
                    JOptionPane.showMessageDialog(dialog,
                        "The selected patient does not match this appointment.");
                    return;
                }
                if (appt.getDoctorId() != selectedDoctor.getDoctorId()) {
                    JOptionPane.showMessageDialog(dialog,
                        "The doctor must match the appointment. Change the appointment or pick the correct doctor.");
                    return;
                }

                int appointmentId = appt.getAppointmentId();

                if (symptomsArea.getText().trim().isEmpty() || diagnosisArea.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Symptoms and Diagnosis fields are required");
                    return;
                }

                final Diagnosis diagnosis = new Diagnosis(
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

                final Patient patientForRefresh = selectedPatient;
                saveButton.setEnabled(false);
                SwingWorker<Integer, Void> saveWorker = new SwingWorker<Integer, Void>() {
                    /** Set on the worker thread when insert fails (ThreadLocal is not visible on EDT). */
                    private String addErrorFromBackground;

                    @Override
                    protected Integer doInBackground() {
                        int id = DiagnosisDAO.addDiagnosis(diagnosis);
                        if (id <= 0) {
                            addErrorFromBackground = DiagnosisDAO.getLastAddDiagnosisError();
                        }
                        return id;
                    }

                    @Override
                    protected void done() {
                        saveButton.setEnabled(true);
                        try {
                            int diagnosisId = get();
                            if (diagnosisId > 0) {
                                JOptionPane.showMessageDialog(dialog, "Diagnosis added successfully!");
                                searchField.setText(String.valueOf(patientForRefresh.getPatientId()));
                                searchDiagnoses();
                                dialog.dispose();
                            } else {
                                String detail = addErrorFromBackground;
                                String msg = "Could not save this diagnosis.\n\n";
                                if (detail != null && !detail.isEmpty()) {
                                    msg += "Reason:\n" + detail;
                                } else {
                                    msg += "No database reason was captured. Check the terminal/console for SQL errors.";
                                }
                                msg += "\n\n— If this dialog still mentions typing an \"Appointment ID\", you are running an old build: "
                                    + "use Clean and Build in NetBeans, or delete the dist/ and build/ folders and run again.";
                                JOptionPane.showMessageDialog(dialog, msg, "Diagnosis save failed", JOptionPane.ERROR_MESSAGE);
                            }
                        } catch (InterruptedException | ExecutionException ex) {
                            JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                            ex.printStackTrace();
                        }
                    }
                };
                saveWorker.execute();
            } catch (Exception ex) {
                String errorMsg = ex.getClass().getSimpleName() + ": " +
                    (ex.getMessage() != null ? ex.getMessage() : "Unknown error");
                JOptionPane.showMessageDialog(dialog, errorMsg, "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
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
        dialog.setSize(600, 550);
        dialog.revalidate();
        dialog.repaint();
    }

    private void viewDiagnosisDetails() {
        int selectedRow = diagnosisTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a diagnosis");
            return;
        }

        Object idVal = tableModel.getValueAt(selectedRow, 0);
        if (idVal == null) {
            JOptionPane.showMessageDialog(this, "Invalid row selection.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        final int diagnosisId = ((Number) idVal).intValue();
        Window owner = SwingUtilities.getWindowAncestor(this);
        if (owner == null) {
            JOptionPane.showMessageDialog(this, "Cannot open details.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        SwingWorker<Diagnosis, Void> worker = new SwingWorker<Diagnosis, Void>() {
            @Override
            protected Diagnosis doInBackground() {
                return DiagnosisDAO.getDiagnosisById(diagnosisId);
            }

            @Override
            protected void done() {
                try {
                    Diagnosis diagnosis = get();
                    if (diagnosis == null) {
                        JOptionPane.showMessageDialog(DiagnosisPanel.this,
                            "Could not load diagnosis details. If this persists, check the database connection.",
                            "Not found",
                            JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    showDiagnosisDetailsDialog(owner, diagnosis);
                } catch (InterruptedException | ExecutionException ex) {
                    JOptionPane.showMessageDialog(DiagnosisPanel.this,
                        "Could not load details: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void showDiagnosisDetailsDialog(Window owner, Diagnosis diagnosis) {
        JDialog dialog = new JDialog(owner, "Diagnosis Details", Dialog.ModalityType.APPLICATION_MODAL);
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
        JPanel bp = new JPanel();
        bp.add(closeButton);
        panel.add(bp, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);
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
