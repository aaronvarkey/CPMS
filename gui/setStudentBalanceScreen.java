package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import backend.*;

public class setStudentBalanceScreen extends JFrame {
    private admin currentAdmin;
    private JList<String> studentList;
    private DefaultListModel<String> listModel;
    private ArrayList<StudentInfo> students;
    private JButton setBalanceButton;
    private JButton refreshButton;
    private JButton closeButton;
    private JTextArea studentDetailsArea;

    private class StudentInfo {
        String studentId;
        String name;
        String hostel;
        double balance;

        StudentInfo(String studentId, String name, String hostel, double balance) {
            this.studentId = studentId;
            this.name = name;
            this.hostel = hostel;
            this.balance = balance;
        }
    }

    public setStudentBalanceScreen(admin a) {
        this.currentAdmin = a;
        this.students = new ArrayList<>();

        setTitle("Set Student Balance");
        setSize(800, 600);
        setLocationRelativeTo(null);

        createComponents();
        loadStudents();
        setVisible(true);
    }

    private void createComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Select Student to Set Balance", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.setBorder(BorderFactory.createTitledBorder("Student List"));

        listModel = new DefaultListModel<>();
        studentList = new JList<>(listModel);
        studentList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        studentList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showStudentDetails();
            }
        });

        JScrollPane listScrollPane = new JScrollPane(studentList);
        listPanel.add(listScrollPane, BorderLayout.CENTER);

        JPanel detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Student Details"));

        studentDetailsArea = new JTextArea();
        studentDetailsArea.setEditable(false);
        studentDetailsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane detailsScrollPane = new JScrollPane(studentDetailsArea);
        detailsPanel.add(detailsScrollPane, BorderLayout.CENTER);

        splitPane.setLeftComponent(listPanel);
        splitPane.setRightComponent(detailsPanel);
        splitPane.setDividerLocation(350);

        mainPanel.add(splitPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        setBalanceButton = new JButton("Set Balance");
        setBalanceButton.setBackground(new Color(46, 125, 50));
        setBalanceButton.setForeground(Color.WHITE);
        setBalanceButton.setFont(new Font("Arial", Font.BOLD, 14));
        setBalanceButton.setPreferredSize(new Dimension(140, 35));
        setBalanceButton.addActionListener(e -> setBalance());
        buttonPanel.add(setBalanceButton);

        refreshButton = new JButton("Refresh List");
        refreshButton.setPreferredSize(new Dimension(140, 35));
        refreshButton.addActionListener(e -> {
            loadStudents();
            JOptionPane.showMessageDialog(this, "Student list refreshed!");
        });
        buttonPanel.add(refreshButton);

        closeButton = new JButton("Close");
        closeButton.setPreferredSize(new Dimension(140, 35));
        closeButton.addActionListener(e -> dispose());
        buttonPanel.add(closeButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void loadStudents() {
        listModel.clear();
        students.clear();
        studentDetailsArea.setText("");

        try {
            BufferedReader br = new BufferedReader(new FileReader("database/userList.txt"));
            String line;

            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");

                if (parts.length >= 5 && parts[3].equals("stud")) {
                    String studentId = parts[0];
                    String name = parts[1];
                    String hostel = parts[4];

                    double balance = student.loadBalance(studentId);

                    StudentInfo studentInfo = new StudentInfo(studentId, name, hostel, balance);
                    students.add(studentInfo);

                    String displayText = studentId + " - " + name + " - Rs." + balance;
                    listModel.addElement(displayText);
                }
            }
            br.close();

            if (listModel.isEmpty()) {
                listModel.addElement("No students found");
                setBalanceButton.setEnabled(false);
            } else {
                setBalanceButton.setEnabled(true);
            }

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading students: " + e.getMessage());
        }
    }

    private void showStudentDetails() {
        int selectedIndex = studentList.getSelectedIndex();
        if (selectedIndex == -1 || students.isEmpty() || selectedIndex >= students.size()) {
            studentDetailsArea.setText("Select a student to view details");
            return;
        }

        StudentInfo student = students.get(selectedIndex);

        StringBuilder details = new StringBuilder();
        details.append("╔════════════════════════════════════════╗\n");
        details.append("         STUDENT INFORMATION\n");
        details.append("╚════════════════════════════════════════╝\n\n");
        details.append("Student ID:     ").append(student.studentId).append("\n");
        details.append("Name:           ").append(student.name).append("\n");
        details.append("Hostel:         ").append(student.hostel).append("\n");
        details.append("Current Balance: Rs. ").append(student.balance).append("\n\n");
        details.append("════════════════════════════════════════\n");
        details.append("Click 'Set Balance' to update balance");

        studentDetailsArea.setText(details.toString());
    }

    private void setBalance() {
        int selectedIndex = studentList.getSelectedIndex();

        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(this, "Please select a student!");
            return;
        }

        if (students.isEmpty() || selectedIndex >= students.size()) {
            JOptionPane.showMessageDialog(this, "No student selected!");
            return;
        }

        StudentInfo selectedStudent = students.get(selectedIndex);

        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        inputPanel.add(new JLabel("Student ID:"));
        JTextField idField = new JTextField(selectedStudent.studentId);
        idField.setEditable(false);
        inputPanel.add(idField);

        inputPanel.add(new JLabel("Current Balance:"));
        JLabel currentBalanceLabel = new JLabel("Rs. " + selectedStudent.balance);
        currentBalanceLabel.setForeground(Color.BLUE);
        inputPanel.add(currentBalanceLabel);

        inputPanel.add(new JLabel("New Balance:"));
        JTextField newBalanceField = new JTextField();
        inputPanel.add(newBalanceField);

        int result = JOptionPane.showConfirmDialog(this, inputPanel,
                "Set Balance for " + selectedStudent.name, JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String balanceStr = newBalanceField.getText().trim();

            if (balanceStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a balance amount!");
                return;
            }

            try {
                double newBalance = Double.parseDouble(balanceStr);

                if (newBalance < 0) {
                    JOptionPane.showMessageDialog(this, "Balance cannot be negative!");
                    return;
                }

                currentAdmin.setStudentBalance(selectedStudent.studentId, newBalance);

                JOptionPane.showMessageDialog(this,
                        "Balance updated successfully!\n" +
                                "Student: " + selectedStudent.name + "\n" +
                                "Old Balance: Rs. " + selectedStudent.balance + "\n" +
                                "New Balance: Rs. " + newBalance,
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

                loadStudents();

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid balance! Please enter a valid number.");
            }
        }
    }
}