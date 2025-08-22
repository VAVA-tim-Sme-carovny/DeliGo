package com.deligo.Backend.FeatureUserRegistration;

public enum UserRegisterMessages {
    PROCESS_NAME("Spracovanie požiadavky na registráciu používateľa", "Processing user registration request"),
    INVALID_JSON("Neplatný formát JSON: %s", "Invalid JSON format: %s"),
    INVALID_USERNAME("Používateľské meno je neplatné", "Username is invalid"),
    USER_NAME_EXISTS("Používateľské meno už existuje", "Username already exists"),
    INVALID_PASSWORD("Heslo je neplatné", "Password is invalid"),
    INVALID_ROLE("Nesprávna rola: %s", "Invalid role: %s"),
    NO_ROLES_PROVIDED("Žiadne role neboli poskytnuté", "No roles were provided"),
    TAGS_PROVIDED("Tagy boli poskytnuté", "Tags were provided"),
    TAGS_NOT_PROVIDED("Žiadne tagy neboli poskytnuté", "No tags were provided"),
    SUCCESS("Používateľ bol úspešne registrovaný", "User was registered successfully"),
    DB_ERROR("Chyba pri ukladaní do databázy", "Error saving to database");


    private final String skMessage;
    private final String enMessage;

    UserRegisterMessages(String skMessage, String enMessage) {
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
