package backend;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.io.*;

public class canteenOrder {
    private String orderId;
    private String studentId;
    private String orderDate;
    private String pickupDate;
    private String location;
    private String status;
    private String qrCode;
    private double cost;

    public canteenOrder(String studentId, String pickupDate, String location, double cost) {
        if (!timeValidation(pickupDate)) {
            throw new IllegalArgumentException("Invalid Pickup Date!");
        }

        this.orderId = generateOrderId();
        this.studentId = studentId;
        this.orderDate = LocalDateTime.now().toString();
        this.pickupDate = pickupDate;
        this.location = location;
        this.status = "ORDERED";
        this.qrCode = generateQRCode();
        this.cost = cost;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getQrCode() {
        return qrCode;
    }

    public String getLocation() {
        return location;
    }

    public String getPickupDate() {
        return pickupDate;
    }

    public String getStatus() {
        return status;
    }

    public double getCost() {
        return cost;
    }

    public String getOrderDate() {
        return orderDate;
    }

    private String generateOrderId() {
        return "ORD" + System.currentTimeMillis();
    }

    private String generateQRCode() {
        return studentId + "_" + location + "_" + System.currentTimeMillis();
    }

    public void saveToFile() {
        String Order = orderId + "|" + studentId + "|" + orderDate + "|" +
                pickupDate + "|" + location + "|" + status + "|" +
                qrCode + "|" + cost;
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("database/canteenOrders.txt", true));
            bw.write(Order);
            bw.newLine();
            bw.close();
        } catch (IOException e) {
            System.out.println("Error in writing order to file." + e.getMessage());
        }
    }

    public static boolean timeValidation(String pickupDate) {
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDate today = now.toLocalDate();
            LocalDate requestedDate = LocalDate.parse(pickupDate);

            if (requestedDate.isBefore(today) || requestedDate.isEqual(today)) {
                System.out.println("Cannot order for this date anymore!");
                return false;
            }
            if (requestedDate.isEqual(today.plusDays(1))) {
                if (now.getHour() >= 14) {
                    System.out.println("Orders for tomorrow must be placed before 2 PM today!");
                    return false;
                }
                System.out.println("Order accepted");
            }
            return true;
        } catch (Exception e) {
            System.out.println("Invalid date format.\n" + e.getMessage());
            return false;
        }
    }

    public static void viewStudentOrders(String studentId) {
        try (BufferedReader br = new BufferedReader(new FileReader("database/canteenOrders.txt"))) {
            String line;
            boolean hasOrders = false;
            System.out.println("\n=== Your Orders ===");

            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");

                if (parts.length < 8) {
                    continue;
                }

                if (parts[1].equals(studentId)) {
                    hasOrders = true;
                    System.out.println("Order ID: " + parts[0]);
                    System.out.println("Order Date: " + parts[2]);
                    System.out.println("Pickup Date: " + parts[3]);
                    System.out.println("Location: " + parts[4]);
                    System.out.println("Status: " + parts[5]);
                    System.out.println("Cost: " + parts[7]);
                    System.out.println("QR Code: " + parts[6]);
                    System.out.println("--------------------");
                }
            }
            if (!hasOrders) {
                System.out.println("No orders found.");
            }
        } catch (IOException e) {
            System.out.println("Error reading orders: " + e.getMessage());
        }
    }

    public static void viewAllOrders() {
        Map<LocalDate, List<String[]>> ordersByDate = new TreeMap<>();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy");

        File orderFile = new File("database/canteenOrders.txt");
        if (!orderFile.exists() || orderFile.length() == 0) {
            System.out.println("No orders found.");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader("database/canteenOrders.txt"))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");

                if (parts.length < 8) {
                    System.out.println("Skipping malformed order data: " + line);
                    continue;
                }

                try {
                    LocalDate pickupDate = LocalDate.parse(parts[3]);
                    ordersByDate.computeIfAbsent(pickupDate, k -> new ArrayList<>()).add(parts);
                } catch (Exception e) {
                    System.out.println("Error parsing date for order: " + parts[0] + " - " + e.getMessage());
                    continue;
                }
            }

            if (ordersByDate.isEmpty()) {
                System.out.println("No orders found.");
                return;
            }

            System.out.println("\n========== ALL ORDERS (DAY-WISE) ==========");

            for (Map.Entry<LocalDate, List<String[]>> entry : ordersByDate.entrySet()) {
                LocalDate date = entry.getKey();
                List<String[]> dayOrders = entry.getValue();

                double dayTotal = dayOrders.stream()
                        .mapToDouble(parts -> Double.parseDouble(parts[7]))
                        .sum();

                System.out.println("\n╔═══════════════════════════════════════════════════════╗");
                System.out.println(" " + date.format(dateFormatter));
                System.out.println(
                        "  Orders: " + dayOrders.size() + " | Total Revenue: " + String.format("%.2f", dayTotal));
                System.out.println("╚═══════════════════════════════════════════════════════╝");

                for (String[] parts : dayOrders) {
                    System.out.println("  Order ID: " + parts[0]);
                    System.out.println("  Student ID: " + parts[1]);
                    System.out.println("  Order Date: " + parts[2]);
                    System.out.println("  Location: " + parts[4]);
                    System.out.println("  Status: " + parts[5]);
                    System.out.println("  Cost: " + parts[7]);
                    System.out.println("  " + "─".repeat(55));
                }
            }
            System.out.println("=".repeat(60));
        } catch (IOException e) {
            System.out.println("Error reading orders: " + e.getMessage());
        }
    }

    public static void viewOrderByStatus(String status) {
        Map<LocalDate, List<String[]>> ordersByDate = new TreeMap<>();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy");

        File orderFile = new File("database/canteenOrders.txt");
        if (!orderFile.exists() || orderFile.length() == 0) {
            System.out.println("No orders found.");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader("database/canteenOrders.txt"))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");

                if (parts.length < 8) {
                    continue;
                }

                if (parts[5].equalsIgnoreCase(status)) {
                    try {
                        LocalDate pickupDate = LocalDate.parse(parts[3]);
                        ordersByDate.computeIfAbsent(pickupDate, k -> new ArrayList<>()).add(parts);
                    } catch (Exception e) {
                        continue;
                    }
                }
            }

            if (ordersByDate.isEmpty()) {
                System.out.println("No orders found with status: " + status);
                return;
            }

            System.out.println("\n========== ORDERS WITH STATUS: " + status.toUpperCase() + " (DAY-WISE) ==========");

            for (Map.Entry<LocalDate, List<String[]>> entry : ordersByDate.entrySet()) {
                LocalDate date = entry.getKey();
                List<String[]> dayOrders = entry.getValue();

                double dayTotal = dayOrders.stream()
                        .mapToDouble(parts -> Double.parseDouble(parts[7]))
                        .sum();

                System.out.println("\n╔═══════════════════════════════════════════════════════╗");
                System.out.println(" " + date.format(dateFormatter));
                System.out.println("  Orders: " + dayOrders.size() + " | Total: " + String.format("%.2f", dayTotal));
                System.out.println("╚═══════════════════════════════════════════════════════╝");

                for (String[] parts : dayOrders) {
                    System.out.println("  Order ID: " + parts[0]);
                    System.out.println("  Student ID: " + parts[1]);
                    System.out.println("  Pickup Date: " + parts[3]);
                    System.out.println("  Location: " + parts[4]);
                    System.out.println("  Cost: " + parts[7]);
                    System.out.println("  " + "─".repeat(55));
                }
            }
            System.out.println("=".repeat(60));

        } catch (IOException e) {
            System.out.println("Error reading orders: " + e.getMessage());
        }
    }

    public static boolean updateOrderStatus(String orderId, String newStatus, String qrCode, String vendorLocation) {
        File inputFile = new File("database/canteenOrders.txt");
        File tempFile = new File("database/tempOrders.txt");

        try (BufferedReader br = new BufferedReader(new FileReader(inputFile));
                BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile))) {
            String line;
            boolean updated = false;

            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");

                if (parts.length < 8) {
                    bw.write(line);
                    bw.newLine();
                    continue;
                }

                if (parts[0].equals(orderId)) {

                    if (vendorLocation != null && !parts[4].equals(vendorLocation)) {
                        System.out.println("This order is not for your serving location!");
                        bw.write(line);
                        bw.newLine();
                        continue;
                    }

                    if (parts[5].equals("DELIVERED")) {
                        System.out.println("Order already delivered!");
                        bw.write(line);
                        bw.newLine();
                        continue;
                    }

                    if (qrCode != null && !parts[6].equals(qrCode)) {
                        System.out.println("Invalid QR Code!");
                        bw.write(line);
                        bw.newLine();
                        continue;
                    }

                    parts[5] = newStatus;
                    bw.write(String.join("|", parts));
                    updated = true;
                } else {
                    bw.write(line);
                }
                bw.newLine();
            }

            br.close();
            bw.close();

            if (!updated) {
                tempFile.delete();
                return false;
            }

            inputFile.delete();
            tempFile.renameTo(inputFile);
            return true;

        } catch (IOException e) {
            System.out.println("Error updating order: " + e.getMessage());
            return false;
        }
    }

    public static boolean updateOrderStatus(String orderId, String newStatus) {
        return updateOrderStatus(orderId, newStatus, null, null);
    }

    public static boolean cancelStudentOrder(String orderId, String studentId) {
        String orderToCancel = null;
        boolean canCancel = false;
        double refundAmount = 0.0;

        try (BufferedReader br = new BufferedReader(new FileReader("database/canteenOrders.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");

                if (parts.length < 8) {
                    continue;
                }

                if (parts[0].equals(orderId) && parts[1].equals(studentId)) {
                    orderToCancel = line;
                    if (!parts[1].equals(studentId)) {
                        System.out.println("You can only cancel your own orders!");
                        return false;
                    }
                    if (parts[5].equals("DELIVERED")) {
                        System.out.println("Cannot cancel delivered orders!");
                        return false;
                    }
                    String pickupDate = parts[3];
                    LocalDateTime now = LocalDateTime.now();
                    LocalDate today = now.toLocalDate();
                    LocalDate pickup = LocalDate.parse(pickupDate);

                    if (pickup.equals(today.plusDays(1)) && now.getHour() < 14) {
                        canCancel = true;
                        refundAmount = Double.parseDouble(parts[7]);
                    } else if (pickup.equals(today.plusDays(1)) && now.getHour() >= 14) {
                        System.out.println("Orders within 24 hours of serving cannot be cancelled.");
                        return false;
                    } else {
                        canCancel = true;
                        refundAmount = Double.parseDouble(parts[7]);
                    }
                    break;
                }
            }
            if (orderToCancel == null) {
                System.out.println("Order not found or doesn't belong to you!");
                return false;
            }
        } catch (IOException e) {
            System.out.println("Error reading orders: " + e.getMessage());
            return false;
        }
        if (!canCancel) {
            return false;
        }
        if (removeOrderFromFile(orderId)) {
            double currentBalance = student.loadBalance(studentId);
            student tempStudent = new student(studentId, "", "", 0, "");
            tempStudent.updateBalance(studentId, currentBalance + refundAmount);

            System.out.println("Order cancelled successfully.");
            System.out.println("Refunded: " + refundAmount);
            return true;
        }
        return false;
    }

    private static boolean removeOrderFromFile(String orderId) {
        File inputFile = new File("database/canteenOrders.txt");
        File tempFile = new File("database/tempOrders.txt");

        try (BufferedReader br = new BufferedReader(new FileReader(inputFile));
                BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile))) {
            String line;
            boolean removed = false;

            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");

                if (parts.length < 8) {
                    bw.write(line);
                    bw.newLine();
                    continue;
                }

                if (!parts[0].equals(orderId)) {
                    bw.write(line);
                    bw.newLine();
                } else {
                    removed = true;
                }
            }
            if (!removed) {
                System.out.println("Order not found for removal!");
                return false;
            }
        } catch (IOException e) {
            System.out.println("Error removing order: " + e.getMessage());
            return false;
        }

        if (inputFile.delete()) {
            return tempFile.renameTo(inputFile);
        }
        return false;
    }

    public static void viewOrdersByLocation(String location) {
        Map<LocalDate, List<String[]>> ordersByDate = new TreeMap<>();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy");

        File orderFile = new File("database/canteenOrders.txt");
        if (!orderFile.exists() || orderFile.length() == 0) {
            System.out.println("No orders found.");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader("database/canteenOrders.txt"))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");

                if (parts.length < 8) {
                    continue;
                }

                if (parts[4].equals(location)) {
                    try {
                        LocalDate pickupDate = LocalDate.parse(parts[3]);
                        ordersByDate.computeIfAbsent(pickupDate, k -> new ArrayList<>()).add(parts);
                    } catch (Exception e) {
                        continue;
                    }
                }
            }

            if (ordersByDate.isEmpty()) {
                System.out.println("No orders found for " + location);
                return;
            }

            System.out.println("\n========== ORDERS FOR: " + location.toUpperCase() + " (DAY-WISE) ==========");

            for (Map.Entry<LocalDate, List<String[]>> entry : ordersByDate.entrySet()) {
                LocalDate date = entry.getKey();
                List<String[]> dayOrders = entry.getValue();

                double dayTotal = dayOrders.stream()
                        .mapToDouble(parts -> Double.parseDouble(parts[7]))
                        .sum();
                System.out.println("\n╔═══════════════════════════════════════════════════════╗");
                System.out.println(" " + date.format(dateFormatter));
                System.out.println("  Orders: " + dayOrders.size() + " | Total: " + String.format("%.2f", dayTotal));
                System.out.println("╚═══════════════════════════════════════════════════════╝");

                for (String[] parts : dayOrders) {
                    System.out.println("  Order ID: " + parts[0]);
                    System.out.println("  Student ID: " + parts[1]);
                    System.out.println("  Status: " + parts[5]);
                    System.out.println("  Cost: " + parts[7]);
                    System.out.println("  " + "─".repeat(55));
                }
            }
            System.out.println("=".repeat(60));

        } catch (IOException e) {
            System.out.println("Error reading orders: " + e.getMessage());
        }
    }

    public static void viewHostelOrdersSummary() {
        Map<String, Map<LocalDate, List<String[]>>> hostelDateOrders = new LinkedHashMap<>();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy");

        try (BufferedReader br = new BufferedReader(new FileReader("database/canteenOrders.txt"))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");

                if (parts.length < 8) {
                    continue;
                }

                String location = parts[4];
                String status = parts[5];

                if (status.equals("ORDERED")) {
                    try {
                        LocalDate pickupDate = LocalDate.parse(parts[3]);
                        hostelDateOrders.computeIfAbsent(location, k -> new TreeMap<>())
                                .computeIfAbsent(pickupDate, k -> new ArrayList<>())
                                .add(parts);
                    } catch (Exception e) {
                        continue;
                    }
                }
            }

            if (hostelDateOrders.isEmpty()) {
                System.out.println("No pending orders found.");
                return;
            }

            System.out.println("\n========== HOSTEL ORDERS SUMMARY (DAY-WISE) ==========");

            for (Map.Entry<String, Map<LocalDate, List<String[]>>> hostelEntry : hostelDateOrders.entrySet()) {
                String hostel = hostelEntry.getKey();
                Map<LocalDate, List<String[]>> dateOrders = hostelEntry.getValue();

                int totalOrders = dateOrders.values().stream()
                        .mapToInt(List::size)
                        .sum();
                double totalRevenue = dateOrders.values().stream()
                        .flatMap(List::stream)
                        .mapToDouble(parts -> Double.parseDouble(parts[7]))
                        .sum();

                System.out.println("\n╔═══════════════════════════════════════════════════════╗");
                System.out.println("LOCATION: " + hostel.toUpperCase());
                System.out.println(
                        "Total Orders: " + totalOrders + " | Total Revenue: " + String.format("%.2f", totalRevenue));
                System.out.println("╚═══════════════════════════════════════════════════════╝");

                for (Map.Entry<LocalDate, List<String[]>> dateEntry : dateOrders.entrySet()) {
                    LocalDate date = dateEntry.getKey();
                    List<String[]> dayOrders = dateEntry.getValue();

                    double dayTotal = dayOrders.stream()
                            .mapToDouble(parts -> Double.parseDouble(parts[7]))
                            .sum();

                    System.out.println(date.format(dateFormatter));
                    System.out.println(
                            "  Orders: " + dayOrders.size() + " | Revenue: " + String.format("%.2f", dayTotal));
                    System.out.println("  " + "─".repeat(55));

                    for (String[] parts : dayOrders) {
                        System.out.println("  Order ID: " + parts[0]);
                        System.out.println("  Student ID: " + parts[1]);
                        System.out.println("  Cost: " + parts[7]);
                        System.out.println("  - - - - - - - - - - - - - - - - - -");
                    }
                }
                System.out.println();
            }
            System.out.println("=".repeat(60));
        } catch (IOException e) {
            System.out.println("Error reading orders: " + e.getMessage());
        }
    }
}
