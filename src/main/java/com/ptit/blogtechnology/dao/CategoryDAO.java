package com.ptit.blogtechnology.dao;

import com.ptit.blogtechnology.model.Category;
import com.ptit.blogtechnology.utils.DatabaseUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CategoryDAO {
    private static final Logger LOGGER = Logger.getLogger(CategoryDAO.class.getName());

    // Thay đổi access modifier từ private sang protected
    protected Category mapResultSetToCategory(ResultSet rs) throws SQLException {
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

    // Lấy tất cả danh mục
    public List<Category> findAll() {
        List<Category> categories = new ArrayList<>();

        String sql = "SELECT * FROM categories ORDER BY name";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Category category = mapResultSetToCategory(rs);
                categories.add(category);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy danh sách danh mục", e);
        }

        return categories;
    }

    // Tìm danh mục theo ID
    public Category findById(int id) {
        String sql = "SELECT * FROM categories WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCategory(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi tìm danh mục theo ID: " + id, e);
        }
        
        return null;
    }

    // Tìm danh mục theo slug
    public Category findBySlug(String slug) {
        Category category = null;

        String sql = "SELECT * FROM categories WHERE slug = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, slug);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    category = mapResultSetToCategory(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi tìm danh mục theo slug: " + slug, e);
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

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, postId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Category category = mapResultSetToCategory(rs);
                    categories.add(category);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy danh mục theo postId: " + postId, e);
        }

        return categories;
    }

    // Thêm danh mục mới
    public boolean save(Category category) {
        LOGGER.info("Saving category: " + category.getName());
        String sql = "INSERT INTO categories (name, slug, description, created_at, updated_at) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, category.getName());
            stmt.setString(2, category.getSlug());
            stmt.setString(3, category.getDescription());
            stmt.setTimestamp(4, Timestamp.valueOf(category.getCreatedAt()));
            stmt.setTimestamp(5, Timestamp.valueOf(category.getUpdatedAt()));
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        category.setId(generatedKeys.getInt(1));
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi thêm danh mục mới", e);
        }
        
        return false;
    }
    
    // Cập nhật danh mục
    public boolean update(Category category) {
        LOGGER.info("Updating category - ID: " + category.getId() + ", Name: " + category.getName());
        String sql = "UPDATE categories SET name = ?, slug = ?, description = ?, updated_at = ? WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, category.getName());
            stmt.setString(2, category.getSlug());
            stmt.setString(3, category.getDescription());
            stmt.setTimestamp(4, Timestamp.valueOf(category.getUpdatedAt()));
            stmt.setInt(5, category.getId());
            
            int affectedRows = stmt.executeUpdate();
            LOGGER.info("Update affected rows: " + affectedRows);
            return affectedRows > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi SQL khi cập nhật danh mục", e);
        }
        
        return false;
    }
    
    // Xóa danh mục
    public boolean delete(int id) {
        LOGGER.info("Deleting category with ID: " + id);
        String sql = "DELETE FROM categories WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi xóa danh mục", e);
        }
        
        return false;
    }
}