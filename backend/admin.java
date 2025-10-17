package backend;

import java.io.*;

public class admin extends user {
    public admin(String id, String name, String password) {
        super(id, name, password, "adm");
    }

    public void setStudentBalance(String studentId, double amount) {
        boolean studentExists = false;

        try (BufferedReader br = new BufferedReader(new FileReader("database/userList.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 4 && parts[0].equals(studentId) && parts[3].equals("stud")) {
                    studentExists = true;
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading user list: " + e.getMessage());
            return;
        }

        if (studentExists) {
            student tempStudent = new student(studentId, "", "", 0, "");
            tempStudent.updateBalance(studentId, amount);
            System.out.println("Balance for student " + studentId + " set to: " + amount);
        } else {
            System.out.println("Error: Student with ID '" + studentId + "' not found!");
        }
    }

    public void viewStudentList() {
        try (BufferedReader br = new BufferedReader(new FileReader("database/userList.txt"))) {
            String line;
            boolean hasStudents = false;

            System.out.println("\n========== STUDENT LIST ==========");
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 4 && parts[3].equals("stud")) {
                    hasStudents = true;
                    double balance = student.loadBalance(parts[0]);
                    System.out.println("Student ID: " + parts[0]);
                    System.out.println("Name: " + parts[1]);
                    System.out.println("Hostel: " + (parts.length > 4 ? parts[4] : "N/A"));
                    System.out.println("Balance: " + balance);
                    System.out.println("------------------------");
                }
            }

            if (!hasStudents) {
                System.out.println("No students found.");
            }
            System.out.println("==================================");

        } catch (IOException e) {
            System.out.println("Error displaying Student List: " + e.getMessage());
        }
    }
}
