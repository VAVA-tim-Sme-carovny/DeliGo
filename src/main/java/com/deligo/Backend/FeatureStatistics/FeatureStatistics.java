package com.deligo.Backend.FeatureStatistics;

import com.deligo.Backend.BaseFeature.BaseFeature;
import com.deligo.ConfigLoader.ConfigLoader;
import com.deligo.DatabaseManager.dao.GenericDAO;
import com.deligo.Logging.Adapter.LoggingAdapter;
import com.deligo.Model.BasicModels.LogPriority;
import com.deligo.Model.BasicModels.LogSource;
import com.deligo.Model.BasicModels.LogType;
import com.deligo.Model.Response;
import com.deligo.Model.Statistic;
import com.deligo.RestApi.RestAPIServer;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Trieda FeatureStatistics slúži na správu štatistík v aplikácii.
 * Poskytuje funkcionalitu pre získavanie denných štatistík a štatistík za určité časové obdobie.
 */
public class FeatureStatistics extends BaseFeature {
    // DAO pre prístup k tabuľke štatistík v databáze
    private final GenericDAO<Statistic> statisticsDAO;
    // Gson pre serializáciu a deserializáciu JSON
    private final Gson gson;

    /**
     * Konštruktor pre inicializáciu správy štatistík.
     * @param globalConfig Globálna konfigurácia aplikácie
     * @param logger Logger pre zaznamenávanie udalostí
     * @param restApiServer Server pre REST API
     */
    public FeatureStatistics(ConfigLoader globalConfig, LoggingAdapter logger, RestAPIServer restApiServer) {
        super(globalConfig, logger, restApiServer);
        this.statisticsDAO = new GenericDAO<>(Statistic.class, "statistics");
        this.gson = new Gson();
        logger.log(LogType.INFO, LogPriority.MIDDLE, LogSource.BECKEND, 
                StatisticsMessages.PROCESS_NAME.getMessage(this.getLanguage()));
    }
    
    /**
     * Získa denné štatistiky pre zadaný dátum.
     * Ak dátum nie je zadaný, použije sa aktuálny dátum.
     * @param json JSON reťazec obsahujúci dátum
     * @return JSON odpoveď s výsledkom operácie a dátami
     */
    public String getDailyStats(String json) {
        try {
            // Parsovanie JSON do mapy
            Type mapType = new TypeToken<Map<String, String>>(){}.getType();
            Map<String, String> requestData = gson.fromJson(json, mapType);
            String dateStr = requestData.get("date");
            
            // Spracovanie dátumu - ak nie je zadaný, použije sa aktuálny
            LocalDate date;
            if (dateStr == null || dateStr.isEmpty()) {
                date = LocalDate.now();
            } else {
                try {
                    date = LocalDate.parse(dateStr, DateTimeFormatter.ISO_DATE);
                } catch (DateTimeParseException e) {
                    logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND, 
                            "Invalid date format: " + dateStr);
                    return gson.toJson(new Response("Invalid date format. Use YYYY-MM-DD", 500));
                }
            }
            
            // Získanie štatistík pre daný dátum
            List<Statistic> stats = statisticsDAO.findByField("date", date);
            
            // Kontrola, či boli nájdené nejaké štatistiky
            if (stats.isEmpty()) {
                logger.log(LogType.WARNING, LogPriority.MIDDLE, LogSource.BECKEND, 
                        StatisticsMessages.NO_STATISTICS_FOUND.getMessage(this.getLanguage()));
                return gson.toJson(new Response("No statistics found for: " + date, 204));
            }
            
            logger.log(LogType.SUCCESS, LogPriority.MIDDLE, LogSource.BECKEND, 
                    StatisticsMessages.STATS_LOADED_SUCCESS.getMessage(this.getLanguage()));
            
            // Vytvorenie odpovede s dátami
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Statistics loaded successfully");
            response.put("status", 200);
            response.put("data", stats);
            
            return gson.toJson(response);
            
        } catch (JsonSyntaxException e) {
            // Chyba pri parsovaní JSON
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND, 
                    "Invalid JSON format: " + e.getMessage());
            return gson.toJson(new Response("Invalid request format", 500));
        } catch (Exception e) {
            // Všeobecná chyba
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND, 
                    StatisticsMessages.STATS_LOADED_ERROR.getMessage(this.getLanguage()) + ": " + e.getMessage());
            return gson.toJson(new Response("Error loading statistics: " + e.getMessage(), 500));
        }
    }
    
    /**
     * Získa štatistiky pre zadané časové obdobie.
     * @param json JSON reťazec obsahujúci počiatočný a koncový dátum
     * @return JSON odpoveď s výsledkom operácie a dátami
     */
    public String getStatsForRange(String json) {
        try {
            // Parsovanie JSON do mapy
            Type mapType = new TypeToken<Map<String, String>>(){}.getType();
            Map<String, String> requestData = gson.fromJson(json, mapType);
            String fromDateStr = requestData.get("fromDate");
            String toDateStr = requestData.get("toDate");
            
            // Kontrola, či sú zadané oba dátumy
            if (fromDateStr == null || toDateStr == null) {
                logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND, 
                        StatisticsMessages.INVALID_DATE_RANGE.getMessage(this.getLanguage()));
                return gson.toJson(new Response("From date and To date are required", 400));
            }
            
            // Parsovanie dátumov
            LocalDate fromDate, toDate;
            try {
                fromDate = LocalDate.parse(fromDateStr, DateTimeFormatter.ISO_DATE);
                toDate = LocalDate.parse(toDateStr, DateTimeFormatter.ISO_DATE);
            } catch (DateTimeParseException e) {
                logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND, 
                        "Invalid date format. Use YYYY-MM-DD");
                return gson.toJson(new Response("Invalid date format. Use YYYY-MM-DD", 400));
            }
            
            // Kontrola, či je počiatočný dátum pred koncovým
            if (fromDate.isAfter(toDate)) {
                logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND, 
                        StatisticsMessages.INVALID_DATE_RANGE.getMessage(this.getLanguage()));
                return gson.toJson(new Response("From date must be before To date", 400));
            }
            
            // Získanie všetkých štatistík a filtrovanie podľa časového rozsahu
            List<Statistic> allStats = statisticsDAO.getAll();
            List<Statistic> statsInRange = allStats.stream()
                    .filter(stat -> !stat.getDate().isBefore(fromDate) && !stat.getDate().isAfter(toDate))
                    .collect(Collectors.toList());
            
            // Kontrola, či boli nájdené nejaké štatistiky
            if (statsInRange.isEmpty()) {
                logger.log(LogType.WARNING, LogPriority.MIDDLE, LogSource.BECKEND, 
                        StatisticsMessages.NO_STATISTICS_FOUND.getMessage(this.getLanguage()));
                return gson.toJson(new Response("No statistics found for the given period", 204));
            }
            
            logger.log(LogType.SUCCESS, LogPriority.MIDDLE, LogSource.BECKEND, 
                    StatisticsMessages.STATS_LOADED_SUCCESS.getMessage(this.getLanguage()));
            
            // Vytvorenie odpovede s dátami
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Statistics loaded successfully");
            response.put("status", 200);
            response.put("data", statsInRange);
            
            return gson.toJson(response);
            
        } catch (JsonSyntaxException e) {
            // Chyba pri parsovaní JSON
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND, 
                    "Invalid JSON format: " + e.getMessage());
            return gson.toJson(new Response("Invalid request format", 500));
        } catch (Exception e) {
            // Všeobecná chyba
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND, 
                    StatisticsMessages.STATS_LOADED_ERROR.getMessage(this.getLanguage()) + ": " + e.getMessage());
            return gson.toJson(new Response("Error loading statistics: " + e.getMessage(), 500));
        }
    }
} 