package com.deligo.Backend.FeatureUserRegister;

import com.deligo.Backend.BaseFeature.BaseFeature;
import com.deligo.Backend.FeatureOrganizationDetails.OrgDetailsMessages;
import com.deligo.ConfigLoader.ConfigLoader;
import com.deligo.Logging.Adapter.LoggingAdapter;
import com.deligo.Model.BasicModels.LogPriority;
import com.deligo.Model.BasicModels.LogSource;
import com.deligo.Model.BasicModels.LogType;
import com.deligo.Model.OrgDetails;
import com.deligo.Model.Response;
import com.deligo.RestApi.RestAPIServer;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.List;
import java.util.regex.Pattern;

public class FeatureUserRegister extends BaseFeature {

    public FeatureUserRegister(ConfigLoader globalConfig, LoggingAdapter logger, RestAPIServer restApiServer) {
        super(globalConfig, logger, restApiServer);
        logger.log(LogType.INFO, LogPriority.MIDDLE, LogSource.BECKEND, MyNewProcessMessages.PROCESS_NAME.getMessage(this.getLanguage()));
    }

    //Add here your new functions
    public String createAccount(String jsonData){
        logger.log(LogType.INFO, LogPriority.MIDDLE, LogSource.BECKEND, "Processing account creation request.");

    }
}
