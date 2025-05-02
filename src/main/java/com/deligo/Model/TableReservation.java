package com.deligo.Model;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class TableReservation {
    private int id;
    private int user_id;
    private int table_id;
    private Timestamp reserved_from;
    private Timestamp reserved_to;
    private Timestamp created_at;

    public TableReservation(int userId, int tableId, Timestamp reservedFrom, Timestamp reservedTo) {
        this.user_id = userId;
        this.table_id = tableId;
        this.reserved_from = reservedFrom;
        this.reserved_to = reservedTo;
        this.created_at = Timestamp.valueOf(LocalDateTime.now());
    }

    public TableReservation() {
        // Default constructor
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return user_id;
    }

    public void setUserId(int userId) {
        this.user_id = userId;
    }

    public int getTableId() {
        return table_id;
    }

    public void setTableId(int tableId) {
        this.table_id = tableId;
    }

    public LocalDateTime getReservedFrom() {
        return reserved_from.toLocalDateTime();
    }

    public void setReservedFrom(LocalDateTime reservedFrom) {
        this.reserved_from = Timestamp.valueOf(reservedFrom);
    }

    public LocalDateTime getReservedTo() {
        return reserved_to.toLocalDateTime();
    }

    public void setReservedTo(LocalDateTime reservedTo) {
        this.reserved_to = Timestamp.valueOf(reservedTo);
    }

    public LocalDateTime getCreatedAt() {
        return created_at.toLocalDateTime();
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.created_at = Timestamp.valueOf(createdAt);
    }

}