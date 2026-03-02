package employee.management.system;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class Conn {

    // ── Database credentials ──────────────────────────────────────────
    private static final String DB_URL  = "jdbc:mysql://localhost:3306/employeemanagement";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "admin123";
    // ─────────────────────────────────────────────────────────────────

    public Connection connection;
    public Statement  statement;

    public Conn() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            statement  = connection.createStatement();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            if (statement  != null) statement.close();
            if (connection != null) connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
