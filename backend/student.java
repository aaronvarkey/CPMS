package backend;

import java.io.*;

public class student extends user {
    private double balance;
    private String hostel;

    public student(String id, String name, String password, double balance, String hostel) {
        super(id, name, password, "stud");
        this.balance = balance;
        this.hostel = hostel;
    }

    public String getHostel() {
        return hostel;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public static double loadBalance(String studentId) {
        try (BufferedReader br = new BufferedReader(new FileReader("database/hostellerAccounts.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts[0].equals(studentId)) {
                    return Double.parseDouble(parts[1]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public void updateBalance(String studentId, double newBalance) {
        File inputFile = new File("database/hostellerAccounts.txt");
        File tempFile = new File("database/tempBalance.txt");

        try (BufferedReader br = new BufferedReader(new FileReader(inputFile));
                BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile))) {
            String line;
            boolean updated = false;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts[0].equals(studentId)) {
                    bw.write(studentId + "|" + newBalance);
                    updated = true;
                } else {
                    bw.write(line);
                }
                bw.newLine();
            }
            if (!updated) {
                bw.write(studentId + "|" + newBalance);
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error updating balance: " + e.getMessage());
            return;
        }
        inputFile.delete();
        tempFile.renameTo(inputFile);
    }
}