package com.deligo.Frontend.Controllers.Admin;

import com.deligo.Frontend.Controllers.InitializableWithParent;
import com.deligo.Frontend.Controllers.MainPage.MainPageController;
import com.deligo.Logging.Adapter.LoggingAdapter;
import com.deligo.Model.BasicModels.*;
import com.deligo.Model.Order;
import com.deligo.Model.OrderItem;
import com.deligo.Model.MenuItem;
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

public class ChangeOrderViewController implements InitializableWithParent {

    @FXML private AnchorPane changeOrderView;
    @FXML private Label orderIdLabel;
    @FXML private Label customerLabel;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private Label createdAtLabel;
    @FXML private TableView<OrderItem> itemsTable;
    @FXML private TableColumn<OrderItem, String> itemNameColumn;
    @FXML private TableColumn<OrderItem, String> quantityColumn;
    @FXML private TableColumn<OrderItem, String> priceColumn;
    @FXML private TableColumn<OrderItem, String> totalColumn;
    @FXML private Button backButton;
    @FXML private Button saveButton;

    private LoggingAdapter logger;
    private MainPageController mainPageController;
    private Order currentOrder;
    private final Gson gson = new Gson();
    private Map<Integer, MenuItem> menuItemsCache = new HashMap<>();

    public ChangeOrderViewController(LoggingAdapter logger, MainPageController mainPageController) {
        this.logger = logger;
        this.mainPageController = mainPageController;
    }

    @Override
    public void initializeWithParent(Object parentController) {
        if (parentController instanceof MainPageController) {
            this.mainPageController = (MainPageController) parentController;
            initializeStatusComboBox();
            initializeItemsTable();
        }
    }

    public void setOrder(Order order) {
        this.currentOrder = order;
        fetchMenuItems();
        updateView();
    }

    private void fetchMenuItems() {
        if (currentOrder != null && currentOrder.getItems() != null) {
            for (OrderItem item : currentOrder.getItems()) {
                if (!menuItemsCache.containsKey(item.getMenuItemId())) {
                    try {
                        String response = mainPageController.getServer().sendGetRequest(
                            "be/menu-items/" + item.getMenuItemId()
                        );
                        
                        Type responseType = new TypeToken<Map<String, Object>>() {}.getType();
                        Map<String, Object> responseMap = gson.fromJson(response, responseType);
                        
                        if (responseMap.containsKey("data")) {
                            String menuItemJson = gson.toJson(responseMap.get("data"));
                            MenuItem menuItem = gson.fromJson(menuItemJson, MenuItem.class);
                            menuItemsCache.put(item.getMenuItemId(), menuItem);
                        }
                    } catch (Exception e) {
                        logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.FRONTEND,
                            "Failed to fetch menu item: " + e.getMessage());
                    }
                }
            }
        }
    }

    private void initializeStatusComboBox() {
        statusComboBox.setItems(FXCollections.observableArrayList(
            "PENDING",
            "PREPARING",
            "READY",
            "DELIVERED",
            "CANCELLED"
        ));
    }

    private void initializeItemsTable() {
        itemNameColumn.setCellValueFactory(cellData -> {
            MenuItem menuItem = menuItemsCache.get(cellData.getValue().getMenuItemId());
            return new SimpleStringProperty(menuItem != null ? menuItem.getName() : "Unknown Item");
        });
        
        quantityColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(String.valueOf(cellData.getValue().getQuantity())));
        
        priceColumn.setCellValueFactory(cellData -> {
            MenuItem menuItem = menuItemsCache.get(cellData.getValue().getMenuItemId());
            return new SimpleStringProperty(menuItem != null ? 
                String.format("%.2f", menuItem.getPrice()) : "0.00");
        });
        
        totalColumn.setCellValueFactory(cellData -> {
            MenuItem menuItem = menuItemsCache.get(cellData.getValue().getMenuItemId());
            return new SimpleStringProperty(menuItem != null ? 
                String.format("%.2f", menuItem.getPrice() * cellData.getValue().getQuantity()) : "0.00");
        });
    }

    private void updateView() {
        if (currentOrder != null) {
            orderIdLabel.setText(String.valueOf(currentOrder.getId()));
            customerLabel.setText(currentOrder.getUserId() != null ? 
                "User ID: " + currentOrder.getUserId() : 
                "Device ID: " + currentOrder.getDeviceId());
            statusComboBox.setValue(currentOrder.getStatus());
            createdAtLabel.setText(currentOrder.getCreatedAt().toString());
            
            ObservableList<OrderItem> items = FXCollections.observableArrayList(currentOrder.getItems());
            itemsTable.setItems(items);
        }
    }

    @FXML
    private void handleBackButton() {
        logger.log(LogType.INFO, LogPriority.LOW, LogSource.FRONTEND, "Returning to orders view");
        mainPageController.loadView("/Views/Content/AdminPanel/AllOrdersView.fxml", Views.mainContent);
    }

    @FXML
    private void handleSaveButton() {
        if (currentOrder != null) {
            String newStatus = statusComboBox.getValue();
            if (newStatus != null && !newStatus.equals(currentOrder.getStatus())) {
                currentOrder.setStatus(newStatus);
                try {
                    Map<String, Object> requestData = new HashMap<>();
                    requestData.put("orderId", currentOrder.getId());
                    requestData.put("status", newStatus);
                    
                    String response = mainPageController.getServer().sendPostRequest(
                        "/be/orders/update-status",
                        gson.toJson(requestData)
                    );
                    
                    logger.log(LogType.INFO, LogPriority.LOW, LogSource.FRONTEND, 
                        "Order status updated successfully");
                    handleBackButton();
                } catch (Exception e) {
                    logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.FRONTEND, 
                        "Failed to update order status: " + e.getMessage());
                }
            }
        }
    }
}