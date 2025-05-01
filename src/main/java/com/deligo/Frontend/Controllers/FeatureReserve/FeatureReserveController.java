package com.deligo.Frontend.Controllers.FeatureReserve;

import com.deligo.ConfigLoader.ConfigLoader;
import com.deligo.Frontend.Controllers.InitializableWithParent;
import com.deligo.Frontend.Controllers.MainPage.MainPageController;
import com.deligo.Backend.FeatureTableReservation.*;
import com.deligo.Frontend.Controllers.Popups.StatusPopupController;
import com.deligo.Logging.Adapter.LoggingAdapter;
import com.deligo.Model.BasicModels;
import com.deligo.Model.Views;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class FeatureReserveController implements InitializableWithParent {

    @FXML public GridPane table_background;
    @FXML public Label table_title;
    @FXML public Label table_name;
    @FXML public Label table_surname;
    @FXML public Label table_id;
    @FXML public Label table_from;
    @FXML public Label table_to;
    @FXML private TextField table_nameF;
    @FXML private TextField table_surnameF;
    @FXML private TextField table_idF;
    @FXML private TextField table_fromF;
    @FXML private TextField table_toF;
    @FXML private Button reservation_homeBtn;
    @FXML private Button reservation_finishBtn;

    private LoggingAdapter logger;
    private ConfigLoader configLoader;
    private MainPageController mainPageController;

    public FeatureReserveController(LoggingAdapter logger, ConfigLoader configLoader) {
        this.logger = logger;
        this.configLoader = configLoader;
    }

    @Override
    public void initializeWithParent(Object parentController) {
        this.mainPageController = (MainPageController) parentController;

        if (reservation_homeBtn != null) {
            try {
                reservation_homeBtn.setOnAction(event -> {
                    System.out.println("Home button clicked");
                    logger.log(BasicModels.LogType.INFO, BasicModels.LogPriority.LOW, BasicModels.LogSource.FRONTEND,
                            "Returning to main page");
                    mainPageController.clearAll();
                    mainPageController.loadView("/Views/Content/MainPanel/MainContentPanel.fxml", Views.mainContent);
                    mainPageController.loadView("/Views/Controllers/MainTopPanelController.fxml", Views.controllerPanel);
                    mainPageController.loadView("/Views/Controllers/MainBottomPanelController.fxml", Views.bottomPanel);
                    mainPageController.loadView("/Views/Controllers/MainRightPanelController.fxml", Views.rightPanel);
                    mainPageController.loadView("/Views/Controllers/MainLeftPanelController.fxml", Views.leftPanel);
                });
            } catch (Exception e) {
                System.out.println("Zachytená výnimka: " + e.getMessage());
                e.printStackTrace();
            }
        }

        if (reservation_finishBtn != null) {
            reservation_finishBtn.setOnAction(event -> {

                String name = table_nameF.getText();
                String surname = table_surnameF.getText();
                String id = table_idF.getText();
                String from = table_fromF.getText();
                String to = table_toF.getText();

                if (checkData()) {
                    System.out.println("Creating reservation for: " + name + " " + surname);
                    logger.log(BasicModels.LogType.INFO, BasicModels.LogPriority.LOW, BasicModels.LogSource.FRONTEND,
                            "Reservation created for: " + name + " " + surname + " " + id + " from: " + from + " to: " + to);
//                    createReservation()

                } else {
//                    mainPageController.("Missing data", "Please fill in all fields.");
                }

                String json = String.format("{\"name\":\"%s\", \"surname\":\"%s\", \"id\":\"%s\", \"from\":\"%s\", \"to\":\"%s\"}",
                        name, surname, id, from, to);

                String response = mainPageController.getServer().sendPostRequest("/be/reserve", json);
                logger.log(BasicModels.LogType.INFO, BasicModels.LogPriority.MIDDLE, BasicModels.LogSource.FRONTEND,
                        "Reservation response: " + response);

                if (response.contains("\"status\":200")) {
                    logger.log(BasicModels.LogType.INFO, BasicModels.LogPriority.LOW, BasicModels.LogSource.FRONTEND,
                            "Reservation successful");
                    // Tu môžeš napr. vyčistiť polia alebo prejsť na inú stránku
                }
            });
        }
    }

    private boolean checkData() {
        if (table_nameF.getText().isEmpty() || table_surnameF.getText().isEmpty() ||
            table_idF.getText().isEmpty() || table_fromF.getText().isEmpty() ||
            table_toF.getText().isEmpty()) {
            logger.log(BasicModels.LogType.ERROR, BasicModels.LogPriority.HIGH, BasicModels.LogSource.FRONTEND,
                    "Missing data in reservation form");
            return false;
        }
        return true;
    }

    private void ClearAllFields() {
        table_nameF.clear();
        table_surnameF.clear();
        table_idF.clear();
        table_fromF.clear();
        table_toF.clear();
    }

}
