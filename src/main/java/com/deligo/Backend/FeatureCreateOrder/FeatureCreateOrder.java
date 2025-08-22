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

    public String createOrder(String json) {
        try {
            // Parse input JSON
            Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
            Map<String, Object> requestData = gson.fromJson(json, mapType);

            // Create new order
            Order order = new Order();
            
            // Set user ID
            order.setUser_id(((Double) requestData.get("user_id")).intValue());
            
            // Set table ID if provided, otherwise set to 0
            Object tableIdObj = requestData.get("table_id");
            if (tableIdObj != null) {
                order.setTable_id(((Double) tableIdObj).intValue());
            } else {
                order.setTable_id(0);
            }
            
            // Set status
            order.setStatus((String) requestData.get("status"));
            
            // Set created_at as current timestamp in string format
            order.setCreated_at(new java.sql.Timestamp(System.currentTimeMillis()).toString());
            
            // Set order items
            Type listType = new TypeToken<List<OrderItem>>(){}.getType();
            List<OrderItem> items = gson.fromJson(gson.toJson(requestData.get("items")), listType);
            order.setItems(items);

            // Insert order into database
            int orderId = orderDAO.insert(order);
            
            if (orderId > 0) {
                logger.log(LogType.SUCCESS, LogPriority.MIDDLE, LogSource.BECKEND,
                        "Order created successfully");
                return gson.toJson(new Response("Order created successfully", 200));
            } else {
                logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND,
                        "Failed to create order");
                return gson.toJson(new Response("Failed to create order", 500));
            }
        } catch (Exception e) {
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND,
                    "Error creating order: " + e.getMessage());
            return gson.toJson(new Response("Error creating order: " + e.getMessage(), 500));
        }
    }

}
