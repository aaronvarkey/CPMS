package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import backend.*;

public class placeOrderScreen extends JFrame {
    private student currentStudent;
    private studentScreen parentScreen;
    private JTextField dateField;
    private JButton selectDateButton;
    private JComboBox<String> locationBox;
    private JTextField costField;
    private JButton placeButton;
    private JButton cancelButton;
    private String selectedDate = null;

    public placeOrderScreen(student s, studentScreen parent) {
        this.currentStudent = s;
        this.parentScreen = parent;

        setTitle("Place Order");
        setSize(400, 320);
        setLocationRelativeTo(parent);

        createComponents();
        setVisible(true);
    }

    private void createComponents() {
        JPanel panel = new JPanel();
        panel.setLayout(null);

        JLabel titleLabel = new JLabel("Place New Order");
        titleLabel.setBounds(130, 20, 200, 30);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(titleLabel);

        JLabel dateLabel = new JLabel("Pickup Date:");
        dateLabel.setBounds(50, 70, 100, 25);
        panel.add(dateLabel);

        dateField = new JTextField();
        dateField.setBounds(160, 70, 140, 25);
        dateField.setEditable(false);
        dateField.setBackground(Color.WHITE);
        dateField.setToolTipText("Click 'Select Date' button");
        panel.add(dateField);

        selectDateButton = new JButton("📅");
        selectDateButton.setBounds(305, 70, 40, 25);
        selectDateButton.setToolTipText("Select Date");
        selectDateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String date = datePicker.showDatePicker(placeOrderScreen.this, "Select Pickup Date");
                if (date != null) {
                    selectedDate = date;
                    dateField.setText(date);
                }
            }
        });
        panel.add(selectDateButton);

        JLabel locationLabel = new JLabel("Location:");
        locationLabel.setBounds(50, 110, 100, 25);
        panel.add(locationLabel);

        String[] locations = { "My Hostel (" + currentStudent.getHostel() + ")", "Canteen" };
        locationBox = new JComboBox<>(locations);
        locationBox.setBounds(160, 110, 180, 25);
        panel.add(locationBox);

        JLabel costLabel = new JLabel("Order Cost:");
        costLabel.setBounds(50, 150, 100, 25);
        panel.add(costLabel);

        costField = new JTextField();
        costField.setBounds(160, 150, 180, 25);
        panel.add(costField);

        JLabel balanceLabel = new JLabel("Your Balance: Rs. " + currentStudent.getBalance());
        balanceLabel.setBounds(160, 180, 200, 25);
        balanceLabel.setForeground(Color.BLUE);
        panel.add(balanceLabel);

        JLabel instructionLabel = new JLabel("* Click calendar icon to select date");
        instructionLabel.setBounds(50, 210, 300, 20);
        instructionLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        instructionLabel.setForeground(Color.GRAY);
        panel.add(instructionLabel);

        placeButton = new JButton("Place Order");
        placeButton.setBounds(90, 240, 120, 30);
        placeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                placeOrder();
            }
        });
        panel.add(placeButton);

        cancelButton = new JButton("Cancel");
        cancelButton.setBounds(220, 240, 120, 30);
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        panel.add(cancelButton);

        add(panel);
    }

    private void placeOrder() {
        String pickupDate = selectedDate; // Use selected date from picker
        String selectedLocation = (String) locationBox.getSelectedItem();
        String costText = costField.getText().trim();

        String finalLocation;
        if (selectedLocation.startsWith("My Hostel")) {
            finalLocation = currentStudent.getHostel();
        } else {
            finalLocation = "Canteen";
        }

        if (pickupDate == null || pickupDate.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a pickup date!");
            return;
        }

        if (costText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter order cost!");
            return;
        }

        double cost;
        try {
            cost = Double.parseDouble(costText);
            if (cost <= 0) {
                JOptionPane.showMessageDialog(this, "Cost must be greater than 0!");
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid cost! Please enter a number.");
            return;
        }

        if (currentStudent.getBalance() < cost) {
            JOptionPane.showMessageDialog(this,
                    "Insufficient balance!\nYour Balance: Rs. " + currentStudent.getBalance() +
                            "\nRequired: Rs. " + cost);
            return;
        }

        try {
            canteenOrder order = new canteenOrder(currentStudent.getId(), pickupDate, finalLocation, cost);
            order.saveToFile();

            double newBalance = currentStudent.getBalance() - cost;
            currentStudent.updateBalance(currentStudent.getId(), newBalance);
            currentStudent.setBalance(newBalance);

            String message = "Order Placed Successfully!\n\n" +
                    "Order ID: " + order.getOrderId() + "\n" +
                    "QR Code: " + order.getQrCode() + "\n" +
                    "Location: " + finalLocation + "\n" +
                    "Pickup Date: " + pickupDate + "\n" +
                    "Cost: Rs. " + cost + "\n" +
                    "Remaining Balance: Rs. " + newBalance;

            JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);

            parentScreen.refreshBalance();
            dispose();

        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Order Failed: " + e.getMessage());
        }
    }
}