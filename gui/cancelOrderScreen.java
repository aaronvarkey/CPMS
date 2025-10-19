package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import backend.*;

public class cancelOrderScreen extends JFrame {
    private student currentStudent;
    private studentScreen parentScreen;
    private JList<String> orderList;
    private DefaultListModel<String> listModel;
    private ArrayList<OrderInfo> orders;
    private JButton cancelButton;
    private JButton refreshButton;
    private JButton backButton;
    private JTextArea orderDetailsArea;

    private class OrderInfo {
        String orderId;
        String pickupDate;
        String location;
        String cost;
        String qrCode;
        String orderDate;

        OrderInfo(String orderId, String pickupDate, String location, String cost, String qrCode, String orderDate) {
            this.orderId = orderId;
            this.pickupDate = pickupDate;
            this.location = location;
            this.cost = cost;
            this.qrCode = qrCode;
            this.orderDate = orderDate;
        }
    }

    public cancelOrderScreen(student s, studentScreen parent) {
        this.currentStudent = s;
        this.parentScreen = parent;
        this.orders = new ArrayList<>();

        setTitle("Cancel Order");
        setSize(700, 500);
        setLocationRelativeTo(parent);

        createComponents();
        loadOrders();
        setVisible(true);
    }

    private void createComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Select Order to Cancel", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.setBorder(BorderFactory.createTitledBorder("Your Pending Orders"));

        listModel = new DefaultListModel<>();
        orderList = new JList<>(listModel);
        orderList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        orderList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showOrderDetails();
            }
        });

        JScrollPane listScrollPane = new JScrollPane(orderList);
        listPanel.add(listScrollPane, BorderLayout.CENTER);

        JPanel detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Order Details"));

        orderDetailsArea = new JTextArea();
        orderDetailsArea.setEditable(false);
        orderDetailsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane detailsScrollPane = new JScrollPane(orderDetailsArea);
        detailsPanel.add(detailsScrollPane, BorderLayout.CENTER);

        splitPane.setLeftComponent(listPanel);
        splitPane.setRightComponent(detailsPanel);
        splitPane.setDividerLocation(300);

        mainPanel.add(splitPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        cancelButton = new JButton("Cancel Selected Order");
        cancelButton.addActionListener(e -> cancelOrder());
        buttonPanel.add(cancelButton);

        refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> {
            loadOrders();
            JOptionPane.showMessageDialog(this, "Orders refreshed!");
        });
        buttonPanel.add(refreshButton);

        backButton = new JButton("Back");
        backButton.addActionListener(e -> dispose());
        buttonPanel.add(backButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void loadOrders() {
        listModel.clear();
        orders.clear();
        orderDetailsArea.setText("");

        try {
            BufferedReader br = new BufferedReader(new FileReader("database/canteenOrders.txt"));
            String line;

            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");

                if (parts.length < 8)
                    continue;

                String orderId = parts[0];
                String studentId = parts[1];
                String orderDate = parts[2];
                String pickupDate = parts[3];
                String location = parts[4];
                String status = parts[5];
                String qrCode = parts[6];
                String cost = parts[7];

                if (studentId.equals(currentStudent.getId()) && status.equals("ORDERED")) {
                    OrderInfo orderInfo = new OrderInfo(orderId, pickupDate, location, cost, qrCode, orderDate);
                    orders.add(orderInfo);

                    String displayText = orderId + " - " + pickupDate + " - Rs." + cost + " - " + location;
                    listModel.addElement(displayText);
                }
            }
            br.close();

            if (listModel.isEmpty()) {
                listModel.addElement("No orders available to cancel");
                cancelButton.setEnabled(false);
            } else {
                cancelButton.setEnabled(true);
            }

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading orders: " + e.getMessage());
        }
    }

    private void showOrderDetails() {
        int selectedIndex = orderList.getSelectedIndex();
        if (selectedIndex == -1 || orders.isEmpty() || selectedIndex >= orders.size()) {
            orderDetailsArea.setText("Select an order to view details");
            return;
        }

        OrderInfo order = orders.get(selectedIndex);

        StringBuilder details = new StringBuilder();
        details.append("╔════════════════════════════════╗\n");
        details.append("      ORDER DETAILS\n");
        details.append("╚════════════════════════════════╝\n\n");
        details.append("Order ID:       ").append(order.orderId).append("\n");
        details.append("Student ID:     ").append(currentStudent.getId()).append("\n");
        details.append("Order Date:     ").append(order.orderDate).append("\n");
        details.append("Pickup Date:    ").append(order.pickupDate).append("\n");
        details.append("Location:       ").append(order.location).append("\n");
        details.append("Status:         ORDERED\n");
        details.append("QR Code:        ").append(order.qrCode).append("\n");
        details.append("Cost:           Rs. ").append(order.cost).append("\n\n");
        details.append("════════════════════════════════\n");
        details.append("Note: Cancelling will refund\n");
        details.append("Rs. ").append(order.cost).append(" to your account");

        orderDetailsArea.setText(details.toString());
    }

    private void cancelOrder() {
        int selectedIndex = orderList.getSelectedIndex();

        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(this, "Please select an order to cancel!");
            return;
        }

        if (orders.isEmpty() || selectedIndex >= orders.size()) {
            JOptionPane.showMessageDialog(this, "No order selected!");
            return;
        }

        OrderInfo selectedOrder = orders.get(selectedIndex);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to cancel order: " + selectedOrder.orderId + "?\n" +
                        "Rs. " + selectedOrder.cost + " will be refunded to your account.",
                "Confirm Cancellation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = canteenOrder.cancelStudentOrder(selectedOrder.orderId, currentStudent.getId());

            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Order cancelled successfully!\nRs. " + selectedOrder.cost + " refunded to your account.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

                parentScreen.refreshBalance();

                loadOrders();

                currentStudent.setBalance(student.loadBalance(currentStudent.getId()));

            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to cancel order!\nPlease try again.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}