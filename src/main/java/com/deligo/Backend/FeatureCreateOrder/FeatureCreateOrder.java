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
import java.util.*;

public class FeatureCreateOrder extends BaseFeature {

    private final Gson gson = new Gson();
    private final GenericDAO<Order> orderDAO;
    private final GenericDAO<User> userDAO;
    private final GenericDAO<Tables> tableDAO;
    private final GenericDAO<MenuItem> itemDAO;

    public FeatureCreateOrder(ConfigLoader config, LoggingAdapter logger, RestAPIServer server) {
        super(config, logger, server);
        this.orderDAO = new GenericDAO<>(Order.class, "orders");
        this.userDAO = new GenericDAO<>(User.class, "users");
        this.tableDAO = new GenericDAO<>(Tables.class, "tables");
        this.itemDAO = new GenericDAO<>(MenuItem.class, "menu_items");
    }

    public String createOrder(String jsonData) {
        try {

            Type listType = new TypeToken<List<Map<String, Object>>>(){}.getType();
            List<Map<String, Object>> items = gson.fromJson(jsonData, listType);

            if (items == null || items.isEmpty()) {
                return gson.toJson(new Response("No items in order", 400));
            }

            // 2. Validácia položiek
            for (Map<String, Object> item : items) {
                int itemId = ((Double) item.get("itemId")).intValue(); // Gson číta číselné hodnoty ako Double
                Optional<MenuItem> menuItemOpt = itemDAO.findOneByField("id", itemId);
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
            newOrder.setCreated_at(String.valueOf(new Date())); // môžeš nahradiť LocalDateTime atď.
            newOrder.setOrder_contain(jsonData); // celý JSON ako String

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
