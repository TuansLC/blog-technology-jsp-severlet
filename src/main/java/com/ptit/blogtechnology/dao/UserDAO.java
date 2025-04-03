package com.ptit.blogtechnology.dao;

import com.ptit.blogtechnology.model.User;
import com.ptit.blogtechnology.utils.DatabaseUtil;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserDAO {

  private static final Logger LOGGER = Logger.getLogger(UserDAO.class.getName());

  public boolean save(User user) {
    LOGGER.info("Saving user: " + user.getUsername());
    
    // Kiểm tra cấu trúc bảng users
    boolean hasTimeColumns = checkIfColumnExists("users", "created_at");
    
    String sql;
    if (hasTimeColumns) {
        sql = "INSERT INTO users (username, full_name, email, password_hash, role, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?)";
    } else {
        sql = "INSERT INTO users (username, full_name, email, password_hash, role) VALUES (?, ?, ?, ?, ?)";
    }
    
    try (Connection conn = DatabaseUtil.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        
        stmt.setString(1, user.getUsername());
        stmt.setString(2, user.getFullName());
        stmt.setString(3, user.getEmail());
        stmt.setString(4, user.getPasswordHash());
        stmt.setString(5, user.getRole());
        
        if (hasTimeColumns) {
            stmt.setTimestamp(6, Timestamp.valueOf(user.getCreatedAt()));
            stmt.setTimestamp(7, Timestamp.valueOf(user.getUpdatedAt()));
        }
        
        int affectedRows = stmt.executeUpdate();
        
        if (affectedRows > 0) {
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getLong(1));
                    return true;
                }
            }
        }
    } catch (SQLException e) {
        LOGGER.log(Level.SEVERE, "Lỗi khi thêm người dùng mới: " + e.getMessage(), e);
    }
    
    return false;
  }

  public User findById(Long id) {
    String sql = "SELECT * FROM users WHERE id = ?";
    
    try (Connection conn = DatabaseUtil.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        stmt.setLong(1, id);
        
        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        }
    } catch (SQLException e) {
        LOGGER.log(Level.SEVERE, "Lỗi khi tìm người dùng theo ID: " + id, e);
    }
    
    return null;
  }

  public User findByUsername(String username) {
    String sql = "SELECT * FROM users WHERE username = ?";
    
    try (Connection conn = DatabaseUtil.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        stmt.setString(1, username);
        
        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        }
    } catch (SQLException e) {
        LOGGER.log(Level.SEVERE, "Lỗi khi tìm người dùng theo username: " + username, e);
    }
    
    return null;
  }

  public User findByEmail(String email) {
    String sql = "SELECT * FROM users WHERE email = ?";
    
    try (Connection conn = DatabaseUtil.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        stmt.setString(1, email);
        
        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        }
    } catch (SQLException e) {
        LOGGER.log(Level.SEVERE, "Lỗi khi tìm người dùng theo email: " + email, e);
    }
    
    return null;
  }

  public List<User> findAll() {
    List<User> users = new ArrayList<>();
    String sql = "SELECT * FROM users";

    try (Connection conn = DatabaseUtil.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql)) {

      while (rs.next()) {
        users.add(mapResultSetToUser(rs));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return users;
  }

  public boolean update(User user) {
    LOGGER.info("Updating user - ID: " + user.getId() + ", Username: " + user.getUsername() + 
               ", FullName: " + user.getFullName() + ", Email: " + user.getEmail() + 
               ", Role: " + user.getRole());
    
    // Kiểm tra cấu trúc bảng users
    boolean hasTimeColumns = checkIfColumnExists("users", "updated_at");
    LOGGER.info("Has time columns: " + hasTimeColumns);
    
    String sql;
    if (hasTimeColumns) {
        sql = "UPDATE users SET username = ?, full_name = ?, email = ?, password_hash = ?, role = ?, updated_at = ? WHERE id = ?";
    } else {
        sql = "UPDATE users SET username = ?, full_name = ?, email = ?, password_hash = ?, role = ? WHERE id = ?";
    }
    LOGGER.info("SQL: " + sql);
    
    try (Connection conn = DatabaseUtil.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        stmt.setString(1, user.getUsername());
        stmt.setString(2, user.getFullName());
        stmt.setString(3, user.getEmail());
        stmt.setString(4, user.getPasswordHash());
        stmt.setString(5, user.getRole());
        
        if (hasTimeColumns) {
            stmt.setTimestamp(6, Timestamp.valueOf(user.getUpdatedAt()));
            stmt.setLong(7, user.getId());
        } else {
            stmt.setLong(6, user.getId());
        }
        
        LOGGER.info("Executing update for user ID: " + user.getId());
        int affectedRows = stmt.executeUpdate();
        LOGGER.info("Update affected rows: " + affectedRows);
        return affectedRows > 0;
    } catch (SQLException e) {
        LOGGER.log(Level.SEVERE, "Lỗi SQL khi cập nhật người dùng: " + e.getMessage(), e);
    }
    
    return false;
  }

  public boolean delete(Long id) {
    LOGGER.info("Deleting user with ID: " + id);
    
    try (Connection conn = DatabaseUtil.getConnection()) {
        conn.setAutoCommit(false);
        
        try {
            // Xóa các bài viết của người dùng
            String deletePostsSql = "DELETE FROM posts WHERE author_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deletePostsSql)) {
                stmt.setLong(1, id);
                stmt.executeUpdate();
            }
            
            // Xóa các bình luận của người dùng
            String deleteCommentsSql = "DELETE FROM comments WHERE user_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteCommentsSql)) {
                stmt.setLong(1, id);
                stmt.executeUpdate();
            }
            
            // Xóa người dùng
            String deleteUserSql = "DELETE FROM users WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteUserSql)) {
                stmt.setLong(1, id);
                int affectedRows = stmt.executeUpdate();
                
                conn.commit();
                return affectedRows > 0;
            }
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    } catch (SQLException e) {
        LOGGER.log(Level.SEVERE, "Lỗi khi xóa người dùng: " + id, e);
    }
    
    return false;
  }

  private User mapResultSetToUser(ResultSet rs) throws SQLException {
    User user = new User();
    user.setId(rs.getLong("id"));
    user.setUsername(rs.getString("username"));
    user.setEmail(rs.getString("email"));
    user.setPasswordHash(rs.getString("password_hash"));
    user.setFullName(rs.getString("full_name"));
    String role = rs.getString("role");
    LOGGER.info("Loading user from DB - Role from DB: " + role);
    user.setRole(role);
    user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
    user.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
    
    LOGGER.info("Mapping user from DB - ID: " + user.getId() + ", Role: " + user.getRole());
    
    return user;
  }

  private boolean checkIfColumnExists(String tableName, String columnName) {
    try (Connection conn = DatabaseUtil.getConnection()) {
        DatabaseMetaData meta = conn.getMetaData();
        ResultSet rs = meta.getColumns(null, null, tableName, columnName);
        return rs.next(); // Trả về true nếu cột tồn tại
    } catch (SQLException e) {
        LOGGER.log(Level.SEVERE, "Lỗi khi kiểm tra cấu trúc bảng: " + e.getMessage(), e);
        return false; // Mặc định là không có cột
    }
  }

}
