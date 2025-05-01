package com.deligo.Frontend.Controllers.OrderPage;

import com.deligo.Frontend.Controllers.InitializableWithParent;
import com.deligo.Frontend.Controllers.MainPage.MainPageController;
import com.deligo.Logging.Adapter.LoggingAdapter;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;

public class ItemController implements InitializableWithParent {

    @FXML private Label nameLabel;
    @FXML private Label priceLabel;
    @FXML private ImageView imageView;
    @FXML private BorderPane item;

    private Runnable onClickCallback;
    private MainPageController mainPageController;
    private LoggingAdapter logger;

    // ✅ Nutné pre FXMLLoader

    public void setFoodData(String name, double price, String imagePath, Runnable onClick) {
        nameLabel.setText(name);
        priceLabel.setText(String.format("%.2f €", price));
        this.onClickCallback = onClick;

        try {
            Image image = new Image(getClass().getResource(imagePath).toExternalForm());
            imageView.setImage(image);
        } catch (Exception e) {
            System.out.println("⚠️ Obrázok sa nepodarilo načítať: " + imagePath);
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
