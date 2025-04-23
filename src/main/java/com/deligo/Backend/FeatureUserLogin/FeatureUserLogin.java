package com.deligo.Backend.FeatureUserLogin;

import com.deligo.Backend.BaseFeature.BaseFeature;
import com.deligo.ConfigLoader.ConfigLoader;
import com.deligo.DatabaseManager.dao.GenericDAO;
import com.deligo.Logging.Adapter.LoggingAdapter;
import com.deligo.Model.*;
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
    protected final GenericDAO<Order> orderDAO;
    private final Gson gson = new Gson();

    public FeatureUserLogin(ConfigLoader globalConfig, LoggingAdapter logger, RestAPIServer restApiServer) {
        super(globalConfig, logger, restApiServer);

        this.userDAO =  new GenericDAO<>(User.class, "users");
        this.orderDAO = new GenericDAO<>(Order.class, "orders");
        logger.log(BasicModels.LogType.INFO, BasicModels.LogPriority.MIDDLE, BasicModels.LogSource.BECKEND, UserLoginMessages.PROCESS_NAME.getMessage(this.getLanguage()));
    }

    /**
     * Spracováva prihlásenie Zariadenie. Aby bolo schopné prijať objednávku
     *
     * @return JSON odpoveď vo forme DeviceLoginResponse (deviceID, správu a status)
     */
    public String loginCustomer(){
        String deviceID = globalConfig.getConfigValue("device", "id", String.class);
        List<Order> orderOpt = orderDAO.findByField("device_id", deviceID);

        for (Order order : orderOpt) {
            String status = order.getStatus();
            if (!status.equals(BasicModels.OrderState.DONE.getValue())) {
                String msg = UserLoginMessages.ACTIVE_ORDER.getMessage(this.getLanguage());
                logger.log(BasicModels.LogType.ERROR, BasicModels.LogPriority.HIGH, BasicModels.LogSource.BECKEND, msg);
                return gson.toJson(new DeviceLoginResponse(deviceID, msg, 500));
            }
        }

        String msg = UserLoginMessages.WELCOME_MESSAGE.getMessage(this.getLanguage());
        logger.log(BasicModels.LogType.SUCCESS, BasicModels.LogPriority.MIDDLE, BasicModels.LogSource.BECKEND, msg);
        return gson.toJson(new DeviceLoginResponse(deviceID, msg, 200));
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

        //validácia json súboru
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

        //kontrola či používateľ existuje
        Optional<User> userOpt = userDAO.findOneByField("username", username);
        if (userOpt.isEmpty()) {
            String msg = UserLoginMessages.USER_NOT_FOUND.getMessage(this.getLanguage(), username);
            logger.log(BasicModels.LogType.ERROR, BasicModels.LogPriority.MIDDLE, BasicModels.LogSource.BECKEND, msg);
            return gson.toJson(new LoginResponse("", Collections.emptyList() , msg, 500));
        }

        //validácia hesla
        User user = userOpt.get();
        if (!BCrypt.checkpw(password, user.getPassword())) {
            String msg = UserLoginMessages.INVALID_CREDENTIALS.getMessage(this.getLanguage());
            logger.log(BasicModels.LogType.ERROR, BasicModels.LogPriority.MIDDLE, BasicModels.LogSource.BECKEND, msg);
            return gson.toJson(new LoginResponse("", Collections.emptyList() , msg, 500));
        }

        //overenie, či používateľ nie je prihlásený
        //TODO: Niekto by akože mohol configloader poriešiť aby som nemusel načítavať boolean ako string a porovnávať
        // ho so stringom "true", ĎAKUJEM
        // java.lang.RuntimeException: Config value for key status is not of type boolean
        if (globalConfig.getConfigValue("login", "status", String.class).equals("true")) {
            String msg = UserLoginMessages.ALREADY_LOGED_IN.getMessage(this.getLanguage());
            logger.log(BasicModels.LogType.ERROR, BasicModels.LogPriority.MIDDLE, BasicModels.LogSource.BECKEND, msg);
            return gson.toJson(new LoginResponse("", Collections.emptyList() , msg, 500));
        }


        List<String> role = Arrays.asList(user.getRole().split(","));
        globalConfig.updateConfigValue("login", "user", username);
        globalConfig.updateConfigValue("login", "status", "true");
        globalConfig.updateConfigValue("login", "role", role);

        String msg = UserLoginMessages.SUCCESS.getMessage(this.getLanguage(), username);
        logger.log(BasicModels.LogType.SUCCESS, BasicModels.LogPriority.MIDDLE, BasicModels.LogSource.BECKEND, msg);
        return gson.toJson(new LoginResponse(user.getUsername(), role , msg, 200));
    }

    /**
     * Odhlásenie používateľa ak je prihlásený
     *
     * @return JSON odpoveď vo forme Response (správu a status)
     */
    public String logout(){
        String userStatus = globalConfig.getConfigValue("login", "status", String.class);

        //overenie či je používateľ prihlásený
        if (userStatus.equals("false")) {
            String msg = UserLoginMessages.NOT_LOGGED_IN.getMessage(this.getLanguage());
            logger.log(BasicModels.LogType.ERROR, BasicModels.LogPriority.MIDDLE, BasicModels.LogSource.BECKEND, msg);
            return gson.toJson(new Response(msg, 500));
        }

        globalConfig.updateConfigValue("login", "status", "false");
        String msg = UserLoginMessages.LOGOUT_MESSAGE.getMessage(this.getLanguage());
        logger.log(BasicModels.LogType.SUCCESS, BasicModels.LogPriority.MIDDLE, BasicModels.LogSource.BECKEND, msg);
        return gson.toJson(new Response(msg, 200));
    }


}
