package com.deligo.Model;

public class MenuItemInsert {
    private int id;
    private float price;
    private boolean is_available;
    private int category_id;

    public MenuItemInsert(int category_id, float price, boolean is_available) {
        this.category_id = category_id;
        this.price = price;
        this.is_available = is_available;
    }

    public MenuItemInsert() {
        // Default constructor
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public boolean isIs_available() {
        return is_available;
    }

    public void setIs_available(boolean is_available) {
        this.is_available = is_available;
    }
    public int getId() {return this.id;}
}
