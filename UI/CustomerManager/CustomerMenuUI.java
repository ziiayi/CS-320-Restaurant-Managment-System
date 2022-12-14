package UI.CustomerManager;
import UI.NavBar.CustomerNavBar;
import database.DishType;
import database.Menu;
import database.OrderDetails;
import database.TransactionHistory;
import database.utils.Connect;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

import static UI.NavBar.CustomerNavBar.customerMenuFrame;

public class CustomerMenuUI{
    public static Connect connect = new Connect();
    public static Menu menu = new Menu(connect.connection);
    public static TransactionHistory TH = new TransactionHistory(connect.connection);
    public static OrderDetails OD = new OrderDetails(connect.connection);
    public int yCoordinate = 50;
    public static final int doesNotExist = -1;
    public static JPanel customerMainPanel;
    public static JPanel menuPanel, cartPanel;
    public static JLabel menuLabel, productLabel, quantityLabel, priceLabel, priceCalculatedLabel;
    public static JComboBox<String> menuDropdown;
    public static JComboBox<String> productDropdown;
    public static JComboBox<String> quantityDropdown;
    public static JTable table;
    public static JButton addButton, deleteButton, checkoutButton, transactionButton;

    public static List<List<String>> data = new ArrayList<>();
    public static String[] options = {"No", "Yes"}, productTypes;

    public static DefaultTableModel tableModel;
    public static final int userWantsReceipt = 1;
    // Column Names
    static String[] columnNames = {"Menu Type", "Product", "Quantity", "Price"};
    long userId;
    long transId;
    public CustomerMenuUI(long userId) {
        this.userId = userId;
    }
    public JPanel generateCustomerUI() {
        customerMainPanel = new JPanel();
        setFrameProperties();

        menuPanel = new JPanel();
        cartPanel = new JPanel();

        menuPanel.setLayout(null);
        cartPanel.setLayout(null);
        transactionButton = new JButton("Transaction History");
        menuPanel.setBorder(BorderFactory.createTitledBorder("Menu"));
        cartPanel.setBorder(BorderFactory.createTitledBorder("Cart"));
        cartPanel.setBounds(0, 370, 700, 500);
        menuPanel.setBounds(0, 20, 700, 350);

        String[] menuTypes = {"Appetizer", "Main Dish", "Dessert", "Drinks"};
        productTypes = new String[]{""};

        menuLabel = new JLabel("Select Menu Type:");
        menuLabelProperties(menuLabel);
        productLabel = new JLabel("Select Product:");
        menuLabelProperties(productLabel);
        quantityLabel = new JLabel("Select Quantity:");
        menuLabelProperties(quantityLabel);
        priceLabel = new JLabel("Calculated Price:");
        menuLabelProperties(priceLabel);
        priceCalculatedLabel = new JLabel("0 TL");
        menuPanel.add(priceCalculatedLabel);
        priceCalculatedLabel.setBounds(400, yCoordinate - 60, 150, 20);
        yCoordinate = 60;

        menuDropdown = new JComboBox<>(menuTypes);
        menuPanel.add(menuDropdown);
        productDropdown = new JComboBox<>();
        menuPanel.add(productDropdown);
        quantityDropdown = new JComboBox<>();
        menuPanel.add(quantityDropdown);
        updateProductDropdown(menuDropdown, productDropdown);
        menuDropdown.setBounds(400, yCoordinate, 150, 20);
        yCoordinate += 60;
        updateQuantityDropdown(productDropdown, quantityDropdown);
        productDropdown.setBounds(400, yCoordinate, 150, 20);
        yCoordinate += 60;
        updatePriceCalculatedLabel(quantityDropdown,priceCalculatedLabel);
        quantityDropdown.setBounds(400, yCoordinate, 150, 20);
        addTable();// adding table comp

        checkoutButton = new JButton("Checkout");
        checkoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("this is from checkout menu ui =>" + userId);

                int response = JOptionPane.showOptionDialog(null,
                        "Do you want a receipt?",
                        "Checkout Success",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        options[0]);

                if (response == userWantsReceipt) {
                    JPanel datePanel = new JPanel();
                    datePanel.setLayout(new BoxLayout(datePanel, BoxLayout.PAGE_AXIS));
                    String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                    datePanel.add(new JLabel("Transaction date: "+today));
                    datePanel.add(new JTable(tableModel));// this line is important
                    double totalPrice = 0;
                    for (int i = 0; i < table.getRowCount(); i++) {
                        Object quantity = table.getValueAt(i, 2);
                        Object price = table.getValueAt(i, 3);
                        totalPrice = totalPrice + (Double.parseDouble(quantity.toString()) * Double.parseDouble(price.toString()));
                    }
                    datePanel.add(new JLabel("Price to be paid: "+totalPrice));
                    // Show the panel in a new dialog window
                    int result = JOptionPane.showConfirmDialog(null, datePanel, "Your receipt:", JOptionPane.OK_CANCEL_OPTION);
                    if (result == 0) {
                        System.out.println(tableModel.getRowCount());
                        TH.setId(transId);
                        TH.setUserId(userId);
                        try {
                            TH.updatePaidStatus();
                            // update product quantity
                           for (int i = 0; i < tableModel.getRowCount(); i++) {
                                String productName = (String) tableModel.getValueAt(i,1);
                                long selectedProductQuantity =  Integer.parseInt(tableModel.getValueAt(i,2).toString());
                                try {
                                    Menu productDB = menu.getMenuByName(productName);
                                    menu.setName(productName);
                                    menu.setPrice(productDB.getPrice());
                                    menu.setDescription(productDB.getDescription());
                                    menu.setServingAmount(productDB.getServingAmount());
                                    menu.setId(productDB.getId());
                                    menu.setDishTypeId(productDB.getDishTypeId());
                                    menu.setQuantity(productDB.getQuantity() - selectedProductQuantity);
                                    menu.updateMenu();// update Quantity
                                } catch (ClassNotFoundException | SQLException ex) {
                                    throw new RuntimeException(ex);
                                }
                           }
                        } catch (SQLException ex) {
                            throw new RuntimeException(ex);
                        }
                        tableModel.setRowCount(0);// reset table data
                        customerMenuFrame.dispose();
                        try {
                            CustomerNavBar.generateCustomerNavBar(userId);
                        } catch (SQLException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                    }
                }

        });
        btnProperties(checkoutButton);
        checkoutButton.setBounds(260, 280, 150, 25);
        cartPanel.add(checkoutButton);
        addButton = new JButton("Add to Cart");

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (menuDropdown.getSelectedItem() != null &&
                        productDropdown.getSelectedItem() != null &&
                        quantityDropdown.getSelectedItem() !=null){
                    Object[] insertedRow = new Object[4];
                    // check if product item already exists in the cart table
                    int exists = checkIfAlreadyExists(tableModel,productDropdown.getSelectedItem().toString());

                    OD.setMenuId(selectedItem.getId());
                    OD.setTransactionId(transId);
                    OD.setQuantity(Long.parseLong(quantityDropdown.getSelectedItem().toString()));

                    if (exists == doesNotExist){
                        insertedRow[0] = menuDropdown.getSelectedItem().toString();
                        insertedRow[1] = productDropdown.getSelectedItem().toString();
                        insertedRow[2] = quantityDropdown.getSelectedItem().toString();
                        insertedRow[3] = String.valueOf(selectedItem.getPrice());
                        try {
                            OD.addOrderDetails();
                        } catch (SQLException | ClassNotFoundException ex) {
                            JOptionPane.showMessageDialog(null, ex.getMessage());
                            return;
                        }
                        tableModel.addRow(insertedRow);
                    }else {
                        try {
                            OD.updateOrderDetails();
                        } catch (SQLException | ClassNotFoundException ex) {
                            JOptionPane.showMessageDialog(null, ex.getMessage());
                            return;
                        }
                        tableModel.setValueAt(quantityDropdown.getSelectedItem().toString(), exists, 2);
                    }

                }
            }

            private int checkIfAlreadyExists(DefaultTableModel tableModel,String productName) {
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    if (tableModel.getValueAt(i,1).equals(productName)) return i;
                }
                return doesNotExist;
            }
        });
        btnProperties(addButton);
        addButton.setBounds(410, 300, 120, 25);
        menuPanel.add(addButton);

        deleteButton = new JButton("Delete");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                String productName = (String) table.getValueAt(selectedRow,1);
                long menuId = 0;
                try {
                    menuId = menu.getMenuByName(productName).getId();
                } catch (SQLException | ClassNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
                OD.setMenuId(menuId);
                OD.setTransactionId(transId);
                try {
                    OD.deleteOrderDetailsRow();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage());
                    return;
                }
                tableModel.removeRow(table.getSelectedRow());
            }
        });
        btnProperties(deleteButton);
        deleteButton.setBounds(550, 240, 100, 25);
        btnProperties(deleteButton);
        cartPanel.add(deleteButton);

        customerMainPanel.add(menuPanel);
        customerMainPanel.add(cartPanel);
        customerMainPanel.setVisible(true);

        return customerMainPanel;
    }

    private  void menuLabelProperties(JLabel label) {
        menuPanel.add(label);
        label.setBounds(50, yCoordinate, 150, 20);
        yCoordinate+= 60;
    }


    private void addTable() {

        tableModel = new DefaultTableModel(null, columnNames);
        // load data
        List<List<String>> results = null;
        try {
            results = TH.getAddToCartTableByUserId((int) userId);
        } catch (SQLException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
            return;
        }
        try {
            long tmp = TH.doesUserHasTHv2(userId);
            System.out.println("trans id =>"+ tmp);
            if (tmp == 0){
                TH.setUserId(userId);
                TH.setHasPaid(false);
                try {
                    TransactionHistory createdTH = TH.createTransactionHistory();
                    transId = createdTH.getId();
                } catch (SQLException | ClassNotFoundException e) {
                    JOptionPane.showMessageDialog(null, e.getMessage());
                    return;
                }
            }else {
                transId = tmp;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        loadData(results, tableModel);
        System.out.println("ready for war =>" +results);
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); //prevent a user from selecting multiple rows
        JScrollPane sp = new JScrollPane(table);
        sp.setBounds(10, 20, 660, 200);
        cartPanel.add(sp);
    }

    public static void loadData(List<List<String>> results, DefaultTableModel tableModel) {
        for (List<String> row : results) {
            Object[] insertedRow = new Object[row.size()];
            for (int i = 0; i < row.size(); i++) {
                if (i == 0){
                    insertedRow[i] = new DishType().getDishType(Integer.parseInt(row.get(i)));
                }else {
                    insertedRow[i] = row.get(i);
                }

            }
            tableModel.addRow(insertedRow);
        }
    }

    private static void updateProductDropdown(JComboBox<String> watchingDropdown, JComboBox<String> changeDropdown) {
        DishType dishType = new DishType();
        watchingDropdown.addActionListener(e -> {
            changeDropdown.removeAllItems();
            if (menuDropdown.getSelectedItem() != null) {
                if (Objects.equals(menuDropdown.getSelectedItem(), "Appetizer")) {
                    int dishTypeNum = dishType.getDishType("appetizer");
                    addItemsList(changeDropdown, dishTypeNum);
                }
                if (Objects.equals(watchingDropdown.getSelectedItem(), "Main Dish")) {
                    int dishTypeNum = dishType.getDishType("main dish");
                    addItemsList(changeDropdown, dishTypeNum);
                }
                if (Objects.equals(menuDropdown.getSelectedItem(), "Dessert")) {
                    int dishTypeNum = dishType.getDishType("dessert");
                    addItemsList(changeDropdown, dishTypeNum);
                }
                if (Objects.equals(watchingDropdown.getSelectedItem(), "Drinks")) {
                    int dishTypeNum = dishType.getDishType("drinks");
                    addItemsList(changeDropdown, dishTypeNum);
                }
            }
        });
    }
    public static Menu selectedItem;
    private static void updateQuantityDropdown(JComboBox<String> watchingDropdown, JComboBox<String> changeDropdown) {

        watchingDropdown.addActionListener(e -> {
            changeDropdown.removeAllItems();
            if (productDropdown.getSelectedItem() != null) {
                try {
                    selectedItem = menu.getMenuByName((String) productDropdown.getSelectedItem());
                    for (int i = 0; i < selectedItem.getQuantity(); i++) {
                        changeDropdown.addItem(String.valueOf(i + 1));
                    }
                } catch (SQLException | ClassNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }

    private static void updatePriceCalculatedLabel(JComboBox<String> quantityDropdown, JLabel priceCalculatedLabel) {
        quantityDropdown.addActionListener(e -> {
            if (quantityDropdown.getSelectedItem() != null) {
                priceCalculatedLabel.
                        setText(
                                String.format("%f TL",
                                        selectedItem.getPrice() * Integer.parseInt(quantityDropdown.getSelectedItem().toString()))
                        );
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

    private void addLabelToPanel(JLabel label, JLabel priceCalculatedLabel) {
        label.setBounds(50, yCoordinate, 150, 20);
        priceCalculatedLabel.setBounds(400, yCoordinate, 150, 20);
        menuPanel.add(label);
        menuPanel.add(priceCalculatedLabel);
        yCoordinate += 60;
    }

    private static void setFrameProperties() {
        customerMainPanel.setLayout(null);
    }

    private static void btnProperties(JButton button) {
        button.setBorderPainted(true);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
    }

}