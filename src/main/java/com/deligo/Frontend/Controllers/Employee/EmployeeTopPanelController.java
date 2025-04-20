package com.deligo.Frontend.Controllers.Employee;

import com.deligo.Frontend.Controllers.InitializableWithParent;
import com.deligo.Frontend.Controllers.MainPage.MainPageController;
import com.deligo.Logging.Adapter.LoggingAdapter;
import com.deligo.Model.BasicModels;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

import java.util.List;

public class EmployeeTopPanelController implements InitializableWithParent {

    @FXML
    private HBox buttonContainer;

    @FXML
    private Button HomeBtn;

    private LoggingAdapter logger;

    private MainPageController mainPageController;

    // Dummy roly pre testovanie
    private final List<String> userRoles = List.of("employee", "admin");


    public EmployeeTopPanelController(LoggingAdapter logger, MainPageController mainPageController) {
        this.logger = logger;
        this.mainPageController = mainPageController;
    }

    @Override
    public void initializeWithParent(Object parentController) {
        if (parentController instanceof MainPageController) {
            this.mainPageController = (MainPageController) parentController;

            if (userRoles.contains("employee") || userRoles.contains("admin")) {
                Button orderButton = new Button("Objednávky");
                orderButton.setOnAction(e -> {
                    logger.log(BasicModels.LogType.INFO, BasicModels.LogPriority.MIDDLE, BasicModels.LogSource.FRONTEND, "Opening order panel");
                    mainPageController.loadMainContent("/Views/Content/OrderPanel/OrderContentPanel.fxml");
                    mainPageController.loadRightPanel("/Views/Content/OrderPanel/CartRightPanel.fxml");
                    mainPageController.loadControllerPanel("/Views/Controllers/ReturnHomeController.fxml");
                });
                buttonContainer.getChildren().add(orderButton);
            }

            if (userRoles.contains("admin")) {
                Button adminButton = new Button("Admin Panel");
                adminButton.setOnAction(e -> {
                    logger.log(BasicModels.LogType.INFO, BasicModels.LogPriority.MIDDLE, BasicModels.LogSource.FRONTEND, "Opening admin panel");
                    mainPageController.loadControllerPanel("/Views/Controllers/EmployeeTopPanelController.fxml");
                    mainPageController.loadMainContent("/Views/Content/AdminPanel/AdminMenuContentPanel.fxml");

                });
                buttonContainer.getChildren().add(adminButton);
            }


            if (HomeBtn != null) {
                HomeBtn.setOnAction(event -> {
                    logger.log(BasicModels.LogType.INFO, BasicModels.LogPriority.LOW, BasicModels.LogSource.FRONTEND, "Returning to main page");
                    mainPageController.loadMainContent("/Views/Content/MainPanel/MainContentPanel.fxml");
                    mainPageController.loadControllerPanel("/Views/Controllers/MainTopPanelController.fxml");
                    mainPageController.clearRightPanel();
                });
            }
        }
    }
}
