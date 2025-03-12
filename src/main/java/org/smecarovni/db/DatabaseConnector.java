package org.smecarovni.db;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.smecarovni.utils.ConfigLoader;
import org.smecarovni.exceptions.DatabaseException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {
    private static final Logger logger = LogManager.getLogger(DatabaseConnector.class); // Logger pre triedu
    private static Connection connection;

    static {
        try {
            // Načítanie konfigurácie pripojenia k databáze
            String url = ConfigLoader.get("db.url");
            String user = ConfigLoader.get("db.user");
            String password = ConfigLoader.get("db.password");

            // Pripojenie k databáze
            connection = DriverManager.getConnection(url, user, password);
            logger.info("✅ Connected to the database!"); // Logovanie úspešného pripojenia
        } catch (SQLException e) {
            logger.error("Database connection failed!", e); // Logovanie chyby pri pripojení k databáze
            throw new DatabaseException("Database connection failed!", e);
        }
    }

    // Metóda na získanie pripojenia k databáze
    public static Connection getConnection() {
        return connection;
    }

    // Metóda na zatvorenie pripojenia
    public static void close() {
        try {
            if (connection != null) {
                connection.close();
                logger.info("✅ Database connection closed."); // Logovanie úspešného uzavretia pripojenia
            }
        } catch (SQLException e) {
            logger.error("Failed to close database connection", e); // Logovanie chyby pri zatváraní pripojenia
            throw new DatabaseException("Failed to close database connection", e);
        }
    }
}
