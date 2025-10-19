package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class registerScreen extends JFrame {
    private JTextField idField;
    private JTextField nameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleBox;
    private JComboBox<String> locationBox;
    private JButton registerButton;
    private JButton backButton;

    public registerScreen() {
        setTitle("Register New User");
        setSize(400, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        createComponents();
        setVisible(true);
    }

    private void createComponents() {
        JPanel panel = new JPanel();
        panel.setLayout(null);

        JLabel titleLabel = new JLabel("Register New User");
        titleLabel.setBounds(130, 20, 200, 30);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(titleLabel);

        JLabel idLabel = new JLabel("ID:");
        idLabel.setBounds(50, 70, 100, 25);
        panel.add(idLabel);

        idField = new JTextField();
        idField.setBounds(150, 70, 200, 25);
        panel.add(idField);

        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setBounds(50, 110, 100, 25);
        panel.add(nameLabel);

        nameField = new JTextField();
        nameField.setBounds(150, 110, 200, 25);
        panel.add(nameField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(50, 150, 100, 25);
        panel.add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(150, 150, 200, 25);
        panel.add(passwordField);

        JLabel roleLabel = new JLabel("Role:");
        roleLabel.setBounds(50, 190, 100, 25);
        panel.add(roleLabel);

        String[] roles = { "Student", "Vendor", "Admin" };
        roleBox = new JComboBox<>(roles);
        roleBox.setBounds(150, 190, 200, 25);
        roleBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateLocationBox();
            }
        });
        panel.add(roleBox);

        JLabel locationLabel = new JLabel("Location:");
        locationLabel.setBounds(50, 230, 100, 25);
        panel.add(locationLabel);

        locationBox = new JComboBox<>();
        locationBox.setBounds(150, 230, 200, 25);
        panel.add(locationBox);

        updateLocationBox();

        registerButton = new JButton("Register");
        registerButton.setBounds(100, 290, 100, 30);
        registerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                registerClicked();
            }
        });
        panel.add(registerButton);

        backButton = new JButton("Back");
        backButton.setBounds(220, 290, 100, 30);
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                new loginScreen();
            }
        });
        panel.add(backButton);

        add(panel);
    }

    private void updateLocationBox() {
        locationBox.removeAllItems();
        String selectedRole = (String) roleBox.getSelectedItem();

        if (selectedRole.equals("Student")) {
            locationBox.addItem("Boys Hostel");
            locationBox.addItem("Girls Hostel");
            locationBox.setEnabled(true);
        } else if (selectedRole.equals("Vendor")) {
            locationBox.addItem("Boys Hostel");
            locationBox.addItem("Girls Hostel");
            locationBox.addItem("Canteen");
            locationBox.setEnabled(true);
        } else {
            locationBox.setEnabled(false);
        }
    }

    private void registerClicked() {
        String id = idField.getText().trim();
        String name = nameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String selectedRole = (String) roleBox.getSelectedItem();
        String location = locationBox.isEnabled() ? (String) locationBox.getSelectedItem() : "";

        if (id.isEmpty() || name.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields!");
            return;
        }

        String roleCode = "";
        if (selectedRole.equals("Student"))
            roleCode = "stud";
        else if (selectedRole.equals("Vendor"))
            roleCode = "vend";
        else if (selectedRole.equals("Admin"))
            roleCode = "adm";

        String userData = id + "|" + name + "|" + password + "|" + roleCode + "|" + location;

        try {
            File dbFolder = new File("database");
            if (!dbFolder.exists()) {
                dbFolder.mkdir();
            }

            BufferedWriter bw = new BufferedWriter(new FileWriter("database/userList.txt", true));
            bw.write(userData);
            bw.newLine();
            bw.close();

            if (roleCode.equals("stud")) {
                BufferedWriter balanceBw = new BufferedWriter(new FileWriter("database/hostellerAccounts.txt", true));
                balanceBw.write(id + "|0.0");
                balanceBw.newLine();
                balanceBw.close();
            }

            JOptionPane.showMessageDialog(this, "Registration Successful!");
            dispose();
            new loginScreen();

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving user data: " + e.getMessage());
        }
    }
}