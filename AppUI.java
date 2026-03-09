import javax.swing.*;
import java.awt.*;

public class AppUI extends JFrame {

    // Constructor
    public AppUI() {
        setTitle("Login / Signup Page");
        setSize(350, 220);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(4, 2, 10, 10));

        JLabel userLabel = new JLabel("Username:");
        JTextField userField = new JTextField();

        JLabel passLabel = new JLabel("Password:");
        JPasswordField passField = new JPasswordField();

        JButton loginBtn = new JButton("Login");
        JButton signupBtn = new JButton("Signup");

        add(userLabel);
        add(userField);
        add(passLabel);
        add(passField);
        add(loginBtn);
        add(signupBtn);

        // Login button click
        loginBtn.addActionListener(e -> {
            String username = userField.getText();
            String password = new String(passField.getPassword());
            if (DatabaseHelper.checkLogin(username, password)) {
                JOptionPane.showMessageDialog(this, "✅ Login successful!");
            } else {
                JOptionPane.showMessageDialog(this, "❌ Invalid credentials!");
            }
        });

        // Signup button click
        signupBtn.addActionListener(e -> {
            String username = userField.getText();
            String password = new String(passField.getPassword());
            if (DatabaseHelper.registerUser(username, password)) {
                JOptionPane.showMessageDialog(this, "✅ Signup successful!");
            } else {
                JOptionPane.showMessageDialog(this, "⚠️ User already exists!");
            }
        });

        setVisible(true);
    }

    public static void main(String[] args) {
        new AppUI();
    }
}