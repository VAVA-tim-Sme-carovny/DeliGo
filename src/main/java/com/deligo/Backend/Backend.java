package com.deligo.Backend;

import com.deligo.Backend.FeatureEditTables.FeatureEditTables;
import com.deligo.Backend.FeatureMenuManagement.FeatureMenuManagement;
import com.deligo.Backend.FeatureOrganizationDetails.FeatureOrgDetails;
import com.deligo.Backend.FeatureStatistics.FeatureStatistics;
import com.deligo.Backend.FeatureUserLogin.FeatureUserLogin;
import com.deligo.Backend.FeatureCreateOrder.FeatureCreateOrder;
import com.deligo.Backend.FeatureUserManagement.FeatureUserManagement;
import com.deligo.Backend.FeatureUserRegistration.FeatureUserRegister;
import com.deligo.ConfigLoader.ConfigLoader;
import com.deligo.DatabaseManager.dao.GenericDAO;
import com.deligo.Logging.Adapter.LoggingAdapter;
import com.deligo.Model.BasicModels.*;
import com.deligo.RestApi.RestAPIServer;
import com.deligo.Backend.FeatureValidateTestConnection.FeatureValidateTestConnection;
import com.deligo.Model.User;

/**
 * Class for backend Features
 */
public class Backend {

    private final ConfigLoader config;

    private final FeatureValidateTestConnection featureValidateTestConnection;
    private final FeatureOrgDetails featureOrgDetails;
//    Add feature.
    private final FeatureUserRegister featureUserRegister;
    private final FeatureUserLogin featureUserLogin;

    //Admin features
    private final FeatureStatistics featureStatistics;
    private final FeatureEditTables featureTableStructure;
    private final FeatureMenuManagement featureMenuManagement;
    private final FeatureUserManagement featureUserManagement;

    //Order
    private final FeatureCreateOrder featureCreateOrder;

    /**
     * Creates Backend Instance for application
     *
     * @param apiServer Rest api server
     * @param logger Logger manager
     * @param config Config manager that returns and sets data in config file
     */
    public Backend(RestAPIServer apiServer, LoggingAdapter logger, ConfigLoader config) {
        this.config = config;

        apiServer.setBackendConfig(new BackendConfig(this));

        this.featureValidateTestConnection = new FeatureValidateTestConnection(config, logger, apiServer);
        this.featureOrgDetails = new FeatureOrgDetails(config, logger, apiServer);

        this.featureStatistics = new FeatureStatistics(config, logger, apiServer);
        this.featureTableStructure = new FeatureEditTables(config, logger, apiServer);
        this.featureMenuManagement = new FeatureMenuManagement(config, logger, apiServer);
        this.featureUserManagement = new FeatureUserManagement(config, logger, apiServer);

//      Add feature.
        this.featureUserRegister = new FeatureUserRegister(config, logger, apiServer, new GenericDAO<>(User.class, "users"));
        this.featureUserLogin = new FeatureUserLogin(config, logger, apiServer);
        this.featureCreateOrder = new FeatureCreateOrder(config, logger, apiServer);

        logger.log(LogType.SUCCESS, LogPriority.HIGH, LogSource.BECKEND, "Backend initialized correctly.");

    }

    public FeatureValidateTestConnection getFeatureValidateTestConnection() {
        return featureValidateTestConnection;
    }

    public FeatureOrgDetails getFeatureOrgDetails() {
        return featureOrgDetails;
    }


    //Admin Features
    public FeatureStatistics getFeatureStatistics() {
        return featureStatistics;
    }

    public FeatureEditTables getFeatureTableStructure() {
        return featureTableStructure;
    }
    public FeatureUserRegister getFeatureUserRegister() {
        return featureUserRegister;
    }

    public FeatureMenuManagement getFeatureMenuManagement() {
        return featureMenuManagement;
    }

    public FeatureUserManagement getFeatureUserManagement() {
        return featureUserManagement;
    }

    public FeatureUserLogin getFeatureUserLogin() {
        return featureUserLogin;
    }
    public FeatureCreateOrder getFeatureCreateOrder() {
        return featureCreateOrder;

    public String getAvailableTables(String json) {
        return featureTableReservation.getAvailableTables(json);
    }

    public String createReservation(String json) {
        return featureTableReservation.createReservation(json);
    }

    public String updateReservationStatus(String json) {
        return featureTableReservation.updateReservationStatus(json);
    }

    public String getReservationById(String json) {
        return featureTableReservation.getReservationById(json);
    }

    public String getReservationsByUser(String json) {
        return featureTableReservation.getReservationsByUser(json);
    }

    public String getReservationsByTable(String json) {
        return featureTableReservation.getReservationsByTable(json);
    }

    public String cancelReservation(String json) {
        return featureTableReservation.cancelReservation(json);
    }

    // FeatureOrderManagement methods
    public String updateOrder(String json) {
        return featureOrderManagement.updateOrder(json);
    }

    public String updateOrderStatus(String json) {
        return featureOrderManagement.updateOrderStatus(json);
    }

    public String getOrdersByTable(String json) {
        return featureOrderManagement.getOrdersByTable(json);
    }

    public String getMenuByCategory(String json) {
        return featureOrderManagement.getMenuByCategory(json);
    }

    public String getCategories(String json) {
        return featureOrderManagement.getCategories(json);
    }

    public String getPendingOrders(String json) {
        return featureOrderManagement.getPendingOrders(json);
    }

    public String markOrderAsDelivered(String json) {
        return featureOrderManagement.markOrderAsDelivered(json);
    }

    public String cancelOrder(String json) {
        return featureOrderManagement.cancelOrder(json);
    }

    // FeatureMenuManagement methods
    public String addMenuItem(String json) {
        return featureMenuManagement.addItem(json);
    }

    public String updateMenuItem(String json) {
        return featureMenuManagement.updateItem(json);
    }

    public String deleteMenuItem(String json) {
        return featureMenuManagement.deleteItem(json);
    }


    public String addMenuCategory(String json) {
        return featureMenuManagement.addCategory(json);
    }


    public String deleteMenuCategory(String json) {
        return featureMenuManagement.deleteCategory(json);
    }

    public String getAllMenuCategories(String json) {
        return featureMenuManagement.getAllCategories(json);
    }

    // FeatureUserManagement methods
    public String editUser(String json) {
        return featureUserManagement.editUser(json);
    }

    public String deleteUser(String json) {
        return featureUserManagement.deleteUser(json);
    }

    public String getAllUsers(String json) {
        return featureUserManagement.getAllUsers(json);
    }

    public String getOrgDetails(String json) {
        return featureUserManagement.getOrgDetails(json);
    }
}