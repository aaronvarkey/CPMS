package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import backend.*;

public class studentScreen extends JFrame {
    private student currentStudent;
    private JLabel welcomeLabel;
    private JButton viewBalanceButton;
    private JButton placeOrderButton;
    private JButton viewOrdersButton;
    private JButton cancelOrderButton;
    private JButton exitButton;

    public studentScreen(student s) {
        this.currentStudent = s;

        setTitle("Student Dashboard");
        setSize(500, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        createComponents();
        setVisible(true);
    }

    private void createComponents() {
        JPanel panel = new JPanel();
        panel.setLayout(null);

        welcomeLabel = new JLabel("Welcome, " + currentStudent.getName());
        welcomeLabel.setBounds(20, 20, 300, 30);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(welcomeLabel);

        viewBalanceButton = new JButton("View Balance");
        viewBalanceButton.setBounds(150, 80, 200, 40);
        viewBalanceButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                viewBalance();
            }
        });
        panel.add(viewBalanceButton);

        placeOrderButton = new JButton("Place Order");
        placeOrderButton.setBounds(150, 130, 200, 40);
        placeOrderButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new placeOrderScreen(currentStudent, studentScreen.this);
            }
        });
        panel.add(placeOrderButton);

        viewOrdersButton = new JButton("View My Orders");
        viewOrdersButton.setBounds(150, 180, 200, 40);
        viewOrdersButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new viewOrdersScreen(currentStudent.getId());
            }
        });
        panel.add(viewOrdersButton);

        cancelOrderButton = new JButton("Cancel Order");
        cancelOrderButton.setBounds(150, 230, 200, 40);
        cancelOrderButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new cancelOrderScreen(currentStudent, studentScreen.this);
            }
        });
        panel.add(cancelOrderButton);

        exitButton = new JButton("Exit");
        exitButton.setBounds(150, 280, 200, 40);
        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int confirm = JOptionPane.showConfirmDialog(studentScreen.this,
                        "Are you sure you want to exit?",
                        "Confirm Exit",
                        JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    dispose();
                    new loginScreen();
                }
            }
        });
        panel.add(exitButton);

        add(panel);
    }

    private void viewBalance() {
        refreshBalance();
        JOptionPane.showMessageDialog(this,
                "Current Balance: Rs. " + currentStudent.getBalance(),
                "Balance Information",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public void refreshBalance() {
        double newBalance = student.loadBalance(currentStudent.getId());
        currentStudent.setBalance(newBalance);
    }
}