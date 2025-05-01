package com.deligo.Backend.FeatureOrderManagement;

import com.deligo.Backend.BaseFeature.BaseFeature;
import com.deligo.Logging.Adapter.LoggingAdapter;
import com.deligo.ConfigLoader.ConfigLoader;
import com.deligo.RestApi.RestAPIServer;
import com.deligo.DatabaseManager.dao.GenericDAO;
import com.deligo.Model.Order;
import com.deligo.Model.OrderItem;
import com.deligo.Model.MenuItem;
import com.deligo.Model.Response;
import com.deligo.Model.BasicModels.LogType;
import com.deligo.Model.BasicModels.LogPriority;
import com.deligo.Model.BasicModels.LogSource;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class FeatureOrderManagement extends BaseFeature {
    private final Gson gson;
    private final GenericDAO<Order> orderDAO;
    private final GenericDAO<OrderItem> orderItemDAO;
    private final GenericDAO<MenuItem> menuItemDAO;

    public FeatureOrderManagement(ConfigLoader globalConfig, LoggingAdapter logger, RestAPIServer restApiServer) {
        super(globalConfig, logger, restApiServer);
        this.gson = new Gson();
        this.orderDAO = new GenericDAO<>(Order.class, "orders");
        this.orderItemDAO = new GenericDAO<>(OrderItem.class, "order_items");
        this.menuItemDAO = new GenericDAO<>(MenuItem.class, "menu_items");
        logger.log(LogType.INFO, LogPriority.MIDDLE, LogSource.BECKEND, 
                OrderManagementMessages.PROCESS_NAME.getMessage(this.getLanguage()));
    }

    // Vytvorenie objednávky
    public String createOrder(String json) {
        try {
            Type mapType = new TypeToken<Map<String, Object>>() {}.getType();
            Map<String, Object> requestData = gson.fromJson(json, mapType);
            
            Integer userId = requestData.get("userId") != null ? ((Number) requestData.get("userId")).intValue() : null;
            Integer tableId = ((Number) requestData.get("tableId")).intValue();
            String deviceId = (String) requestData.get("deviceId");
            String note = (String) requestData.get("note");
            
            Type itemsType = new TypeToken<List<Map<String, Object>>>() {}.getType();
            List<Map<String, Object>> items = gson.fromJson(gson.toJson(requestData.get("items")), itemsType);

            if (tableId <= 0 || 
                (userId == null && deviceId == null) ||
                items == null || items.isEmpty()) {
                logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND,
                        OrderManagementMessages.INVALID_REQUEST_FORMAT.getMessage(this.getLanguage()));
                return gson.toJson(new Response(OrderManagementMessages.INVALID_REQUEST_FORMAT.getMessage(this.getLanguage()), 400));
            }

            Order order = new Order();
            order.setUserId(userId);
            order.setTableId(tableId);
            order.setDeviceId(deviceId);
            order.setStatus("pending");
            order.setNote(note);
            
            Timestamp now = new Timestamp(System.currentTimeMillis());
            order.setCreatedAt(now);
            order.setUpdatedAt(now);

            int orderId = orderDAO.insert(order);

            if (orderId > 0) {
                for (Map<String, Object> item : items) {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrderId(orderId);
                    orderItem.setMenuItemId(((Number) item.get("menuItemId")).intValue());
                    orderItem.setQuantity(((Number) item.get("quantity")).intValue());
                    orderItemDAO.insert(orderItem);
                }

                logger.log(LogType.SUCCESS, LogPriority.HIGH, LogSource.BECKEND,
                        OrderManagementMessages.ORDER_CREATED_SUCCESS.getMessage(this.getLanguage()));
                return gson.toJson(new Response(OrderManagementMessages.ORDER_CREATED_SUCCESS.getMessage(this.getLanguage()), 200));
            } else {
                logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND,
                        OrderManagementMessages.ORDER_CREATED_FAILED.getMessage(this.getLanguage()));
                return gson.toJson(new Response(OrderManagementMessages.ORDER_CREATED_FAILED.getMessage(this.getLanguage()), 500));
            }
        } catch (Exception e) {
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND,
                    OrderManagementMessages.ORDER_CREATED_FAILED.getMessage(this.getLanguage()) + ": " + e.getMessage());
            return gson.toJson(new Response(OrderManagementMessages.ORDER_CREATED_FAILED.getMessage(this.getLanguage()), 500));
        }
    }

    // Updatovanie objednávky
    public String updateOrder(String json) {
        try {
            Type mapType = new TypeToken<Map<String, Object>>() {}.getType();
            Map<String, Object> requestData = gson.fromJson(json, mapType);
            
            int orderId = ((Number) requestData.get("orderId")).intValue();
            String note = (String) requestData.get("note");
            
            Type itemsType = new TypeToken<List<Map<String, Object>>>() {}.getType();
            List<Map<String, Object>> items = gson.fromJson(gson.toJson(requestData.get("items")), itemsType);

            Optional<Order> optionalOrder = orderDAO.getById(orderId);
            if (optionalOrder.isEmpty()) {
                logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND,
                        OrderManagementMessages.ORDER_NOT_FOUND.getMessage(this.getLanguage()));
                return gson.toJson(new Response(OrderManagementMessages.ORDER_NOT_FOUND.getMessage(this.getLanguage()), 404));
            }

            Order order = optionalOrder.get();
            
            // Pozriem ci objednávka je staršia ako 2 minúty ak nie tak vrátim chybu
            Timestamp now = new Timestamp(System.currentTimeMillis());
            long timeDifference = now.getTime() - order.getCreatedAt().getTime();
            if (timeDifference > 120000) { // 2 minutes
                logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND,
                        OrderManagementMessages.ORDER_UPDATE_DEADLINE_EXPIRED.getMessage(this.getLanguage()));
                return gson.toJson(new Response(OrderManagementMessages.ORDER_UPDATE_DEADLINE_EXPIRED.getMessage(this.getLanguage()), 400));
            }

            // Vymažem existujúce položky objednávky
            List<OrderItem> existingItems = orderItemDAO.findByField("order_id", String.valueOf(orderId));
            for (OrderItem item : existingItems) {
                orderItemDAO.delete(item.getId());
            }

            // Pridám nové položky objednávky
            for (Map<String, Object> itemData : items) {
                OrderItem newItem = new OrderItem();
                newItem.setOrderId(orderId);
                newItem.setMenuItemId(((Number) itemData.get("menuItemId")).intValue());
                newItem.setQuantity(((Number) itemData.get("quantity")).intValue());
                orderItemDAO.insert(newItem);
            }

            // Aktualizujem objednávku
            if (note != null) {
                order.setNote(note);
            }
            order.setStatus("modified");
            orderDAO.update(orderId, order);

            logger.log(LogType.INFO, LogPriority.LOW, LogSource.BECKEND,
                    OrderManagementMessages.ORDER_UPDATE_SUCCESS.getMessage(this.getLanguage()));
            return gson.toJson(new Response(OrderManagementMessages.ORDER_UPDATE_SUCCESS.getMessage(this.getLanguage()), 200));
        } catch (Exception e) {
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND,
                    OrderManagementMessages.ORDER_UPDATE_FAILED.getMessage(this.getLanguage()));
            return gson.toJson(new Response(OrderManagementMessages.ORDER_UPDATE_FAILED.getMessage(this.getLanguage()), 500));
        }
    }

    // Získanie objednávky podľa ID
    public String getOrderById(String json) {
        try {
            Type mapType = new TypeToken<Map<String, Object>>() {}.getType();
            Map<String, Object> requestData = gson.fromJson(json, mapType);
            int orderId = ((Number) requestData.get("orderId")).intValue();

            Optional<Order> optionalOrder = orderDAO.getById(orderId);
            if (optionalOrder.isEmpty()) {
                logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND,
                        OrderManagementMessages.ORDER_NOT_FOUND.getMessage(this.getLanguage()));
                return gson.toJson(new Response(OrderManagementMessages.ORDER_NOT_FOUND.getMessage(this.getLanguage()), 404));
            }

            Order order = optionalOrder.get();
            List<OrderItem> items = orderItemDAO.findByField("order_id", String.valueOf(orderId));
            order.setItems(items);

            logger.log(LogType.INFO, LogPriority.LOW, LogSource.BECKEND,
                    OrderManagementMessages.ORDER_UPDATED_SUCCESS.getMessage(this.getLanguage()));
            return gson.toJson(new Response(gson.toJson(order), 200));
        } catch (Exception e) {
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND,
                    OrderManagementMessages.ORDER_UPDATED_FAILED.getMessage(this.getLanguage()));
            return gson.toJson(new Response(OrderManagementMessages.ORDER_UPDATED_FAILED.getMessage(this.getLanguage()), 500));
        }
    }

    // Získanie objednávok podľa stola
    public String getOrdersByTable(String json) {
        try {
            Type mapType = new TypeToken<Map<String, Object>>() {}.getType();
            Map<String, Object> requestData = gson.fromJson(json, mapType);
            int tableId = ((Number) requestData.get("tableId")).intValue();

            List<Order> orders = orderDAO.findByField("table_id", String.valueOf(tableId));
            
            for (Order order : orders) {
                List<OrderItem> items = orderItemDAO.findByField("order_id", String.valueOf(order.getId()));
                order.setItems(items);
            }

            logger.log(LogType.INFO, LogPriority.LOW, LogSource.BECKEND,
                    OrderManagementMessages.ORDER_UPDATED_SUCCESS.getMessage(this.getLanguage()));
            return gson.toJson(new Response(gson.toJson(orders), 200));
        } catch (Exception e) {
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND,
                    OrderManagementMessages.ORDER_UPDATED_FAILED.getMessage(this.getLanguage()));
            return gson.toJson(new Response(OrderManagementMessages.ORDER_UPDATED_FAILED.getMessage(this.getLanguage()), 500));
        }
    }

    public String getMenuByCategory(String json) {
        try {
            Type mapType = new TypeToken<Map<String, Object>>() {}.getType();
            Map<String, Object> requestData = gson.fromJson(json, mapType);
            String category = (String) requestData.get("category");

            List<MenuItem> items = menuItemDAO.findByField("category", category);
            
            logger.log(LogType.INFO, LogPriority.LOW, LogSource.BECKEND,
                    OrderManagementMessages.ORDER_ITEM_ADDED_SUCCESS.getMessage(this.getLanguage()));
            return gson.toJson(new Response(gson.toJson(items), 200));
        } catch (Exception e) {
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND,
                    OrderManagementMessages.ORDER_ITEM_ADDED_FAILED.getMessage(this.getLanguage()));
            return gson.toJson(new Response(OrderManagementMessages.ORDER_ITEM_ADDED_FAILED.getMessage(this.getLanguage()), 500));
        }
    }

    // Získanie kategórií menu
    public String getCategories(String json) {
        try {
            List<MenuItem> items = menuItemDAO.findByField("1", "1"); // Get all items
            List<String> categories = items.stream()
                .map(MenuItem::getCategories)
                .flatMap(List::stream)
                .distinct()
                .collect(Collectors.toList());
            
            logger.log(LogType.INFO, LogPriority.LOW, LogSource.BECKEND,
                    OrderManagementMessages.ORDER_ITEM_ADDED_SUCCESS.getMessage(this.getLanguage()));
            return gson.toJson(new Response(gson.toJson(categories), 200));
        } catch (Exception e) {
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND,
                    OrderManagementMessages.ORDER_ITEM_ADDED_FAILED.getMessage(this.getLanguage()));
            return gson.toJson(new Response(OrderManagementMessages.ORDER_ITEM_ADDED_FAILED.getMessage(this.getLanguage()), 500));
        }
    }

    public String getPendingOrders(String json) {
        try {
            List<Order> orders = orderDAO.findByField("status", "pending");
            
            for (Order order : orders) {
                List<OrderItem> items = orderItemDAO.findByField("order_id", String.valueOf(order.getId()));
                order.setItems(items);
            }
            
            logger.log(LogType.INFO, LogPriority.LOW, LogSource.BECKEND,
                    OrderManagementMessages.ORDER_UPDATED_SUCCESS.getMessage(this.getLanguage()));
            return gson.toJson(new Response(gson.toJson(orders), 200));
        } catch (Exception e) {
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND,
                    OrderManagementMessages.ORDER_UPDATED_FAILED.getMessage(this.getLanguage()));
            return gson.toJson(new Response(OrderManagementMessages.ORDER_UPDATED_FAILED.getMessage(this.getLanguage()), 500));
        }
    }

    // Aktualizovanie statusu objednávky
    public String updateOrderStatus(String json) {
        try {
            Type mapType = new TypeToken<Map<String, Object>>() {}.getType();
            Map<String, Object> requestData = gson.fromJson(json, mapType);
            
            int orderId = ((Number) requestData.get("orderId")).intValue();
            String newStatus = (String) requestData.get("status");

            Optional<Order> optionalOrder = orderDAO.getById(orderId);
            if (optionalOrder.isEmpty()) {
                logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND,
                        OrderManagementMessages.ORDER_NOT_FOUND.getMessage(this.getLanguage()));
                return gson.toJson(new Response(OrderManagementMessages.ORDER_NOT_FOUND.getMessage(this.getLanguage()), 404));
            }

            Order order = optionalOrder.get();
            order.setStatus(newStatus);
            orderDAO.update(order.getId(), order);

            logger.log(LogType.INFO, LogPriority.LOW, LogSource.BECKEND,
                    OrderManagementMessages.ORDER_UPDATED_SUCCESS.getMessage(this.getLanguage()));
            return gson.toJson(new Response(OrderManagementMessages.ORDER_UPDATED_SUCCESS.getMessage(this.getLanguage()), 200));
        } catch (Exception e) {
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND,
                    OrderManagementMessages.ORDER_UPDATED_FAILED.getMessage(this.getLanguage()));
            return gson.toJson(new Response(OrderManagementMessages.ORDER_UPDATED_FAILED.getMessage(this.getLanguage()), 500));
        }
    }

    public String markOrderAsDelivered(String json) {
        try {
            Type mapType = new TypeToken<Map<String, Object>>() {}.getType();
            Map<String, Object> requestData = gson.fromJson(json, mapType);
            
            int orderId = ((Number) requestData.get("orderId")).intValue();

            Optional<Order> optionalOrder = orderDAO.getById(orderId);
            if (optionalOrder.isEmpty()) {
                logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND,
                        OrderManagementMessages.ORDER_NOT_FOUND.getMessage(this.getLanguage()));
                return gson.toJson(new Response(OrderManagementMessages.ORDER_NOT_FOUND.getMessage(this.getLanguage()), 404));
            }

            Order order = optionalOrder.get();
            order.setStatus("delivered");
            orderDAO.update(order.getId(), order);

            logger.log(LogType.INFO, LogPriority.LOW, LogSource.BECKEND,
                    OrderManagementMessages.ORDER_UPDATED_SUCCESS.getMessage(this.getLanguage()));
            return gson.toJson(new Response(OrderManagementMessages.ORDER_UPDATED_SUCCESS.getMessage(this.getLanguage()), 200));
        } catch (Exception e) {
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND,
                    OrderManagementMessages.ORDER_UPDATED_FAILED.getMessage(this.getLanguage()));
            return gson.toJson(new Response(OrderManagementMessages.ORDER_UPDATED_FAILED.getMessage(this.getLanguage()), 500));
        }
    }

    public String cancelOrder(String json) {
        try {
            Type mapType = new TypeToken<Map<String, Object>>() {}.getType();
            Map<String, Object> requestData = gson.fromJson(json, mapType);
            
            int orderId = ((Number) requestData.get("orderId")).intValue();

            Optional<Order> optionalOrder = orderDAO.getById(orderId);
            if (optionalOrder.isEmpty()) {
                logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND,
                        OrderManagementMessages.ORDER_NOT_FOUND.getMessage(this.getLanguage()));
                return gson.toJson(new Response(OrderManagementMessages.ORDER_NOT_FOUND.getMessage(this.getLanguage()), 404));
            }

            Order order = optionalOrder.get();
            
            // Check if order can be cancelled (within 2 minutes of creation)
            Timestamp now = new Timestamp(System.currentTimeMillis());
            long timeDifference = now.getTime() - order.getCreatedAt().getTime();
            if (timeDifference > 120000) { // 2 minutes
                logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND,
                        OrderManagementMessages.ORDER_UPDATE_DEADLINE_EXPIRED.getMessage(this.getLanguage()));
                return gson.toJson(new Response(OrderManagementMessages.ORDER_UPDATE_DEADLINE_EXPIRED.getMessage(this.getLanguage()), 400));
            }

            // Delete order items
            List<OrderItem> items = orderItemDAO.findByField("order_id", String.valueOf(orderId));
            for (OrderItem item : items) {
                orderItemDAO.delete(item.getId());
            }

            // Delete the order
            orderDAO.delete(orderId);

            logger.log(LogType.INFO, LogPriority.LOW, LogSource.BECKEND,
                    OrderManagementMessages.ORDER_DELETED_SUCCESS.getMessage(this.getLanguage()));
            return gson.toJson(new Response(OrderManagementMessages.ORDER_DELETED_SUCCESS.getMessage(this.getLanguage()), 200));
        } catch (Exception e) {
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND,
                    OrderManagementMessages.ORDER_DELETED_FAILED.getMessage(this.getLanguage()));
            return gson.toJson(new Response(OrderManagementMessages.ORDER_DELETED_FAILED.getMessage(this.getLanguage()), 500));
        }
    }
}
