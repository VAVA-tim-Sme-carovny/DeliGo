package com.deligo.Backend;

import com.deligo.Logging.Adapter.LoggingAdapter;
import com.deligo.Model.BasicModels.*;
import com.deligo.RestApi.RestAPIServer;
import com.deligo.Backend.FeatureValidateTestConnection.FeatureValidateTestConnection;

public class Backend {
    private final FeatureValidateTestConnection featureValidateTestConnection;

    public Backend(RestAPIServer apiServer, LoggingAdapter logger) {
        apiServer.setBackendConfig(new BackendConfig(this));

        /*Add here your feature*/

        this.featureValidateTestConnection = new FeatureValidateTestConnection(logger, apiServer);
        logger.log(LogType.SUCCESS, LogPriority.HIGH, LogSource.BECKEND, "Backend initialized correctly.");

    }

    public FeatureValidateTestConnection getFeatureValidateTestConnection() {
        return featureValidateTestConnection;
    }
}