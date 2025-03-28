package com.deligo.Backend.BaseFeature;

import com.deligo.ConfigLoader.ConfigLoader;
import com.deligo.DatabaseManager.DatabaseManager;
import com.deligo.Logging.Adapter.LoggingAdapter;
import com.deligo.Model.BasicModels.*;
import com.deligo.RestApi.RestAPIServer;
import org.flywaydb.core.internal.database.base.Database;

public abstract class BaseFeature {
    protected ConfigLoader globalConfig;
    protected static String language = "en";
    protected LoggingAdapter logger;
    protected RestAPIServer server;
    protected DatabaseManager databaseManager;

    public BaseFeature(ConfigLoader globalConfig, LoggingAdapter logger, RestAPIServer restApiServer, DatabaseManager db) {
        this.globalConfig = globalConfig;
        this.language = "en";
        this.logger = logger;
        this.server = restApiServer;
        this.databaseManager = db;
        this.updateLanguage(globalConfig);
    }



   protected void log(LogType type, LogPriority priority, LogSource source, String message) {
        this.logger.log(type, priority, source, message);
   }

    public static void updateLanguage(ConfigLoader config) {
        language = config.getConfigValue("device", "language", String.class);
    }

    public String getLanguage() {
        return language;
    }

    protected Object read(Class<?> type, Object query) {
        return this.databaseManager.readData(type, query);
    }
}