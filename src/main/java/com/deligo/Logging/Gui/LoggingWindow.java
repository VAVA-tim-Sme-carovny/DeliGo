package com.deligo.Logging.Gui;

import com.deligo.Model.BasicModels.*;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class LoggingWindow {

    private VBox logContainer;
    private ScrollPane scrollPane;

    private ComboBox<LogType> typeFilter;
    private ComboBox<LogSource> sourceFilter;

    private List<LogRecord> allLogs = new ArrayList<>();

    public void start(Stage primaryStage) {
        primaryStage.setTitle("📝 com.deligo.Logging Window");

        logContainer = new VBox(5);

        scrollPane = new ScrollPane(logContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.vvalueProperty().bind(logContainer.heightProperty());

        BorderPane root = new BorderPane();

        // Filters
        typeFilter = new ComboBox<>();
        typeFilter.getItems().add(null); // null = zobrazí všetky typy (žiadny filter)
        typeFilter.getItems().addAll(LogType.ERROR, LogType.WARNING, LogType.SUCCESS);
        typeFilter.setValue(null);

        sourceFilter = new ComboBox<>();
        sourceFilter.getItems().add(null); // null = zobrazí všetky source
        sourceFilter.getItems().addAll(LogSource.REST_API, LogSource.BECKEND, LogSource.FRONTEND, LogSource.MAVEN);
        sourceFilter.setValue(null);

        typeFilter.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(LogType item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? "All Types" : item.name());
            }
        });
        typeFilter.setButtonCell(typeFilter.getCellFactory().call(null));

        sourceFilter.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(LogSource item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? "All Sources" : item.name());
            }
        });
        sourceFilter.setButtonCell(sourceFilter.getCellFactory().call(null));

        HBox filterBar = new HBox(10, new Label("Type:"), typeFilter, new Label("Source:"), sourceFilter);
        filterBar.setPadding(new Insets(10));

        root.setTop(filterBar);
        root.setCenter(scrollPane);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void addLogEntry(String timestamp, String message, LogType type, LogPriority priority, LogSource source) {
        Platform.runLater(() -> {
            LogRecord record = new LogRecord(timestamp, message, type, priority, source);
            allLogs.add(record);

            if (matchesFilter(record)) {
                logContainer.getChildren().add(buildLogLine(record));
            }
        });
    }

    private boolean matchesFilter(LogRecord record) {
        LogType selectedType = typeFilter.getValue();
        LogSource selectedSource = sourceFilter.getValue();

        boolean typeMatch = selectedType == null || record.type == selectedType;
        boolean sourceMatch = selectedSource == null || record.source == selectedSource;

        return typeMatch && sourceMatch;
    }

    private void filterLogs() {
        logContainer.getChildren().clear();

        for (LogRecord record : allLogs) {
            if (matchesFilter(record)) {
                logContainer.getChildren().add(buildLogLine(record));
            }
        }
    }

    private TextFlow buildLogLine(LogRecord record) {
        TextFlow logLine = new TextFlow();

        Text timeText = new Text(record.timestamp + " ");
        timeText.setStyle("-fx-font-weight: bold;");

        Text typeText = new Text("[" + record.type.name() + "]");
        typeText.setFill(getColorForLogType(record.type));
        typeText.setStyle("-fx-font-weight: bold; ");

        logLine.getChildren().addAll(timeText, typeText, new Text(" "));

        if (record.priority != null) {
            Text priorityText = new Text("[" + record.priority.name() + "]");
            priorityText.setFill(getColorForLogPriority(record.priority));
            priorityText.setStyle("-fx-font-weight: bold; ");
            logLine.getChildren().addAll(priorityText, new Text(" "));
        }

        if (record.source != null) {
            Text sourceText = new Text("[" + record.source.name() + "]");
            sourceText.setStyle("-fx-font-weight: bold;");
            logLine.getChildren().addAll(sourceText, new Text(" "));
        }

        Text msgText = new Text(": " + record.message);
        logLine.getChildren().add(msgText);

        return logLine;
    }

    private javafx.scene.paint.Color getColorForLogType(LogType type) {
        return switch (type) {
            case ERROR -> javafx.scene.paint.Color.RED;
            case WARNING -> javafx.scene.paint.Color.ORANGE;
            case SUCCESS -> javafx.scene.paint.Color.GREEN;
            case INFO -> javafx.scene.paint.Color.BLUE;
        };
    }

    private javafx.scene.paint.Color getColorForLogPriority(LogPriority priority) {
        return switch (priority) {
            case LOW -> javafx.scene.paint.Color.BLUE;
            case MIDDLE -> javafx.scene.paint.Color.ORANGE;
            case HIGH -> javafx.scene.paint.Color.PURPLE;
        };
    }

    private static class LogRecord {
        String timestamp;
        String message;
        LogType type;
        LogPriority priority;
        LogSource source;

        public LogRecord(String timestamp, String message, LogType type, LogPriority priority, LogSource source) {
            this.timestamp = timestamp;
            this.message = message;
            this.type = type;
            this.priority = priority;
            this.source = source;
        }
    }
}
