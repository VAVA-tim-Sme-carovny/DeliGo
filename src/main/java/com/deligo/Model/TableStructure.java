package com.deligo.Model;


public class TableStructure {
    private int id;
    private String category;
    private String name;
    private int seats;
    private boolean isActive;
    
    public TableStructure() {}
    
    public TableStructure(String category, String name, int seats, boolean isActive) {
        this.category = category;
        this.name = name;
        this.seats = seats;
        this.isActive = isActive;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public int getSeats() {
        return seats;
    }
    
    public void setSeats(int seats) {
        this.seats = seats;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        isActive = active;
    }
} 