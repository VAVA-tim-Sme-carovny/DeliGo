package com.deligo.Frontend.Controllers.MainPage;

import com.deligo.Backend.BaseFeature.BaseFeature;
import com.deligo.ConfigLoader.ConfigLoader;
import com.deligo.Frontend.Controllers.InitializableWithParent;
import com.deligo.Frontend.Helpers.CustomControllerFactory;
import com.deligo.Logging.Adapter.LoggingAdapter;
import com.deligo.RestApi.RestAPIServer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
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

    public MainPageController(ConfigLoader globalConfig, LoggingAdapter logger, RestAPIServer restApiServer) {
        super(globalConfig, logger, restApiServer);
        this.logger = logger;
        this.server = restApiServer;
    }
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




    public void initialize() {
        this.logger.log(LogType.INFO, LogPriority.HIGH, LogSource.FRONTEND, " MainPageController.initialize() called");

        loadMainContent("/Views/Content/MainPanel/MainContentPanel.fxml");
        loadControllerPanel("/Views/Controllers/MainTopPanelController.fxml");
    }


    public void loadControllerPanel(String fxmlPath) {
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
    public void loadMainContent(String fxmlPath) {
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



    public void loadBottomPanel(String fxmlPath) {
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

            // 🔽 Ukotvenie do AnchorPane, aby sa BorderPane roztiahol cez celú plochu
            AnchorPane.setTopAnchor(node, 0.0);
            AnchorPane.setBottomAnchor(node, 0.0);
            AnchorPane.setLeftAnchor(node, 0.0);
            AnchorPane.setRightAnchor(node, 0.0);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void loadRightPanel(String fxmlPath) {
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


    public void loadLeftPanel(String fxmlPath) {
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
