package com.deligo.Frontend.Controllers.MainPage;

import com.deligo.Frontend.Controllers.InitializableWithParent;
import com.deligo.Logging.Adapter.LoggingAdapter;
import com.deligo.Model.BasicModels;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;

public class MainBottomPanelController implements InitializableWithParent {

    @FXML
    private Button callStaffBtn;

    private MainPageController mainController;
    private LoggingAdapter logger;

    public MainBottomPanelController(LoggingAdapter logger) {
        this.logger = logger;
    }

    @Override
    public void initializeWithParent(Object parentController) {
        if (parentController instanceof MainPageController) {
            this.mainController = (MainPageController) parentController;
        }

        if (callStaffBtn != null) {
            callStaffBtn.setOnAction(e -> {
                String user = getUserFromConfig();

                logger.log(BasicModels.LogType.INFO, BasicModels.LogPriority.MIDDLE, BasicModels.LogSource.FRONTEND,
                        "Called staff from table: " + user);
                System.out.println("✅ Klikol si na testButton! Používateľ: " + user);
            });
        }
    }

    private String getUserFromConfig() {
        try {
            Yaml yaml = new Yaml();
            InputStream inputStream = getClass().getResourceAsStream("/config.yaml");

            if (inputStream == null) {
                System.out.println("⚠️ Konfiguračný súbor sa nenašiel.");
                return "unknown";
            }

            Map<String, Object> config = yaml.load(inputStream);
            Map<String, Object> login = (Map<String, Object>) config.get("login");

            if (login != null) {
                return (String) login.get("user");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "unknown";
    }
}
