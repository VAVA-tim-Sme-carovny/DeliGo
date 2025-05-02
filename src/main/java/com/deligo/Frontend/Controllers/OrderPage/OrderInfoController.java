package com.deligo.Frontend.Controllers.OrderPage;

import com.deligo.Frontend.Controllers.InitializableWithParent;
import com.deligo.Frontend.Controllers.MainPage.MainPageController;
import com.deligo.Logging.Adapter.LoggingAdapter;
import com.deligo.Model.BasicModels.*;
import com.deligo.Model.Food;
import com.deligo.Model.Order;
import com.deligo.Model.OrderItem;
import com.deligo.Model.Views;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class OrderInfoController implements InitializableWithParent {

    @FXML private AnchorPane orderInfoView;
    @FXML private Label orderNumberLabel;
    @FXML private Label numberLabel;
    @FXML private Label priceTotalLabel;
    @FXML private TableView<OrderItem> itemsTable;
    @FXML private TableColumn<OrderItem, String> itemNameColumn;
    @FXML private TableColumn<OrderItem, String> quantityColumn;
    @FXML private TableColumn<OrderItem, String> priceColumn;
    @FXML private TableColumn<OrderItem, String> totalColumn;
    @FXML private Button backButton;
    @FXML private Button finishButton;

    private LoggingAdapter logger;
    private MainPageController mainPageController;
    private Order currentOrder;
    private final Gson gson = new Gson();
    private Map<Integer, Food> foodItemsCache = new HashMap<>();

    public OrderInfoController(LoggingAdapter logger, MainPageController mainPageController) {
        this.logger = logger;
        this.mainPageController = mainPageController;
    }

    @Override
    public void initializeWithParent(Object parentController) {
        if (parentController instanceof MainPageController) {
            this.mainPageController = (MainPageController) parentController;
            initializeItemsTable();
            setupButtonHandlers();
        }
    }

    private void initializeItemsTable() {
        itemNameColumn.setCellValueFactory(cellData -> {
            Food food = foodItemsCache.get(cellData.getValue().getMenuItemId());
            return new SimpleStringProperty(food != null ? food.name : "Unknown Item");
        });
        
        quantityColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(String.valueOf(cellData.getValue().getQuantity())));
        
        priceColumn.setCellValueFactory(cellData -> {
            Food food = foodItemsCache.get(cellData.getValue().getMenuItemId());
            return new SimpleStringProperty(food != null ? 
                String.format("%.2f", food.price) : "0.00");
        });
        
        totalColumn.setCellValueFactory(cellData -> {
            Food food = foodItemsCache.get(cellData.getValue().getMenuItemId());
            return new SimpleStringProperty(food != null ? 
                String.format("%.2f", food.price * cellData.getValue().getQuantity()) : "0.00");
        });

        setupButtonHandlers();
    }

    private void setupButtonHandlers() {
        backButton.setOnAction(event -> {
            logger.log(LogType.INFO, LogPriority.LOW, LogSource.FRONTEND, "Returning to cart");
            mainPageController.clearAll();
            mainPageController.loadView("/Views/Content/OrderPanel/CartRightPanel.fxml", Views.rightPanel);
            mainPageController.loadView("/Views/Content/OrderPanel/OrderContentPanel.fxml", Views.mainContent);
            mainPageController.loadView("/Views/Controllers/ReturnHomeController.fxml", Views.controllerPanel);
        });

        finishButton.setOnAction(event -> {
            logger.log(LogType.INFO, LogPriority.LOW, LogSource.FRONTEND, "Submitting order to database");
            try {
                // Send order to server
                String orderJson = gson.toJson(currentOrder);
                String response = mainPageController.getServer().sendPostRequest("be/orders", orderJson);
                
                // Parse response
                Type responseType = new TypeToken<Map<String, Object>>() {}.getType();
                Map<String, Object> responseMap = gson.fromJson(response, responseType);
                
                if (responseMap.containsKey("status") && (int)responseMap.get("status") == 200) {
                    logger.log(LogType.SUCCESS, LogPriority.MIDDLE, LogSource.FRONTEND, "Order successfully submitted to database");
                    // Clear the cart after successful order
                    mainPageController.clearAll();
                    mainPageController.loadView("/Views/Content/MainPanel/MainContentPanel.fxml", Views.mainContent);
                    mainPageController.loadView("/Views/Controllers/MainTopPanelController.fxml", Views.controllerPanel);
                    mainPageController.loadView("/Views/Controllers/MainBottomPanelController.fxml", Views.bottomPanel);
                } else {
                    logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.FRONTEND, 
                        "Failed to submit order: " + responseMap.get("message"));
                }
            } catch (Exception e) {
                logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.FRONTEND, 
                    "Error submitting order: " + e.getMessage());
            }
        });
    }

    public void setOrder(Order order) {
        this.currentOrder = order;
        updateView();
    }

    public void setFoodItems(Map<Integer, Food> foodItems) {
        this.foodItemsCache = foodItems;
        updateView();
    }

    private void updateView() {
        if (currentOrder != null) {
            orderNumberLabel.setText("Order number: " + currentOrder.getId());
            numberLabel.setText("");
            priceTotalLabel.setText("Price total: " + String.format("%.2f", calculateTotal()) + " €");
            
            ObservableList<OrderItem> items = FXCollections.observableArrayList(currentOrder.getItems());
            itemsTable.setItems(items);
        }
    }

    private double calculateTotal() {
        double total = 0.0;
        for (OrderItem item : currentOrder.getItems()) {
            Food food = foodItemsCache.get(item.getMenuItemId());
            if (food != null) {
                total += food.price * item.getQuantity();
            }
        }
        return total;
    }
} 