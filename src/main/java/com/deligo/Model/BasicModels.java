package com.deligo.Model;

public class BasicModels {

    public enum LogType {
        ERROR,
        WARNING,
        SUCCESS,
        INFO
    }

    public enum LogPriority {
        LOW,
        MIDDLE,
        HIGH
    }

    public enum LogSource {
        REST_API,
        DB_PERSISTENCE,
        LOGGING,
        BECKEND,
        FRONTEND,
        MAVEN
    }

}