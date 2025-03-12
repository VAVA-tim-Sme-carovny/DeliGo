package org.smecarovni.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {

    private static final Logger logger = LogManager.getLogger(ConfigLoader.class);
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = ConfigLoader.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new IOException("❌ config.properties file not found in resources!");
            }
            properties.load(input);
            logger.info("✅ Config loaded successfully.");
        } catch (IOException e) {
            logger.error("❌ Failed to load config: " + e.getMessage());
            throw new RuntimeException("Could not load config.properties", e);
        }
    }

    public static String get(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            logger.warn("⚠️ Configuration key '{}' not found.", key);
        } else {
            logger.info("🔑 Retrieved configuration for key: '{}'", key);
        }
        return value;
    }
}
