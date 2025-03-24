package com.ptit.blogtechnology.model;

import java.time.LocalDateTime;

public class Subscriber {
  private int id;
  private String email;
  private String status; // ACTIVE, INACTIVE, UNSUBSCRIBED
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  // Constructors
  public Subscriber() {
  }

  public Subscriber(int id, String email, String status, LocalDateTime createdAt, LocalDateTime updatedAt) {
    this.id = id;
    this.email = email;
    this.status = status;
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

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
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
    return "Subscriber{" +
        "id=" + id +
        ", email='" + email + '\'' +
        ", status='" + status + '\'' +
        '}';
  }
}