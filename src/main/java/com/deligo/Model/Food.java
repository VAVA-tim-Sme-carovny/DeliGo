package com.deligo.Model;
public class Food {
    public String name;
    public double price;
    public String category;
    public String imagePath;

    public Food(String name, double price, String category, String imagePath) {
        this.name = name;
        this.price = price;
        this.category = category;
        this.imagePath = imagePath;
    }

    public Food(String name, double price) {
        this(name, price, null, null);
    }

    public String getName() { return name; }
    public double getPrice() { return price; }

}