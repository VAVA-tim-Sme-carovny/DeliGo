package com.deligo.Frontend.Controllers.Employee;

import com.deligo.ConfigLoader.ConfigLoader;
import com.deligo.Frontend.Controllers.InitializableWithParent;
import com.deligo.Frontend.Controllers.MainPage.MainPageController;
import com.deligo.Logging.Adapter.LoggingAdapter;
import com.deligo.Model.BasicModels;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import com.deligo.Model.BasicModels.*;
import javafx.scene.layout.HBox;

import java.util.List;

public class EmployeeTopPanelController implements InitializableWithParent {

    @FXML private Button logoutBtn;
    @FXML private Button ordersButton;
    @FXML private Button adminButton;

    private LoggingAdapter logger;
    private ConfigLoader configLoader;
    private MainPageController mainPageController;

    // Dummy roly pre testovanie
    private final List<String> userRoles = List.of("employee", "admin");


    public EmployeeTopPanelController(LoggingAdapter logger, MainPageController mainPageController, ConfigLoader configLoader) {
        this.logger = logger;
        this.mainPageController = mainPageController;
        this.configLoader = configLoader;
    }

    @Override
    public void initializeWithParent(Object parentController) {
        if (parentController instanceof MainPageController) {
            this.mainPageController = (MainPageController) parentController;

            String role = configLoader.getConfigValue("login", "role", String.class);

            if (role != null && role.contains("admin")) {
                adminButton.setVisible(true);
            }

            ordersButton.setOnAction(event -> {
                logger.log(LogType.INFO, LogPriority.LOW, LogSource.FRONTEND, "Opening Current Orders view");
                mainPageController.loadMainContent("/Views/Content/OrderPanel/CurrentOrdersPanel.fxml", false);
            });

            adminButton.setOnAction(event -> {
                logger.log(LogType.INFO, LogPriority.LOW, LogSource.FRONTEND, "Opening Admin Panel");
                mainPageController.loadMainContent("/Views/Content/AdminPanel/AdminContentPanel.fxml", false);
            });

            logoutBtn.setOnAction(event -> {
                logger.log(LogType.INFO, LogPriority.LOW, LogSource.FRONTEND, "Logging out");
                String response = mainPageController.getServer().sendPostRequest("/be/logout", null);
                logger.log(LogType.INFO, LogPriority.LOW, LogSource.FRONTEND, "Logout successful!");
                mainPageController.loadMainContent("/Views/Content/MainPanel/MainContentPanel.fxml", false);
                mainPageController.loadControllerPanel("/Views/Controllers/MainTopPanelController.fxml", false);
                mainPageController.loadBottomPanel("/Views/Controllers/MainBottomPanelController.fxml", false);
            });
        }
    }
}
