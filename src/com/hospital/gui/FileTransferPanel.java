package com.hospital.gui;

import com.hospital.client.SocketClient;
import com.hospital.database.UserDAO;
import com.hospital.model.User;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.List;

/**
 * File Transfer Panel
 */
public class FileTransferPanel extends JPanel {

    private User currentUser;
    private SocketClient socketClient;
    private JTextArea logArea;
    private JTextField filePathField;
    private JComboBox<User> recipientCombo;

    public FileTransferPanel(User user, SocketClient client) {
        this.currentUser = user;
        this.socketClient = client;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title Panel
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(Color.WHITE);
        JLabel titleLabel = new JLabel("File Transfer System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titlePanel.add(titleLabel);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createTitledBorder("Send File"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Recipient
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel recipientLabel = new JLabel("Recipient:");
        recipientLabel.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(recipientLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        List<User> users = UserDAO.getAllUsers();
        recipientCombo = new JComboBox<>(users.toArray(new User[0]));
        recipientCombo.setPreferredSize(new Dimension(300, 30));
        formPanel.add(recipientCombo, gbc);

        // File selection
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        JLabel fileLabel = new JLabel("File:");
        fileLabel.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(fileLabel, gbc);

        gbc.gridx = 1;
        filePathField = new JTextField(25);
        filePathField.setEditable(false);
        formPanel.add(filePathField, gbc);

        gbc.gridx = 2;
        JButton browseButton = new JButton("Browse");
        browseButton.addActionListener(e -> browseFile());
        formPanel.add(browseButton, gbc);

        // Send button
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton sendButton = new JButton("Send File");
        sendButton.setBackground(new Color(34, 139, 34));
        sendButton.setForeground(Color.WHITE);
        sendButton.setFont(new Font("Arial", Font.BOLD, 14));
        sendButton.setPreferredSize(new Dimension(150, 35));
        sendButton.addActionListener(e -> sendFile());
        formPanel.add(sendButton, gbc);

        // Log Area
        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setBackground(Color.WHITE);
        logPanel.setBorder(BorderFactory.createTitledBorder("Transfer Log"));

        logArea = new JTextArea(15, 50);
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane logScrollPane = new JScrollPane(logArea);
        logPanel.add(logScrollPane, BorderLayout.CENTER);

        JButton clearLogButton = new JButton("Clear Log");
        clearLogButton.addActionListener(e -> logArea.setText(""));
        JPanel logButtonPanel = new JPanel();
        logButtonPanel.add(clearLogButton);
        logPanel.add(logButtonPanel, BorderLayout.SOUTH);

        // Connection Status
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBackground(Color.WHITE);
        String status = (socketClient != null && socketClient.isConnected()) ? "Connected" : "Disconnected";
        Color statusColor = (socketClient != null && socketClient.isConnected()) ? Color.GREEN : Color.RED;
        JLabel statusLabel = new JLabel("Server Status: " + status);
        statusLabel.setForeground(statusColor);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 12));
        statusPanel.add(statusLabel);

        // Add components
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.add(titlePanel, BorderLayout.NORTH);
        topPanel.add(statusPanel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(logPanel, BorderLayout.SOUTH);

        addLog("File Transfer System initialized.");
        if (socketClient == null || !socketClient.isConnected()) {
            addLog("WARNING: Not connected to server. File transfer will not work.");
        }
    }

    private void browseFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        // Accept all files by default to ensure files are visible
        fileChooser.setAcceptAllFileFilterUsed(true);

        // Set current directory to user's home for easier navigation
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));

        // Add common file type filters for convenience
        fileChooser.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "PDF Documents (*.pdf)", "pdf"));
        fileChooser.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "Images (*.jpg, *.png, *.gif)", "jpg", "jpeg", "png", "gif"));
        fileChooser.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "Text Files (*.txt)", "txt"));
        fileChooser.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "Documents (*.doc, *.docx)", "doc", "docx"));

        // Set "All Files" as the default filter
        fileChooser.setFileFilter(fileChooser.getAcceptAllFileFilter());

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            filePathField.setText(selectedFile.getAbsolutePath());
            addLog("Selected file: " + selectedFile.getName());
        }
    }

    private void sendFile() {
        String filePath = filePathField.getText().trim();
        if (filePath.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a file to send");
            return;
        }

        User recipient = (User) recipientCombo.getSelectedItem();
        if (recipient == null) {
            JOptionPane.showMessageDialog(this, "Please select a recipient");
            return;
        }

        if (socketClient == null || !socketClient.isConnected()) {
            JOptionPane.showMessageDialog(this,
                "Not connected to server. Cannot send file.",
                "Connection Error",
                JOptionPane.ERROR_MESSAGE);
            addLog("ERROR: Cannot send file - not connected to server");
            return;
        }

        File file = new File(filePath);
        if (!file.exists()) {
            JOptionPane.showMessageDialog(this, "File does not exist");
            return;
        }

        // Check file size (limit to 5MB for demo)
        if (file.length() > 5 * 1024 * 1024) {
            JOptionPane.showMessageDialog(this,
                "File is too large. Maximum size is 5MB.",
                "File Size Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            addLog("Reading file: " + file.getName() + " (" + file.length() + " bytes)");

            // Read file and encode to Base64
            byte[] fileBytes = Files.readAllBytes(file.toPath());
            String encodedFile = Base64.getEncoder().encodeToString(fileBytes);

            addLog("Sending file to " + recipient.getFullName() + "...");

            // Send via socket
            socketClient.sendFileToUser(recipient.getUserId(), file.getName(), encodedFile);

            addLog("File sent successfully!");
            JOptionPane.showMessageDialog(this,
                "File sent to " + recipient.getFullName(),
                "Success",
                JOptionPane.INFORMATION_MESSAGE);

            filePathField.setText("");

        } catch (IOException e) {
            addLog("ERROR: Failed to read file - " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                "Failed to read file: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addLog(String message) {
        String timestamp = new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date());
        logArea.append("[" + timestamp + "] " + message + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }
}
