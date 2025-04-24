package com.deligo.Backend.FeatureMenuManagement;

import com.deligo.Backend.BaseFeature.BaseFeature;
import com.deligo.ConfigLoader.ConfigLoader;
import com.deligo.DatabaseManager.dao.GenericDAO;
import com.deligo.Logging.Adapter.LoggingAdapter;
import com.deligo.Model.BasicModels.LogPriority;
import com.deligo.Model.BasicModels.LogSource;
import com.deligo.Model.BasicModels.LogType;
import com.deligo.Model.MenuItem;
import com.deligo.Model.Response;
import com.deligo.RestApi.RestAPIServer;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Trieda FeatureMenuManagement implementuje funkcionalitu pre správu položiek menu 
 * a ich kategórií v aplikácii DeliGo.
 * Poskytuje metódy na pridávanie, aktualizáciu, mazanie a získavanie položiek menu a kategórií.
 */
public class FeatureMenuManagement extends BaseFeature {
    // DAO pre prístup k databáze položiek menu
    private final GenericDAO<MenuItem> menuItemDAO;
    // Gson pre serializáciu/deserializáciu JSON
    private final Gson gson;

    /**
     * Konštruktor inicializuje DAO pre položky menu a logovací systém.
     * 
     * @param globalConfig globálna konfigurácia aplikácie
     * @param logger logovací adaptér
     * @param restApiServer REST API server
     */
    public FeatureMenuManagement(ConfigLoader globalConfig, LoggingAdapter logger, RestAPIServer restApiServer) {
        super(globalConfig, logger, restApiServer);
        this.menuItemDAO = new GenericDAO<>(MenuItem.class, "menu_items");
        this.gson = new Gson();
        logger.log(LogType.INFO, LogPriority.MIDDLE, LogSource.BECKEND, 
                MenuManagementMessages.PROCESS_NAME.getMessage(this.getLanguage()));
    }
    
    /**
     * Pridá novú položku do menu.
     * 
     * @param json JSON reprezentácia položky menu
     * @return JSON odpoveď o výsledku operácie
     */
    public String addItem(String json) {
        try {
            // Deserializácia JSON na objekt MenuItem
            MenuItem item = gson.fromJson(json, MenuItem.class);
            
            // Validácia povinných polí
            if (item.getName() == null || item.getName().isEmpty()) {
                logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND, 
                        MenuManagementMessages.INVALID_REQUEST_FORMAT.getMessage(this.getLanguage()));
                return gson.toJson(new Response("Item name is required", 400));
            }
            
            if (item.getDetails() == null || item.getDetails().isEmpty()) {
                logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND, 
                        MenuManagementMessages.DETAILS_REQUIRED.getMessage(this.getLanguage()));
                return gson.toJson(new Response("Item details are required", 400));
            }
            
            // Kontrola, či položka s rovnakým názvom už existuje
            List<MenuItem> existingItems = menuItemDAO.findByField("name", item.getName());
            if (!existingItems.isEmpty()) {
                logger.log(LogType.ERROR, LogPriority.MIDDLE, LogSource.BECKEND, 
                        MenuManagementMessages.ITEM_ALREADY_EXISTS.getMessage(this.getLanguage()));
                return gson.toJson(new Response("Item with this name already exists", 400));
            }
            
            // Vloženie položky do databázy
            int id = menuItemDAO.insert(item);
            
            // Kontrola úspešnosti operácie
            if (id > 0) {
                logger.log(LogType.SUCCESS, LogPriority.MIDDLE, LogSource.BECKEND, 
                        MenuManagementMessages.ITEM_ADDED_SUCCESS.getMessage(this.getLanguage()));
                return gson.toJson(new Response("Item was successfully added", 200));
            } else {
                logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND, 
                        MenuManagementMessages.ITEM_ADDED_ERROR.getMessage(this.getLanguage()));
                return gson.toJson(new Response("Failed to add item", 500));
            }
            
        } catch (JsonSyntaxException e) {
            // Chyba pri parsovaní JSON
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND, 
                    MenuManagementMessages.INVALID_REQUEST_FORMAT.getMessage(this.getLanguage()));
            return gson.toJson(new Response("Invalid request format", 400));
        } catch (Exception e) {
            // Iné chyby
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND, 
                    MenuManagementMessages.ITEM_ADDED_ERROR.getMessage(this.getLanguage()) + ": " + e.getMessage());
            return gson.toJson(new Response("Error adding item: " + e.getMessage(), 500));
        }
    }
    
    /**
     * Aktualizuje existujúcu položku menu.
     * 
     * @param json JSON reprezentácia aktualizovanej položky
     * @return JSON odpoveď o výsledku operácie
     */
    public String updateItem(String json) {
        try {
            // Deserializácia JSON na mapu
            Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
            Map<String, Object> requestData = gson.fromJson(json, mapType);
            int itemId = ((Double) requestData.get("itemId")).intValue();
            
            // Overenie existencie položky
            Optional<MenuItem> itemOpt = menuItemDAO.getById(itemId);
            if (!itemOpt.isPresent()) {
                logger.log(LogType.ERROR, LogPriority.MIDDLE, LogSource.BECKEND, 
                        MenuManagementMessages.ITEM_NOT_FOUND.getMessage(this.getLanguage()));
                return gson.toJson(new Response("Item not found", 404));
            }
            
            // Získanie dát z požiadavky
            MenuItem item = itemOpt.get();
            String name = (String) requestData.get("name");
            String description = (String) requestData.get("description");
            String details = (String) requestData.get("details");
            double price = ((Double) requestData.get("price")).doubleValue();
            
            // Validácia povinných polí
            if (details == null || details.isEmpty()) {
                logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND, 
                        MenuManagementMessages.DETAILS_REQUIRED.getMessage(this.getLanguage()));
                return gson.toJson(new Response("Item details are required", 400));
            }
            
            // Aktualizácia polí
            item.setName(name);
            item.setDescription(description);
            item.setDetails(details);
            item.setPrice(price);
            
            // Spracovanie kategórií (konverzia z List<LinkedTreeMap> na List<String>)
            @SuppressWarnings("unchecked")
            List<Object> categoriesObj = (List<Object>) requestData.get("categories");
            if (categoriesObj != null) {
                List<String> categories = categoriesObj.stream()
                        .map(Object::toString)
                        .collect(Collectors.toList());
                item.setCategories(categories);
            }
            
            // Spracovanie dostupného množstva
            Double availableCountObj = (Double) requestData.get("availableCount");
            if (availableCountObj != null) {
                item.setAvailableCount(availableCountObj.intValue());
            }
            
            // Aktualizácia položky v databáze
            menuItemDAO.update(itemId, item);
            
            logger.log(LogType.SUCCESS, LogPriority.MIDDLE, LogSource.BECKEND, 
                    MenuManagementMessages.ITEM_UPDATED_SUCCESS.getMessage(this.getLanguage()));
            return gson.toJson(new Response("Item was successfully updated", 200));
            
        } catch (JsonSyntaxException e) {
            // Chyba pri parsovaní JSON
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND, 
                    MenuManagementMessages.INVALID_REQUEST_FORMAT.getMessage(this.getLanguage()));
            return gson.toJson(new Response("Invalid request format", 400));
        } catch (Exception e) {
            // Iné chyby
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND, 
                    MenuManagementMessages.ITEM_UPDATED_ERROR.getMessage(this.getLanguage()) + ": " + e.getMessage());
            return gson.toJson(new Response("Error updating item: " + e.getMessage(), 500));
        }
    }
    
    /**
     * Odstráni položku z menu.
     * 
     * @param json JSON obsahujúci ID a názov položky na odstránenie
     * @return JSON odpoveď o výsledku operácie
     */
    public String deleteItem(String json) {
        try {
            // Deserializácia JSON na mapu
            Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
            Map<String, Object> requestData = gson.fromJson(json, mapType);
            int itemId = ((Double) requestData.get("itemId")).intValue();
            String name = (String) requestData.get("name");
            
            // Overenie existencie položky
            Optional<MenuItem> itemOpt = menuItemDAO.getById(itemId);
            if (!itemOpt.isPresent()) {
                logger.log(LogType.ERROR, LogPriority.MIDDLE, LogSource.BECKEND, 
                        MenuManagementMessages.ITEM_NOT_FOUND.getMessage(this.getLanguage()));
                return gson.toJson(new Response("Item not found", 404));
            }
            
            MenuItem item = itemOpt.get();
            
            // Overenie zhody názvu s ID
            if (!item.getName().equals(name)) {
                logger.log(LogType.ERROR, LogPriority.MIDDLE, LogSource.BECKEND, 
                        "Item name does not match");
                return gson.toJson(new Response("Item name does not match with the ID", 400));
            }
            
            // Odstránenie položky z databázy
            menuItemDAO.delete(itemId);
            
            logger.log(LogType.SUCCESS, LogPriority.MIDDLE, LogSource.BECKEND, 
                    MenuManagementMessages.ITEM_DELETED_SUCCESS.getMessage(this.getLanguage()));
            return gson.toJson(new Response("Item was successfully deleted", 200));
            
        } catch (JsonSyntaxException e) {
            // Chyba pri parsovaní JSON
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND, 
                    MenuManagementMessages.INVALID_REQUEST_FORMAT.getMessage(this.getLanguage()));
            return gson.toJson(new Response("Invalid request format", 400));
        } catch (Exception e) {
            // Iné chyby
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND, 
                    MenuManagementMessages.ITEM_DELETED_ERROR.getMessage(this.getLanguage()) + ": " + e.getMessage());
            return gson.toJson(new Response("Error deleting item: " + e.getMessage(), 500));
        }
    }
    
    /**
     * Získa všetky položky menu.
     * 
     * @param json JSON požiadavka (v tomto prípade neobsahuje žiadne parametre)
     * @return JSON odpoveď so zoznamom všetkých položiek
     */
    public String getAllItems(String json) {
        try {
            // Získanie všetkých položiek z databázy
            List<MenuItem> items = menuItemDAO.getAll();
            
            // Vytvorenie odpovede
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Items retrieved successfully");
            response.put("status", 200);
            response.put("data", items);
            
            return gson.toJson(response);
            
        } catch (Exception e) {
            // Logovanie a vrátenie chyby
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND, 
                    "Error retrieving items: " + e.getMessage());
            return gson.toJson(new Response("Error retrieving items: " + e.getMessage(), 500));
        }
    }
    
    /**
     * Získa položky menu podľa zadanej kategórie.
     * 
     * @param json JSON požiadavka obsahujúca kategóriu
     * @return JSON odpoveď so zoznamom položiek v danej kategórii
     */
    public String getItemsByCategory(String json) {
        try {
            // Deserializácia JSON na mapu
            Type mapType = new TypeToken<Map<String, String>>(){}.getType();
            Map<String, String> requestData = gson.fromJson(json, mapType);
            String category = requestData.get("category");
            
            // Validácia kategórie
            if (category == null || category.isEmpty()) {
                logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND, 
                        MenuManagementMessages.INVALID_REQUEST_FORMAT.getMessage(this.getLanguage()));
                return gson.toJson(new Response("Category is required", 400));
            }
            
            // Získanie všetkých položiek a filtrovanie podľa kategórie
            List<MenuItem> allItems = menuItemDAO.getAll();
            List<MenuItem> itemsInCategory = allItems.stream()
                    .filter(item -> item.getCategories() != null && item.getCategories().contains(category))
                    .collect(Collectors.toList());
            
            // Vytvorenie odpovede
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Items retrieved successfully");
            response.put("status", 200);
            response.put("data", itemsInCategory);
            
            return gson.toJson(response);
            
        } catch (JsonSyntaxException e) {
            // Chyba pri parsovaní JSON
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND, 
                    MenuManagementMessages.INVALID_REQUEST_FORMAT.getMessage(this.getLanguage()));
            return gson.toJson(new Response("Invalid request format", 400));
        } catch (Exception e) {
            // Iné chyby
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND, 
                    "Error retrieving items by category: " + e.getMessage());
            return gson.toJson(new Response("Error retrieving items: " + e.getMessage(), 500));
        }
    }
    
    /**
     * Získa zoznam všetkých kategórií v systéme.
     * 
     * @param json JSON požiadavka (v tomto prípade neobsahuje žiadne parametre)
     * @return JSON odpoveď so zoznamom unikátnych kategórií
     */
    public String getAllCategories(String json) {
        try {
            // Získanie všetkých položiek a extrakcia unikátnych kategórií
            List<MenuItem> items = menuItemDAO.getAll();
            Set<String> categories = new HashSet<>();
            
            for (MenuItem item : items) {
                if (item.getCategories() != null) {
                    categories.addAll(item.getCategories());
                }
            }
            
            // Vytvorenie odpovede
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Categories retrieved successfully");
            response.put("status", 200);
            response.put("data", categories);
            
            return gson.toJson(response);
            
        } catch (Exception e) {
            // Logovanie a vrátenie chyby
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND, 
                    "Error retrieving categories: " + e.getMessage());
            return gson.toJson(new Response("Error retrieving categories: " + e.getMessage(), 500));
        }
    }
    
    /**
     * Pridá novú kategóriu do systému.
     * Poznámka: Kategórie sú implementované pomocou "dummy" položiek menu,
     * čo nie je ideálne riešenie, ale postačuje pre demo účely.
     * 
     * @param json JSON požiadavka obsahujúca názov kategórie
     * @return JSON odpoveď o výsledku operácie
     */
    public String addCategory(String json) {
        try {
            // Deserializácia JSON na mapu
            Type mapType = new TypeToken<Map<String, String>>(){}.getType();
            Map<String, String> requestData = gson.fromJson(json, mapType);
            String category = requestData.get("name");
            
            // Validácia názvu kategórie
            if (category == null || category.isEmpty()) {
                logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND, 
                        MenuManagementMessages.INVALID_REQUEST_FORMAT.getMessage(this.getLanguage()));
                return gson.toJson(new Response("Category name is required", 400));
            }
            
            // Kontrola, či kategória už existuje
            List<MenuItem> items = menuItemDAO.getAll();
            Set<String> categories = new HashSet<>();
            
            for (MenuItem item : items) {
                if (item.getCategories() != null) {
                    categories.addAll(item.getCategories());
                }
            }
            
            if (categories.contains(category)) {
                logger.log(LogType.ERROR, LogPriority.MIDDLE, LogSource.BECKEND, 
                        MenuManagementMessages.CATEGORY_ALREADY_EXISTS.getMessage(this.getLanguage()));
                return gson.toJson(new Response("Category already exists", 400));
            }
            
            // Pre pridanie kategórie vytvoríme dummy položku s touto kategóriou
            // V reálnej aplikácii by bolo lepšie mať samostatnú tabuľku kategórií
            List<String> categoryList = new ArrayList<>();
            categoryList.add(category);
            
            MenuItem dummyItem = new MenuItem("CATEGORY_" + category, categoryList, 
                    "Category placeholder", "Category placeholder", 0, 0.0);
            
            // Vloženie dummy položky do databázy
            int id = menuItemDAO.insert(dummyItem);
            
            // Kontrola úspešnosti operácie
            if (id > 0) {
                logger.log(LogType.SUCCESS, LogPriority.MIDDLE, LogSource.BECKEND, 
                        MenuManagementMessages.CATEGORY_ADDED_SUCCESS.getMessage(this.getLanguage()));
                return gson.toJson(new Response("Category was successfully added", 200));
            } else {
                logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND, 
                        MenuManagementMessages.CATEGORY_ADDED_ERROR.getMessage(this.getLanguage()));
                return gson.toJson(new Response("Failed to add category", 500));
            }
            
        } catch (JsonSyntaxException e) {
            // Chyba pri parsovaní JSON
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND, 
                    MenuManagementMessages.INVALID_REQUEST_FORMAT.getMessage(this.getLanguage()));
            return gson.toJson(new Response("Invalid request format", 400));
        } catch (Exception e) {
            // Iné chyby
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND, 
                    MenuManagementMessages.CATEGORY_ADDED_ERROR.getMessage(this.getLanguage()) + ": " + e.getMessage());
            return gson.toJson(new Response("Error adding category: " + e.getMessage(), 500));
        }
    }
    
    /**
     * Aktualizuje existujúcu kategóriu (premenovanie).
     * 
     * @param json JSON požiadavka obsahujúca starý a nový názov kategórie
     * @return JSON odpoveď o výsledku operácie
     */
    public String updateCategory(String json) {
        try {
            // Deserializácia JSON na mapu
            Type mapType = new TypeToken<Map<String, String>>(){}.getType();
            Map<String, String> requestData = gson.fromJson(json, mapType);
            String oldCategory = requestData.get("prevName");
            String newCategory = requestData.get("newName");
            
            // Validácia názvov kategórií
            if (oldCategory == null || oldCategory.isEmpty() || newCategory == null || newCategory.isEmpty()) {
                logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND, 
                        MenuManagementMessages.INVALID_REQUEST_FORMAT.getMessage(this.getLanguage()));
                return gson.toJson(new Response("Both old and new category names are required", 400));
            }
            
            // Aktualizácia všetkých položiek s touto kategóriou
            List<MenuItem> items = menuItemDAO.getAll();
            int updatedCount = 0;
            
            for (MenuItem item : items) {
                if (item.getCategories() != null && item.getCategories().contains(oldCategory)) {
                    // Nahradenie starej kategórie novou
                    List<String> categories = new ArrayList<>(item.getCategories());
                    categories.remove(oldCategory);
                    categories.add(newCategory);
                    item.setCategories(categories);
                    
                    // Aktualizácia položky v databáze
                    menuItemDAO.update(item.getId(), item);
                    updatedCount++;
                }
            }
            
            // Kontrola úspešnosti operácie
            if (updatedCount > 0) {
                logger.log(LogType.SUCCESS, LogPriority.MIDDLE, LogSource.BECKEND, 
                        MenuManagementMessages.CATEGORY_UPDATED_SUCCESS.getMessage(this.getLanguage()));
                return gson.toJson(new Response("Category was successfully updated", 200));
            } else {
                logger.log(LogType.ERROR, LogPriority.MIDDLE, LogSource.BECKEND, 
                        MenuManagementMessages.CATEGORY_NOT_FOUND.getMessage(this.getLanguage()));
                return gson.toJson(new Response("Category not found", 404));
            }
            
        } catch (JsonSyntaxException e) {
            // Chyba pri parsovaní JSON
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND, 
                    MenuManagementMessages.INVALID_REQUEST_FORMAT.getMessage(this.getLanguage()));
            return gson.toJson(new Response("Invalid request format", 400));
        } catch (Exception e) {
            // Iné chyby
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND, 
                    MenuManagementMessages.CATEGORY_UPDATED_ERROR.getMessage(this.getLanguage()) + ": " + e.getMessage());
            return gson.toJson(new Response("Error updating category: " + e.getMessage(), 500));
        }
    }
    
    /**
     * Odstráni kategóriu zo systému.
     * 
     * @param json JSON požiadavka obsahujúca názov kategórie na odstránenie
     * @return JSON odpoveď o výsledku operácie
     */
    public String deleteCategory(String json) {
        try {
            // Deserializácia JSON na mapu
            Type mapType = new TypeToken<Map<String, String>>(){}.getType();
            Map<String, String> requestData = gson.fromJson(json, mapType);
            String category = requestData.get("name");
            
            // Validácia názvu kategórie
            if (category == null || category.isEmpty()) {
                logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND, 
                        MenuManagementMessages.INVALID_REQUEST_FORMAT.getMessage(this.getLanguage()));
                return gson.toJson(new Response("Category name is required", 400));
            }
            
            // Spracovanie všetkých položiek s touto kategóriou
            List<MenuItem> items = menuItemDAO.getAll();
            int updatedCount = 0;
            int deletedCount = 0;
            
            for (MenuItem item : items) {
                if (item.getCategories() != null && item.getCategories().contains(category)) {
                    // Pre dummy položky kategórií ich odstránime
                    if (item.getName().startsWith("CATEGORY_")) {
                        menuItemDAO.delete(item.getId());
                        deletedCount++;
                    } else {
                        // Pre bežné položky len odstránime kategóriu
                        List<String> categories = new ArrayList<>(item.getCategories());
                        categories.remove(category);
                        item.setCategories(categories);
                        
                        menuItemDAO.update(item.getId(), item);
                        updatedCount++;
                    }
                }
            }
            
            // Kontrola úspešnosti operácie
            if (updatedCount > 0 || deletedCount > 0) {
                logger.log(LogType.SUCCESS, LogPriority.MIDDLE, LogSource.BECKEND, 
                        MenuManagementMessages.CATEGORY_DELETED_SUCCESS.getMessage(this.getLanguage()));
                return gson.toJson(new Response("Category was successfully deleted", 200));
            } else {
                logger.log(LogType.ERROR, LogPriority.MIDDLE, LogSource.BECKEND, 
                        MenuManagementMessages.CATEGORY_NOT_FOUND.getMessage(this.getLanguage()));
                return gson.toJson(new Response("Category not found", 404));
            }
            
        } catch (JsonSyntaxException e) {
            // Chyba pri parsovaní JSON
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND, 
                    MenuManagementMessages.INVALID_REQUEST_FORMAT.getMessage(this.getLanguage()));
            return gson.toJson(new Response("Invalid request format", 400));
        } catch (Exception e) {
            // Iné chyby
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND, 
                    MenuManagementMessages.CATEGORY_DELETED_ERROR.getMessage(this.getLanguage()) + ": " + e.getMessage());
            return gson.toJson(new Response("Error deleting category: " + e.getMessage(), 500));
        }
    }
} 