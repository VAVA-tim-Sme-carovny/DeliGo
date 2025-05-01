package com.deligo.Frontend.Controllers.Employee;

import com.deligo.ConfigLoader.ConfigLoader;
import com.deligo.Frontend.Controllers.InitializableWithParent;
import com.deligo.Frontend.Controllers.MainPage.MainPageController;
import com.deligo.Logging.Adapter.LoggingAdapter;
import com.deligo.Model.Views;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import com.deligo.Model.BasicModels.*;

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
                mainPageController.loadView("/Views/Content/OrderPanel/CurrentOrdersPanel.fxml", Views.mainContent);
            });

            adminButton.setOnAction(event -> {
                logger.log(LogType.INFO, LogPriority.LOW, LogSource.FRONTEND, "Opening Admin Panel");
                mainPageController.loadView("/Views/Content/AdminPanel/UsersView.fxml", Views.mainContent);
                mainPageController.loadView("/Views/Controllers/Employee/AdminMenuController.fxml", Views.leftPanel);
            });

            logoutBtn.setOnAction(event -> {
                logger.log(LogType.INFO, LogPriority.LOW, LogSource.FRONTEND, "Logging out");
                String response = mainPageController.getServer().sendPostRequest("/be/logout", null);
                logger.log(LogType.INFO, LogPriority.LOW, LogSource.FRONTEND, "Logout successful!");
                mainPageController.loadView("/Views/Content/MainPanel/MainContentPanel.fxml", Views.mainContent);
                mainPageController.loadView("/Views/Controllers/MainTopPanelController.fxml", Views.controllerPanel);
                mainPageController.loadView("/Views/Controllers/MainBottomPanelController.fxml", Views.bottomPanel);
            });
        }
    }
}
