package com.deligo.Frontend.Controllers.OrderPage;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class CartItemController {

    @FXML private Label itemNameLabel;
    @FXML private Label itemPriceLabel;
    @FXML private Button removeBtn;

    private double price;

    public void setData(String name, double price, Runnable onRemove) {
        this.price = price;
        itemNameLabel.setText(name);
        itemPriceLabel.setText(String.format("%.2f €", price));

        removeBtn.setOnAction(e -> {
            if (onRemove != null) {
                onRemove.run();
            }
        });
    }

    public double getPrice() {
        return price;
    }
}
