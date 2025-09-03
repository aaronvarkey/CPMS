public class admin extends user {
    public admin(String id, String name, String password) {
        super(id, name, password, "adm");
    }

    public void setStudentBalance(String studentId, double amount) {
        student tempStudent = new student(studentId, "", "", 0);
        tempStudent.updateBalance(studentId, amount);
        System.out.println("Balance amount set to: " + amount);
    }
}
