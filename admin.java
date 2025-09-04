import java.io.*;

public class admin extends user {
    public admin(String id, String name, String password) {
        super(id, name, password, "adm");
    }

    public void setStudentBalance(String studentId, double amount) {
        student tempStudent = new student(studentId, "", "", 0);
        tempStudent.updateBalance(studentId, amount);
        System.out.println("Balance amount set to: " + amount);
    }

    public void viewStudentList() {
        try (BufferedReader br = new BufferedReader(new FileReader("userList.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts[3].equals("stud")) {
                    double balance = student.loadBalance(parts[0]);
                    System.out.println("Student ID: " + parts[0]);
                    System.out.println("Name: " + parts[1]);
                    System.out.println("Balance: " + balance);
                    System.out.println("------------------------");
                }
            }
        } catch (IOException e) {
            System.out.println("Error displaying Student List: " + e.getMessage());
        }
    }
}
