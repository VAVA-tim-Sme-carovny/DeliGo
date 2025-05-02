package com.deligo.Frontend.Controllers.Employee.Admin;

import com.deligo.Frontend.Controllers.InitializableWithParent;
import com.deligo.Frontend.Controllers.MainPage.MainPageController;
import com.deligo.Logging.Adapter.LoggingAdapter;
import com.deligo.Model.BasicModels.*;
import com.deligo.Model.Response;
import com.google.gson.Gson;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.json.JSONObject;

public class EditOrgDetails implements InitializableWithParent {

    @FXML private TextField phoneField;
    @FXML private TextField emailField;

    @FXML private TextField monOpen, monClose;
    @FXML private TextField tueOpen, tueClose;
    @FXML private TextField wedOpen, wedClose;
    @FXML private TextField thuOpen, thuClose;
    @FXML private TextField friOpen, friClose;
    @FXML private TextField satOpen, satClose;
    @FXML private TextField sunOpen, sunClose;

    @FXML private Button saveButton;
    private MainPageController mainPageController;
    private LoggingAdapter logger;
    private final Gson gson = new Gson();

    public EditOrgDetails(LoggingAdapter logger, MainPageController mainPageController) {
        this.logger = logger;
        this.mainPageController = mainPageController;
    }


    @Override
    public void initializeWithParent(Object parentController) {
        if (parentController instanceof MainPageController) {
            this.mainPageController = (MainPageController) parentController;
        }
        saveButton.setOnAction(event -> {
            JSONObject request = new JSONObject();

            request.put("phone", phoneField.getText());
            request.put("email", emailField.getText());

            JSONObject openHours = new JSONObject();
            openHours.put("monday", dayToJson(monOpen, monClose));
            openHours.put("tuesday", dayToJson(tueOpen, tueClose));
            openHours.put("wednesday", dayToJson(wedOpen, wedClose));
            openHours.put("thursday", dayToJson(thuOpen, thuClose));
            openHours.put("friday", dayToJson(friOpen, friClose));
            openHours.put("saturday", dayToJson(satOpen, satClose));
            openHours.put("sunday", dayToJson(sunOpen, sunClose));

            request.put("opening_hours", openHours);

            // odoslanie na backend
            String responseData = mainPageController.getServer().sendPostRequest("/be/update-info", request.toString());
            Response response = gson.fromJson(responseData, Response.class);

            if(response.getStatus() == 200){
                logger.log(LogType.INFO, LogPriority.MIDDLE, LogSource.FRONTEND, response.getMessage());
            }


        });
    }

    private JSONObject dayToJson(TextField open, TextField close) {
        JSONObject obj = new JSONObject();
        obj.put("open", open.getText());
        obj.put("close", close.getText());
        return obj;
    }


}
