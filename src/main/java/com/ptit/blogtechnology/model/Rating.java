package com.ptit.blogtechnology.model;

import java.time.LocalDateTime;

public class Rating {
  private int id;
  private int postId;
  private Integer userId; // Có thể null nếu là đánh giá của khách
  private int rating; // 1-5
  private String ipAddress; // Địa chỉ IP của người đánh giá
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  // Constructors
  public Rating() {
  }

  public Rating(int id, int postId, Integer userId, int rating, String ipAddress,
      LocalDateTime createdAt, LocalDateTime updatedAt) {
    this.id = id;
    this.postId = postId;
    this.userId = userId;
    this.rating = rating;
    this.ipAddress = ipAddress;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
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

  public int getRating() {
    return rating;
  }

  public void setRating(int rating) {
    this.rating = rating;
  }

  public String getIpAddress() {
    return ipAddress;
  }

  public void setIpAddress(String ipAddress) {
    this.ipAddress = ipAddress;
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

  @Override
  public String toString() {
    return "Rating{" +
        "id=" + id +
        ", postId=" + postId +
        ", userId=" + userId +
        ", rating=" + rating +
        '}';
  }
}