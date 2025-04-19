package com.deligo.Frontend.Controllers.OrderPage;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OrderCartController {

    @FXML private VBox cartItemsContainer;
    @FXML private Label totalPriceLabel;
    @FXML private Button confirmBtn;

    private final List<Food> dummyOrder = List.of(
            new Food("Burger", 4.50),
            new Food("Cheeseburger", 5.00),
            new Food("Big Mac", 6.00),
            new Food("Fries", 2.00)
    );

    private final List<CartItemController> itemControllers = new ArrayList<>();

    public void initialize() {
        for (Food food : dummyOrder) {
            addItemToCart(food);
        }

        confirmBtn.setOnAction(e -> {
            System.out.println("✅ Objednávka potvrdená");
            // implementuj logiku odoslania neskôr
        });

        recalculateTotal();
    }

    private void addItemToCart(Food food) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Content/OrderPanel/CartItem.fxml"));
            Node node = loader.load();

            CartItemController controller = loader.getController();
            controller.setData(food.getName(), food.getPrice(), () -> {
                cartItemsContainer.getChildren().remove(node);
                itemControllers.remove(controller);
                recalculateTotal();
            });

            itemControllers.add(controller);
            cartItemsContainer.getChildren().add(node);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void recalculateTotal() {
        double total = itemControllers.stream()
                .mapToDouble(CartItemController::getPrice)
                .sum();
        totalPriceLabel.setText(String.format("Spolu: %.2f €", total));
    }
}
