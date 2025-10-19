package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import backend.*;
import java.io.*;

public class loginScreen extends JFrame {
    private JTextField idField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;

    public loginScreen() {
        setTitle("CPMS Login");

        setSize(400, 250);

        setLocationRelativeTo(null);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        createComponents();

        setVisible(true);
    }

    private void createComponents() {
        JPanel panel = new JPanel();
        panel.setLayout(null);

        JLabel titleLabel = new JLabel("Canteen Pre-ordering System");
        titleLabel.setBounds(80, 20, 250, 30);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(titleLabel);

        JLabel idLabel = new JLabel("ID:");
        idLabel.setBounds(50, 70, 80, 25);
        panel.add(idLabel);

        idField = new JTextField();
        idField.setBounds(150, 70, 200, 25);
        panel.add(idField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(50, 110, 80, 25);
        panel.add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(150, 110, 200, 25);
        panel.add(passwordField);

        loginButton = new JButton("Login");
        loginButton.setBounds(100, 160, 100, 30);
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loginClicked();
            }
        });
        panel.add(loginButton);

        registerButton = new JButton("Register");
        registerButton.setBounds(220, 160, 100, 30);
        registerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                registerClicked();
            }
        });
        panel.add(registerButton);

        add(panel);
    }

    private void loginClicked() {
        String id = idField.getText();
        String password = new String(passwordField.getPassword());

        if (id.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter ID and password!");
            return;
        }

        user loggedInUser = checkLogin(id, password);

        if (loggedInUser != null) {
            JOptionPane.showMessageDialog(this, "Login Successful!\nWelcome " + loggedInUser.getName());

            this.dispose();

            String role = loggedInUser.getRole();
            if (role.equals("stud")) {
                new studentScreen((student) loggedInUser);
            } else if (role.equals("vend")) {
                new vendorScreen((vendor) loggedInUser); // Add this
            } else if (role.equals("adm")) {
                new adminScreen((admin) loggedInUser); // Add this
            }
        } else {
            JOptionPane.showMessageDialog(this, "Invalid ID or Password!");
            passwordField.setText("");
        }
    }

    private void registerClicked() {
        this.dispose();
        new registerScreen();
    }

    private user checkLogin(String enteredId, String enteredPassword) {
        try {
            BufferedReader br = new BufferedReader(new FileReader("database/userList.txt"));
            String line;

            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");

                if (parts.length < 4)
                    continue;

                String id = parts[0];
                String name = parts[1];
                String password = parts[2];
                String role = parts[3];
                String location = parts.length > 4 ? parts[4] : "";

                if (id.equals(enteredId) && password.equals(enteredPassword)) {
                    if (role.equals("stud")) {
                        double balance = student.loadBalance(id);
                        br.close();
                        return new student(id, name, password, balance, location);
                    } else if (role.equals("vend")) {
                        br.close();
                        return new vendor(id, name, password, location);
                    } else if (role.equals("adm")) {
                        br.close();
                        return new admin(id, name, password);
                    }
                }
            }
            br.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading user data!");
        }
        return null;
    }
}
