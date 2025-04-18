package com.deligo.Logging.Adapter;

import com.deligo.Logging.Config.LoggerConfig;
import com.deligo.Logging.Gui.LoggingWindow;
import com.deligo.Model.BasicModels.*;
import org.apache.logging.log4j.Logger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;


/**
 * Adapter that routes logs to console, GUI, etc.
 */
public class LoggingAdapter {

    private final LoggingWindow loggingWindow;
    private final Logger logger;

    public LoggingAdapter(LoggingWindow loggingWindow) {
        this.loggingWindow = loggingWindow;
        this.logger = LoggerConfig.getGlobalLogger();
    }

    /**
     * Main method to add log entries.
     */
    public void log(LogType type, LogPriority priority, LogSource source, String message) {
        String timestamp = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        String formattedMessage = String.format("%s [%s]%s%s : %s",
                timestamp,
                type,
                (priority != null ? " [" + priority + "]" : ""),
                (source != null ? " [" + source + "]" : ""),
                message
        );

        // Log do konzoly
        switch (type) {
            case ERROR:
                logger.error(formattedMessage);
                break;
            case WARNING:
                logger.warn(formattedMessage);
                break;
            case SUCCESS:
                logger.info(formattedMessage);
                break;
        }

        // Log do GUI
        loggingWindow.addLogEntry(timestamp, message, type, priority, source);

        // Zápis do log súboru (priamo tu)
        writeToFile(formattedMessage);
    }

    private void writeToFile(String message) {
        String logFilePath = "logs/logs.log";

        try (FileWriter fw = new FileWriter(logFilePath, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.println(message);
        } catch (IOException e) {
            logger.error("Failed to write log to file: " + e.getMessage());
        }
    }
}
