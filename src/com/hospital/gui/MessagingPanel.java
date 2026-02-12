package com.hospital.gui;

import com.hospital.client.SocketClient;
import com.hospital.database.MessageDAO;
import com.hospital.database.UserDAO;
import com.hospital.model.Message;
import com.hospital.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Messaging Panel for Internal Communication
 */
public class MessagingPanel extends JPanel {

    private User currentUser;
    private SocketClient socketClient;
    private JTable messageTable;
    private DefaultTableModel tableModel;
    private JTabbedPane tabbedPane;

    public MessagingPanel(User user, SocketClient client) {
        this.currentUser = user;
        this.socketClient = client;
        initComponents();
        loadReceivedMessages();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title Panel
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(Color.WHITE);
        JLabel titleLabel = new JLabel("Internal Messaging System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titlePanel.add(titleLabel);

        // Control Panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.setBackground(Color.WHITE);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refreshMessages());

        JButton composeButton = new JButton("Compose Message");
        composeButton.setBackground(Color.GREEN);
        composeButton.setForeground(Color.BLACK);
        composeButton.setOpaque(true);
        composeButton.setBorderPainted(true);
        composeButton.addActionListener(e -> showComposeDialog());

        controlPanel.add(refreshButton);
        controlPanel.add(composeButton);

        // Tabbed Pane
        tabbedPane = new JTabbedPane();

        // Received Messages Tab
        String[] columns = {"ID", "From", "Subject", "Date", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        messageTable = new JTable(tableModel);
        messageTable.setFont(new Font("Arial", Font.PLAIN, 12));
        messageTable.setRowHeight(25);
        messageTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        JScrollPane receivedScrollPane = new JScrollPane(messageTable);
        tabbedPane.addTab("Received Messages", receivedScrollPane);

        // Sent Messages Tab
        JPanel sentPanel = new JPanel(new BorderLayout());
        sentPanel.add(new JLabel("Sent messages view - similar implementation", SwingConstants.CENTER));
        tabbedPane.addTab("Sent Messages", sentPanel);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.WHITE);

        JButton viewButton = new JButton("View Message");
        viewButton.addActionListener(e -> viewMessage());

        JButton markReadButton = new JButton("Mark as Read");
        markReadButton.addActionListener(e -> markAsRead());

        buttonPanel.add(viewButton);
        buttonPanel.add(markReadButton);

        // Add components
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.add(titlePanel, BorderLayout.NORTH);
        topPanel.add(controlPanel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadReceivedMessages() {
        tableModel.setRowCount(0);
        List<Message> messages = MessageDAO.getReceivedMessages(currentUser.getUserId());

        for (Message message : messages) {
            Object[] row = {
                message.getMessageId(),
                message.getSenderName(),
                message.getSubject(),
                message.getSentAt(),
                message.isReadStatus() ? "Read" : "Unread"
            };
            tableModel.addRow(row);
        }
    }

    private void refreshMessages() {
        loadReceivedMessages();
    }

    private void showComposeDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Compose Message", true);
        dialog.setSize(700, 550);  // Increased from 500x400 to 700x550
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        List<User> users = UserDAO.getAllUsers();
        JComboBox<User> recipientCombo = new JComboBox<>(users.toArray(new User[0]));
        JTextField subjectField = new JTextField(40);  // Wider: 40 instead of 30
        JTextArea messageArea = new JTextArea(15, 50);  // Larger: 15x50 instead of 10x30
        messageArea.setFont(new Font("Arial", Font.PLAIN, 14));  // Larger font
        messageArea.setLineWrap(true);  // Enable line wrapping
        messageArea.setWrapStyleWord(true);  // Wrap at word boundaries

        int row = 0;
        addFormField(panel, gbc, row++, "To:", recipientCombo);
        addFormField(panel, gbc, row++, "Subject:", subjectField);
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        JLabel msgLabel = new JLabel("Message:");
        msgLabel.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(msgLabel, gbc);
        gbc.gridy = row++;
        panel.add(new JScrollPane(messageArea), gbc);

        JPanel buttonPanel = new JPanel();
        JButton sendButton = new JButton("Send");
        JButton cancelButton = new JButton("Cancel");

        sendButton.addActionListener(e -> {
            try {
                User recipient = (User) recipientCombo.getSelectedItem();
                if (recipient == null) {
                    JOptionPane.showMessageDialog(dialog, "Please select a recipient");
                    return;
                }

                Message message = new Message(
                    currentUser.getUserId(),
                    recipient.getUserId(),
                    subjectField.getText().trim(),
                    messageArea.getText().trim()
                );

                int messageId = MessageDAO.sendMessage(message);
                if (messageId > 0) {
                    // Also send via socket if connected
                    if (socketClient != null && socketClient.isConnected()) {
                        socketClient.sendMessageToUser(recipient.getUserId(),
                            subjectField.getText() + ": " + messageArea.getText());
                    }

                    JOptionPane.showMessageDialog(dialog, "Message sent successfully!");
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to send message", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(sendButton);
        buttonPanel.add(cancelButton);

        gbc.gridy = row;
        panel.add(buttonPanel, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void viewMessage() {
        int selectedRow = messageTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a message");
            return;
        }

        int messageId = (int) tableModel.getValueAt(selectedRow, 0);
        List<Message> messages = MessageDAO.getReceivedMessages(currentUser.getUserId());

        for (Message message : messages) {
            if (message.getMessageId() == messageId) {
                JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "View Message", true);
                dialog.setSize(800, 600);  // Increased from 500x350 to 800x600
                dialog.setLocationRelativeTo(this);

                JPanel panel = new JPanel(new BorderLayout(10, 10));
                panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

                JTextArea messageArea = new JTextArea(20, 60);  // Set explicit rows and columns
                messageArea.setEditable(false);
                messageArea.setFont(new Font("Arial", Font.PLAIN, 14));  // Larger font: 14 instead of 12
                messageArea.setLineWrap(true);  // Enable line wrapping
                messageArea.setWrapStyleWord(true);  // Wrap at word boundaries
                messageArea.setText(String.format(
                    "From: %s\nDate: %s\nSubject: %s\n\n%s",
                    message.getSenderName(), message.getSentAt(),
                    message.getSubject(), message.getMessageText()
                ));

                panel.add(new JScrollPane(messageArea), BorderLayout.CENTER);

                JButton closeButton = new JButton("Close");
                closeButton.addActionListener(e -> dialog.dispose());
                JPanel buttonPanel = new JPanel();
                buttonPanel.add(closeButton);
                panel.add(buttonPanel, BorderLayout.SOUTH);

                dialog.add(panel);
                dialog.setVisible(true);

                // Mark as read
                if (!message.isReadStatus()) {
                    MessageDAO.markAsRead(messageId);
                    loadReceivedMessages();
                }
                break;
            }
        }
    }

    private void markAsRead() {
        int selectedRow = messageTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a message");
            return;
        }

        int messageId = (int) tableModel.getValueAt(selectedRow, 0);
        boolean success = MessageDAO.markAsRead(messageId);

        if (success) {
            JOptionPane.showMessageDialog(this, "Message marked as read");
            loadReceivedMessages();
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
