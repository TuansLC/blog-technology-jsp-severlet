package com.ptit.blogtechnology.dao;

import com.ptit.blogtechnology.model.Comment;
import com.ptit.blogtechnology.utils.DBUtils;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CommentDAO {
  private static final Logger LOGGER = Logger.getLogger(CommentDAO.class.getName());

  // Lấy danh sách bình luận theo bài viết
  public List<Comment> findByPostId(int postId) {
    List<Comment> comments = new ArrayList<>();
    Map<Integer, Comment> commentMap = new HashMap<>();

    String sql = "SELECT c.*, u.username, u.full_name FROM comments c " +
        "LEFT JOIN users u ON c.user_id = u.id " +
        "WHERE c.post_id = ? AND c.status = 'APPROVED' " +
        "ORDER BY c.created_at ASC";

    try (Connection conn = DBUtils.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setInt(1, postId);

      try (ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
          Comment comment = mapResultSetToComment(rs);
          commentMap.put(comment.getId(), comment);

          // Nếu là bình luận gốc (không có parent), thêm vào danh sách
          if (comment.getParentId() == null) {
            comments.add(comment);
          }
        }

        // Xử lý các bình luận con
        for (Comment comment : commentMap.values()) {
          if (comment.getParentId() != null) {
            Comment parent = commentMap.get(comment.getParentId());
            if (parent != null) {
              if (parent.getReplies() == null) {
                parent.setReplies(new ArrayList<>());
              }
              parent.getReplies().add(comment);
            }
          }
        }
      }
    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, "Lỗi khi lấy danh sách bình luận theo bài viết", e);
    }

    return comments;
  }

  // Lưu bình luận mới
  public boolean save(Comment comment) {
    String sql = "INSERT INTO comments (post_id, user_id, author_name, author_email, " +
        "content, status, parent_id, created_at, updated_at) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    try (Connection conn = DBUtils.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

      stmt.setInt(1, comment.getPostId());

      if (comment.getUserId() != null) {
        stmt.setInt(2, comment.getUserId());
      } else {
        stmt.setNull(2, Types.INTEGER);
      }

      stmt.setString(3, comment.getAuthorName());
      stmt.setString(4, comment.getAuthorEmail());
      stmt.setString(5, comment.getContent());
      stmt.setString(6, comment.getStatus());

      if (comment.getParentId() != null) {
        stmt.setInt(7, comment.getParentId());
      } else {
        stmt.setNull(7, Types.INTEGER);
      }

      LocalDateTime now = LocalDateTime.now();
      stmt.setTimestamp(8, Timestamp.valueOf(now));
      stmt.setTimestamp(9, Timestamp.valueOf(now));

      int affectedRows = stmt.executeUpdate();

      if (affectedRows > 0) {
        try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
          if (generatedKeys.next()) {
            comment.setId(generatedKeys.getInt(1));
            return true;
          }
        }
      }
    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, "Lỗi khi lưu bình luận", e);
    }

    return false;
  }

  // Cập nhật trạng thái bình luận
  public boolean updateStatus(int commentId, String status) {
    String sql = "UPDATE comments SET status = ?, updated_at = ? WHERE id = ?";

    try (Connection conn = DBUtils.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setString(1, status);
      stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
      stmt.setInt(3, commentId);

      int affectedRows = stmt.executeUpdate();
      return affectedRows > 0;
    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, "Lỗi khi cập nhật trạng thái bình luận", e);
    }

    return false;
  }

  // Xóa bình luận
  public boolean delete(int commentId) {
    Connection conn = null;
    PreparedStatement stmt = null;

    try {
      conn = DBUtils.getConnection();
      conn.setAutoCommit(false);

      // Xóa các bình luận con trước
      String deleteRepliesSql = "DELETE FROM comments WHERE parent_id = ?";
      try (PreparedStatement deleteStmt = conn.prepareStatement(deleteRepliesSql)) {
        deleteStmt.setInt(1, commentId);
        deleteStmt.executeUpdate();
      }

      // Xóa bình luận chính
      String sql = "DELETE FROM comments WHERE id = ?";
      stmt = conn.prepareStatement(sql);
      stmt.setInt(1, commentId);

      int affectedRows = stmt.executeUpdate();

      conn.commit();
      return affectedRows > 0;
    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, "Lỗi khi xóa bình luận", e);
      if (conn != null) {
        try {
          conn.rollback();
        } catch (SQLException ex) {
          LOGGER.log(Level.SEVERE, "Lỗi khi rollback transaction", ex);
        }
      }
    } finally {
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

    return false;
  }

  // Lấy danh sách bình luận chờ phê duyệt
  public List<Comment> findPendingComments() {
    List<Comment> comments = new ArrayList<>();

    String sql = "SELECT c.*, u.username, u.full_name FROM comments c " +
        "LEFT JOIN users u ON c.user_id = u.id " +
        "WHERE c.status = 'NEW' " +
        "ORDER BY c.created_at ASC";

    try (Connection conn = DBUtils.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery()) {

      while (rs.next()) {
        Comment comment = mapResultSetToComment(rs);
        comments.add(comment);
      }
    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, "Lỗi khi lấy danh sách bình luận chờ phê duyệt", e);
    }

    return comments;
  }

  // Phương thức hỗ trợ để chuyển ResultSet thành đối tượng Comment
  private Comment mapResultSetToComment(ResultSet rs) throws SQLException {
    Comment comment = new Comment();
    comment.setId(rs.getInt("id"));
    comment.setPostId(rs.getInt("post_id"));

    Integer userId = rs.getInt("user_id");
    if (!rs.wasNull()) {
      comment.setUserId(userId);

      // Thông tin người dùng nếu có
      String username = rs.getString("username");
      String fullName = rs.getString("full_name");

      if (username != null) {
        comment.getUser().setUsername(username);
        comment.getUser().setFullName(fullName);
      }
    }

    comment.setAuthorName(rs.getString("author_name"));
    comment.setAuthorEmail(rs.getString("author_email"));
    comment.setContent(rs.getString("content"));
    comment.setStatus(rs.getString("status"));

    Integer parentId = rs.getInt("parent_id");
    if (!rs.wasNull()) {
      comment.setParentId(parentId);
    }

    Timestamp createdAt = rs.getTimestamp("created_at");
    if (createdAt != null) {
      comment.setCreatedAt(createdAt.toLocalDateTime());
    }

    Timestamp updatedAt = rs.getTimestamp("updated_at");
    if (updatedAt != null) {
      comment.setUpdatedAt(updatedAt.toLocalDateTime());
    }

    return comment;
  }
}