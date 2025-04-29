package com.deligo.Model;

public class OrderItem {
    private int id;
    private int order_id;
    private int menu_item_id;
    private int quantity;
    // Transient fields - not stored in the database
    private transient String status;
    private transient String note;
    private transient String category;

    public OrderItem() {}

    public OrderItem(int order_id, int menu_item_id, int quantity, String status, String note, String category) {
        this.order_id = order_id;
        this.menu_item_id = menu_item_id;
        this.quantity = quantity;
        this.status = status;
        this.note = note;
        this.category = category;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrderId() {
        return order_id;
    }

    public void setOrderId(int order_id) {
        this.order_id = order_id;
    }

    public int getMenuItemId() {
        return menu_item_id;
    }

    public void setMenuItemId(int menu_item_id) {
        this.menu_item_id = menu_item_id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
