package UI;
import database.DishType;
import database.Menu;
import database.utils.Connect;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class CustomerMenuUI implements ActionListener {
    public static Connect connect = new Connect();
    public static Menu menu = new Menu(connect.connection);
    public static int yCoordinate = 50;
    public static JFrame customerFrame;
    public static JPanel menuPanel, cartPanel, navBar;
    public static JLabel menuLabel, productLabel, quantityLabel, priceLabel, priceCalculatedLabel;
    public static JComboBox<String> menuDropdown;
    public static JComboBox<String> productDropdown;
    public static JComboBox<String> quantityDropdown;
    public static JTable table;
    public static JButton addButton, deleteButton, checkoutButton, menuButton, transactionButton;
    public static String[] options = {"Yes", "No"}, productTypes;

    public static DefaultTableModel tableModel;

    public static void generateCustomerUI() {
        customerFrame = new JFrame();
        setFrameProperties();

        menuPanel = new JPanel();
        cartPanel = new JPanel();
        navBar = new JPanel();

        menuPanel.setLayout(null);
        cartPanel.setLayout(null);
        navBar.setLayout(new GridLayout(1,2));

        menuButton = new JButton("Menu");
        menuButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        transactionButton=new JButton("Transaction History");
        transactionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JMenu menu, TH;
                JFrame frame = new JFrame("Menu and MenuItem Example");

                TransactionHistUI transactionHistUI = new TransactionHistUI();;
                frame.add(transactionHistUI.getUIComponent());

                JMenuBar mb = new JMenuBar();
                menu = new JMenu("Menu");
                TH = new JMenu("Transaction History");

                mb.add(menu);
                mb.add(TH);
                frame.setJMenuBar(mb);

                frame.setSize(800,800);
                frame.setVisible(true);
            }
        });


        navBar.add(menuButton);
        navBar.add(transactionButton );


        menuPanel.setBorder(BorderFactory.createTitledBorder("Menu"));
        cartPanel.setBorder(BorderFactory.createTitledBorder("Cart"));


        cartPanel.setBounds(0, 370, 700, 500);
        menuPanel.setBounds(0, 20, 700, 350);
        navBar.setBounds(0,0,700,20);

        String[] menuTypes = {"Appetizer", "Main Dish", "Dessert", "Drinks"};
        String[] quantity = {"1", "2", "3", "4", "5", "6", "7"};
        productTypes = new String[]{""};

        menuLabel = new JLabel("Select Menu Type:");
        menuProperties(menuLabel);
        productLabel = new JLabel("Select Product:");
        menuProperties(productLabel);
        quantityLabel = new JLabel("Select Quantity:");
        menuProperties(quantityLabel);
        priceLabel = new JLabel("Calculated Price:");
        priceCalculatedLabel = new JLabel("$100");

        menuDropdown = new JComboBox<>(menuTypes);
        menuPanel.add(menuDropdown);
        productDropdown = new JComboBox<>();
        menuPanel.add(productDropdown);
        quantityDropdown = new JComboBox<>(quantity);
        menuPanel.add(quantityDropdown);
        addComponent(menuDropdown, productDropdown);
        addComponent2(productDropdown, quantityDropdown);
        quantityDropdown.setBounds(400, yCoordinate, 150, 20);
        addTable();

        checkoutButton = new JButton("Checkout");
        btnProperties(checkoutButton);
        checkoutButton.setBounds(260, 280, 150, 25);
        checkoutButton.setForeground(Color.WHITE);
        checkoutButton.setBackground(Color.BLACK);
        cartPanel.add(checkoutButton);

        addButton = new JButton("Add to Cart");
        btnProperties(addButton);
        addButton.setBounds(410, 300, 120, 25);
        menuPanel.add(addButton);

        deleteButton = new JButton("Delete");
        btnProperties(deleteButton);
        deleteButton.setBounds(550, 240, 100, 25);
        btnProperties(deleteButton);
        cartPanel.add(deleteButton);

        customerFrame.add(navBar);
        customerFrame.add(menuPanel);
        customerFrame.add(cartPanel);
        customerFrame.setVisible(true);
    }



    private static void menuProperties(JLabel label) {
        menuPanel.add(label);
        label.setBounds(50, yCoordinate, 150, 20);
    }


    private static void addTable() {
        String[][] data = {
                {"", "", "", ""},
                {"", "", "", ""},
                {"", "", "", ""},
                {"", "", "", ""},
                {"", "", "", ""},
                {"", "", "", ""},
                {"", "", "", ""},
                {"", "", "", ""},
                {"", "", "", ""},
                {"", "", "", ""},
                {"", "", "", ""},
                {"", "", "", ""},
        };
        // Column Names
        String[] columnNames = {"Menu", "Product", "Quantity", "Price"};
        tableModel = new DefaultTableModel(data, columnNames);
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); //prevent a user from selecting multiple rows
        JScrollPane sp = new JScrollPane(table);
        sp.setBounds(10, 20, 660, 200);
        cartPanel.add(sp);
    }

    private static void addComponent(JComboBox<String> watchingDropdown,JComboBox<String> changeDropdown) {
        DishType dishType = new DishType();
        watchingDropdown.setBounds(400, yCoordinate, 150, 20);
        watchingDropdown.addActionListener(e -> {
            changeDropdown.removeAllItems();
            if (Objects.equals(menuDropdown.getSelectedItem(), "Appetizer")){
                int dishTypeNum = dishType.getDishType("appetizer");
                addItemsList(changeDropdown, dishTypeNum);
            }
            if(Objects.equals(watchingDropdown.getSelectedItem(), "Main Dish")){
                int dishTypeNum = dishType.getDishType("main dish");
                addItemsList(changeDropdown, dishTypeNum);
            }
            if(Objects.equals(menuDropdown.getSelectedItem(), "Dessert")){
                int dishTypeNum = dishType.getDishType("dessert");
                addItemsList(changeDropdown, dishTypeNum);
            }
            if(Objects.equals(watchingDropdown.getSelectedItem(), "Drinks")){
                int dishTypeNum = dishType.getDishType("drinks");
                addItemsList(changeDropdown, dishTypeNum);
            }
        });
        yCoordinate += 60;
    }

    private static void addComponent2(JComboBox<String> watchingDropdown,JComboBox<String> changeDropdown) {
        watchingDropdown.setBounds(400, yCoordinate, 150, 20);
        yCoordinate += 60;
        watchingDropdown.addActionListener(e -> {
            changeDropdown.removeAllItems();
            System.out.println(productDropdown.getSelectedItem());
            try {
                Menu selectedMenu = menu.getMenuByName((String) productDropdown.getSelectedItem());
                for (int i = 0; i < selectedMenu.getQuantity(); i++) {
                    changeDropdown.addItem(String.valueOf(i+1));
                }
            } catch (SQLException | ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    private static void addItemsList(JComboBox<String> menuDropdown, int dishTypeNum) {
        List<List<String>> res = menu.getMenuItemsByDishType(dishTypeNum);
        for(List<String> row : res){
            menuDropdown.addItem(row.get(1));
        }
        System.out.println(res);
    }

    private static void addLabelToPanel(JLabel label, JLabel priceCalculatedLabel) {
        label.setBounds(50, yCoordinate, 150, 20);
        priceCalculatedLabel.setBounds(400, yCoordinate, 150, 20);
        menuPanel.add(label);
        menuPanel.add(priceCalculatedLabel);
        yCoordinate += 60;
    }

    private static void setFrameProperties() {
        customerFrame.setLayout(null);
        customerFrame.setTitle("Welcome");
        customerFrame.setResizable(false);
        customerFrame.setSize(700, 750);
        customerFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private static void btnProperties(JButton button) {
        button.setBorderPainted(true);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.addActionListener((ActionListener) new CustomerMenuUI());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == deleteButton && table.getSelectedRow() != -1) {
            tableModel.removeRow(table.getSelectedRow());
            JOptionPane.showMessageDialog(null, "Selected product removed from cart");
        }
        if (e.getSource() == addButton){
            JOptionPane.showMessageDialog(null, "Product added to cart!");
        }
        if (e.getSource() == checkoutButton){
            JOptionPane.showOptionDialog(null,
                    "Do you want a receipt?",
                    "Checkout Success",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]);
        }
    }
}