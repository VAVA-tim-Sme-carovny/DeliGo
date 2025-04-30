package com.deligo.Model;

import java.util.ArrayList;
import java.sql.Timestamp;
import java.util.List;
import com.deligo.Model.BasicModels.Roles;

public class Order {
    private int id;
    private int userId;
    private Integer userId;
    private int tableId;
    private String deviceId;
    private String status;
    private String note;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private List<OrderItem> items;

    public Order() {
    }

    public Order(int userId, int tableId, String deviceId, String status, String note) {
        this.userId = userId;
        this.tableId = tableId;
        this.deviceId = deviceId;
        this.status = status;
        this.note = note;
        this.createdAt = new Timestamp(System.currentTimeMillis());
        this.updatedAt = new Timestamp(System.currentTimeMillis());
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public int getTableId() {
        return tableId;
    }

    public void setTableId(int tableId) {
        this.tableId = tableId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
        this.updatedAt = new Timestamp(System.currentTimeMillis());
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }
}