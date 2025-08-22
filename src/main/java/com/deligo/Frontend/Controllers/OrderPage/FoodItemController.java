package com.deligo.Frontend.Controllers.OrderPage;

import com.deligo.Frontend.Controllers.InitializableWithParent;
import com.deligo.Frontend.Controllers.MainPage.MainPageController;
import com.deligo.Model.Food;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;

public class FoodItemController implements InitializableWithParent {

    @FXML private Label nameLabel;
    @FXML private Label priceLabel;
    @FXML private ImageView imageView;
    @FXML private BorderPane item;

    private Runnable onClickCallback;
    private MainPageController mainPageController;

    public void setData(Food food, Runnable onClick) {
        nameLabel.setText(food.name);
        priceLabel.setText(String.format("%.2f €", food.price));
        this.onClickCallback = onClick;

        try {
            Image image = new Image(getClass().getResource(food.imagePath).toExternalForm());
            imageView.setImage(image);
        } catch (Exception e) {
            System.out.println("⚠️ Failed to load image: " + food.imagePath);
        }

        item.setOnMouseClicked(event -> {
            if (onClickCallback != null) {
                onClickCallback.run();
            }
        });
    }

    @Override
    public void initializeWithParent(Object parentController) {
        if (parentController instanceof MainPageController) {
            this.mainPageController = (MainPageController) parentController;
        }
    }
} 