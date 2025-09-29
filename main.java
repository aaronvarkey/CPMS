import java.io.*;
import java.util.*;

public class main {
    public static void register(Scanner sc) {
        System.out.print("ID: ");
        String id = sc.nextLine();
        System.out.print("Name: ");
        String name = sc.nextLine();
        System.out.print("Password: ");
        String password = sc.nextLine();
        System.out.print("User Type (stud/vend/adm): ");
        String role = sc.nextLine();

        String userData = id + "|" + name + "|" + password + "|" + role;
        try {
            FileWriter fw = new FileWriter("userList.txt", true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(userData);
            bw.newLine();
            bw.close();

            if (role.equals("stud")) {
                BufferedWriter bf = new BufferedWriter(new FileWriter("hostellerAccounts.txt", true));
                bf.write(id + "|" + "0.0");
                bf.newLine();
                bf.close();
            }

            System.out.println("User has been registered successfully");
        } catch (IOException e) {
            System.out.println("Error occured while writing to file" + e.getMessage());
        }
    }

    public static user login(Scanner sc) {
        System.out.print("ID: ");
        String enteredId = sc.nextLine();
        System.out.print("Password: ");
        String enteredPassword = sc.nextLine();
        boolean loginSuccess = false;
        user u = null;
        try {
            BufferedReader br = new BufferedReader(new FileReader("userList.txt"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
                String id = parts[0];
                String name = parts[1];
                String password = parts[2];
                String role = parts[3];

                String roleFormatted = "";
                switch (role) {
                    case "stud":
                        roleFormatted = "Student";
                        double balance = student.loadBalance(id);
                        u = new student(id, name, password, balance);
                        break;
                    case "vend":
                        roleFormatted = "Vendor";
                        u = new vendor(id, name, password);
                        break;
                    case "adm":
                        roleFormatted = "Admin";
                        u = new admin(id, name, password);
                        break;
                }

                if (enteredId.equals(u.getId()) && u.checkPassword(enteredPassword)) {
                    System.out.println("\n=====Login Successful=====");
                    System.out.println("\nUser: " + name);
                    System.out.println("ID: " + id);
                    System.out.println("Role: " + roleFormatted);
                    System.out.println("--------------------------");
                    loginSuccess = true;
                    break;
                } else {
                    u = null;
                }
            }
            if (!loginSuccess) {
                System.out.print("Login Unsuccessful. ID or Password incorrect");
            }
            br.close();
        } catch (IOException e) {
            System.out.println("Error occured while reading to file" + e.getMessage());
        }
        return u;
    }

    public static void studentMenu(student s, Scanner sc) {
        boolean studentChoice = true;
        while (studentChoice) {
            System.out.println("\n===== Student Menu =====");
            System.out.println("1. View Balance");
            System.out.println("2. Place Order");
            System.out.println("3. View My Orders");
            System.out.println("4. Cancel Order");
            System.out.println("5. Exit");
            System.out.println("Choose an option: ");
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    System.out.println("Current Balance: " + s.getBalance());
                    break;
                case 2:
                    System.out.println("Enter pickup date (YYYY-MM-DD): ");
                    String pickupDate = sc.nextLine();
                    System.out.println("Enter location (Hostel/Canteen): ");
                    String location = sc.nextLine();
                    System.out.println("Enter order cost: ");
                    double orderCost = sc.nextDouble();
                    sc.nextLine();

                    try {
                        if (s.getBalance() >= orderCost) {
                            canteenOrder order = new canteenOrder(s.getId(), pickupDate, location, orderCost);
                            order.saveToFile();

                            double newBalance = s.getBalance() - orderCost;
                            s.updateBalance(s.getId(), newBalance);
                            s.setBalance(newBalance);

                            System.out.println("Order placed successfully!");
                            System.out.println("Order ID: " + order.getOrderId());
                            System.out.println("QR Code: " + order.getQrCode());
                            System.out.println("Remaining Balance: " + newBalance);
                        } else {
                            System.out.println(
                                    "Insufficient balance! Current: " + s.getBalance() + ", Required: " + orderCost);
                        }
                    } catch (IllegalArgumentException e) {
                        System.out.println("Order Failed: " + e.getMessage());
                    }
                    break;
                case 3:
                    canteenOrder.viewStudentOrders(s.getId());
                    break;
                case 4:
                    System.out.println("Enter Order ID to cancel: ");
                    String cancelOrderId = sc.nextLine();
                    canteenOrder.cancelStudentOrder(cancelOrderId, s.getId());

                    s.setBalance(student.loadBalance(s.getId()));
                    break;
                case 5:
                    studentChoice = false;
                    System.out.println("Exited.");
                    break;
                default:
                    System.out.println("Invalid choice. Try Again.");
            }
        }
    }

    public static void vendorMenu(vendor v, Scanner sc) {
        boolean vendorChoice = true;
        while (vendorChoice) {
            System.out.println("\n===== Vendor Menu =====");
            System.out.println("1. View All Orders");
            System.out.println("2. View Pending Orders");
            System.out.println("3. Mark Order as Completed");
            System.out.println("4. Exit");
            System.out.println("Choose an option: ");
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    canteenOrder.viewAllOrders();
                    break;
                case 2:
                    canteenOrder.viewOrderByStatus("ORDERED");
                    break;
                case 3:
                    System.out.println("Enter OrderId to be marked as completed: ");
                    String completeOrderId = sc.nextLine();
                    canteenOrder.updateOrderStatus(completeOrderId, "COMPLETED");
                    break;
                case 4:
                    vendorChoice = false;
                    System.out.println("Exited.");
                    break;
                default:
                    System.out.println("Invalid Choice. Try Again.");
            }
        }
    }

    public static void adminMenu(admin a, Scanner sc) {
        boolean adminChoice = true;
        while (adminChoice) {
            System.out.println("\n===== Admin Menu =====");
            System.out.println("1. Set Student Balance");
            System.out.println("2. View Student List");
            System.out.println("3. View All Orders");
            System.out.println("4. View Order by Status");
            System.out.println("5. Exit");
            System.out.println("Choose an option: ");
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    System.out.println("Student ID to set balance for: ");
                    String setId = sc.nextLine();
                    System.out.println("Input balance amount to set: ");
                    double amount = sc.nextDouble();
                    sc.nextLine();
                    a.setStudentBalance(setId, amount);
                    break;
                case 2:
                    a.viewStudentList();
                    break;
                case 3:
                    canteenOrder.viewAllOrders();
                    break;
                case 4:
                    System.out.print("Enter status to filter (ORDERED/DELIVERED): ");
                    String filterStatus = sc.nextLine();
                    canteenOrder.viewOrderByStatus(filterStatus);
                    break;
                case 5:
                    adminChoice = false;
                    System.out.println("Exited.");
                    break;
                default:
                    System.out.println("Invalid choice. Try Again.");
            }
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        boolean choice2 = true;
        while (choice2) {
            System.out.println("Do you want to:\n1. Register\n2. Login ");
            System.out.println("Choose an option: ");
            int choice = sc.nextInt();
            sc.nextLine();
            switch (choice) {
                case 1:
                    register(sc);
                    choice2 = false;
                    break;
                case 2:
                    user u = login(sc);
                    if (u != null) {
                        String roleFormatted = u.getRole();
                        switch (roleFormatted) {
                            case "stud":
                                roleFormatted = "Student";
                                break;
                            case "vend":
                                roleFormatted = "Vendor";
                                break;
                            case "adm":
                                roleFormatted = "Admin";
                                break;
                            default:
                                roleFormatted = "Invalid";
                                break;
                        }
                        System.out.println("Welcome " + roleFormatted + ",\n" + u.getName());
                        if (u.getRole().equals("stud")) {
                            studentMenu((student) u, sc);
                        } else if (u.getRole().equals("vend")) {
                            vendorMenu((vendor) u, sc);
                        } else if (u.getRole().equals("adm")) {
                            adminMenu((admin) u, sc);
                        }
                    }
                    choice2 = false;
                    break;
                default:
                    System.out.println("Invalid input received, Do you want to ry again? (true/false)");
                    choice2 = sc.nextBoolean();
            }
        }
        sc.close();
    }
}