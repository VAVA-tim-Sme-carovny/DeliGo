package com.deligo.Model;

/**
 * Represents a category for menu items.
 * This class is used to store category information separately from menu items.
 */
public class Category {
    private int id;
    private String name;
    
    /**
     * Default constructor for Category.
     */
    public Category() {}
    
    /**
     * Constructor with name parameter.
     * 
     * @param name The name of the category
     */
    public Category(String name) {
        this.name = name;
    }
    
    /**
     * Constructor with id and name parameters.
     * 
     * @param id The ID of the category
     * @param name The name of the category
     */
    public Category(int id, String name) {
        this.id = id;
        this.name = name;
    }
    
    /**
     * Gets the ID of the category.
     * 
     * @return The category ID
     */
    public int getId() {
        return id;
    }
    
    /**
     * Sets the ID of the category.
     * 
     * @param id The category ID to set
     */
    public void setId(int id) {
        this.id = id;
    }
    
    /**
     * Gets the name of the category.
     * 
     * @return The category name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Sets the name of the category.
     * 
     * @param name The category name to set
     */
    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        Category category = (Category) o;
        
        if (id != 0 && id == category.id) return true;
        return name != null && name.equals(category.name);
    }
    
    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
    
    @Override
    public String toString() {
        return name;
    }
}