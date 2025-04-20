package com.deligo.Backend.FeatureUserManagement;

import com.deligo.Backend.BaseFeature.BaseFeature;
import com.deligo.ConfigLoader.ConfigLoader;
import com.deligo.DatabaseManager.dao.GenericDAO;
import com.deligo.Logging.Adapter.LoggingAdapter;
import com.deligo.Model.BasicModels.LogPriority;
import com.deligo.Model.BasicModels.LogSource;
import com.deligo.Model.BasicModels.LogType;
import com.deligo.Model.OrgDetails;
import com.deligo.Model.Response;
import com.deligo.Model.User;
import com.deligo.RestApi.RestAPIServer;
import com.google.gson.Gson;
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
     * @param globalConfig globálna konfigurácia aplikácie
     * @param logger logovací adaptér
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
     * @param json JSON obsahujúci údaje o používateľovi na úpravu
     * @return JSON odpoveď o výsledku operácie
     */
    public String editUser(String json) {
        try {
            // Deserializácia JSON na mapu
            Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
            Map<String, Object> requestData = gson.fromJson(json, mapType);
            String userId = (String) requestData.get("userId");
            String username = (String) requestData.get("username");
            
            // Získanie rolí a značiek z požiadavky
            @SuppressWarnings("unchecked")
            List<String> roles = (requestData.get("roles") != null) 
                ? (List<String>) requestData.get("roles") 
                : new ArrayList<>();

            @SuppressWarnings("unchecked")
            List<String> tags = (requestData.get("tags") != null) 
                ? (List<String>) requestData.get("tags") 
                : new ArrayList<>();
            
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
            
            // Aktualizácia rolí a značiek
            user.setRoles(roles);
            user.setTags(tags);
            
            // Aktualizácia používateľa v databáze
            userDAO.update(user.getId(), user);
            
            logger.log(LogType.SUCCESS, LogPriority.MIDDLE, LogSource.BECKEND, 
                    UserManagementMessages.USER_ROLES_UPDATED.getMessage(this.getLanguage()));
            return gson.toJson(new Response("User roles and tags were successfully updated", 200));
            
        } catch (JsonSyntaxException e) {
            // Chyba pri parsovaní JSON
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND, 
                    UserManagementMessages.INVALID_REQUEST_FORMAT.getMessage(this.getLanguage()));
            return gson.toJson(new Response("Invalid request format", 400));
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
            Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
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
    
    /**
     * Získa zoznam všetkých používateľov v systéme.
     * Z bezpečnostných dôvodov sa z odpovede odstránia citlivé údaje ako heslá.
     * 
     * @param json JSON požiadavka (v tomto prípade neobsahuje žiadne parametre)
     * @return JSON odpoveď so zoznamom všetkých používateľov a ich údajmi
     */
    public String getAllUsers(String json) {
        try {
            // Získanie všetkých používateľov z databázy
            List<User> users = userDAO.getAll();
            
            // Odstránenie citlivých údajov (heslo)
            List<Map<String, Object>> sanitizedUsers = users.stream().map(user -> {
                Map<String, Object> sanitizedUser = new HashMap<>();
                sanitizedUser.put("id", user.getId());
                sanitizedUser.put("username", user.getUsername());
                sanitizedUser.put("email", user.getEmail());
                sanitizedUser.put("roles", user.getRoles());
                sanitizedUser.put("tags", user.getTags());
                return sanitizedUser;
            }).collect(Collectors.toList());
            
            // Vytvorenie odpovede
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Users retrieved successfully");
            response.put("status", 200);
            response.put("data", sanitizedUsers);
            
            return gson.toJson(response);
            
        } catch (Exception e) {
            // Logovanie a vrátenie chyby
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND, 
                    "Error retrieving users: " + e.getMessage());
            return gson.toJson(new Response("Error retrieving users: " + e.getMessage(), 500));
        }
    }
    
    /**
     * Aktualizuje detaily organizácie vrátane otváracích hodín, telefónneho čísla a emailu.
     * 
     * @param json JSON obsahujúci detaily organizácie na aktualizáciu
     * @return JSON odpoveď o výsledku operácie
     */
    public String updateOrgDetails(String json) {
        try {
            // Deserializácia JSON na mapu
            Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
            Map<String, Object> requestData = gson.fromJson(json, mapType);
                
            // Extrakcia a validácia časových údajov
            @SuppressWarnings("unchecked")
            List<List<String>> openingTimes = (List<List<String>>) requestData.get("openingTimes");
            String phoneNumber = (String) requestData.get("phoneNumber");
            String email = (String) requestData.get("email");
            
            // Validácia telefónneho čísla
            if (phoneNumber != null && !phoneNumber.isEmpty() && !PHONE_PATTERN.matcher(phoneNumber).matches()) {
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
            
            // Spracovanie otváracích hodín
            Map<String, String> processedTimes = new HashMap<>();
            String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
            
            for (int i = 0; i < Math.min(openingTimes.size(), 7); i++) {
                List<String> times = openingTimes.get(i);
                
                if (times.size() >= 2) {
                    String openTime = times.get(0);
                    String closeTime = times.get(1);
                    
                    if (openTime == null || closeTime == null) {
                        processedTimes.put(days[i], "Closed");
                    } else {
                        processedTimes.put(days[i], openTime + " - " + closeTime);
                    }
                } else {
                    processedTimes.put(days[i], "Closed");
                }
            }
            
            // Konverzia mapy na formát List<List<String>>
            List<List<String>> timesList = new ArrayList<>();
            for (String day : new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"}) {
                String timeRange = processedTimes.get(day);
                List<String> dayData = new ArrayList<>();
                dayData.add(day);
                dayData.add(timeRange);
                timesList.add(dayData);
            }
            
            // Vytvorenie alebo aktualizácia detailov organizácie
            List<OrgDetails> existingDetails = orgDetailsDAO.getAll();
            OrgDetails orgDetails;
            
            if (existingDetails.isEmpty()) {
                orgDetails = new OrgDetails();
            } else {
                orgDetails = existingDetails.get(0);
            }
            
            // Aktualizácia detailov
            orgDetails.setOpeningTimes(timesList);
            orgDetails.setPhoneNumber(phoneNumber);
            orgDetails.setEmail(email);
            
            // Uloženie detailov
            if (orgDetails.getId() > 0) {
                orgDetailsDAO.update(orgDetails.getId(), orgDetails);
            } else {
                orgDetailsDAO.insert(orgDetails);
            }
            
            logger.log(LogType.SUCCESS, LogPriority.MIDDLE, LogSource.BECKEND, 
                    UserManagementMessages.ORG_DETAILS_UPDATED.getMessage(this.getLanguage()));
            return gson.toJson(new Response("Organization details were updated successfully", 200));
            
        } catch (JsonSyntaxException e) {
            // Chyba pri parsovaní JSON
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND, 
                    UserManagementMessages.INVALID_REQUEST_FORMAT.getMessage(this.getLanguage()));
            return gson.toJson(new Response("Invalid request format", 400));
        } catch (Exception e) {
            // Iné chyby
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND, 
                    UserManagementMessages.ORG_DETAILS_ERROR.getMessage(this.getLanguage()) + ": " + e.getMessage());
            return gson.toJson(new Response("Error updating organization details: " + e.getMessage(), 500));
        }
    }
    
    /**
     * Získa detaily organizácie.
     * Ak detaily nie sú nastavené, vráti predvolené prázdne hodnoty.
     * 
     * @param json JSON požiadavka (v tomto prípade neobsahuje žiadne parametre)
     * @return JSON odpoveď s detailmi organizácie
     */
    public String getOrgDetails(String json) {
        try {
            // Získanie detailov organizácie z databázy
            List<OrgDetails> details = orgDetailsDAO.getAll();
            
            if (details.isEmpty()) {
                // Vrátenie predvolených hodnôt, ak neexistujú žiadne detaily
                Map<String, Object> defaultDetails = new HashMap<>();
                defaultDetails.put("openingTimes", new HashMap<>());
                defaultDetails.put("phoneNumber", "");
                defaultDetails.put("email", "");
                
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Default organization details retrieved");
                response.put("status", 200);
                response.put("data", defaultDetails);
                
                return gson.toJson(response);
            }
            
            // Vytvorenie odpovede s existujúcimi detailmi
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Organization details retrieved successfully");
            response.put("status", 200);
            response.put("data", details.get(0));
            
            return gson.toJson(response);
            
        } catch (Exception e) {
            // Logovanie a vrátenie chyby
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND, 
                    "Error retrieving organization details: " + e.getMessage());
            return gson.toJson(new Response("Error retrieving organization details: " + e.getMessage(), 500));
        }
    }
} 