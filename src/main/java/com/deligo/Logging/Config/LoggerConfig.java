package com.deligo.Logging.Config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Central Log4j2 config class.
 */
public class LoggerConfig {

    private static final Logger logger = LogManager.getLogger("GlobalLogger");

    public static Logger getLogger(Class<?> clazz) {
        return LogManager.getLogger(clazz);
    }

    public static Logger getGlobalLogger() {
        return logger;
    }

    public static void logAppStart() {
        logger.info("✅ Logger initialized and application started.");
    }
}
