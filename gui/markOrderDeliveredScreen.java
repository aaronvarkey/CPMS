package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import backend.*;

public class markOrderDeliveredScreen extends JFrame {
    private vendor currentVendor;
    private JList<String> orderList;
    private DefaultListModel<String> listModel;
    private ArrayList<OrderInfo> orders;
    private JButton deliverButton;
    private JButton refreshButton;
    private JButton closeButton;
    private JTextArea orderDetailsArea;

    private class OrderInfo {
        String orderId;
        String studentId;
        String pickupDate;
        String location;
        String cost;
        String qrCode;
        String orderDate;

        OrderInfo(String orderId, String studentId, String pickupDate, String location,
                String cost, String qrCode, String orderDate) {
            this.orderId = orderId;
            this.studentId = studentId;
            this.pickupDate = pickupDate;
            this.location = location;
            this.cost = cost;
            this.qrCode = qrCode;
            this.orderDate = orderDate;
        }
    }

    public markOrderDeliveredScreen(vendor v) {
        this.currentVendor = v;
        this.orders = new ArrayList<>();

        setTitle("Mark Order as Delivered - " + currentVendor.getSLocation());
        setSize(800, 600);
        setLocationRelativeTo(null);

        createComponents();
        loadOrders();
        setVisible(true);
    }

    private void createComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Select Order to Mark as Delivered", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.setBorder(BorderFactory.createTitledBorder("Pending Orders at " + currentVendor.getSLocation()));

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
        splitPane.setDividerLocation(400);

        mainPanel.add(splitPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        deliverButton = new JButton("Mark as Delivered");
        deliverButton.addActionListener(e -> deliverOrder());
        buttonPanel.add(deliverButton);

        refreshButton = new JButton("Refresh List");
        refreshButton.addActionListener(e -> {
            loadOrders();
            JOptionPane.showMessageDialog(this, "Order list refreshed!");
        });
        buttonPanel.add(refreshButton);

        closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());
        buttonPanel.add(closeButton);

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

                if (status.equals("ORDERED") && location.equals(currentVendor.getSLocation())) {
                    OrderInfo orderInfo = new OrderInfo(orderId, studentId, pickupDate,
                            location, cost, qrCode, orderDate);
                    orders.add(orderInfo);

                    String displayText = orderId + " | " + pickupDate + " | Student: " +
                            studentId + " | Rs." + cost;
                    listModel.addElement(displayText);
                }
            }
            br.close();

            if (listModel.isEmpty()) {
                listModel.addElement("No pending orders at your location");
                deliverButton.setEnabled(false);
            } else {
                deliverButton.setEnabled(true);
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
        details.append("╔════════════════════════════════════════╗\n");
        details.append("           ORDER DETAILS\n");
        details.append("╚════════════════════════════════════════╝\n\n");
        details.append("Order ID:       ").append(order.orderId).append("\n");
        details.append("Student ID:     ").append(order.studentId).append("\n");
        details.append("Order Date:     ").append(order.orderDate).append("\n");
        details.append("Pickup Date:    ").append(order.pickupDate).append("\n");
        details.append("Location:       ").append(order.location).append("\n");
        details.append("Status:         ORDERED (Pending)\n");
        details.append("QR Code:        ").append(order.qrCode).append("\n");
        details.append("Cost:           Rs. ").append(order.cost).append("\n\n");
        details.append("════════════════════════════════════════\n");
        details.append("To deliver: Enter Order ID and QR Code\n");
        details.append("════════════════════════════════════════");

        orderDetailsArea.setText(details.toString());
    }

    private void deliverOrder() {
        int selectedIndex = orderList.getSelectedIndex();

        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(this, "Please select an order to deliver!");
            return;
        }

        if (orders.isEmpty() || selectedIndex >= orders.size()) {
            JOptionPane.showMessageDialog(this, "No order selected!");
            return;
        }

        OrderInfo selectedOrder = orders.get(selectedIndex);

        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        inputPanel.add(new JLabel("Order ID:"));
        JTextField orderIdField = new JTextField(selectedOrder.orderId);
        orderIdField.setEditable(false);
        inputPanel.add(orderIdField);

        inputPanel.add(new JLabel("QR Code:"));
        JTextField qrCodeField = new JTextField();
        inputPanel.add(qrCodeField);

        inputPanel.add(new JLabel("Expected QR:"));
        JLabel expectedQR = new JLabel(selectedOrder.qrCode);
        expectedQR.setForeground(Color.BLUE);
        inputPanel.add(expectedQR);

        int result = JOptionPane.showConfirmDialog(this, inputPanel,
                "Verify QR Code to Deliver", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String enteredQR = qrCodeField.getText().trim();

            if (enteredQR.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter QR Code!");
                return;
            }

            boolean success = currentVendor.deliverOrder(selectedOrder.orderId, enteredQR);

            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Order delivered successfully!\n" +
                                "Order ID: " + selectedOrder.orderId + "\n" +
                                "Amount: Rs. " + selectedOrder.cost,
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

                loadOrders();

            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to deliver order!\n" +
                                "Please verify:\n" +
                                "- QR Code matches\n" +
                                "- Order exists and is pending",
                        "Delivery Failed",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}