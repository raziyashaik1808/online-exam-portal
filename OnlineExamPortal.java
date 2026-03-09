import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;

// Import the specific Timer class for clarity
import javax.swing.Timer; 

public class OnlineExamPortal extends JFrame {
    private static String DB_URL;
private static String DB_USER;
private static String DB_PASSWORD;

static {
    try {
        java.util.Properties props = new java.util.Properties();
        java.io.FileInputStream fis = new java.io.FileInputStream("config.properties");
        props.load(fis);

        DB_URL = props.getProperty("db.url");
        DB_USER = props.getProperty("db.user");
        DB_PASSWORD = props.getProperty("db.password");

    } catch (Exception e) {
        e.printStackTrace();
    }
}
    // Define the mark per question as a constant
    private static final int MARKS_PER_QUESTION = 2;

    private Connection connection;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private String currentUserId;
    private String currentUserName; 
    private String currentUserBranch; 
    
    public OnlineExamPortal() {
        setTitle("Online Exam Portal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        
        // Initialize database connection
        connectToDatabase();
        
        // Setup UI
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        mainPanel.add(createLoginPanel(), "LOGIN");
        mainPanel.add(createSignupPanel(), "SIGNUP");
        mainPanel.add(createAdminDashboard(), "ADMIN");
        mainPanel.add(createStudentDashboard(), "STUDENT");
        
        add(mainPanel);
        cardLayout.show(mainPanel, "LOGIN");
    }
    
    // Database Connection
    private void connectToDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("Database connected successfully!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Database connection failed: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    // LOGIN PANEL
    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(102, 126, 234));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));
        
        JLabel titleLabel = new JLabel("🎓 Online Exam Portal");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(102, 126, 234));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        formPanel.add(titleLabel, gbc);
        
        JLabel subtitleLabel = new JLabel("Welcome Back!");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridy = 1;
        formPanel.add(subtitleLabel, gbc);
        
        gbc.gridwidth = 1;
        gbc.gridy = 2;
        JTextField emailField = new JTextField(20);
        emailField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(224, 224, 224), 2),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        formPanel.add(emailField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        JPasswordField passField = new JPasswordField(20);
        passField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(224, 224, 224), 2),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        formPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        formPanel.add(passField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        String[] roles = {"Student", "Admin"};
        JComboBox<String> roleBox = new JComboBox<>(roles);
        formPanel.add(new JLabel("Role:"), gbc);
        gbc.gridx = 1;
        formPanel.add(roleBox, gbc);
        
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        JButton loginBtn = new JButton("LOGIN");
        loginBtn.setBackground(new Color(102, 126, 234));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFont(new Font("Arial", Font.BOLD, 14));
        loginBtn.setPreferredSize(new Dimension(200, 40));
        
        loginBtn.addActionListener(evt -> performLogin(
            emailField.getText(), 
            new String(passField.getPassword()),
            roleBox.getSelectedItem().toString().toLowerCase()
        ));
        formPanel.add(loginBtn, gbc);
        
        gbc.gridy = 6;
        JButton signupLink = new JButton("Don't have an account? Sign up");
        signupLink.setBorderPainted(false);
        signupLink.setContentAreaFilled(false);
        signupLink.setForeground(new Color(102, 126, 234));
        
        signupLink.addActionListener(evt -> cardLayout.show(mainPanel, "SIGNUP"));
        formPanel.add(signupLink, gbc);
        
        panel.add(formPanel);
        return panel;
    }
    
    // SIGNUP PANEL
    private JPanel createSignupPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(102, 126, 234));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));
        
        JLabel titleLabel = new JLabel("Create Account");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(102, 126, 234));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        formPanel.add(titleLabel, gbc);
        
        gbc.gridwidth = 1;
        
        JTextField nameField = new JTextField(20);
        JTextField emailField = new JTextField(20);
        JPasswordField passField = new JPasswordField(20);
        JPasswordField confirmPassField = new JPasswordField(20);
        String[] roles = {"Student", "Admin"};
        JComboBox<String> roleBox = new JComboBox<>(roles);
        String[] branches = {"MPC", "BiPC"};
        JComboBox<String> branchBox = new JComboBox<>(branches);
        JTextField yearField = new JTextField(20);
        
        // Add fields
        gbc.gridy = 1; gbc.gridx = 0;
        formPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        formPanel.add(nameField, gbc);
        
        gbc.gridy = 2; gbc.gridx = 0;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        formPanel.add(emailField, gbc);
        
        gbc.gridy = 3; gbc.gridx = 0;
        formPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        formPanel.add(passField, gbc);
        
        gbc.gridy = 4; gbc.gridx = 0;
        formPanel.add(new JLabel("Confirm Password:"), gbc);
        gbc.gridx = 1;
        formPanel.add(confirmPassField, gbc);
        
        gbc.gridy = 5; gbc.gridx = 0;
        formPanel.add(new JLabel("Role:"), gbc);
        gbc.gridx = 1;
        formPanel.add(roleBox, gbc);
        
        gbc.gridy = 6; gbc.gridx = 0;
        JLabel branchLabel = new JLabel("Branch:");
        formPanel.add(branchLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(branchBox, gbc);
        
        gbc.gridy = 7; gbc.gridx = 0;
        JLabel yearLabel = new JLabel("Year:");
        formPanel.add(yearLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(yearField, gbc);
        
        // Show/hide branch and year based on role
        roleBox.addActionListener(evt -> {
            boolean isStudent = roleBox.getSelectedItem().equals("Student");
            branchLabel.setVisible(isStudent);
            branchBox.setVisible(isStudent);
            yearLabel.setVisible(isStudent);
            yearField.setVisible(isStudent);
        });
        
        gbc.gridx = 0; gbc.gridy = 8; gbc.gridwidth = 2;
        JButton signupBtn = new JButton("SIGN UP");
        signupBtn.setBackground(new Color(102, 126, 234));
        signupBtn.setForeground(Color.WHITE);
        signupBtn.setFont(new Font("Arial", Font.BOLD, 14));
        signupBtn.setPreferredSize(new Dimension(200, 40));
        
        signupBtn.addActionListener(evt -> performSignup(
            nameField.getText(),
            emailField.getText(),
            new String(passField.getPassword()),
            new String(confirmPassField.getPassword()),
            roleBox.getSelectedItem().toString().toLowerCase(),
            branchBox.getSelectedItem().toString(),
            yearField.getText()
        ));
        formPanel.add(signupBtn, gbc);
        
        gbc.gridy = 9;
        JButton loginLink = new JButton("Already have an account? Login");
        loginLink.setBorderPainted(false);
        loginLink.setContentAreaFilled(false);
        loginLink.setForeground(new Color(102, 126, 234));
        
        loginLink.addActionListener(evt -> cardLayout.show(mainPanel, "LOGIN"));
        formPanel.add(loginLink, gbc);
        
        panel.add(formPanel);
        return panel;
    }
    
    // ADMIN DASHBOARD
    private JPanel createAdminDashboard() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        // Top bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(102, 126, 234));
        topBar.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel welcomeLabel = new JLabel("Admin Dashboard");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        welcomeLabel.setForeground(Color.WHITE);
        topBar.add(welcomeLabel, BorderLayout.WEST);
        
        JPanel userInfoPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userInfoPanel.setOpaque(false);
        
        // Display Admin Name
        JLabel nameLabel = new JLabel("Welcome, Admin");
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        nameLabel.setForeground(Color.WHITE);
        userInfoPanel.add(nameLabel);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBackground(Color.WHITE);
        logoutBtn.setForeground(new Color(102, 126, 234));
        logoutBtn.addActionListener(evt -> logout());
        userInfoPanel.add(logoutBtn);
        
        topBar.add(userInfoPanel, BorderLayout.EAST);
        
        panel.add(topBar, BorderLayout.NORTH);
        
        // Main content
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Subjects panel
        JPanel subjectsPanel = new JPanel(new GridLayout(0, 3, 15, 15));
        JScrollPane scrollPane = new JScrollPane(subjectsPanel);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Results button
        JButton viewResultsBtn = new JButton("📊 View All Results");
        viewResultsBtn.setBackground(new Color(17, 153, 142));
        viewResultsBtn.setForeground(Color.WHITE);
        viewResultsBtn.setFont(new Font("Arial", Font.BOLD, 14));
        viewResultsBtn.setPreferredSize(new Dimension(200, 40));
        viewResultsBtn.addActionListener(evt -> showAdminResults());
        contentPanel.add(viewResultsBtn, BorderLayout.SOUTH);
        
        panel.add(contentPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    // STUDENT DASHBOARD
    private JPanel createStudentDashboard() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        // Top bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(102, 126, 234));
        topBar.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel welcomeLabel = new JLabel("Student Dashboard");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        welcomeLabel.setForeground(Color.WHITE);
        topBar.add(welcomeLabel, BorderLayout.WEST);
        
        JPanel userInfoPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userInfoPanel.setOpaque(false);
        
        // Display Student Name
        JLabel nameLabel = new JLabel("Welcome, Student");
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        nameLabel.setForeground(Color.WHITE);
        userInfoPanel.add(nameLabel);

        // Display Branch
        JLabel branchLabel = new JLabel("Branch: N/A");
        branchLabel.setFont(new Font("Arial", Font.BOLD, 12));
        branchLabel.setForeground(Color.WHITE);
        branchLabel.setOpaque(true);
        branchLabel.setBackground(new Color(245, 87, 108));
        branchLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        userInfoPanel.add(branchLabel);
        
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBackground(Color.WHITE);
        logoutBtn.setForeground(new Color(102, 126, 234));
        logoutBtn.addActionListener(evt -> logout());
        userInfoPanel.add(logoutBtn);
        
        topBar.add(userInfoPanel, BorderLayout.EAST);
        
        panel.add(topBar, BorderLayout.NORTH);
        
        // Main content
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Subjects panel
        JPanel subjectsPanel = new JPanel(new GridLayout(0, 3, 15, 15));
        JScrollPane scrollPane = new JScrollPane(subjectsPanel);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Results button
        JButton viewResultsBtn = new JButton("📊 View My Results");
        viewResultsBtn.setBackground(new Color(17, 153, 142));
        viewResultsBtn.setForeground(Color.WHITE);
        viewResultsBtn.setFont(new Font("Arial", Font.BOLD, 14));
        viewResultsBtn.setPreferredSize(new Dimension(200, 40));
        viewResultsBtn.addActionListener(evt -> showStudentResults());
        contentPanel.add(viewResultsBtn, BorderLayout.SOUTH);
        
        panel.add(contentPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    // LOGIN LOGIC
    private void performLogin(String email, String password, String role) {
        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields!", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            String query;
            if (role.equals("admin")) {
                query = "SELECT admin_id, username FROM admin WHERE username = ? AND password = ?";
            } else {
                query = "SELECT student_id, name, branch FROM student WHERE email = ? AND password = ?";
            }
            
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, email);
            stmt.setString(2, password);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                currentUserId = rs.getString(1);
                currentUserName = rs.getString(2);
                
                if (role.equals("admin")) {
                    loadAdminDashboard();
                    cardLayout.show(mainPanel, "ADMIN");
                } else {
                    currentUserBranch = rs.getString(3);
                    loadStudentDashboard();
                    cardLayout.show(mainPanel, "STUDENT");
                }
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Invalid credentials or role!", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
            
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Login error: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    // SIGNUP LOGIC
    private void performSignup(String name, String email, String password, 
                               String confirmPass, String role, String branch, String year) {
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields!", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!password.equals(confirmPass)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match!", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            // Generate ID
            String id = generateId(role);
            
            String query;
            if (role.equals("admin")) {
                query = "INSERT INTO admin (admin_id, username, password) VALUES (?, ?, ?)";
                PreparedStatement stmt = connection.prepareStatement(query);
                stmt.setString(1, id);
                stmt.setString(2, email);
                stmt.setString(3, password);
                stmt.executeUpdate();
                stmt.close();
            } else {
                query = "INSERT INTO student (student_id, name, email, password, branch, year) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement stmt = connection.prepareStatement(query);
                stmt.setString(1, id);
                stmt.setString(2, name);
                stmt.setString(3, email);
                stmt.setString(4, password);
                stmt.setString(5, branch);
                stmt.setInt(6, Integer.parseInt(year));
                stmt.executeUpdate();
                stmt.close();
            }
            
            JOptionPane.showMessageDialog(this, 
                "Account created successfully! Please login.", 
                "Success", JOptionPane.INFORMATION_MESSAGE);
            cardLayout.show(mainPanel, "LOGIN");
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Signup error: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    // LOAD ADMIN DASHBOARD
    private void loadAdminDashboard() {
        JPanel adminPanel = (JPanel) mainPanel.getComponent(2);
        
        // Update Admin Name Label
        JPanel topBar = (JPanel) adminPanel.getComponent(0);
        JPanel userInfoPanel = (JPanel) topBar.getComponent(1);
        JLabel nameLabel = (JLabel) userInfoPanel.getComponent(0); 
        nameLabel.setText("Welcome, " + currentUserName);

        JPanel contentPanel = (JPanel) adminPanel.getComponent(1);
        JScrollPane scrollPane = (JScrollPane) contentPanel.getComponent(0);
        JPanel subjectsPanel = (JPanel) scrollPane.getViewport().getView();
        subjectsPanel.removeAll();
        
        try {
            String query = "SELECT s.subject_id, s.subject_name, " +
                          "CASE WHEN e.subject_id IS NOT NULL THEN 1 ELSE 0 END as is_active " +
                          "FROM subject s LEFT JOIN exam e ON s.subject_id = e.subject_id";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            while (rs.next()) {
                String subjectId = rs.getString("subject_id");
                String subjectName = rs.getString("subject_name");
                boolean isActive = rs.getInt("is_active") == 1;
                
                JPanel card = createSubjectCard(subjectId, subjectName, isActive, true);
                subjectsPanel.add(card);
            }
            
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        subjectsPanel.revalidate();
        subjectsPanel.repaint();
    }
    
    // LOAD STUDENT DASHBOARD
    private void loadStudentDashboard() {
        JPanel studentPanel = (JPanel) mainPanel.getComponent(3);
        
        // Update Student Name and Branch Label
        JPanel topBar = (JPanel) studentPanel.getComponent(0);
        JPanel userInfoPanel = (JPanel) topBar.getComponent(1);
        JLabel nameLabel = (JLabel) userInfoPanel.getComponent(0);
        JLabel branchLabel = (JLabel) userInfoPanel.getComponent(1);
        
        nameLabel.setText("Welcome, " + currentUserName);
        branchLabel.setText(currentUserBranch);

        JPanel contentPanel = (JPanel) studentPanel.getComponent(1);
        JScrollPane scrollPane = (JScrollPane) contentPanel.getComponent(0);
        JPanel subjectsPanel = new JPanel(new GridLayout(0, 3, 15, 15)); // Re-create panel to clear
        contentPanel.remove(scrollPane); // Remove old scroll pane
        scrollPane = new JScrollPane(subjectsPanel); // Create new scroll pane
        contentPanel.add(scrollPane, BorderLayout.CENTER); // Add new scroll pane
        
        try {
            String query = "SELECT s.subject_id, s.subject_name " +
                          "FROM subject s " +
                          "INNER JOIN exam e ON s.subject_id = e.subject_id " +
                          "INNER JOIN student_subject ss ON s.subject_id = ss.subject_id " +
                          "WHERE ss.student_id = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, currentUserId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                String subjectId = rs.getString("subject_id");
                String subjectName = rs.getString("subject_name");
                
                JPanel card = createSubjectCard(subjectId, subjectName, true, false);
                subjectsPanel.add(card);
            }
            
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        subjectsPanel.revalidate();
        subjectsPanel.repaint();
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    // CREATE SUBJECT CARD
    private JPanel createSubjectCard(String subjectId, String subjectName, 
                                     boolean isActive, boolean isAdmin) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(isActive ? new Color(168, 237, 234) : new Color(245, 247, 250));
        card.setBorder(BorderFactory.createLineBorder(
            isActive ? new Color(102, 126, 234) : Color.GRAY, 2));
        card.setPreferredSize(new Dimension(200, 150));
        
        JLabel nameLabel = new JLabel(subjectName, SwingConstants.CENTER);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        card.add(nameLabel, BorderLayout.CENTER);
        
        JLabel statusLabel = new JLabel(isActive ? "✓ Active" : "○ Inactive", 
                                         SwingConstants.CENTER);
        statusLabel.setOpaque(true);
        statusLabel.setBackground(isActive ? new Color(76, 175, 80) : new Color(158, 158, 158));
        statusLabel.setForeground(Color.WHITE);
        card.add(statusLabel, BorderLayout.SOUTH);
        
        if (isAdmin) {
            card.setCursor(new Cursor(Cursor.HAND_CURSOR));
            card.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent evt) {
                    toggleExam(subjectId);
                }
            });
        } else {
            card.setCursor(new Cursor(Cursor.HAND_CURSOR));
            card.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent evt) {
                    if(isActive) {
                         startExam(subjectId, subjectName);
                    } else {
                         JOptionPane.showMessageDialog(card, "Exam is not active yet!", "Info", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            });
        }
        
        return card;
    }
    
    // TOGGLE EXAM (Admin)
    private void toggleExam(String subjectId) {
        try {
            // Check if exam exists
            String checkQuery = "SELECT subject_id FROM exam WHERE subject_id = ?";
            PreparedStatement checkStmt = connection.prepareStatement(checkQuery);
            checkStmt.setString(1, subjectId);
            ResultSet rs = checkStmt.executeQuery();
            
            if (rs.next()) {
                // Deactivate exam
                String deleteQuery = "DELETE FROM exam WHERE subject_id = ?";
                PreparedStatement deleteStmt = connection.prepareStatement(deleteQuery);
                deleteStmt.setString(1, subjectId);
                deleteStmt.executeUpdate();
                deleteStmt.close();
                JOptionPane.showMessageDialog(this, "Exam deactivated!");
            } else {
                // Activate exam
                // NOTE: Assumes duration column exists in exam table
                String insertQuery = "INSERT INTO exam (subject_id, total_marks, duration) VALUES (?, 100, 60)";
                PreparedStatement insertStmt = connection.prepareStatement(insertQuery);
                insertStmt.setString(1, subjectId);
                insertStmt.executeUpdate();
                insertStmt.close();
                JOptionPane.showMessageDialog(this, "Exam activated!");
            }
            
            rs.close();
            checkStmt.close();
            loadAdminDashboard();
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // START EXAM (Student)
    private void startExam(String subjectId, String subjectName) {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you ready to start the " + subjectName + " exam?",
            "Start Exam", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            new ExamWindow(subjectId, subjectName).setVisible(true);
        }
    }
    
    // EXAM WINDOW
    class ExamWindow extends JFrame {
        private String subjectId;
        private java.util.List<Question> questions;
        private int currentQuestionIndex = 0;
        private Map<Integer, String> answers = new HashMap<>();
        
        // Timer variables
        private int remainingSeconds;
        private Timer timer; // Uses javax.swing.Timer due to import change
        private JLabel timerLabel;
        
        public ExamWindow(String subjectId, String subjectName) {
            this.subjectId = subjectId;
            
            setTitle("Exam: " + subjectName);
            setSize(800, 600);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // Prevent closing mid-exam
            
            // Add a window listener to handle closing attempt
            addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    confirmExit();
                }
            });

            loadQuestions();
            fetchDuration();
            setupUI();
            startTimer();
        }
        
        private void fetchDuration() {
            int defaultDuration = 60; // 60 minutes default if DB fails
            try {
                String query = "SELECT duration FROM exam WHERE subject_id = ?";
                PreparedStatement stmt = connection.prepareStatement(query);
                stmt.setString(1, subjectId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    // Duration is typically stored in minutes in the exam table
                    remainingSeconds = rs.getInt("duration") * 60;
                } else {
                    remainingSeconds = defaultDuration * 60;
                }
                rs.close();
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
                remainingSeconds = defaultDuration * 60;
            }
        }
        
        private void startTimer() {
            timer = new Timer(1000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    remainingSeconds--;
                    updateTimerLabel();
                    if (remainingSeconds <= 0) {
                        timer.stop();
                        autoSubmitExam();
                    }
                }
            });
            timer.start();
        }

        private void updateTimerLabel() {
            int minutes = remainingSeconds / 60;
            int seconds = remainingSeconds % 60;
            timerLabel.setText(String.format("Time Remaining: %02d:%02d", minutes, seconds));
            
            // Change color when time is low
            if (remainingSeconds < 60) {
                timerLabel.setForeground(Color.RED);
            } else if (remainingSeconds < 300) { // 5 minutes
                timerLabel.setForeground(Color.ORANGE);
            }
        }
        
        private void confirmExit() {
            int confirm = JOptionPane.showConfirmDialog(this,
                "The exam is still running. Are you sure you want to exit and submit?",
                "Confirm Exit", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                if (timer != null && timer.isRunning()) {
                    timer.stop();
                }
                submitExam(false); // Submit without confirmation
            }
        }

        private void autoSubmitExam() {
            JOptionPane.showMessageDialog(this,
                "Time's up! The exam will now be automatically submitted.",
                "Time Over", JOptionPane.WARNING_MESSAGE);
            submitExam(false); // Auto-submit without asking for confirmation again
        }

        private void loadQuestions() {
            questions = new ArrayList<>();
            try {
                String query = "SELECT * FROM question WHERE subject_id = ?";
                PreparedStatement stmt = connection.prepareStatement(query);
                stmt.setString(1, subjectId);
                ResultSet rs = stmt.executeQuery();
                
                while (rs.next()) {
                    Question q = new Question();
                    q.id = rs.getInt("question_id");
                    q.text = rs.getString("question_text");
                    q.optionA = rs.getString("option_A");
                    q.optionB = rs.getString("option_B");
                    q.optionC = rs.getString("option_C");
                    q.optionD = rs.getString("option_D");
                    q.correctOption = rs.getString("correct_option");
                    questions.add(q);
                }
                
                rs.close();
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        private void setupUI() {
            JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
            mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            
            if (questions.isEmpty()) {
                mainPanel.add(new JLabel("No questions available for this exam."), 
                              BorderLayout.CENTER);
                add(mainPanel);
                return;
            }
            
            // Header panel for Question and Timer
            JPanel headerPanel = new JPanel(new BorderLayout());
            
            JLabel questionLabel = new JLabel();
            questionLabel.setFont(new Font("Arial", Font.BOLD, 16));
            headerPanel.add(questionLabel, BorderLayout.CENTER);
            
            timerLabel = new JLabel();
            timerLabel.setFont(new Font("Arial", Font.BOLD, 16));
            timerLabel.setForeground(new Color(17, 153, 142)); // Accent Color
            timerLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            updateTimerLabel(); // Initial call
            headerPanel.add(timerLabel, BorderLayout.EAST);

            mainPanel.add(headerPanel, BorderLayout.NORTH);
            
            JPanel optionsPanel = new JPanel(new GridLayout(4, 1, 10, 10));
            ButtonGroup group = new ButtonGroup();
            JRadioButton[] optionButtons = new JRadioButton[4];
            
            for (int i = 0; i < 4; i++) {
                optionButtons[i] = new JRadioButton();
                optionButtons[i].setFont(new Font("Arial", Font.PLAIN, 14));
                group.add(optionButtons[i]);
                optionsPanel.add(optionButtons[i]);
            }
            
            mainPanel.add(optionsPanel, BorderLayout.CENTER);
            
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton prevBtn = new JButton("Previous");
            JButton nextBtn = new JButton("Next");
            JButton submitBtn = new JButton("Submit Exam");
            
            prevBtn.addActionListener(evt -> {
                saveCurrentAnswer(group, optionButtons);
                if (currentQuestionIndex > 0) {
                    currentQuestionIndex--;
                    displayQuestion(questionLabel, optionButtons, group);
                }
            });
            
            nextBtn.addActionListener(evt -> {
                saveCurrentAnswer(group, optionButtons);
                if (currentQuestionIndex < questions.size() - 1) {
                    currentQuestionIndex++;
                    displayQuestion(questionLabel, optionButtons, group);
                }
            });
            
            submitBtn.addActionListener(evt -> {
                saveCurrentAnswer(group, optionButtons);
                submitExam(true); // User-initiated submission
            });
            
            buttonPanel.add(prevBtn);
            buttonPanel.add(nextBtn);
            buttonPanel.add(submitBtn);
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);
            
            displayQuestion(questionLabel, optionButtons, group);
            add(mainPanel);
        }
        
        private void displayQuestion(JLabel questionLabel, 
                                     JRadioButton[] optionButtons, 
                                     ButtonGroup group) {
            Question q = questions.get(currentQuestionIndex);
            questionLabel.setText("Q" + (currentQuestionIndex + 1) + ": " + q.text);
            optionButtons[0].setText("A. " + q.optionA);
            optionButtons[1].setText("B. " + q.optionB);
            optionButtons[2].setText("C. " + q.optionC);
            optionButtons[3].setText("D. " + q.optionD);
            
            // Load saved answer if exists
            group.clearSelection();
            if (answers.containsKey(q.id)) {
                String savedAnswer = answers.get(q.id);
                switch (savedAnswer) {
                    case "A": optionButtons[0].setSelected(true); break;
                    case "B": optionButtons[1].setSelected(true); break;
                    case "C": optionButtons[2].setSelected(true); break;
                    case "D": optionButtons[3].setSelected(true); break;
                }
            }
        }
        
        private void saveCurrentAnswer(ButtonGroup group, JRadioButton[] optionButtons) {
            Question q = questions.get(currentQuestionIndex);
            for (int i = 0; i < optionButtons.length; i++) {
                if (optionButtons[i].isSelected()) {
                    answers.put(q.id, String.valueOf((char)('A' + i)));
                    break;
                }
            }
        }
        
        // Modified submitExam to accept a boolean for user confirmation
        private void submitExam(boolean requireConfirmation) {
            int confirm = JOptionPane.YES_OPTION;
            
            if (requireConfirmation) {
                confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to submit the exam?",
                    "Submit Exam", JOptionPane.YES_NO_OPTION);
            }
            
            if (confirm == JOptionPane.YES_OPTION) {
                if (timer != null && timer.isRunning()) {
                    timer.stop();
                }
                
                int score = calculateScore();
                saveResult(score);
                
                int totalPossibleScore = questions.size() * MARKS_PER_QUESTION;
                
                JOptionPane.showMessageDialog(this,
                    "Exam submitted successfully!\n\nYour Score: " + score + "/" + totalPossibleScore,
                    "Exam Complete", JOptionPane.INFORMATION_MESSAGE);
                
                // Dispose must be last
                dispose();
                loadStudentDashboard();
            }
        }
        
        private int calculateScore() {
            int score = 0;
            for (Question q : questions) {
                if (answers.containsKey(q.id) && 
                    answers.get(q.id).equals(q.correctOption)) {
                    score += MARKS_PER_QUESTION; // Awards 2 marks per correct answer
                }
            }
            return score;
        }
        
        private void saveResult(int score) {
            try {
                int totalMarks = questions.size() * MARKS_PER_QUESTION; 
                
                // Ensure your database 'result' table has a 'total_marks' column
                String query = "INSERT INTO result (student_id, subject_id, score, exam_date, total_marks) " +
                               "VALUES (?, ?, ?, ?, ?) " +
                               "ON DUPLICATE KEY UPDATE score = ?, exam_date = ?, total_marks = ?";
                
                PreparedStatement stmt = connection.prepareStatement(query);
                stmt.setString(1, currentUserId);
                stmt.setString(2, subjectId);
                stmt.setInt(3, score);
                stmt.setDate(4, new java.sql.Date(System.currentTimeMillis()));
                stmt.setInt(5, totalMarks); 
                stmt.setInt(6, score);
                stmt.setDate(7, new java.sql.Date(System.currentTimeMillis()));
                stmt.setInt(8, totalMarks);
                
                stmt.executeUpdate();
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    // Question class
    class Question {
        int id;
        String text;
        String optionA, optionB, optionC, optionD;
        String correctOption;
    }
    
    // SHOW ADMIN RESULTS
    private void showAdminResults() {
        JFrame resultsFrame = new JFrame("All Students Results");
        resultsFrame.setSize(900, 600);
        resultsFrame.setLocationRelativeTo(this);
        
        String[] columns = {"Student Name", "Branch", "Subject", "Score", "Total", "Percentage", "Status", "Date"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        
        try {
            String query = "SELECT st.name, st.branch, sub.subject_name, r.score, " +
                           "IFNULL(r.total_marks, (SELECT COUNT(*) * " + MARKS_PER_QUESTION + " FROM question WHERE subject_id = r.subject_id)) as total_marks, " +
                           "r.exam_date " +
                           "FROM result r " +
                           "JOIN student st ON r.student_id = st.student_id " +
                           "JOIN subject sub ON r.subject_id = sub.subject_id " +
                           "ORDER BY r.exam_date DESC";
            
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            while (rs.next()) {
                String name = rs.getString("name");
                String branch = rs.getString("branch");
                String subject = rs.getString("subject_name");
                int score = rs.getInt("score");
                int total = rs.getInt("total_marks"); 
                
                double percentage = (total > 0) ? (score * 100.0) / total : 0;
                String status = percentage >= 40 ? "PASS" : "FAIL";
                String date = rs.getDate("exam_date").toString();
                
                model.addRow(new Object[]{name, branch, subject, score, total, 
                    String.format("%.1f%%", percentage), status, date});
            }
            
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        JScrollPane scrollPane = new JScrollPane(table);
        resultsFrame.add(scrollPane);
        resultsFrame.setVisible(true);
    }
    
    // SHOW STUDENT RESULTS
    private void showStudentResults() {
        JFrame resultsFrame = new JFrame("My Results");
        resultsFrame.setSize(800, 500);
        resultsFrame.setLocationRelativeTo(this);
        
        String[] columns = {"Subject", "Score", "Total", "Percentage", "Status", "Date"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        
        try {
            String query = "SELECT sub.subject_name, r.score, r.exam_date, " +
                           "IFNULL(r.total_marks, (SELECT COUNT(*) * " + MARKS_PER_QUESTION + " FROM question WHERE subject_id = r.subject_id)) as total_marks " +
                           "FROM result r " +
                           "JOIN subject sub ON r.subject_id = sub.subject_id " +
                           "WHERE r.student_id = ? " +
                           "ORDER BY r.exam_date DESC";
            
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, currentUserId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                String subject = rs.getString("subject_name");
                int score = rs.getInt("score");
                int total = rs.getInt("total_marks"); 
                
                double percentage = (total > 0) ? (score * 100.0) / total : 0;
                String status = percentage >= 40 ? "PASS" : "FAIL";
                String date = rs.getDate("exam_date").toString();
                
                model.addRow(new Object[]{
                    subject, score, total, 
                    String.format("%.1f%%", percentage), 
                    status, date
                });
            }
            
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        JScrollPane scrollPane = new JScrollPane(table);
        resultsFrame.add(scrollPane);
        resultsFrame.setVisible(true);
    }
    
    // GENERATE ID
    private String generateId(String role) throws SQLException {
        String prefix = role.equals("admin") ? "A" : "S";
        String query = role.equals("admin") ? 
            "SELECT admin_id FROM admin ORDER BY admin_id DESC LIMIT 1" :
            "SELECT student_id FROM student ORDER BY student_id DESC LIMIT 1";
        
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        
        int maxNum = 0;
        if (rs.next()) {
            String lastId = rs.getString(1);
            try {
                maxNum = Integer.parseInt(lastId.substring(1));
            } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
            }
        }
        
        rs.close();
        stmt.close();
        
        return prefix + String.format("%03d", maxNum + 1);
    }
    
    // LOGOUT
    private void logout() {
        currentUserId = null;
        currentUserBranch = null;
        cardLayout.show(mainPanel, "LOGIN");
    }
    
    // MAIN METHOD
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            OnlineExamPortal portal = new OnlineExamPortal();
            portal.setVisible(true);
        });
    }
    
    // Close database connection on exit
    @Override
    public void dispose() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        super.dispose();
    }
}