package com.deligo.DatabaseManager.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
    private static final Logger logger = LogManager.getLogger(ConfigLoader.class);
    private static final Properties properties = new Properties();

    static {
        loadConfig();
    }

    private static void loadConfig() {
        String configPath = System.getProperty("config.properties"); // Možnosť načítať externý súbor
        try (InputStream input = (configPath != null) ? new FileInputStream(configPath) :
                ConfigLoader.class.getClassLoader().getResourceAsStream("config.properties")) {

            if (input == null) {
                throw new IOException("❌ config.properties file not found!");
            }
            properties.load(input);
            logger.info("✅ Config loaded successfully.");
            logAllProperties();
        } catch (IOException e) {
            logger.error("❌ Failed to load config: " + e.getMessage());
            throw new RuntimeException("Could not load config.properties", e);
        }
    }

    public static String get(String key) {
        // Skús načítať z environment variables, ak neexistuje, tak z properties
        String value = System.getenv(key);
        if (value == null) {
            value = properties.getProperty(key);
        }
        if (value == null) {
            logger.warn("⚠️ Configuration key '{}' not found.", key);
        }
        return value;
    }
    private static void logAllProperties() {
        if (properties.isEmpty()) {
            logger.warn("⚠️ No properties loaded from config file.");
        } else {
            logger.info("📋 Loaded properties from config.properties:");
            for (String key : properties.stringPropertyNames()) {
                String value = properties.getProperty(key);
                logger.info("  → {} = {}", key, value);
            }
        }
    }

}
