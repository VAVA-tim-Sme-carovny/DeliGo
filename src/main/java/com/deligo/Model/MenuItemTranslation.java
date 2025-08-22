package com.deligo.Model;

public class MenuItemTranslation {
    private int id;
    private int menu_item_id;
    private String language;
    private String name;
    private String description;

    public MenuItemTranslation() {
    }

    public MenuItemTranslation(int menu_item_id, String language, String name, String description) {
        this.menu_item_id = menu_item_id;
        this.language = language;
        this.name = name;
        this.description = description;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMenuItemId() {
        return menu_item_id;
    }

    public void setMenuItemId(int menu_item_id) {
        this.menu_item_id = menu_item_id;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

} 
