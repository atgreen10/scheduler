package utils;

import java.sql.*;

public class DBConnection {

    /** Database object references */
    public static Connection conn = null;
    private Statement stmt = null;

    /** database credentials: */
    

    public static void main(String[] args) {

        /** Driver interface reference*/
        final String JDBCDriver = "com.mysql.cj.jdbc.Driver";
    }


    /** Creates connection with database */
    public static Connection startConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(jdbcURL, userName, password);
            System.out.println("Connection successful");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

        return conn;
    }

    public static void closeAll(Statement stmt, ResultSet resultSet, Connection con) {
        try {
            if (resultSet != null)
                resultSet.close();
        } catch (SQLException ignored) {
        }
        try {
            if (stmt != null)
                stmt.close();
        } catch (SQLException ignored) {
        }
        try {
            if (con != null)
                con.close();
        } catch (SQLException ignored) {
        }
    }

    /** Closes connection with database */
    public static void closeConnection() {
        try {
            conn.close();
            System.out.println("Connection to database closed");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
