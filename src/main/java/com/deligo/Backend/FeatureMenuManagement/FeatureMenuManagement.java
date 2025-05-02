package com.deligo.Backend.FeatureMenuManagement;

import com.deligo.Backend.BaseFeature.BaseFeature;
import com.deligo.ConfigLoader.ConfigLoader;
import com.deligo.DatabaseManager.dao.GenericDAO;
import com.deligo.Logging.Adapter.LoggingAdapter;
import com.deligo.Model.*;
import com.deligo.Model.BasicModels.LogPriority;
import com.deligo.Model.BasicModels.LogSource;
import com.deligo.Model.BasicModels.LogType;
import com.deligo.Model.BasicModels.OrderState;
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
    private final GenericDAO<MenuItemInsert> menuItemDAO;
    // DAO pre prístup k databáze kategórií
    private final GenericDAO<Category> categoryDAO;
    // DAO pre prístup k databáze objednávok
    private final GenericDAO<Order> orderDAO;
    // DAO pre prístup k databáze položiek objednávok
    private final GenericDAO<OrderItem> orderItemDAO;
    // DAO pre prístup k databáze prekladov položiek menu
    private final GenericDAO<MenuItemTranslation> menuItemTranslationDAO;
    // Gson pre serializáciu/deserializáciu JSON
    private final Gson gson;

    /**
     * Konštruktor inicializuje DAO pre položky menu, kategórie a logovací systém.
     * 
     * @param globalConfig globálna konfigurácia aplikácie
     * @param logger logovací adaptér
     * @param restApiServer REST API server
     */
    public FeatureMenuManagement(ConfigLoader globalConfig, LoggingAdapter logger, RestAPIServer restApiServer) {
        super(globalConfig, logger, restApiServer);
        this.menuItemDAO = new GenericDAO<>(MenuItemInsert.class, "menu_items");
        this.categoryDAO = new GenericDAO<>(Category.class, "categories");
        this.orderDAO = new GenericDAO<>(Order.class, "orders");
        this.orderItemDAO = new GenericDAO<>(OrderItem.class, "order_items");
        this.menuItemTranslationDAO = new GenericDAO<>(MenuItemTranslation.class, "menu_item_translations");
        this.gson = new Gson();
        logger.log(LogType.INFO, LogPriority.MIDDLE, LogSource.BECKEND, 
                MenuManagementMessages.PROCESS_NAME.getMessage(this.getLanguage()));
    }

    /**
     * Kontroluje, či je aktuálne prihlásený používateľ administrátor.
     * 
     * @return true ak je používateľ administrátor, inak false
     */
    private boolean isAdmin() {
        // Kontrola, či je používateľ prihlásený
        Boolean userStatus = globalConfig.getConfigValue("login", "status", Boolean.class);
        if (userStatus == null || !userStatus) {
            return false;
        }

        // Kontrola, či má používateľ rolu administrátora
        String userRole = globalConfig.getConfigValue("login", "role", String.class);
        return userRole != null && userRole.toLowerCase().contains("admin");
    }

    /**
     * Kontroluje, či je položka menu použitá v nejakej otvorenej objednávke.
     * 
     * @param itemId ID položky menu
     * @return true ak je položka použitá v otvorenej objednávke, inak false
     */
    private boolean isItemInUse(int itemId) {
        // Získanie všetkých objednávok
        List<Order> orders = orderDAO.getAll();

        // Filtrovanie objednávok, ktoré nie sú v stave DONE
        List<Order> activeOrders = orders.stream()
                .filter(order -> !OrderState.DONE.getValue().equals(order.getStatus()))
                .collect(Collectors.toList());

        // Kontrola, či je položka použitá v nejakej aktívnej objednávke
        for (Order order : activeOrders) {
            List<OrderItem> items = orderItemDAO.findByField("order_id", order.getId());
            for (OrderItem item : items) {
                if (item.getMenuItemId() == itemId) {
                    return true;
                }
            }
        }

        return false;
    }


    /**
     * Pridá novú položku do menu.
     * 
     * @param json JSON reprezentácia položky menu
     * @return JSON odpoveď o výsledku operácie
     */
    public String addItem(String json) {
        try {
            // Kontrola, či je používateľ administrátor
            if (!isAdmin()) {
                String msg = MenuManagementMessages.ADMIN_REQUIRED.getMessage(this.getLanguage());
                logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND, msg);
                return gson.toJson(new Response(msg, 403));
            }

            // Deserializácia JSON na objekt MenuItem
            MenuItem item = gson.fromJson(json, MenuItem.class);

            // Extrakcia lokalizovaných polí
            String name = item.getName();
            String description = item.getDescription();
            String details = item.getDetails();

            // Validácia povinných polí
            if (name == null || name.isEmpty()) {
                logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND, 
                        MenuManagementMessages.INVALID_REQUEST_FORMAT.getMessage(this.getLanguage()));
                return gson.toJson(new Response("Item name is required", 400));
            }

            if (details == null || details.isEmpty()) {
                logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND, 
                        MenuManagementMessages.DETAILS_REQUIRED.getMessage(this.getLanguage()));
                return gson.toJson(new Response("Item details are required", 400));
            }

            // Spracovanie kategórie
            if (item.getCategories() != null && !item.getCategories().isEmpty()) {
                // Pre spätnú kompatibilitu - ak položka má nastavený zoznam kategórií
                String categoryName = item.getCategories().get(0);

                // Nájdenie kategórie podľa názvu
                List<Category> categories = categoryDAO.findByField("name", categoryName);
                if (!categories.isEmpty()) {
                    // Nastavenie ID kategórie
                    item.setCategoryId(categories.get(0).getId());
                } else {
                    // Kategória neexistuje, vytvoríme novú
                    Category newCategory = new Category(categoryName);
                    int category_id = categoryDAO.insert(newCategory);
                    item.setCategoryId(category_id);
                }
            }

            // Kontrola, či položka s rovnakým názvom už existuje
            List<MenuItemTranslation> existingTranslations = menuItemTranslationDAO.findByField("name", name);
            if (!existingTranslations.isEmpty()) {
                logger.log(LogType.ERROR, LogPriority.MIDDLE, LogSource.BECKEND, 
                        MenuManagementMessages.ITEM_ALREADY_EXISTS.getMessage(this.getLanguage()));
                return gson.toJson(new Response("Item with this name already exists", 400));
            }

            // Vytvorenie novej položky menu s nelokalizovanými poľami
            MenuItemInsert newItem = new MenuItemInsert(item.getCategoryId(), item.getPrice(), item.getIsAvailable());

            // Vloženie položky do databázy
            int menuItemId = menuItemDAO.insert(newItem);

            // Kontrola úspešnosti operácie
            if (menuItemId > 0) {
                // Vytvorenie prekladu položky
                String language = this.getLanguage(); // Aktuálny jazyk
                MenuItemTranslation translation = new MenuItemTranslation(menuItemId, language, name, description);

                // Vloženie prekladu do databázy
                int translationId = menuItemTranslationDAO.insert(translation);

                if (translationId > 0) {
                    logger.log(LogType.SUCCESS, LogPriority.MIDDLE, LogSource.BECKEND, 
                            MenuManagementMessages.ITEM_ADDED_SUCCESS.getMessage(this.getLanguage()));
                    return gson.toJson(new Response("Item was successfully added", 200));
                } else {
                    // Ak sa nepodarilo vložiť preklad, odstránime aj položku
                    menuItemDAO.delete(menuItemId);
                    logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND, 
                            "Failed to add item translation");
                    return gson.toJson(new Response("Failed to add item translation", 500));
                }
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
            // Kontrola, či je používateľ administrátor
            if (!isAdmin()) {
                String msg = MenuManagementMessages.ADMIN_REQUIRED.getMessage(this.getLanguage());
                logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND, msg);
                return gson.toJson(new Response(msg, 403));
            }

            // Deserializácia JSON na mapu
            Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
            Map<String, Object> requestData = gson.fromJson(json, mapType);
            int itemId = ((Double) requestData.get("itemId")).intValue();

            // Overenie existencie položky
            Optional<MenuItemInsert> itemOpt = menuItemDAO.getById(itemId);
            if (!itemOpt.isPresent()) {
                logger.log(LogType.ERROR, LogPriority.MIDDLE, LogSource.BECKEND, 
                        MenuManagementMessages.ITEM_NOT_FOUND.getMessage(this.getLanguage()));
                return gson.toJson(new Response("Item not found", 404));
            }

            // Získanie dát z požiadavky
            MenuItemInsert item = itemOpt.get();
            String name = (String) requestData.get("name");
            String description = (String) requestData.get("description");
            String details = (String) requestData.get("details");
            float price = ((Double) requestData.get("price")).floatValue();

            // Validácia povinných polí
            if (details == null || details.isEmpty()) {
                logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND, 
                        MenuManagementMessages.DETAILS_REQUIRED.getMessage(this.getLanguage()));
                return gson.toJson(new Response("Item details are required", 400));
            }

            // Spracovanie kategórií
            @SuppressWarnings("unchecked")
            List<Object> categoriesObj = (List<Object>) requestData.get("categories");
            if (categoriesObj != null && !categoriesObj.isEmpty()) {
                // Získanie názvu kategórie
                String categoryName = categoriesObj.get(0).toString();

                // Nájdenie kategórie podľa názvu
                List<Category> categories = categoryDAO.findByField("name", categoryName);
                if (!categories.isEmpty()) {
                    // Nastavenie ID kategórie
                    item.setCategory_id(categories.get(0).getId());
                } else {
                    // Kategória neexistuje, vytvoríme novú
                    Category newCategory = new Category(categoryName);
                    int category_id = categoryDAO.insert(newCategory);
                    item.setCategory_id(category_id);
                }

            }

            // Spracovanie dostupnosti
            boolean isAvailableObj = (boolean) requestData.get("is_available");
            if (isAvailableObj) {
                item.setIs_available(true);
            }

            // Aktualizácia nelokalizovaných polí položky
            item.setPrice(price);

            // Aktualizácia položky v databáze
            menuItemDAO.update(itemId, item);

            // Aktualizácia alebo vytvorenie prekladu
            String language = this.getLanguage();
            List<MenuItemTranslation> translations = menuItemTranslationDAO.findByField("menu_item_id", itemId);

            // Filtrovanie prekladov podľa jazyka
            Optional<MenuItemTranslation> translationOpt = translations.stream()
                    .filter(t -> language.equals(t.getLanguage()))
                    .findFirst();

            if (translationOpt.isPresent()) {
                // Aktualizácia existujúceho prekladu
                MenuItemTranslation translation = translationOpt.get();
                translation.setName(name);
                translation.setDescription(description);
                menuItemTranslationDAO.update(translation.getId(), translation);
            } else {
                // Vytvorenie nového prekladu
                MenuItemTranslation translation = new MenuItemTranslation(itemId, language, name, description);
                menuItemTranslationDAO.insert(translation);
            }

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
            // Kontrola, či je používateľ administrátor
            if (!isAdmin()) {
                String msg = MenuManagementMessages.ADMIN_REQUIRED.getMessage(this.getLanguage());
                logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND, msg);
                return gson.toJson(new Response(msg, 403));
            }

            // Deserializácia JSON na mapu
            Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
            Map<String, Object> requestData = gson.fromJson(json, mapType);
            int itemId = ((Double) requestData.get("itemId")).intValue();
            String name = (String) requestData.get("name");

            // Overenie existencie položky
            Optional<MenuItemInsert> itemOpt = menuItemDAO.getById(itemId);
            if (!itemOpt.isPresent()) {
                logger.log(LogType.ERROR, LogPriority.MIDDLE, LogSource.BECKEND, 
                        MenuManagementMessages.ITEM_NOT_FOUND.getMessage(this.getLanguage()));
                return gson.toJson(new Response("Item not found", 404));
            }

            MenuItemInsert item = itemOpt.get();


            // Kontrola, či položka nie je použitá v nejakej otvorenej objednávke
            if (isItemInUse(itemId)) {
                String msg = MenuManagementMessages.ITEM_IN_USE.getMessage(this.getLanguage());
                logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND, msg);
                return gson.toJson(new Response(msg, 400));
            }

            // Odstránenie prekladov položky
            List<MenuItemTranslation> translations = menuItemTranslationDAO.findByField("menu_item_id", itemId);
            for (MenuItemTranslation translation : translations) {
                menuItemTranslationDAO.delete(translation.getId());
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
     * Získa zoznam všetkých kategórií v systéme.
     * 
     * @param json JSON požiadavka (v tomto prípade neobsahuje žiadne parametre)
     * @return JSON odpoveď so zoznamom kategórií
     */
    public String getAllCategories(String json) {
        try {
            // Získanie všetkých kategórií z databázy
            List<Category> categories = categoryDAO.getAll();

            // Pre spätnú kompatibilitu - konverzia na zoznam názvov kategórií
            List<String> categoryNames = categories.stream()
                    .map(Category::getName)
                    .collect(Collectors.toList());

            // Vytvorenie odpovede
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Categories retrieved successfully");
            response.put("status", 200);
            response.put("data", categoryNames); // Pre spätnú kompatibilitu vraciam názvy

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
     * 
     * @param json JSON požiadavka obsahujúca názov kategórie
     * @return JSON odpoveď o výsledku operácie
     */
    public String addCategory(String json) {
        try {
            // Kontrola, či je používateľ administrátor
            if (!isAdmin()) {
                String msg = MenuManagementMessages.ADMIN_REQUIRED.getMessage(this.getLanguage());
                logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND, msg);
                return gson.toJson(new Response(msg, 403));
            }

            // Deserializácia JSON na mapu
            Type mapType = new TypeToken<Map<String, String>>(){}.getType();
            Map<String, String> requestData = gson.fromJson(json, mapType);
            String categoryName = requestData.get("name");

            // Validácia názvu kategórie
            if (categoryName == null || categoryName.isEmpty()) {
                logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND, 
                        MenuManagementMessages.INVALID_REQUEST_FORMAT.getMessage(this.getLanguage()));
                return gson.toJson(new Response("Category name is required", 400));
            }

            // Kontrola, či kategória už existuje
            List<Category> existingCategories = categoryDAO.findByField("name", categoryName);

            if (!existingCategories.isEmpty()) {
                logger.log(LogType.ERROR, LogPriority.MIDDLE, LogSource.BECKEND, 
                        MenuManagementMessages.CATEGORY_ALREADY_EXISTS.getMessage(this.getLanguage()));
                return gson.toJson(new Response("Category already exists", 400));
            }

            // Vytvorenie novej kategórie
            Category newCategory = new Category(categoryName);

            // Vloženie kategórie do databázy
            int id = categoryDAO.insert(newCategory);

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
     * Odstráni kategóriu zo systému.
     * 
     * @param json JSON požiadavka obsahujúca názov kategórie na odstránenie
     * @return JSON odpoveď o výsledku operácie
     */
    public String deleteCategory(String json) {
        try {
            // Kontrola, či je používateľ administrátor
            if (!isAdmin()) {
                String msg = MenuManagementMessages.ADMIN_REQUIRED.getMessage(this.getLanguage());
                logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND, msg);
                return gson.toJson(new Response(msg, 403));
            }

            // Deserializácia JSON na mapu
            Type mapType = new TypeToken<Map<String, String>>(){}.getType();
            Map<String, String> requestData = gson.fromJson(json, mapType);
            String categoryName = requestData.get("name");

            // Validácia názvu kategórie
            if (categoryName == null || categoryName.isEmpty()) {
                logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND, 
                        MenuManagementMessages.INVALID_REQUEST_FORMAT.getMessage(this.getLanguage()));
                return gson.toJson(new Response("Category name is required", 400));
            }

            // Nájdenie kategórie podľa názvu
            List<Category> categories = categoryDAO.findByField("name", categoryName);
            if (categories.isEmpty()) {
                logger.log(LogType.ERROR, LogPriority.MIDDLE, LogSource.BECKEND, 
                        MenuManagementMessages.CATEGORY_NOT_FOUND.getMessage(this.getLanguage()));
                return gson.toJson(new Response("Category not found", 404));
            }

            Category category = categories.get(0);
            int category_id = category.getId();


            // Aktualizácia všetkých položiek s touto kategóriou
            List<MenuItemInsert> items = menuItemDAO.findByField("category_id", category_id);
            int updatedCount = 0;

            // Pre položky, ktoré používajú túto kategóriu, nastavíme category_id na 0
            for (MenuItemInsert item : items) {
                item.setCategory_id(0); // 0 znamená žiadna kategória
                menuItemDAO.update(item.getCategory_id(), item);
                updatedCount++;
            }

            // Odstránenie kategórie
            categoryDAO.delete(category_id);

            logger.log(LogType.SUCCESS, LogPriority.MIDDLE, LogSource.BECKEND, 
                    MenuManagementMessages.CATEGORY_DELETED_SUCCESS.getMessage(this.getLanguage()));
            return gson.toJson(new Response("Category was successfully deleted", 200));

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

    public String getAllItems(String json) {
        try {
            // Get all menu items
            List<MenuItemInsert> menuItems = menuItemDAO.getAll();
            
            // Get current language
            String language = this.getLanguage();
            
            // Get all translations for the current language
            List<MenuItemTranslation> translations = menuItemTranslationDAO.findByField("language", language);
            
            // Create a map of item ID to translation for quick lookup
            Map<Integer, MenuItemTranslation> translationMap = translations.stream()
                .collect(Collectors.toMap(MenuItemTranslation::getMenuItemId, t -> t));
            
            // Combine menu items with their translations
            List<Map<String, Object>> result = menuItems.stream()
                .map(item -> {
                    Map<String, Object> itemMap = new HashMap<>();
                    itemMap.put("id", item.getId());
                    itemMap.put("categoryId", item.getCategory_id());
                    itemMap.put("price", item.getPrice());
                    itemMap.put("isAvailable", item.isIs_available());
                    
                    // Add translation if available
                    MenuItemTranslation translation = translationMap.get(item.getId());
                    if (translation != null) {
                        itemMap.put("name", translation.getName());
                        itemMap.put("description", translation.getDescription());
                    }
                    
                    return itemMap;
                })
                .collect(Collectors.toList());
            
            logger.log(LogType.INFO, LogPriority.LOW, LogSource.BECKEND,
                    "Retrieved all menu items successfully");
            return gson.toJson(new Response(gson.toJson(result), 200));
        } catch (Exception e) {
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND,
                    "Failed to get menu items");
            return gson.toJson(new Response("Failed to get menu items", 500));
        }
    }
} 
