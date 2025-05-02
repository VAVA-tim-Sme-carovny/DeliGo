package com.deligo.Frontend.Controllers.Employee.Admin;

import com.deligo.DatabaseManager.dao.GenericDAO;
import com.deligo.Frontend.Controllers.InitializableWithParent;
import com.deligo.Frontend.Controllers.MainPage.MainPageController;
import com.deligo.Model.OrgDetails;
import com.deligo.Model.User;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class OrgDetailsViewController implements InitializableWithParent {

    @FXML private Label phoneLabel;
    @FXML private Label emailLabel;
    @FXML private GridPane hoursGrid;

    private final Gson gson = new Gson();
    private MainPageController mainPageController;
    private final GenericDAO<OrgDetails> orgDAO = new GenericDAO<>(OrgDetails.class, "info_board");

    private static final Map<String, Integer> DAY_ROW = Map.of(
            "monday", 0, "tuesday", 1, "wednesday", 2,
            "thursday", 3, "friday", 4, "saturday", 5, "sunday", 6
    );

    @Override
    public void initializeWithParent(Object parentController) {
        this.mainPageController = (MainPageController) parentController;

        String json = mainPageController.getServer().sendGetRequest("/be/info");
        List<OrgDetails> orgDetails = orgDAO.getAll();

        OrgDetails details = orgDetails.getFirst();
        if (details == null) {
            System.err.println("Chýbajúce alebo prázdne orgDetails!");
            return;
        }
//        OrgDetails details = new OrgDetails();

        phoneLabel.setText("Tel. číslo: " + details.getPhoneNumber());
        emailLabel.setText("Email: " + details.getMail());

        String openingTimes = details.getOpeningTimes();
        if (openingTimes == null || openingTimes.isEmpty()) {
            System.err.println("Chýbajúce alebo prázdne openingTimes!");
            return;
        }

        JsonObject hoursJson;
        try {
            hoursJson = JsonParser.parseString(openingTimes).getAsJsonObject();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        for (Map.Entry<String, Integer> entry : DAY_ROW.entrySet()) {
            String day = entry.getKey();
            int row = entry.getValue();

            JsonObject time = hoursJson.getAsJsonObject(day);
            String open = time.get("open").getAsString();
            String close = time.get("close").getAsString();

            hoursGrid.addRow(row,
                    new Label(capitalize(day) + ":"),
                    new Label(open),
                    new Label(close)
            );
        }

    }
    private String capitalize(String text) {
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }
}
