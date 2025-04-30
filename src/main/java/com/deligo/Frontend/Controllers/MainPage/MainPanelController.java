package com.deligo.Frontend.Controllers.MainPage;

import com.deligo.ConfigLoader.ConfigLoader;
import com.deligo.Frontend.Controllers.InitializableWithParent;
import com.deligo.Logging.Adapter.LoggingAdapter;
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

        if (BookTableBtn != null) {
            BookTableBtn.setOnAction(event -> {
                logger.log(LogType.INFO, LogPriority.LOW, LogSource.FRONTEND, "Opening BookTable menu");
                mainPageController.loadMainContent("/Views/Content/MainPanel/BookTableContentPanel.fxml", false);
                mainPageController.loadControllerPanel("/Views/Controllers/ReturnHomeController.fxml", false);
                mainPageController.clearRightPanel(); // Ak máš pravý panel, vyčisti ho
                mainPageController.clearBottomPanel(); // Ak máš dolný panel, vyčisti ho
            });
        }




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
                            // TODO Spravit popup pre spravu
                        }else{
                            this.openOrderMenu();
                        }
                    } else if (user != null && !user.isEmpty() && role.equals("customer")) {
                        logger.log(LogType.INFO, LogPriority.LOW, LogSource.FRONTEND, "Opening Order menu");
                        this.openOrderMenu();
                    } else {
                        logger.log(LogType.INFO, LogPriority.LOW, LogSource.FRONTEND, "Opening Login menu");
                        mainPageController.loadMainContent("/Views/Content/MainPanel/LoginContentPanel.fxml", false);
                        mainPageController.loadControllerPanel("/Views/Controllers/ReturnHomeController.fxml", false);
                        mainPageController.clearBottomPanel();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

    }

    private void openOrderMenu(){
        mainPageController.loadMainContent("/Views/Content/OrderPanel/OrderContentPanel.fxml", false);
        mainPageController.loadRightPanel("/Views/Content/OrderPanel/CartRightPanel.fxml", false);
        mainPageController.loadControllerPanel("/Views/Controllers/ReturnHomeController.fxml", false);
        mainPageController.clearBottomPanel();
    }
}
