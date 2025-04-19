package com.deligo.Frontend.Controllers.OrderPage;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;

public class ItemController {

    @FXML private Label nameLabel;
    @FXML private Label priceLabel;
    @FXML private ImageView imageView;
    @FXML private BorderPane item; // fx:id="item" v FXML

    private Runnable onClickCallback;

    public void setFoodData(String name, double price, String imagePath, Runnable onClick) {
        nameLabel.setText(name);
        priceLabel.setText(String.format("%.2f €", price));
        this.onClickCallback = onClick;

        try {
            Image image = new Image(imagePath, true);
            imageView.setImage(image);
        } catch (Exception e) {
            System.out.println("⚠️ Obrázok sa nepodarilo načítať: " + imagePath);
        }

        // Klik na celé políčko
        item.setOnMouseClicked(event -> {
            if (onClickCallback != null) {
                onClickCallback.run();
            }
        });
    }
}
