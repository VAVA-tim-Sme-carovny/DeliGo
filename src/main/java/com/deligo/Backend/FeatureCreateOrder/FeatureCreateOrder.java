package com.deligo.Backend.FeatureCreateOrder;

import com.deligo.Backend.BaseFeature.BaseFeature;
import com.deligo.ConfigLoader.ConfigLoader;
import com.deligo.DatabaseManager.dao.GenericDAO;
import com.deligo.Logging.Adapter.LoggingAdapter;
import com.deligo.Model.*;
import com.deligo.Model.BasicModels.*;
import com.deligo.RestApi.RestAPIServer;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class FeatureCreateOrder extends BaseFeature {

    private final Gson gson = new Gson();
    private final GenericDAO<Order> orderDAO;
    private final GenericDAO<User> userDAO;
    private final GenericDAO<Tables> tableDAO;
    private final GenericDAO<MenuItemInsert> itemDAO;

    public FeatureCreateOrder(ConfigLoader config, LoggingAdapter logger, RestAPIServer server) {
        super(config, logger, server);
        this.orderDAO = new GenericDAO<>(Order.class, "orders");
        this.userDAO = new GenericDAO<>(User.class, "users");
        this.tableDAO = new GenericDAO<>(Tables.class, "tables");
        this.itemDAO = new GenericDAO<>(MenuItemInsert.class, "menu_items");
    }

    public String createOrder(String rawJsonData) {
        try {

            JsonObject rootObject = JsonParser.parseString(rawJsonData).getAsJsonObject();
            JsonArray messageArray = rootObject.getAsJsonArray("message");

            if (messageArray == null || messageArray.size() == 0) {
                return gson.toJson(new Response("No items in order", 400));
            }

            // Konverzia JSON array na List<Map>
            Type listType = new TypeToken<List<Map<String, Object>>>(){}.getType();
            List<Map<String, Object>> items = gson.fromJson(messageArray, listType);

            // Validácia položiek
            for (Map<String, Object> item : items) {
                int itemId = ((Double) item.get("itemId")).intValue();
                Optional<MenuItemInsert> menuItemOpt = itemDAO.getById(itemId);
                if (menuItemOpt.isEmpty()) {
                    return gson.toJson(new Response("Item with ID " + itemId + " not found", 404));
                }
            }


            // Získanie prihlaseného používateľa
            String tableId = globalConfig.getConfigValue("device", "id", String.class);
            String username = globalConfig.getConfigValue("login", "user", String.class);
            Order newOrder = new Order();
            if(!username.isEmpty() || !username.equals("null")) {
                Optional<User> userOpt = userDAO.findOneByField("username", username);
                if (userOpt.isEmpty()) {
                    return gson.toJson(new Response("Logged-in user not found", 500));
                }else{
                    int userId = userOpt.get().getId();
                    newOrder.setUser_id(userId);
                    newOrder.setTable_id(null);
                }
            }else if(!tableId.isEmpty() || !tableId.equals("null")){
                Optional<Tables> currentTable = tableDAO.findOneByField("name", tableId);
                List<Order> existing = orderDAO.findByField("table_id", tableId);
                for (Order o : existing) {
                    if ("in progress".equalsIgnoreCase(o.getStatus())) {
                        return gson.toJson(new Response("Order already in progress for this table", 500));
                    }
                }
                if(!currentTable.isPresent()) {
                    newOrder.setTable_id(currentTable.get().getId());
                }else{
                    return gson.toJson(new Response("This table doesnt exist in DB", 500));
                }

            }else{
                logger.log(LogType.ERROR, LogPriority.MIDDLE, LogSource.BECKEND, "Kokot");
            }

            // Príprava objednávky

             // defaultne nula ak nie je
            newOrder.setStatus(Status.pending.toString());
            LocalDateTime now = LocalDateTime.now();


            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            Timestamp timestamp = Timestamp.valueOf(now);

            newOrder.setCreated_at(timestamp);

            newOrder.setOrder_contain(messageArray.toString());

            orderDAO.insert(newOrder);

            logger.log(LogType.SUCCESS, LogPriority.MIDDLE, LogSource.BECKEND, "Order created successfully");
            return gson.toJson(new Response("Order created successfully", 200));

        } catch (JsonSyntaxException e) {
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND, "Invalid JSON: " + e.getMessage());
            return gson.toJson(new Response("Invalid JSON format", 400));
        } catch (Exception e) {
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND, "Order creation failed: " + e.getMessage());
            return gson.toJson(new Response("Failed to create order", 500));
        }
    }

}
