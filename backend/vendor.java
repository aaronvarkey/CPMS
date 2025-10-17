package backend;

import java.io.*;

public class vendor extends user {
    private String SLocation;

    public vendor(String id, String name, String password, String SLocation) {
        super(id, name, password, "vend");
        this.SLocation = SLocation;
    }

    public String getSLocation() {
        return SLocation;
    }

    public void setSLocation(String SLocation) {
        this.SLocation = SLocation;
    }

    public void viewMyLocationOrders() {
        canteenOrder.viewOrdersByLocation(this.SLocation);
    }

    public boolean viewPendingOrders() {
        System.out.println("\n=== Pending Orders for " + SLocation + " ===");
        try (BufferedReader br = new BufferedReader(new FileReader("database/canteenOrders.txt"))) {
            String line;
            boolean hasOrders = false;

            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");

                if (parts.length < 8) {
                    continue;
                }

                if (parts[4].equals(SLocation) && parts[5].equals("ORDERED")) {
                    hasOrders = true;
                    System.out.println("Order ID: " + parts[0]);
                    System.out.println("Student ID: " + parts[1]);
                    System.out.println("Pickup Date: " + parts[3]);
                    System.out.println("Cost: " + parts[7]);
                    System.out.println("QR Code: " + parts[6]);
                    System.out.println("--------------------");
                }
            }
            if (!hasOrders) {
                System.out.println("No pending orders.");
            }
            return hasOrders;
        } catch (IOException e) {
            System.out.println("Error reading orders: " + e.getMessage());
            return false;
        }
    }

    public boolean deliverOrder(String orderId, String qrCode) {
        return canteenOrder.updateOrderStatus(orderId, "DELIVERED", qrCode, this.SLocation);
    }

    public void viewDailyRevenue(String date) {
        double totalRevenue = 0;
        int orderCount = 0;

        try (BufferedReader br = new BufferedReader(new FileReader("database/canteenOrders.txt"))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");

                if (parts.length < 8) {
                    continue;
                }

                if (parts[4].equals(SLocation) && parts[3].equals(date) && parts[5].equals("DELIVERED")) {
                    totalRevenue += Double.parseDouble(parts[7]);
                    orderCount++;
                }
            }

            System.out.println("\n=== Revenue Report for " + date + " ===");
            System.out.println("Serving Location: " + SLocation);
            System.out.println("Orders Delivered: " + orderCount);
            System.out.println("Total Revenue: " + String.format("%.2f", totalRevenue));

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void viewOrderStats() {
        int ordered = 0;
        int delivered = 0;
        double totalRevenue = 0;

        try (BufferedReader br = new BufferedReader(new FileReader("database/canteenOrders.txt"))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");

                if (parts.length < 8) {
                    continue;
                }

                if (parts[4].equals(SLocation)) {
                    if (parts[5].equals("ORDERED")) {
                        ordered++;
                    } else if (parts[5].equals("DELIVERED")) {
                        delivered++;
                        totalRevenue += Double.parseDouble(parts[7]);
                    }
                }
            }

            System.out.println("\n=== Order Statistics for " + SLocation + " ===");
            System.out.println("Pending Orders: " + ordered);
            System.out.println("Delivered Orders: " + delivered);
            System.out.println("Total Revenue (Delivered): " + String.format("%.2f", totalRevenue));
            System.out.println("Total Orders: " + (ordered + delivered));

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    @Override
    public String toString() {
        return "Vendor[ID=" + getId() + ", Name=" + getName() + ", Serving Location=" + SLocation + "]";
    }
}
