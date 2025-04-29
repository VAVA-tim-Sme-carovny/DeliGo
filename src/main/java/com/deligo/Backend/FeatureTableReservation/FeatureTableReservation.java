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
import com.deligo.RestApi.RestAPIServer;
import com.google.gson.JsonSyntaxException;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.List;
import java.util.Optional;
import java.lang.reflect.Type;
import java.util.stream.Collectors;
import com.deligo.Model.TableStructure;

public class FeatureTableReservation extends BaseFeature {
    private final GenericDAO<TableReservation> tableReservationDAO;
    private final Gson gson;

    public FeatureTableReservation(ConfigLoader globalConfig, LoggingAdapter logger, RestAPIServer restApiServer) {
        super(globalConfig, logger, restApiServer);
        this.gson = new Gson();
        this.tableReservationDAO = new GenericDAO<>(TableReservation.class, "reservations");
        logger.log(LogType.INFO, LogPriority.MIDDLE, LogSource.BECKEND, 
                TableReservationMessages.PROCESS_NAME.getMessage(this.getLanguage()));
    }

    public String createReservation(String json) {
        try {
            Type mapType = new TypeToken<Map<String, Object>>() {}.getType();
            Map<String, Object> requestData = gson.fromJson(json, mapType);

            int userId = ((Number) requestData.get("userId")).intValue();
            int tableId = ((Number) requestData.get("tableId")).intValue();
            LocalDateTime reservedFrom = LocalDateTime.parse(requestData.get("reservedFrom").toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            LocalDateTime reservedTo = LocalDateTime.parse(requestData.get("reservedTo").toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            if (reservedFrom.isBefore(LocalDateTime.now())) {
                logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND,
                        TableReservationMessages.INVALID_RESERVATION_TIME.getMessage(this.getLanguage()));
                return gson.toJson(new Response(TableReservationMessages.INVALID_RESERVATION_TIME.getMessage(this.getLanguage()), 400));
            }

            if (!isTableAvailable(tableId, reservedFrom, reservedTo)) {
                logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND,
                        TableReservationMessages.RESERVATION_ALREADY_EXISTS.getMessage(this.getLanguage()));
                return gson.toJson(new Response(TableReservationMessages.RESERVATION_ALREADY_EXISTS.getMessage(this.getLanguage()), 400));
            }

            TableReservation reservation = new TableReservation(userId, tableId, reservedFrom, reservedTo);
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

    public String updateReservationStatus(String json) {
        try {
            Type mapType = new TypeToken<Map<String, Object>>() {}.getType();
            Map<String, Object> requestData = gson.fromJson(json, mapType);

            int reservationId = ((Number) requestData.get("reservationId")).intValue();
            String status = (String) requestData.get("status");

            Optional<TableReservation> optionalReservation = tableReservationDAO.getById(reservationId);
            if (optionalReservation.isEmpty()) {
                logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND,
                        TableReservationMessages.RESERVATION_NOT_FOUND.getMessage(this.getLanguage()));
                return gson.toJson(new Response(TableReservationMessages.RESERVATION_NOT_FOUND.getMessage(this.getLanguage()), 404));
            }

            TableReservation reservation = optionalReservation.get();
            reservation.setStatus(status);
            tableReservationDAO.update(reservationId, reservation);

            String successMessage = status.equals("CONFIRMED") ? 
                TableReservationMessages.RESERVATION_UPDATE_SUCCESS.getMessage(this.getLanguage()) :
                TableReservationMessages.RESERVATION_CANCELLED_SUCCESS.getMessage(this.getLanguage());

            logger.log(LogType.INFO, LogPriority.LOW, LogSource.BECKEND, successMessage);
            return gson.toJson(new Response(successMessage, 200));
        } catch (Exception e) {
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND,
                    TableReservationMessages.RESERVATION_UPDATE_FAILED.getMessage(this.getLanguage()));
            return gson.toJson(new Response(TableReservationMessages.RESERVATION_UPDATE_FAILED.getMessage(this.getLanguage()), 500));
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
            int userId = ((Number) requestData.get("userId")).intValue();

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

    private boolean isTableAvailable(int tableId, LocalDateTime from, LocalDateTime to) {
        try {
            List<TableReservation> existingReservations = tableReservationDAO.findByField("table_id", tableId);
            
            for (TableReservation reservation : existingReservations) {
                if (reservation.getStatus().equals("CANCELLED")) {
                    continue;
                }
                
                if ((from.isAfter(reservation.getReservedFrom()) && from.isBefore(reservation.getReservedTo())) ||
                    (to.isAfter(reservation.getReservedFrom()) && to.isBefore(reservation.getReservedTo())) ||
                    (from.isBefore(reservation.getReservedFrom()) && to.isAfter(reservation.getReservedTo()))) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND,
                    TableReservationMessages.RESERVATION_UPDATE_FAILED.getMessage(this.getLanguage()));
            return false;
        }
    }

    public String getAvailableTables(String json) {
        try {
            Type mapType = new TypeToken<Map<String, Object>>() {}.getType();
            Map<String, Object> requestData = gson.fromJson(json, mapType);

            LocalDateTime from = LocalDateTime.parse(requestData.get("from").toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            LocalDateTime to = LocalDateTime.parse(requestData.get("to").toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            GenericDAO<TableStructure> tableDAO = new GenericDAO<>(TableStructure.class, "tables");
            List<TableStructure> allTables = tableDAO.getAll();

            List<TableStructure> availableTables = allTables.stream()
                .filter(table -> table.isActive() && isTableAvailable(table.getId(), from, to))
                .collect(Collectors.toList());

            logger.log(LogType.INFO, LogPriority.LOW, LogSource.BECKEND,
                    TableReservationMessages.RESERVATION_UPDATE_SUCCESS.getMessage(this.getLanguage()));
            return gson.toJson(new Response(gson.toJson(availableTables), 200));

        } catch (Exception e) {
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND,
                    TableReservationMessages.RESERVATION_UPDATE_FAILED.getMessage(this.getLanguage()));
            return gson.toJson(new Response(TableReservationMessages.RESERVATION_UPDATE_FAILED.getMessage(this.getLanguage()), 500));
        }
    }
}
