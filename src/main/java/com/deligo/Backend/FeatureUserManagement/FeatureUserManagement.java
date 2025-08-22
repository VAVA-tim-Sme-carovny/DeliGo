package com.deligo.Backend.FeatureUserManagement;

import com.deligo.Backend.BaseFeature.BaseFeature;
import com.deligo.Backend.FeatureUserLogin.UserLoginMessages;
import com.deligo.ConfigLoader.ConfigLoader;
import com.deligo.DatabaseManager.dao.GenericDAO;
import com.deligo.Logging.Adapter.LoggingAdapter;
import com.deligo.Model.*;
import com.deligo.Model.BasicModels.LogPriority;
import com.deligo.Model.BasicModels.LogSource;
import com.deligo.Model.BasicModels.LogType;
import com.deligo.RestApi.RestAPIServer;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Trieda FeatureUserManagement implementuje funkcionalitu pre správu používateľov
 * a detailov organizácie v aplikácii DeliGo.
 * Poskytuje metódy na úpravu, mazanie a získavanie používateľov a ich rolí.
 */
public class FeatureUserManagement extends BaseFeature {
    // DAO pre prístup k databáze používateľov
    private final GenericDAO<User> userDAO;
    // DAO pre prístup k detailom organizácie
    private final GenericDAO<OrgDetails> orgDetailsDAO;
    // Gson pre serializáciu/deserializáciu JSON
    private final Gson gson;

    // Regulárne výrazy pre validáciu
    private static final Pattern PHONE_PATTERN = Pattern.compile("^(\\+421\\s9\\d{2}\\s\\d{3}\\s\\d{3}|09\\d{2}\\s\\d{3}\\s\\d{3})$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9_.]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$");

    /**
     * Konštruktor inicializuje DAO pre používateľov, detaily organizácie a logovací systém.
     *
     * @param globalConfig  globálna konfigurácia aplikácie
     * @param logger        logovací adaptér
     * @param restApiServer REST API server
     */
    public FeatureUserManagement(ConfigLoader globalConfig, LoggingAdapter logger, RestAPIServer restApiServer) {
        super(globalConfig, logger, restApiServer);
        this.userDAO = new GenericDAO<>(User.class, "users");
        this.orgDetailsDAO = new GenericDAO<>(OrgDetails.class, "organization_details");
        this.gson = new Gson();
        logger.log(LogType.INFO, LogPriority.MIDDLE, LogSource.BECKEND,
                UserManagementMessages.PROCESS_NAME.getMessage(this.getLanguage()));
    }

    /**
     * Upraví existujúceho používateľa, najmä jeho role a značky.
     *
     * @param jsonData JSON obsahujúci údaje o používateľovi na úpravu
     * @return JSON odpoveď o výsledku operácie
     */
    public String editUser(String jsonData) {
        try {
            // Deserializácia JSON na mapu
            JsonObject json = JsonParser.parseString(jsonData).getAsJsonObject();

            int userId = json.get("userId").getAsInt();
            String username = json.get("username").getAsString();
            String newRoles = json.has("roles") ? json.get("roles").getAsString() : "customer";

            //TODO neskor dofixovat
            // Získanie rolí a značiek z požiadavky

            // Nájdenie používateľa podľa ID
            Optional<User> userOpt = userDAO.findOneByField("id", userId);
            if (!userOpt.isPresent()) {
                logger.log(LogType.ERROR, LogPriority.MIDDLE, LogSource.BECKEND,
                        UserManagementMessages.USER_NOT_FOUND.getMessage(this.getLanguage()));
                return gson.toJson(new Response("User not found", 500));
            }

            User user = userOpt.get();

            // Overenie, či sa používateľské meno zhoduje
            if (!user.getUsername().equals(username)) {
                logger.log(LogType.ERROR, LogPriority.MIDDLE, LogSource.BECKEND,
                        "Username mismatch");
                return gson.toJson(new Response("Username does not match with the user ID", 500));
            }

            // Aktualizácia rolí a značiek
            user.setRoles(newRoles);
//            user.setTags(tags);

            // Aktualizácia používateľa v databáze
            userDAO.update(user.getId(), user);

            logger.log(LogType.SUCCESS, LogPriority.MIDDLE, LogSource.BECKEND,
                    UserManagementMessages.USER_ROLES_UPDATED.getMessage(this.getLanguage()));
            return gson.toJson(new Response("User roles and tags were successfully updated", 200));

        } catch (JsonSyntaxException e) {
            // Chyba pri parsovaní JSON
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND,
                    UserManagementMessages.INVALID_REQUEST_FORMAT.getMessage(this.getLanguage()));
            return gson.toJson(new Response("Invalid request format", 500));
        } catch (Exception e) {
            // Iné chyby
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND,
                    UserManagementMessages.USER_UPDATE_ERROR.getMessage(this.getLanguage()) + ": " + e.getMessage());
            return gson.toJson(new Response("Error updating user: " + e.getMessage(), 500));
        }
    }

    /**
     * Odstráni používateľa zo systému.
     *
     * @param json JSON obsahujúci ID a používateľské meno používateľa na odstránenie
     * @return JSON odpoveď o výsledku operácie
     */
    public String deleteUser(String json) {
        try {
            // Deserializácia JSON na mapu
            Type mapType = new TypeToken<Map<String, Object>>() {
            }.getType();
            Map<String, Object> requestData = gson.fromJson(json, mapType);
            String userId = (String) requestData.get("userId");
            String username = (String) requestData.get("username");

            // Nájdenie používateľa podľa ID
            Optional<User> userOpt = userDAO.findOneByField("id", userId);
            if (!userOpt.isPresent()) {
                logger.log(LogType.ERROR, LogPriority.MIDDLE, LogSource.BECKEND,
                        UserManagementMessages.USER_NOT_FOUND.getMessage(this.getLanguage()));
                return gson.toJson(new Response("User not found", 404));
            }

            User user = userOpt.get();

            // Overenie, či sa používateľské meno zhoduje
            if (!user.getUsername().equals(username)) {
                logger.log(LogType.ERROR, LogPriority.MIDDLE, LogSource.BECKEND,
                        "Username mismatch");
                return gson.toJson(new Response("Username does not match with the user ID", 400));
            }

            // Odstránenie používateľa
            userDAO.delete(user.getId());

            logger.log(LogType.SUCCESS, LogPriority.MIDDLE, LogSource.BECKEND,
                    UserManagementMessages.USER_DELETED.getMessage(this.getLanguage()));
            return gson.toJson(new Response("User was successfully deleted", 200));

        } catch (JsonSyntaxException e) {
            // Chyba pri parsovaní JSON
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND,
                    UserManagementMessages.INVALID_REQUEST_FORMAT.getMessage(this.getLanguage()));
            return gson.toJson(new Response("Invalid request format", 400));
        } catch (Exception e) {
            // Iné chyby
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND,
                    UserManagementMessages.USER_DELETE_ERROR.getMessage(this.getLanguage()) + ": " + e.getMessage());
            return gson.toJson(new Response("Error deleting user: " + e.getMessage(), 500));
        }
    }
}