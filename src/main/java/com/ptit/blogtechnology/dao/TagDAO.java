package com.ptit.blogtechnology.dao;

import com.ptit.blogtechnology.model.Tag;
import com.ptit.blogtechnology.utils.DatabaseUtil;
import com.ptit.blogtechnology.utils.SlugGenerator;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TagDAO {

  private static final Logger LOGGER = Logger.getLogger(TagDAO.class.getName());

  // Thay đổi access modifier từ private sang protected
  protected Tag mapResultSetToTag(ResultSet rs) throws SQLException {
    Tag tag = new Tag();
    tag.setId(rs.getInt("id"));
    tag.setName(rs.getString("name"));
    tag.setSlug(rs.getString("slug"));

    // Kiểm tra xem có cột created_at không
    try {
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            tag.setCreatedAt(createdAt.toLocalDateTime());
        }
    } catch (SQLException e) {
        // Bỏ qua nếu không có cột created_at
        LOGGER.log(Level.FINE, "Không tìm thấy cột created_at", e);
    }

    // Kiểm tra xem có cột updated_at không
    try {
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            tag.setUpdatedAt(updatedAt.toLocalDateTime());
        }
    } catch (SQLException e) {
        // Bỏ qua nếu không có cột updated_at
        LOGGER.log(Level.FINE, "Không tìm thấy cột updated_at", e);
    }

    return tag;
  }

  // Lấy tất cả thẻ
  public List<Tag> findAll() {
    List<Tag> tags = new ArrayList<>();

    String sql = "SELECT * FROM tags ORDER BY name";

    try (Connection conn = DatabaseUtil.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery()) {

      while (rs.next()) {
        Tag tag = mapResultSetToTag(rs);
        tags.add(tag);
      }
    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, "Lỗi khi lấy danh sách thẻ", e);
    }

    return tags;
  }

  // Tìm thẻ theo ID
  public Tag findById(int id) {
    String sql = "SELECT * FROM tags WHERE id = ?";
    
    try (Connection conn = DatabaseUtil.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        stmt.setInt(1, id);
        
        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return mapResultSetToTag(rs);
            }
        }
    } catch (SQLException e) {
        LOGGER.log(Level.SEVERE, "Lỗi khi tìm thẻ theo ID: " + id, e);
    }
    
    return null;
  }

  // Tìm thẻ theo slug
  public Tag findBySlug(String slug) {
    Tag tag = null;

    String sql = "SELECT * FROM tags WHERE slug = ?";

    try (Connection conn = DatabaseUtil.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setString(1, slug);

      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          tag = mapResultSetToTag(rs);
        }
      }
    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, "Lỗi khi tìm thẻ theo slug: " + slug, e);
    }

    return tag;
  }

  // Tìm thẻ theo tên
  public Tag findByName(String name) {
    Tag tag = null;

    String sql = "SELECT * FROM tags WHERE name = ?";

    try (Connection conn = DatabaseUtil.getConnection();
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

    try (Connection conn = DatabaseUtil.getConnection();
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

  // Thêm thẻ mới
  public boolean save(Tag tag) {
    LOGGER.info("Saving tag: " + tag.getName());
    String sql = "INSERT INTO tags (name, slug) VALUES (?, ?)";
    
    try (Connection conn = DatabaseUtil.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        
        stmt.setString(1, tag.getName());
        stmt.setString(2, tag.getSlug());
        
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
        LOGGER.log(Level.SEVERE, "Lỗi khi thêm thẻ mới: " + e.getMessage(), e);
    }
    
    return false;
  }

  // Cập nhật thẻ
  public boolean update(Tag tag) {
    LOGGER.info("Updating tag - ID: " + tag.getId() + ", Name: " + tag.getName());
    String sql = "UPDATE tags SET name = ?, slug = ? WHERE id = ?";
    
    try (Connection conn = DatabaseUtil.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        stmt.setString(1, tag.getName());
        stmt.setString(2, tag.getSlug());
        stmt.setInt(3, tag.getId());
        
        int affectedRows = stmt.executeUpdate();
        LOGGER.info("Update affected rows: " + affectedRows);
        return affectedRows > 0;
    } catch (SQLException e) {
        LOGGER.log(Level.SEVERE, "Lỗi SQL khi cập nhật thẻ: " + e.getMessage(), e);
    }
    
    return false;
  }

  // Phương thức kiểm tra xem cột có tồn tại trong bảng không
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

  // Xóa thẻ
  public boolean delete(int id) {
    LOGGER.info("Deleting tag with ID: " + id);
    
    // Trước tiên, xóa các liên kết trong bảng post_tags
    try (Connection conn = DatabaseUtil.getConnection()) {
        conn.setAutoCommit(false);
        
        try {
            // Xóa liên kết với bài viết
            String deletePostTagsSql = "DELETE FROM post_tags WHERE tag_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deletePostTagsSql)) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
            }
            
            // Sau đó xóa thẻ
            String deleteTagSql = "DELETE FROM tags WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteTagSql)) {
                stmt.setInt(1, id);
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
        LOGGER.log(Level.SEVERE, "Lỗi khi xóa thẻ: " + id, e);
    }
    
    return false;
  }
}