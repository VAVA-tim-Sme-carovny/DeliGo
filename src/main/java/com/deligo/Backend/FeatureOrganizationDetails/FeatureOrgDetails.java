package com.deligo.Backend.FeatureOrganizationDetails;

import com.deligo.Backend.BaseFeature.BaseFeature;
import com.deligo.ConfigLoader.ConfigLoader;
import com.deligo.Logging.Adapter.LoggingAdapter;
import com.deligo.Model.BasicModels.*;
import com.deligo.Model.OrgDetails;
import com.deligo.Model.Response;
import com.deligo.RestApi.RestAPIServer;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.List;
import java.util.regex.Pattern;

public class FeatureOrgDetails extends BaseFeature {

    // Regex pre validáciu telefónneho čísla a emailu
    private static final Pattern PHONE_PATTERN = Pattern.compile("^(\\+421\\s9\\d{2}\\s\\d{3}\\s\\d{3}|09\\d{2}\\s\\d{3}\\s\\d{3})$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$");

    private Gson gson = new Gson();

    public FeatureOrgDetails(ConfigLoader globalConfig, LoggingAdapter logger, RestAPIServer restApiServer) {
        super(globalConfig, logger, restApiServer);
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
        logger.log(LogType.INFO, LogPriority.MIDDLE, LogSource.BECKEND, "Processing organization details update request.");

        OrgDetails details;
        try {
            details = gson.fromJson(jsonData, OrgDetails.class);
        } catch (JsonSyntaxException e) {
            String msg = OrgDetailsMessages.INVALID_JSON.getMessage(this.getLanguage()) + e.getMessage();
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND, msg);
            return gson.toJson(new Response(msg, 500));
        }

        // Validácia telefónneho čísla
        if (details.getPhoneNumber() == null || !PHONE_PATTERN.matcher(details.getPhoneNumber()).matches()) {
            String msg = OrgDetailsMessages.INVALID_PHONE.getMessage(this.getLanguage());
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND, msg);
            return gson.toJson(new Response(msg, 500));
        }

        // Validácia emailu
        if (details.getMail() == null || !EMAIL_PATTERN.matcher(details.getMail()).matches()) {
            String msg = OrgDetailsMessages.INVALID_EMAIL.getMessage(this.getLanguage());
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND, msg);
            return gson.toJson(new Response(msg, 500));
        }

        // Spracovanie otváracích časov
        List<List<String>> openingTimes = details.getOpeningTimes();
        if (openingTimes != null) {
            for (int i = 0; i < openingTimes.size(); i++) {
                List<String> times = openingTimes.get(i);
                // Predpokladáme, že pole obsahuje aspoň dva prvky ("od" a "do")
                if (times == null || times.size() < 2 ||
                        ((times.get(0) == null || times.get(0).isEmpty()) && (times.get(1) == null || times.get(1).isEmpty()))) {
                    // Ak oba časy sú null alebo prázdne, nastav "zatvorené"
                    openingTimes.set(i, List.of("zatvorené", "zatvorené"));
                }
            }
        } else {
            logger.log(LogType.WARNING, LogPriority.MIDDLE, LogSource.BECKEND, "Opening times not provided.");
        }

        // Simulácia zápisu do databázy
        boolean dbSuccess = writeToDatabase(details);
        if (dbSuccess) {
            String msg = OrgDetailsMessages.SUCCESS.getMessage(this.getLanguage());
            logger.log(LogType.SUCCESS, LogPriority.HIGH, LogSource.BECKEND, msg);
            return gson.toJson(new Response(msg, 200));
        } else {
            String msg = OrgDetailsMessages.DB_ERROR.getMessage(this.getLanguage());
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND, msg);
            return gson.toJson(new Response(msg, 500));
        }
    }

    // Simulovaná metóda na zápis do databázy (tu iba logovanie)
    private boolean writeToDatabase(OrgDetails details) {
        logger.log(LogType.INFO, LogPriority.MIDDLE, LogSource.BECKEND, "Simulating DB write: " + details.toString());
        return true;
    }

}
