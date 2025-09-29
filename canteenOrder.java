import java.time.LocalDate;
import java.time.LocalDateTime;
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
            BufferedWriter bw = new BufferedWriter(new FileWriter("canteenOrders.txt", true));
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
        try (BufferedReader br = new BufferedReader(new FileReader("canteenOrders.txt"))) {
            String line;
            boolean hasOrders = false;
            System.out.println("\n=== Your Orders ===");

            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
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
        try (BufferedReader br = new BufferedReader(new FileReader("canteenOrders.txt"))) {
            String line;
            System.out.println("=== All Orders ===");

            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
                System.out.println("Order ID: " + parts[0]);
                System.out.println("Student ID: " + parts[1]);
                System.out.println("Order Date: " + parts[2]);
                System.out.println("Pickup Date: " + parts[3]);
                System.out.println("Location: " + parts[4]);
                System.out.println("Status: " + parts[5]);
                System.out.println("Cost: " + parts[7]);
                System.out.println("-------------------");

            }
        } catch (IOException e) {
            System.out.println("Error reading orders: " + e.getMessage());
        }
    }

    public static void viewOrderByStatus(String status) {
        try (BufferedReader br = new BufferedReader(new FileReader("canteenOrders.txt"))) {
            String line;
            boolean hasOrders = false;
            System.out.println("=== List of Orders ===");

            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts[5].equals(status)) {
                    hasOrders = true;
                    System.out.println("Order ID: " + parts[0]);
                    System.out.println("Student ID: " + parts[1]);
                    System.out.println("Pickup Date: " + parts[3]);
                    System.out.println("Location: " + parts[4]);
                    System.out.println("Cost: " + parts[7]);
                    System.out.println("--------------------");
                }
            }
            if (!hasOrders) {
                System.out.println("No pending orders found");
            }
        } catch (IOException e) {
            System.out.println("Error reading orders: " + e.getMessage());
        }
    }

    public static void updateOrderStatus(String orderId, String newStatus) {
        File inputFile = new File("canteenOrders.txt");
        File tempFile = new File("tempOrders.txt");

        try (BufferedReader br = new BufferedReader(new FileReader(inputFile));
                BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile))) {
            String line;
            boolean updated = false;

            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts[0].equals(orderId)) {
                    parts[5] = newStatus;
                    bw.write(String.join("|", parts));
                    updated = true;
                    System.out.println("Order" + orderId + "status updated to" + newStatus);
                } else {
                    bw.write(line);
                }
                bw.newLine();
            }
            if (!updated) {
                System.out.println("Order ID " + orderId + " not found.");
            }
        } catch (IOException e) {
            System.out.println("Error updating order: " + e.getMessage());
            return;
        }
        inputFile.delete();
        tempFile.renameTo(inputFile);
    }

    public static boolean cancelStudentOrder(String orderId, String studentId) {
        String orderToCancel = null;
        boolean canCancel = false;
        double refundAmount = 0.0;

        try (BufferedReader br = new BufferedReader(new FileReader("canteenOrders.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
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
            student tempStudent = new student(studentId, "", "", 0);
            tempStudent.updateBalance(studentId, currentBalance + refundAmount);

            System.out.println("Order cancelled successfully.");
            System.out.println("Refunded: " + refundAmount);
            return true;
        }
        return false;
    }

    private static boolean removeOrderFromFile(String orderId) {
        File inputFile = new File("canteenOrders.txt");
        File tempFile = new File("tempOrders.txt");

        try (BufferedReader br = new BufferedReader(new FileReader(inputFile));
                BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile))) {
            String line;
            boolean removed = false;

            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
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
}
