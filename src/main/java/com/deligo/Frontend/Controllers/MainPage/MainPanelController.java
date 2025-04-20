package com.deligo.Frontend.Controllers.MainPage;

import com.deligo.Frontend.Controllers.InitializableWithParent;
import com.deligo.Logging.Adapter.LoggingAdapter;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;

public class MainPanelController implements InitializableWithParent {

    @FXML
    private Button BookTableBtn;

    @FXML
    private Button OrderBtn;

    private MainPageController mainPageController;
    private LoggingAdapter logger;

    public MainPanelController(LoggingAdapter logger, MainPageController mainPageController) {
        this.logger = logger;
        this.mainPageController = mainPageController;

    }

    @Override
    public void initializeWithParent(Object parentController) {
        this.mainPageController = (MainPageController) parentController;

        if (BookTableBtn != null) {
            BookTableBtn.setOnAction(event -> {
                mainPageController.loadMainContent("/Views/Content/MainPanel/BookTableContentPanel.fxml");
                mainPageController.loadControllerPanel("/Views/Controllers/ReturnHomeController.fxml");
                mainPageController.clearRightPanel(); // Ak máš pravý panel, vyčisti ho
            });
        }




        if (OrderBtn != null) {
            OrderBtn.setOnAction(event -> {
                try {
                    Yaml yaml = new Yaml();
                    InputStream inputStream = getClass().getResourceAsStream("/config.yaml");

                    if (inputStream == null) {
                        System.out.println("⚠️ Konfiguračný súbor sa nenašiel.");
                        return;
                    }

                    Map<String, Object> config = yaml.load(inputStream);
                    Map<String, Object> device = (Map<String, Object>) config.get("device");
                    String deviceId = device != null ? (String) device.get("id") : null;

                    if (deviceId != null && !deviceId.isEmpty()) {
                        // ID existuje – otvoriť objednávkové menu
                        mainPageController.loadMainContent("/Views/Content/OrderPanel/OrderContentPanel.fxml");
                        mainPageController.loadRightPanel("/Views/Content/OrderPanel/CartRightPanel.fxml");
                        mainPageController.loadControllerPanel("/Views/Controllers/ReturnHomeController.fxml");
                    } else {
                        // ID neexistuje – presmerovať na prihlasovacie menu
                        mainPageController.loadMainContent("/Views/Content/LoginContentPanel.fxml");
                        mainPageController.loadControllerPanel("/Views/Controllers/ReturnHomeController.fxml");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

    }
}
