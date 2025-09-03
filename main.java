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
                        // u = new vendor(id, name, password); //Feature to be implemented.
                        break;
                    case "adm":
                        roleFormatted = "Admin";
                        // u = new admin(id, name, password); //Feature to be implemented.
                        break;
                }

                if (enteredId.equals(u.getId()) && u.checkPassword(enteredPassword)) {
                    System.out.println("=====Login Successful=====");
                    System.out.println("--------------------------");
                    System.out.println("User: " + name);
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
            System.out.println("\n=====Student Menu=====");
            System.out.println("\n1. View Balance\n2. Exit");
            System.out.println("Choose an option: ");
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    System.out.println("Current Balance: " + s.getBalance());
                    break;
                case 2:
                    studentChoice = false;
                    System.out.println("Exited.");
                    break;
                default:
                    System.out.println("Invalid choice. Try Again.");
            }
        }
    }

    public static void vendorMenu(vendor v, Scanner sc) {
        System.out.println("Vendor menu coming soon.....");
    }

    public static void adminMenu(admin a, Scanner sc) {
        System.out.println("Admin menu coming soon.....");
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
                        System.out.println("Welcome, " + u.getName() + "! Role = " + u.getRole());
                        if (u.getRole().equals("stud")) {
                            studentMenu((student) u, sc);
                        } else if (u.getRole().equals("vend")) {
                            // vendorMenu((vendor) u, sc); //Feature to be implemented.
                        } else if (u.getRole().equals("vend")) {
                            // adminMenu((admin) u, sc); //Feature to be implemented.
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