package com.deligo.Model;

import java.sql.Timestamp;

public class Review {
    private int id;
    private int userId;
    private int menuItemId;
    private int rating;
    private String comment;
    private Timestamp createdAt;

    public Review() {}

    public Review(int userId, int menuItemId, int rating, String comment) {
        this.userId = userId;
        this.menuItemId = menuItemId;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getMenuItemId() {
        return menuItemId;
    }

    public void setMenuItemId(int menuItemId) {
        this.menuItemId = menuItemId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}