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

    public enum Roles {
        BASIC,
        ADMIN,
        CREATE_ORDER,
        EDIT_ORDER,
        CHANGE_ORDER_STATE;

        public String getRoleName() {
            return this.name().toLowerCase();
        }

        public static Roles fromString(String text) {
            for (Roles role : Roles.values()) {
                if (role.getRoleName().equalsIgnoreCase(text)) {
                    return role;
                }
            }
            throw new IllegalArgumentException(text);
        }
    }

}