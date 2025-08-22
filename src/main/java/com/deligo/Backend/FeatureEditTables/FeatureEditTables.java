package com.deligo.Backend.FeatureEditTables;

import com.deligo.Backend.BaseFeature.BaseFeature;
import com.deligo.ConfigLoader.ConfigLoader;
import com.deligo.DatabaseManager.dao.GenericDAO;
import com.deligo.Logging.Adapter.LoggingAdapter;
import com.deligo.Model.BasicModels.*;
import com.deligo.Model.Response;
import com.deligo.Model.Tables;
import com.deligo.RestApi.RestAPIServer;
import com.google.gson.*;

import java.util.Optional;

public class FeatureEditTables extends BaseFeature {

    private final Gson gson = new Gson();
    private final GenericDAO<Tables> tableDAO;

    public FeatureEditTables(ConfigLoader config, LoggingAdapter logger, RestAPIServer server) {
        super(config, logger, server);
        this.tableDAO = new GenericDAO<>(Tables.class, "tables");
    }

    public String editTable(String jsonData) {
        try {
            JsonObject jsonObject = JsonParser.parseString(jsonData).getAsJsonObject();

            String name = jsonObject.get("name").getAsString();
            String newNameElement = jsonObject.get("newName").getAsString();

            if (newNameElement == null) {
                logger.log(LogType.SUCCESS, LogPriority.MIDDLE, LogSource.BECKEND, TableStructureMessages.TABLE_ADDED_ERROR.getMessage(this.getLanguage()));
                return gson.toJson(new Response(TableStructureMessages.TABLE_ADDED_ERROR.getMessage(this.getLanguage()), 200));
            } else {
                // ⬇️ Overenie existencie
                Optional<Tables> tableOpt = tableDAO.findOneByField("name", name);
                if (tableOpt.isEmpty() || name == "null") {
                    Tables newTable = new Tables(newNameElement.toString());
                    tableDAO.insert(newTable);
                    globalConfig.updateConfigValue("device", "id", newTable.getName());
                    String msg = TableStructureMessages.TABLE_ADDED_SUCCESS.getMessage(this.getLanguage());
                    logger.log(LogType.ERROR, LogPriority.MIDDLE, LogSource.BECKEND, msg);
                    return gson.toJson(new Response(msg, 200));
                }else{
                    Tables table = tableOpt.get();
                    table.setName(newNameElement);
                    tableDAO.update(table.getId(), table);
                    globalConfig.updateConfigValue("device", "id", table.getName());

                    logger.log(LogType.SUCCESS, LogPriority.MIDDLE, LogSource.BECKEND, TableStructureMessages.TABLE_UPDATED_SUCCESS.getMessage(this.getLanguage()));
                    return gson.toJson(new Response(TableStructureMessages.TABLE_UPDATED_SUCCESS.getMessage(this.getLanguage()), 200));
                }


            }

        } catch (JsonSyntaxException e) {
            String msg = TableStructureMessages.INVALID_REQUEST_FORMAT.getMessage(this.getLanguage());
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND, msg);
            return gson.toJson(new Response(msg, 500));
        } catch (Exception e) {
            String msg = TableStructureMessages.INVALID_REQUEST_FORMAT.getMessage(this.getLanguage());
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND, msg);
            return gson.toJson(new Response(msg, 500));
        }
    }

    public String deleteTable(String jsonData) {
        try {
            JsonObject jsonObject = JsonParser.parseString(jsonData).getAsJsonObject();
            String name = jsonObject.get("name").getAsString();

            // ⬇️ Skontroluj, či existuje stôl

            Optional<Tables> tableOpt = tableDAO.findOneByField("name", name);
            if (tableOpt.isEmpty()) {
                String msg = TableStructureMessages.TABLE_NOT_FOUND.getMessage(this.getLanguage());
                logger.log(LogType.ERROR, LogPriority.MIDDLE, LogSource.BECKEND, msg);
                return gson.toJson(new Response(msg, 500));
            }

            // ⬇️ Vymazanie záznamu z DB
            globalConfig.updateConfigValue("device", "id", null);
            tableDAO.delete(tableOpt.get().getId());

            logger.log(LogType.SUCCESS, LogPriority.MIDDLE, LogSource.BECKEND, TableStructureMessages.TABLE_DELETED_SUCCESS.getMessage(this.getLanguage()));
            return gson.toJson(new Response(TableStructureMessages.TABLE_DELETED_SUCCESS.getMessage(this.getLanguage()), 200));

        } catch (JsonSyntaxException e) {
            String msg = TableStructureMessages.INVALID_REQUEST_FORMAT.getMessage(this.getLanguage());
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND, msg);
            return gson.toJson(new Response(msg, 500));
        } catch (Exception e) {
            String msg = TableStructureMessages.TABLE_DELETED_ERROR.getMessage(this.getLanguage());
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND, msg + ": " + e.getMessage());
            return gson.toJson(new Response(msg, 500));
        }
    }
}

