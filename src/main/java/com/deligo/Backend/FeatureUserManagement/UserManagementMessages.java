package com.deligo.Backend.FeatureUserManagement;

public enum UserManagementMessages {
    PROCESS_NAME("FeatureUserManagement Started", "FeatureUserManagement Started"),
    USER_NOT_FOUND("Používateľ nebol nájdený", "User not found"),
    USER_ROLES_UPDATED("Role používateľa boli zmenené", "User roles have been updated"),
    USER_TAGS_UPDATED("Tagy používateľa boli zmenené", "User tags have been updated"),
    USER_DELETED("Používateľ bol úspešne vymazaný", "User was successfully deleted"),
    USER_UPDATE_ERROR("Chyba pri aktualizácii používateľa", "Error while updating user"),
    USER_DELETE_ERROR("Chyba pri vymazávaní používateľa", "Error while deleting user"),
    ORG_DETAILS_UPDATED("Údaje o organizácii boli aktualizované", "Organization details were updated"),
    ORG_DETAILS_ERROR("Chyba pri aktualizácii údajov o organizácii", "Error updating organization details"),
    INVALID_PHONE_FORMAT("Telefónne číslo je zadané v nesprávnom formáte", "Phone number format is invalid"),
    INVALID_EMAIL_FORMAT("Email je zadaný v nesprávnom formáte", "Email format is invalid"),
    INVALID_REQUEST_FORMAT("Neplatný formát požiadavky", "Invalid request format");

    private final String skMessage;
    private final String enMessage;

    UserManagementMessages(String skMessage, String enMessage) {
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