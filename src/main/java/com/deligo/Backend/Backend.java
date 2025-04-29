package com.deligo.Backend;

import com.deligo.Backend.FeatureMenuManagement.FeatureMenuManagement;
import com.deligo.Backend.FeatureOrganizationDetails.FeatureOrgDetails;
import com.deligo.Backend.FeatureStatistics.FeatureStatistics;
import com.deligo.Backend.FeatureTableStructure.FeatureTableStructure;
import com.deligo.Backend.FeatureUserLogin.FeatureUserLogin;
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
    private final FeatureTableStructure featureTableStructure;
    private final FeatureMenuManagement featureMenuManagement;
    private final FeatureUserManagement featureUserManagement;


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
        this.featureTableStructure = new FeatureTableStructure(config, logger, apiServer);
        this.featureMenuManagement = new FeatureMenuManagement(config, logger, apiServer);
        this.featureUserManagement = new FeatureUserManagement(config, logger, apiServer);

//      Add feature.
        this.featureUserRegister = new FeatureUserRegister(config, logger, apiServer, new GenericDAO<>(User.class, "users"));
        this.featureUserLogin = new FeatureUserLogin(config, logger, apiServer);

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

    public FeatureTableStructure getFeatureTableStructure() {
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
}