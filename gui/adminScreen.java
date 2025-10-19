package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import backend.*;
import java.io.*;
import java.util.*;

public class adminScreen extends JFrame {
    private admin currentAdmin;

    public adminScreen(admin a) {
        this.currentAdmin = a;

        setTitle("Admin Dashboard");
        setSize(600, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        createComponents();
        setVisible(true);
    }

    private void createComponents() {
        JPanel panel = new JPanel();
        panel.setLayout(null);

        JLabel welcomeLabel = new JLabel("Admin: " + currentAdmin.getName());
        welcomeLabel.setBounds(20, 20, 400, 30);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(welcomeLabel);

        int yPos = 80;
        int buttonHeight = 50;
        int gap = 12;

        JButton btn1 = new JButton("Set Student Balance");
        btn1.setBounds(150, yPos, 300, buttonHeight);
        btn1.setBackground(new Color(25, 118, 210));
        btn1.setForeground(Color.WHITE);
        btn1.setFont(new Font("Arial", Font.BOLD, 14));
        btn1.addActionListener(e -> openSetBalanceScreen());
        panel.add(btn1);
        yPos += buttonHeight + gap;

        JButton btn2 = new JButton("View Student List");
        btn2.setBounds(150, yPos, 300, buttonHeight);
        btn2.addActionListener(e -> showInDialog(() -> currentAdmin.viewStudentList()));
        panel.add(btn2);
        yPos += buttonHeight + gap;

        JButton btn3 = new JButton("View All Orders");
        btn3.setBounds(150, yPos, 300, buttonHeight);
        btn3.addActionListener(e -> showInDialog(() -> canteenOrder.viewAllOrders()));
        panel.add(btn3);
        yPos += buttonHeight + gap;

        JButton btn4 = new JButton("View Orders by Status");
        btn4.setBounds(150, yPos, 300, buttonHeight);
        btn4.addActionListener(e -> viewByStatus());
        panel.add(btn4);
        yPos += buttonHeight + gap;

        JButton btn5 = new JButton("View Orders by Location");
        btn5.setBounds(150, yPos, 300, buttonHeight);
        btn5.addActionListener(e -> viewByLocation());
        panel.add(btn5);
        yPos += buttonHeight + gap;

        JButton btn6 = new JButton("View Location Summary");
        btn6.setBounds(150, yPos, 300, buttonHeight);
        btn6.addActionListener(e -> showInDialog(() -> canteenOrder.viewHostelOrdersSummary()));
        panel.add(btn6);
        yPos += buttonHeight + gap;

        JButton exitBtn = new JButton("Exit");
        exitBtn.setBounds(150, yPos, 300, buttonHeight);
        exitBtn.setBackground(new Color(211, 47, 47));
        exitBtn.setForeground(Color.WHITE);
        exitBtn.setFont(new Font("Arial", Font.BOLD, 14));
        exitBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(adminScreen.this,
                    "Are you sure you want to exit?",
                    "Confirm Exit",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
                new loginScreen();
            }
        });
        panel.add(exitBtn);

        add(panel);
    }

    private void showInDialog(Runnable backendMethod) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);
        PrintStream oldOut = System.out;

        try {
            System.setOut(printStream);
            backendMethod.run();
            System.setOut(oldOut);

            String output = outputStream.toString();

            JTextArea textArea = new JTextArea(output);
            textArea.setEditable(false);
            textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(600, 400));

            JOptionPane.showMessageDialog(this, scrollPane, "Output", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            System.setOut(oldOut);
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void openSetBalanceScreen() {
        new setStudentBalanceScreen(currentAdmin);
    }

    private void viewByStatus() {
        String[] options = { "ORDERED", "DELIVERED" };
        String status = (String) JOptionPane.showInputDialog(this,
                "Select Status:",
                "Filter by Status",
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        if (status != null) {
            showInDialog(() -> canteenOrder.viewOrderByStatus(status));
        }
    }

    private void viewByLocation() {
        String[] options = { "Boys Hostel", "Girls Hostel", "Canteen" };
        String location = (String) JOptionPane.showInputDialog(this,
                "Select Location:",
                "Filter by Location",
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        if (location != null) {
            showInDialog(() -> canteenOrder.viewOrdersByLocation(location));
        }
    }
}