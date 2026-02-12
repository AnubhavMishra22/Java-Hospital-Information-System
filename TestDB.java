import java.sql.*;

public class TestDB {
    public static void main(String[] args) {
        System.out.println("Testing database connection...");

        String url = "jdbc:mysql://localhost:3306/hospital_management";
        String user = "root";
        String password = "root";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("✓ MySQL Driver loaded");

            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("✓ Connected to database!");

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SHOW TABLES");

            System.out.println("\nTables in database:");
            while (rs.next()) {
                System.out.println("  - " + rs.getString(1));
            }

            conn.close();
            System.out.println("\n✓ Test successful!");

        } catch (ClassNotFoundException e) {
            System.err.println("✗ MySQL Driver NOT found!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("✗ Database connection FAILED!");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
