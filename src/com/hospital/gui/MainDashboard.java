package com.hospital.gui;

import com.hospital.client.SocketClient;
import com.hospital.database.MessageDAO;
import com.hospital.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Main Dashboard for Hospital Management System
 */
public class MainDashboard extends JFrame {

    private User currentUser;
    private SocketClient socketClient;
    private JLabel welcomeLabel;
    private JLabel unreadMessagesLabel;
    private JPanel contentPanel;

    public MainDashboard(User user) {
        this.currentUser = user;
        initComponents();
        connectToServer();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        setTitle("Hospital Management System - Dashboard");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Header Panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Sidebar Panel
        JPanel sidebarPanel = createSidebarPanel();
        add(sidebarPanel, BorderLayout.WEST);

        // Content Panel
        contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        showWelcomePanel();
        add(contentPanel, BorderLayout.CENTER);

        // Add window listener for cleanup
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (socketClient != null) {
                    socketClient.disconnect();
                }
            }
        });
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBackground(new Color(70, 130, 180));
        headerPanel.setPreferredSize(new Dimension(1200, 70));

        JLabel titleLabel = new JLabel("Hospital Management System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 10));

        welcomeLabel = new JLabel("Welcome, " + currentUser.getFullName() + " (" + currentUser.getRole() + ")");
        welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 20));

        unreadMessagesLabel = new JLabel();
        updateUnreadCount();
        unreadMessagesLabel.setFont(new Font("Arial", Font.BOLD, 12));
        unreadMessagesLabel.setForeground(Color.YELLOW);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setBackground(new Color(70, 130, 180));
        rightPanel.add(unreadMessagesLabel);
        rightPanel.add(welcomeLabel);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(rightPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createSidebarPanel() {
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(new Color(47, 79, 79));
        sidebarPanel.setPreferredSize(new Dimension(250, 700));

        sidebarPanel.add(Box.createVerticalStrut(20));

        // Menu buttons
        addMenuButton(sidebarPanel, "Dashboard", e -> showWelcomePanel());
        addMenuButton(sidebarPanel, "Patient Management", e -> showPatientManagement());
        addMenuButton(sidebarPanel, "Appointments", e -> showAppointments());
        addMenuButton(sidebarPanel, "Diagnoses", e -> showDiagnoses());
        addMenuButton(sidebarPanel, "Messages", e -> showMessages());
        addMenuButton(sidebarPanel, "File Transfer", e -> showFileTransfer());

        sidebarPanel.add(Box.createVerticalGlue());

        // Logout button at bottom
        JButton logoutButton = new JButton("Logout");
        logoutButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutButton.setMaximumSize(new Dimension(200, 40));
        logoutButton.setBackground(Color.RED);
        logoutButton.setForeground(Color.BLACK);
        logoutButton.setFocusPainted(false);
        logoutButton.setOpaque(true);
        logoutButton.setBorderPainted(true);
        logoutButton.setFont(new Font("Arial", Font.BOLD, 14));
        logoutButton.addActionListener(e -> logout());

        sidebarPanel.add(logoutButton);
        sidebarPanel.add(Box.createVerticalStrut(20));

        return sidebarPanel;
    }

    private void addMenuButton(JPanel panel, String text, ActionListener listener) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(220, 45));
        button.setBackground(Color.GREEN);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setBorderPainted(true);
        button.setFont(new Font("Arial", Font.BOLD, 13));
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        button.addActionListener(listener);

        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(Color.GREEN.brighter());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(Color.GREEN);
            }
        });

        panel.add(button);
        panel.add(Box.createVerticalStrut(10));
    }

    private void showWelcomePanel() {
        contentPanel.removeAll();

        JPanel welcomePanel = new JPanel();
        welcomePanel.setLayout(new GridBagLayout());
        welcomePanel.setBackground(Color.WHITE);

        JLabel welcomeText = new JLabel("<html><center><h1>Welcome to Hospital Management System</h1>" +
            "<p>Select an option from the menu to get started</p></center></html>");
        welcomeText.setFont(new Font("Arial", Font.PLAIN, 16));

        welcomePanel.add(welcomeText);

        contentPanel.add(welcomePanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showPatientManagement() {
        contentPanel.removeAll();
        PatientManagementPanel panel = new PatientManagementPanel(currentUser);
        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showAppointments() {
        contentPanel.removeAll();
        AppointmentPanel panel = new AppointmentPanel(currentUser);
        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showDiagnoses() {
        contentPanel.removeAll();
        DiagnosisPanel panel = new DiagnosisPanel(currentUser);
        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showMessages() {
        contentPanel.removeAll();
        MessagingPanel panel = new MessagingPanel(currentUser, socketClient);
        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
        updateUnreadCount();
    }

    private void showFileTransfer() {
        contentPanel.removeAll();
        FileTransferPanel panel = new FileTransferPanel(currentUser, socketClient);
        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void updateUnreadCount() {
        int unreadCount = MessageDAO.getUnreadCount(currentUser.getUserId());
        if (unreadCount > 0) {
            unreadMessagesLabel.setText("You have " + unreadCount + " unread message(s) | ");
        } else {
            unreadMessagesLabel.setText("");
        }
    }

    private void connectToServer() {
        socketClient = new SocketClient();
        boolean connected = socketClient.connect(currentUser.getUserId());

        if (!connected) {
            JOptionPane.showMessageDialog(this,
                "Warning: Could not connect to messaging server.\n" +
                "Real-time messaging features will not be available.",
                "Server Connection Warning",
                JOptionPane.WARNING_MESSAGE);
        }

        // Set up message listener
        socketClient.setMessageListener(new SocketClient.MessageListener() {
            @Override
            public void onMessageReceived(int senderId, String message) {
                SwingUtilities.invokeLater(() -> {
                    updateUnreadCount();
                    JOptionPane.showMessageDialog(MainDashboard.this,
                        "New message received!",
                        "New Message",
                        JOptionPane.INFORMATION_MESSAGE);
                });
            }

            @Override
            public void onFileReceived(int senderId, String fileName, String fileData) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(MainDashboard.this,
                        "File received: " + fileName,
                        "File Transfer",
                        JOptionPane.INFORMATION_MESSAGE);
                });
            }

            @Override
            public void onRegistered() {
                System.out.println("Successfully registered with server");
            }

            @Override
            public void onMessageSent() {
                System.out.println("Message sent successfully");
            }
        });
    }

    private void logout() {
        int option = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to logout?",
            "Logout Confirmation",
            JOptionPane.YES_NO_OPTION);

        if (option == JOptionPane.YES_OPTION) {
            if (socketClient != null) {
                socketClient.disconnect();
            }
            this.dispose();
            SwingUtilities.invokeLater(() -> {
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setVisible(true);
            });
        }
    }
}
