package com.hospital.database;

import com.hospital.model.Message;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Message operations
 */
public class MessageDAO {

    /**
     * Send message
     */
    public static int sendMessage(Message message) {
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        int messageId = -1;

        try {
            conn = DatabaseConnection.getConnection();
            String query = "INSERT INTO messages (sender_id, receiver_id, subject, message_text) " +
                          "VALUES (?, ?, ?, ?)";
            pst = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            pst.setInt(1, message.getSenderId());
            pst.setInt(2, message.getReceiverId());
            pst.setString(3, message.getSubject());
            pst.setString(4, message.getMessageText());

            int result = pst.executeUpdate();
            if (result > 0) {
                rs = pst.getGeneratedKeys();
                if (rs.next()) {
                    messageId = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResultSet(rs);
            DatabaseConnection.closePreparedStatement(pst);
        }
        return messageId;
    }

    /**
     * Get received messages for a user
     */
    public static List<Message> getReceivedMessages(int userId) {
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        List<Message> messages = new ArrayList<>();

        try {
            conn = DatabaseConnection.getConnection();
            String query = "SELECT m.*, " +
                          "s.full_name as sender_name, " +
                          "r.full_name as receiver_name " +
                          "FROM messages m " +
                          "INNER JOIN users s ON m.sender_id = s.user_id " +
                          "INNER JOIN users r ON m.receiver_id = r.user_id " +
                          "WHERE m.receiver_id = ? " +
                          "ORDER BY m.sent_at DESC";
            pst = conn.prepareStatement(query);
            pst.setInt(1, userId);

            rs = pst.executeQuery();
            while (rs.next()) {
                messages.add(extractMessageFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResultSet(rs);
            DatabaseConnection.closePreparedStatement(pst);
        }
        return messages;
    }

    /**
     * Get sent messages for a user
     */
    public static List<Message> getSentMessages(int userId) {
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        List<Message> messages = new ArrayList<>();

        try {
            conn = DatabaseConnection.getConnection();
            String query = "SELECT m.*, " +
                          "s.full_name as sender_name, " +
                          "r.full_name as receiver_name " +
                          "FROM messages m " +
                          "INNER JOIN users s ON m.sender_id = s.user_id " +
                          "INNER JOIN users r ON m.receiver_id = r.user_id " +
                          "WHERE m.sender_id = ? " +
                          "ORDER BY m.sent_at DESC";
            pst = conn.prepareStatement(query);
            pst.setInt(1, userId);

            rs = pst.executeQuery();
            while (rs.next()) {
                messages.add(extractMessageFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResultSet(rs);
            DatabaseConnection.closePreparedStatement(pst);
        }
        return messages;
    }

    /**
     * Mark message as read
     */
    public static boolean markAsRead(int messageId) {
        Connection conn = null;
        PreparedStatement pst = null;

        try {
            conn = DatabaseConnection.getConnection();
            String query = "UPDATE messages SET read_status = TRUE, read_at = NOW() WHERE message_id = ?";
            pst = conn.prepareStatement(query);
            pst.setInt(1, messageId);

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
     * Get unread message count
     */
    public static int getUnreadCount(int userId) {
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        int count = 0;

        try {
            conn = DatabaseConnection.getConnection();
            String query = "SELECT COUNT(*) as count FROM messages WHERE receiver_id = ? AND read_status = FALSE";
            pst = conn.prepareStatement(query);
            pst.setInt(1, userId);

            rs = pst.executeQuery();
            if (rs.next()) {
                count = rs.getInt("count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResultSet(rs);
            DatabaseConnection.closePreparedStatement(pst);
        }
        return count;
    }

    /**
     * Extract Message object from ResultSet
     */
    private static Message extractMessageFromResultSet(ResultSet rs) throws SQLException {
        Message message = new Message();
        message.setMessageId(rs.getInt("message_id"));
        message.setSenderId(rs.getInt("sender_id"));
        message.setReceiverId(rs.getInt("receiver_id"));
        message.setSubject(rs.getString("subject"));
        message.setMessageText(rs.getString("message_text"));
        message.setSentAt(rs.getTimestamp("sent_at"));
        message.setReadStatus(rs.getBoolean("read_status"));
        message.setReadAt(rs.getTimestamp("read_at"));
        message.setSenderName(rs.getString("sender_name"));
        message.setReceiverName(rs.getString("receiver_name"));
        return message;
    }
}
