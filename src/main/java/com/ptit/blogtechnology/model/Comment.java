package com.ptit.blogtechnology.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Comment {
  private int id;
  private int postId;
  private Integer userId; // Có thể null nếu là bình luận của khách
  private String authorName; // Tên người bình luận (nếu là khách)
  private String authorEmail; // Email người bình luận (nếu là khách)
  private String content;
  private String status; // NEW, APPROVED, SPAM, DELETED
  private Integer parentId; // ID của bình luận cha (nếu là trả lời)
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private User user; // Thông tin người dùng (nếu đã đăng nhập)
  private List<Comment> replies; // Danh sách các bình luận trả lời

  // Constructors
  public Comment() {
    this.user = new User();
  }

  public Comment(int id, int postId, Integer userId, String authorName, String authorEmail,
      String content, String status, Integer parentId, LocalDateTime createdAt,
      LocalDateTime updatedAt) {
    this.id = id;
    this.postId = postId;
    this.userId = userId;
    this.authorName = authorName;
    this.authorEmail = authorEmail;
    this.content = content;
    this.status = status;
    this.parentId = parentId;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.user = new User();
  }

  // Getters and Setters
  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getPostId() {
    return postId;
  }

  public void setPostId(int postId) {
    this.postId = postId;
  }

  public Integer getUserId() {
    return userId;
  }

  public void setUserId(Integer userId) {
    this.userId = userId;
  }

  public String getAuthorName() {
    return authorName;
  }

  public void setAuthorName(String authorName) {
    this.authorName = authorName;
  }

  public String getAuthorEmail() {
    return authorEmail;
  }

  public void setAuthorEmail(String authorEmail) {
    this.authorEmail = authorEmail;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public Integer getParentId() {
    return parentId;
  }

  public void setParentId(Integer parentId) {
    this.parentId = parentId;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public List<Comment> getReplies() {
    return replies;
  }

  public void setReplies(List<Comment> replies) {
    this.replies = replies;
  }

  // Utility methods
  public String getDisplayName() {
    if (userId != null && user != null && user.getFullName() != null) {
      return user.getFullName();
    } else if (authorName != null && !authorName.isEmpty()) {
      return authorName;
    } else {
      return "Khách";
    }
  }

  public String getFormattedCreatedAt() {
    if (createdAt != null) {
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
      return createdAt.format(formatter);
    }
    return "";
  }

  public boolean hasReplies() {
    return replies != null && !replies.isEmpty();
  }

  @Override
  public String toString() {
    return "Comment{" +
        "id=" + id +
        ", postId=" + postId +
        ", userId=" + userId +
        ", content='" + content + '\'' +
        ", status='" + status + '\'' +
        '}';
  }
}