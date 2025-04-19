package com.deligo.Frontend.Controllers.OrderPage;
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
}