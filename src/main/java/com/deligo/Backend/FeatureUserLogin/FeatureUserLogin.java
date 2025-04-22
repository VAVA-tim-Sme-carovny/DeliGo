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

    /**
     * Spracováva prihlásenie používateľa na základe JSON údajov.
     *
     * 1. Pokúsi sa deserializovať JSON vstup do objektu LoginData.
     * 2. Overí, či používateľ so zadaným menom existuje v databáze.
     * 3. Overí správnosť hesla pomocou BCrypt.
     * 4. Skontroluje, či už nie je používateľ označený ako prihlásený.
     * 5. Ak je všetko v poriadku, vráti úspešnú odpoveď so stavom 200 a rolami používateľa.
     *
     * V prípade chyby (neplatný JSON, neexistujúci používateľ, nesprávne heslo, alebo opätovné prihlásenie)
     * sa vráti chybová správa a príslušný HTTP stavový kód (napr. 500).
     *
     * @param jsonData JSON reťazec obsahujúci prihlasovacie údaje (meno a heslo)
     * @return JSON odpoveď vo forme LoginResponse (obsahuje username, role, správu a status)
     */
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

        if (globalConfig.getConfigValue("login", "status", boolean.class)) {
            String msg = UserLoginMessages.ALREADY_LOGED_IN.getMessage(this.getLanguage());
            logger.log(BasicModels.LogType.ERROR, BasicModels.LogPriority.MIDDLE, BasicModels.LogSource.BECKEND, msg);
            return gson.toJson(new LoginResponse("", Collections.emptyList() , msg, 500));
        }



        String msg = UserLoginMessages.SUCCESS.getMessage(this.getLanguage(), username);
        logger.log(BasicModels.LogType.SUCCESS, BasicModels.LogPriority.MIDDLE, BasicModels.LogSource.BECKEND, msg);

        List<String> role = Arrays.asList(user.getRole().split(","));
        globalConfig.updateConfigValue("login", "status", true);

        return gson.toJson(new LoginResponse(user.getUsername(), role , msg, 200));
    }


}
