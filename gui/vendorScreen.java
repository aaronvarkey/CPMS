package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import backend.*;
import java.io.*;

public class vendorScreen extends JFrame {
    private vendor currentVendor;

    public vendorScreen(vendor v) {
        this.currentVendor = v;

        setTitle("Vendor Dashboard - " + currentVendor.getSLocation());
        setSize(600, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        createComponents();
        setVisible(true);
    }

    private void createComponents() {
        JPanel panel = new JPanel();
        panel.setLayout(null);

        JLabel welcomeLabel = new JLabel("Vendor: " + currentVendor.getName());
        welcomeLabel.setBounds(20, 20, 400, 30);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(welcomeLabel);

        JLabel locationLabel = new JLabel("Serving Location: " + currentVendor.getSLocation());
        locationLabel.setBounds(20, 50, 400, 25);
        locationLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(locationLabel);

        int yPos = 100;
        int buttonHeight = 50;
        int gap = 12;

        JButton btn1 = new JButton("View My Location Orders");
        btn1.setBounds(150, yPos, 300, buttonHeight);
        btn1.addActionListener(e -> showInDialog(() -> currentVendor.viewMyLocationOrders()));
        panel.add(btn1);
        yPos += buttonHeight + gap;

        JButton btn2 = new JButton("View Orders by Status");
        btn2.setBounds(150, yPos, 300, buttonHeight);
        btn2.addActionListener(e -> viewOrdersByStatus());
        panel.add(btn2);
        yPos += buttonHeight + gap;

        JButton btn3 = new JButton("View Pending Orders");
        btn3.setBounds(150, yPos, 300, buttonHeight);
        btn3.addActionListener(e -> showInDialog(() -> currentVendor.viewPendingOrders()));
        panel.add(btn3);
        yPos += buttonHeight + gap;

        JButton btn4 = new JButton("Mark Order as Delivered");
        btn4.setBounds(150, yPos, 300, buttonHeight);
        btn4.setBackground(new Color(46, 125, 50));
        btn4.setForeground(Color.WHITE);
        btn4.setFont(new Font("Arial", Font.BOLD, 14));
        btn4.addActionListener(e -> new markOrderDeliveredScreen(currentVendor));
        panel.add(btn4);
        yPos += buttonHeight + gap;

        JButton btn5 = new JButton("View Order Statistics");
        btn5.setBounds(150, yPos, 300, buttonHeight);
        btn5.addActionListener(e -> showInDialog(() -> currentVendor.viewOrderStats()));
        panel.add(btn5);
        yPos += buttonHeight + gap;

        JButton btn6 = new JButton("View Daily Revenue");
        btn6.setBounds(150, yPos, 300, buttonHeight);
        btn6.addActionListener(e -> viewDailyRevenue());
        panel.add(btn6);
        yPos += buttonHeight + gap;

        JButton exitBtn = new JButton("Exit");
        exitBtn.setBounds(150, yPos, 300, buttonHeight);
        exitBtn.setBackground(new Color(211, 47, 47));
        exitBtn.setForeground(Color.WHITE);
        exitBtn.setFont(new Font("Arial", Font.BOLD, 14));
        exitBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(vendorScreen.this,
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

    private void viewOrdersByStatus() {
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

    private void viewDailyRevenue() {
        String date = datePicker.showDatePicker(this, "Select Date for Revenue");

        if (date != null && !date.trim().isEmpty()) {
            showInDialog(() -> currentVendor.viewDailyRevenue(date.trim()));
        }
    }
}