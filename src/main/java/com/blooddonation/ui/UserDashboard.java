package com.blooddonation.ui;

import com.blooddonation.util.DatabaseConnection;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;
import java.util.Calendar;

public class UserDashboard extends JFrame {
    private JPanel mainPanel;
    private JPanel menuPanel;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private Timer animationTimer;
    private int menuWidth = 0;
    private final int FINAL_MENU_WIDTH = 200;
    private int userId;
    private String currentPage = "Dashboard";
    private DefaultTableModel appointmentsTableModel;
    private JTable appointmentsTable;
    private JTextField nameField, phoneField, emailField, addressField;
    private JComboBox<String> bloodTypeCombo, genderCombo;
    private JButton editButton, saveButton;

    public UserDashboard(int userId) {
        this.userId = userId;
        setTitle("Blood Donation System - User Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);

        // Main panel setup
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(248, 249, 250));

        // Menu panel with animation
        setupMenuPanel();
        
        // Content panel with card layout
        setupContentPanel();

        // Add panels to main panel
        mainPanel.add(menuPanel, BorderLayout.WEST);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        add(mainPanel);
        setVisible(true);

        // Start menu animation
        startMenuAnimation();
    }

    private void setupMenuPanel() {
        menuPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, new Color(220, 53, 69),
                        0, getHeight(), new Color(173, 41, 54));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setPreferredSize(new Dimension(0, getHeight()));

        // Menu items
        String[] menuItems = {
            "Dashboard", "Schedule Donation", "My Appointments", "Donation History",
            "Blood Availability", "My Profile", "Help"
        };

        // Add some spacing at the top
        menuPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        for (String item : menuItems) {
            JPanel menuItemPanel = createMenuItemPanel(item);
            menuPanel.add(menuItemPanel);
            menuPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        // Add spacing before logout button
        menuPanel.add(Box.createVerticalGlue());

        // Logout button
        JButton logoutButton = new JButton("Logout");
        logoutButton.setMaximumSize(new Dimension(150, 40));
        logoutButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutButton.setBackground(new Color(248, 249, 250));
        logoutButton.setForeground(new Color(220, 53, 69));
        logoutButton.setFocusPainted(false);
        logoutButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        logoutButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );
            
            if (confirm == JOptionPane.YES_OPTION) {
                this.dispose();
                new LoginScreen();
            }
        });

        menuPanel.add(logoutButton);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 20)));
    }

    private JPanel createMenuItemPanel(String text) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setMaximumSize(new Dimension(200, 40));
        panel.setOpaque(false);

        // Create indicator panel
        JPanel indicatorPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (currentPage.equals(text)) {
                    g.setColor(Color.WHITE);
                    g.fillRect(0, 0, 4, getHeight());
                }
            }
        };
        indicatorPanel.setPreferredSize(new Dimension(4, 40));
        indicatorPanel.setOpaque(false);

        // Create label panel
        JPanel labelPanel = new JPanel(new BorderLayout());
        labelPanel.setOpaque(false);
        
        JLabel label = new JLabel(text);
        label.setForeground(currentPage.equals(text) ? Color.WHITE : new Color(255, 255, 255, 200));
        label.setFont(new Font("Arial", currentPage.equals(text) ? Font.BOLD : Font.PLAIN, 14));
        label.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        labelPanel.add(label, BorderLayout.CENTER);

        panel.add(indicatorPanel, BorderLayout.WEST);
        panel.add(labelPanel, BorderLayout.CENTER);

        panel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (!currentPage.equals(text)) {
                    label.setForeground(Color.WHITE);
                }
                panel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (!currentPage.equals(text)) {
                    label.setForeground(new Color(255, 255, 255, 200));
                }
            }

            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                currentPage = text;
                cardLayout.show(contentPanel, text);
                updateMenuSelection();
            }
        });

        return panel;
    }

    private void updateMenuSelection() {
        // Refresh all menu items
        for (Component c : menuPanel.getComponents()) {
            if (c instanceof JPanel) {
                c.repaint();
                // Update label styling in the panel
                for (Component inner : ((JPanel) c).getComponents()) {
                    if (inner instanceof JPanel) {
                        for (Component innermost : ((JPanel) inner).getComponents()) {
                            if (innermost instanceof JLabel) {
                                JLabel label = (JLabel) innermost;
                                if (label.getText().equals(currentPage)) {
                                    label.setForeground(Color.WHITE);
                                    label.setFont(new Font("Arial", Font.BOLD, 14));
                                } else {
                                    label.setForeground(new Color(255, 255, 255, 200));
                                    label.setFont(new Font("Arial", Font.PLAIN, 14));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void setupContentPanel() {
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(Color.WHITE);

        // Add different panels for each menu item
        contentPanel.add(createDashboardPanel(), "Dashboard");
        contentPanel.add(createScheduleDonationPanel(), "Schedule Donation");
        contentPanel.add(createMyAppointmentsPanel(), "My Appointments");
        contentPanel.add(createDonationHistoryPanel(), "Donation History");
        contentPanel.add(createBloodAvailabilityPanel(), "Blood Availability");
        contentPanel.add(createProfilePanel(), "My Profile");
        contentPanel.add(createHelpPanel(), "Help");
    }

    private void startMenuAnimation() {
        animationTimer = new Timer(10, e -> {
            if (menuWidth < FINAL_MENU_WIDTH) {
                menuWidth += 10;
                menuPanel.setPreferredSize(new Dimension(menuWidth, getHeight()));
                menuPanel.revalidate();
            } else {
                ((Timer) e.getSource()).stop();
            }
        });
        animationTimer.start();
    }

    private JComponent createDashboardPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(new Color(245, 246, 247));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Welcome section at the top
        JPanel welcomePanel = new JPanel(new BorderLayout(10, 10));
        welcomePanel.setOpaque(false);

        JLabel welcomeLabel = new JLabel("Welcome to Blood Donation System");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        welcomeLabel.setForeground(new Color(33, 37, 41));

        JLabel subtitleLabel = new JLabel("Track your donations and manage appointments");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(108, 117, 125));

        welcomePanel.add(welcomeLabel, BorderLayout.NORTH);
        welcomePanel.add(subtitleLabel, BorderLayout.CENTER);

        // Cards Panel using GridBagLayout for better control
        JPanel cardsPanel = new JPanel(new GridBagLayout());
        cardsPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;

        // Statistics Cards Row
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weighty = 0.0;

        // Total Donations Card
        gbc.gridx = 0;
        cardsPanel.add(createStatCard("Total Donations", getTotalDonations(), "🩸"), gbc);

        // Last Donation Card
        gbc.gridx = 1;
        cardsPanel.add(createStatCard("Last Donation", getLastDonationDate(), "📅"), gbc);

        // Next Eligible Date Card
        gbc.gridx = 2;
        cardsPanel.add(createStatCard("Next Eligible Date", getNextEligibleDate(), "✅"), gbc);

        // Quick Actions Row
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        gbc.gridx = 0;
        cardsPanel.add(createQuickActionsPanel(), gbc);

        // Recent Activity Panel
        gbc.gridy = 2;
        cardsPanel.add(createRecentActivityPanel(), gbc);

        // Add all sections to main panel
        mainPanel.add(welcomePanel, BorderLayout.NORTH);
        mainPanel.add(cardsPanel, BorderLayout.CENTER);

        // Wrap in scroll pane
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        return scrollPane;
    }

    private JPanel createStatCard(String title, String value, String emoji) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(233, 236, 239), 1),
            BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));

        // Emoji and Title in one row
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        headerPanel.setOpaque(false);

        JLabel emojiLabel = new JLabel(emoji);
        emojiLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titleLabel.setForeground(new Color(108, 117, 125));

        headerPanel.add(emojiLabel);
        headerPanel.add(titleLabel);

        // Value
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(new Color(33, 37, 41));
        valueLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(headerPanel);
        card.add(valueLabel);

        return card;
    }

    private JPanel createQuickActionsPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(233, 236, 239), 1),
            BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));

        // Title
        JLabel titleLabel = new JLabel("Quick Actions");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(33, 37, 41));

        // Buttons Panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        buttonsPanel.setOpaque(false);

        // Schedule Appointment Button
        JButton scheduleButton = createActionButton("Schedule Appointment", "📅", new Color(220, 53, 69));
        scheduleButton.addActionListener(e -> {
            currentPage = "Schedule Donation";
            cardLayout.show(contentPanel, "Schedule Donation");
            updateMenuSelection();
        });

        // View History Button
        JButton historyButton = createActionButton("View Donation History", "📋", new Color(0, 123, 255));
        historyButton.addActionListener(e -> {
            currentPage = "Donation History";
            cardLayout.show(contentPanel, "Donation History");
            updateMenuSelection();
        });

        // Update Profile Button
        JButton profileButton = createActionButton("Update Profile", "👤", new Color(40, 167, 69));
        profileButton.addActionListener(e -> {
            currentPage = "My Profile";
            cardLayout.show(contentPanel, "My Profile");
            updateMenuSelection();
        });

        buttonsPanel.add(scheduleButton);
        buttonsPanel.add(historyButton);
        buttonsPanel.add(profileButton);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(buttonsPanel, BorderLayout.CENTER);

        return panel;
    }

    private JButton createActionButton(String text, String emoji, Color color) {
        JButton button = new JButton("<html><center>" + emoji + "<br>" + text + "</center></html>");
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.darker());
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });

        return button;
    }

    private JPanel createRecentActivityPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(233, 236, 239), 1),
            BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));

        // Title
        JLabel titleLabel = new JLabel("Recent Activity");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(33, 37, 41));

        // Activity List
        JPanel activityList = new JPanel();
        activityList.setLayout(new BoxLayout(activityList, BoxLayout.Y_AXIS));
        activityList.setOpaque(false);

        // Add recent activities
        loadRecentActivities(activityList);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(activityList, BorderLayout.CENTER);

        return panel;
    }

    private void loadRecentActivities(JPanel activityList) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT a.appointment_date, a.appointment_time, a.status, d.first_name " +
                     "FROM appointments a " +
                     "JOIN donors d ON a.donor_id = d.donor_id " +
                     "WHERE a.donor_id = ? " +
                     "ORDER BY a.created_at DESC LIMIT 5")) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            boolean hasActivities = false;
            while (rs.next()) {
                hasActivities = true;
                JPanel activityItem = createActivityItem(
                    rs.getDate("appointment_date"),
                    rs.getString("status"),
                    "Appointment " + rs.getString("status").toLowerCase()
                );
                activityList.add(activityItem);
                activityList.add(Box.createRigidArea(new Dimension(0, 10)));
            }

            if (!hasActivities) {
                JLabel noActivityLabel = new JLabel("No recent activities");
                noActivityLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                noActivityLabel.setForeground(new Color(108, 117, 125));
                activityList.add(noActivityLabel);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JLabel errorLabel = new JLabel("Error loading activities");
            errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            errorLabel.setForeground(new Color(220, 53, 69));
            activityList.add(errorLabel);
        }
    }

    private JPanel createActivityItem(Date date, String status, String description) {
        JPanel item = new JPanel(new BorderLayout(10, 0));
        item.setOpaque(false);
        item.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        // Status indicator
        JPanel statusIndicator = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                Color statusColor;
                switch(status) {
                    case "COMPLETED": statusColor = new Color(40, 167, 69); break;
                    case "PENDING": statusColor = new Color(255, 193, 7); break;
                    case "APPROVED": statusColor = new Color(0, 123, 255); break;
                    case "REJECTED": statusColor = new Color(220, 53, 69); break;
                    default: statusColor = new Color(108, 117, 125);
                }
                
                g2d.setColor(statusColor);
                g2d.fillOval(0, 0, 10, 10);
            }
        };
        statusIndicator.setPreferredSize(new Dimension(10, 10));

        // Date
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
        JLabel dateLabel = new JLabel(dateFormat.format(date));
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dateLabel.setForeground(new Color(108, 117, 125));

        // Description
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descLabel.setForeground(new Color(33, 37, 41));

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        textPanel.setOpaque(false);
        textPanel.add(dateLabel);
        textPanel.add(descLabel);

        item.add(statusIndicator, BorderLayout.WEST);
        item.add(textPanel, BorderLayout.CENTER);

        return item;
    }

    private String getTotalDonations() {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT COUNT(*) as total FROM appointments " +
                     "WHERE donor_id = ? AND status = 'COMPLETED'")) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return String.valueOf(rs.getInt("total"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "0";
    }

    private String getLastDonationDate() {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT last_donation_date FROM donors WHERE donor_id = ?")) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Date lastDonation = rs.getDate("last_donation_date");
                if (lastDonation != null) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
                    return dateFormat.format(lastDonation);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "No donations yet";
    }

    private String getNextEligibleDate() {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT last_donation_date FROM donors WHERE donor_id = ?")) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Date lastDonation = rs.getDate("last_donation_date");
                if (lastDonation != null) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(lastDonation);
                    cal.add(Calendar.MONTH, 3); // 3 months waiting period
                    
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
                    return dateFormat.format(cal.getTime());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Eligible now";
    }

    private JComponent createScheduleDonationPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Title
        JLabel titleLabel = new JLabel("Schedule a Blood Donation");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(220, 53, 69));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 53, 69), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        GridBagConstraints formGbc = new GridBagConstraints();
        formGbc.insets = new Insets(5, 5, 5, 5);
        formGbc.anchor = GridBagConstraints.WEST;

        // Date picker
        JLabel dateLabel = new JLabel("Select Date:");
        formGbc.gridx = 0;
        formGbc.gridy = 0;
        formPanel.add(dateLabel, formGbc);

        JDateChooser dateChooser = new JDateChooser();
        dateChooser.setPreferredSize(new Dimension(200, 30));
        formGbc.gridx = 1;
        formPanel.add(dateChooser, formGbc);

        // Time slots
        JLabel timeLabel = new JLabel("Select Time:");
        formGbc.gridx = 0;
        formGbc.gridy = 1;
        formPanel.add(timeLabel, formGbc);

        String[] timeSlots = {
            "09:00 AM", "10:00 AM", "11:00 AM",
            "02:00 PM", "03:00 PM", "04:00 PM"
        };
        JComboBox<String> timeCombo = new JComboBox<>(timeSlots);
        timeCombo.setPreferredSize(new Dimension(200, 30));
        formGbc.gridx = 1;
        formPanel.add(timeCombo, formGbc);

        // Blood Center
        JLabel centerLabel = new JLabel("Blood Center:");
        formGbc.gridx = 0;
        formGbc.gridy = 2;
        formPanel.add(centerLabel, formGbc);

        String[] centers = {
            "City Blood Bank - Main Center",
            "Community Health Center",
            "Regional Blood Bank",
            "Medical Center Blood Bank"
        };
        JComboBox<String> centerCombo = new JComboBox<>(centers);
        centerCombo.setPreferredSize(new Dimension(200, 30));
        formGbc.gridx = 1;
        formPanel.add(centerCombo, formGbc);

        // Notes
        JLabel notesLabel = new JLabel("Additional Notes:");
        formGbc.gridx = 0;
        formGbc.gridy = 3;
        formPanel.add(notesLabel, formGbc);

        JTextArea notesArea = new JTextArea(3, 20);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        JScrollPane notesScroll = new JScrollPane(notesArea);
        formGbc.gridx = 1;
        formPanel.add(notesScroll, formGbc);

        // Add form panel to main panel
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(formPanel, gbc);

        // Schedule button
        JButton scheduleButton = new JButton("Schedule Donation");
        scheduleButton.setBackground(new Color(220, 53, 69));
        scheduleButton.setForeground(Color.WHITE);
        scheduleButton.setFocusPainted(false);
        scheduleButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(scheduleButton, gbc);

        // Add button action
        scheduleButton.addActionListener(e -> {
            if (validateDonationForm(dateChooser)) {
                scheduleDonation(
                    dateChooser.getDate(),
                    (String) timeCombo.getSelectedItem(),
                    (String) centerCombo.getSelectedItem(),
                    notesArea.getText()
                );
            }
        });

        return new JScrollPane(panel);
    }

    private boolean validateDonationForm(JDateChooser dateChooser) {
        if (dateChooser.getDate() == null) {
            JOptionPane.showMessageDialog(this,
                "Please select a date for your donation",
                "Validation Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (dateChooser.getDate().before(new Date())) {
            JOptionPane.showMessageDialog(this,
                "Please select a future date for your donation",
                "Validation Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Check if user is eligible to donate
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT last_donation_date, is_eligible FROM donors WHERE donor_id = ?")) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Date lastDonation = rs.getDate("last_donation_date");
                boolean isEligible = rs.getBoolean("is_eligible");

                if (!isEligible) {
                    JOptionPane.showMessageDialog(this,
                        "You are currently not eligible to donate blood",
                        "Eligibility Error",
                        JOptionPane.ERROR_MESSAGE);
                    return false;
                }

                if (lastDonation != null) {
                    // Check if 56 days (8 weeks) have passed since last donation
                    long daysSinceLastDonation = (new Date().getTime() - lastDonation.getTime()) / (1000 * 60 * 60 * 24);
                    if (daysSinceLastDonation < 56) {
                        JOptionPane.showMessageDialog(this,
                            "You must wait at least 56 days between donations.\n" +
                            "Days remaining: " + (56 - daysSinceLastDonation),
                            "Eligibility Error",
                            JOptionPane.ERROR_MESSAGE);
                        return false;
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error checking eligibility: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    private void scheduleDonation(Date date, String time, String center, String notes) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO appointments (donor_id, appointment_date, appointment_time, " +
                     "notes, status) VALUES (?, ?, ?, ?, 'PENDING')")) {
            
            // Convert 12-hour time format to 24-hour format
            SimpleDateFormat inputFormat = new SimpleDateFormat("hh:mm a");
            SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm:ss");
            Date timeDate = inputFormat.parse(time);
            String formattedTime = outputFormat.format(timeDate);
            
            stmt.setInt(1, userId);
            stmt.setDate(2, new java.sql.Date(date.getTime()));
            stmt.setString(3, formattedTime);
            stmt.setString(4, notes);
            
            stmt.executeUpdate();
            
            JOptionPane.showMessageDialog(this,
                "Donation appointment submitted successfully!\n" +
                "Status: Pending admin approval\n" +
                "Date: " + new SimpleDateFormat("yyyy-MM-dd").format(date) + "\n" +
                "Time: " + time,
                "Success",
                JOptionPane.INFORMATION_MESSAGE);

            // Switch to My Appointments panel
            currentPage = "My Appointments";
            cardLayout.show(contentPanel, "My Appointments");
            updateMenuSelection();
            loadAppointmentsData();
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error scheduling appointment: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private JComponent createMyAppointmentsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("My Appointments");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(220, 53, 69));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Table
        String[] columns = {"Appointment ID", "Date", "Time", "Status", "Notes"};
        appointmentsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        appointmentsTable = new JTable(appointmentsTableModel);
        JScrollPane scrollPane = new JScrollPane(appointmentsTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Load appointments data
        loadAppointmentsData();

        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton cancelButton = new JButton("Cancel Appointment");
        cancelButton.setEnabled(false);

        // Style button
        cancelButton.setBackground(new Color(220, 53, 69));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);

        // Add table row selection listener
        appointmentsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = appointmentsTable.getSelectedRow();
                if (selectedRow != -1) {
                    String status = (String) appointmentsTable.getValueAt(selectedRow, 3);
                    // Only allow cancellation of pending or approved appointments
                    cancelButton.setEnabled(status.equals("PENDING") || status.equals("APPROVED"));
                }
            }
        });

        // Cancel button action
        cancelButton.addActionListener(e -> {
            int selectedRow = appointmentsTable.getSelectedRow();
            if (selectedRow != -1) {
                int appointmentId = (int) appointmentsTable.getValueAt(selectedRow, 0);
                cancelAppointment(appointmentId);
            }
        });

        buttonsPanel.add(cancelButton);
        panel.add(buttonsPanel, BorderLayout.SOUTH);

        return new JScrollPane(panel);
    }

    private void loadAppointmentsData() {
        appointmentsTableModel.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT * FROM appointments WHERE donor_id = ? " +
                     "AND status NOT IN ('COMPLETED', 'REJECTED') " +
                     "ORDER BY appointment_date DESC, appointment_time DESC")) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            SimpleDateFormat inputFormat = new SimpleDateFormat("HH:mm:ss");
            SimpleDateFormat outputFormat = new SimpleDateFormat("hh:mm a");
            
            while (rs.next()) {
                // Convert time from 24-hour to 12-hour format
                String dbTime = rs.getString("appointment_time");
                String displayTime = dbTime;
                try {
                    Date timeDate = inputFormat.parse(dbTime);
                    displayTime = outputFormat.format(timeDate);
                } catch (Exception e) {
                    // If time parsing fails, use the original time string
                    e.printStackTrace();
                }
                
                appointmentsTableModel.addRow(new Object[]{
                    rs.getInt("appointment_id"),
                    rs.getDate("appointment_date"),
                    displayTime,
                    rs.getString("status"),
                    rs.getString("notes")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error loading appointments: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cancelAppointment(int appointmentId) {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to cancel this appointment?",
            "Confirm Cancellation",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                         "UPDATE appointments SET status = 'CANCELLED' " +
                         "WHERE appointment_id = ? AND donor_id = ?")) {
                
                stmt.setInt(1, appointmentId);
                stmt.setInt(2, userId);
                
                int updated = stmt.executeUpdate();
                if (updated > 0) {
                    JOptionPane.showMessageDialog(this,
                        "Appointment cancelled successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                    loadAppointmentsData();
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this,
                    "Error cancelling appointment: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JComponent createDonationHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        // Title
        JLabel titleLabel = new JLabel("Donation History");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(220, 53, 69));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Table
        String[] columns = {"Date", "Time", "Status", "Notes"};
        DefaultTableModel historyTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable historyTable = new JTable(historyTableModel);
        JScrollPane scrollPane = new JScrollPane(historyTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Load donation history
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT * FROM appointments WHERE donor_id = ? " +
                     "AND status IN ('COMPLETED', 'REJECTED') " +
                     "ORDER BY appointment_date DESC, appointment_time DESC")) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            SimpleDateFormat inputFormat = new SimpleDateFormat("HH:mm:ss");
            SimpleDateFormat outputFormat = new SimpleDateFormat("hh:mm a");
            
            while (rs.next()) {
                // Convert time from 24-hour to 12-hour format
                String dbTime = rs.getString("appointment_time");
                String displayTime = dbTime;
                try {
                    Date timeDate = inputFormat.parse(dbTime);
                    displayTime = outputFormat.format(timeDate);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
                historyTableModel.addRow(new Object[]{
                    rs.getDate("appointment_date"),
                    displayTime,
                    rs.getString("status"),
                    rs.getString("notes")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error loading donation history: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }

        return new JScrollPane(panel);
    }

    private JComponent createBloodAvailabilityPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        // Title
        JLabel titleLabel = new JLabel("Current Blood Availability");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(220, 53, 69));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Table
        String[] columns = {"Blood Type", "Units Available", "Status"};
        Object[][] data = {
            {"A+", "50", "Available"},
            {"A-", "30", "Available"},
            {"B+", "45", "Available"},
            {"B-", "25", "Low"},
            {"AB+", "20", "Low"},
            {"AB-", "15", "Critical"},
            {"O+", "60", "Available"},
            {"O-", "35", "Available"}
        };

        JTable table = new JTable(data, columns);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        return new JScrollPane(panel);
    }

    private JComponent createProfilePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel titleLabel = new JLabel("My Profile");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(220, 53, 69));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        // Profile fields
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        panel.add(new JLabel("Name:"), gbc);
        
        gbc.gridx = 1;
        nameField = new JTextField(20);
        panel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Blood Type:"), gbc);
        
        gbc.gridx = 1;
        String[] bloodTypes = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
        bloodTypeCombo = new JComboBox<>(bloodTypes);
        panel.add(bloodTypeCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Gender:"), gbc);
        
        gbc.gridx = 1;
        String[] genders = {"MALE", "FEMALE", "OTHER"};
        genderCombo = new JComboBox<>(genders);
        panel.add(genderCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("Phone:"), gbc);
        
        gbc.gridx = 1;
        phoneField = new JTextField(20);
        panel.add(phoneField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(new JLabel("Email:"), gbc);
        
        gbc.gridx = 1;
        emailField = new JTextField(20);
        panel.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        panel.add(new JLabel("Address:"), gbc);
        
        gbc.gridx = 1;
        addressField = new JTextField(20);
        panel.add(addressField, gbc);

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(Color.WHITE);

        editButton = new JButton("Edit Profile");
        saveButton = new JButton("Save Changes");
        
        // Style buttons
        editButton.setBackground(new Color(220, 53, 69));
        editButton.setForeground(Color.WHITE);
        editButton.setFocusPainted(false);
        
        saveButton.setBackground(new Color(220, 53, 69));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        saveButton.setVisible(false);

        buttonPanel.add(editButton);
        buttonPanel.add(saveButton);

        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        // Set initial state
        setFieldsEditable(false);
        loadProfileData();

        // Add button actions
        editButton.addActionListener(e -> {
            setFieldsEditable(true);
            editButton.setVisible(false);
            saveButton.setVisible(true);
        });

        saveButton.addActionListener(e -> {
            if (saveProfileData()) {
                setFieldsEditable(false);
                editButton.setVisible(true);
                saveButton.setVisible(false);
            }
        });

        return new JScrollPane(panel);
    }

    private void setFieldsEditable(boolean editable) {
        // Name and email remain non-editable for security reasons
        nameField.setEditable(false);
        emailField.setEditable(false);
        
        // Style non-editable fields differently
        nameField.setBackground(new Color(240, 240, 240));
        emailField.setBackground(new Color(240, 240, 240));
        
        // Add tooltip to explain why these fields can't be edited
        nameField.setToolTipText("Please contact support to change your name");
        emailField.setToolTipText("Please contact support to change your email");
        
        // Other fields can be edited
        bloodTypeCombo.setEnabled(editable);
        genderCombo.setEnabled(editable);
        phoneField.setEditable(editable);
        addressField.setEditable(editable);
    }

    private void loadProfileData() {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT d.* FROM donors d " +
                     "JOIN users u ON d.donor_id = u.user_id " +
                     "WHERE u.user_id = ?")) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                nameField.setText(rs.getString("first_name") + " " + rs.getString("last_name"));
                bloodTypeCombo.setSelectedItem(rs.getString("blood_type"));
                genderCombo.setSelectedItem(rs.getString("gender"));
                phoneField.setText(rs.getString("phone"));
                emailField.setText(rs.getString("email"));
                addressField.setText(rs.getString("address"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error loading profile data: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean saveProfileData() {
        // Validate fields
        if (phoneField.getText().trim().isEmpty() || addressField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please fill in all required fields",
                "Validation Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                "UPDATE donors SET blood_type = ?, gender = ?, " +
                "phone = ?, address = ? WHERE donor_id = ?");
            
            stmt.setString(1, (String) bloodTypeCombo.getSelectedItem());
            stmt.setString(2, (String) genderCombo.getSelectedItem());
            stmt.setString(3, phoneField.getText().trim());
            stmt.setString(4, addressField.getText().trim());
            stmt.setInt(5, userId);
            
            int updated = stmt.executeUpdate();
            if (updated > 0) {
                JOptionPane.showMessageDialog(this,
                    "Profile updated successfully!\n" +
                    "Note: Name and email can only be changed through support.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error saving profile data: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    private JComponent createHelpPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        // Title
        JLabel titleLabel = new JLabel("Help & Support");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(220, 53, 69));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Help content
        JTextArea helpText = new JTextArea();
        helpText.setEditable(false);
        helpText.setLineWrap(true);
        helpText.setWrapStyleWord(true);
        helpText.setFont(new Font("Arial", Font.PLAIN, 14));
        helpText.setText("Welcome to the Blood Donation System Help Center!\n\n" +
                        "Common Questions:\n\n" +
                        "1. How often can I donate blood?\n" +
                        "   - You can donate whole blood every 8 weeks (56 days)\n\n" +
                        "2. What are the basic requirements for blood donation?\n" +
                        "   - Be at least 17 years old\n" +
                        "   - Weigh at least 110 pounds\n" +
                        "   - Be in good health\n\n" +
                        "3. How long does a blood donation take?\n" +
                        "   - The actual donation takes about 8-10 minutes\n" +
                        "   - The entire process takes about an hour\n\n" +
                        "For more information or support, contact us at:\n" +
                        "Phone: 1-800-RED-CROSS\n" +
                        "Email: support@blooddonation.org");

        JScrollPane scrollPane = new JScrollPane(helpText);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.add(scrollPane, BorderLayout.CENTER);

        return new JScrollPane(panel);
    }
} 