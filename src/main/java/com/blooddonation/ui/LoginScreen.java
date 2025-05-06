package com.blooddonation.ui;

import com.formdev.flatlaf.FlatLightLaf;
import com.blooddonation.util.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.toedter.calendar.JDateChooser;

public class LoginScreen extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel statusLabel;
    private CardLayout cardLayout;
    private JPanel contentPanel;
    
    // Registration fields - Step 1
    private JTextField regUsernameField;
    private JTextField regEmailField;
    private JPasswordField regPasswordField;
    private JPasswordField confirmPasswordField;
    
    // Registration fields - Step 2
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField phoneField;
    private JComboBox<String> bloodTypeCombo;
    private JComboBox<String> genderCombo;
    private JTextArea addressArea;
    private JDateChooser dobChooser;

    public LoginScreen() {
        // Set FlatLaf look and feel
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }

        setTitle("Blood Donation System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        
        // Main panel with gradient background
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int w = getWidth();
                int h = getHeight();
                Color color1 = new Color(220, 53, 69); // Bootstrap red
                Color color2 = new Color(248, 249, 250); // Light gray
                GradientPaint gp = new GradientPaint(0, 0, color1, 0, h, color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        mainPanel.setLayout(new BorderLayout());

        // Content panel with card layout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setOpaque(false);

        // Add panels
        contentPanel.add(createLoginPanel(), "login");
        contentPanel.add(createRegistrationPanel(), "register");

        mainPanel.add(contentPanel, BorderLayout.CENTER);
        add(mainPanel);
        setVisible(true);
    }

    private JPanel createLoginPanel() {
        JPanel loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setOpaque(false);

        // Create a container panel for login content
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        contentPanel.setMaximumSize(new Dimension(400, 500));

        // Form Container with semi-transparent white background
        JPanel formContainer = new JPanel();
        formContainer.setLayout(new BoxLayout(formContainer, BoxLayout.Y_AXIS));
        formContainer.setBackground(new Color(255, 255, 255, 30));
        formContainer.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        formContainer.setMaximumSize(new Dimension(400, 400));

        // Title
        JLabel titleLabel = new JLabel("Blood Donation System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formContainer.add(titleLabel);
        formContainer.add(Box.createRigidArea(new Dimension(0, 30)));

        // Username field panel
        JPanel usernamePanel = new JPanel();
        usernamePanel.setLayout(new BoxLayout(usernamePanel, BoxLayout.Y_AXIS));
        usernamePanel.setOpaque(false);
        usernamePanel.setMaximumSize(new Dimension(300, 70));
        usernamePanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setForeground(Color.WHITE);
        userLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        usernamePanel.add(userLabel);
        usernamePanel.add(Box.createRigidArea(new Dimension(0, 5)));

        usernameField = createStyledTextField("Enter username");
        usernameField.setMaximumSize(new Dimension(300, 35));
        usernameField.setPreferredSize(new Dimension(300, 35));
        usernameField.setAlignmentX(Component.LEFT_ALIGNMENT);
        usernamePanel.add(usernameField);

        formContainer.add(usernamePanel);
        formContainer.add(Box.createRigidArea(new Dimension(0, 15)));

        // Password field panel
        JPanel passwordPanel = new JPanel();
        passwordPanel.setLayout(new BoxLayout(passwordPanel, BoxLayout.Y_AXIS));
        passwordPanel.setOpaque(false);
        passwordPanel.setMaximumSize(new Dimension(300, 70));
        passwordPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setForeground(Color.WHITE);
        passLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordPanel.add(passLabel);
        passwordPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        passwordField = createStyledPasswordField("Enter password");
        passwordField.setMaximumSize(new Dimension(300, 35));
        passwordField.setPreferredSize(new Dimension(300, 35));
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordPanel.add(passwordField);

        formContainer.add(passwordPanel);
        formContainer.add(Box.createRigidArea(new Dimension(0, 25)));

        // Login button
        loginButton = createStyledButton("Login", new Color(220, 53, 69));
        loginButton.setMaximumSize(new Dimension(300, 40));
        loginButton.setPreferredSize(new Dimension(300, 40));
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.addActionListener(this::handleLogin);
        formContainer.add(loginButton);
        formContainer.add(Box.createRigidArea(new Dimension(0, 20)));

        // Register link
        JButton registerLink = new JButton("Don't have an account? Register here");
        styleLink(registerLink);
        registerLink.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerLink.addActionListener(e -> cardLayout.show(this.contentPanel, "register"));
        formContainer.add(registerLink);

        // Status label
        statusLabel = new JLabel("");
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusLabel.setForeground(Color.WHITE);
        formContainer.add(Box.createRigidArea(new Dimension(0, 10)));
        formContainer.add(statusLabel);

        // Add form container to content panel
        contentPanel.add(formContainer);

        // Add content panel to the center of login panel using GridBagLayout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        loginPanel.add(contentPanel, gbc);

        return loginPanel;
    }

    private JComponent createRegistrationPanel() {
        JPanel registrationPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        registrationPanel.setOpaque(false);
        registrationPanel.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));

        // Left side - Step 1
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setOpaque(false);

        // Logo/Icon at the top
        ImageIcon originalIcon = new ImageIcon(getClass().getResource("/images/blood-drop.png"));
        Image scaledImage = originalIcon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(scaledImage));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        leftPanel.add(logoLabel);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Title
        JLabel titleLabel = new JLabel("Create Account");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        leftPanel.add(titleLabel);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Step indicator
        JLabel stepLabel = new JLabel("Step 1: Account Information");
        stepLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        stepLabel.setForeground(new Color(255, 255, 255, 200));
        stepLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        leftPanel.add(stepLabel);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Form Container with white background
        JPanel leftFormContainer = new JPanel();
        leftFormContainer.setLayout(new BoxLayout(leftFormContainer, BoxLayout.Y_AXIS));
        leftFormContainer.setBackground(new Color(255, 255, 255, 30));
        leftFormContainer.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        leftFormContainer.setMaximumSize(new Dimension(350, 400));

        // Username field with icon
        JLabel userIcon = new JLabel("👤");
        userIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        regUsernameField = createStyledTextField("Enter username");
        addFormFieldWithIcon(leftFormContainer, "Username", userIcon, regUsernameField);

        // Email field with icon
        JLabel emailIcon = new JLabel("✉");
        emailIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        regEmailField = createStyledTextField("Enter email address");
        addFormFieldWithIcon(leftFormContainer, "Email Address", emailIcon, regEmailField);

        // Password field with icon
        JLabel passIcon = new JLabel("🔒");
        passIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        regPasswordField = createStyledPasswordField("Enter password");
        addFormFieldWithIcon(leftFormContainer, "Password", passIcon, regPasswordField);

        // Confirm Password field with icon
        JLabel confirmPassIcon = new JLabel("🔒");
        confirmPassIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        confirmPasswordField = createStyledPasswordField("Confirm password");
        addFormFieldWithIcon(leftFormContainer, "Confirm Password", confirmPassIcon, confirmPasswordField);

        leftPanel.add(leftFormContainer);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Back to login link
        JButton loginLink = new JButton("Already have an account? Login here");
        styleLink(loginLink);
        loginLink.addActionListener(e -> cardLayout.show(contentPanel, "login"));
        leftPanel.add(loginLink);

        // Right side - Step 2
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setOpaque(false);

        // Title
        JLabel rightTitleLabel = new JLabel("Personal Details");
        rightTitleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        rightTitleLabel.setForeground(Color.WHITE);
        rightTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        rightPanel.add(rightTitleLabel);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Step indicator
        JLabel rightStepLabel = new JLabel("Step 2: Personal Information");
        rightStepLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        rightStepLabel.setForeground(new Color(255, 255, 255, 200));
        rightStepLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        rightPanel.add(rightStepLabel);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Form Container with white background
        JPanel rightFormContainer = new JPanel();
        rightFormContainer.setLayout(new BoxLayout(rightFormContainer, BoxLayout.Y_AXIS));
        rightFormContainer.setBackground(new Color(255, 255, 255, 30));
        rightFormContainer.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        rightFormContainer.setMaximumSize(new Dimension(350, 600));

        // Name fields in a row
        JPanel namePanel = new JPanel(new GridLayout(1, 2, 10, 0));
        namePanel.setOpaque(false);
        namePanel.setMaximumSize(new Dimension(350, 70));

        // First Name
        JPanel firstNamePanel = new JPanel();
        firstNamePanel.setLayout(new BoxLayout(firstNamePanel, BoxLayout.Y_AXIS));
        firstNamePanel.setOpaque(false);
        JLabel firstNameLabel = new JLabel("First Name");
        firstNameLabel.setForeground(Color.WHITE);
        firstNameField = createStyledTextField("First name");
        firstNamePanel.add(firstNameLabel);
        firstNamePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        firstNamePanel.add(firstNameField);

        // Last Name
        JPanel lastNamePanel = new JPanel();
        lastNamePanel.setLayout(new BoxLayout(lastNamePanel, BoxLayout.Y_AXIS));
        lastNamePanel.setOpaque(false);
        JLabel lastNameLabel = new JLabel("Last Name");
        lastNameLabel.setForeground(Color.WHITE);
        lastNameField = createStyledTextField("Last name");
        lastNamePanel.add(lastNameLabel);
        lastNamePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        lastNamePanel.add(lastNameField);

        namePanel.add(firstNamePanel);
        namePanel.add(lastNamePanel);
        rightFormContainer.add(namePanel);
        rightFormContainer.add(Box.createRigidArea(new Dimension(0, 15)));

        // Phone
        JLabel phoneIcon = new JLabel("📱");
        phoneIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        phoneField = createStyledTextField("Enter phone number");
        addFormFieldWithIcon(rightFormContainer, "Phone Number", phoneIcon, phoneField);

        // Blood Type and Gender in a row
        JPanel typeGenderPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        typeGenderPanel.setOpaque(false);
        typeGenderPanel.setMaximumSize(new Dimension(350, 70));

        // Blood Type
        JPanel bloodTypePanel = new JPanel();
        bloodTypePanel.setLayout(new BoxLayout(bloodTypePanel, BoxLayout.Y_AXIS));
        bloodTypePanel.setOpaque(false);
        JLabel bloodTypeLabel = new JLabel("Blood Type");
        bloodTypeLabel.setForeground(Color.WHITE);
        String[] bloodTypes = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
        bloodTypeCombo = createStyledComboBox(bloodTypes);
        bloodTypePanel.add(bloodTypeLabel);
        bloodTypePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        bloodTypePanel.add(bloodTypeCombo);

        // Gender
        JPanel genderPanel = new JPanel();
        genderPanel.setLayout(new BoxLayout(genderPanel, BoxLayout.Y_AXIS));
        genderPanel.setOpaque(false);
        JLabel genderLabel = new JLabel("Gender");
        genderLabel.setForeground(Color.WHITE);
        String[] genders = {"MALE", "FEMALE", "OTHER"};
        genderCombo = createStyledComboBox(genders);
        genderPanel.add(genderLabel);
        genderPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        genderPanel.add(genderCombo);

        typeGenderPanel.add(bloodTypePanel);
        typeGenderPanel.add(genderPanel);
        rightFormContainer.add(typeGenderPanel);
        rightFormContainer.add(Box.createRigidArea(new Dimension(0, 15)));

        // Date of Birth
        JLabel dobLabel = new JLabel("Date of Birth");
        dobLabel.setForeground(Color.WHITE);
        dobChooser = new JDateChooser();
        dobChooser.setMaximumSize(new Dimension(350, 35));
        styleComponent(dobChooser);
        rightFormContainer.add(dobLabel);
        rightFormContainer.add(Box.createRigidArea(new Dimension(0, 5)));
        rightFormContainer.add(dobChooser);
        rightFormContainer.add(Box.createRigidArea(new Dimension(0, 15)));

        // Address
        JLabel addressLabel = new JLabel("Address");
        addressLabel.setForeground(Color.WHITE);
        addressArea = new JTextArea(3, 20);
        addressArea.setLineWrap(true);
        addressArea.setWrapStyleWord(true);
        styleComponent(addressArea);
        JScrollPane addressScroll = new JScrollPane(addressArea);
        addressScroll.setMaximumSize(new Dimension(350, 70));
        rightFormContainer.add(addressLabel);
        rightFormContainer.add(Box.createRigidArea(new Dimension(0, 5)));
        rightFormContainer.add(addressScroll);

        rightPanel.add(rightFormContainer);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Register button
        JButton registerButton = createStyledButton("Register", new Color(220, 53, 69));
        registerButton.setMaximumSize(new Dimension(350, 45));
        registerButton.addActionListener(this::handleRegistration);
        rightPanel.add(registerButton);

        // Add both panels to the main registration panel
        registrationPanel.add(leftPanel);
        registrationPanel.add(rightPanel);

        // Wrap in scroll pane
        JScrollPane scrollPane = new JScrollPane(registrationPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        return scrollPane;
    }

    // Helper methods for styling
    private JTextField createStyledTextField(String placeholder) {
        JTextField field = new JTextField();
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 53, 69), 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        field.setBackground(new Color(255, 255, 255, 240));
        return field;
    }

    private JPasswordField createStyledPasswordField(String placeholder) {
        JPasswordField field = new JPasswordField();
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 53, 69), 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        field.setBackground(new Color(255, 255, 255, 240));
        return field;
    }

    private JComboBox<String> createStyledComboBox(String[] items) {
        JComboBox<String> comboBox = new JComboBox<>(items);
        comboBox.setMaximumSize(new Dimension(350, 35));
        styleComponent(comboBox);
        return comboBox;
    }

    private void styleComponent(JComponent component) {
        component.setBackground(new Color(255, 255, 255, 240));
        component.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 53, 69), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        if (component instanceof JTextField || component instanceof JTextArea) {
            component.setFont(new Font("Arial", Font.PLAIN, 14));
        }
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void styleLink(JButton link) {
        link.setBorderPainted(false);
        link.setContentAreaFilled(false);
        link.setForeground(Color.WHITE);
        link.setCursor(new Cursor(Cursor.HAND_CURSOR));
        link.setFont(new Font("Arial", Font.PLAIN, 14));
        link.setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    private void addFormFieldWithIcon(JPanel container, String labelText, JLabel icon, JComponent field) {
        container.add(Box.createRigidArea(new Dimension(0, 10)));
        
        JLabel label = new JLabel(labelText);
        label.setForeground(Color.WHITE);
        container.add(label);
        
        container.add(Box.createRigidArea(new Dimension(0, 5)));
        
        JPanel fieldPanel = new JPanel();
        fieldPanel.setLayout(new BoxLayout(fieldPanel, BoxLayout.X_AXIS));
        fieldPanel.setOpaque(false);
        fieldPanel.setMaximumSize(new Dimension(350, 35));
        
        icon.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        fieldPanel.add(icon);
        fieldPanel.add(field);
        
        container.add(fieldPanel);
        container.add(Box.createRigidArea(new Dimension(0, 10)));
    }

    private void handleLogin(ActionEvent e) {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT user_id, role FROM users WHERE username = ? AND password = ?")) {
            
            stmt.setString(1, username);
            stmt.setString(2, password);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String role = rs.getString("role");
                int userId = rs.getInt("user_id");
                statusLabel.setForeground(new Color(40, 167, 69));
                statusLabel.setText("Login successful!");
                
                // Open appropriate dashboard based on role
                SwingUtilities.invokeLater(() -> {
                    if (role.equals("ADMIN")) {
                        new AdminDashboard();
                    } else {
                        new UserDashboard(userId);
                    }
                    this.dispose();
                });
            } else {
                statusLabel.setForeground(new Color(220, 53, 69));
                statusLabel.setText("Invalid username or password");
                passwordField.setText("");
            }
        } catch (Exception ex) {
            statusLabel.setForeground(new Color(220, 53, 69));
            statusLabel.setText("Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void handleRegistration(ActionEvent e) {
        // Validate all fields
        if (!validateRegistrationForm()) {
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // First check if username already exists
                try (PreparedStatement checkStmt = conn.prepareStatement(
                        "SELECT COUNT(*) FROM users WHERE username = ?")) {
                    checkStmt.setString(1, regUsernameField.getText());
                    ResultSet rs = checkStmt.executeQuery();
                    if (rs.next() && rs.getInt(1) > 0) {
                        throw new Exception("Username already exists");
                    }
                }

                // Insert into users table
                int userId;
                try (PreparedStatement stmt = conn.prepareStatement(
                        "INSERT INTO users (username, password, role) VALUES (?, ?, 'USER')",
                        PreparedStatement.RETURN_GENERATED_KEYS)) {
                    stmt.setString(1, regUsernameField.getText());
                    stmt.setString(2, new String(regPasswordField.getPassword()));
                    stmt.executeUpdate();

                    ResultSet rs = stmt.getGeneratedKeys();
                    if (rs.next()) {
                        userId = rs.getInt(1);
                    } else {
                        throw new Exception("Failed to get user ID");
                    }
                }

                // Insert into donors table
                try (PreparedStatement stmt = conn.prepareStatement(
                        "INSERT INTO donors (user_id, first_name, last_name, blood_type, " +
                        "gender, phone, email, address, date_of_birth, is_eligible) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, true)")) {
                    stmt.setInt(1, userId);
                    stmt.setString(2, firstNameField.getText());
                    stmt.setString(3, lastNameField.getText());
                    stmt.setString(4, (String) bloodTypeCombo.getSelectedItem());
                    stmt.setString(5, (String) genderCombo.getSelectedItem());
                    stmt.setString(6, phoneField.getText());
                    stmt.setString(7, regEmailField.getText());
                    stmt.setString(8, addressArea.getText());
                    stmt.setDate(9, new java.sql.Date(dobChooser.getDate().getTime()));
                    stmt.executeUpdate();
                }

                conn.commit();
                JOptionPane.showMessageDialog(this,
                    "Registration successful! Please login with your credentials.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                cardLayout.show(contentPanel, "login");
                clearRegistrationForm();

            } catch (Exception ex) {
                conn.rollback();
                throw ex;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error during registration: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean validateRegistrationForm() {
        // Validate Step 1 fields
        if (regUsernameField.getText().trim().isEmpty() ||
            regEmailField.getText().trim().isEmpty() ||
            new String(regPasswordField.getPassword()).trim().isEmpty() ||
            new String(confirmPasswordField.getPassword()).trim().isEmpty()) {
            
            JOptionPane.showMessageDialog(this,
                "Please fill in all account information fields",
                "Validation Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String password = new String(regPasswordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this,
                "Passwords do not match",
                "Validation Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validate email format
        if (!regEmailField.getText().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            JOptionPane.showMessageDialog(this,
                "Please enter a valid email address",
                "Validation Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Check if username already exists
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT COUNT(*) FROM users WHERE username = ?")) {
            stmt.setString(1, regUsernameField.getText());
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                JOptionPane.showMessageDialog(this,
                    "Username already exists. Please choose a different username.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error checking username: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validate Step 2 fields
        if (firstNameField.getText().trim().isEmpty() ||
            lastNameField.getText().trim().isEmpty() ||
            phoneField.getText().trim().isEmpty() ||
            addressArea.getText().trim().isEmpty() ||
            dobChooser.getDate() == null) {
            
            JOptionPane.showMessageDialog(this,
                "Please fill in all personal information fields",
                "Validation Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validate age (must be at least 17 years old)
        Date dob = dobChooser.getDate();
        Date today = new Date();
        long age = (today.getTime() - dob.getTime()) / (1000L * 60 * 60 * 24 * 365);
        if (age < 17) {
            JOptionPane.showMessageDialog(this,
                "You must be at least 17 years old to register",
                "Validation Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    private void clearRegistrationForm() {
        // Clear step 1 fields
        regUsernameField.setText("");
        regEmailField.setText("");
        regPasswordField.setText("");
        confirmPasswordField.setText("");

        // Clear step 2 fields
        firstNameField.setText("");
        lastNameField.setText("");
        phoneField.setText("");
        addressArea.setText("");
        dobChooser.setDate(null);
        bloodTypeCombo.setSelectedIndex(0);
        genderCombo.setSelectedIndex(0);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginScreen::new);
    }
} 