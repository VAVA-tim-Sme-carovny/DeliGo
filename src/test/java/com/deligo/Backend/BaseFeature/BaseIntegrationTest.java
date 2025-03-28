package com.deligo.Backend.BaseFeature;

import com.deligo.ConfigLoader.ConfigLoader;
import com.deligo.DatabaseManager.DatabaseManager;
import com.deligo.Logging.Adapter.LoggingAdapter;
import com.deligo.Logging.LoggingManager;
import com.deligo.RestApi.RestAPIServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import java.io.IOException;

public abstract class BaseIntegrationTest {

    protected static LoggingAdapter logger;
    protected static ConfigLoader configLoader;
    protected static RestAPIServer restApiServer;
    protected static DatabaseManager dbManager;

    @BeforeAll
    public static void init() throws IOException {
        // Inicializácia loggera – môžeš použiť reálny logger alebo dummy podľa potreby
        LoggingManager.initialize();
        // Ak je potrebné počkať, kým sa logger inicializuje:
        while (LoggingManager.getAdapter() == null) {
            try {
                System.out.println("Waiting for LoggingManager initialization...");
                Thread.sleep(400);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        logger = LoggingManager.getAdapter();

        // Inicializácia config loader – cesta upravená podľa tvojho prostredia
        configLoader = new ConfigLoader("src/main/resources/config.yaml");

        // Inicializácia reálneho REST API servera
        restApiServer = new RestAPIServer(logger, configLoader);

        // Inicializácia databázového manažéra – môže byť dummy, ak zatiaľ nepoužívaš reálnu DB
        dbManager = new DatabaseManager();
    }

    @AfterAll
    public static void cleanup() {
        // Ak potrebuješ ukončiť server alebo uvoľniť zdroje, urob to tu
        // Napríklad restApiServer.stop();
    }
}
