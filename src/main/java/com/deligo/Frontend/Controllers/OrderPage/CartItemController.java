package com.deligo.Frontend.Controllers.OrderPage;

import com.deligo.Frontend.Controllers.InitializableWithParent;
import com.deligo.Frontend.Controllers.MainPage.MainPageController;
import com.deligo.Logging.Adapter.LoggingAdapter;
import com.deligo.Model.BasicModels;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class CartItemController implements InitializableWithParent {

    @FXML private Label itemNameLabel;
    @FXML private Label itemPriceLabel;
    @FXML private Button removeBtn;

    private double price;

    private MainPageController mainPageController;

    // ✅ Toto je potrebné pre FXMLLoader
    public CartItemController() {}

    public void setData(String name, double price, Runnable onRemove, LoggingAdapter logger) {
        this.price = price;
        itemNameLabel.setText(name);
        itemPriceLabel.setText(String.format("%.2f €", price));

        removeBtn.setOnAction(e -> {
            if (onRemove != null) {
                logger.log(BasicModels.LogType.INFO, BasicModels.LogPriority.LOW, BasicModels.LogSource.FRONTEND,
                        "Item removed from cart: " + name);
                onRemove.run();
            }
        });
    }


    public double getPrice() {
        return price;
    }

    @Override
    public void initializeWithParent(Object parentController) {
        if (parentController instanceof MainPageController) {
            this.mainPageController = (MainPageController) parentController;
        }
    }
}
