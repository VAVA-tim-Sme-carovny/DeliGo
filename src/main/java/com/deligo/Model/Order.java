package com.deligo.Model;

import java.util.ArrayList;
import java.util.List;
import com.deligo.Model.BasicModels.Roles;

public class Order {
    private int id;
    private int userId;
    private int tableId;
    private String deviceId;
    private String status;
    private String note;

    public Order() {}

    public Order(int userId, int tableId, String deviceId, String status, String note) {
        this.userId = userId;
        this.tableId = tableId;
        this.deviceId = deviceId;
        this.status = status;
        this.note = note;
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
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}

