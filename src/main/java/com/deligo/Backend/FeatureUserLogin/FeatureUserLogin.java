package com.deligo.Backend.FeatureUserLogin;

import com.deligo.Backend.BaseFeature.BaseFeature;
import com.deligo.ConfigLoader.ConfigLoader;
import com.deligo.DatabaseManager.dao.GenericDAO;
import com.deligo.Logging.Adapter.LoggingAdapter;
import com.deligo.Model.BasicModels;
import com.deligo.Model.LoginData;
import com.deligo.Model.LoginResponse;
import com.deligo.Model.User;
import com.deligo.RestApi.RestAPIServer;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.List;

public class FeatureUserLogin extends BaseFeature {
    protected final GenericDAO<User> userDAO;
    private final Gson gson = new Gson();

    public FeatureUserLogin(ConfigLoader globalConfig, LoggingAdapter logger, RestAPIServer restApiServer, GenericDAO<User> userDAO) {
        super(globalConfig, logger, restApiServer);

        this.userDAO = userDAO;
        logger.log(BasicModels.LogType.INFO, BasicModels.LogPriority.MIDDLE, BasicModels.LogSource.BECKEND, UserLoginMessages.PROCESS_NAME.getMessage(this.getLanguage()));
    }

    public String loginEmployee(String jsonData){

        LoginData loginData;

        try {
            loginData = gson.fromJson(jsonData, LoginData.class);
        }
        catch (JsonSyntaxException e){
            String msg = UserLoginMessages.INVALID_JSON.getMessage(this.getLanguage(), e.getMessage());
            logger.log(BasicModels.LogType.ERROR, BasicModels.LogPriority.HIGH, BasicModels.LogSource.BECKEND, msg);
            return gson.toJson(new LoginResponse("", Collections.emptyList() , msg, 500));
        }

        String username = loginData.getUsername();
        String password = loginData.getPassword();


        Optional<User> userOpt = userDAO.findOneByField("username", username);
        if (userOpt.isEmpty()) {
            String msg = UserLoginMessages.USER_NOT_FOUND.getMessage(this.getLanguage(), username);
            logger.log(BasicModels.LogType.ERROR, BasicModels.LogPriority.MIDDLE, BasicModels.LogSource.BECKEND, msg);
            return gson.toJson(new LoginResponse("", Collections.emptyList() , msg, 500));
        }


        User user = userOpt.get();
        if (!BCrypt.checkpw(password, user.getPassword())) {
            String msg = UserLoginMessages.INVALID_CREDENTIALS.getMessage(this.getLanguage());
            logger.log(BasicModels.LogType.ERROR, BasicModels.LogPriority.MIDDLE, BasicModels.LogSource.BECKEND, msg);
            return gson.toJson(new LoginResponse("", Collections.emptyList() , msg, 500));
        }



        String msg = UserLoginMessages.SUCCESS.getMessage(this.getLanguage(), username);
        logger.log(BasicModels.LogType.SUCCESS, BasicModels.LogPriority.MIDDLE, BasicModels.LogSource.BECKEND, msg);

        List<String> role = Arrays.asList(user.getRole().split(","));

        return gson.toJson(new LoginResponse(user.getUsername(), role , msg, 200));

    }

    private String hashPassword(String rawPassword) {
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt());
    }

}
