package com.deligo.Backend.FeatureEditTables;

public enum TableStructureMessages {
    PROCESS_NAME("FeatureTableStructure Started", "FeatureTableStructure Started"),
    TABLE_ADDED_SUCCESS("Stôl bol úspešne pridaný", "Table was successfully added"),
    TABLE_ADDED_ERROR("Chyba pri pridávaní stola", "Error while adding table"),
    TABLE_UPDATED_SUCCESS("Stôl bol úspešne aktualizovaný", "Table was successfully updated"),
    TABLE_UPDATED_ERROR("Chyba pri aktualizácii stola", "Error while updating table"),
    TABLE_DELETED_SUCCESS("Stôl bol úspešne vymazaný", "Table was successfully deleted"),
    TABLE_DELETED_ERROR("Chyba pri vymazávaní stola", "Error while deleting table"),
    TABLE_NOT_FOUND("Stôl nebol nájdený", "Table not found"),
    CATEGORY_ADDED_SUCCESS("Kategória bola úspešne pridaná", "Category was successfully added"),
    CATEGORY_UPDATED_SUCCESS("Kategória bola úspešne aktualizovaná", "Category was successfully updated"),
    CATEGORY_DELETED_SUCCESS("Kategória bola úspešne vymazaná", "Category was successfully deleted"),
    CATEGORY_ERROR("Chyba pri operácii s kategóriou", "Error during category operation"),
    CATEGORY_NOT_FOUND("Kategória nebola nájdená", "Category not found"),
    CATEGORY_ALREADY_EXISTS("Kategória už existuje", "Category already exists"),
    INVALID_REQUEST_FORMAT("Neplatný formát požiadavky", "Invalid request format");

    private final String skMessage;
    private final String enMessage;

    TableStructureMessages(String skMessage, String enMessage) {
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