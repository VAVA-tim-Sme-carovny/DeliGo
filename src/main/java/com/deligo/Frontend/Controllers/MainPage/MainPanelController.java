package com.deligo.Frontend.Controllers.MainPage;

import com.deligo.ConfigLoader.ConfigLoader;
import com.deligo.Frontend.Controllers.InitializableWithParent;
import com.deligo.Logging.Adapter.LoggingAdapter;
import com.deligo.Model.Views;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;
import com.deligo.Model.BasicModels.*;

public class MainPanelController implements InitializableWithParent {

    @FXML
    private Button BookTableBtn;

    @FXML
    private Button OrderBtn;

    private MainPageController mainPageController;
    private LoggingAdapter logger;
    private ConfigLoader configLoader;

    public MainPanelController(LoggingAdapter logger, MainPageController mainPageController, ConfigLoader configLoader) {
        this.logger = logger;
        this.mainPageController = mainPageController;
        this.configLoader = configLoader;
    }

    @Override
    public void initializeWithParent(Object parentController) {
        this.mainPageController = (MainPageController) parentController;

        if (OrderBtn != null) {
            OrderBtn.setOnAction(event -> {
                try {
                    String deviceId = configLoader.getConfigValue("device", "id", String.class);
                    String user = configLoader.getConfigValue("login", "user", String.class);
                    String role = configLoader.getConfigValue("login", "role", String.class);

                    if(deviceId != null && !deviceId.isEmpty()){
                        String response = mainPageController.getServer().sendPostRequest("api/be/login/customer", null);
                        if(response.contains("\"status\":500")){
                            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.FRONTEND, "There is still one order for this table. Contact waitress!");
//                            mainPageController.showWarningPopup("");
                        }else{
                            this.openOrderMenu();
                        }
                    } else if (user != null && !user.isEmpty() && role.equals("customer")) {
                        logger.log(LogType.INFO, LogPriority.LOW, LogSource.FRONTEND, "Opening Order menu");
                        this.openOrderMenu();
                    } else {
                        logger.log(LogType.INFO, LogPriority.LOW, LogSource.FRONTEND, "Opening Login menu");
                        mainPageController.clearAll();
                        mainPageController.loadView("/Views/Content/MainPanel/LoginContentPanel.fxml", Views.mainContent);
                        mainPageController.loadView("/Views/Controllers/ReturnHomeController.fxml", Views.controllerPanel);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        if (BookTableBtn != null) {
            BookTableBtn.setOnAction(event -> {
                String deviceId = configLoader.getConfigValue("device", "id", String.class);
                String user = configLoader.getConfigValue("login", "user", String.class);
                String role = configLoader.getConfigValue("login", "role", String.class);
                System.out.println("Device ID: " + deviceId);
                System.out.println("User: " + user);
                System.out.println("Role: " + role);
                if (deviceId != null && !deviceId.isEmpty()) {
                    logger.log(LogType.INFO, LogPriority.LOW, LogSource.FRONTEND, "Opening BookTable menu");
                    this.openBookTableMenu();
                } else if (user != null && !user.isEmpty() && role.equals("customer")) {
                    logger.log(LogType.INFO, LogPriority.LOW, LogSource.FRONTEND, "Opening BookTable menu");
                    this.openBookTableMenu();
                    logger.log(LogType.INFO, LogPriority.LOW, LogSource.FRONTEND, "Opening BookTable menu");
                } else {
//                    mainPageController.
                    this.openBookTableMenu();
                }
            });
        }
    }

    private void openOrderMenu(){
        mainPageController.clearAll();
        mainPageController.loadView("/Views/Content/OrderPanel/OrderContentPanel.fxml", Views.mainContent);
        mainPageController.loadView("/Views/Content/OrderPanel/CartRightPanel.fxml", Views.rightPanel);
        mainPageController.loadView("/Views/Controllers/ReturnHomeController.fxml", Views.controllerPanel);
    }

    private void openBookTableMenu(){
        mainPageController.clearAll();
        mainPageController.loadView("/Views/Content/Reservation/ReservationTable.fxml", Views.mainContent);
    }
}
