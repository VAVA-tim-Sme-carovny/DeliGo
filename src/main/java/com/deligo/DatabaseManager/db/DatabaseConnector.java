package com.deligo.DatabaseManager.db;

import com.deligo.DatabaseManager.exceptions.DatabaseException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.deligo.DatabaseManager.utils.ConfigLoader;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnector {
    private static final Logger logger = LogManager.getLogger(DatabaseConnector.class);
    private static HikariDataSource dataSource;

    static {
        initialize();
    }

    private static void initialize() {
        try {
            Class.forName("org.postgresql.Driver");
            logger.info("✅ PostgreSQL driver loaded.");
        } catch (ClassNotFoundException e) {
            logger.error("❌ PostgreSQL driver NOT FOUND in classpath!", e);
        }


        try {
            logger.info("🛠️ Starting HikariCP initialization...");

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(ConfigLoader.get("DB_URL"));
            config.setUsername(ConfigLoader.get("DB_USER"));
            config.setPassword(ConfigLoader.get("DB_PASSWORD"));
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            config.setIdleTimeout(30000);
            config.setMaxLifetime(1800000);
            config.setConnectionTimeout(3000);
            config.setConnectionTestQuery("SELECT 1");

            logger.info("🔍 JDBC URL: {}", config.getJdbcUrl());
            logger.info("🔍 DB User: {}", config.getUsername());

            dataSource = new HikariDataSource(config);
            logger.info("✅ HikariCP initialized successfully.");

        } catch (Exception e) {
            logger.error("❌ Failed to initialize HikariCP: {}", e.getMessage(), e);
        }
    }

    // Metóda na získanie spojenia
    public static Connection getConnection() throws SQLException {
        logger.info("👉 Requesting connection from HikariCP...");

        long start = System.currentTimeMillis();
        try {
            Connection conn = dataSource.getConnection();
            long duration = System.currentTimeMillis() - start;
            logger.info("✅ Connection obtained in {} ms: {}", duration, conn);
            return conn;
        } catch (SQLException e) {
            long duration = System.currentTimeMillis() - start;
            logger.error("❌ Failed to obtain connection after {} ms", duration, e);
            throw e;
        }
    }


    // Metóda na zatvorenie connection poolu
    public static void close() {
        if (dataSource != null) {
            dataSource.close();
            logger.info("✅ HikariCP connection pool closed.");
        }
    }
}
