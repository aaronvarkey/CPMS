package gui;

import javax.swing.*;
import java.awt.*;
import java.io.*;

public class viewOrdersScreen extends JFrame {
    private String studentId;
    private JTextArea ordersArea;
    private JButton closeButton;
    private JButton refreshButton;

    public viewOrdersScreen(String studentId) {
        this.studentId = studentId;

        setTitle("My Orders");
        setSize(700, 600);
        setLocationRelativeTo(null);

        createComponents();
        loadOrders();
        setVisible(true);
    }

    private void createComponents() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("My Orders (Sorted by Date)", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(titleLabel, BorderLayout.NORTH);

        ordersArea = new JTextArea();
        ordersArea.setEditable(false);
        ordersArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(ordersArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> {
            loadOrders();
            JOptionPane.showMessageDialog(this, "Orders refreshed!");
        });
        buttonPanel.add(refreshButton);

        closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());
        buttonPanel.add(closeButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        add(panel);
    }

    private void loadOrders() {
        StringBuilder sb = new StringBuilder();
        boolean hasOrders = false;
        int orderCount = 0;
        double totalSpent = 0;

        try {
            BufferedReader br = new BufferedReader(new FileReader("database/canteenOrders.txt"));
            String line;

            java.util.ArrayList<String[]> ordersList = new java.util.ArrayList<>();

            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");

                if (parts.length < 8)
                    continue;

                if (parts[1].equals(studentId)) {
                    ordersList.add(parts);
                }
            }
            br.close();

            ordersList.sort((a, b) -> a[3].compareTo(b[3]));

            for (String[] parts : ordersList) {
                hasOrders = true;
                orderCount++;
                double cost = Double.parseDouble(parts[7]);
                totalSpent += cost;

                sb.append("╔════════════════════════════════════════════════╗\n");
                sb.append("  Order #").append(orderCount).append("\n");
                sb.append("╚════════════════════════════════════════════════╝\n");
                sb.append("Order ID:       ").append(parts[0]).append("\n");
                sb.append("Order Date:     ").append(parts[2]).append("\n");
                sb.append("Pickup Date:    ").append(parts[3]).append("\n");
                sb.append("Location:       ").append(parts[4]).append("\n");
                sb.append("Status:         ").append(parts[5]).append("\n");
                sb.append("QR Code:        ").append(parts[6]).append("\n");
                sb.append("Cost:           Rs. ").append(parts[7]).append("\n");
                sb.append("════════════════════════════════════════════════\n\n");
            }

            if (!hasOrders) {
                sb.append("\n\n");
                sb.append("     ╔═══════════════════════════════╗\n");
                sb.append("           No orders found.\n");
                sb.append("     ╚═══════════════════════════════╝\n");
            } else {
                sb.append("\n");
                sb.append("╔════════════════════ SUMMARY ═══════════════════╗\n");
                sb.append("  Total Orders:    ").append(orderCount).append("\n");
                sb.append("  Total Spent:     Rs. ").append(String.format("%.2f", totalSpent)).append("\n");
                sb.append("╚════════════════════════════════════════════════╝\n");
            }

        } catch (IOException e) {
            sb.append("Error loading orders: " + e.getMessage());
        }

        ordersArea.setText(sb.toString());
        ordersArea.setCaretPosition(0); // Scroll to top
    }
}