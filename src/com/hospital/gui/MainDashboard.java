package com.hospital.gui;

import com.hospital.client.SocketClient;
import com.hospital.database.MessageDAO;
import com.hospital.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

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
                cleanup();
                System.exit(0);
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

        // Create main welcome panel with gradient background
        JPanel welcomePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int w = getWidth();
                int h = getHeight();
                Color color1 = new Color(135, 206, 250); // Light sky blue
                Color color2 = new Color(70, 130, 180);  // Steel blue
                GradientPaint gp = new GradientPaint(0, 0, color1, 0, h, color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        welcomePanel.setLayout(new BorderLayout(20, 20));
        welcomePanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        // Top welcome message
        JPanel topPanel = new JPanel();
        topPanel.setOpaque(false);
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));

        JLabel welcomeTitle = new JLabel("Welcome, " + currentUser.getFullName() + "!");
        welcomeTitle.setFont(new Font("Arial", Font.BOLD, 36));
        welcomeTitle.setForeground(Color.WHITE);
        welcomeTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Hospital Management System Dashboard");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 20));
        subtitle.setForeground(new Color(240, 248, 255));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        topPanel.add(Box.createVerticalStrut(20));
        topPanel.add(welcomeTitle);
        topPanel.add(Box.createVerticalStrut(10));
        topPanel.add(subtitle);
        topPanel.add(Box.createVerticalStrut(40));

        // Center panel with dashboard cards
        JPanel centerPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        centerPanel.setOpaque(false);

        // Get statistics
        int totalPatients = com.hospital.database.PatientDAO.getAllPatients().size();
        int totalAppointments = com.hospital.database.AppointmentDAO.getUpcomingAppointments().size();
        int unreadMessages = com.hospital.database.MessageDAO.getUnreadCount(currentUser.getUserId());

        // Create dashboard cards
        centerPanel.add(createDashboardCard("Total Patients", String.valueOf(totalPatients), new Color(46, 204, 113)));
        centerPanel.add(createDashboardCard("Upcoming Appointments", String.valueOf(totalAppointments), new Color(52, 152, 219)));
        centerPanel.add(createDashboardCard("Unread Messages", String.valueOf(unreadMessages), new Color(241, 196, 15)));
        centerPanel.add(createDashboardCard("Your Role", currentUser.getRole().toString(), new Color(155, 89, 182)));

        // Bottom message
        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        JLabel instructionLabel = new JLabel("<html><center>Select an option from the left menu to get started<br>" +
            "Quick access to all hospital management features</center></html>");
        instructionLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        instructionLabel.setForeground(Color.WHITE);
        instructionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        bottomPanel.add(instructionLabel);

        welcomePanel.add(topPanel, BorderLayout.NORTH);
        welcomePanel.add(centerPanel, BorderLayout.CENTER);
        welcomePanel.add(bottomPanel, BorderLayout.SOUTH);

        contentPanel.add(welcomePanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JPanel createDashboardCard(String title, String value, Color bgColor) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(bgColor);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.WHITE, 2),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 48));
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(Box.createVerticalGlue());
        card.add(valueLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(titleLabel);
        card.add(Box.createVerticalGlue());

        return card;
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

    /**
     * Cleanup resources before closing or logging out
     * Note: Database connection uses singleton pattern and should not be closed here
     */
    private void cleanup() {
        try {
            if (socketClient != null) {
                socketClient.disconnect();
            }
            // Do NOT close DatabaseConnection - it uses a singleton pattern
            // Closing it will break subsequent logins/operations
        } catch (Exception e) {
            System.err.println("Error during cleanup: " + e.getMessage());
        }
    }

    private void logout() {
        int option = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to logout?",
            "Logout Confirmation",
            JOptionPane.YES_NO_OPTION);

        if (option == JOptionPane.YES_OPTION) {
            cleanup();
            this.dispose();
            SwingUtilities.invokeLater(() -> {
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setVisible(true);
            });
        }
    }
}
