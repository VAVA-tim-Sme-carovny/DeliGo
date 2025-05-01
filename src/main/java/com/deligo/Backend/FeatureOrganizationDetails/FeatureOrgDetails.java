package com.deligo.Backend.FeatureOrganizationDetails;

import com.deligo.Backend.BaseFeature.BaseFeature;
import com.deligo.Backend.FeatureUserManagement.UserManagementMessages;
import com.deligo.ConfigLoader.ConfigLoader;
import com.deligo.DatabaseManager.dao.GenericDAO;
import com.deligo.Logging.Adapter.LoggingAdapter;
import com.deligo.Model.BasicModels.*;
import com.deligo.Model.OrgDetails;
import com.deligo.Model.Response;
import com.deligo.Model.User;
import com.deligo.RestApi.RestAPIServer;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.util.List;
import java.util.regex.Pattern;

public class FeatureOrgDetails extends BaseFeature {

    // Regex pre validáciu telefónneho čísla a emailu
    private static final Pattern PHONE_PATTERN = Pattern.compile("^(\\+421\\s9\\d{2}\\s\\d{3}\\s\\d{3}|09\\d{2}\\s\\d{3}\\s\\d{3})$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$");

    protected final GenericDAO<OrgDetails> orgDetailsDAO;
    private Gson gson = new Gson();

    public FeatureOrgDetails(ConfigLoader globalConfig, LoggingAdapter logger, RestAPIServer restApiServer) {
        super(globalConfig, logger, restApiServer);
        this.orgDetailsDAO = new GenericDAO<>(OrgDetails.class, "info");
        logger.log(LogType.INFO, LogPriority.MIDDLE, LogSource.BECKEND, "FeatureOrgDetails Started.");
    }

    /**
     * Spracuje a aktualizuje organizačné detaily.
     * @param jsonData JSON reťazec s údajmi, ktorý obsahuje:
     *                 - openingTimes: Array of arrays (napr. [[8:30, 23:00], [...], ...])
     *                 - phoneNumber: String
     *                 - mail: String
     * @return JSON odpoveď s message a status (200 pre úspech, 500 pre chybu)
     */
    public String updateOrgDetails(String jsonData) {
        try {
            JsonObject jsonObject = JsonParser.parseString(jsonData).getAsJsonObject();

            String phone = jsonObject.has("phone") ? jsonObject.get("phone").getAsString() : null;
            String email = jsonObject.has("email") ? jsonObject.get("email").getAsString() : null;
            String openingHours = jsonObject.has("opening_hours") ? jsonObject.get("opening_hours").getAsString() : "";

            // Validácia telefónneho čísla
            if (phone != null && !phone.isEmpty() && !PHONE_PATTERN.matcher(phone).matches()) {
                logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND,
                        UserManagementMessages.INVALID_PHONE_FORMAT.getMessage(this.getLanguage()));
                return gson.toJson(new Response("Phone number is in invalid format", 400));
            }

            // Validácia emailu
            if (email != null && !email.isEmpty() && !EMAIL_PATTERN.matcher(email).matches()) {
                logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND,
                        UserManagementMessages.INVALID_EMAIL_FORMAT.getMessage(this.getLanguage()));
                return gson.toJson(new Response("Email is in invalid format", 400));
            }

            // Získaj existujúci záznam
            List<OrgDetails> existingDetails = orgDetailsDAO.getAll();
            OrgDetails orgDetails;

            if (existingDetails.isEmpty()) {
                orgDetails = new OrgDetails();
            } else {
                orgDetails = existingDetails.get(0);
            }

            // Aktualizuj hodnoty
            orgDetails.setOpeningTimes(openingHours);
            orgDetails.setPhoneNumber(phone);
            orgDetails.setMail(email);

            // Ulož do databázy
            if (orgDetails.getId() > 0) {
                orgDetailsDAO.update(orgDetails.getId(), orgDetails);
            } else {
                orgDetailsDAO.insert(orgDetails);
            }

            logger.log(LogType.SUCCESS, LogPriority.MIDDLE, LogSource.BECKEND,
                    UserManagementMessages.ORG_DETAILS_UPDATED.getMessage(this.getLanguage()));
            return gson.toJson(new Response("Organization details were updated successfully", 200));

        } catch (JsonSyntaxException e) {
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND,
                    UserManagementMessages.INVALID_REQUEST_FORMAT.getMessage(this.getLanguage()));
            return gson.toJson(new Response("Invalid request format", 400));
        } catch (Exception e) {
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND,
                    UserManagementMessages.ORG_DETAILS_ERROR.getMessage(this.getLanguage()) + ": " + e.getMessage());
            return gson.toJson(new Response("Error updating organization details: " + e.getMessage(), 500));
        }
    }


    // Simulovaná metóda na zápis do databázy (tu iba logovanie)
    private boolean writeToDatabase(OrgDetails details) {
        logger.log(LogType.INFO, LogPriority.MIDDLE, LogSource.BECKEND, "Simulating DB write: " + details.toString());
        return true;
    }

}
