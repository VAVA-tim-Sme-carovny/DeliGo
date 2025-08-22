package com.deligo.Frontend.Controllers.OrderPage;

import com.deligo.Frontend.Controllers.InitializableWithParent;
import com.deligo.Frontend.Controllers.MainPage.MainPageController;
import com.deligo.Logging.Adapter.LoggingAdapter;
import com.deligo.Model.BasicModels;
import com.deligo.Model.Food;
import com.deligo.Model.Order;
import com.deligo.Model.OrderItem;
import com.deligo.Model.Views;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderCartController implements InitializableWithParent {

    @FXML private VBox cartItemsContainer;
    @FXML private Label totalPriceLabel;
    @FXML private Button confirmBtn;

    private LoggingAdapter logger;
    private MainPageController mainPageController;
    private List<CartItemController> itemControllers = new ArrayList<>();

    public OrderCartController(LoggingAdapter logger, MainPageController mainPageController) {
        this.logger = logger;
        this.mainPageController = mainPageController;
    }

    @Override
    public void initializeWithParent(Object parentController) {
        this.mainPageController = (MainPageController) parentController;

        if (confirmBtn != null) {
            confirmBtn.setOnAction(e -> {
                this.logger.log(BasicModels.LogType.INFO, BasicModels.LogPriority.LOW, BasicModels.LogSource.FRONTEND, "Showing order info");
                System.out.println("✅ Showing order info");
                
                // Create a new order with the current items
                Order order = new Order();
                order.setItems(convertToOrderItems());
                
                // Create a map of food items
                Map<Integer, Food> foodItems = new HashMap<>();
                for (CartItemController controller : itemControllers) {
                    Food food = new Food(
                        controller.getFoodId(),
                        controller.getItemName(),
                        controller.getUnitPrice()
                    );
                    foodItems.put(food.id, food);
                }
                
                // Load the order info view
                mainPageController.clearAll();
                mainPageController.loadView("/Views/Content/OrderPanel/order_info.fxml", Views.mainContent);
                
                // Get the controller and set the order and food items
                OrderInfoController controller = (OrderInfoController) mainPageController.getCurrentController();
                controller.setFoodItems(foodItems);
                controller.setOrder(order);
            });
        }

        updateTotal();
    }

    private List<OrderItem> convertToOrderItems() {
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItemController controller : itemControllers) {
            OrderItem item = new OrderItem();
            item.setMenuItemId(controller.getFoodId());
            item.setQuantity(controller.getQuantity());
            orderItems.add(item);
        }
        return orderItems;
    }

    public void addItemToCart(Food food) {
        // Check if item already exists in cart
        CartItemController existingItem = itemControllers.stream()
                .filter(controller -> controller.getFoodId() == food.id)
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            // If item exists, increment its quantity
            existingItem.incrementQuantity();
            updateTotal();
            logger.log(BasicModels.LogType.INFO, BasicModels.LogPriority.LOW, BasicModels.LogSource.FRONTEND, 
                "Increased quantity for item: " + food.name);
        } else {
            // If item doesn't exist, add new cart item
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Content/OrderPanel/CartItem.fxml"));
                Node itemNode = loader.load();

                CartItemController controller = loader.getController();
                controller.setData(food.id, food.name, food.price, () -> {
                    removeItemFromCart(controller);
                    updateTotal();
                }, this.logger);

                itemControllers.add(controller);
                cartItemsContainer.getChildren().add(itemNode);
                updateTotal();
                logger.log(BasicModels.LogType.INFO, BasicModels.LogPriority.LOW, BasicModels.LogSource.FRONTEND, 
                    "Added new item to cart: " + food.name);
            } catch (IOException e) {
                e.printStackTrace();
                logger.log(BasicModels.LogType.ERROR, BasicModels.LogPriority.HIGH, BasicModels.LogSource.FRONTEND, 
                    "Error adding item to cart: " + e.getMessage());
            }
        }
    }

    private void removeItemFromCart(CartItemController controller) {
        int index = itemControllers.indexOf(controller);
        if (index != -1) {
            itemControllers.remove(index);
            cartItemsContainer.getChildren().remove(index);
        }
    }

    private void updateTotal() {
        double total = 0.0;
        for (CartItemController controller : itemControllers) {
            total += controller.getPrice();
        }
        totalPriceLabel.setText(String.format("Spolu: %.2f €", total));
    }
}
