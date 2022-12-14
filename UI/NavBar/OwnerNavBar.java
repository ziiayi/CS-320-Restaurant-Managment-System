package UI.NavBar;
import UI.EmployeeManager.EmployeeTableUI;
import UI.MenuManager.OwnerMenu;
import javax.swing.*;
import java.sql.SQLException;

public class OwnerNavBar {
    public static JFrame mainFrame;
    public static void generateOwnerNavBar() throws SQLException {
        try {
            UIManager.setLookAndFeel( UIManager.getCrossPlatformLookAndFeelClassName() );
        } catch (Exception e) {
            e.printStackTrace();
        }
        mainFrame = new JFrame();
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.add("Manage Menu Items", OwnerMenu.generateUI());
        tabbedPane.add("Manage Employees", EmployeeTableUI.generateUI());
        mainFrame.add(tabbedPane);
        mainFrame.setTitle("Welcome");
        mainFrame.setResizable(false);
        mainFrame.setSize(700, 750);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setVisible(true);
    }

}
