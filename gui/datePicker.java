package gui;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class datePicker extends JDialog {
    private JComboBox<Integer> yearBox;
    private JComboBox<String> monthBox;
    private JComboBox<Integer> dayBox;
    private JButton okButton;
    private JButton cancelButton;
    private String selectedDate = null;

    public datePicker(JFrame parent, String title) {
        super(parent, title, true);
        setSize(450, 350);
        setLocationRelativeTo(parent);
        setResizable(false);

        createComponents();
    }

    private void createComponents() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(null);
        mainPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Select Date:", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBounds(0, 20, 450, 30);
        mainPanel.add(titleLabel);

        JLabel yearLabel = new JLabel("Year:");
        yearLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        yearLabel.setBounds(80, 80, 60, 30);
        mainPanel.add(yearLabel);

        Integer[] years = new Integer[10];
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 0; i < 10; i++) {
            years[i] = currentYear + i;
        }
        yearBox = new JComboBox<>(years);
        yearBox.setFont(new Font("Arial", Font.PLAIN, 14));
        yearBox.setBounds(150, 80, 220, 35);
        yearBox.setMaximumRowCount(10);
        yearBox.addActionListener(e -> updateDays());
        mainPanel.add(yearBox);

        JLabel monthLabel = new JLabel("Month:");
        monthLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        monthLabel.setBounds(80, 135, 60, 30);
        mainPanel.add(monthLabel);

        String[] months = { "January", "February", "March", "April",
                "May", "June", "July", "August",
                "September", "October", "November", "December" };
        monthBox = new JComboBox<>(months);
        monthBox.setFont(new Font("Arial", Font.PLAIN, 14));
        monthBox.setBounds(150, 135, 220, 35);
        monthBox.setMaximumRowCount(12);
        monthBox.addActionListener(e -> updateDays());
        mainPanel.add(monthBox);

        JLabel dayLabel = new JLabel("Day:");
        dayLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        dayLabel.setBounds(80, 190, 60, 30);
        mainPanel.add(dayLabel);

        dayBox = new JComboBox<>();
        dayBox.setFont(new Font("Arial", Font.PLAIN, 14));
        dayBox.setBounds(150, 190, 220, 35);
        dayBox.setMaximumRowCount(20);
        mainPanel.add(dayBox);

        updateDays();

        okButton = new JButton("OK");
        okButton.setFont(new Font("Arial", Font.BOLD, 14));
        okButton.setBounds(110, 260, 100, 40);
        okButton.setBackground(new Color(46, 125, 50));
        okButton.setForeground(Color.WHITE);
        okButton.setFocusPainted(false);
        okButton.addActionListener(e -> {
            int year = (Integer) yearBox.getSelectedItem();
            int month = monthBox.getSelectedIndex() + 1;
            int day = (Integer) dayBox.getSelectedItem();

            selectedDate = String.format("%d-%02d-%02d", year, month, day);
            dispose();
        });
        mainPanel.add(okButton);

        cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("Arial", Font.BOLD, 14));
        cancelButton.setBounds(240, 260, 100, 40);
        cancelButton.setBackground(new Color(211, 47, 47));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);
        cancelButton.addActionListener(e -> {
            selectedDate = null;
            dispose();
        });
        mainPanel.add(cancelButton);

        add(mainPanel);
    }

    private void updateDays() {
        dayBox.removeAllItems();

        int year = (Integer) yearBox.getSelectedItem();
        int month = monthBox.getSelectedIndex() + 1;

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1);
        int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int i = 1; i <= daysInMonth; i++) {
            dayBox.addItem(i);
        }
    }

    public String getSelectedDate() {
        return selectedDate;
    }

    public static String showDatePicker(JFrame parent, String title) {
        datePicker picker = new datePicker(parent, title);
        picker.setVisible(true);
        return picker.getSelectedDate();
    }
}