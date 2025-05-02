package com.deligo.Model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;
import java.sql.Timestamp;

public class Order {

    private int id;
    private int user_id;
    private int table_id;
    private String status;
    private String created_at;
    private String order_contain;
    private final Gson gson = new Gson();

    public Order() {}

    public int getId() {
        return id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    // Alias for getUser_id for frontend compatibility
    public Integer getUserId() {
        return user_id;
    }

    public int getTable_id() {
        return table_id;
    }

    public void setTable_id(int table_id) {
        this.table_id = table_id;
    }

    // Alias for getTable_id for frontend compatibility
    public int getTableId() {
        return getTable_id();
    }

    // Alias for getTable_id as device ID
    public Integer getDeviceId() {
        return table_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    // Alias for getCreated_at for frontend compatibility
    public Timestamp getCreatedAt() {
        return created_at != null ? Timestamp.valueOf(created_at) : null;
    }

    public String getOrder_contain() {
        return order_contain;
    }

    public void setOrder_contain(String order_contain) {
        this.order_contain = order_contain;
    }

    public void setItems(List<OrderItem> items) {
        this.order_contain = gson.toJson(items);
    }

    public List<OrderItem> getItems() {
        if (order_contain == null || order_contain.isEmpty()) {
            return new java.util.ArrayList<>();
        }
        Type listType = new TypeToken<List<OrderItem>>(){}.getType();
        return gson.fromJson(order_contain, listType);
    }
}