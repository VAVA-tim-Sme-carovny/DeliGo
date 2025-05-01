package com.deligo.Frontend.Controllers.MainPage;

import com.deligo.Backend.BaseFeature.BaseFeature;
import com.deligo.ConfigLoader.ConfigLoader;
import com.deligo.Frontend.Controllers.InitializableWithParent;
import com.deligo.Frontend.Controllers.Popups.StatusPopupController;
import com.deligo.Frontend.Helpers.CustomControllerFactory;
import com.deligo.Logging.Adapter.LoggingAdapter;
import com.deligo.RestApi.RestAPIServer;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.Node;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

import com.deligo.Model.BasicModels.*;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class MainPageController extends BaseFeature {

    @FXML
    private AnchorPane mainContent;

    @FXML
    private VBox controllerPanel;

    @FXML
    private VBox rightPanel;

    @FXML
    private AnchorPane bottomPanel;

    @FXML
    private AnchorPane leftPanel;

    private final LoggingAdapter logger;
    private final RestAPIServer server;

    public MainPageController(ConfigLoader globalConfig, LoggingAdapter logger, RestAPIServer restApiServer) {
        super(globalConfig, logger, restApiServer);
        this.logger = logger;
        this.server = restApiServer;
    }

    public void initialize() {
        this.logger.log(LogType.INFO, LogPriority.HIGH, LogSource.FRONTEND, " MainPageController.initialize() called");

        loadMainContent("/Views/Content/MainPanel/MainContentPanel.fxml", false);
        loadControllerPanel("/Views/Controllers/MainTopPanelController.fxml", false);
        loadBottomPanel("/Views/Controllers/MainBottomPanelController.fxml", false);
    }


    public void loadControllerPanel(String fxmlPath, boolean deleteAll) {
        if(deleteAll) {
            this.clearAll();
        }
        try {
            ResourceBundle bundle = ResourceBundle.getBundle("i18n.messages", Locale.getDefault());
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath), bundle);


            loader.setControllerFactory(
                    new CustomControllerFactory(this, this.logger, this.server, this.globalConfig)
            );
            Node node = loader.load();

            Object controller = loader.getController();
            if (controller instanceof InitializableWithParent) {
                ((InitializableWithParent) controller).initializeWithParent(this);
            }
            controllerPanel.getChildren().clear();
            controllerPanel.getChildren().add(node);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Načíta obsah do hlavného panela (center content)
     */
    public void loadMainContent(String fxmlPath, boolean deleteAll) {
        if(deleteAll) {
            this.clearAll();
        }
        try {
            ResourceBundle bundle = ResourceBundle.getBundle("i18n.messages", Locale.getDefault());
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath), bundle);

            loader.setControllerFactory(
                    new CustomControllerFactory(this, this.logger, this.server, this.globalConfig)
            );
            Node node = loader.load();

            Object controller = loader.getController();
            if (controller instanceof InitializableWithParent) {
                ((InitializableWithParent) controller).initializeWithParent(this);
            }

            mainContent.getChildren().clear();
            mainContent.getChildren().add(node);

            // Zabezpečíme, že sa `node` roztiahne
            AnchorPane.setTopAnchor(node, 0.0);
            AnchorPane.setBottomAnchor(node, 0.0);
            AnchorPane.setLeftAnchor(node, 0.0);
            AnchorPane.setRightAnchor(node, 0.0);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadBottomPanel(String fxmlPath, boolean deleteAll) {
        if(deleteAll) {
            this.clearAll();
        }
        try {
            ResourceBundle bundle = ResourceBundle.getBundle("i18n.messages", Locale.getDefault());
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath), bundle);
            loader.setControllerFactory(
                    new CustomControllerFactory(this, this.logger, this.server, this.globalConfig)
            );
            Node node = loader.load();

            Object controller = loader.getController();
            if (controller instanceof InitializableWithParent) {
                ((InitializableWithParent) controller).initializeWithParent(this);
            }

            bottomPanel.getChildren().clear();
            bottomPanel.getChildren().add(node);

            // 🔧 Ukotvenie pre AnchorPane
            AnchorPane.setTopAnchor(node, 0.0);
            AnchorPane.setBottomAnchor(node, 0.0);
            AnchorPane.setLeftAnchor(node, 0.0);
            AnchorPane.setRightAnchor(node, 0.0);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadRightPanel(String fxmlPath, boolean deleteAll) {
        if(deleteAll) {
            this.clearAll();
        }
        try {
            ResourceBundle bundle = ResourceBundle.getBundle("i18n.messages", Locale.getDefault());
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath), bundle);
            loader.setControllerFactory(
                    new CustomControllerFactory(this, this.logger, this.server, this.globalConfig)
            );
            Node node = loader.load();

            Object controller = loader.getController();
            if (controller instanceof InitializableWithParent) {
                ((InitializableWithParent) controller).initializeWithParent(this);
            }

            rightPanel.getChildren().clear();
            rightPanel.getChildren().add(node);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadLeftPanel(String fxmlPath, boolean deleteAll) {
        if(deleteAll) {
            this.clearAll();
        }
        try {
            ResourceBundle bundle = ResourceBundle.getBundle("i18n.messages", Locale.getDefault());
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath), bundle);
            loader.setControllerFactory(
                    new CustomControllerFactory(this, this.logger, this.server, this.globalConfig)
            );
            Node node = loader.load();

            Object controller = loader.getController();
            if (controller instanceof InitializableWithParent) {
                ((InitializableWithParent) controller).initializeWithParent(this);
            }

            leftPanel.getChildren().clear();
            leftPanel.getChildren().add(node);


        } catch (IOException e) {
            e.printStackTrace();
        }
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
            mainContent.getChildren().add(popup);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }








    /**
     * Vymaže všetky panely
     */
    public void clearAll() {
        rightPanel.getChildren().clear();
        bottomPanel.getChildren().clear();
        leftPanel.getChildren().clear();
        mainContent.getChildren().clear();
    }

    public void clearRightPanel() {
        rightPanel.getChildren().clear();
    }

    public void clearBottomPanel() {
        bottomPanel.getChildren().clear();
    }

    public void clearLeftPanel() {
        leftPanel.getChildren().clear();
    }

    public void clearContentPanel() {
        mainContent.getChildren().clear();
    }

    public RestAPIServer getServer() {
        return this.server;
    }

}