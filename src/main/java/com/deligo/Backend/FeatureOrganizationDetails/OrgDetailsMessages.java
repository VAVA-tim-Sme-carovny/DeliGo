package com.deligo.Backend.FeatureOrganizationDetails;

public enum OrgDetailsMessages {
    PROCESS_NAME("FeatureOrgDetails Started", "FeatureOrgDetails Started"),
    INVALID_JSON("Neplatný formát JSON: %s", "Invalid JSON format: %s"),
    INVALID_PHONE("Telefónne číslo je zadané v nesprávnom formáte", "Phone number is in incorrect format"),
    INVALID_EMAIL("Email je zadaný v nesprávnom formáte", "Email is in incorrect format"),
    OPENING_TIMES_NOT_PROVIDED("Otváracie časy neboli poskytnuté", "Opening times not provided"),
    SUCCESS("Údaje boli zapísané správne", "Data saved successfully"),
    DB_ERROR("Chyba pri zápise do databázy", "Database write error"),
    ORG_DETAILS_UPDATED_SUCCESS("Údaje boli aktualizované", "Data updated successfully"),
    ORG_DETAILS_UPDATED_FAILED("Chyba pri aktualizácii údajov", "Data update failed");

    private final String skMessage;
    private final String enMessage;

    OrgDetailsMessages(String skMessage, String enMessage) {
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
