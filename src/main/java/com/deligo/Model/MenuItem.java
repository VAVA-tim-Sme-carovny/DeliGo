package com.deligo.Model;

import java.util.List;

public class MenuItem {
    private int id;
    private int categoryId;
    private int availableCount;
    private double price;

    // For backward compatibility during transition
    private List<String> categories;
    private String name;
    private String description;
    private String details;

    public MenuItem() {}

    public MenuItem(int categoryId, int availableCount, double price) {
        this.categoryId = categoryId;
        this.availableCount = availableCount;
        this.price = price;
    }

    // Constructor with backward compatibility for transition
    public MenuItem(String name, int categoryId, String description, String details, int availableCount, double price) {
        this.name = name;
        this.categoryId = categoryId;
        this.description = description;
        this.details = details;
        this.availableCount = availableCount;
        this.price = price;
    }

    // Constructor with backward compatibility for transition
    public MenuItem(String name, List<String> categories, String description, String details, int availableCount, double price) {
        this.name = name;
        this.categories = categories;
        this.description = description;
        this.details = details;
        this.availableCount = availableCount;
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
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
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

    public int getAvailableCount() {
        return availableCount;
    }

    public void setAvailableCount(int availableCount) {
        this.availableCount = availableCount;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
} 
