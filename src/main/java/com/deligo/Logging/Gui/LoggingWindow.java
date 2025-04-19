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
import java.util.stream.Collectors;

public class LoggingWindow {

    private VBox logContainer;
    private ScrollPane scrollPane;

    private ComboBox<LogType> typeFilter;
    private ComboBox<LogSource> sourceFilter;
    private TextField searchField;

    private List<LogRecord> allLogs = new ArrayList<>();

    public void start(Stage primaryStage) {
        primaryStage.setTitle("📝 Logging Window");

        logContainer = new VBox(5);
        scrollPane = new ScrollPane(logContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.vvalueProperty().bind(logContainer.heightProperty());

        BorderPane root = new BorderPane();

        typeFilter = new ComboBox<>();
        typeFilter.getItems().add(null);
        typeFilter.getItems().addAll(LogType.values());
        typeFilter.setValue(null);

        sourceFilter = new ComboBox<>();
        sourceFilter.getItems().add(null);
        sourceFilter.getItems().addAll(LogSource.values());
        sourceFilter.setValue(null);

        searchField = new TextField();
        searchField.setPromptText("Search logs...");

        typeFilter.setOnAction(e -> applyFilters());
        sourceFilter.setOnAction(e -> applyFilters());
        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());

        typeFilter.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(LogType item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? "All Types" : item.name());
            }
        });
        typeFilter.setButtonCell(typeFilter.getCellFactory().call(null));

        sourceFilter.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(LogSource item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? "All Sources" : item.name());
            }
        });
        sourceFilter.setButtonCell(sourceFilter.getCellFactory().call(null));

        HBox filterBar = new HBox(10,
                new Label("Type:"), typeFilter,
                new Label("Source:"), sourceFilter,
                new Label("Search:"), searchField
        );
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
                logContainer.getChildren().add(createLogNode(record));
            }
        });
    }

    private void applyFilters() {
        Platform.runLater(() -> {
            logContainer.getChildren().clear();
            List<LogRecord> filtered = allLogs.stream()
                    .filter(this::matchesFilter)
                    .collect(Collectors.toList());

            for (LogRecord r : filtered) {
                logContainer.getChildren().add(createLogNode(r));
            }
        });
    }

    private boolean matchesFilter(LogRecord record) {
        LogType selectedType = typeFilter.getValue();
        LogSource selectedSource = sourceFilter.getValue();
        String query = searchField.getText().toLowerCase();

        boolean typeMatches = (selectedType == null || record.getType() == selectedType);
        boolean sourceMatches = (selectedSource == null || record.getSource() == selectedSource);
        boolean textMatches = query.isEmpty() || record.getMessage().toLowerCase().contains(query);

        return typeMatches && sourceMatches && textMatches;
    }

    private TextFlow createLogNode(LogRecord record) {
        TextFlow logLine = new TextFlow();

        Text timeText = new Text(record.getTimestamp() + " ");
        timeText.setStyle("-fx-font-weight: bold;");

        Text typeText = new Text("[" + record.getType().name() + "]");
        typeText.setFill(getColorForLogType(record.getType()));
        typeText.setStyle("-fx-font-weight: bold; ");

        logLine.getChildren().addAll(timeText, typeText, new Text(" "));

        if (record.getPriority() != null) {
            Text priorityText = new Text("[" + record.getPriority().name() + "]");
            priorityText.setFill(getColorForLogPriority(record.getPriority()));
            priorityText.setStyle("-fx-font-weight: bold; ");
            logLine.getChildren().addAll(priorityText, new Text(" "));
        }

        if (record.getSource() != null) {
            Text sourceText = new Text("[" + record.getSource().name() + "]");
            sourceText.setStyle("-fx-font-weight: bold;");
            logLine.getChildren().addAll(sourceText, new Text(" "));
        }

        Text msgText = new Text(": " + record.getMessage());
        logLine.getChildren().add(msgText);

        return logLine;
    }

    private javafx.scene.paint.Color getColorForLogType(LogType type) {
        switch (type) {
            case ERROR:
                return javafx.scene.paint.Color.RED;
            case WARNING:
                return javafx.scene.paint.Color.ORANGE;
            case SUCCESS:
                return javafx.scene.paint.Color.GREEN;
            case INFO:
                return javafx.scene.paint.Color.BLUE;
            default:
                return javafx.scene.paint.Color.BLACK;
        }
    }

    private javafx.scene.paint.Color getColorForLogPriority(LogPriority priority) {
        switch (priority) {
            case LOW:
                return javafx.scene.paint.Color.BLUE;
            case MIDDLE:
                return javafx.scene.paint.Color.ORANGE;
            case HIGH:
                return javafx.scene.paint.Color.PURPLE;
            default:
                return javafx.scene.paint.Color.GRAY;
        }
    }

    private static class LogRecord {
        private final String timestamp;
        private final String message;
        private final LogType type;
        private final LogPriority priority;
        private final LogSource source;

        public LogRecord(String timestamp, String message, LogType type, LogPriority priority, LogSource source) {
            this.timestamp = timestamp;
            this.message = message;
            this.type = type;
            this.priority = priority;
            this.source = source;
        }

        public String getTimestamp() { return timestamp; }
        public String getMessage() { return message; }
        public LogType getType() { return type; }
        public LogPriority getPriority() { return priority; }
        public LogSource getSource() { return source; }
    }
}