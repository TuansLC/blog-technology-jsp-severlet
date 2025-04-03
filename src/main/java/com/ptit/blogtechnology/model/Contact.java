package com.ptit.blogtechnology.model;

import java.time.LocalDateTime;

public class Contact {
    private int id;
    private String name;
    private String email;
    private String message;
    private Status status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Enum cho trạng thái
    public enum Status {
        NEW, READ, REPLIED
    }

    // Constructors
    public Contact() {
        this.status = Status.NEW; // Mặc định là NEW
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
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
} 