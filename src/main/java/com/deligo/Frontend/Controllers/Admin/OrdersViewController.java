package com.deligo.Frontend.Controllers.Admin;

import com.deligo.ConfigLoader.ConfigLoader;
import com.deligo.Frontend.Controllers.InitializableWithParent;
import com.deligo.Frontend.Controllers.MainPage.MainPageController;
import com.deligo.Logging.Adapter.LoggingAdapter;
import com.deligo.Model.BasicModels.*;
import com.deligo.Model.Order;
import com.deligo.Model.OrderItem;
import com.deligo.Model.Views;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.Button;

import java.lang.reflect.Type;
import java.util.List;

public class OrdersViewController implements InitializableWithParent {

    @FXML
    private TreeTableView<Order> OrderTable;

    @FXML
    private TreeTableColumn<Order, String> orderIdColumn;

    @FXML
    private TreeTableColumn<Order, String> customerColumn;

    @FXML
    private TreeTableColumn<Order, String> statusColumn;

    @FXML
    private TreeTableColumn<Order, String> createdAtColumn;

    @FXML
    private AnchorPane OrdersView;

    @FXML
    private Button backButton;

    @FXML
    private Button changeOrderButton;

    private LoggingAdapter logger;
    private ConfigLoader configLoader;
    private MainPageController mainPageController;
    private final Gson gson = new Gson();

    public OrdersViewController(LoggingAdapter logger, MainPageController mainPageController, ConfigLoader configLoader) {
        this.logger = logger;
        this.mainPageController = mainPageController;
        this.configLoader = configLoader;
    }

    @Override
    public void initializeWithParent(Object parentController) {
        if (parentController instanceof MainPageController) {
            this.mainPageController = (MainPageController) parentController;
            initializeOrderTable();
            loadOrders();
            setupSelectionListener();
        }
    }

    private void setupSelectionListener() {
        OrderTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                changeOrderButton.setDisable(false);
            } else {
                changeOrderButton.setDisable(true);
            }
        });
    }

    @FXML
    private void handleChangeOrderButton() {
        TreeItem<Order> selectedItem = OrderTable.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            Order selectedOrder = selectedItem.getValue();
            logger.log(LogType.INFO, LogPriority.LOW, LogSource.FRONTEND, "Changing order: " + selectedOrder.getId());
        }
    }

    @FXML
    private void handleBackButton() {
        logger.log(LogType.INFO, LogPriority.LOW, LogSource.FRONTEND, "Returning to previous view");
        mainPageController.loadView("/Views/Content/MainPanel/MainContentPanel.fxml", Views.mainContent);
        mainPageController.loadView("/Views/Controllers/MainTopPanelController.fxml", Views.controllerPanel);
        mainPageController.loadView("/Views/Controllers/MainBottomPanelController.fxml", Views.bottomPanel);
    }

    private void initializeOrderTable() {
        // Set up cell value factories for each column
        orderIdColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(String.valueOf(cellData.getValue().getValue().getId())));
        
        customerColumn.setCellValueFactory(cellData -> {
            Order order = cellData.getValue().getValue();
            String customerInfo = order.getUserId() != null ? 
                "User ID: " + order.getUserId() : 
                "Device ID: " + order.getDeviceId();
            return new SimpleStringProperty(customerInfo);
        });
        
        statusColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getValue().getStatus()));
        
        createdAtColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getValue().getCreatedAt().toString()));
    }

    private void loadOrders() {
        try {
            String response = mainPageController.getServer().sendGetRequest("be/orders");
            
            Type orderListType = new TypeToken<List<Order>>(){}.getType();
            List<Order> orders = gson.fromJson(response, orderListType);

            TreeItem<Order> root = new TreeItem<>();
            orders.forEach(order -> root.getChildren().add(new TreeItem<>(order)));
            
            OrderTable.setRoot(root);
            OrderTable.setShowRoot(false);
            
            logger.log(LogType.INFO, LogPriority.LOW, LogSource.FRONTEND, "Successfully loaded orders");
        } catch (Exception e) {
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.FRONTEND, "Failed to load orders: " + e.getMessage());
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.FRONTEND, "Response content: " + e.getMessage());
        }
    }
}
