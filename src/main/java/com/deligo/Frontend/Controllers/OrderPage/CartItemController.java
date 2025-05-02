package com.deligo.Frontend.Controllers.OrderPage;

import com.deligo.Frontend.Controllers.InitializableWithParent;
import com.deligo.Frontend.Controllers.MainPage.MainPageController;
import com.deligo.Logging.Adapter.LoggingAdapter;
import com.deligo.Model.BasicModels;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.HBox;

public class CartItemController implements InitializableWithParent {

    @FXML private Label itemNameLabel;
    @FXML private Label itemPriceLabel;
    @FXML private Button removeBtn;
    @FXML private Spinner<Integer> quantitySpinner;
    @FXML private HBox root;

    private double price;
    private int foodId;
    private String itemName;

    private MainPageController mainPageController;
    private LoggingAdapter logger;
    private Runnable onRemove;

    @FXML
    public void initialize() {
        // Initialize quantity spinner with default values
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 99, 1);
        if (quantitySpinner != null) {
            quantitySpinner.setValueFactory(valueFactory);
            quantitySpinner.setEditable(true);
            
            // Update price when quantity changes
            quantitySpinner.valueProperty().addListener((obs, oldValue, newValue) -> {
                updatePrice();
            });
        }
    }

    public void setData(int foodId, String name, double price, Runnable onRemove, LoggingAdapter logger) {
        this.foodId = foodId;
        this.price = price;
        this.itemName = name;
        this.onRemove = onRemove;
        this.logger = logger;

        if (itemNameLabel != null) {
            itemNameLabel.setText(name);
        }
        
        if (removeBtn != null) {
            removeBtn.setOnAction(e -> {
                if (onRemove != null) {
                    logger.log(BasicModels.LogType.INFO, BasicModels.LogPriority.LOW, BasicModels.LogSource.FRONTEND,
                            "Item removed from cart: " + name);
                    onRemove.run();
                }
            });
        }

        updatePrice();
    }

    public void incrementQuantity() {
        if (quantitySpinner != null) {
            int currentValue = quantitySpinner.getValue();
            quantitySpinner.getValueFactory().setValue(currentValue + 1);
            updatePrice();
            logger.log(BasicModels.LogType.INFO, BasicModels.LogPriority.LOW, BasicModels.LogSource.FRONTEND,
                    "Increased quantity to: " + (currentValue + 1));
        }
    }

    private void updatePrice() {
        if (itemPriceLabel != null && quantitySpinner != null) {
            int quantity = quantitySpinner.getValue();
            double total = price * quantity;
            itemPriceLabel.setText(String.format("%.2f €", total));
        }
    }

    @Override
    public void initializeWithParent(Object parentController) {
        if (parentController instanceof MainPageController) {
            this.mainPageController = (MainPageController) parentController;
        }
    }

    public double getPrice() {
        return price * quantitySpinner.getValue();
    }

    public int getQuantity() {
        return quantitySpinner.getValue();
    }

    public int getFoodId() {
        return foodId;
    }

    public String getItemName() {
        return itemName;
    }

    public double getUnitPrice() {
        return price;
    }
}
