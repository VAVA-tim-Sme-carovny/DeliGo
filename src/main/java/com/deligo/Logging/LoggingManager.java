package com.deligo.Logging;

import com.deligo.Logging.Adapter.LoggingAdapter;
import com.deligo.Logging.Gui.LoggingWindow;
import javafx.application.Application;
import javafx.stage.Stage;

public class LoggingManager extends Application {

    private static LoggingAdapter adapter;

    @Override
    public void start(Stage primaryStage) {
        // Toto je hlavný JavaFX štart
        LoggingWindow window = new LoggingWindow();
        window.start(primaryStage);
        adapter = new LoggingAdapter(window);
    }

    public static void initialize() {
        new Thread(() -> Application.launch(LoggingManager.class)).start();
    }

    public static LoggingAdapter getAdapter() {
        return adapter;
    }
}
