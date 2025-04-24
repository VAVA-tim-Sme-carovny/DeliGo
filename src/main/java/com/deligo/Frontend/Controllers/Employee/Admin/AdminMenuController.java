package com.deligo.Frontend.Controllers.Employee.Admin;

import com.deligo.Frontend.Controllers.InitializableWithParent;
import com.deligo.Frontend.Controllers.MainPage.MainPageController;
import com.deligo.Logging.Adapter.LoggingAdapter;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class AdminMenuController implements InitializableWithParent {

    private LoggingAdapter logger;
    private MainPageController mainPageController;
    public AdminMenuController(LoggingAdapter logger, MainPageController mainPageController) {
        this.logger = logger;
        this.mainPageController = mainPageController;
    }


    @FXML
    private Button staticticsBtn;
    @FXML
    private Button editTablesBtn;
    @FXML
    private Button editFoodMenuBtn;
    @FXML
    private Button editEmployeesBtn;
    @FXML
    private Button editInfoBtn;


    @Override
    public void initializeWithParent(Object parentController) {
        if (parentController instanceof MainPageController) {
            this.mainPageController = (MainPageController) parentController;
        }

        if (staticticsBtn != null) staticticsBtn.setOnAction(event -> {
            this.logger.log(com.deligo.Model.BasicModels.LogType.INFO, com.deligo.Model.BasicModels.LogPriority.LOW, com.deligo.Model.BasicModels.LogSource.FRONTEND, "Opening statistics menu");
            mainPageController.loadMainContent("/Views/Content/AdminPanel/StatisticsContentPanel.fxml");
        });

        if (editTablesBtn != null) editTablesBtn.setOnAction(event -> {
            this.logger.log(com.deligo.Model.BasicModels.LogType.INFO, com.deligo.Model.BasicModels.LogPriority.LOW, com.deligo.Model.BasicModels.LogSource.FRONTEND, "Opening edit tables menu");
            mainPageController.loadMainContent("/Views/Content/AdminPanel/EditTablesContentPanel.fxml");
        });

        if (editFoodMenuBtn != null) editFoodMenuBtn.setOnAction(event -> {
            this.logger.log(com.deligo.Model.BasicModels.LogType.INFO, com.deligo.Model.BasicModels.LogPriority.LOW, com.deligo.Model.BasicModels.LogSource.FRONTEND, "Opening edit food menu");
            mainPageController.loadMainContent("/Views/Content/AdminPanel/EditFoodMenuContentPanel.fxml");
        });

        if (editEmployeesBtn != null) editEmployeesBtn.setOnAction(event -> {
            this.logger.log(com.deligo.Model.BasicModels.LogType.INFO, com.deligo.Model.BasicModels.LogPriority.LOW, com.deligo.Model.BasicModels.LogSource.FRONTEND, "Opening edit employees menu");
            mainPageController.loadMainContent("/Views/Content/AdminPanel/EditEmployeesContentPanel.fxml");
        });

        if (editInfoBtn != null) editInfoBtn.setOnAction(event -> {
            this.logger.log(com.deligo.Model.BasicModels.LogType.INFO, com.deligo.Model.BasicModels.LogPriority.LOW, com.deligo.Model.BasicModels.LogSource.FRONTEND, "Opening edit info menu");
            mainPageController.loadMainContent("/Views/Content/AdminPanel/EditInfoContentPanel.fxml");
        });



    }


}
