package com.deligo.Frontend.Controllers.FeatureReserve;

import com.deligo.Backend.BaseFeature.BaseFeature;
import com.deligo.ConfigLoader.ConfigLoader;
import com.deligo.Frontend.Controllers.InitializableWithParent;
import com.deligo.Frontend.Controllers.MainPage.MainPageController;
import com.deligo.Logging.Adapter.LoggingAdapter;
import com.deligo.Model.BasicModels;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class FeatureReserveController implements InitializableWithParent {

    @FXML private Label nameLabel;
    @FXML private Label countLabel;
    @FXML private Label tableLabel;
    @FXML private Label timeFromLabel;
    @FXML private Label timeToLabel;

    private LoggingAdapter logger;
    private ConfigLoader configLoader;

    private MainPageController mainPageController;

    public FeatureReserveController(LoggingAdapter logger, ConfigLoader configLoader) {
        this.logger = logger;
        this.configLoader = configLoader;
    }

    @Override
    public void initializeWithParent(Object parentController) {
        if (parentController instanceof MainPageController) {
            MainPageController mainPageController = (MainPageController) parentController;
        }
    }

    private void checkData() {
        if (nameLabel.getText().isEmpty() || countLabel.getText().isEmpty() ||
            tableLabel.getText().isEmpty() || timeFromLabel.getText().isEmpty() ||
            timeToLabel.getText().isEmpty()) {
            logger.log(BasicModels.LogType.ERROR, BasicModels.LogPriority.HIGH, BasicModels.LogSource.FRONTEND,
                    "Missing data in reservation form");
            return;
        }
    }
}
