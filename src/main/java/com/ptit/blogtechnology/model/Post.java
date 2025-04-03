package com.ptit.blogtechnology.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Post {
  private int id;
  private String title;
  private String slug;
  private String content;
  private String summary;
  private String featuredImage;
  private int authorId;
  private User author = new User(); // Để lưu thông tin tác giả
  private String status; // DRAFT, PUBLISHED, PENDING
  private boolean isFeatured;
  private int viewCount;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private LocalDateTime publishedAt;
  private List<Category> categories = new ArrayList<>();
  private List<Tag> tags = new ArrayList<>();
  private double averageRating;
  private int ratingCount;
  private List<Comment> comments = new ArrayList<>();
  private String excerpt;

  // Constructors
  public Post() {
  }

  public Post(int id, String title, String slug, String content, String summary,
      String featuredImage, int authorId, String status, boolean isFeatured,
      int viewCount, LocalDateTime createdAt, LocalDateTime updatedAt,
      LocalDateTime publishedAt) {
    this.id = id;
    this.title = title;
    this.slug = slug;
    this.content = content;
    this.summary = summary;
    this.featuredImage = featuredImage;
    this.authorId = authorId;
    this.status = status;
    this.isFeatured = isFeatured;
    this.viewCount = viewCount;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.publishedAt = publishedAt;
  }

  // Getters and Setters
  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getSlug() {
    return slug;
  }

  public void setSlug(String slug) {
    this.slug = slug;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getSummary() {
    return summary;
  }

  public void setSummary(String summary) {
    this.summary = summary;
  }

  public String getFeaturedImage() {
    return featuredImage;
  }

  public void setFeaturedImage(String featuredImage) {
    this.featuredImage = featuredImage;
  }

  public int getAuthorId() {
    return authorId;
  }

  public void setAuthorId(int authorId) {
    this.authorId = authorId;
  }

  public User getAuthor() {
    return author;
  }

  public void setAuthor(User author) {
    this.author = author;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public boolean isFeatured() {
    return isFeatured;
  }

  public void setFeatured(boolean isFeatured) {
    this.isFeatured = isFeatured;
  }

  public int getViewCount() {
    return viewCount;
  }

  public void setViewCount(int viewCount) {
    this.viewCount = viewCount;
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

  public LocalDateTime getPublishedAt() {
    return publishedAt;
  }

  public void setPublishedAt(LocalDateTime publishedAt) {
    this.publishedAt = publishedAt;
  }

  public List<Category> getCategories() {
    return categories;
  }

  public void setCategories(List<Category> categories) {
    this.categories = categories;
  }

  public List<Tag> getTags() {
    return tags;
  }

  public void setTags(List<Tag> tags) {
    this.tags = tags;
  }

  public double getAverageRating() {
    return averageRating;
  }

  public void setAverageRating(double averageRating) {
    this.averageRating = averageRating;
  }

  public int getRatingCount() {
    return ratingCount;
  }

  public void setRatingCount(int ratingCount) {
    this.ratingCount = ratingCount;
  }

  public List<Comment> getComments() {
    return comments;
  }

  public void setComments(List<Comment> comments) {
    this.comments = comments;
  }

  public String getExcerpt() {
    return excerpt;
  }

  public void setExcerpt(String excerpt) {
    this.excerpt = excerpt;
  }

  // Utility methods
  public String getFormattedDate() {
    if (publishedAt != null) {
      return publishedAt.toString();
    } else if (updatedAt != null) {
      return updatedAt.toString();
    } else {
      return createdAt.toString();
    }
  }

  public String getExcerpt(int length) {
    if (summary != null && !summary.isEmpty()) {
      return summary.length() > length ? summary.substring(0, length) + "..." : summary;
    } else if (content != null && !content.isEmpty()) {
      // Loại bỏ các thẻ HTML
      String plainText = content.replaceAll("<[^>]*>", "");
      return plainText.length() > length ? plainText.substring(0, length) + "..." : plainText;
    }
    return "";
  }

  public String getCategoriesString() {
    if (categories.isEmpty()) {
      return "Chưa phân loại";
    }

    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < categories.size(); i++) {
      sb.append(categories.get(i).getName());
      if (i < categories.size() - 1) {
        sb.append(", ");
      }
    }
    return sb.toString();
  }

  public String getTagsString() {
    if (tags.isEmpty()) {
      return "";
    }

    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < tags.size(); i++) {
      sb.append(tags.get(i).getName());
      if (i < tags.size() - 1) {
        sb.append(", ");
      }
    }
    return sb.toString();
  }

  @Override
  public String toString() {
    return "Post{" +
        "id=" + id +
        ", title='" + title + '\'' +
        ", slug='" + slug + '\'' +
        ", authorId=" + authorId +
        ", status='" + status + '\'' +
        ", viewCount=" + viewCount +
        '}';
  }
}