package com.deligo.Backend.FeatureUserLogin;

public enum UserLoginMessages {
    PROCESS_NAME("Spracovanie požiadavky na prihlásenie používateľa", "Processing user login request"),
    INVALID_JSON("Neplatný formát JSON: %s", "Invalid JSON format: %s"),
    MISSING_USERNAME("Chýba používateľské meno", "Username is missing"),
    MISSING_PASSWORD("Chýba heslo", "Password is missing"),
    USER_NOT_FOUND("Používateľ %s nebol nájdený", "User %s not found"),
    INVALID_CREDENTIALS("Neplatné prihlasovacie údaje", "Invalid login credentials"),
    ACCOUNT_LOCKED("Účet je zablokovaný", "Account is locked"),
    SUCCESS("Vitaj %s!", "Welcome %s!"),
    DB_ERROR("Chyba pri načítaní z databázy", "Error retrieving from database"),
    NOT_LOGGED_IN("Nie ste prihlásený, preto sa nemôžete odhlásiť", "YOU ARE NOT LOGGED IN"),
    ALREADY_LOGED_IN("Už ste prihlásený na inom zariadení", "User already logged in"),
    ACTIVE_ORDER("Objednávka nieje ukončená, zavolajte prosím obsluhu", "The order is not completed, call the service please"),
    WELCOME_MESSAGE("Vitaj!", "Welcome!"),
    LOGOUT_MESSAGE("Úspešné odhlásenie!", "Successful logout!"),;

    private final String skMessage;
    private final String enMessage;

    UserLoginMessages(String skMessage, String enMessage) {
        this.skMessage = skMessage;
        this.enMessage = enMessage;
    }

    /**
     * Vráti naformátovanú správu podľa zvoleného jazyka.
     *
     * @param language Hodnota "sk" alebo "en"
     * @param params Voliteľné parametre pre formátovanie správy
     * @return Naformátovaná správa
     */
    public String getMessage(String language, Object... params) {
        String template = "en".equalsIgnoreCase(language) ? enMessage : skMessage;
        return String.format(template, params);
    }
}
