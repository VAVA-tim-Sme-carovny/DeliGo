package com.deligo.Frontend.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import com.deligo.Frontend.Views.MainView;

public class TopBarController {

    @FXML
    private Button goToPage1Button;

    @FXML
    private Button goToPage2Button;

    private MainView mainView;  // Reference to the MainView

    public void setMainView(MainView mainView) {
        this.mainView = mainView;
    }

    @FXML
    public void initialize() {
        // Adding actions to the buttons
        goToPage1Button.setOnAction(event -> handleGoToPage1());
        goToPage2Button.setOnAction(event -> handleGoToPage2());
    }

    private void handleGoToPage1() {
        mainView.loadPage("Page 1");
    }

    private void handleGoToPage2() {
        System.out.println("Navigating to Page 2");
        mainView.loadPage("Page 2");
    }
}
