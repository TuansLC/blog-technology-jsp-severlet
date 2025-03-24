package com.ptit.blogtechnology.dao;

import com.ptit.blogtechnology.model.Post;
import com.ptit.blogtechnology.model.Category;
import com.ptit.blogtechnology.model.Tag;
import com.ptit.blogtechnology.utils.DBUtils;
import com.ptit.blogtechnology.utils.SlugGenerator;

import java.sql.*;
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
  private static final int BATCH_SIZE = 100;

  public PostDAO() {
    categoryDAO = new CategoryDAO();
    tagDAO = new TagDAO();
  }

  // Lấy danh sách bài viết đã xuất bản với phân trang
  public List<Post> findPublishedPosts(int page, int postsPerPage) {
    List<Post> posts = new ArrayList<>();
    int offset = (page - 1) * postsPerPage;

    String sql = "SELECT p.*, u.username, u.full_name FROM posts p " +
        "JOIN users u ON p.author_id = u.id " +
        "WHERE p.status = 'PUBLISHED' " +
        "ORDER BY p.published_at DESC " +
        "LIMIT ? OFFSET ?";

    try (Connection conn = DBUtils.getConnection();
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

    try (Connection conn = DBUtils.getConnection();
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

    String sql = "SELECT p.*, u.username, u.full_name FROM posts p " +
        "JOIN users u ON p.author_id = u.id " +
        "WHERE p.status = 'PUBLISHED' AND p.is_featured = true " +
        "ORDER BY p.published_at DESC " +
        "LIMIT ?";

    try (Connection conn = DBUtils.getConnection();
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

    String sql = "SELECT p.*, u.username, u.full_name FROM posts p " +
        "JOIN users u ON p.author_id = u.id " +
        "WHERE p.slug = ?";

    try (Connection conn = DBUtils.getConnection();
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
    Post post = null;

    String sql = "SELECT p.*, u.username, u.full_name FROM posts p " +
        "JOIN users u ON p.author_id = u.id " +
        "WHERE p.id = ?";

    try (Connection conn = DBUtils.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setInt(1, id);

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
      LOGGER.log(Level.SEVERE, "Lỗi khi tìm bài viết theo ID: " + id, e);
    }

    return post;
  }

  // Tăng lượt xem bài viết
  public boolean incrementViewCount(int postId) {
    String sql = "UPDATE posts SET view_count = view_count + 1 WHERE id = ?";

    try (Connection conn = DBUtils.getConnection();
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
    String sql = "SELECT DISTINCT p.*, u.username, u.full_name FROM posts p " +
        "JOIN post_categories pc1 ON p.id = pc1.post_id " +
        "JOIN post_categories pc2 ON pc1.category_id = pc2.category_id " +
        "JOIN users u ON p.author_id = u.id " +
        "WHERE pc2.post_id = ? AND p.id != ? AND p.status = 'PUBLISHED' " +
        "ORDER BY p.published_at DESC " +
        "LIMIT ?";

    try (Connection conn = DBUtils.getConnection();
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

    try (Connection conn = DBUtils.getConnection();
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

    try (Connection conn = DBUtils.getConnection();
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

    try (Connection conn = DBUtils.getConnection();
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

    try (Connection conn = DBUtils.getConnection();
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

    try (Connection conn = DBUtils.getConnection();
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

    try (Connection conn = DBUtils.getConnection();
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

    String sql = "SELECT p.*, u.username, u.full_name FROM posts p " +
        "JOIN users u ON p.author_id = u.id " +
        "WHERE p.status = 'PUBLISHED' " +
        "ORDER BY p.published_at DESC " +
        "LIMIT ?";

    try (Connection conn = DBUtils.getConnection();
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

    String sql = "SELECT p.*, u.username, u.full_name FROM posts p " +
        "JOIN users u ON p.author_id = u.id " +
        "WHERE p.status = 'PUBLISHED' " +
        "ORDER BY p.view_count DESC " +
        "LIMIT ?";

    try (Connection conn = DBUtils.getConnection();
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

  // Lưu bài viết mới
  public boolean save(Post post) {
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet generatedKeys = null;

    try {
      conn = DBUtils.getConnection();
      conn.setAutoCommit(false);

      // Tạo slug nếu chưa có
      if (post.getSlug() == null || post.getSlug().isEmpty()) {
        post.setSlug(SlugGenerator.toSlug(post.getTitle()));
      }

      // Kiểm tra slug đã tồn tại chưa
      String checkSlugSql = "SELECT id FROM posts WHERE slug = ?";
      try (PreparedStatement checkStmt = conn.prepareStatement(checkSlugSql)) {
        checkStmt.setString(1, post.getSlug());

        try (ResultSet rs = checkStmt.executeQuery()) {
          if (rs.next()) {
            // Slug đã tồn tại, thêm timestamp để tạo slug mới
            post.setSlug(SlugGenerator.toUniqueSlug(post.getTitle()));
          }
        }
      }

      // Lưu bài viết
      String sql = "INSERT INTO posts (title, slug, content, summary, featured_image, " +
          "author_id, status, is_featured, view_count, created_at, updated_at, published_at) " +
          "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

      stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

      stmt.setString(1, post.getTitle());
      stmt.setString(2, post.getSlug());
      stmt.setString(3, post.getContent());
      stmt.setString(4, post.getSummary());
      stmt.setString(5, post.getFeaturedImage());
      stmt.setInt(6, post.getAuthorId());
      stmt.setString(7, post.getStatus());
      stmt.setBoolean(8, post.isFeatured());
      stmt.setInt(9, 0); // view_count ban đầu là 0

      LocalDateTime now = LocalDateTime.now();
      stmt.setTimestamp(10, Timestamp.valueOf(now));
      stmt.setTimestamp(11, Timestamp.valueOf(now));

      if ("PUBLISHED".equals(post.getStatus())) {
        stmt.setTimestamp(12, Timestamp.valueOf(now));
      } else {
        stmt.setNull(12, Types.TIMESTAMP);
      }

      int affectedRows = stmt.executeUpdate();

      if (affectedRows == 0) {
        throw new SQLException("Tạo bài viết thất bại, không có dòng nào được thêm vào.");
      }

      generatedKeys = stmt.getGeneratedKeys();
      if (generatedKeys.next()) {
        post.setId(generatedKeys.getInt(1));
      } else {
        throw new SQLException("Tạo bài viết thất bại, không lấy được ID.");
      }

      // Lưu danh mục cho bài viết
      if (post.getCategories() != null && !post.getCategories().isEmpty()) {
        saveCategoriesForPost(conn, post);
      }

      // Lưu tags cho bài viết
      if (post.getTags() != null && !post.getTags().isEmpty()) {
        saveTagsForPost(conn, post);
      }

      conn.commit();
      return true;

    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, "Lỗi khi lưu bài viết mới", e);
      if (conn != null) {
        try {
          conn.rollback();
        } catch (SQLException ex) {
          LOGGER.log(Level.SEVERE, "Lỗi khi rollback transaction", ex);
        }
      }
    } finally {
      closeResources(generatedKeys, stmt, conn);
    }

    return false;
  }

  // Cập nhật bài viết
  public boolean update(Post post) {
    Connection conn = null;
    PreparedStatement stmt = null;

    try {
      conn = DBUtils.getConnection();
      conn.setAutoCommit(false);

      // Tạo slug nếu chưa có
      if (post.getSlug() == null || post.getSlug().isEmpty()) {
        post.setSlug(SlugGenerator.toSlug(post.getTitle()));
      }

      // Kiểm tra slug đã tồn tại chưa
      String checkSlugSql = "SELECT id FROM posts WHERE slug = ? AND id != ?";
      try (PreparedStatement checkStmt = conn.prepareStatement(checkSlugSql)) {
        checkStmt.setString(1, post.getSlug());
        checkStmt.setInt(2, post.getId());

        try (ResultSet rs = checkStmt.executeQuery()) {
          if (rs.next()) {
            // Slug đã tồn tại, thêm timestamp để tạo slug mới
            post.setSlug(SlugGenerator.toUniqueSlug(post.getTitle()));
          }
        }
      }

      // Cập nhật bài viết
      String sql = "UPDATE posts SET title = ?, slug = ?, content = ?, summary = ?, " +
          "featured_image = ?, status = ?, is_featured = ?, updated_at = ?, " +
          "published_at = ? WHERE id = ?";

      stmt = conn.prepareStatement(sql);

      stmt.setString(1, post.getTitle());
      stmt.setString(2, post.getSlug());
      stmt.setString(3, post.getContent());
      stmt.setString(4, post.getSummary());
      stmt.setString(5, post.getFeaturedImage());
      stmt.setString(6, post.getStatus());
      stmt.setBoolean(7, post.isFeatured());

      LocalDateTime now = LocalDateTime.now();
      stmt.setTimestamp(8, Timestamp.valueOf(now));

      // Nếu bài viết được xuất bản lần đầu, cập nhật published_at
      if ("PUBLISHED".equals(post.getStatus())) {
        if (post.getPublishedAt() == null) {
          stmt.setTimestamp(9, Timestamp.valueOf(now));
        } else {
          stmt.setTimestamp(9, Timestamp.valueOf(post.getPublishedAt()));
        }
      } else {
        stmt.setNull(9, Types.TIMESTAMP);
      }

      stmt.setInt(10, post.getId());

      int affectedRows = stmt.executeUpdate();

      if (affectedRows == 0) {
        throw new SQLException("Cập nhật bài viết thất bại, không có dòng nào được cập nhật.");
      }

      // Xóa các liên kết danh mục cũ
      String deleteCategoriesSql = "DELETE FROM post_categories WHERE post_id = ?";
      try (PreparedStatement deleteStmt = conn.prepareStatement(deleteCategoriesSql)) {
        deleteStmt.setInt(1, post.getId());
        deleteStmt.executeUpdate();
      }

      // Lưu danh mục mới cho bài viết
      if (post.getCategories() != null && !post.getCategories().isEmpty()) {
        saveCategoriesForPost(conn, post);
      }

      // Xóa các liên kết tag cũ
      String deleteTagsSql = "DELETE FROM post_tags WHERE post_id = ?";
      try (PreparedStatement deleteStmt = conn.prepareStatement(deleteTagsSql)) {
        deleteStmt.setInt(1, post.getId());
        deleteStmt.executeUpdate();
      }

      // Lưu tags mới cho bài viết
      if (post.getTags() != null && !post.getTags().isEmpty()) {
        saveTagsForPost(conn, post);
      }

      conn.commit();
      return true;

    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, "Lỗi khi cập nhật bài viết ID: " + post.getId(), e);
      if (conn != null) {
        try {
          conn.rollback();
        } catch (SQLException ex) {
          LOGGER.log(Level.SEVERE, "Lỗi khi rollback transaction", ex);
        }
      }
    } finally {
      closeResources(null, stmt, conn);
    }

    return false;
  }

  // Xóa bài viết
  public boolean delete(int postId) {
    Connection conn = null;
    PreparedStatement stmt = null;

    try {
      conn = DBUtils.getConnection();
      conn.setAutoCommit(false);

      // Xóa các liên kết danh mục
      String deleteCategoriesSql = "DELETE FROM post_categories WHERE post_id = ?";
      try (PreparedStatement deleteStmt = conn.prepareStatement(deleteCategoriesSql)) {
        deleteStmt.setInt(1, postId);
        deleteStmt.executeUpdate();
      }

      // Xóa các liên kết tag
      String deleteTagsSql = "DELETE FROM post_tags WHERE post_id = ?";
      try (PreparedStatement deleteStmt = conn.prepareStatement(deleteTagsSql)) {
        deleteStmt.setInt(1, postId);
        deleteStmt.executeUpdate();
      }

      // Xóa các bình luận
      String deleteCommentsSql = "DELETE FROM comments WHERE post_id = ?";
      try (PreparedStatement deleteStmt = conn.prepareStatement(deleteCommentsSql)) {
        deleteStmt.setInt(1, postId);
        deleteStmt.executeUpdate();
      }

      // Xóa các đánh giá
      String deleteRatingsSql = "DELETE FROM ratings WHERE post_id = ?";
      try (PreparedStatement deleteStmt = conn.prepareStatement(deleteRatingsSql)) {
        deleteStmt.setInt(1, postId);
        deleteStmt.executeUpdate();
      }

      // Xóa các liên kết bài viết liên quan
      String deleteRelatedSql = "DELETE FROM related_posts WHERE post_id = ? OR related_post_id = ?";
      try (PreparedStatement deleteStmt = conn.prepareStatement(deleteRelatedSql)) {
        deleteStmt.setInt(1, postId);
        deleteStmt.setInt(2, postId);
        deleteStmt.executeUpdate();
      }

      // Xóa bài viết
      String sql = "DELETE FROM posts WHERE id = ?";
      stmt = conn.prepareStatement(sql);
      stmt.setInt(1, postId);

      int affectedRows = stmt.executeUpdate();

      conn.commit();
      return affectedRows > 0;

    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, "Lỗi khi xóa bài viết ID: " + postId, e);
      if (conn != null) {
        try {
          conn.rollback();
        } catch (SQLException ex) {
          LOGGER.log(Level.SEVERE, "Lỗi khi rollback transaction", ex);
        }
      }
    } finally {
      closeResources(null, stmt, conn);
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
    post.setSummary(rs.getString("summary"));
    post.setFeaturedImage(rs.getString("featured_image"));
    post.setAuthorId(rs.getInt("author_id"));
    post.setStatus(rs.getString("status"));
    post.setFeatured(rs.getBoolean("is_featured"));
    post.setViewCount(rs.getInt("view_count"));

    // Chuyển đổi Timestamp thành LocalDateTime
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

    // Thông tin tác giả
    if (rs.getMetaData().getColumnCount() > 12) { // Kiểm tra xem có thông tin tác giả không
      post.getAuthor().setUsername(rs.getString("username"));
      post.getAuthor().setFullName(rs.getString("full_name"));
    }

    return post;
  }

  // Phương thức tối ưu để lấy danh mục cho nhiều bài viết cùng lúc
  private void loadCategoriesForPosts(Connection conn, List<Post> posts) throws SQLException {
    if (posts.isEmpty()) {
      return;
    }

    // Tạo danh sách ID bài viết
    StringBuilder postIds = new StringBuilder();
    for (int i = 0; i < posts.size(); i++) {
      if (i > 0) {
        postIds.append(",");
      }
      postIds.append(posts.get(i).getId());
    }

    // Tạo map để lưu trữ danh mục theo ID bài viết
    Map<Integer, List<Category>> categoriesByPostId = new HashMap<>();

    // Truy vấn tất cả danh mục cho các bài viết
    String sql = "SELECT c.*, pc.post_id FROM categories c " +
        "JOIN post_categories pc ON c.id = pc.category_id " +
        "WHERE pc.post_id IN (" + postIds.toString() + ") " +
        "ORDER BY c.name";

    try (PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery()) {

      while (rs.next()) {
        int postId = rs.getInt("post_id");
        Category category = categoryDAO.mapResultSetToCategory(rs);

        // Thêm danh mục vào map
        categoriesByPostId.computeIfAbsent(postId, k -> new ArrayList<>()).add(category);
      }
    }

    // Gán danh mục cho từng bài viết
    for (Post post : posts) {
      List<Category> categories = categoriesByPostId.get(post.getId());
      if (categories != null) {
        post.setCategories(categories);
      } else {
        post.setCategories(new ArrayList<>());
      }
    }
  }

  // Phương thức tối ưu để lấy tags cho nhiều bài viết cùng lúc
  private void loadTagsForPosts(Connection conn, List<Post> posts) throws SQLException {
    if (posts.isEmpty()) {
      return;
    }

    // Tạo danh sách ID bài viết
    StringBuilder postIds = new StringBuilder();
    for (int i = 0; i < posts.size(); i++) {
      if (i > 0) {
        postIds.append(",");
      }
      postIds.append(posts.get(i).getId());
    }

    // Tạo map để lưu trữ tags theo ID bài viết
    Map<Integer, List<Tag>> tagsByPostId = new HashMap<>();

    // Truy vấn tất cả tags cho các bài viết
    String sql = "SELECT t.*, pt.post_id FROM tags t " +
        "JOIN post_tags pt ON t.id = pt.tag_id " +
        "WHERE pt.post_id IN (" + postIds.toString() + ") " +
        "ORDER BY t.name";

    try (PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery()) {

      while (rs.next()) {
        int postId = rs.getInt("post_id");
        Tag tag = tagDAO.mapResultSetToTag(rs);

        // Thêm tag vào map
        tagsByPostId.computeIfAbsent(postId, k -> new ArrayList<>()).add(tag);
      }
    }

    // Gán tags cho từng bài viết
    for (Post post : posts) {
      List<Tag> tags = tagsByPostId.get(post.getId());
      if (tags != null) {
        post.setTags(tags);
      } else {
        post.setTags(new ArrayList<>());
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

    try (Connection conn = DBUtils.getConnection();
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

    try (Connection conn = DBUtils.getConnection();
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
}