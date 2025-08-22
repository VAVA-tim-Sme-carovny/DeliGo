package com.deligo.Model;

public class Food {
    public int id;
    public String name;
    public double price;
    public String category;
    public String imagePath;

    public Food(int id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = "Kat. 1"; // Default category
        this.imagePath = "/Images/food/" + name.toLowerCase().replace(" ", "_") + ".png";
    }

    public Food(int id, String name, double price, String category, String imagePath) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
        this.imagePath = imagePath;
    }

    public Food(String name, double price, String category, String imagePath) {
        this.name = name;
        this.price = price;
        this.category = category;
        this.imagePath = imagePath;
    }

    public Food(String name, double price) {
        this.name = name;
        this.price = price;
        this.category = "Kat. 1"; // Default category
        this.imagePath = "/Images/food/" + name.toLowerCase().replace(" ", "_") + ".png";
    }

    public String getName() { return name; }
    public double getPrice() { return price; }
}