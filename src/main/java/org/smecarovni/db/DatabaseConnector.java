package org.smecarovni.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.smecarovni.utils.ConfigLoader;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnector {
    private static final Logger logger = LogManager.getLogger(DatabaseConnector.class);
    private static final HikariDataSource dataSource;

    static {
        // Konfigurácia HikariCP
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(ConfigLoader.get("DB_URL")); // Získa JDBC URL z configu
        config.setUsername(ConfigLoader.get("DB_USER"));
        config.setPassword(ConfigLoader.get("DB_PASSWORD"));
        config.setMaximumPoolSize(10); // Počet max pripojení v poole
        config.setMinimumIdle(2); // Minimálny počet voľných spojení
        config.setIdleTimeout(30000); // 30s timeout na idle spojenia
        config.setMaxLifetime(1800000); // 30 min max životnosť spojenia
        config.setConnectionTimeout(10000); // 10s timeout na získanie spojenia

        dataSource = new HikariDataSource(config);
        logger.info("✅ HikariCP initialized successfully.");
    }

    // Metóda na získanie spojenia
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    // Metóda na zatvorenie connection poolu
    public static void close() {
        if (dataSource != null) {
            dataSource.close();
            logger.info("✅ HikariCP connection pool closed.");
        }
    }
}
