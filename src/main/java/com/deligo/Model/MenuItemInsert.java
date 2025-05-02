package com.deligo.Model;

public class MenuItemInsert {
    private int category_id;
    private double price;
    private boolean is_available;

    public MenuItemInsert(int category_id, double price, boolean is_available) {
        this.category_id = category_id;
        this.price = price;
        this.is_available = is_available;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isIs_available() {
        return is_available;
    }

    public void setIs_available(boolean is_available) {
        this.is_available = is_available;
    }
}
