package com.deligo.Model;

import java.util.List;

public class Order {

    private Integer id;
    private Integer user_id;
    private Integer table_id;
    private Integer device_id;
    private String status;
    private String created_at;
    private String order_contain;

    private List<OrderItem> items;

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public Order() {}

    public Integer getId() {
        return id;
    }

    public Integer getUser_id() {
        return user_id;
    }

    // Alias method for camelCase convention
    public Integer getUserId() {
        return getUser_id();
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public Integer getTable_id() {
        return table_id;
    }

    // Alias method for camelCase convention
    public Integer getTableId() {
        return getTable_id();
    }

    public void setTable_id(Integer table_id) {
        this.table_id = table_id;
    }

    public Integer getDevice_id() {
        return device_id;
    }

    // Alias method for camelCase convention
    public Integer getDeviceId() {
        return getDevice_id();
    }

    public void setDevice_id(Integer device_id) {
        this.device_id = device_id;
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

    // Alias method for camelCase convention
    public String getCreatedAt() {
        return getCreated_at();
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getOrder_contain() {
        return order_contain;
    }

    public void setOrder_contain(String order_contain) {
        this.order_contain = order_contain;
    }

}