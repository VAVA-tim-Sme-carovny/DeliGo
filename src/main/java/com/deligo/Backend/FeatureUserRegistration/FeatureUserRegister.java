package com.deligo.Backend.FeatureUserRegistration;

import com.deligo.Backend.BaseFeature.BaseFeature;
import com.deligo.ConfigLoader.ConfigLoader;
import com.deligo.DatabaseManager.dao.GenericDAO;
import com.deligo.DatabaseManager.exceptions.DatabaseException;
import com.deligo.Model.RegisterData;
import com.deligo.Model.Response;
import com.deligo.Model.User;
import com.google.gson.Gson;
import com.deligo.Logging.Adapter.LoggingAdapter;
import com.deligo.Model.BasicModels.LogPriority;
import com.deligo.Model.BasicModels.LogSource;
import com.deligo.Model.BasicModels.LogType;
import com.deligo.Model.BasicModels.Roles;
import com.deligo.RestApi.RestAPIServer;
import com.google.gson.JsonSyntaxException;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;
import java.util.Optional;

public class FeatureUserRegister extends BaseFeature {

    protected final GenericDAO<User> userDAO;
    private final Gson gson = new Gson();

    public FeatureUserRegister(ConfigLoader globalConfig, LoggingAdapter logger, RestAPIServer restApiServer, GenericDAO<User> userDAO) {
        super(globalConfig, logger, restApiServer);

        this.userDAO = userDAO;
        logger.log(LogType.INFO, LogPriority.MIDDLE, LogSource.BECKEND, UserRegisterMessages.PROCESS_NAME.getMessage(this.getLanguage()));
    }

    /**
     *
     * @param jsonData JSON reťazec s údajmi, ktorý obsahuje:
     *      *                 - username: String
     *      *                 - password: String
     *      *                 - roles: Array of strings
     *      *                 - tag: Empty Array
     * @return JSON odpoveď s message a status (200 pre úspech, 500 pre chybu)
     */
    public String createAccount(String jsonData){
        logger.log(LogType.INFO, LogPriority.MIDDLE, LogSource.BECKEND, "Processing account creation request.");

        RegisterData user;

        try {
            user = gson.fromJson(jsonData, RegisterData.class);
        }
        catch (JsonSyntaxException e) {
            String msg = UserRegisterMessages.INVALID_JSON.getMessage(this.getLanguage(), e.getMessage());
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND, msg);
            return gson.toJson(new Response(msg, 500));
        }

        String username = user.getUsername();
        String rawPassword = user.getPassword();
        String hashedPassword = hashPassword(rawPassword);
        List<String> roles = user.getRoles();

        //Validácia rolý
        String rolesAsString = "testing-role";
//        for (String r : roles) {
//            try {
//                Roles role = Roles.fromString(r);
//                if(rolesAsString.isEmpty()) {
//                    rolesAsString = role.getRoleName();
//                }
//                else {
//                    rolesAsString = rolesAsString.concat("," + role.getRoleName());
//                }
//            } catch (IllegalArgumentException e) {
//                String msg = UserRegisterMessages.INVALID_ROLE.getMessage(this.getLanguage(), e.getMessage());
//                logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND, msg);
//                return gson.toJson(new Response(msg, 500));
//            }
//        }
        logger.log(LogType.INFO, LogPriority.MIDDLE, LogSource.BECKEND, "Parsed roles: " + rolesAsString);

        //Validácia či user existuje v databaze
        Optional<User> userOpt = userDAO.findOneByField("username", username);
        logger.log(LogType.INFO, LogPriority.MIDDLE, LogSource.BECKEND, "UserOpt: " + userOpt);

        if (userOpt.isPresent()) {
            String msg = UserRegisterMessages.USER_NAME_EXISTS.getMessage(this.getLanguage());
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND, msg);
            return gson.toJson(new Response(msg, 500));
        }

        // Vloženie nového používateľa
        User newUser = new User(username, hashedPassword, rolesAsString);
        try {
            userDAO.insert(newUser);
            logger.log(
                    LogType.INFO,
                    LogPriority.MIDDLE,
                    LogSource.BECKEND,
                    "Creating user - Username: " + username + ", Roles: " + newUser.getRoles() // TODO fixnut getrole a get roles
            );
        } catch (DatabaseException e) {
            String msg = UserRegisterMessages.DB_ERROR.getMessage(this.getLanguage()) + e.getMessage();
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND, msg);
            return gson.toJson(new Response(msg, 500));
        }

        // Úspešné registrovanie
        String msg = UserRegisterMessages.SUCCESS.getMessage(this.getLanguage());
        logger.log(LogType.INFO, LogPriority.HIGH, LogSource.BECKEND, msg);
        return gson.toJson(new Response(msg, 200));
    }

    private String hashPassword(String rawPassword) {
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt());
    }


}
