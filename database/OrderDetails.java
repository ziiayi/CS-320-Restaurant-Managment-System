package database;

import database.utils.Helper;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class OrderDetails implements Template,Cloneable {
    private long id;
    private long transactionId;
    private long quantity;
    private long menuId;

    // create order details
    public OrderDetails createOrderDetails(OrderDetails OD) throws SQLException, ClassNotFoundException {
        String SQL_QUERY = "INSERT INTO order_details(transaction_id,quantity,menu_id) VALUES (?, ?, ?)";
        Connect connect = Connect.getInstance();
        if (TransactionHistory.doesTransactionHistoryExists(OD.transactionId)){
            try (
                    PreparedStatement statement = connect.connection.prepareStatement(SQL_QUERY,
                            Statement.RETURN_GENERATED_KEYS);
            ) {
                statement.setLong(1, OD.transactionId);
                statement.setLong(2, OD.quantity);
                statement.setLong(3, OD.menuId);

                return Helper.executeAndGetId(OD,statement);
            }

        }

        throw new SQLException("transaction id does not exists");

    }

    public void setId(long id) {
        this.id = id;
    }

    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }




}