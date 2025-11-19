package com.hospital.database;

import com.hospital.model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for User operations
 */
public class UserDAO {

    /**
     * Authenticate user login
     */
    public static User authenticateUser(String username, String password) {
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        User user = null;

        try {
            conn = DatabaseConnection.getConnection();
            String query = "SELECT * FROM users WHERE username = ? AND password = ? AND status = 'ACTIVE'";
            pst = conn.prepareStatement(query);
            pst.setString(1, username);
            pst.setString(2, password);

            rs = pst.executeQuery();
            if (rs.next()) {
                user = extractUserFromResultSet(rs);

                // Update last login
                updateLastLogin(user.getUserId());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResultSet(rs);
            DatabaseConnection.closePreparedStatement(pst);
        }
        return user;
    }

    /**
     * Update last login timestamp
     */
    private static void updateLastLogin(int userId) {
        Connection conn = null;
        PreparedStatement pst = null;

        try {
            conn = DatabaseConnection.getConnection();
            String query = "UPDATE users SET last_login = NOW() WHERE user_id = ?";
            pst = conn.prepareStatement(query);
            pst.setInt(1, userId);
            pst.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closePreparedStatement(pst);
        }
    }

    /**
     * Get all users
     */
    public static List<User> getAllUsers() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        List<User> users = new ArrayList<>();

        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM users ORDER BY full_name");

            while (rs.next()) {
                users.add(extractUserFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResultSet(rs);
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return users;
    }

    /**
     * Get users by role
     */
    public static List<User> getUsersByRole(User.UserRole role) {
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        List<User> users = new ArrayList<>();

        try {
            conn = DatabaseConnection.getConnection();
            String query = "SELECT * FROM users WHERE role = ? AND status = 'ACTIVE' ORDER BY full_name";
            pst = conn.prepareStatement(query);
            pst.setString(1, role.name());

            rs = pst.executeQuery();
            while (rs.next()) {
                users.add(extractUserFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResultSet(rs);
            DatabaseConnection.closePreparedStatement(pst);
        }
        return users;
    }

    /**
     * Add new user
     */
    public static boolean addUser(User user) {
        Connection conn = null;
        PreparedStatement pst = null;

        try {
            conn = DatabaseConnection.getConnection();
            String query = "INSERT INTO users (username, password, full_name, email, phone, role, status) " +
                          "VALUES (?, ?, ?, ?, ?, ?, ?)";
            pst = conn.prepareStatement(query);
            pst.setString(1, user.getUsername());
            pst.setString(2, user.getPassword());
            pst.setString(3, user.getFullName());
            pst.setString(4, user.getEmail());
            pst.setString(5, user.getPhone());
            pst.setString(6, user.getRole().name());
            pst.setString(7, user.getStatus().name());

            int result = pst.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DatabaseConnection.closePreparedStatement(pst);
        }
    }

    /**
     * Get user by ID
     */
    public static User getUserById(int userId) {
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        User user = null;

        try {
            conn = DatabaseConnection.getConnection();
            String query = "SELECT * FROM users WHERE user_id = ?";
            pst = conn.prepareStatement(query);
            pst.setInt(1, userId);

            rs = pst.executeQuery();
            if (rs.next()) {
                user = extractUserFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResultSet(rs);
            DatabaseConnection.closePreparedStatement(pst);
        }
        return user;
    }

    /**
     * Extract User object from ResultSet
     */
    private static User extractUserFromResultSet(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setFullName(rs.getString("full_name"));
        user.setEmail(rs.getString("email"));
        user.setPhone(rs.getString("phone"));
        user.setRole(User.UserRole.valueOf(rs.getString("role")));
        user.setStatus(User.UserStatus.valueOf(rs.getString("status")));
        user.setCreatedAt(rs.getTimestamp("created_at"));
        user.setLastLogin(rs.getTimestamp("last_login"));
        return user;
    }
}
