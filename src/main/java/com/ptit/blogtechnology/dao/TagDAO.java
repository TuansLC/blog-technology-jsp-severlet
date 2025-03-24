package com.ptit.blogtechnology.dao;

import com.ptit.blogtechnology.model.Tag;
import com.ptit.blogtechnology.utils.DBUtils;
import com.ptit.blogtechnology.utils.SlugGenerator;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TagDAO {

  // Lấy tất cả thẻ
  public List<Tag> findAll() {
    List<Tag> tags = new ArrayList<>();

    String sql = "SELECT * FROM tags ORDER BY name";

    try (Connection conn = DBUtils.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery()) {

      while (rs.next()) {
        Tag tag = mapResultSetToTag(rs);
        tags.add(tag);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return tags;
  }

  // Tìm thẻ theo ID
  public Tag findById(int id) {
    Tag tag = null;

    String sql = "SELECT * FROM tags WHERE id = ?";

    try (Connection conn = DBUtils.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setInt(1, id);

      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          tag = mapResultSetToTag(rs);
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return tag;
  }

  // Tìm thẻ theo slug
  public Tag findBySlug(String slug) {
    Tag tag = null;

    String sql = "SELECT * FROM tags WHERE slug = ?";

    try (Connection conn = DBUtils.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setString(1, slug);

      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          tag = mapResultSetToTag(rs);
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return tag;
  }

  // Tìm thẻ theo tên
  public Tag findByName(String name) {
    Tag tag = null;

    String sql = "SELECT * FROM tags WHERE name = ?";

    try (Connection conn = DBUtils.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setString(1, name);

      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          tag = mapResultSetToTag(rs);
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return tag;
  }

  // Lấy thẻ của một bài viết
  public List<Tag> findByPostId(int postId) {
    List<Tag> tags = new ArrayList<>();

    String sql = "SELECT t.* FROM tags t " +
        "JOIN post_tags pt ON t.id = pt.tag_id " +
        "WHERE pt.post_id = ? " +
        "ORDER BY t.name";

    try (Connection conn = DBUtils.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setInt(1, postId);

      try (ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
          Tag tag = mapResultSetToTag(rs);
          tags.add(tag);
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return tags;
  }

  // Lưu thẻ mới
  public boolean save(Tag tag) {
    String sql = "INSERT INTO tags (name, slug, created_at, updated_at) VALUES (?, ?, ?, ?)";

    try (Connection conn = DBUtils.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

      // Tạo slug từ tên nếu chưa có
      if (tag.getSlug() == null || tag.getSlug().isEmpty()) {
        tag.setSlug(SlugGenerator.toSlug(tag.getName()));
      }

      // Kiểm tra slug đã tồn tại chưa
      Tag existingTag = findBySlug(tag.getSlug());
      if (existingTag != null) {
        // Slug đã tồn tại, thêm timestamp để tạo slug mới
        tag.setSlug(tag.getSlug() + "-" + System.currentTimeMillis());
      }

      stmt.setString(1, tag.getName());
      stmt.setString(2, tag.getSlug());

      LocalDateTime now = LocalDateTime.now();
      stmt.setTimestamp(3, Timestamp.valueOf(now));
      stmt.setTimestamp(4, Timestamp.valueOf(now));

      int affectedRows = stmt.executeUpdate();

      if (affectedRows > 0) {
        try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
          if (generatedKeys.next()) {
            tag.setId(generatedKeys.getInt(1));
            return true;
          }
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return false;
  }

  // Cập nhật thẻ
  public boolean update(Tag tag) {
    String sql = "UPDATE tags SET name = ?, slug = ?, updated_at = ? WHERE id = ?";

    try (Connection conn = DBUtils.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      // Tạo slug từ tên nếu chưa có
      if (tag.getSlug() == null || tag.getSlug().isEmpty()) {
        tag.setSlug(SlugGenerator.toSlug(tag.getName()));
      }

      // Kiểm tra slug đã tồn tại chưa
      String checkSlugSql = "SELECT id FROM tags WHERE slug = ? AND id != ?";
      try (PreparedStatement checkStmt = conn.prepareStatement(checkSlugSql)) {
        checkStmt.setString(1, tag.getSlug());
        checkStmt.setInt(2, tag.getId());

        try (ResultSet rs = checkStmt.executeQuery()) {
          if (rs.next()) {
            // Slug đã tồn tại, thêm timestamp để tạo slug mới
            tag.setSlug(tag.getSlug() + "-" + System.currentTimeMillis());
          }
        }
      }

      stmt.setString(1, tag.getName());
      stmt.setString(2, tag.getSlug());
      stmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
      stmt.setInt(4, tag.getId());

      int affectedRows = stmt.executeUpdate();
      return affectedRows > 0;

    } catch (SQLException e) {
      e.printStackTrace();
    }

    return false;
  }

  // Xóa thẻ
  public boolean delete(int tagId) {
    Connection conn = null;
    PreparedStatement stmt = null;

    try {
      conn = DBUtils.getConnection();
      conn.setAutoCommit(false);

      // Xóa các liên kết với bài viết
      String deletePostTagsSql = "DELETE FROM post_tags WHERE tag_id = ?";
      try (PreparedStatement deleteStmt = conn.prepareStatement(deletePostTagsSql)) {
        deleteStmt.setInt(1, tagId);
        deleteStmt.executeUpdate();
      }

      // Xóa thẻ
      String sql = "DELETE FROM tags WHERE id = ?";
      stmt = conn.prepareStatement(sql);
      stmt.setInt(1, tagId);

      int affectedRows = stmt.executeUpdate();

      conn.commit();
      return affectedRows > 0;

    } catch (SQLException e) {
      e.printStackTrace();
      if (conn != null) {
        try {
          conn.rollback();
        } catch (SQLException ex) {
          ex.printStackTrace();
        }
      }
    } finally {
      if (stmt != null) {
        try {
          stmt.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
      if (conn != null) {
        try {
          conn.setAutoCommit(true);
          conn.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    }

    return false;
  }

  // Phương thức hỗ trợ để chuyển ResultSet thành đối tượng Tag
  private Tag mapResultSetToTag(ResultSet rs) throws SQLException {
    Tag tag = new Tag();
    tag.setId(rs.getInt("id"));
    tag.setName(rs.getString("name"));
    tag.setSlug(rs.getString("slug"));

    // Chuyển đổi Timestamp thành LocalDateTime
    Timestamp createdAt = rs.getTimestamp("created_at");
    if (createdAt != null) {
      tag.setCreatedAt(createdAt.toLocalDateTime());
    }

    Timestamp updatedAt = rs.getTimestamp("updated_at");
    if (updatedAt != null) {
      tag.setUpdatedAt(updatedAt.toLocalDateTime());
    }

    return tag;
  }
}