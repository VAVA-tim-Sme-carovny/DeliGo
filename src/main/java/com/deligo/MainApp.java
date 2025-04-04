package com.deligo;

import com.deligo.ConfigLoader.ConfigLoader;
import com.deligo.DatabaseManager.dao.GenericDAO;
import com.deligo.DatabaseManager.example.Users;
import com.deligo.Logging.Adapter.LoggingAdapter;
import com.deligo.Logging.LoggingManager;

import com.deligo.Model.BasicModels.*;
import com.deligo.RestApi.RestAPIServer;
import com.deligo.Frontend.Frontend;
import com.deligo.Backend.Backend;


public class MainApp {



    private static final String CONFIG_FILE = "src/main/resources/config.yaml";
//    GenericDAO<Users> userDAO = new GenericDAO<>(Users.class, "users");

    public static void main(String[] args) {

//        GenericDAO<Users> userDAO = new GenericDAO<>(Users.class, "users");
        LoggingManager.initialize();

        ConfigLoader config = new ConfigLoader(CONFIG_FILE);

        while (LoggingManager.getAdapter() == null) {
            try {
                System.out.println("Waiting for LoggingManager initialization...");
                Thread.sleep(400); // 300 ms delay
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        LoggingAdapter logger = LoggingManager.getAdapter();
        logger.log(LogType.SUCCESS, LogPriority.HIGH, LogSource.LOGGING ,"Logging loaded successfully");

        RestAPIServer restApiServer = null;
        Backend backend = null;
        Frontend frontend = null;

        try{
            restApiServer = new RestAPIServer(logger, config);
        }catch (Exception e){
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.REST_API ,"Rest Api didn't initialize properly: " + e.getMessage());
        }


        if (args.length == 0) {
            System.out.println("No argument provided. Use '--com.deligo.backend', '--com.deligo.frontend', '--dev'");
            System.exit(1);
        }

        String mode = args[0].toLowerCase();

        try {
            switch (mode) {
                case "--com.deligo.backend":

                    logger.log(LogType.INFO, null, null, "Starting backend mode");

                    try{
                        backend = new Backend(restApiServer, logger, config);
                    }catch (Exception e) {
                        logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND, "Backend didn't initialize properly: " + e.getMessage());
                    }

                    break;
                case "--com.deligo.frontend":
                    logger.log(LogType.INFO, null, null, "Starting frontend mode");

                    try{
                        frontend = new Frontend(restApiServer, logger, config);
                    }catch (Exception e) {
                        logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND, "Frontend didn't initialize properly: " + e.getMessage());
                    }

                    break;
                case "--com.deligo.development":
                    logger.log(LogType.INFO, null, null, "Starting development mode");
                    try{
                        backend = new Backend(restApiServer, logger, config);
                    }catch (Exception e) {
                        logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND, "Backend didn't initialize properly: " + e.getMessage());
                    }

                    try{
                        frontend = new Frontend(restApiServer, logger, config);
                    }catch (Exception e) {
                        logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND, "Frontend didn't initialize properly: " + e.getMessage());
                    }

                    break;
                default:
                    logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.MAVEN ,"Invalid argument '{}'. Use 'restapi', 'com.deligo.backend', or 'com.deligo.frontend'");
                    System.exit(1);
            }
        } catch (Exception e) {
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.MAVEN ,"Error during inicialization: " + e);
            System.exit(1);
        }
    }
}