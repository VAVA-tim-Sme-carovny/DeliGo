package com.deligo.Frontend.Controllers.OrderPage;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;

import java.util.List;

public class OrderPanelController {

    @FXML private FlowPane contentArea;

    @FXML private Button btnCat1;
    @FXML private Button btnCat2;
    @FXML private Button btnCat3;
    @FXML private Button btnCat4;
    @FXML private Button btnCat5;

    private final List<Food> foodList = List.of(
            new Food("Burger", 4.50, "Kat. 1", "file:/C:/Users/miso4/Desktop/bbbb.jpg"),
            new Food("Cheeseburger", 5.00, "Kat. 1", "file:/C:/Users/miso4/Desktop/bbbb.jpg"),
            new Food("Big Mac", 6.00, "Kat. 1", "file:/C:/Users/miso4/Desktop/bbbb.jpg"),

            new Food("Fries", 2.00, "Kat. 2", "file:/C:/Users/miso4/Desktop/bbbb.jpg"),
            new Food("Curly Fries", 2.50, "Kat. 2", "file:/C:/Users/miso4/Desktop/bbbb.jpg"),

            new Food("Coca-Cola", 1.80, "Kat. 3", "file:/C:/Users/miso4/Desktop/bbbb.jpg"),
            new Food("Fanta", 1.80, "Kat. 3", "file:/C:/Users/miso4/Desktop/bbbb.jpg"),

            new Food("Zmrzlina", 2.20, "Kat. 4", "file:/C:/Users/miso4/Desktop/bbbb.jpg"),
            new Food("Shake", 3.50, "Kat. 5", "file:/C:/Users/miso4/Desktop/bbbb.jpg")
    );


    public void initialize() {
        // Dynamické napojenie akcie podľa fx:id
        btnCat1.setOnAction(e -> showCategory("Kat. 1"));
        btnCat2.setOnAction(e -> showCategory("Kat. 2"));
        btnCat3.setOnAction(e -> showCategory("Kat. 3"));
        btnCat4.setOnAction(e -> showCategory("Kat. 4"));
        btnCat5.setOnAction(e -> showCategory("Kat. 5"));

        showCategory("Kat. 1"); // predvolené zobrazenie
    }

    public void showCategory(String category) {
        contentArea.getChildren().clear();
        try {
            for (Food food : foodList) {
                if (food.category.equals(category)) {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Content/OrderPanel/OrderItem.fxml"));
                    Node foodNode = loader.load();

                    ItemController controller = loader.getController();
                    controller.setFoodData(food.name, food.price, food.imagePath, () -> {
                        int orderCounter = 0;
                        orderCounter++;
                        System.out.println("🛒 Pridané do košíka: " + food.name + " | Celkom: " + orderCounter);
                    });



                    contentArea.getChildren().add(foodNode);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
