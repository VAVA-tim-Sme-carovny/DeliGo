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
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.time.LocalDate;
import java.time.LocalTime;

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
    @FXML private ComboBox<String> table_fromTime;  // ComboBox pre výber času od
    @FXML private ComboBox<String> table_toTime;
    @FXML private DatePicker table_fromDate;
    @FXML private DatePicker table_toDate;
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

        table_fromDate.setValue(java.time.LocalDate.now());
        table_toDate.setValue(java.time.LocalDate.now().plusDays(1));

        // Nastavte prednastavený čas pre ComboBox
        for (int i = 10; i < 22; i++) {
            for (int j = 0; j < 60; j += 60) {
                String time = String.format("%02d:%02d", i, j);
                table_fromTime.getItems().add(time);
                table_toTime.getItems().add(time);
            }
        }

        table_fromTime.setValue("12:00");
        table_toTime.setValue("13:00");


        table_idF.setTextFormatter(new TextFormatter<>(change -> {
            if (change.getControlNewText().matches("\\d*")) {
                return change;
            }
            return null;
        }));

        if (reservation_homeBtn != null) {
            try {
                reservation_homeBtn.setOnAction(event -> {
                    logger.log(BasicModels.LogType.INFO, BasicModels.LogPriority.LOW, BasicModels.LogSource.FRONTEND,
                            "Returning, deleting all field content");
                    this.ClearAllFields();
                    mainPageController.clearAll();
                    mainPageController.loadView("/Views/Content/MainPanel/MainContentPanel.fxml", Views.mainContent);
                    mainPageController.loadView("/Views/Controllers/MainTopPanelController.fxml", Views.controllerPanel);
                    mainPageController.loadView("/Views/Controllers/MainBottomPanelController.fxml", Views.bottomPanel);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (reservation_finishBtn != null) {
            reservation_finishBtn.setOnAction(event -> {

                if (!checkData()) {
                    mainPageController.showWarningPopup("%missing" , 500);
                } else {
                    String name = table_nameF.getText();
                    String surname = table_surnameF.getText();
                    int userId = 3;
//                        Integer.parseInt(configLoader.getConfigValue("device", "id", String.class));
                    int tableId = Integer.parseInt(table_idF.getText());
                    String text = table_idF.getText();

                    LocalTime fromTime = LocalTime.parse(table_fromTime.getValue());
                    LocalTime toTime = LocalTime.parse(table_toTime.getValue());
                    LocalTime newToTime = toTime.minusMinutes(10);
                    if (fromTime.isAfter(toTime) || fromTime.equals(toTime)) {
                        mainPageController.showWarningPopup("%timeerror", 500);
                        return;
                    }

                    LocalDate fromDate = table_fromDate.getValue();
                    LocalDate toDate = table_toDate.getValue();

                    logger.log(BasicModels.LogType.INFO, BasicModels.LogPriority.LOW, BasicModels.LogSource.FRONTEND,
                            "Reservation created for: " + name + " " + "with id:" + " " + userId);
                    FeatureTableReservation featureTableReservation = new FeatureTableReservation(this.configLoader,this.logger, mainPageController.getServer());

                    String DateFrom = fromDate.toString() + " " + fromTime.toString() + ":00";
                    String DateTo = toDate.toString() + " " + newToTime.toString() + ":00";
                    String json = String.format(
                            "{\"userId\":%d, \"tableId\":%d, \"reservedFrom\":\"%s\", \"reservedTo\":\"%s\"}",
                            userId, tableId, DateFrom, DateTo
                    );

                    featureTableReservation.createReservation(json);

                    String response = mainPageController.getServer().sendPostRequest("/be/reserve", json);
                    logger.log(BasicModels.LogType.INFO, BasicModels.LogPriority.MIDDLE, BasicModels.LogSource.FRONTEND,
                            "Reservation response: " + response);

                    if (response.contains("\"status\":200")) {
                        logger.log(BasicModels.LogType.INFO, BasicModels.LogPriority.LOW, BasicModels.LogSource.FRONTEND,
                                "Reservation successful");
                        mainPageController.showWarningPopup("%reservationsucsess", 500);
                        mainPageController.clearAll();
                    } else {
                        logger.log(BasicModels.LogType.ERROR, BasicModels.LogPriority.HIGH, BasicModels.LogSource.FRONTEND,
                                "Reservation failed!");
                        mainPageController.showWarningPopup("%reservationunsucsess", 500);
                        this.ClearAllFields();

                    }
                }
            });
        }
    }

    private boolean checkData() {
        if (table_nameF.getText().isEmpty() ||
                table_surnameF.getText().isEmpty() ||
                table_idF.getText().isEmpty() ||
                table_fromDate.getValue() == null ||
                table_toDate.getValue() == null ||
                table_fromTime.getValue() == null ||
                table_toTime.getValue() == null) {

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
        table_fromDate.setValue(LocalDate.now());
        table_toDate.setValue(LocalDate.now());
    }

}
