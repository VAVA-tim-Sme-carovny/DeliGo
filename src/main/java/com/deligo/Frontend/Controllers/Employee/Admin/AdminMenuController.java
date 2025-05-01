package com.deligo.Frontend.Controllers.Employee.Admin;

import com.deligo.DatabaseManager.dao.GenericDAO;
import com.deligo.Frontend.Controllers.InitializableWithParent;
import com.deligo.Frontend.Controllers.MainPage.MainPageController;
import com.deligo.Logging.Adapter.LoggingAdapter;
import com.deligo.Model.User;
import com.deligo.Model.Views;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import com.google.gson.Gson;

public class AdminMenuController implements InitializableWithParent {

    private LoggingAdapter logger;
    private MainPageController mainPageController;
    protected final GenericDAO<User> userDAO;
    private final Gson gson = new Gson();

    @FXML private StackPane adminMainContent;

    //Admin navigation
    @FXML private Button staticticsBtn;
    @FXML private Button editTablesBtn;
    @FXML private Button editFoodMenuBtn;
    @FXML private Button editEmployeesBtn;
    @FXML private Button editInfoBtn;
    @FXML private Button backButton;

    public AdminMenuController(LoggingAdapter logger, MainPageController mainPageController) {
        this.logger = logger;
        this.mainPageController = mainPageController;
        this.userDAO =  new GenericDAO<>(User.class, "users");
    }


    @Override
    public void initializeWithParent(Object parentController) {
        if (parentController instanceof MainPageController) {
            this.mainPageController = (MainPageController) parentController;
        }

        if (staticticsBtn != null) staticticsBtn.setOnAction(event -> {
            this.logger.log(com.deligo.Model.BasicModels.LogType.INFO, com.deligo.Model.BasicModels.LogPriority.LOW, com.deligo.Model.BasicModels.LogSource.FRONTEND, "Opening statistics menu");
            mainPageController.loadView("/Views/Content/AdminPanel/StatisticsContentPanel.fxml", Views.mainContent);
        });

        if (editTablesBtn != null) editTablesBtn.setOnAction(event -> {
            this.logger.log(com.deligo.Model.BasicModels.LogType.INFO, com.deligo.Model.BasicModels.LogPriority.LOW, com.deligo.Model.BasicModels.LogSource.FRONTEND, "Opening edit tables menu");
            mainPageController.loadView("/Views/Content/AdminPanel/EditTablesContentPanel.fxml", Views.mainContent);
        });

        if (editFoodMenuBtn != null) editFoodMenuBtn.setOnAction(event -> {
            this.logger.log(com.deligo.Model.BasicModels.LogType.INFO, com.deligo.Model.BasicModels.LogPriority.LOW, com.deligo.Model.BasicModels.LogSource.FRONTEND, "Opening edit food menu");
            mainPageController.loadView("/Views/Content/AdminPanel/EditFoodMenuContentPanel.fxml", Views.mainContent);
        });

        if (editEmployeesBtn != null) editEmployeesBtn.setOnAction(event -> {
            this.logger.log(com.deligo.Model.BasicModels.LogType.INFO, com.deligo.Model.BasicModels.LogPriority.LOW, com.deligo.Model.BasicModels.LogSource.FRONTEND, "Opening edit employees menu");
            mainPageController.loadView("/Views/Content/AdminPanel/EditEmployeesContentPanel.fxml", Views.mainContent);
        });

        if (editInfoBtn != null) editInfoBtn.setOnAction(event -> {
            this.logger.log(com.deligo.Model.BasicModels.LogType.INFO, com.deligo.Model.BasicModels.LogPriority.LOW, com.deligo.Model.BasicModels.LogSource.FRONTEND, "Opening edit info menu");
            mainPageController.loadView("/Views/Content/AdminPanel/EditInfoContentPanel.fxml", Views.mainContent);
        });

        if (backButton != null) backButton.setOnAction(event -> {
            this.logger.log(com.deligo.Model.BasicModels.LogType.INFO, com.deligo.Model.BasicModels.LogPriority.LOW, com.deligo.Model.BasicModels.LogSource.FRONTEND, "Going back to main panel");
            mainPageController.clearAll();
            mainPageController.loadView("/Views/Content/MainPanel/MainContentPanel.fxml", Views.mainContent);
            mainPageController.loadView("/Views/Controllers/MainTopPanelController.fxml", Views.controllerPanel);
        });
    }

    @FXML
    private void handleBackButton() {
        this.logger.log(com.deligo.Model.BasicModels.LogType.INFO, com.deligo.Model.BasicModels.LogPriority.LOW, com.deligo.Model.BasicModels.LogSource.FRONTEND, "Going back to main panel");
        mainPageController.clearAll();
        mainPageController.loadView("/Views/Content/MainPanel/MainContentPanel.fxml", Views.mainContent);
        mainPageController.loadView("/Views/Controllers/MainTopPanelController.fxml", Views.controllerPanel);
    }
}
