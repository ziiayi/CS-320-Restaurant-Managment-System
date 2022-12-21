package UI;
import database.Employee;
import database.User;
import database.utils.Connect;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class AddEmployeeUI implements ActionListener{
    public static int yCoordinate = 60;
    public static JPanel employeePanel;
    public static JLabel nameLabel, roleLabel, workingHourLabel, contractLabel, salaryLabel;
    public static JTextField inputName, inputRole, inputWorkingHour, inputContract, inputSalary;
    public static JButton submitButton;
    public static void generateEmployeeUI() {
        employeePanel = new JPanel();
        employeePanel.setLayout(null);

        JFrame addEmployeeFrame = new JFrame();
        setFrameProperties(addEmployeeFrame);
        addEmployeeFrame.add(employeePanel);

        String[] role = {"Chef","Waiter"};
        String[] contact = {"Monthly", "Hourly"};

        nameLabel = new JLabel("Employee Name:");
        inputName = new JTextField();
        roleLabel = new JLabel(" Employee Role:");
        inputRole = new JTextField();
        workingHourLabel = new JLabel("Working Hours:");
        inputWorkingHour = new JTextField();
        contractLabel = new JLabel("Contract Type:");
        inputContract = new JTextField();
        salaryLabel = new JLabel("Salary:");
        inputSalary = new JTextField();

        inputSalary.setText("$100");

        addComponentToPanel(nameLabel, inputName);
        addDropDown(roleLabel, role);
        addComponentToPanel(workingHourLabel, inputWorkingHour);
        addDropDown(contractLabel, contact);
        addComponentToPanel(salaryLabel, inputSalary);


        submitButton = new JButton("Submit");
        submitButton.setBounds(300, 350, 90, 25);
        btnProperties(submitButton);
        employeePanel.add(submitButton);

        addEmployeeFrame.setVisible(true);
    }

    public static void addComponentToPanel(JLabel label, JTextField textField){
        label.setBounds(150, yCoordinate, 150, 20);
        employeePanel.add(label);
        textField.setBounds(250, yCoordinate, 193, 28);
        employeePanel.add(textField);
        yCoordinate+=60;
    }

    public static void addDropDown(JLabel label, String[] list){
        label.setBounds(150, yCoordinate, 150, 20);
        employeePanel.add(label);
        JComboBox<String> roleList = new JComboBox<>(list);
        roleList.setBounds(250, yCoordinate, 193, 28);
        employeePanel.add(roleList);
        yCoordinate+=60;
    }

    private static void btnProperties(JButton button) {
        button.setForeground(Color.WHITE);
        button.setBackground(Color.BLACK);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.addActionListener((ActionListener) new AddEmployeeUI());
    }

    private static void setFrameProperties(JFrame frame){
        frame.setTitle("Add New Employee!");
        frame.setSize(700, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Connect connect = new Connect();
        User user = new User(connect.connection);
        Employee employee = new Employee(connect.connection);
        if (e.getSource() == submitButton) {
            user.setUsername(inputName.getText());
            user.setPassword(inputName.getText());
            try {
                String salary = inputSalary.getText().substring(1);
                employee.setSalary(Long.parseLong(salary));

                //employee.setSalaryType(Long.parseLong(inputContract.getText()));
            }catch(NumberFormatException nfe){
                JOptionPane.showMessageDialog(null,nfe.getMessage());
            }
            try {
                User newUser = user.createUser();
                employee.setUserId(newUser.getId());
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
            try {
                employee.createEmployee();
                JOptionPane.showMessageDialog(null, "Employee added successfully!");
            }catch(SQLException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        }
    }
}