package com.ptit.blogtechnology.dao;

import com.ptit.blogtechnology.model.Category;
import com.ptit.blogtechnology.utils.DBUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {

  // Lấy tất cả danh mục
  public List<Category> findAll() {
    List<Category> categories = new ArrayList<>();

    String sql = "SELECT * FROM categories ORDER BY name";

    try (Connection conn = DBUtils.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery()) {

      while (rs.next()) {
        Category category = mapResultSetToCategory(rs);
        categories.add(category);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return categories;
  }

  // Tìm danh mục theo ID
  public Category findById(int id) {
    Category category = null;

    String sql = "SELECT * FROM categories WHERE id = ?";

    try (Connection conn = DBUtils.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setInt(1, id);

      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          category = mapResultSetToCategory(rs);
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return category;
  }

  // Tìm danh mục theo slug
  public Category findBySlug(String slug) {
    Category category = null;

    String sql = "SELECT * FROM categories WHERE slug = ?";

    try (Connection conn = DBUtils.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setString(1, slug);

      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          category = mapResultSetToCategory(rs);
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return category;
  }

  // Lấy danh mục của một bài viết
  public List<Category> findByPostId(int postId) {
    List<Category> categories = new ArrayList<>();

    String sql = "SELECT c.* FROM categories c " +
        "JOIN post_categories pc ON c.id = pc.category_id " +
        "WHERE pc.post_id = ? " +
        "ORDER BY c.name";

    try (Connection conn = DBUtils.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setInt(1, postId);

      try (ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
          Category category = mapResultSetToCategory(rs);
          categories.add(category);
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return categories;
  }

  // Phương thức hỗ trợ để chuyển ResultSet thành đối tượng Category
  private Category mapResultSetToCategory(ResultSet rs) throws SQLException {
    Category category = new Category();
    category.setId(rs.getInt("id"));
    category.setName(rs.getString("name"));
    category.setSlug(rs.getString("slug"));
    category.setDescription(rs.getString("description"));

    // Chuyển đổi Timestamp thành LocalDateTime
    Timestamp createdAt = rs.getTimestamp("created_at");
    if (createdAt != null) {
      category.setCreatedAt(createdAt.toLocalDateTime());
    }

    Timestamp updatedAt = rs.getTimestamp("updated_at");
    if (updatedAt != null) {
      category.setUpdatedAt(updatedAt.toLocalDateTime());
    }

    return category;
  }
}