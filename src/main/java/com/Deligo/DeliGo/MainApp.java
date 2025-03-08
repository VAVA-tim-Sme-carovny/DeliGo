//package com.Deligo.DeliGo;
//
//import javafx.application.Application;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Scene;
//import javafx.scene.layout.AnchorPane;
//import javafx.stage.Stage;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.boot.builder.SpringApplicationBuilder;
//import org.springframework.context.ConfigurableApplicationContext;
//
//import java.io.IOException;
//
//@SpringBootApplication
//public class MainApp extends Application {
//
//	private static ConfigurableApplicationContext context;
//
//	@Override
//	public void init() {
//		context = new SpringApplicationBuilder(MainApp.class)
//				.headless(false)  // JavaFX needs this
//				.run();
//	}
//
//	@Override
//	public void start(Stage primaryStage) {
//		try {
//			FXMLLoader loader = new FXMLLoader(getClass().getResource("/javafx/views/MainView.fxml"));
//			loader.setControllerFactory(context::getBean);  // Allow Spring to inject controllers
//
//			AnchorPane root = loader.load();
//			Scene scene = new Scene(root);
//
//			primaryStage.setTitle("DeliGo - Finance Manager");
//			primaryStage.setScene(scene);
//			primaryStage.show();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//
//	@Override
//	public void stop() {
//		context.close(); // Shut down Spring when JavaFX closes
//	}
//
//	public static void main(String[] args) {
//		launch(args);
//	}
//}

package com.Deligo.DeliGo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lombok.Getter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;

@SpringBootApplication
public class MainApp extends Application {

	@Getter
    private static ConfigurableApplicationContext context;
	@Getter
    private static Stage primaryStage;

	@Override
	public void init() {
		// Initialize Spring Boot
		context = SpringApplication.run(MainApp.class);
	}

	@Override
	public void start(Stage stage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/javafx/views/MainView.fxml"));
			loader.setControllerFactory(context::getBean); // Inject Spring beans into controllers
			AnchorPane root = loader.load();

			Scene scene = new Scene(root);
			stage.setTitle("DeliGo - Finance Manager");
			stage.setScene(scene);
			stage.show();

			primaryStage = stage;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void stop() {
		// Close Spring Boot context when JavaFX stops
		context.close();
	}

    public static void main(String[] args) {
		launch(args);
	}
}