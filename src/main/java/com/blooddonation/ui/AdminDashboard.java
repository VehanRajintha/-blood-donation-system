package com.blooddonation.ui;

import com.blooddonation.util.DatabaseConnection;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.util.Calendar;
import javax.swing.SpinnerNumberModel;
import com.blooddonation.dao.DonorDAO;
import com.blooddonation.dao.DonorDAOImpl;
import com.blooddonation.model.Donor;
import com.blooddonation.dao.RequestDAO;
import com.blooddonation.dao.RequestDAOImpl;
import com.blooddonation.model.Request;
import com.blooddonation.dao.AppointmentDAO;
import com.blooddonation.dao.AppointmentDAOImpl;
import com.blooddonation.model.Appointment;

public class AdminDashboard extends JFrame {
    private JPanel mainPanel;
    private JPanel menuPanel;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private Timer animationTimer;
    private int menuWidth = 0;
    private final int FINAL_MENU_WIDTH = 200;
    private DefaultTableModel donorTableModel;
    private JTable donorTable;
    private DefaultTableModel inventoryTableModel;
    private JTable inventoryTable;
    private DefaultTableModel requestTableModel;
    private JTable requestTable;
    private DefaultTableModel appointmentTableModel;
    private JTable appointmentTable;
    private String currentPage = "Dashboard";
    private DonorDAO donorDAO;
    private RequestDAO requestDAO = new RequestDAOImpl();
    private AppointmentDAO appointmentDAO = new AppointmentDAOImpl();

    public AdminDashboard() {
        setTitle("Blood Donation System - Admin Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
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

        donorDAO = new DonorDAOImpl(); // Demonstrates abstraction/polymorphism
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
            "Dashboard", "Donors", "Blood Inventory",
            "Blood Requests", "Appointments", "Reports", "Settings"
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

        // Add hover effect
        logoutButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                logoutButton.setBackground(new Color(220, 53, 69));
                logoutButton.setForeground(Color.WHITE);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                logoutButton.setBackground(new Color(248, 249, 250));
                logoutButton.setForeground(new Color(220, 53, 69));
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

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!currentPage.equals(text)) {
                    label.setForeground(Color.WHITE);
                }
                panel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!currentPage.equals(text)) {
                    label.setForeground(new Color(255, 255, 255, 200));
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
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
        contentPanel.add(createDonorsPanel(), "Donors");
        contentPanel.add(createBloodInventoryPanel(), "Blood Inventory");
        contentPanel.add(createBloodRequestsPanel(), "Blood Requests");
        contentPanel.add(createAppointmentsPanel(), "Appointments");
        contentPanel.add(createReportsPanel(), "Reports");
        contentPanel.add(createSettingsPanel(), "Settings");
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
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        
        // Stats cards
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(createStatsCard("Total Donors", getTotalDonors()), gbc);

        gbc.gridx = 1;
        panel.add(createStatsCard("Blood Units Available", getTotalBloodUnits()), gbc);

        gbc.gridx = 2;
        panel.add(createStatsCard("Pending Requests", getPendingRequests()), gbc);

        // Blood inventory chart
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        panel.add(createBloodInventoryChart(), gbc);

        return new JScrollPane(panel);
    }

    private JPanel createStatsCard(String title, int value) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 53, 69), 2),
            BorderFactory.createEmptyBorder(20, 30, 20, 30)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel valueLabel = new JLabel(String.valueOf(value));
        valueLabel.setFont(new Font("Arial", Font.BOLD, 24));
        valueLabel.setForeground(new Color(220, 53, 69));
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(titleLabel);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(valueLabel);

        return card;
    }

    private ChartPanel createBloodInventoryChart() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT blood_type, SUM(units) as total FROM blood_inventory " +
                     "WHERE status = 'AVAILABLE' GROUP BY blood_type")) {
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                dataset.setValue(rs.getString("blood_type"),
                               rs.getInt("total"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        JFreeChart chart = ChartFactory.createPieChart(
            "Blood Inventory Distribution",
            dataset,
            true,
            true,
            false
        );

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(600, 400));
        return chartPanel;
    }

    private int getTotalDonors() {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT COUNT(*) as total FROM donors")) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private int getTotalBloodUnits() {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT SUM(units) as total FROM blood_inventory " +
                     "WHERE status = 'AVAILABLE'")) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private int getPendingRequests() {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT COUNT(*) as total FROM blood_requests " +
                     "WHERE status = 'PENDING'")) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private JComponent createDonorsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("Donor Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(220, 53, 69));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Name", "Blood Type", "Phone", "Last Donation", "Status"};
        donorTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        donorTable = new JTable(donorTableModel);
        JScrollPane scrollPane = new JScrollPane(donorTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Load donor data
        loadDonorData();

        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = new JButton("Add Donor");
        JButton editButton = new JButton("Edit");
        JButton deleteButton = new JButton("Delete");

        // Style buttons
        styleButton(addButton);
        styleButton(editButton);
        styleButton(deleteButton);

        // Initially disable edit and delete buttons
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);

        // Add table row selection listener
        donorTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                boolean rowSelected = donorTable.getSelectedRow() != -1;
                editButton.setEnabled(rowSelected);
                deleteButton.setEnabled(rowSelected);
            }
        });

        // Add button action
        addButton.addActionListener(e -> showDonorDialog(null));

        // Edit button action
        editButton.addActionListener(e -> {
            int selectedRow = donorTable.getSelectedRow();
            if (selectedRow != -1) {
                int donorId = (int) donorTable.getValueAt(selectedRow, 0);
                showDonorDialog(getDonorData(donorId));
            }
        });

        // Delete button action
        deleteButton.addActionListener(e -> {
            int selectedRow = donorTable.getSelectedRow();
            if (selectedRow != -1) {
                int donorId = (int) donorTable.getValueAt(selectedRow, 0);
                deleteDonor(donorId);
            }
        });

        buttonsPanel.add(addButton);
        buttonsPanel.add(editButton);
        buttonsPanel.add(deleteButton);
        panel.add(buttonsPanel, BorderLayout.SOUTH);

        return new JScrollPane(panel);
    }

    private void styleButton(JButton button) {
        button.setBackground(new Color(220, 53, 69));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void loadDonorData() {
        donorTableModel.setRowCount(0);
        java.util.List<Donor> donors = donorDAO.getAllDonors();
        for (Donor d : donors) {
            donorTableModel.addRow(new Object[]{
                d.getDonorId(),
                d.getFirstName() + " " + d.getLastName(),
                d.getBloodType(),
                d.getPhone(),
                d.getLastDonationDate(),
                d.isEligible() ? "Eligible" : "Not Eligible"
            });
        }
    }

    private Donor getDonorData(int donorId) {
        return donorDAO.getDonor(donorId);
    }

    private void showDonorDialog(Donor donorData) {
        JDialog dialog = new JDialog(this, donorData == null ? "Add New Donor" : "Edit Donor", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(500, 600);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Form fields
        JTextField firstNameField = new JTextField(20);
        JTextField lastNameField = new JTextField(20);
        JComboBox<String> bloodTypeCombo = new JComboBox<>(new String[]{"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"});
        JComboBox<String> genderCombo = new JComboBox<>(new String[]{"MALE", "FEMALE", "OTHER"});
        JTextField phoneField = new JTextField(20);
        JTextField emailField = new JTextField(20);
        JTextArea addressArea = new JTextArea(3, 20);
        JDateChooser dobChooser = new JDateChooser();
        JDateChooser lastDonationChooser = new JDateChooser();
        JCheckBox eligibleCheck = new JCheckBox("Eligible for donation");
        JTextArea medicalConditionsArea = new JTextArea(3, 20);

        // Add components to form
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("First Name:"), gbc);
        gbc.gridx = 1;
        formPanel.add(firstNameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Last Name:"), gbc);
        gbc.gridx = 1;
        formPanel.add(lastNameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Blood Type:"), gbc);
        gbc.gridx = 1;
        formPanel.add(bloodTypeCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Gender:"), gbc);
        gbc.gridx = 1;
        formPanel.add(genderCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        formPanel.add(phoneField, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        formPanel.add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy = 6;
        formPanel.add(new JLabel("Address:"), gbc);
        gbc.gridx = 1;
        formPanel.add(new JScrollPane(addressArea), gbc);

        gbc.gridx = 0; gbc.gridy = 7;
        formPanel.add(new JLabel("Date of Birth:"), gbc);
        gbc.gridx = 1;
        formPanel.add(dobChooser, gbc);

        gbc.gridx = 0; gbc.gridy = 8;
        formPanel.add(new JLabel("Last Donation:"), gbc);
        gbc.gridx = 1;
        formPanel.add(lastDonationChooser, gbc);

        gbc.gridx = 0; gbc.gridy = 9;
        gbc.gridwidth = 2;
        formPanel.add(eligibleCheck, gbc);

        gbc.gridx = 0; gbc.gridy = 10;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("Medical Conditions:"), gbc);
        gbc.gridx = 1;
        formPanel.add(new JScrollPane(medicalConditionsArea), gbc);

        // If editing, populate fields with existing data
        if (donorData != null) {
            firstNameField.setText(donorData.getFirstName());
            lastNameField.setText(donorData.getLastName());
            bloodTypeCombo.setSelectedItem(donorData.getBloodType());
            genderCombo.setSelectedItem(donorData.getGender());
            phoneField.setText(donorData.getPhone());
            emailField.setText(donorData.getEmail());
            addressArea.setText(donorData.getAddress());
            dobChooser.setDate(donorData.getDateOfBirth());
            if (donorData.getLastDonationDate() != null) {
                lastDonationChooser.setDate(donorData.getLastDonationDate());
            }
            eligibleCheck.setSelected(donorData.isEligible());
            medicalConditionsArea.setText(donorData.getMedicalConditions());
        }

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        styleButton(saveButton);
        styleButton(cancelButton);

        saveButton.addActionListener(e -> {
            if (validateForm(firstNameField, lastNameField, phoneField)) {
                saveDonor(donorData != null ? donorData.getDonorId() : null,
                         firstNameField.getText(),
                         lastNameField.getText(),
                         bloodTypeCombo.getSelectedItem().toString(),
                         genderCombo.getSelectedItem().toString(),
                         phoneField.getText(),
                         emailField.getText(),
                         addressArea.getText(),
                         dobChooser.getDate(),
                         lastDonationChooser.getDate(),
                         eligibleCheck.isSelected(),
                         medicalConditionsArea.getText());
                dialog.dispose();
                loadDonorData();
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(new JScrollPane(formPanel), BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private boolean validateForm(JTextField firstNameField, JTextField lastNameField, JTextField phoneField) {
        if (firstNameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "First name is required", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (lastNameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Last name is required", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (phoneField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Phone number is required", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private void saveDonor(Integer donorId, String firstName, String lastName, String bloodType,
                          String gender, String phone, String email, String address,
                          Date dob, Date lastDonation, boolean isEligible, String medicalConditions) {
        Donor donor = new Donor(
            donorId != null ? donorId : 0,
            firstName,
            lastName,
            bloodType,
            gender,
            phone,
            email,
            address,
            dob,
            lastDonation,
            isEligible,
            medicalConditions
        );
        boolean success;
        if (donorId == null) {
            success = donorDAO.addDonor(donor) > 0;
        } else {
            success = donorDAO.updateDonor(donor);
        }
        if (success) {
            JOptionPane.showMessageDialog(this, "Donor saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Error saving donor.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteDonor(int donorId) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this donor?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = donorDAO.deleteDonor(donorId);
            if (success) {
                JOptionPane.showMessageDialog(this, "Donor deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadDonorData();
            } else {
                JOptionPane.showMessageDialog(this, "Error deleting donor.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JComponent createBloodInventoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("Blood Inventory Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(220, 53, 69));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Blood Type", "Units", "Collection Date", "Expiry Date", "Status"};
        inventoryTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        inventoryTable = new JTable(inventoryTableModel);
        JScrollPane scrollPane = new JScrollPane(inventoryTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Load inventory data
        loadInventoryData();

        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = new JButton("Add Stock");
        JButton updateButton = new JButton("Update");
        JButton removeButton = new JButton("Remove");

        // Style buttons
        styleButton(addButton);
        styleButton(updateButton);
        styleButton(removeButton);

        // Initially disable update and remove buttons
        updateButton.setEnabled(false);
        removeButton.setEnabled(false);

        // Add table row selection listener
        inventoryTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean rowSelected = inventoryTable.getSelectedRow() != -1;
                updateButton.setEnabled(rowSelected);
                removeButton.setEnabled(rowSelected);
            }
        });

        // Add button action
        addButton.addActionListener(e -> showInventoryDialog(null));

        // Update button action
        updateButton.addActionListener(e -> {
            int selectedRow = inventoryTable.getSelectedRow();
            if (selectedRow != -1) {
                int inventoryId = (int) inventoryTable.getValueAt(selectedRow, 0);
                showInventoryDialog(getInventoryData(inventoryId));
            }
        });

        // Remove button action
        removeButton.addActionListener(e -> {
            int selectedRow = inventoryTable.getSelectedRow();
            if (selectedRow != -1) {
                int inventoryId = (int) inventoryTable.getValueAt(selectedRow, 0);
                removeInventory(inventoryId);
            }
        });

        buttonsPanel.add(addButton);
        buttonsPanel.add(updateButton);
        buttonsPanel.add(removeButton);
        panel.add(buttonsPanel, BorderLayout.SOUTH);

        return new JScrollPane(panel);
    }

    private void loadInventoryData() {
        inventoryTableModel.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT * FROM blood_inventory ORDER BY expiry_date")) {
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                inventoryTableModel.addRow(new Object[]{
                    rs.getInt("inventory_id"),
                    rs.getString("blood_type"),
                    rs.getInt("units"),
                    rs.getDate("collection_date"),
                    rs.getDate("expiry_date"),
                    rs.getString("status")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading inventory data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Vector<Object> getInventoryData(int inventoryId) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT * FROM blood_inventory WHERE inventory_id = ?")) {
            
            stmt.setInt(1, inventoryId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Vector<Object> inventoryData = new Vector<>();
                inventoryData.add(rs.getInt("inventory_id"));
                inventoryData.add(rs.getString("blood_type"));
                inventoryData.add(rs.getInt("units"));
                inventoryData.add(rs.getDate("collection_date"));
                inventoryData.add(rs.getDate("expiry_date"));
                inventoryData.add(rs.getString("status"));
                return inventoryData;
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error retrieving inventory data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }

    private void showInventoryDialog(Vector<Object> inventoryData) {
        JDialog dialog = new JDialog(this, inventoryData == null ? "Add Blood Stock" : "Update Blood Stock", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(400, 500);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Form fields
        JComboBox<String> bloodTypeCombo = new JComboBox<>(new String[]{"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"});
        JSpinner unitsSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));
        JDateChooser collectionDateChooser = new JDateChooser();
        JDateChooser expiryDateChooser = new JDateChooser();
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"AVAILABLE", "RESERVED", "DISPOSED"});

        // Add components to form
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Blood Type:"), gbc);
        gbc.gridx = 1;
        formPanel.add(bloodTypeCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Units:"), gbc);
        gbc.gridx = 1;
        formPanel.add(unitsSpinner, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Collection Date:"), gbc);
        gbc.gridx = 1;
        formPanel.add(collectionDateChooser, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Expiry Date:"), gbc);
        gbc.gridx = 1;
        formPanel.add(expiryDateChooser, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        formPanel.add(statusCombo, gbc);

        // If editing, populate fields with existing data
        if (inventoryData != null) {
            bloodTypeCombo.setSelectedItem(inventoryData.get(1));
            unitsSpinner.setValue(inventoryData.get(2));
            collectionDateChooser.setDate((Date) inventoryData.get(3));
            expiryDateChooser.setDate((Date) inventoryData.get(4));
            statusCombo.setSelectedItem(inventoryData.get(5));
        } else {
            // Set default dates for new stock
            collectionDateChooser.setDate(new Date());
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MONTH, 3); // Default expiry of 3 months
            expiryDateChooser.setDate(calendar.getTime());
        }

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        styleButton(saveButton);
        styleButton(cancelButton);

        saveButton.addActionListener(e -> {
            if (validateInventoryForm(collectionDateChooser, expiryDateChooser)) {
                saveInventory(
                    inventoryData != null ? (Integer) inventoryData.get(0) : null,
                    bloodTypeCombo.getSelectedItem().toString(),
                    (Integer) unitsSpinner.getValue(),
                    collectionDateChooser.getDate(),
                    expiryDateChooser.getDate(),
                    statusCombo.getSelectedItem().toString()
                );
                dialog.dispose();
                loadInventoryData();
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private boolean validateInventoryForm(JDateChooser collectionDateChooser, JDateChooser expiryDateChooser) {
        if (collectionDateChooser.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Collection date is required", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (expiryDateChooser.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Expiry date is required", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (expiryDateChooser.getDate().before(collectionDateChooser.getDate())) {
            JOptionPane.showMessageDialog(this, "Expiry date must be after collection date", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private void saveInventory(Integer inventoryId, String bloodType, int units,
                             Date collectionDate, Date expiryDate, String status) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql;
            if (inventoryId == null) {
                sql = "INSERT INTO blood_inventory (blood_type, units, collection_date, expiry_date, status) " +
                      "VALUES (?, ?, ?, ?, ?)";
            } else {
                sql = "UPDATE blood_inventory SET blood_type=?, units=?, collection_date=?, " +
                      "expiry_date=?, status=? WHERE inventory_id=?";
            }

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, bloodType);
            stmt.setInt(2, units);
            stmt.setDate(3, new java.sql.Date(collectionDate.getTime()));
            stmt.setDate(4, new java.sql.Date(expiryDate.getTime()));
            stmt.setString(5, status);

            if (inventoryId != null) {
                stmt.setInt(6, inventoryId);
            }

            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Blood inventory saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving inventory: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void removeInventory(int inventoryId) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to remove this blood stock?",
                "Confirm Remove",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("DELETE FROM blood_inventory WHERE inventory_id = ?")) {
                
                stmt.setInt(1, inventoryId);
                stmt.executeUpdate();
                
                JOptionPane.showMessageDialog(this, "Blood stock removed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadInventoryData();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error removing blood stock: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JComponent createBloodRequestsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("Blood Requests");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(220, 53, 69));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Table
        String[] columns = {"Request ID", "Requester", "Blood Type", "Units", "Hospital", "Priority", "Status", "Required By"};
        requestTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        requestTable = new JTable(requestTableModel);
        JScrollPane scrollPane = new JScrollPane(requestTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Load requests data
        loadRequestsData();

        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addRequestPanelButtons(buttonsPanel);
        panel.add(buttonsPanel, BorderLayout.SOUTH);

        return new JScrollPane(panel);
    }

    private void addRequestPanelButtons(JPanel buttonsPanel) {
        JButton addButton = new JButton("Add Request");
        JButton editButton = new JButton("Edit");
        JButton deleteButton = new JButton("Delete");
        styleButton(addButton);
        styleButton(editButton);
        styleButton(deleteButton);
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
        requestTable.getSelectionModel().addListSelectionListener(e -> {
            boolean rowSelected = requestTable.getSelectedRow() != -1;
            editButton.setEnabled(rowSelected);
            deleteButton.setEnabled(rowSelected);
        });
        addButton.addActionListener(e -> showRequestDialog(null));
        editButton.addActionListener(e -> {
            int selectedRow = requestTable.getSelectedRow();
            if (selectedRow != -1) {
                int requestId = (int) requestTable.getValueAt(selectedRow, 0);
                showRequestDialog(requestDAO.getRequest(requestId));
            }
        });
        deleteButton.addActionListener(e -> {
            int selectedRow = requestTable.getSelectedRow();
            if (selectedRow != -1) {
                int requestId = (int) requestTable.getValueAt(selectedRow, 0);
                deleteRequest(requestId);
            }
        });
        buttonsPanel.add(addButton);
        buttonsPanel.add(editButton);
        buttonsPanel.add(deleteButton);
    }

    private void loadRequestsData() {
        requestTableModel.setRowCount(0);
        java.util.List<Request> requests = requestDAO.getAllRequests();
        for (Request req : requests) {
            requestTableModel.addRow(new Object[]{
                req.getRequestId(),
                req.getRequesterName(),
                req.getBloodType(),
                req.getUnitsNeeded(),
                req.getHospitalName(),
                req.getPriority(),
                req.getStatus(),
                req.getRequiredByDate()
            });
        }
    }

    private void approveRequest(int requestId, String bloodType, int unitsNeeded) {
        // First check if we have enough blood units available
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT SUM(units) as available_units FROM blood_inventory " +
                     "WHERE blood_type = ? AND status = 'AVAILABLE'")) {
            
            stmt.setString(1, bloodType);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                int availableUnits = rs.getInt("available_units");
                if (availableUnits >= unitsNeeded) {
                    // We have enough units, proceed with approval
                    updateRequestStatus(requestId, "APPROVED");
                    // Reserve the blood units
                    reserveBloodUnits(bloodType, unitsNeeded);
                    JOptionPane.showMessageDialog(this, "Request approved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Not enough " + bloodType + " blood units available!\n" +
                            "Available: " + availableUnits + " units\n" +
                            "Needed: " + unitsNeeded + " units",
                            "Insufficient Stock", JOptionPane.WARNING_MESSAGE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error approving request: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void reserveBloodUnits(String bloodType, int unitsNeeded) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE blood_inventory SET status = 'RESERVED' " +
                     "WHERE blood_type = ? AND status = 'AVAILABLE' " +
                     "ORDER BY collection_date ASC LIMIT ?")) {
            
            stmt.setString(1, bloodType);
            stmt.setInt(2, unitsNeeded);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error reserving blood units: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void rejectRequest(int requestId) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to reject this request?",
                "Confirm Rejection",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            updateRequestStatus(requestId, "CANCELLED");
            JOptionPane.showMessageDialog(this, "Request rejected successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void completeRequest(int requestId) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to mark this request as completed?",
                "Confirm Completion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            updateRequestStatus(requestId, "COMPLETED");
            // Update the reserved blood units to disposed
            updateReservedBloodStatus(requestId);
            JOptionPane.showMessageDialog(this, "Request marked as completed!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void updateRequestStatus(int requestId, String status) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE blood_requests SET status = ? WHERE request_id = ?")) {
            
            stmt.setString(1, status);
            stmt.setInt(2, requestId);
            stmt.executeUpdate();
            loadRequestsData(); // Refresh the table
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating request status: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateReservedBloodStatus(int requestId) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE blood_inventory SET status = 'DISPOSED' " +
                     "WHERE status = 'RESERVED' AND inventory_id IN " +
                     "(SELECT inventory_id FROM blood_request_items WHERE request_id = ?)")) {
            
            stmt.setInt(1, requestId);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating blood status: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JComponent createAppointmentsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("Appointment Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(220, 53, 69));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Table
        String[] columns = {"Appointment ID", "Donor Name", "Date", "Time", "Status", "Notes"};
        appointmentTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        appointmentTable = new JTable(appointmentTableModel);
        JScrollPane scrollPane = new JScrollPane(appointmentTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Load appointments data
        loadAppointmentsData();

        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addAppointmentPanelButtons(buttonsPanel);
        panel.add(buttonsPanel, BorderLayout.SOUTH);

        return new JScrollPane(panel);
    }

    private void addAppointmentPanelButtons(JPanel buttonsPanel) {
        JButton addButton = new JButton("Add Appointment");
        JButton editButton = new JButton("Edit");
        JButton deleteButton = new JButton("Delete");
        styleButton(addButton);
        styleButton(editButton);
        styleButton(deleteButton);
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
        appointmentTable.getSelectionModel().addListSelectionListener(e -> {
            boolean rowSelected = appointmentTable.getSelectedRow() != -1;
            editButton.setEnabled(rowSelected);
            deleteButton.setEnabled(rowSelected);
        });
        addButton.addActionListener(e -> showAppointmentDialog(null));
        editButton.addActionListener(e -> {
            int selectedRow = appointmentTable.getSelectedRow();
            if (selectedRow != -1) {
                int appointmentId = (int) appointmentTable.getValueAt(selectedRow, 0);
                showAppointmentDialog(appointmentDAO.getAppointment(appointmentId));
            }
        });
        deleteButton.addActionListener(e -> {
            int selectedRow = appointmentTable.getSelectedRow();
            if (selectedRow != -1) {
                int appointmentId = (int) appointmentTable.getValueAt(selectedRow, 0);
                deleteAppointment(appointmentId);
            }
        });
        buttonsPanel.add(addButton);
        buttonsPanel.add(editButton);
        buttonsPanel.add(deleteButton);
    }

    private void updateAppointmentStatus(int appointmentId, String newStatus) {
        // Show confirmation dialog
        String displayStatus = newStatus.substring(0, 1) + newStatus.substring(1).toLowerCase();
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to " + displayStatus.toLowerCase() + " this appointment?",
            "Confirm " + displayStatus,
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE appointments SET status = ? WHERE appointment_id = ?")) {
                
                stmt.setString(1, newStatus);
                stmt.setInt(2, appointmentId);
                
                int updated = stmt.executeUpdate();
                if (updated > 0) {
                    JOptionPane.showMessageDialog(this,
                        "Appointment " + displayStatus.toLowerCase() + "d successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                    loadAppointmentsData(); // Refresh the table
                } else {
                    JOptionPane.showMessageDialog(this,
                        "No appointment was updated. Please try again.",
                        "Warning",
                        JOptionPane.WARNING_MESSAGE);
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this,
                    "Error updating appointment status: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private String getDisplayStatus(String dbStatus) {
        switch(dbStatus) {
            case "A": return "APPROVED";
            case "R": return "REJECTED";
            case "C": return "COMPLETED";
            case "X": return "CANCELLED";
            case "S":
            default: return "SCHEDULED";
        }
    }

    private void markAppointmentCompleted(int appointmentId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Start transaction
            conn.setAutoCommit(false);
            
            try {
                // Update appointment status
                PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE appointments SET status = 'COMPLETED' WHERE appointment_id = ?");
                stmt.setInt(1, appointmentId);
                stmt.executeUpdate();

                // Get donor information
                stmt = conn.prepareStatement(
                    "SELECT donor_id, appointment_date FROM appointments WHERE appointment_id = ?");
                stmt.setInt(1, appointmentId);
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    int donorId = rs.getInt("donor_id");
                    java.sql.Date donationDate = rs.getDate("appointment_date");
                    
                    // Update donor's last donation date
                    stmt = conn.prepareStatement(
                        "UPDATE donors SET last_donation_date = ? WHERE donor_id = ?");
                    stmt.setDate(1, donationDate);
                    stmt.setInt(2, donorId);
                    stmt.executeUpdate();
                }

                // Commit transaction
                conn.commit();
                
                JOptionPane.showMessageDialog(this,
                    "Appointment marked as completed successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                loadAppointmentsData();
                
            } catch (Exception e) {
                // Rollback transaction on error
                conn.rollback();
                throw e;
            } finally {
                // Restore auto-commit
                conn.setAutoCommit(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error completing appointment: " + e.getMessage(),
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
                     "UPDATE appointments SET status = 'CANCELLED' WHERE appointment_id = ?")) {
                
                stmt.setInt(1, appointmentId);
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

    private void loadAppointmentsData() {
        appointmentTableModel.setRowCount(0);
        java.util.List<Appointment> appointments = appointmentDAO.getAllAppointments();
        for (Appointment appt : appointments) {
            appointmentTableModel.addRow(new Object[]{
                appt.getAppointmentId(),
                appt.getDonorId(),
                appt.getAppointmentDate(),
                appt.getAppointmentTime(),
                appt.getStatus(),
                appt.getNotes()
            });
        }
    }

    private JComponent createReportsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("Reports");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(220, 53, 69));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Reports options panel
        JPanel reportsPanel = new JPanel(new GridLayout(3, 2, 20, 20));
        reportsPanel.setBackground(Color.WHITE);

        // Donation Statistics Report
        JButton donationStatsButton = createReportButton("Donation Statistics", "View donation trends and statistics");
        reportsPanel.add(donationStatsButton);

        // Inventory Report
        JButton inventoryReportButton = createReportButton("Inventory Report", "Current blood stock levels and expiry dates");
        reportsPanel.add(inventoryReportButton);

        // Donor Report
        JButton donorReportButton = createReportButton("Donor Report", "Active donors and donation history");
        reportsPanel.add(donorReportButton);

        // Request Report
        JButton requestReportButton = createReportButton("Request Report", "Blood request statistics and fulfillment rates");
        reportsPanel.add(requestReportButton);

        // Monthly Summary
        JButton monthlySummaryButton = createReportButton("Monthly Summary", "Overview of monthly activities");
        reportsPanel.add(monthlySummaryButton);

        // Custom Report
        JButton customReportButton = createReportButton("Custom Report", "Generate custom reports");
        reportsPanel.add(customReportButton);

        panel.add(reportsPanel, BorderLayout.CENTER);

        return new JScrollPane(panel);
    }

    private JButton createReportButton(String title, String description) {
        JButton button = new JButton("<html><center>" + title + "<br><font size='-2'>" + description + "</font></center></html>");
        button.setPreferredSize(new Dimension(200, 100));
        button.setBackground(new Color(220, 53, 69));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        return button;
    }

    private JComponent createSettingsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("Settings");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(220, 53, 69));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Settings options panel
        JPanel settingsPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        settingsPanel.setBackground(Color.WHITE);

        // System Settings
        addSettingSection(settingsPanel, "System Settings", new String[]{
            "Database Configuration",
            "Backup Settings",
            "Email Notifications",
            "SMS Notifications"
        });

        // User Management
        addSettingSection(settingsPanel, "User Management", new String[]{
            "User Accounts",
            "Role Management",
            "Access Control",
            "Activity Logs"
        });

        // Preferences
        addSettingSection(settingsPanel, "Preferences", new String[]{
            "Language",
            "Time Zone",
            "Date Format",
            "Theme"
        });

        panel.add(settingsPanel, BorderLayout.CENTER);

        // Save button
        JButton saveButton = new JButton("Save Changes");
        saveButton.setBackground(new Color(220, 53, 69));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        panel.add(saveButton, BorderLayout.SOUTH);

        return new JScrollPane(panel);
    }

    private void addSettingSection(JPanel panel, String title, String[] options) {
        JLabel sectionTitle = new JLabel(title);
        sectionTitle.setFont(new Font("Arial", Font.BOLD, 16));
        sectionTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
        panel.add(sectionTitle);

        for (String option : options) {
            JPanel optionPanel = new JPanel(new BorderLayout());
            optionPanel.setBackground(Color.WHITE);
            optionPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 53, 69), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));

            JLabel optionLabel = new JLabel(option);
            optionPanel.add(optionLabel, BorderLayout.WEST);

            JButton configButton = new JButton("Configure");
            configButton.setBackground(new Color(220, 53, 69));
            configButton.setForeground(Color.WHITE);
            configButton.setFocusPainted(false);
            optionPanel.add(configButton, BorderLayout.EAST);

            panel.add(optionPanel);
        }
    }

    private void saveRequest(Integer requestId, String requesterName, String bloodType, int unitsNeeded, String hospitalName, String priority, String status, Date requiredByDate) {
        Request req = new Request(
            requestId != null ? requestId : 0,
            requesterName,
            bloodType,
            unitsNeeded,
            hospitalName,
            priority,
            status,
            requiredByDate
        );
        boolean success = (requestId == null)
            ? requestDAO.addRequest(req) > 0
            : requestDAO.updateRequest(req);
        if (success) {
            JOptionPane.showMessageDialog(this, "Request saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadRequestsData();
        } else {
            JOptionPane.showMessageDialog(this, "Error saving request.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteRequest(int requestId) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this request?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = requestDAO.deleteRequest(requestId);
            if (success) {
                JOptionPane.showMessageDialog(this, "Request deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadRequestsData();
            } else {
                JOptionPane.showMessageDialog(this, "Error deleting request.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void saveAppointment(Integer appointmentId, int donorId, Date date, String time, String status, String notes) {
        Appointment appt = new Appointment(
            appointmentId != null ? appointmentId : 0,
            donorId,
            date,
            time,
            status,
            notes
        );
        boolean success = (appointmentId == null)
            ? appointmentDAO.addAppointment(appt) > 0
            : appointmentDAO.updateAppointment(appt);
        if (success) {
            JOptionPane.showMessageDialog(this, "Appointment saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadAppointmentsData();
        } else {
            JOptionPane.showMessageDialog(this, "Error saving appointment.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteAppointment(int appointmentId) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this appointment?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = appointmentDAO.deleteAppointment(appointmentId);
            if (success) {
                JOptionPane.showMessageDialog(this, "Appointment deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadAppointmentsData();
            } else {
                JOptionPane.showMessageDialog(this, "Error deleting appointment.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
} 