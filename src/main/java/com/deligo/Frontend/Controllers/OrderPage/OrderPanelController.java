package com.deligo.Frontend.Controllers.OrderPage;

import com.deligo.DatabaseManager.dao.GenericDAO;
import com.deligo.Frontend.Controllers.InitializableWithParent;
import com.deligo.Frontend.Controllers.MainPage.MainPageController;
import com.deligo.Logging.Adapter.LoggingAdapter;
import com.deligo.Model.MenuItemInsert;
import com.google.gson.Gson;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import com.deligo.Model.BasicModels.*;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;


public class OrderPanelController implements Initializable, InitializableWithParent {

    @FXML private TableView<MenuItemInsert> menuTable;
    @FXML private TableColumn<MenuItemInsert, Integer> idColumn;
    @FXML private TableColumn<MenuItemInsert, Integer> categoryColumn;
    @FXML private TableColumn<MenuItemInsert, Double> priceColumn;
    @FXML private TableColumn<MenuItemInsert, Boolean> availabilityColumn;
    @FXML private TableColumn<MenuItemInsert, Void> actionColumn;

    private LoggingAdapter logger;
    private MainPageController mainPageController;

//    private GenericDAO<MenuItemInsert> menuDAO = new GenericDAO<>(MenuItemInsert.class, "menu_items");
    private final Gson gson = new Gson();

    public OrderPanelController(LoggingAdapter logger,  MainPageController mainPageController) {
        this.logger = logger;
        this.mainPageController = mainPageController;
//        this.menuDAO = new GenericDAO<>(MenuItemInsert.class, "menu_items");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category_id"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        availabilityColumn.setCellValueFactory(new PropertyValueFactory<>("is_available"));
        addButtonToTable();
    }

    @Override
    public void initializeWithParent(Object parentController) {
        this.mainPageController = (MainPageController) parentController;

//        mainPageController.getServer().sendPostRequest("/be/create-order" , )

//        loadItems(); // načítaj až keď je všetko pripravené
    }

//    private void loadItems() {
//        try {
//            List<MenuItemInsert> items = menuDAO.getAll();
//            menuTable.setItems(FXCollections.observableArrayList(items));
//        } catch (Exception e) {
//            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.FRONTEND, "Nepodarilo sa načítať menu položky: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }

    private void addButtonToTable() {
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button addButton = new Button("Pridať");

            {
                addButton.setOnAction(event -> {
                    MenuItemInsert item = getTableView().getItems().get(getIndex());
                    System.out.println("Pridaný item: " + item.getCategory_id());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : addButton);
            }
        });
    }
}

