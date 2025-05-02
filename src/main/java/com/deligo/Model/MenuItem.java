package com.deligo.Model;

import java.util.List;

public class MenuItem {
    private int id;
    private int category_id;
    private boolean is_available;
    private float price;

    // For backward compatibility during transition
    private List<String> categories;
    private String name;
    private String description;
    private String details;

    public MenuItem() {
        id = 1;
    }

    public MenuItem(int category_id, boolean is_available, float price) {
        this.category_id = category_id;
        this.is_available = is_available;
        this.price = price;
    }

    // Constructor with backward compatibility for transition
    public MenuItem(String name, int category_id, String description, String details, boolean is_available, float price) {
        this.name = name;
        this.category_id = category_id;
        this.description = description;
        this.details = details;
        this.is_available = is_available;
        this.price = price;
    }

    // Constructor with backward compatibility for transition
    public MenuItem(String name, List<String> categories, String description, String details, boolean is_available, float price) {
        this.name = name;
        this.categories = categories;
        this.description = description;
        this.details = details;
        this.is_available = is_available;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCategoryId() {
        return category_id;
    }

    public void setCategoryId(int category_id) {
        this.category_id = category_id;
    }

    // For backward compatibility during transition
    public List<String> getCategories() {
        return categories;
    }

    // For backward compatibility during transition
    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public boolean getIsAvailable() {
        return is_available;
    }

    public void setIsAvailable(boolean is_available) {
        this.is_available = is_available;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }
} 
