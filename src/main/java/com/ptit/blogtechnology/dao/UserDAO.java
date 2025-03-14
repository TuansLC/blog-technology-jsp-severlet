package com.ptit.blogtechnology.dao;

import com.ptit.blogtechnology.model.User;
import com.ptit.blogtechnology.utils.DatabaseUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

  public boolean save(User user) {
    String sql = "INSERT INTO users (username, email, password_hash, full_name, created_at, updated_at) " +
        "VALUES (?, ?, ?, ?, ?, ?)";

    try (Connection conn = DatabaseUtil.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

      stmt.setString(1, user.getUsername());
      stmt.setString(2, user.getEmail());
      stmt.setString(3, user.getPasswordHash());
      stmt.setString(4, user.getFullName());
      stmt.setTimestamp(5, Timestamp.valueOf(user.getCreatedAt()));
      stmt.setTimestamp(6, Timestamp.valueOf(user.getUpdatedAt()));

      int affectedRows = stmt.executeUpdate();

      if (affectedRows > 0) {
        try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
          if (generatedKeys.next()) {
            user.setId(generatedKeys.getLong(1));
            return true;
          }
        }
      }
      return false;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
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
      e.printStackTrace();
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
      e.printStackTrace();
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
      e.printStackTrace();
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
    String sql = "UPDATE users SET username = ?, email = ?, password_hash = ?, " +
        "full_name = ?, updated_at = ? WHERE id = ?";

    try (Connection conn = DatabaseUtil.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setString(1, user.getUsername());
      stmt.setString(2, user.getEmail());
      stmt.setString(3, user.getPasswordHash());
      stmt.setString(4, user.getFullName());
      stmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
      stmt.setLong(6, user.getId());

      return stmt.executeUpdate() > 0;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public boolean delete(Long id) {
    String sql = "DELETE FROM users WHERE id = ?";

    try (Connection conn = DatabaseUtil.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setLong(1, id);

      return stmt.executeUpdate() > 0;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  private User mapResultSetToUser(ResultSet rs) throws SQLException {
    User user = new User();
    user.setId(rs.getLong("id"));
    user.setUsername(rs.getString("username"));
    user.setEmail(rs.getString("email"));
    user.setPasswordHash(rs.getString("password_hash"));
    user.setFullName(rs.getString("full_name"));
    user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
    user.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
    return user;
  }

}
