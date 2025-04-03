package com.ptit.blogtechnology.dao;

import com.ptit.blogtechnology.model.Category;
import com.ptit.blogtechnology.model.Post;
import com.ptit.blogtechnology.model.Tag;
import com.ptit.blogtechnology.model.User;
import com.ptit.blogtechnology.utils.DatabaseUtil;
import com.ptit.blogtechnology.utils.SlugGenerator;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PostDAO {
  private static final Logger LOGGER = Logger.getLogger(PostDAO.class.getName());
  private CategoryDAO categoryDAO;
  private TagDAO tagDAO;
  private UserDAO userDAO;
  private static final int BATCH_SIZE = 100;

  public PostDAO() {
    categoryDAO = new CategoryDAO();
    tagDAO = new TagDAO();
    userDAO = new UserDAO();
  }

  public List<Post> findAll() {
    List<Post> posts = new ArrayList<>();
    String sql = "SELECT * FROM posts";

    try (Connection conn = DatabaseUtil.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql)) {

      while (rs.next()) {
        posts.add(mapResultSetToPost(rs));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return posts;
  }

  // Lấy danh sách bài viết đã xuất bản với phân trang
  public List<Post> findPublishedPosts(int page, int postsPerPage) {
    List<Post> posts = new ArrayList<>();
    int offset = (page - 1) * postsPerPage;

    String sql = "SELECT p.*, u.username, u.full_name, u.email, u.updated_at as user_updated_at FROM posts p " +
        "JOIN users u ON p.author_id = u.id " +
        "WHERE p.status = 'PUBLISHED' " +
        "ORDER BY p.published_at DESC " +
        "LIMIT ? OFFSET ?";

    try (Connection conn = DatabaseUtil.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, postsPerPage);
        stmt.setInt(2, offset);

        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Post post = mapResultSetToPost(rs);
                posts.add(post);
            }
        }

        // Lấy danh mục và tags cho tất cả bài viết trong một lần truy vấn
        if (!posts.isEmpty()) {
            loadCategoriesForPosts(conn, posts);
            loadTagsForPosts(conn, posts);
        }
    } catch (SQLException e) {
        LOGGER.log(Level.SEVERE, "Lỗi khi lấy danh sách bài viết đã xuất bản", e);
    }

    return posts;
  }

  // Đếm tổng số bài viết đã xuất bản
  public int countPublishedPosts() {
    int count = 0;
    String sql = "SELECT COUNT(*) FROM posts WHERE status = 'PUBLISHED'";

    try (Connection conn = DatabaseUtil.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery()) {

      if (rs.next()) {
        count = rs.getInt(1);
      }
    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, "Lỗi khi đếm số bài viết đã xuất bản", e);
    }

    return count;
  }

  // Lấy danh sách bài viết nổi bật
  public List<Post> findFeaturedPosts(int limit) {
    List<Post> posts = new ArrayList<>();

    String sql = "SELECT p.*, u.username, u.full_name, u.email, u.updated_at as user_updated_at FROM posts p " +
        "JOIN users u ON p.author_id = u.id " +
        "WHERE p.status = 'PUBLISHED' AND p.is_featured = true " +
        "ORDER BY p.published_at DESC " +
        "LIMIT ?";

    try (Connection conn = DatabaseUtil.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, limit);

        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Post post = mapResultSetToPost(rs);
                posts.add(post);
            }
        }

        // Lấy danh mục và tags cho tất cả bài viết trong một lần truy vấn
        if (!posts.isEmpty()) {
            loadCategoriesForPosts(conn, posts);
            loadTagsForPosts(conn, posts);
        }
    } catch (SQLException e) {
        LOGGER.log(Level.SEVERE, "Lỗi khi lấy danh sách bài viết nổi bật", e);
    }

    return posts;
  }

  // Tìm bài viết theo slug
  public Post findBySlug(String slug) {
    Post post = null;

    String sql = "SELECT p.*, u.username, u.full_name, u.email, u.updated_at as user_updated_at FROM posts p " +
        "JOIN users u ON p.author_id = u.id " +
        "WHERE p.slug = ?";

    try (Connection conn = DatabaseUtil.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setString(1, slug);

      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          post = mapResultSetToPost(rs);

          // Lấy danh mục cho bài viết
          post.setCategories(categoryDAO.findByPostId(post.getId()));

          // Lấy tags cho bài viết
          post.setTags(tagDAO.findByPostId(post.getId()));
        }
      }
    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, "Lỗi khi tìm bài viết theo slug: " + slug, e);
    }

    return post;
  }

  // Tìm bài viết theo ID
  public Post findById(int id) {
    String sql = "SELECT * FROM posts WHERE id = ?";
    
    try (Connection conn = DatabaseUtil.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        stmt.setInt(1, id);
        
        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                Post post = mapResultSetToPost(rs);
                
                // Lấy danh mục và tags
                loadCategoriesForPost(post);
                loadTagsForPost(post);
                
                // Lấy thông tin tác giả
                if (post.getAuthorId() > 0) {
                    User author = userDAO.findById(Long.valueOf(post.getAuthorId()));
                    post.setAuthor(author);
                }
                
                return post;
            }
        }
    } catch (SQLException e) {
        LOGGER.log(Level.SEVERE, "Lỗi khi tìm bài viết theo ID: " + id, e);
    }
    
    return null;
  }

  // Tăng lượt xem bài viết
  public boolean incrementViewCount(int postId) {
    String sql = "UPDATE posts SET view_count = view_count + 1 WHERE id = ?";

    try (Connection conn = DatabaseUtil.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setInt(1, postId);

      int affectedRows = stmt.executeUpdate();
      return affectedRows > 0;

    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, "Lỗi khi tăng lượt xem bài viết ID: " + postId, e);
    }

    return false;
  }

  // Tìm bài viết liên quan
  public List<Post> findRelatedPosts(int postId, int limit) {
    List<Post> relatedPosts = new ArrayList<>();

    // Tìm bài viết cùng danh mục
    String sql = "SELECT DISTINCT p.*, u.username, u.full_name, u.email, u.updated_at as user_updated_at FROM posts p " +
        "JOIN post_categories pc1 ON p.id = pc1.post_id " +
        "JOIN post_categories pc2 ON pc1.category_id = pc2.category_id " +
        "JOIN users u ON p.author_id = u.id " +
        "WHERE pc2.post_id = ? AND p.id != ? AND p.status = 'PUBLISHED' " +
        "ORDER BY p.published_at DESC " +
        "LIMIT ?";

    try (Connection conn = DatabaseUtil.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setInt(1, postId);
      stmt.setInt(2, postId);
      stmt.setInt(3, limit);

      try (ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
          Post post = mapResultSetToPost(rs);
          relatedPosts.add(post);
        }
      }

      // Lấy danh mục và tags cho tất cả bài viết trong một lần truy vấn
      if (!relatedPosts.isEmpty()) {
        loadCategoriesForPosts(conn, relatedPosts);
        loadTagsForPosts(conn, relatedPosts);
      }
    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, "Lỗi khi tìm bài viết liên quan cho ID: " + postId, e);
    }

    return relatedPosts;
  }

  // Tìm kiếm bài viết
  public List<Post> searchPosts(String query, int page, int postsPerPage) {
    List<Post> searchResults = new ArrayList<>();
    int offset = (page - 1) * postsPerPage;

    String sql = "SELECT p.*, u.username, u.full_name FROM posts p " +
        "JOIN users u ON p.author_id = u.id " +
        "WHERE p.status = 'PUBLISHED' AND " +
        "(p.title LIKE ? OR p.content LIKE ? OR p.summary LIKE ?) " +
        "ORDER BY p.published_at DESC " +
        "LIMIT ? OFFSET ?";

    try (Connection conn = DatabaseUtil.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      String searchPattern = "%" + query + "%";
      stmt.setString(1, searchPattern);
      stmt.setString(2, searchPattern);
      stmt.setString(3, searchPattern);
      stmt.setInt(4, postsPerPage);
      stmt.setInt(5, offset);

      try (ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
          Post post = mapResultSetToPost(rs);
          searchResults.add(post);
        }
      }

      // Lấy danh mục và tags cho tất cả bài viết trong một lần truy vấn
      if (!searchResults.isEmpty()) {
        loadCategoriesForPosts(conn, searchResults);
        loadTagsForPosts(conn, searchResults);
      }
    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, "Lỗi khi tìm kiếm bài viết với từ khóa: " + query, e);
    }

    return searchResults;
  }

  // Đếm số kết quả tìm kiếm
  public int countSearchResults(String query) {
    int count = 0;

    String sql = "SELECT COUNT(*) FROM posts " +
        "WHERE status = 'PUBLISHED' AND " +
        "(title LIKE ? OR content LIKE ? OR summary LIKE ?)";

    try (Connection conn = DatabaseUtil.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      String searchPattern = "%" + query + "%";
      stmt.setString(1, searchPattern);
      stmt.setString(2, searchPattern);
      stmt.setString(3, searchPattern);

      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          count = rs.getInt(1);
        }
      }
    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, "Lỗi khi đếm kết quả tìm kiếm với từ khóa: " + query, e);
    }

    return count;
  }

  // Lấy bài viết theo danh mục
  public List<Post> findByCategory(int categoryId, int page, int postsPerPage) {
    List<Post> posts = new ArrayList<>();
    int offset = (page - 1) * postsPerPage;

    String sql = "SELECT p.*, u.username, u.full_name FROM posts p " +
        "JOIN post_categories pc ON p.id = pc.post_id " +
        "JOIN users u ON p.author_id = u.id " +
        "WHERE pc.category_id = ? AND p.status = 'PUBLISHED' " +
        "ORDER BY p.published_at DESC " +
        "LIMIT ? OFFSET ?";

    try (Connection conn = DatabaseUtil.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setInt(1, categoryId);
      stmt.setInt(2, postsPerPage);
      stmt.setInt(3, offset);

      try (ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
          Post post = mapResultSetToPost(rs);
          posts.add(post);
        }
      }

      // Lấy danh mục và tags cho tất cả bài viết trong một lần truy vấn
      if (!posts.isEmpty()) {
        loadCategoriesForPosts(conn, posts);
        loadTagsForPosts(conn, posts);
      }
    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, "Lỗi khi lấy bài viết theo danh mục ID: " + categoryId, e);
    }

    return posts;
  }

  // Đếm số bài viết theo danh mục
  public int countByCategory(int categoryId) {
    int count = 0;

    String sql = "SELECT COUNT(*) FROM posts p " +
        "JOIN post_categories pc ON p.id = pc.post_id " +
        "WHERE pc.category_id = ? AND p.status = 'PUBLISHED'";

    try (Connection conn = DatabaseUtil.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setInt(1, categoryId);

      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          count = rs.getInt(1);
        }
      }
    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, "Lỗi khi đếm bài viết theo danh mục ID: " + categoryId, e);
    }

    return count;
  }

  // Lấy bài viết theo tag
  public List<Post> findByTag(int tagId, int page, int postsPerPage) {
    List<Post> posts = new ArrayList<>();
    int offset = (page - 1) * postsPerPage;

    String sql = "SELECT p.*, u.username, u.full_name FROM posts p " +
        "JOIN post_tags pt ON p.id = pt.post_id " +
        "JOIN users u ON p.author_id = u.id " +
        "WHERE pt.tag_id = ? AND p.status = 'PUBLISHED' " +
        "ORDER BY p.published_at DESC " +
        "LIMIT ? OFFSET ?";

    try (Connection conn = DatabaseUtil.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setInt(1, tagId);
      stmt.setInt(2, postsPerPage);
      stmt.setInt(3, offset);

      try (ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
          Post post = mapResultSetToPost(rs);
          posts.add(post);
        }
      }

      // Lấy danh mục và tags cho tất cả bài viết trong một lần truy vấn
      if (!posts.isEmpty()) {
        loadCategoriesForPosts(conn, posts);
        loadTagsForPosts(conn, posts);
      }
    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, "Lỗi khi lấy bài viết theo tag ID: " + tagId, e);
    }

    return posts;
  }

  // Đếm số bài viết theo tag
  public int countByTag(int tagId) {
    int count = 0;

    String sql = "SELECT COUNT(*) FROM posts p " +
        "JOIN post_tags pt ON p.id = pt.post_id " +
        "WHERE pt.tag_id = ? AND p.status = 'PUBLISHED'";

    try (Connection conn = DatabaseUtil.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setInt(1, tagId);

      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          count = rs.getInt(1);
        }
      }
    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, "Lỗi khi đếm bài viết theo tag ID: " + tagId, e);
    }

    return count;
  }

  // Lấy bài viết mới nhất
  public List<Post> findLatestPosts(int limit) {
    List<Post> posts = new ArrayList<>();

    String sql = "SELECT p.*, u.username, u.full_name, u.email, u.updated_at as user_updated_at FROM posts p " +
        "JOIN users u ON p.author_id = u.id " +
        "WHERE p.status = 'PUBLISHED' " +
        "ORDER BY p.published_at DESC " +
        "LIMIT ?";

    try (Connection conn = DatabaseUtil.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setInt(1, limit);

      try (ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
          Post post = mapResultSetToPost(rs);
          posts.add(post);
        }
      }

      // Lấy danh mục và tags cho tất cả bài viết trong một lần truy vấn
      if (!posts.isEmpty()) {
        loadCategoriesForPosts(conn, posts);
        loadTagsForPosts(conn, posts);
      }
    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, "Lỗi khi lấy bài viết mới nhất", e);
    }

    return posts;
  }

  // Lấy bài viết phổ biến nhất (nhiều lượt xem nhất)
  public List<Post> findPopularPosts(int limit) {
    List<Post> posts = new ArrayList<>();

    String sql = "SELECT p.*, u.username, u.full_name, u.email, u.updated_at as user_updated_at FROM posts p " +
        "JOIN users u ON p.author_id = u.id " +
        "WHERE p.status = 'PUBLISHED' " +
        "ORDER BY p.view_count DESC " +
        "LIMIT ?";

    try (Connection conn = DatabaseUtil.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setInt(1, limit);

      try (ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
          Post post = mapResultSetToPost(rs);
          posts.add(post);
        }
      }

      // Lấy danh mục và tags cho tất cả bài viết trong một lần truy vấn
      if (!posts.isEmpty()) {
        loadCategoriesForPosts(conn, posts);
        loadTagsForPosts(conn, posts);
      }
    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, "Lỗi khi lấy bài viết phổ biến nhất", e);
    }

    return posts;
  }

  // Lấy bài viết mới nhất
  public boolean save(Post post) {
    LOGGER.info("Saving post: " + post.getTitle());
    
    String sql = "INSERT INTO posts (title, slug, content, featured_image, status, author_id, is_featured, view_count, created_at, updated_at, published_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    
    try (Connection conn = DatabaseUtil.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        
        stmt.setString(1, post.getTitle());
        stmt.setString(2, post.getSlug());
        stmt.setString(3, post.getContent());
        stmt.setString(4, post.getFeaturedImage());
        stmt.setString(5, post.getStatus());
        stmt.setInt(6, post.getAuthorId());
        stmt.setBoolean(7, post.isFeatured());
        stmt.setInt(8, 0); // view_count mặc định là 0
        stmt.setTimestamp(9, Timestamp.valueOf(post.getCreatedAt()));
        stmt.setTimestamp(10, Timestamp.valueOf(post.getUpdatedAt()));
        
        if (post.getPublishedAt() != null) {
            stmt.setTimestamp(11, Timestamp.valueOf(post.getPublishedAt()));
        } else {
            stmt.setNull(11, Types.TIMESTAMP);
        }
        
        int affectedRows = stmt.executeUpdate();
        
        if (affectedRows > 0) {
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    post.setId(generatedKeys.getInt(1));
                    return true;
                }
            }
        }
    } catch (SQLException e) {
        LOGGER.log(Level.SEVERE, "Lỗi khi lưu bài viết: " + e.getMessage(), e);
    }
    
    return false;
  }

  // Cập nhật bài viết (bao gồm danh mục và thẻ)
  public boolean update(Post post, List<Integer> categoryIds, List<Integer> tagIds) {
    LOGGER.info("Updating post - ID: " + post.getId() + ", Title: " + post.getTitle());
    
    Connection conn = null;
    try {
        conn = DatabaseUtil.getConnection();
        conn.setAutoCommit(false);
        
        // Cập nhật bài viết
        String sql = "UPDATE posts SET title = ?, slug = ?, content = ?, summary = ?, featured_image = ?, status = ?, is_featured = ?, updated_at = ?, published_at = ? WHERE id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, post.getTitle());
            stmt.setString(2, post.getSlug());
            stmt.setString(3, post.getContent());
            stmt.setString(4, post.getSummary());
            stmt.setString(5, post.getFeaturedImage());
            stmt.setString(6, post.getStatus());
            stmt.setBoolean(7, post.isFeatured());
            stmt.setTimestamp(8, Timestamp.valueOf(post.getUpdatedAt()));
            
            if (post.getPublishedAt() != null) {
                stmt.setTimestamp(9, Timestamp.valueOf(post.getPublishedAt()));
            } else {
                stmt.setNull(9, Types.TIMESTAMP);
            }
            
            stmt.setInt(10, post.getId());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                // Xóa danh mục cũ
                deletePostCategories(conn, post.getId());
                
                // Lưu danh mục mới
                for (Integer categoryId : categoryIds) {
                    savePostCategory(conn, post.getId(), categoryId);
                }
                
                // Xóa thẻ cũ
                deletePostTags(conn, post.getId());
                
                // Lưu thẻ mới
                for (Integer tagId : tagIds) {
                    savePostTag(conn, post.getId(), tagId);
                }
                
                conn.commit();
                return true;
            }
        }
        
        conn.rollback();
    } catch (SQLException e) {
        LOGGER.log(Level.SEVERE, "Lỗi khi cập nhật bài viết: " + e.getMessage(), e);
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, "Lỗi khi rollback: " + ex.getMessage(), ex);
            }
        }
    } finally {
        if (conn != null) {
            try {
                conn.setAutoCommit(true);
                conn.close();
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Lỗi khi đóng kết nối: " + e.getMessage(), e);
            }
        }
    }
    
    return false;
  }

  // Xóa bài viết
  public boolean delete(int id) {
    // Xóa các liên kết trong bảng post_categories và post_tags trước
    try (Connection conn = DatabaseUtil.getConnection()) {
        conn.setAutoCommit(false);
        
        try {
            // Xóa liên kết với danh mục
            String deleteCategoriesSql = "DELETE FROM post_categories WHERE post_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteCategoriesSql)) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
            }
            
            // Xóa liên kết với tags
            String deleteTagsSql = "DELETE FROM post_tags WHERE post_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteTagsSql)) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
            }
            
            // Xóa các bình luận
            String deleteCommentsSql = "DELETE FROM comments WHERE post_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteCommentsSql)) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
            }
            
            // Xóa bài viết
            String deletePostSql = "DELETE FROM posts WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deletePostSql)) {
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
        LOGGER.log(Level.SEVERE, "Lỗi khi xóa bài viết: " + id, e);
    }
    
    return false;
  }

  // Cập nhật trạng thái bài viết
  public boolean updateStatus(int id, String status) {
    String sql = "UPDATE posts SET status = ?, updated_at = ?";
    
    // Nếu trạng thái là PUBLISHED, cập nhật published_at
    if ("PUBLISHED".equals(status)) {
        sql += ", published_at = ?";
    }
    
    sql += " WHERE id = ?";
    
    try (Connection conn = DatabaseUtil.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        LocalDateTime now = LocalDateTime.now();
        stmt.setString(1, status);
        stmt.setTimestamp(2, Timestamp.valueOf(now));
        
        if ("PUBLISHED".equals(status)) {
            stmt.setTimestamp(3, Timestamp.valueOf(now));
            stmt.setInt(4, id);
        } else {
            stmt.setInt(3, id);
        }
        
        int affectedRows = stmt.executeUpdate();
        return affectedRows > 0;
    } catch (SQLException e) {
        LOGGER.log(Level.SEVERE, "Lỗi khi cập nhật trạng thái bài viết: " + id, e);
    }
    
    return false;
  }

  // Phương thức hỗ trợ để chuyển ResultSet thành đối tượng Post
  private Post mapResultSetToPost(ResultSet rs) throws SQLException {
    Post post = new Post();
    post.setId(rs.getInt("id"));
    post.setTitle(rs.getString("title"));
    post.setSlug(rs.getString("slug"));
    post.setContent(rs.getString("content"));
    
    // Kiểm tra xem cột summary có tồn tại không
    try {
        post.setSummary(rs.getString("summary"));
    } catch (SQLException e) {
        // Bỏ qua nếu không có cột summary
    }
    
    post.setFeaturedImage(rs.getString("featured_image"));
    post.setAuthorId(rs.getInt("author_id"));
    post.setStatus(rs.getString("status"));
    post.setFeatured(rs.getBoolean("is_featured"));
    post.setViewCount(rs.getInt("view_count"));
    
    // Lấy thời gian
    Timestamp createdAt = rs.getTimestamp("created_at");
    if (createdAt != null) {
        post.setCreatedAt(createdAt.toLocalDateTime());
    }
    
    Timestamp updatedAt = rs.getTimestamp("updated_at");
    if (updatedAt != null) {
        post.setUpdatedAt(updatedAt.toLocalDateTime());
    }
    
    Timestamp publishedAt = rs.getTimestamp("published_at");
    if (publishedAt != null) {
        post.setPublishedAt(publishedAt.toLocalDateTime());
    }
    
    // Lấy thông tin tác giả nếu có
    try {
        User author = new User();
        author.setUsername(rs.getString("username"));
        author.setFullName(rs.getString("full_name"));
        author.setEmail(rs.getString("email"));
        
        Timestamp userUpdatedAt = rs.getTimestamp("user_updated_at");
        if (userUpdatedAt != null) {
            author.setUpdatedAt(userUpdatedAt.toLocalDateTime());
        }
        
        post.setAuthor(author);
    } catch (SQLException e) {
        // Bỏ qua nếu không có thông tin tác giả
        LOGGER.log(Level.FINE, "Không có thông tin tác giả trong kết quả truy vấn", e);
    }
    
    return post;
  }

  // Phương thức để load danh mục cho nhiều bài viết cùng lúc
  private void loadCategoriesForPosts(Connection conn, List<Post> posts) throws SQLException {
    if (posts.isEmpty()) return;
    
    // Lấy tất cả ID của bài viết
    StringBuilder postIds = new StringBuilder();
    for (int i = 0; i < posts.size(); i++) {
        if (i > 0) postIds.append(",");
        postIds.append(posts.get(i).getId());
    }
    
    // Truy vấn để lấy tất cả danh mục cho các bài viết
    String sql = "SELECT pc.post_id, c.* FROM categories c " +
                 "JOIN post_categories pc ON c.id = pc.category_id " +
                 "WHERE pc.post_id IN (" + postIds.toString() + ") " +
                 "ORDER BY c.name";
    
    try (PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {
        
        // Tạo map để lưu danh mục cho từng bài viết
        Map<Integer, List<Category>> postCategoriesMap = new HashMap<>();
        
        while (rs.next()) {
            int postId = rs.getInt("post_id");
            Category category = categoryDAO.mapResultSetToCategory(rs);
            
            // Thêm danh mục vào map
            postCategoriesMap.computeIfAbsent(postId, k -> new ArrayList<>()).add(category);
        }
        
        // Gán danh mục cho từng bài viết
        for (Post post : posts) {
            List<Category> categories = postCategoriesMap.getOrDefault(post.getId(), new ArrayList<>());
            post.setCategories(categories);
        }
    }
  }

  // Phương thức để load tags cho nhiều bài viết cùng lúc
  private void loadTagsForPosts(Connection conn, List<Post> posts) throws SQLException {
    if (posts.isEmpty()) return;
    
    // Lấy tất cả ID của bài viết
    StringBuilder postIds = new StringBuilder();
    for (int i = 0; i < posts.size(); i++) {
        if (i > 0) postIds.append(",");
        postIds.append(posts.get(i).getId());
    }
    
    // Truy vấn để lấy tất cả tags cho các bài viết
    String sql = "SELECT pt.post_id, t.* FROM tags t " +
                 "JOIN post_tags pt ON t.id = pt.tag_id " +
                 "WHERE pt.post_id IN (" + postIds.toString() + ") " +
                 "ORDER BY t.name";
    
    try (PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {
        
        // Tạo map để lưu tags cho từng bài viết
        Map<Integer, List<Tag>> postTagsMap = new HashMap<>();
        
        while (rs.next()) {
            int postId = rs.getInt("post_id");
            Tag tag = tagDAO.mapResultSetToTag(rs);
            
            // Thêm tag vào map
            postTagsMap.computeIfAbsent(postId, k -> new ArrayList<>()).add(tag);
        }
        
        // Gán tags cho từng bài viết
        for (Post post : posts) {
            List<Tag> tags = postTagsMap.getOrDefault(post.getId(), new ArrayList<>());
            post.setTags(tags);
        }
    }
  }

  // Phương thức tối ưu để lưu danh mục cho bài viết
  private void saveCategoriesForPost(Connection conn, Post post) throws SQLException {
    String categorySql = "INSERT INTO post_categories (post_id, category_id) VALUES (?, ?)";
    try (PreparedStatement categoryStmt = conn.prepareStatement(categorySql)) {
      for (Category category : post.getCategories()) {
        categoryStmt.setInt(1, post.getId());
        categoryStmt.setInt(2, category.getId());
        categoryStmt.addBatch();
      }
      categoryStmt.executeBatch();
    }
  }

  // Phương thức tối ưu để lưu tags cho bài viết
  private void saveTagsForPost(Connection conn, Post post) throws SQLException {
    // Lưu tags mới
    for (Tag tag : post.getTags()) {
      // Kiểm tra tag đã tồn tại chưa
      Tag existingTag = tagDAO.findByName(tag.getName());
      if (existingTag == null) {
        // Tạo tag mới
        tag.setSlug(SlugGenerator.toSlug(tag.getName()));
        tag.setCreatedAt(LocalDateTime.now());
        tag.setUpdatedAt(LocalDateTime.now());
        tagDAO.save(tag);
      } else {
        tag.setId(existingTag.getId());
      }
    }

    // Liên kết tags với bài viết
    String tagSql = "INSERT INTO post_tags (post_id, tag_id) VALUES (?, ?)";
    try (PreparedStatement tagStmt = conn.prepareStatement(tagSql)) {
      for (Tag tag : post.getTags()) {
        tagStmt.setInt(1, post.getId());
        tagStmt.setInt(2, tag.getId());
        tagStmt.addBatch();
      }
      tagStmt.executeBatch();
    }
  }

  // Phương thức đóng tài nguyên
  private void closeResources(ResultSet rs, PreparedStatement stmt, Connection conn) {
    if (rs != null) {
      try {
        rs.close();
      } catch (SQLException e) {
        LOGGER.log(Level.WARNING, "Lỗi khi đóng ResultSet", e);
      }
    }
    if (stmt != null) {
      try {
        stmt.close();
      } catch (SQLException e) {
        LOGGER.log(Level.WARNING, "Lỗi khi đóng PreparedStatement", e);
      }
    }
    if (conn != null) {
      try {
        conn.setAutoCommit(true);
        conn.close();
      } catch (SQLException e) {
        LOGGER.log(Level.WARNING, "Lỗi khi đóng Connection", e);
      }
    }
  }

  // Lấy bài viết theo tác giả
  public List<Post> findByAuthor(int authorId, int page, int postsPerPage) {
    List<Post> posts = new ArrayList<>();
    int offset = (page - 1) * postsPerPage;

    String sql = "SELECT p.*, u.username, u.full_name FROM posts p " +
        "JOIN users u ON p.author_id = u.id " +
        "WHERE p.author_id = ? " +
        "ORDER BY p.created_at DESC " +
        "LIMIT ? OFFSET ?";

    try (Connection conn = DatabaseUtil.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setInt(1, authorId);
      stmt.setInt(2, postsPerPage);
      stmt.setInt(3, offset);

      try (ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
          Post post = mapResultSetToPost(rs);
          posts.add(post);
        }
      }

      // Lấy danh mục và tags cho tất cả bài viết trong một lần truy vấn
      if (!posts.isEmpty()) {
        loadCategoriesForPosts(conn, posts);
        loadTagsForPosts(conn, posts);
      }
    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, "Lỗi khi lấy danh sách bài viết theo tác giả", e);
    }

    return posts;
  }

  // Đếm số bài viết theo tác giả
  public int countByAuthor(int authorId) {
    int count = 0;

    String sql = "SELECT COUNT(*) FROM posts WHERE author_id = ?";

    try (Connection conn = DatabaseUtil.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setInt(1, authorId);

      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          count = rs.getInt(1);
        }
      }
    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, "Lỗi khi đếm số bài viết theo tác giả", e);
    }

    return count;
  }

  // Đếm tổng số bài viết theo bộ lọc
  public int countPosts(String status, Integer categoryId, String search) {
    int count = 0;
    
    StringBuilder sqlBuilder = new StringBuilder();
    sqlBuilder.append("SELECT COUNT(DISTINCT p.id) FROM posts p ");
    
    // Thêm JOIN với bảng post_categories nếu có lọc theo danh mục
    if (categoryId != null) {
        sqlBuilder.append("JOIN post_categories pc ON p.id = pc.post_id ");
    }
    
    // Thêm điều kiện WHERE
    List<Object> params = new ArrayList<>();
    sqlBuilder.append("WHERE 1=1 ");
    
    if (status != null && !status.isEmpty()) {
        sqlBuilder.append("AND p.status = ? ");
        params.add(status);
    }
    
    if (categoryId != null) {
        sqlBuilder.append("AND pc.category_id = ? ");
        params.add(categoryId);
    }
    
    if (search != null && !search.isEmpty()) {
        sqlBuilder.append("AND (p.title LIKE ? OR p.content LIKE ?) ");
        String searchPattern = "%" + search + "%";
        params.add(searchPattern);
        params.add(searchPattern);
    }
    
    String sql = sqlBuilder.toString();
    LOGGER.info("Count SQL query: " + sql);
    
    try (Connection conn = DatabaseUtil.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        // Đặt các tham số
        for (int i = 0; i < params.size(); i++) {
            Object param = params.get(i);
            if (param instanceof String) {
                stmt.setString(i + 1, (String) param);
            } else if (param instanceof Integer) {
                stmt.setInt(i + 1, (Integer) param);
            }
        }
        
        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                count = rs.getInt(1);
            }
        }
    } catch (SQLException e) {
        LOGGER.log(Level.SEVERE, "Lỗi khi đếm số bài viết", e);
    }
    
    return count;
  }

  // Lấy danh sách bài viết có phân trang và lọc
  public List<Post> findAllWithFilters(int page, int postsPerPage, String status, Integer categoryId, String search) {
    List<Post> posts = new ArrayList<>();
    int offset = (page - 1) * postsPerPage;
    
    StringBuilder sqlBuilder = new StringBuilder();
    // Thêm JOIN với bảng users để lấy thông tin tác giả
    sqlBuilder.append("SELECT p.*, u.username, u.full_name, u.email, u.updated_at as user_updated_at FROM posts p ");
    sqlBuilder.append("JOIN users u ON p.author_id = u.id ");
    
    // Thêm JOIN với bảng post_categories nếu có lọc theo danh mục
    if (categoryId != null) {
        sqlBuilder.append("JOIN post_categories pc ON p.id = pc.post_id ");
    }
    
    // Thêm điều kiện WHERE
    List<Object> params = new ArrayList<>();
    sqlBuilder.append("WHERE 1=1 ");
    
    if (status != null && !status.isEmpty()) {
        sqlBuilder.append("AND p.status = ? ");
        params.add(status);
    }
    
    if (categoryId != null) {
        sqlBuilder.append("AND pc.category_id = ? ");
        params.add(categoryId);
    }
    
    if (search != null && !search.isEmpty()) {
        sqlBuilder.append("AND (p.title LIKE ? OR p.content LIKE ?) ");
        String searchPattern = "%" + search + "%";
        params.add(searchPattern);
        params.add(searchPattern);
    }
    
    // Thêm ORDER BY, LIMIT và OFFSET
    sqlBuilder.append("ORDER BY p.created_at DESC ");
    sqlBuilder.append("LIMIT ? OFFSET ?");
    params.add(postsPerPage);
    params.add(offset);
    
    String sql = sqlBuilder.toString();
    LOGGER.info("SQL query: " + sql);
    
    try (Connection conn = DatabaseUtil.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        // Đặt các tham số
        for (int i = 0; i < params.size(); i++) {
            Object param = params.get(i);
            if (param instanceof String) {
                stmt.setString(i + 1, (String) param);
            } else if (param instanceof Integer) {
                stmt.setInt(i + 1, (Integer) param);
            }
        }
        
        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Post post = mapResultSetToPost(rs);
                posts.add(post);
            }
        }
        
        // Lấy danh mục và tags cho tất cả bài viết trong một lần truy vấn
        if (!posts.isEmpty()) {
            loadCategoriesForPosts(conn, posts);
            loadTagsForPosts(conn, posts);
        }
    } catch (SQLException e) {
        LOGGER.log(Level.SEVERE, "Lỗi khi lấy danh sách bài viết có lọc", e);
    }
    
    return posts;
  }

  // Lấy danh sách bài viết của tác giả có phân trang và lọc
  public List<Post> findByAuthorWithFilters(int authorId, int page, int postsPerPage, String status, Integer categoryId, String search) {
    List<Post> posts = new ArrayList<>();
    int offset = (page - 1) * postsPerPage;
    
    StringBuilder sqlBuilder = new StringBuilder();
    // Thêm JOIN với bảng users để lấy thông tin tác giả
    sqlBuilder.append("SELECT p.*, u.username, u.full_name, u.email, u.updated_at as user_updated_at FROM posts p ");
    sqlBuilder.append("JOIN users u ON p.author_id = u.id ");
    
    // Thêm JOIN với bảng post_categories nếu có lọc theo danh mục
    if (categoryId != null) {
        sqlBuilder.append("JOIN post_categories pc ON p.id = pc.post_id ");
    }
    
    // Thêm điều kiện WHERE
    List<Object> params = new ArrayList<>();
    sqlBuilder.append("WHERE p.author_id = ? ");
    params.add(authorId);
    
    if (status != null && !status.isEmpty()) {
        sqlBuilder.append("AND p.status = ? ");
        params.add(status);
    }
    
    if (categoryId != null) {
        sqlBuilder.append("AND pc.category_id = ? ");
        params.add(categoryId);
    }
    
    if (search != null && !search.isEmpty()) {
        sqlBuilder.append("AND (p.title LIKE ? OR p.content LIKE ?) ");
        String searchPattern = "%" + search + "%";
        params.add(searchPattern);
        params.add(searchPattern);
    }
    
    // Thêm ORDER BY, LIMIT và OFFSET
    sqlBuilder.append("ORDER BY p.created_at DESC ");
    sqlBuilder.append("LIMIT ? OFFSET ?");
    params.add(postsPerPage);
    params.add(offset);
    
    String sql = sqlBuilder.toString();
    LOGGER.info("SQL query: " + sql);
    
    try (Connection conn = DatabaseUtil.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        // Đặt các tham số
        for (int i = 0; i < params.size(); i++) {
            Object param = params.get(i);
            if (param instanceof String) {
                stmt.setString(i + 1, (String) param);
            } else if (param instanceof Integer) {
                stmt.setInt(i + 1, (Integer) param);
            }
        }
        
        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Post post = mapResultSetToPost(rs);
                posts.add(post);
            }
        }
        
        // Lấy danh mục và tags cho tất cả bài viết trong một lần truy vấn
        if (!posts.isEmpty()) {
            loadCategoriesForPosts(conn, posts);
            loadTagsForPosts(conn, posts);
        }
    } catch (SQLException e) {
        LOGGER.log(Level.SEVERE, "Lỗi khi lấy danh sách bài viết của tác giả có lọc", e);
    }
    
    return posts;
  }

  // Phương thức để load danh mục cho một bài viết
  private void loadCategoriesForPost(Post post) {
    String sql = "SELECT c.* FROM categories c " +
                 "JOIN post_categories pc ON c.id = pc.category_id " +
                 "WHERE pc.post_id = ?";
    
    try (Connection conn = DatabaseUtil.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        stmt.setInt(1, post.getId());
        
        try (ResultSet rs = stmt.executeQuery()) {
            List<Category> categories = new ArrayList<>();
            while (rs.next()) {
                Category category = categoryDAO.mapResultSetToCategory(rs);
                categories.add(category);
            }
            post.setCategories(categories);
        }
    } catch (SQLException e) {
        LOGGER.log(Level.SEVERE, "Lỗi khi load danh mục cho bài viết ID: " + post.getId(), e);
    }
  }

  // Phương thức để load tags cho một bài viết
  private void loadTagsForPost(Post post) {
    String sql = "SELECT t.* FROM tags t " +
                 "JOIN post_tags pt ON t.id = pt.tag_id " +
                 "WHERE pt.post_id = ?";
    
    try (Connection conn = DatabaseUtil.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        stmt.setInt(1, post.getId());
        
        try (ResultSet rs = stmt.executeQuery()) {
            List<Tag> tags = new ArrayList<>();
            while (rs.next()) {
                Tag tag = tagDAO.mapResultSetToTag(rs);
                tags.add(tag);
            }
            post.setTags(tags);
        }
    } catch (SQLException e) {
        LOGGER.log(Level.SEVERE, "Lỗi khi load tags cho bài viết ID: " + post.getId(), e);
    }
  }

  // Phương thức lưu danh mục cho bài viết
  public boolean savePostCategory(int postId, int categoryId) {
    LOGGER.info("Saving category " + categoryId + " for post " + postId);
    
    String sql = "INSERT INTO post_categories (post_id, category_id) VALUES (?, ?)";
    
    try (Connection conn = DatabaseUtil.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        stmt.setInt(1, postId);
        stmt.setInt(2, categoryId);
        
        int affectedRows = stmt.executeUpdate();
        return affectedRows > 0;
    } catch (SQLException e) {
        LOGGER.log(Level.SEVERE, "Lỗi khi lưu danh mục cho bài viết: " + e.getMessage(), e);
    }
    
    return false;
  }

  // Phương thức lưu thẻ cho bài viết
  public boolean savePostTag(int postId, int tagId) {
    LOGGER.info("Saving tag " + tagId + " for post " + postId);
    
    String sql = "INSERT INTO post_tags (post_id, tag_id) VALUES (?, ?)";
    
    try (Connection conn = DatabaseUtil.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        stmt.setInt(1, postId);
        stmt.setInt(2, tagId);
        
        int affectedRows = stmt.executeUpdate();
        return affectedRows > 0;
    } catch (SQLException e) {
        LOGGER.log(Level.SEVERE, "Lỗi khi lưu thẻ cho bài viết: " + e.getMessage(), e);
    }
    
    return false;
  }

  // Phương thức xóa danh mục của bài viết
  private void deletePostCategories(Connection conn, int postId) throws SQLException {
    String sql = "DELETE FROM post_categories WHERE post_id = ?";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, postId);
        stmt.executeUpdate();
    }
  }

  // Phương thức xóa thẻ của bài viết
  private void deletePostTags(Connection conn, int postId) throws SQLException {
    String sql = "DELETE FROM post_tags WHERE post_id = ?";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, postId);
        stmt.executeUpdate();
    }
  }

  // Phương thức lưu danh mục cho bài viết (sử dụng trong transaction)
  private void savePostCategory(Connection conn, int postId, int categoryId) throws SQLException {
    String sql = "INSERT INTO post_categories (post_id, category_id) VALUES (?, ?)";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, postId);
        stmt.setInt(2, categoryId);
        stmt.executeUpdate();
    }
  }

  // Phương thức lưu thẻ cho bài viết (sử dụng trong transaction)
  private void savePostTag(Connection conn, int postId, int tagId) throws SQLException {
    String sql = "INSERT INTO post_tags (post_id, tag_id) VALUES (?, ?)";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, postId);
        stmt.setInt(2, tagId);
        stmt.executeUpdate();
    }
  }

  // Thêm phương thức public để xóa danh mục của bài viết
  public boolean deletePostCategories(int postId) {
    try (Connection conn = DatabaseUtil.getConnection()) {
        deletePostCategories(conn, postId);
        return true;
    } catch (SQLException e) {
        LOGGER.log(Level.SEVERE, "Lỗi khi xóa danh mục của bài viết: " + e.getMessage(), e);
        return false;
    }
  }

  // Thêm phương thức public để xóa thẻ của bài viết
  public boolean deletePostTags(int postId) {
    try (Connection conn = DatabaseUtil.getConnection()) {
        deletePostTags(conn, postId);
        return true;
    } catch (SQLException e) {
        LOGGER.log(Level.SEVERE, "Lỗi khi xóa thẻ của bài viết: " + e.getMessage(), e);
        return false;
    }
  }
}