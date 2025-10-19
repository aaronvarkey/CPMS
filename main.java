import java.io.*;
import java.util.*;
import backend.*;

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

        String hostel = "";
        boolean a = true;
        while (a) {
            if (role.equals("stud")) {
                System.out.println("Select Hostel: ");
                System.out.println("1. Boys Hostel");
                System.out.println("2. Girls Hostel");
                System.out.print("Choose: ");
                int hostelChoice = sc.nextInt();
                sc.nextLine();

                if (hostelChoice == 1) {
                    hostel = "Boys Hostel";
                    a = false;
                } else if (hostelChoice == 2) {
                    hostel = "Girls Hostel";
                    a = false;
                } else {
                    System.out.println("Invalid Choice, Please Try Again");
                }
            } else if (role.equals("vend")) {
                System.out.println("Select Serving Location: ");
                System.out.println("1. Boys Hostel");
                System.out.println("2. Girls Hostel");
                System.out.println("3. Canteen");
                System.out.print("Choose: ");
                int locationChoice = sc.nextInt();
                sc.nextLine();

                if (locationChoice == 1) {
                    hostel = "Boys Hostel";
                    a = false;
                } else if (locationChoice == 2) {
                    hostel = "Girls Hostel";
                    a = false;
                } else if (locationChoice == 3) {
                    hostel = "Canteen";
                    a = false;
                } else {
                    System.out.println("Invalid Choice, Please Try Again");
                }
            } else {
                a = false;
            }
        }
        String userData = id + "|" + name + "|" + password + "|" + role + "|" + hostel;
        try {
            FileWriter fw = new FileWriter("database/userList.txt", true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(userData);
            bw.newLine();
            bw.close();

            if (role.equals("stud")) {
                BufferedWriter bf = new BufferedWriter(new FileWriter("database/hostellerAccounts.txt", true));
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
            BufferedReader br = new BufferedReader(new FileReader("database/userList.txt"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");

                if (parts.length < 4) {
                    continue;
                }

                String id = parts[0];
                String name = parts[1];
                String password = parts[2];
                String role = parts[3];
                String hostel = "";

                if (parts.length > 4) {
                    hostel = parts[4];
                }

                String roleFormatted = "";
                switch (role) {
                    case "stud":
                        roleFormatted = "Student";
                        double balance = student.loadBalance(id);
                        u = new student(id, name, password, balance, hostel);
                        break;
                    case "vend":
                        roleFormatted = "Vendor";
                        u = new vendor(id, name, password, hostel);
                        break;
                    case "adm":
                        roleFormatted = "Admin";
                        u = new admin(id, name, password);
                        break;
                    default:
                        continue;
                }

                if (enteredId.equals(u.getId()) && u.checkPassword(enteredPassword)) {
                    System.out.println("\n=====Login Successful=====");
                    System.out.println("\nUser: " + name);
                    System.out.println("ID: " + id);
                    System.out.println("Role: " + roleFormatted);
                    if (role.equals("stud")) {
                        System.out.println("Hostel: " + hostel);
                    } else if (role.equals("vend")) {
                        System.out.println("Serving Location: " + hostel);
                    }
                    System.out.println("--------------------------");
                    loginSuccess = true;
                    break;
                } else {
                    u = null;
                }
            }
            if (!loginSuccess) {
                System.out.println("Login Unsuccessful. ID or Password incorrect");
            }
            br.close();
        } catch (IOException e) {
            System.out.println("Error occured while reading file: " + e.getMessage());
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Error: Corrupted user data in file. Please check userList.txt");
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
            System.out.print("Choose an option: ");
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    System.out.println("Current Balance: " + s.getBalance());
                    break;
                case 2:
                    System.out.print("Enter pickup date (YYYY-MM-DD): ");
                    String pickupDate = sc.nextLine();

                    // Option-based location selection
                    System.out.println("Select location:");
                    System.out.println("1. My Hostel (" + s.getHostel() + ")");
                    System.out.println("2. Canteen");
                    System.out.print("Choose: ");
                    int locChoice = sc.nextInt();
                    sc.nextLine();

                    String finalLocation = "";
                    if (locChoice == 1) {
                        finalLocation = s.getHostel();
                    } else if (locChoice == 2) {
                        finalLocation = "Canteen";
                    } else {
                        System.out.println("Invalid location choice!");
                        break;
                    }

                    System.out.print("Enter order cost: ");
                    double orderCost = sc.nextDouble();
                    sc.nextLine();

                    if (orderCost <= 0) {
                        System.out.println("Invalid order cost!");
                        break;
                    }

                    try {
                        if (s.getBalance() >= orderCost) {
                            canteenOrder order = new canteenOrder(s.getId(), pickupDate, finalLocation, orderCost);
                            order.saveToFile();

                            double newBalance = s.getBalance() - orderCost;
                            s.updateBalance(s.getId(), newBalance);
                            s.setBalance(newBalance);

                            System.out.println("Order placed successfully!");
                            System.out.println("Order ID: " + order.getOrderId());
                            System.out.println("Pickup Location: " + finalLocation);
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
                    System.out.print("Enter Order ID to cancel: ");
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
            System.out.println("\n===== Vendor Menu (" + v.getSLocation() + ") =====");
            System.out.println("1. View All Orders");
            System.out.println("2. View Orders by Status");
            System.out.println("3. View Orders by Location");
            System.out.println("4. View Dine-in Summary");
            System.out.println("5. Mark Order as Delivered");
            System.out.println("6. View My Location Orders");
            System.out.println("7. View Pending Orders");
            System.out.println("8. View Order Statistics");
            System.out.println("9. View Daily Revenue");
            System.out.println("10. Exit");
            System.out.print("Choose an option: ");
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    canteenOrder.viewAllOrders();
                    break;
                case 2:
                    System.out.println("Select status:");
                    System.out.println("1. ORDERED");
                    System.out.println("2. DELIVERED");
                    System.out.print("Choose: ");
                    int s = sc.nextInt();
                    sc.nextLine();
                    String status = "";
                    if (s == 1)
                        status = "ORDERED";
                    else if (s == 2)
                        status = "DELIVERED";
                    else
                        System.out.println("Invalid Status Choice.");
                    if (!status.isEmpty())
                        canteenOrder.viewOrderByStatus(status);
                    break;
                case 3:
                    System.out.println("Select Locations: ");
                    System.out.println("1. Boys Hostel");
                    System.out.println("2. Girls Hostel");
                    System.out.println("3. Canteen");
                    System.out.print("Choose: ");
                    int loc = sc.nextInt();
                    sc.nextLine();
                    String location = "";
                    if (loc == 1)
                        location = "Boys Hostel";
                    else if (loc == 2)
                        location = "Girls Hostel";
                    else if (loc == 3)
                        location = "Canteen";
                    else
                        System.out.println("Invalid location choice");
                    if (!location.isEmpty())
                        canteenOrder.viewOrdersByLocation(location);
                    break;
                case 4:
                    canteenOrder.viewHostelOrdersSummary();
                    break;
                case 5: {
                    boolean hasPendingOrders = v.viewPendingOrders();

                    if (hasPendingOrders) {
                        System.out.print("\nEnter Order ID to mark as delivered: ");
                        String deliverOrderId = sc.nextLine();
                        System.out.print("Enter QR Code for verification: ");
                        String qrCode = sc.nextLine();

                        boolean success = v.deliverOrder(deliverOrderId, qrCode);

                        if (success) {
                            System.out.println("Order successfully delivered!");
                        } else {
                            System.out.println("Failed to deliver order. Please check details.");
                        }
                    } else {
                        System.out.println("\nNo orders to deliver. Returning to menu...");
                    }
                    break;
                }
                case 6:
                    v.viewMyLocationOrders();
                    break;
                case 7:
                    v.viewPendingOrders();
                    break;
                case 8:
                    v.viewOrderStats();
                    break;
                case 9: {
                    System.out.print("Enter date (YYYY-MM-DD): ");
                    String date = sc.nextLine();
                    v.viewDailyRevenue(date);
                    break;
                }
                case 10:
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
            System.out.println("4. View Orders by Status");
            System.out.println("5. View Orders by Location");
            System.out.println("6. View Location Summary");
            System.out.println("7. Exit");
            System.out.print("Choose an option: ");
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    System.out.print("Student ID to set balance for: ");
                    String setId = sc.nextLine();
                    System.out.print("Input balance amount to set: ");
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
                    System.out.println("Select status:");
                    System.out.println("1. ORDERED");
                    System.out.println("2. DELIVERED");
                    System.out.print("Choose: ");
                    int statusChoice = sc.nextInt();
                    sc.nextLine();
                    String filterStatus = "";
                    if (statusChoice == 1)
                        filterStatus = "ORDERED";
                    else if (statusChoice == 2)
                        filterStatus = "DELIVERED";
                    else
                        System.out.println("Invalid Status Choice.");
                    if (!filterStatus.isEmpty())
                        canteenOrder.viewOrderByStatus(filterStatus);
                    break;
                case 5:
                    System.out.println("Select Location: ");
                    System.out.println("1. Boys Hostel");
                    System.out.println("2. Girls Hostel");
                    System.out.println("3. Canteen");
                    System.out.print("Choose: ");
                    int locChoice = sc.nextInt();
                    sc.nextLine();
                    String location = "";
                    if (locChoice == 1)
                        location = "Boys Hostel";
                    else if (locChoice == 2)
                        location = "Girls Hostel";
                    else if (locChoice == 3)
                        location = "Canteen";
                    else
                        System.out.println("Invalid location choice");
                    if (!location.isEmpty())
                        canteenOrder.viewOrdersByLocation(location);
                    break;
                case 6:
                    canteenOrder.viewHostelOrdersSummary();
                    break;
                case 7:
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
            try {
                System.out.println("Do you want to:\n1. Register\n2. Login ");
                System.out.print("Choose an option: ");
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
                        System.out.println("Invalid input received, Do you want to try again? (true/false)");
                        choice2 = sc.nextBoolean();
                }
            } catch (Exception e) {
                System.out.println("ERROR: " + e.getMessage());
                e.printStackTrace();
                choice2 = false;
            }
        }
        sc.close();
    }
}