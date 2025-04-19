package com.deligo.Frontend.Controllers.OrderPage;

import com.deligo.Frontend.Controllers.InitializableWithParent;
import com.deligo.Frontend.Controllers.MainPage.MainPageController;
import com.deligo.Logging.Adapter.LoggingAdapter;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;

import java.util.List;

public class OrderPanelController implements InitializableWithParent {

    @FXML private FlowPane contentArea;

    @FXML private Button btnCat1;
    @FXML private Button btnCat2;
    @FXML private Button btnCat3;
    @FXML private Button btnCat4;
    @FXML private Button btnCat5;

    private LoggingAdapter logger;
    private MainPageController mainPageController;

    private final List<Food> foodList = List.of(
            new Food("Burger", 4.50, "Kat. 1", "/Views/FoodImages/bbbb.jpg"),
            new Food("Cheeseburger", 5.00, "Kat. 1", "/Views/FoodImages/bbbb.jpg"),
            new Food("Big Mac", 6.00, "Kat. 1", "/Views/FoodImages/bbbb.jpg"),

            new Food("Fries", 2.00, "Kat. 2", "/Views/FoodImages/bbbb.jpg"),
            new Food("Curly Fries", 2.50, "Kat. 2", "/Views/FoodImages/bbbb.jpg"),

            new Food("Coca-Cola", 1.80, "Kat. 3", "/Views/FoodImages/bbbb.jpg"),
            new Food("Fanta", 1.80, "Kat. 3", "/Views/FoodImages/bbbb.jpg"),

            new Food("Zmrzlina", 2.20, "Kat. 4", "/Views/FoodImages/bbbb.jpg"),
            new Food("Shake", 3.50, "Kat. 5", "/Views/FoodImages/bbbb.jpg")
    );

    public OrderPanelController(LoggingAdapter logger,  MainPageController mainPageController) {
        this.logger = logger;
        this.mainPageController = mainPageController;
    }

    @Override
    public void initializeWithParent(Object parentController) {
        this.mainPageController = (MainPageController) parentController;

        if (btnCat1 != null) btnCat1.setOnAction(e -> showCategory("Kat. 1"));
        if (btnCat2 != null) btnCat2.setOnAction(e -> showCategory("Kat. 2"));
        if (btnCat3 != null) btnCat3.setOnAction(e -> showCategory("Kat. 3"));
        if (btnCat4 != null) btnCat4.setOnAction(e -> showCategory("Kat. 4"));
        if (btnCat5 != null) btnCat5.setOnAction(e -> showCategory("Kat. 5"));

        showCategory("Kat. 1"); // predvolene zobrazenie
    }

    private void showCategory(String category) {
        contentArea.getChildren().clear();
        try {
            for (Food food : foodList) {
                if (food.category.equals(category)) {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Content/OrderPanel/OrderItem.fxml"));
                    Node foodNode = loader.load();

                    ItemController controller = loader.getController();
                    controller.setFoodData(food.name, food.price, food.imagePath, () -> {
                        // Dummy callback zatiaľ
                        System.out.println("🛒 Pridané do košíka: " + food.name);
                    });

                    contentArea.getChildren().add(foodNode);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
