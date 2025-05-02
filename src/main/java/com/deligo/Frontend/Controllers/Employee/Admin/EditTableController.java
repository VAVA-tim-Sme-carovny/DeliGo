package com.deligo.Frontend.Controllers.Employee.Admin;

import com.deligo.ConfigLoader.ConfigLoader;
import com.deligo.Frontend.Controllers.InitializableWithParent;
import com.deligo.Frontend.Controllers.MainPage.MainPageController;
import com.deligo.Logging.Adapter.LoggingAdapter;
import com.deligo.Model.BasicModels.*;
import com.deligo.Model.Response;
import com.google.gson.Gson;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.json.JSONObject;

import java.net.URL;
import java.util.ResourceBundle;

public class EditTableController implements InitializableWithParent {

    @FXML private TextField inputField;
    @FXML private Button submitButton;
    @FXML private Button deleteButton;

    private MainPageController mainPageController;
    private ConfigLoader globalConfig;
    private LoggingAdapter logger;
    private final Gson gson = new Gson();

    private String currentName;

    public EditTableController(ConfigLoader globalConfig, LoggingAdapter logger, MainPageController mainPageController) {
        this.globalConfig = globalConfig;
        this.mainPageController = mainPageController;
        this.logger = logger;
    }

    @Override
    public void initializeWithParent(Object parentController) {
        if (parentController instanceof MainPageController) {
            this.mainPageController = (MainPageController) parentController;
            String deviceId = globalConfig.getConfigValue("device", "id", String.class);
            // Načítanie pôvodnej hodnoty z configu (name)
            if (deviceId != null && !deviceId.equals("null")) {
                currentName = String.valueOf(deviceId);
            }

            inputField.setText(currentName);

            // Nastavenie onClick akcie
            submitButton.setOnAction(e -> {
                String newName = inputField.getText();

                JSONObject data = new JSONObject();
                data.put("name", currentName == null ? "null" : currentName);   // pôvodná hodnota z configu
                data.put("newName", newName);    // aktuálny vstup z textového poľa

                String responseString = mainPageController.getServer().sendPostRequest("/be/devices/update", data.toString());

                Response response = gson.fromJson(responseString, Response.class);
//                    mainPageController.showWarningPopup("nieco", 200);

                // ak 200 → refresh view alebo správa
                if (response.getStatus() == 200) {
                    logger.log(com.deligo.Model.BasicModels.LogType.SUCCESS,
                            com.deligo.Model.BasicModels.LogPriority.LOW,
                            com.deligo.Model.BasicModels.LogSource.FRONTEND,
                            "Table updated successfully");
                }
            });

            deleteButton.setOnAction(e -> {
                JSONObject data = new JSONObject();
                data.put("id", currentName);

                String responseString = mainPageController.getServer().sendPostRequest("/be/devices/delete", data.toString());
                Response response = gson.fromJson(responseString, Response.class);
                if (response.getStatus() == 200) {
                    logger.log(LogType.SUCCESS, LogPriority.LOW, LogSource.FRONTEND, "Table deleted.");
                    inputField.clear();
                }
            });
        }
    }
}