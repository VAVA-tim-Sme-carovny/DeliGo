package com.Deligo.DeliGo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.springframework.context.ApplicationContext;

public class JavaFXApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        ApplicationContext context = MainApp.getContext();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/javafx/views/MainView.fxml"));
        loader.setControllerFactory(context::getBean);

        AnchorPane root = loader.load();
        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("DeliGo - Finance Manager");
        primaryStage.show();
    }
}