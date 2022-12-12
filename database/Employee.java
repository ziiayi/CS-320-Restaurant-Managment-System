package database;

import java.sql.*;

public class Employee {
    private Connection conn;
    private long id;
    private long salary;
    private long userId;
    private long salaryType;

    public Employee(Connection conn) {
        this.conn = conn;
    }

    public Employee(long userId,long salary,long salaryType){
        this.userId = userId;
        this.salary = salary;
        this.salaryType = salaryType;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getSalary() {
        return salary;
    }

    public void setSalary(long salary) {
        this.salary = salary;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getSalaryType() {
        return salaryType;
    }

    public void setSalaryType(long salaryType) {
        this.salaryType = salaryType;
    }

    public Employee getEmployeeId(long userId) throws SQLException {
        String SQL_INSERT = "SELECT * from employee WHERE employee.user_id = ?";
        PreparedStatement statement = conn.prepareStatement(SQL_INSERT);
        statement.setLong(1, userId);
        ResultSet rs = statement.executeQuery();
        if (!rs.next()){
            throw new SQLException("employee not found!");
        }

        return new Employee(rs.getLong(3),
                rs.getLong(2),
                rs.getLong(4));
    }

    // update salary by user id
    public boolean updateSalaryByUserId() throws SQLException {
        String SQL_QUERY = "UPDATE employee SET salary = ? WHERE employee.user_id = ?";
        try (
                PreparedStatement statement = conn.prepareStatement(SQL_QUERY, Statement.RETURN_GENERATED_KEYS);
        ) {
            statement.setLong(1, salary);
            statement.setLong(2, userId);
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("updating salary failed, no rows affected.");
            }
            return true;
        }
    }
}