package com.deligo.Model;

import java.time.LocalDateTime;

public class TableReservation {
    private int id;
    private int userId;
    private int tableId;
    private LocalDateTime reservedFrom;
    private LocalDateTime reservedTo;
    private LocalDateTime createdAt;
    private String status; // PENDING, CONFIRMED, DENIED, CANCELLED

    public TableReservation(int userId, int tableId, LocalDateTime reservedFrom, LocalDateTime reservedTo) {
        this.userId = userId;
        this.tableId = tableId;
        this.reservedFrom = reservedFrom;
        this.reservedTo = reservedTo;
        this.createdAt = LocalDateTime.now();
        this.status = "PENDING";
    }

    // Getters and Setters
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

    public int getTableId() {
        return tableId;
    }

    public void setTableId(int tableId) {
        this.tableId = tableId;
    }

    public LocalDateTime getReservedFrom() {
        return reservedFrom;
    }

    public void setReservedFrom(LocalDateTime reservedFrom) {
        this.reservedFrom = reservedFrom;
    }

    public LocalDateTime getReservedTo() {
        return reservedTo;
    }

    public void setReservedTo(LocalDateTime reservedTo) {
        this.reservedTo = reservedTo;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
} 