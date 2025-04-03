package com.ptit.blogtechnology.dao;

import com.ptit.blogtechnology.model.Subscriber;
import com.ptit.blogtechnology.utils.DatabaseUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class SubscriberDAO {

  // Tìm subscriber theo email
  public Subscriber findByEmail(String email) {
    Subscriber subscriber = null;

    String sql = "SELECT * FROM subscribers WHERE email = ?";

    try (Connection conn = DatabaseUtil.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setString(1, email);

      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          subscriber = mapResultSetToSubscriber(rs);
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return subscriber;
  }

  // Lưu subscriber mới
  public boolean save(Subscriber subscriber) {
    String sql = "INSERT INTO subscribers (email, status, created_at, updated_at) VALUES (?, ?, ?, ?)";

    try (Connection conn = DatabaseUtil.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

      stmt.setString(1, subscriber.getEmail());
      stmt.setString(2, subscriber.getStatus());
      stmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
      stmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));

      int affectedRows = stmt.executeUpdate();

      if (affectedRows > 0) {
        try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
          if (generatedKeys.next()) {
            subscriber.setId(generatedKeys.getInt(1));
            return true;
          }
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return false;
  }

  // Cập nhật trạng thái subscriber
  public boolean updateStatus(int id, String status) {
    String sql = "UPDATE subscribers SET status = ?, updated_at = ? WHERE id = ?";

    try (Connection conn = DatabaseUtil.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setString(1, status);
      stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
      stmt.setInt(3, id);

      int affectedRows = stmt.executeUpdate();
      return affectedRows > 0;

    } catch (SQLException e) {
      e.printStackTrace();
    }

    return false;
  }

  // Phương thức hỗ trợ để chuyển ResultSet thành đối tượng Subscriber
  private Subscriber mapResultSetToSubscriber(ResultSet rs) throws SQLException {
    Subscriber subscriber = new Subscriber();
    subscriber.setId(rs.getInt("id"));
    subscriber.setEmail(rs.getString("email"));
    subscriber.setStatus(rs.getString("status"));

    // Chuyển đổi Timestamp thành LocalDateTime
    Timestamp createdAt = rs.getTimestamp("created_at");
    if (createdAt != null) {
      subscriber.setCreatedAt(createdAt.toLocalDateTime());
    }

    Timestamp updatedAt = rs.getTimestamp("updated_at");
    if (updatedAt != null) {
      subscriber.setUpdatedAt(updatedAt.toLocalDateTime());
    }

    return subscriber;
  }
}