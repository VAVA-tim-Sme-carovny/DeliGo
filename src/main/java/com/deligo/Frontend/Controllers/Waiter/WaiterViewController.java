package com.deligo.Frontend.Controllers.Waiter;

import com.deligo.Frontend.Controllers.InitializableWithParent;
import com.deligo.Frontend.Controllers.MainPage.MainPageController;
import com.deligo.Logging.Adapter.LoggingAdapter;
import com.deligo.Model.BasicModels.*;
import com.deligo.Model.Order;
import com.deligo.Model.Views;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WaiterViewController implements InitializableWithParent {

    @FXML private TableView<Order> ordersTable;
    @FXML private TableColumn<Order, String> orderIdColumn;
    @FXML private TableColumn<Order, String> tableIdColumn;
    @FXML private TableColumn<Order, String> statusColumn;
    @FXML private TableColumn<Order, String> timeColumn;
    @FXML private TableColumn<Order, Void> actionsColumn;
    @FXML private Button backButton;

    private LoggingAdapter logger;
    private MainPageController mainPageController;
    private final Gson gson = new Gson();

    public WaiterViewController(LoggingAdapter logger, MainPageController mainPageController) {
        this.logger = logger;
        this.mainPageController = mainPageController;
    }

    @Override
    public void initializeWithParent(Object parentController) {
        if (parentController instanceof MainPageController) {
            this.mainPageController = (MainPageController) parentController;
            initializeTable();
            loadOrders();
        }
    }

    private void initializeTable() {
        orderIdColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(String.valueOf(cellData.getValue().getId())));
        
        tableIdColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(String.valueOf(cellData.getValue().getTableId())));
        
        statusColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getStatus()));
        
        timeColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getCreatedAt().toString()));

        actionsColumn.setCellFactory(col -> new TableCell<>() {
            private final Button viewButton = new Button("View");
            private final Button changeStatusButton = new Button("Change Status");
            private final HBox buttons = new HBox(5, viewButton, changeStatusButton);

            {
                viewButton.setOnAction(event -> {
                    Order order = getTableView().getItems().get(getIndex());
                    handleViewOrder(order);
                });

                changeStatusButton.setOnAction(event -> {
                    Order order = getTableView().getItems().get(getIndex());
                    handleChangeStatus(order);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : buttons);
            }
        });
    }

    private void loadOrders() {
        try {
            String response = mainPageController.getServer().sendGetRequest("be/orders");
            Type responseType = new TypeToken<Map<String, Object>>() {}.getType();
            Map<String, Object> responseMap = gson.fromJson(response, responseType);

            if (responseMap.containsKey("data")) {
                String ordersJson = gson.toJson(responseMap.get("data"));
                Type listType = new TypeToken<ArrayList<Order>>() {}.getType();
                List<Order> orders = gson.fromJson(ordersJson, listType);
                ordersTable.setItems(FXCollections.observableArrayList(orders));
            }
        } catch (Exception e) {
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.FRONTEND,
                "Failed to load orders: " + e.getMessage());
        }
    }

    private void handleViewOrder(Order order) {
        try {
            mainPageController.loadView("/Views/Content/AdminPanel/ChangeOrderView.fxml", Views.mainContent);
            // Get the controller and set the order
            // This will be handled by the MainPageController's loadView method
        } catch (Exception e) {
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.FRONTEND,
                "Failed to open order view: " + e.getMessage());
        }
    }

    private void handleChangeStatus(Order order) {
        try {
            mainPageController.loadView("/Views/Content/AdminPanel/ChangeOrderView.fxml", Views.mainContent);
            // Get the controller and set the order
            // This will be handled by the MainPageController's loadView method
        } catch (Exception e) {
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.FRONTEND,
                "Failed to open change status view: " + e.getMessage());
        }
    }

    @FXML
    private void handleBackButton() {
        logger.log(LogType.INFO, LogPriority.LOW, LogSource.FRONTEND, "Returning to main view");
        mainPageController.loadView("/Views/Content/MainPanel/MainContentPanel.fxml", Views.mainContent);
        mainPageController.loadView("/Views/Controllers/MainTopPanelController.fxml", Views.controllerPanel);
        mainPageController.loadView("/Views/Controllers/MainBottomPanelController.fxml", Views.bottomPanel);
    }
} 