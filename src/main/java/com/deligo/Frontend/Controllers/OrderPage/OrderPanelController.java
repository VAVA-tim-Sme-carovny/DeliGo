package com.deligo.Frontend.Controllers.OrderPage;

import com.deligo.Frontend.Controllers.InitializableWithParent;
import com.deligo.Frontend.Controllers.MainPage.MainPageController;
import com.deligo.Logging.Adapter.LoggingAdapter;
import com.deligo.Model.BasicModels;
import com.deligo.Model.Food;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.AnchorPane;

import java.util.List;
import java.util.ArrayList;
import java.io.IOException;

public class OrderPanelController implements InitializableWithParent {

    @FXML private FlowPane contentArea;

    @FXML private Button btnCat1;
    @FXML private Button btnCat2;
    @FXML private Button btnCat3;
    @FXML private Button btnCat4;
    @FXML private Button btnCat5;

    private LoggingAdapter logger;
    private MainPageController mainPageController;
    private OrderCartController cartController;

    private final List<Food> foodList = List.of(
            new Food(1, "Burger", 4.50, "Kat. 1", "/Views/FoodImages/bbbb.jpg"),
            new Food(2, "Cheeseburger", 5.00, "Kat. 1", "/Views/FoodImages/bbbb.jpg"),
            new Food(3, "Big Mac", 6.00, "Kat. 1", "/Views/FoodImages/bbbb.jpg"),

            new Food(4, "Fries", 2.00, "Kat. 2", "/Views/FoodImages/bbbb.jpg"),
            new Food(5, "Curly Fries", 2.50, "Kat. 2", "/Views/FoodImages/bbbb.jpg"),

            new Food(6, "Coca-Cola", 1.80, "Kat. 3", "/Views/FoodImages/bbbb.jpg"),
            new Food(7, "Fanta", 1.80, "Kat. 3", "/Views/FoodImages/bbbb.jpg"),

            new Food(8, "Zmrzlina", 2.20, "Kat. 4", "/Views/FoodImages/bbbb.jpg"),
            new Food(9, "Shake", 3.50, "Kat. 5", "/Views/FoodImages/bbbb.jpg")
    );

    public OrderPanelController(LoggingAdapter logger, MainPageController mainPageController) {
        this.logger = logger;
        this.mainPageController = mainPageController;
    }

    @Override
    public void initializeWithParent(Object parentController) {
        this.mainPageController = (MainPageController) parentController;
        setupCategoryButtons();
        showCategory("Kat. 1"); // Show default category
    }

    private void setupCategoryButtons() {
        String buttonStyle = "-fx-background-color: #007bff; -fx-text-fill: white; -fx-font-weight: bold; -fx-min-width: 120px;";
        String activeButtonStyle = "-fx-background-color: #0056b3; -fx-text-fill: white; -fx-font-weight: bold; -fx-min-width: 120px;";

        btnCat1.setStyle(buttonStyle);
        btnCat2.setStyle(buttonStyle);
        btnCat3.setStyle(buttonStyle);
        btnCat4.setStyle(buttonStyle);
        btnCat5.setStyle(buttonStyle);

        btnCat1.setOnAction(e -> {
            resetButtonStyles(buttonStyle);
            btnCat1.setStyle(activeButtonStyle);
            this.logger.log(BasicModels.LogType.INFO, BasicModels.LogPriority.LOW, BasicModels.LogSource.FRONTEND, "Category opened: Kat. 1");
            showCategory("Kat. 1");
        });

        btnCat2.setOnAction(e -> {
            resetButtonStyles(buttonStyle);
            btnCat2.setStyle(activeButtonStyle);
            this.logger.log(BasicModels.LogType.INFO, BasicModels.LogPriority.LOW, BasicModels.LogSource.FRONTEND, "Category opened: Kat. 2");
            showCategory("Kat. 2");
        });

        btnCat3.setOnAction(e -> {
            resetButtonStyles(buttonStyle);
            btnCat3.setStyle(activeButtonStyle);
            this.logger.log(BasicModels.LogType.INFO, BasicModels.LogPriority.LOW, BasicModels.LogSource.FRONTEND, "Category opened: Kat. 3");
            showCategory("Kat. 3");
        });

        btnCat4.setOnAction(e -> {
            resetButtonStyles(buttonStyle);
            btnCat4.setStyle(activeButtonStyle);
            this.logger.log(BasicModels.LogType.INFO, BasicModels.LogPriority.LOW, BasicModels.LogSource.FRONTEND, "Category opened: Kat. 4");
            showCategory("Kat. 4");
        });

        btnCat5.setOnAction(e -> {
            resetButtonStyles(buttonStyle);
            btnCat5.setStyle(activeButtonStyle);
            this.logger.log(BasicModels.LogType.INFO, BasicModels.LogPriority.LOW, BasicModels.LogSource.FRONTEND, "Category opened: Kat. 5");
            showCategory("Kat. 5");
        });

        // Set initial active state
        btnCat1.setStyle(activeButtonStyle);
    }

    private void resetButtonStyles(String defaultStyle) {
        btnCat1.setStyle(defaultStyle);
        btnCat2.setStyle(defaultStyle);
        btnCat3.setStyle(defaultStyle);
        btnCat4.setStyle(defaultStyle);
        btnCat5.setStyle(defaultStyle);
    }

    private void showCategory(String category) {
        contentArea.getChildren().clear();

        for (Food food : foodList) {
            if (category.equals("All") || food.category.equals(category)) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Content/OrderPanel/FoodItem.fxml"));
                    Node foodNode = loader.load();

                    FoodItemController controller = loader.getController();
                    controller.setData(food, () -> {
                        OrderCartController cartController = mainPageController.getCartController();
                        if (cartController != null) {
                            cartController.addItemToCart(food);
                            logger.log(BasicModels.LogType.INFO, BasicModels.LogPriority.LOW, BasicModels.LogSource.FRONTEND, "Added to cart: " + food.name);
                        } else {
                            logger.log(BasicModels.LogType.ERROR, BasicModels.LogPriority.HIGH, BasicModels.LogSource.FRONTEND, "Cart controller is null");
                        }
                    });

                    contentArea.getChildren().add(foodNode);
                } catch (IOException e) {
                    e.printStackTrace();
                    logger.log(BasicModels.LogType.ERROR, BasicModels.LogPriority.HIGH, BasicModels.LogSource.FRONTEND, "Error loading food item: " + e.getMessage());
                }
            }
        }
    }
}
