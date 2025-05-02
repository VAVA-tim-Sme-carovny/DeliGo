package com.deligo.Backend.FeatureTableReservation;

import com.deligo.Backend.BaseFeature.BaseFeature;
import com.deligo.DatabaseManager.dao.GenericDAO;
import com.deligo.Logging.Adapter.LoggingAdapter;
import com.deligo.Model.BasicModels.LogPriority;
import com.deligo.Model.BasicModels.LogSource;
import com.deligo.Model.BasicModels.LogType;
import com.deligo.ConfigLoader.ConfigLoader;
import com.deligo.Model.Response;
import com.deligo.Model.TableReservation;
import com.deligo.Model.TableStructure;
import com.deligo.RestApi.RestAPIServer;
import com.google.gson.JsonSyntaxException;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.List;
import java.util.Optional;
import java.lang.reflect.Type;
import java.util.stream.Collectors;

public class FeatureTableReservation extends BaseFeature {
    private final GenericDAO<TableReservation> tableReservationDAO;
    private final GenericDAO<TableStructure> tableStructureDAO;
    private final Gson gson;

    public FeatureTableReservation(ConfigLoader globalConfig, LoggingAdapter logger, RestAPIServer restApiServer) {
        super(globalConfig, logger, restApiServer);
        this.gson = new Gson();
        this.tableReservationDAO = new GenericDAO<>(TableReservation.class, "reservations");
        this.tableStructureDAO = new GenericDAO<>(TableStructure.class, "tables");
        logger.log(LogType.INFO, LogPriority.MIDDLE, LogSource.BECKEND, 
                TableReservationMessages.PROCESS_NAME.getMessage(this.getLanguage()));
    }

    public String createReservation(String json) {
        try {
            Type mapType = new TypeToken<Map<String, Object>>() {}.getType();
            Map<String, Object> requestData = gson.fromJson(json, mapType);

            int user_Id = ((Number) requestData.get("userId")).intValue();
            int tableId = ((Number) requestData.get("tableId")).intValue();

            // Validate that the table exists
            Optional<TableStructure> tableOpt = tableStructureDAO.getById(tableId);
            if (tableOpt.isEmpty()) {
                logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND,
                        TableReservationMessages.TABLE_NOT_FOUND.getMessage(this.getLanguage()));
                return gson.toJson(new Response(TableReservationMessages.TABLE_NOT_FOUND.getMessage(this.getLanguage()), 400));
            }

            // Validate that the table is active
            TableStructure table = tableOpt.get();
            if (!table.isActive()) {
                logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND,
                        TableReservationMessages.TABLE_NOT_ACTIVE.getMessage(this.getLanguage()));
                return gson.toJson(new Response(TableReservationMessages.TABLE_NOT_ACTIVE.getMessage(this.getLanguage()), 400));
            }

            LocalDateTime reservedFrom = LocalDateTime.parse(requestData.get("reservedFrom").toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            LocalDateTime reservedTo = LocalDateTime.parse(requestData.get("reservedTo").toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            Timestamp reservedFromTimestamp = Timestamp.valueOf(reservedFrom);
            Timestamp reservedToTimestamp = Timestamp.valueOf(reservedTo);

            // Check if the table is available for the given time slot
            if (reservedFrom.isBefore(LocalDateTime.now())) {
                logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND,
                        TableReservationMessages.INVALID_RESERVATION_TIME.getMessage(this.getLanguage()));
                return gson.toJson(new Response(TableReservationMessages.INVALID_RESERVATION_TIME.getMessage(this.getLanguage()), 400));
            } else if (!isTableAvailable(tableId, reservedFrom, reservedTo)) {
                logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND,
                        TableReservationMessages.RESERVATION_ALREADY_EXISTS.getMessage(this.getLanguage()));
                return gson.toJson(new Response(TableReservationMessages.RESERVATION_ALREADY_EXISTS.getMessage(this.getLanguage()), 400));
            }

            // Create the reservation
            TableReservation reservation = new TableReservation(user_Id, tableId, reservedFromTimestamp, reservedToTimestamp);
            tableReservationDAO.insert(reservation);

            logger.log(LogType.INFO, LogPriority.LOW, LogSource.BECKEND,
                    TableReservationMessages.RESERVATION_CREATION_SUCCESS.getMessage(this.getLanguage()));
            return gson.toJson(new Response(TableReservationMessages.RESERVATION_CREATION_SUCCESS.getMessage(this.getLanguage()), 200));
        } catch (JsonSyntaxException e) {
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND,
                    TableReservationMessages.INVALID_REQUEST_FORMAT.getMessage(this.getLanguage()));
            return gson.toJson(new Response(TableReservationMessages.INVALID_REQUEST_FORMAT.getMessage(this.getLanguage()), 400));
        } catch (Exception e) {
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND,
                    TableReservationMessages.RESERVATION_CREATION_FAILED.getMessage(this.getLanguage()));
            return gson.toJson(new Response(TableReservationMessages.RESERVATION_CREATION_FAILED.getMessage(this.getLanguage()), 500));
        }
    }

    public String getReservationById(String json) {
        try {
            Type mapType = new TypeToken<Map<String, Object>>() {}.getType();
            Map<String, Object> requestData = gson.fromJson(json, mapType);
            int reservationId = ((Number) requestData.get("reservationId")).intValue();

            Optional<TableReservation> optionalReservation = tableReservationDAO.getById(reservationId);
            if (optionalReservation.isEmpty()) {
                logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND,
                        TableReservationMessages.RESERVATION_NOT_FOUND.getMessage(this.getLanguage()));
                return gson.toJson(new Response(TableReservationMessages.RESERVATION_NOT_FOUND.getMessage(this.getLanguage()), 404));
            }

            return gson.toJson(optionalReservation.get());
        } catch (Exception e) {
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND,
                    TableReservationMessages.RESERVATION_UPDATE_FAILED.getMessage(this.getLanguage()));
            return gson.toJson(new Response(TableReservationMessages.RESERVATION_UPDATE_FAILED.getMessage(this.getLanguage()), 500));
        }
    }

    public String getReservationsByUser(String json) {
        try {
            Type mapType = new TypeToken<Map<String, Object>>() {}.getType();
            Map<String, Object> requestData = gson.fromJson(json, mapType);
            int userId = ((Number) requestData.get("user_Id")).intValue();

            List<TableReservation> reservations = tableReservationDAO.findByField("user_id", userId);
            return gson.toJson(reservations);
        } catch (Exception e) {
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND,
                    TableReservationMessages.RESERVATION_UPDATE_FAILED.getMessage(this.getLanguage()));
            return gson.toJson(new Response(TableReservationMessages.RESERVATION_UPDATE_FAILED.getMessage(this.getLanguage()), 500));
        }
    }

    public String getReservationsByTable(String json) {
        try {
            Type mapType = new TypeToken<Map<String, Object>>() {}.getType();
            Map<String, Object> requestData = gson.fromJson(json, mapType);
            int tableId = ((Number) requestData.get("tableId")).intValue();

            List<TableReservation> reservations = tableReservationDAO.findByField("table_id", String.valueOf(tableId));
            
            logger.log(LogType.INFO, LogPriority.LOW, LogSource.BECKEND,
                    TableReservationMessages.RESERVATION_UPDATE_SUCCESS.getMessage(this.getLanguage()));
            return gson.toJson(new Response(gson.toJson(reservations), 200));
        } catch (Exception e) {
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND,
                    TableReservationMessages.RESERVATION_UPDATE_FAILED.getMessage(this.getLanguage()));
            return gson.toJson(new Response(TableReservationMessages.RESERVATION_UPDATE_FAILED.getMessage(this.getLanguage()), 500));
        }
    }

    public String cancelReservation(String json) {
        try {
            Type mapType = new TypeToken<Map<String, Object>>() {}.getType();
            Map<String, Object> requestData = gson.fromJson(json, mapType);
            
            int reservationId = ((Number) requestData.get("reservationId")).intValue();

            Optional<TableReservation> optionalReservation = tableReservationDAO.getById(reservationId);
            if (optionalReservation.isEmpty()) {
                logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND,
                        TableReservationMessages.RESERVATION_NOT_FOUND.getMessage(this.getLanguage()));
                return gson.toJson(new Response(TableReservationMessages.RESERVATION_NOT_FOUND.getMessage(this.getLanguage()), 404));
            }

            TableReservation reservation = optionalReservation.get();
            
            LocalDateTime now = LocalDateTime.now();
            long timeDifference = java.time.Duration.between(reservation.getCreatedAt(), now).toMillis();
            if (timeDifference > 120000) { // 2 minutes
                logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND,
                        TableReservationMessages.INVALID_RESERVATION_TIME.getMessage(this.getLanguage()));
                return gson.toJson(new Response(TableReservationMessages.INVALID_RESERVATION_TIME.getMessage(this.getLanguage()), 400));
            }

            tableReservationDAO.delete(reservationId);

            logger.log(LogType.INFO, LogPriority.LOW, LogSource.BECKEND,
                    TableReservationMessages.RESERVATION_CANCELLED_SUCCESS.getMessage(this.getLanguage()));
            return gson.toJson(new Response(TableReservationMessages.RESERVATION_CANCELLED_SUCCESS.getMessage(this.getLanguage()), 200));
        } catch (Exception e) {
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND,
                    TableReservationMessages.RESERVATION_CANCEL_ERROR.getMessage(this.getLanguage()));
            return gson.toJson(new Response(TableReservationMessages.RESERVATION_CANCEL_ERROR.getMessage(this.getLanguage()), 500));
        }
    }

    public String getAvailableTables(String json) {
        try {
            Type mapType = new TypeToken<Map<String, Object>>() {}.getType();
            Map<String, Object> requestData = gson.fromJson(json, mapType);
            
            LocalDateTime from = LocalDateTime.parse(requestData.get("from").toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            LocalDateTime to = LocalDateTime.parse(requestData.get("to").toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            // Get all tables
            List<TableStructure> allTables = tableStructureDAO.getAll();
            
            // Filter available tables
            List<TableStructure> availableTables = allTables.stream()
                .filter(table -> table.isActive() && isTableAvailable(table.getId(), from, to))
                .collect(Collectors.toList());

            logger.log(LogType.INFO, LogPriority.LOW, LogSource.BECKEND,
                    "Retrieved available tables successfully");
            return gson.toJson(new Response(gson.toJson(availableTables), 200));
        } catch (Exception e) {
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND,
                    "Failed to get available tables");
            return gson.toJson(new Response("Failed to get available tables", 500));
        }
    }

    private boolean isTableAvailable(int tableId, LocalDateTime from, LocalDateTime to) {
        try {
            if (from.isBefore(LocalDateTime.now())) {
                return false;
            }

            List<TableReservation> existingReservations = tableReservationDAO.findByField("table_id", tableId);

            for (TableReservation reservation : existingReservations) {
                if (from.isAfter(reservation.getReservedTo()) ||
                    to.isBefore(reservation.getReservedFrom()) ||
                    to.isAfter(reservation.getReservedFrom()) ||
                    from.isBefore(reservation.getReservedTo())) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND,
                    "Error checking table availability");
            return false;
        }
    }

}
