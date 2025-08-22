package com.deligo.Backend.FeatureTableStructure;

import com.deligo.Backend.BaseFeature.BaseFeature;
import com.deligo.ConfigLoader.ConfigLoader;
import com.deligo.DatabaseManager.dao.GenericDAO;
import com.deligo.Logging.Adapter.LoggingAdapter;
import com.deligo.Model.BasicModels.LogPriority;
import com.deligo.Model.BasicModels.LogSource;
import com.deligo.Model.BasicModels.LogType;
import com.deligo.Model.Response;
import com.deligo.Model.TableStructure;
import com.deligo.RestApi.RestAPIServer;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Trieda FeatureTableStructure slúži na správu stolov v reštaurácii.
 * Poskytuje funkcionalitu pre pridávanie, aktualizáciu, mazanie a získavanie stolov a kategórií.
 */
public class FeatureTableStructure extends BaseFeature {
    // DAO pre prístup k tabuľke stolov v databáze
    private final GenericDAO<TableStructure> tableDAO;
    // Gson pre serializáciu a deserializáciu JSON
    private final Gson gson;

    /**
     * Konštruktor pre inicializáciu správy stolov.
     * @param globalConfig Globálna konfigurácia aplikácie
     * @param logger Logger pre zaznamenávanie udalostí
     * @param restApiServer Server pre REST API
     */
    public FeatureTableStructure(ConfigLoader globalConfig, LoggingAdapter logger, RestAPIServer restApiServer) {
        super(globalConfig, logger, restApiServer);
        this.tableDAO = new GenericDAO<>(TableStructure.class, "tables");
        this.gson = new Gson();
        logger.log(LogType.INFO, LogPriority.MIDDLE, LogSource.BECKEND, 
                TableStructureMessages.PROCESS_NAME.getMessage(this.getLanguage()));
    }
    
    /**
     * Pridá nový stôl do databázy.
     * @param json JSON reťazec obsahujúci údaje o stole
     * @return JSON odpoveď s výsledkom operácie
     */
    public String addTable(String json) {
        try {
            TableStructure table = gson.fromJson(json, TableStructure.class);
            
            // Kontrola, či sú zadané povinné polia
            if (table.getName() == null || table.getName().isEmpty() || 
                table.getCategory() == null || table.getCategory().isEmpty()) {
                logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND, 
                        TableStructureMessages.INVALID_REQUEST_FORMAT.getMessage(this.getLanguage()));
                return gson.toJson(new Response("Table name and category are required", 400));
            }
            
            // Kontrola, či stôl s rovnakým názvom už existuje
            List<TableStructure> existingTables = tableDAO.findByField("name", table.getName());
            if (!existingTables.isEmpty()) {
                logger.log(LogType.ERROR, LogPriority.MIDDLE, LogSource.BECKEND, 
                        "Table with name " + table.getName() + " already exists");
                return gson.toJson(new Response("Table with this name already exists", 400));
            }
            
            // Vloženie stola do databázy
            int id = tableDAO.insert(table);
            
            if (id > 0) {
                logger.log(LogType.SUCCESS, LogPriority.MIDDLE, LogSource.BECKEND, 
                        TableStructureMessages.TABLE_ADDED_SUCCESS.getMessage(this.getLanguage()));
                return gson.toJson(new Response("Table added successfully", 200));
            } else {
                logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND, 
                        TableStructureMessages.TABLE_ADDED_ERROR.getMessage(this.getLanguage()));
                return gson.toJson(new Response("Failed to add table", 500));
            }
            
        } catch (JsonSyntaxException e) {
            // Chyba pri parsovaní JSON
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND, 
                    TableStructureMessages.INVALID_REQUEST_FORMAT.getMessage(this.getLanguage()));
            return gson.toJson(new Response("Invalid request format", 400));
        } catch (Exception e) {
            // Všeobecná chyba
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND, 
                    TableStructureMessages.TABLE_ADDED_ERROR.getMessage(this.getLanguage()) + ": " + e.getMessage());
            return gson.toJson(new Response("Error adding table: " + e.getMessage(), 500));
        }
    }
    
    /**
     * Aktualizuje existujúci stôl v databáze.
     * @param json JSON reťazec obsahujúci údaje o stole na aktualizáciu
     * @return JSON odpoveď s výsledkom operácie
     */
    public String updateTable(String json) {
        try {
            // Parsovanie JSON do mapy
            Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
            Map<String, Object> requestData = gson.fromJson(json, mapType);
            int tableId = ((Double) requestData.get("id")).intValue();
            String name = (String) requestData.get("name");
            String category = (String) requestData.get("category");
            int seats = ((Double) requestData.get("seats")).intValue();
            boolean isActive = (Boolean) requestData.get("isActive");
            
            // Kontrola, či stôl existuje
            Optional<TableStructure> tableOpt = tableDAO.getById(tableId);
            if (!tableOpt.isPresent()) {
                logger.log(LogType.ERROR, LogPriority.MIDDLE, LogSource.BECKEND, 
                        TableStructureMessages.TABLE_NOT_FOUND.getMessage(this.getLanguage()));
                return gson.toJson(new Response("Table not found", 404));
            }
            
            // Aktualizácia údajov stola
            TableStructure table = tableOpt.get();
            table.setName(name);
            table.setCategory(category);
            table.setSeats(seats);
            table.setActive(isActive);
            
            // Uloženie aktualizovaného stola
            tableDAO.update(tableId, table);
            
            logger.log(LogType.SUCCESS, LogPriority.MIDDLE, LogSource.BECKEND, 
                    TableStructureMessages.TABLE_UPDATED_SUCCESS.getMessage(this.getLanguage()));
            return gson.toJson(new Response("Table updated successfully", 200));
            
        } catch (JsonSyntaxException e) {
            // Chyba pri parsovaní JSON
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND, 
                    TableStructureMessages.INVALID_REQUEST_FORMAT.getMessage(this.getLanguage()));
            return gson.toJson(new Response("Invalid request format", 400));
        } catch (Exception e) {
            // Všeobecná chyba
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND, 
                    TableStructureMessages.TABLE_UPDATED_ERROR.getMessage(this.getLanguage()) + ": " + e.getMessage());
            return gson.toJson(new Response("Error updating table: " + e.getMessage(), 500));
        }
    }
    
    /**
     * Vymaže stôl z databázy.
     * @param json JSON reťazec obsahujúci ID stola na vymazanie
     * @return JSON odpoveď s výsledkom operácie
     */
    public String deleteTable(String json) {
        try {
            // Parsovanie JSON do mapy
            Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
            Map<String, Object> requestData = gson.fromJson(json, mapType);
            int tableId = ((Double) requestData.get("id")).intValue();
            
            // Kontrola, či stôl existuje
            Optional<TableStructure> tableOpt = tableDAO.getById(tableId);
            if (!tableOpt.isPresent()) {
                logger.log(LogType.ERROR, LogPriority.MIDDLE, LogSource.BECKEND, 
                        TableStructureMessages.TABLE_NOT_FOUND.getMessage(this.getLanguage()));
                return gson.toJson(new Response("Table not found", 404));
            }
            
            // Vymazanie stola
            tableDAO.delete(tableId);
            
            logger.log(LogType.SUCCESS, LogPriority.MIDDLE, LogSource.BECKEND, 
                    TableStructureMessages.TABLE_DELETED_SUCCESS.getMessage(this.getLanguage()));
            return gson.toJson(new Response("Table deleted successfully", 200));
            
        } catch (JsonSyntaxException e) {
            // Chyba pri parsovaní JSON
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND, 
                    TableStructureMessages.INVALID_REQUEST_FORMAT.getMessage(this.getLanguage()));
            return gson.toJson(new Response("Invalid request format", 400));
        } catch (Exception e) {
            // Všeobecná chyba
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND, 
                    TableStructureMessages.TABLE_DELETED_ERROR.getMessage(this.getLanguage()) + ": " + e.getMessage());
            return gson.toJson(new Response("Error deleting table: " + e.getMessage(), 500));
        }
    }
    
    /**
     * Získa všetky stoly z databázy.
     * @param json JSON reťazec (v tomto prípade nepoužitý)
     * @return JSON odpoveď so zoznamom všetkých stolov
     */
    public String getAllTables(String json) {
        try {
            // Získanie všetkých stolov
            List<TableStructure> tables = tableDAO.getAll();
            
            // Vytvorenie odpovede
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Tables retrieved successfully");
            response.put("status", 200);
            response.put("data", tables);
            
            return gson.toJson(response);
            
        } catch (Exception e) {
            // Všeobecná chyba
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND, 
                    "Error retrieving tables: " + e.getMessage());
            return gson.toJson(new Response("Error retrieving tables: " + e.getMessage(), 500));
        }
    }
    
    /**
     * Získa stoly podľa kategórie.
     * @param json JSON reťazec obsahujúci kategóriu
     * @return JSON odpoveď so zoznamom stolov v danej kategórii
     */
    public String getTablesByCategory(String json) {
        try {
            // Parsovanie JSON do mapy
            Type mapType = new TypeToken<Map<String, String>>(){}.getType();
            Map<String, String> requestData = gson.fromJson(json, mapType);
            String category = requestData.get("category");
            
            // Kontrola, či je kategória zadaná
            if (category == null || category.isEmpty()) {
                logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND, 
                        TableStructureMessages.INVALID_REQUEST_FORMAT.getMessage(this.getLanguage()));
                return gson.toJson(new Response("Category is required", 400));
            }
            
            // Získanie stolov podľa kategórie
            List<TableStructure> tables = tableDAO.findByField("category", category);
            
            // Vytvorenie odpovede
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Tables retrieved successfully");
            response.put("status", 200);
            response.put("data", tables);
            
            return gson.toJson(response);
            
        } catch (JsonSyntaxException e) {
            // Chyba pri parsovaní JSON
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND, 
                    TableStructureMessages.INVALID_REQUEST_FORMAT.getMessage(this.getLanguage()));
            return gson.toJson(new Response("Invalid request format", 400));
        } catch (Exception e) {
            // Všeobecná chyba
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND, 
                    "Error retrieving tables by category: " + e.getMessage());
            return gson.toJson(new Response("Error retrieving tables: " + e.getMessage(), 500));
        }
    }
    
    /**
     * Získa všetky kategórie stolov.
     * @param json JSON reťazec (v tomto prípade nepoužitý)
     * @return JSON odpoveď so zoznamom všetkých kategórií
     */
    public String getAllCategories(String json) {
        try {
            // Získanie všetkých stolov a extrakcia unikátnych kategórií
            List<TableStructure> tables = tableDAO.getAll();
            Set<String> categories = tables.stream()
                    .map(TableStructure::getCategory)
                    .filter(c -> c != null && !c.isEmpty())
                    .collect(Collectors.toSet());
            
            // Vytvorenie odpovede
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Categories retrieved successfully");
            response.put("status", 200);
            response.put("data", categories);
            
            return gson.toJson(response);
            
        } catch (Exception e) {
            // Všeobecná chyba
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND, 
                    "Error retrieving categories: " + e.getMessage());
            return gson.toJson(new Response("Error retrieving categories: " + e.getMessage(), 500));
        }
    }
    
    /**
     * Pridá novú kategóriu stolov.
     * @param json JSON reťazec obsahujúci názov kategórie
     * @return JSON odpoveď s výsledkom operácie
     */
    public String addCategory(String json) {
        try {
            // Parsovanie JSON do mapy
            Type mapType = new TypeToken<Map<String, String>>(){}.getType();
            Map<String, String> requestData = gson.fromJson(json, mapType);
            String category = requestData.get("name");
            
            // Kontrola, či je názov kategórie zadaný
            if (category == null || category.isEmpty()) {
                logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND, 
                        TableStructureMessages.INVALID_REQUEST_FORMAT.getMessage(this.getLanguage()));
                return gson.toJson(new Response("Category name is required", 400));
            }
            
            // Kontrola, či kategória už existuje
            List<TableStructure> tables = tableDAO.getAll();
            Set<String> categories = tables.stream()
                    .map(TableStructure::getCategory)
                    .filter(c -> c != null && !c.isEmpty())
                    .collect(Collectors.toSet());
            
            if (categories.contains(category)) {
                logger.log(LogType.ERROR, LogPriority.MIDDLE, LogSource.BECKEND, 
                        TableStructureMessages.CATEGORY_ALREADY_EXISTS.getMessage(this.getLanguage()));
                return gson.toJson(new Response("Category already exists", 400));
            }
            

            TableStructure dummyTable = new TableStructure(category, "CATEGORY_" + category, 0, false);
            int id = tableDAO.insert(dummyTable);
            
            if (id > 0) {
                logger.log(LogType.SUCCESS, LogPriority.MIDDLE, LogSource.BECKEND, 
                        TableStructureMessages.CATEGORY_ADDED_SUCCESS.getMessage(this.getLanguage()));
                return gson.toJson(new Response("Category added successfully", 200));
            } else {
                logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND, 
                        TableStructureMessages.CATEGORY_ERROR.getMessage(this.getLanguage()));
                return gson.toJson(new Response("Failed to add category", 500));
            }
            
        } catch (JsonSyntaxException e) {
            // Chyba pri parsovaní JSON
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND, 
                    TableStructureMessages.INVALID_REQUEST_FORMAT.getMessage(this.getLanguage()));
            return gson.toJson(new Response("Invalid request format", 400));
        } catch (Exception e) {
            // Všeobecná chyba
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND, 
                    TableStructureMessages.CATEGORY_ERROR.getMessage(this.getLanguage()) + ": " + e.getMessage());
            return gson.toJson(new Response("Error adding category: " + e.getMessage(), 500));
        }
    }
    
    /**
     * Aktualizuje názov kategórie stolov.
     * @param json JSON reťazec obsahujúci starý a nový názov kategórie
     * @return JSON odpoveď s výsledkom operácie
     */
    public String updateCategory(String json) {
        try {
            // Parsovanie JSON do mapy
            Type mapType = new TypeToken<Map<String, String>>(){}.getType();
            Map<String, String> requestData = gson.fromJson(json, mapType);
            String oldCategory = requestData.get("prevName");
            String newCategory = requestData.get("newName");
            
            // Kontrola, či sú zadané oba názvy kategórií
            if (oldCategory == null || oldCategory.isEmpty() || newCategory == null || newCategory.isEmpty()) {
                logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND, 
                        TableStructureMessages.INVALID_REQUEST_FORMAT.getMessage(this.getLanguage()));
                return gson.toJson(new Response("Both old and new category names are required", 400));
            }
            
            // Aktualizácia všetkých stolov s touto kategóriou
            List<TableStructure> tables = tableDAO.findByField("category", oldCategory);
            
            if (tables.isEmpty()) {
                logger.log(LogType.ERROR, LogPriority.MIDDLE, LogSource.BECKEND, 
                        TableStructureMessages.CATEGORY_NOT_FOUND.getMessage(this.getLanguage()));
                return gson.toJson(new Response("Category not found", 404));
            }
            
            for (TableStructure table : tables) {
                table.setCategory(newCategory);
                tableDAO.update(table.getId(), table);
            }
            
            logger.log(LogType.SUCCESS, LogPriority.MIDDLE, LogSource.BECKEND, 
                    TableStructureMessages.CATEGORY_UPDATED_SUCCESS.getMessage(this.getLanguage()));
            return gson.toJson(new Response("Category updated successfully", 200));
            
        } catch (JsonSyntaxException e) {
            // Chyba pri parsovaní JSON
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND, 
                    TableStructureMessages.INVALID_REQUEST_FORMAT.getMessage(this.getLanguage()));
            return gson.toJson(new Response("Invalid request format", 400));
        } catch (Exception e) {
            // Všeobecná chyba
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND, 
                    TableStructureMessages.CATEGORY_ERROR.getMessage(this.getLanguage()) + ": " + e.getMessage());
            return gson.toJson(new Response("Error updating category: " + e.getMessage(), 500));
        }
    }
    
    /**
     * Vymaže kategóriu a všetky stoly v tejto kategórii.
     * @param json JSON reťazec obsahujúci názov kategórie na vymazanie
     * @return JSON odpoveď s výsledkom operácie
     */
    public String deleteCategory(String json) {
        try {
            // Parsovanie JSON do mapy
            Type mapType = new TypeToken<Map<String, String>>(){}.getType();
            Map<String, String> requestData = gson.fromJson(json, mapType);
            String category = requestData.get("name");
            
            // Kontrola, či je názov kategórie zadaný
            if (category == null || category.isEmpty()) {
                logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND, 
                        TableStructureMessages.INVALID_REQUEST_FORMAT.getMessage(this.getLanguage()));
                return gson.toJson(new Response("Category name is required", 400));
            }
            
            // Získanie všetkých stolov s touto kategóriou
            List<TableStructure> tables = tableDAO.findByField("category", category);
            
            if (tables.isEmpty()) {
                logger.log(LogType.ERROR, LogPriority.MIDDLE, LogSource.BECKEND, 
                        TableStructureMessages.CATEGORY_NOT_FOUND.getMessage(this.getLanguage()));
                return gson.toJson(new Response("Category not found", 404));
            }
            
            // Vymazanie všetkých stolov s touto kategóriou
            for (TableStructure table : tables) {
                tableDAO.delete(table.getId());
            }
            
            logger.log(LogType.SUCCESS, LogPriority.MIDDLE, LogSource.BECKEND, 
                    TableStructureMessages.CATEGORY_DELETED_SUCCESS.getMessage(this.getLanguage()));
            return gson.toJson(new Response("Category and all associated tables deleted successfully", 200));
            
        } catch (JsonSyntaxException e) {
            // Chyba pri parsovaní JSON
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND, 
                    TableStructureMessages.INVALID_REQUEST_FORMAT.getMessage(this.getLanguage()));
            return gson.toJson(new Response("Invalid request format", 400));
        } catch (Exception e) {
            // Všeobecná chyba
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND, 
                    TableStructureMessages.CATEGORY_ERROR.getMessage(this.getLanguage()) + ": " + e.getMessage());
            return gson.toJson(new Response("Error deleting category: " + e.getMessage(), 500));
        }
    }
} 