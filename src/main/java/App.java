

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class App {
    public static void main(String[] args) throws SQLException {
        Connection conn = null;
        try {
            conn = openConnection("jdbc:derby:", "localhost", 1527, "UsersDb");
            conn.setAutoCommit(false);
//             getUsers(conn);
//             fillBalance(conn);
//             deleteData(conn);

            conn.commit();
        } catch (SQLException ex) {
            conn.rollback();
            printSQLException(ex);
        } finally {
            closeConnection(conn);
        }
    }

    private static Connection openConnection(String protocol, String host, int port, String db) throws SQLException {
        StringBuilder sb = new StringBuilder();
        sb.append(protocol);
        sb.append("//");
        sb.append(host);
        sb.append(":");
        sb.append(port);
        sb.append("/");
        sb.append(db);
        return DriverManager.getConnection(sb.toString());
    }

    private static void closeConnection(Connection conn) {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException ex) {
            printSQLException(ex);
        }
    }

    private static void getUsers(Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT Id, Name, Balance FROM Users");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                System.out.println("Id: " + rs.getInt("Id"));
                System.out.println("Name: " + rs.getString("Name"));
                System.out.println("Balance: " + rs.getDouble("Balance"));
                System.out.println(" ");
            }
        }
    }

    private static void fillBalance(Connection conn) throws SQLException {
        try (PreparedStatement ps = conn
                .prepareStatement("UPDATE Transactions SET Amount = ? WHERE User_id = ?")) {
            ps.setDouble(1, 2000);
            ps.setInt(2, 1);
            int updatedRowsCount = ps.executeUpdate();
            System.out.println(updatedRowsCount + " row(s) updated");
        }
    }

    private static void deleteData(Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM Transactions WHERE User_id = ?")) {
            ps.setString(1, "1");
            int deletedRowsCount = ps.executeUpdate();
            System.out.println(deletedRowsCount + " row(s) deleted");
        }
    }


    private static void printSQLException(SQLException e) {
        while (e != null) {
            System.err.println("\n----- SQLException -----");
            System.err.println("  SQL State:  " + e.getSQLState());
            System.err.println("  Error Code: " + e.getErrorCode());
            System.err.println("  Message:    " + e.getMessage());
            e = e.getNextException();
        }
    }
}
