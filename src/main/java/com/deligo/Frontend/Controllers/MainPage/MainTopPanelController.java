package com.deligo.Frontend.Controllers.MainPage;

import com.deligo.ConfigLoader.ConfigLoader;
import com.deligo.Frontend.Controllers.InitializableWithParent;
import com.deligo.Logging.Adapter.LoggingAdapter;
import com.deligo.Model.BasicModels.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class MainTopPanelController implements InitializableWithParent {

    @FXML private Button openLogin;
    @FXML private Button openRegister;
    @FXML private Button openInfoBtn;
    @FXML private Button logoutBtn;
    @FXML private Button btnEn;
    @FXML private Button btnSk;



    private MainPageController mainController;

    private LoggingAdapter logger;
    private ConfigLoader configLoader;

    public MainTopPanelController(LoggingAdapter logger, ConfigLoader configLoader) {
        this.logger = logger;
        this.configLoader = configLoader;
    }

    @Override
    public void initializeWithParent(Object parentController) {
        this.mainController = (MainPageController) parentController;
        String user = configLoader.getConfigValue("login", "user", String.class);
        String role = configLoader.getConfigValue("login", "role", String.class);
        String deviceId = configLoader.getConfigValue("device", "id", String.class);


        if (btnSk != null) btnSk.setOnAction(e -> this.switchLanguage("sk"));
        if (btnEn != null) btnEn.setOnAction(e -> this.switchLanguage("en"));
        if (openLogin != null) openLogin.setOnAction(e -> {
            logger.log(LogType.INFO, LogPriority.MIDDLE, LogSource.FRONTEND, "Open login");
            mainController.loadMainContent("/Views/Content/MainPanel/LoginContentPanel.fxml", false);
            mainController.loadControllerPanel("/Views/Controllers/ReturnHomeController.fxml", false);
        });
        if (openInfoBtn != null) openInfoBtn.setOnAction(e -> {
            mainController.loadMainContent("/Views/Content/MainPanel/InfoContentPanel.fxml", false);
            mainController.loadControllerPanel("/Views/Controllers/ReturnHomeController.fxml", false);
            mainController.clearBottomPanel();
        });

        if (openRegister != null) openRegister.setOnAction(e -> {
            mainController.loadMainContent("/Views/Content/MainPanel/RegisterContentPanel.fxml", false);
            mainController.loadControllerPanel("/Views/Controllers/ReturnHomeController.fxml", false);
            mainController.clearBottomPanel();
        });

        if (logoutBtn != null) {
            logoutBtn.setOnAction(e -> {
                logger.log(LogType.INFO, LogPriority.HIGH, LogSource.FRONTEND, "User clicked logout");

                mainController.getServer().sendPostRequest("/be/logout", null);
                // Reloadni hlavný panel (alebo choď na login)
                mainController.loadMainContent("/Views/Content/MainPanel/MainContentPanel.fxml", false);
                mainController.loadControllerPanel("/Views/Controllers/MainTopPanelController.fxml", false);
            });
        }
        if (user != null && !user.isEmpty() && role != null && !role.isEmpty()) {
            openLogin.setVisible(false);
            openRegister.setVisible(false);
            logoutBtn.setVisible(true);

        } else if (deviceId != null && !deviceId.isEmpty()) {
            openLogin.setVisible(true);
            openRegister.setVisible(false);
            logoutBtn.setVisible(false);

        } else {
            openLogin.setVisible(true);
            openRegister.setVisible(true);
            logoutBtn.setVisible(false);
        }





    }



    @FXML
    private void switchToEnglish() {
        System.out.println("Klik na EN");
        switchLanguage("en");
    }

    @FXML
    private void switchToSlovak() {
        switchLanguage("sk");
    }



    private void switchLanguage(String langCode) {
        try {
            Locale.setDefault(new Locale(langCode));
            ResourceBundle bundle = ResourceBundle.getBundle("i18n.messages", Locale.getDefault());

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/deligo/Frontend/Views/main_page.fxml"), bundle);
            Parent root = loader.load();

            Stage stage = (Stage) goToCallService.getScene().getWindow();

            stage.getScene().setRoot(root);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private Button goToCallService;

    private void handleCallService() {
        System.out.println("Privolanie obsluhy...");
    }


    public void handleInfoButtonClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Content/InfoContentPanel.fxml"));
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.initStyle(StageStyle.UNDECORATED);
            popupStage.setScene(new Scene(loader.load()));


            popupStage.show();
            Stage primaryStage = (Stage) goToCallService.getScene().getWindow();
            double centerX = primaryStage.getX() + primaryStage.getWidth() / 2;
            double centerY = primaryStage.getY() + primaryStage.getHeight() / 2;
            popupStage.setX(centerX - popupStage.getWidth() / 2);
            popupStage.setY(centerY - popupStage.getHeight() / 2);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void loginClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Frontend_fxml/LoginContentPanel.fxml"));
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.initStyle(StageStyle.UNDECORATED);
            popupStage.setScene(new Scene(loader.load()));

            // nastav vlastníka na hlavné okno (veľmi dôležité pre ďalší krok)
            Stage primaryStage = (Stage) goToCallService.getScene().getWindow();
            popupStage.initOwner(primaryStage);

            popupStage.show();

            // centrovanie
            double centerX = primaryStage.getX() + primaryStage.getWidth() / 2;
            double centerY = primaryStage.getY() + primaryStage.getHeight() / 2;
            popupStage.setX(centerX - popupStage.getWidth() / 2);
            popupStage.setY(centerY - popupStage.getHeight() / 2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}