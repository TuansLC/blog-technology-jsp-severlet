package com.ptit.blogtechnology.dao;

import com.ptit.blogtechnology.model.Contact;
import com.ptit.blogtechnology.utils.DatabaseUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ContactDAO {
    private static final Logger LOGGER = Logger.getLogger(ContactDAO.class.getName());

    public boolean save(Contact contact) {
        String sql = "INSERT INTO contact_messages (name, email, message, status) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, contact.getName());
            stmt.setString(2, contact.getEmail());
            stmt.setString(3, contact.getMessage());
            stmt.setString(4, Contact.Status.NEW.name());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        contact.setId(rs.getInt(1));
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lưu tin nhắn liên hệ", e);
        }
        return false;
    }

    public List<Contact> findAll() {
        List<Contact> contacts = new ArrayList<>();
        String sql = "SELECT * FROM contact_messages ORDER BY created_at DESC";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Contact contact = mapResultSetToContact(rs);
                contacts.add(contact);
            }
            
            // Thêm log để debug
            LOGGER.info("Loaded " + contacts.size() + " contacts from database");
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy danh sách tin nhắn liên hệ", e);
        }
        return contacts;
    }

    public boolean updateStatus(int id, Contact.Status status) {
        String sql = "UPDATE contact_messages SET status = ? WHERE id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status.name());
            stmt.setInt(2, id);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi cập nhật trạng thái tin nhắn", e);
            return false;
        }
    }

    public int countNewMessages() {
        int count = 0;
        String sql = "SELECT COUNT(*) FROM contact_messages WHERE status = 'NEW'";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi đếm tin nhắn mới", e);
        }
        return count;
    }

    public int countAllContacts() {
        int count = 0;
        String sql = "SELECT COUNT(*) FROM contact_messages";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi đếm tổng số liên hệ", e);
        }
        return count;
    }

    public List<Contact> findAllPaginated(int page, int contactsPerPage, String status) {
        List<Contact> contacts = new ArrayList<>();
        int offset = (page - 1) * contactsPerPage;
        
        String sql = "SELECT * FROM contact_messages";
        if (status != null && !status.isEmpty()) {
            sql += " WHERE status = ?";
        }
        sql += " ORDER BY created_at DESC LIMIT ? OFFSET ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            int paramIndex = 1;
            if (status != null && !status.isEmpty()) {
                stmt.setString(paramIndex++, status);
            }
            stmt.setInt(paramIndex++, contactsPerPage);
            stmt.setInt(paramIndex, offset);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Contact contact = mapResultSetToContact(rs);
                    contacts.add(contact);
                }
            }
            
            LOGGER.info("Loaded " + contacts.size() + " contacts from database for page " + page + (status != null ? " with status " + status : ""));
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy danh sách tin nhắn liên hệ có phân trang", e);
        }
        return contacts;
    }

    public int countAllContacts(String status) {
        int count = 0;
        String sql = "SELECT COUNT(*) FROM contact_messages";
        if (status != null && !status.isEmpty()) {
            sql += " WHERE status = ?";
        }

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            if (status != null && !status.isEmpty()) {
                stmt.setString(1, status);
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    count = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi đếm tổng số liên hệ", e);
        }
        return count;
    }

    private Contact mapResultSetToContact(ResultSet rs) throws SQLException {
        Contact contact = new Contact();
        contact.setId(rs.getInt("id"));
        contact.setName(rs.getString("name"));
        contact.setEmail(rs.getString("email"));
        contact.setMessage(rs.getString("message"));
        contact.setStatus(Contact.Status.valueOf(rs.getString("status")));
        contact.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        contact.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        return contact;
    }
} 