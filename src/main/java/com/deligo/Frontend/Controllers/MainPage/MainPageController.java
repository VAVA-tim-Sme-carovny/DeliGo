package com.deligo.Frontend.Controllers.MainPage;

import com.deligo.Backend.BaseFeature.BaseFeature;
import com.deligo.ConfigLoader.ConfigLoader;
import com.deligo.Frontend.Controllers.InitializableWithParent;
import com.deligo.Frontend.Controllers.Popups.StatusPopupController;
import com.deligo.Frontend.Controllers.OrderPage.OrderCartController;
import com.deligo.Frontend.Helpers.CustomControllerFactory;
import com.deligo.Logging.Adapter.LoggingAdapter;
import com.deligo.Model.Views;
import com.deligo.RestApi.RestAPIServer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.Node;

import java.awt.*;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

import com.deligo.Model.BasicModels.*;

public class MainPageController extends BaseFeature {

    private final LoggingAdapter logger;
    private final RestAPIServer server;
    private Object currentController;
    private OrderCartController cartController;

    public MainPageController(ConfigLoader globalConfig, LoggingAdapter logger, RestAPIServer restApiServer) {
        super(globalConfig, logger, restApiServer);
        this.logger = logger;
        this.server = restApiServer;
    }

    @FXML private AnchorPane mainContent;
    @FXML private AnchorPane controllerPanel;
    @FXML private AnchorPane rightPanel;
    @FXML private AnchorPane bottomPanel;
    @FXML private AnchorPane leftPanel;

    public void initialize() {
        this.logger.log(LogType.INFO, LogPriority.HIGH, LogSource.FRONTEND, " MainPageController.initialize() called");

        // Perform logout on startup
        try {
            String response = server.sendPostRequest("/be/logout", null);
            this.logger.log(LogType.INFO, LogPriority.LOW, LogSource.FRONTEND, "Logout on startup successful");
        } catch (Exception e) {
            this.logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.FRONTEND, "Failed to logout on startup: " + e.getMessage());
        }

        loadView("/Views/Content/MainPanel/MainContentPanel.fxml", Views.mainContent);
        loadView("/Views/Controllers/MainTopPanelController.fxml", Views.controllerPanel);
        loadView("/Views/Controllers/MainBottomPanelController.fxml", Views.bottomPanel);
    }

    public void loadView(String fxmlPath, Views view) {
        AnchorPane viewToChange= mainContent;

        switch (view) {
            case mainContent:
                viewToChange = mainContent;
                break;
            case controllerPanel:
                viewToChange = controllerPanel;
                break;
            case leftPanel:
                viewToChange = leftPanel;
                break;
            case rightPanel:
                viewToChange = rightPanel;
                break;
            case bottomPanel:
                viewToChange = bottomPanel;
                break;
            default:
                break;
        }

        try {
            ResourceBundle bundle = ResourceBundle.getBundle("i18n.messages", Locale.getDefault());
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath), bundle);

            loader.setControllerFactory(
                    new CustomControllerFactory(this, this.logger, this.server, this.globalConfig)
            );
            Node node = loader.load();

            Object controller = loader.getController();
            this.currentController = controller;
            
            // Store cart controller if loading cart panel
            if (fxmlPath.contains("CartRightPanel.fxml")) {
                this.cartController = (OrderCartController) controller;
            }
            
            if (controller instanceof InitializableWithParent) {
                ((InitializableWithParent) controller).initializeWithParent(this);
            }
            viewToChange.getChildren().clear();
            viewToChange.getChildren().add(node);

            AnchorPane.setTopAnchor(node, 0.0);
            AnchorPane.setBottomAnchor(node, 0.0);
            AnchorPane.setLeftAnchor(node, 0.0);
            AnchorPane.setRightAnchor(node, 0.0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Object getCurrentController() {
        return currentController;
    }

    public OrderCartController getCartController() {
        return cartController;
    }

    public void clearAll() {
        controllerPanel.getChildren().clear();
        rightPanel.getChildren().clear();
        bottomPanel.getChildren().clear();
        leftPanel.getChildren().clear();
        mainContent.getChildren().clear();
    }

    public void showWarningPopup(String message, int statusCode) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Popups/StatusPopup.fxml"));

            AnchorPane popup = loader.load();

            StatusPopupController controller = loader.getController();
            controller.showMessage(message, statusCode);

            // Position the popup in the right top of the screen
            AnchorPane.setTopAnchor(popup, 20.0);
            AnchorPane.setRightAnchor(popup, 20.0);

            // Add the popup to the main content
            controllerPanel.getChildren().add(popup);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public RestAPIServer getServer() {
        return this.server;
    }
}