import java.io.*;
import java.util.*;

public class main {
    public static void register() {
        Scanner sc = new Scanner(System.in);
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
            System.out.println("User has been registered successfully");
        } catch (IOException e) {
            System.out.println("Error occured while writing to file" + e.getMessage());
        }
        sc.close();
    }

    public static void login() {
        Scanner sc = new Scanner(System.in);
        System.out.print("ID: ");
        String enteredId = sc.nextLine();
        System.out.print("Password: ");
        String enteredPassword = sc.nextLine();
        boolean loginSuccess = false;

        try {
            BufferedReader br = new BufferedReader(new FileReader("userList.txt"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
                String id = parts[0];
                String name = parts[1];
                String password = parts[2];
                String role = parts[3];
                user u = new user(id, name, password, role);
                String roleFormatted = "";
                switch (role) {
                    case "stud":
                        roleFormatted = "Student";
                        break;
                    case "vend":
                        roleFormatted = "Vendor";
                        break;
                    case "adm":
                        roleFormatted = "Admin";
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
                }
            }
            if (!loginSuccess) {
                System.out.print("Login Unsuccessful. ID or Password incorrect");
            }
            br.close();
        } catch (IOException e) {
            System.out.println("Error occured while reading to file" + e.getMessage());
        }
        sc.close();
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        boolean choice2 = true;
        while (choice2) {
            System.out.println("Do you want to:\n1. Register\n2. Login ");
            int choice = sc.nextInt();
            switch (choice) {
                case 1:
                    register();
                    choice2 = false;
                    break;
                case 2:
                    login();
                    choice2 = false;
                    break;
                default:
                    System.out.println("Invalid input received, Do you want to ry again? (true/false)");
                    sc.nextLine();
                    choice2 = sc.nextBoolean();
            }
        }
        sc.close();
    }
}