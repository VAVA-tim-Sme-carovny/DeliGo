package com.deligo.Backend;

import com.deligo.Backend.FeatureOrganizationDetails.FeatureOrgDetails;
import com.deligo.Backend.FeatureUserRegister.FeatureUserRegister;
import com.deligo.ConfigLoader.ConfigLoader;
import com.deligo.DatabaseManager.dao.GenericDAO;
import com.deligo.Logging.Adapter.LoggingAdapter;
import com.deligo.Model.BasicModels.*;
import com.deligo.Model.User;
import com.deligo.RestApi.RestAPIServer;
import com.deligo.Backend.FeatureValidateTestConnection.FeatureValidateTestConnection;

/**
 * Class for backend Features
 */
public class Backend {

    private final ConfigLoader config;

    private final FeatureValidateTestConnection featureValidateTestConnection;
    private final FeatureOrgDetails featureOrgDetails;
    private final FeatureUserRegister featureUserRegister;
//    Add feature.
//    private final FeatureMyNewProcess featureMyNewProcess;



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

        //FeatureUserRegistration
        this.featureUserRegister = new FeatureUserRegister(config, logger, apiServer, new GenericDAO<>(User.class, "users"));

        logger.log(LogType.SUCCESS, LogPriority.HIGH, LogSource.BECKEND, "Backend initialized correctly.");

    }

    public FeatureValidateTestConnection getFeatureValidateTestConnection() {
        return featureValidateTestConnection;
    }

    public FeatureOrgDetails getFeatureOrgDetails() {
        return featureOrgDetails;
    }

    public FeatureUserRegister getFeatureUserRegister() {
        return featureUserRegister;
    }


}