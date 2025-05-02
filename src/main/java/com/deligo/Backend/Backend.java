package com.deligo.Backend;

import com.deligo.Backend.FeatureCreateOrder.FeatureCreateOrder;
import com.deligo.Backend.FeatureMenuManagement.FeatureMenuManagement;
import com.deligo.Backend.FeatureOrganizationDetails.FeatureOrgDetails;
import com.deligo.Backend.FeatureReview.FeatureReview;
import com.deligo.Backend.FeatureTableReservation.FeatureTableReservation;
import com.deligo.Backend.FeatureUserLogin.FeatureUserLogin;
import com.deligo.Backend.FeatureUserManagement.FeatureUserManagement;
import com.deligo.Backend.FeatureUserRegistration.FeatureUserRegister;
import com.deligo.Backend.FeatureValidateTestConnection.FeatureValidateTestConnection;
import com.deligo.ConfigLoader.ConfigLoader;
import com.deligo.DatabaseManager.dao.GenericDAO;
import com.deligo.Logging.Adapter.LoggingAdapter;
import com.deligo.Model.BasicModels.*;
import com.deligo.RestApi.RestAPIServer;
import com.deligo.Model.User;

/**
 * Class for backend Features
 */
public class Backend {

    public final ConfigLoader config;

    private final FeatureValidateTestConnection featureValidateTestConnection;

    // User features
    private final FeatureUserRegister featureUserRegister;
    private final FeatureUserLogin featureUserLogin;
    private final FeatureReview featureReview;
    private final FeatureOrgDetails featureOrgDetails;

    // Admin features
    private final FeatureCreateOrder featureCreateOrder;
    private final FeatureMenuManagement featureMenuManagement;
    private final FeatureUserManagement featureUserManagement;
    private final FeatureTableReservation featureTableReservation;

    /**
     * Creates Backend Instance for application
     *
     * @param apiServer Rest api server
     * @param logger Logger manager
     * @param config Config manager that returns and sets data in config file
     */
    public Backend(RestAPIServer apiServer, LoggingAdapter logger, ConfigLoader config) {
        this.config = config;

        RestAPIServer.setBackendConfig(new BackendConfig(this));

        this.featureValidateTestConnection = new FeatureValidateTestConnection(config, logger, apiServer);
        this.featureReview = new FeatureReview(config, logger, apiServer);
        this.featureOrgDetails = new FeatureOrgDetails(config, logger, apiServer);
        this.featureCreateOrder = new FeatureCreateOrder(config, logger, apiServer);
        this.featureMenuManagement = new FeatureMenuManagement(config, logger, apiServer);
        this.featureUserManagement = new FeatureUserManagement(config, logger, apiServer);
        this.featureTableReservation = new FeatureTableReservation(config, logger, apiServer);

        // Add feature.
        this.featureUserRegister = new FeatureUserRegister(config, logger, apiServer, new GenericDAO<>(User.class, "users"));
        this.featureUserLogin = new FeatureUserLogin(config, logger, apiServer);

        logger.log(LogType.SUCCESS, LogPriority.HIGH, LogSource.BECKEND, "Backend initialized correctly.");
    }

    public FeatureValidateTestConnection getFeatureValidateTestConnection() {
        return featureValidateTestConnection;
    }

    public FeatureReview getFeatureReview() {
        return featureReview;
    }

    public FeatureOrgDetails getFeatureOrgDetails() {
        return featureOrgDetails;
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

    public FeatureTableReservation getFeatureTableReservation() {
        return featureTableReservation;
    }

    public FeatureUserLogin getFeatureUserLogin() {
        return featureUserLogin;
    }

    public FeatureCreateOrder getFeatureCreateOrder() {
        return featureCreateOrder;
    }

    // Table Reservation methods
    public String getAvailableTables(String json) {
        return featureTableReservation.getAvailableTables(json);
    }

    public String createReservation(String json) {
        return featureTableReservation.createReservation(json);
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

    public String getAllItems(String json) {
        return featureMenuManagement.getAllItems(json);
    }
}