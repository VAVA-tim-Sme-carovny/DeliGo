package com.deligo.Backend.FeatureMenuManagement;

public enum MenuManagementMessages {
    PROCESS_NAME("FeatureMenuManagement Started", "FeatureMenuManagement Started"),
    ITEM_ADDED_SUCCESS("Položka bola úspešne pridaná", "Item was successfully added"),
    ITEM_ADDED_ERROR("Chyba pri pridávaní položky", "Error while adding item"),
    ITEM_UPDATED_SUCCESS("Položka bola úspešne aktualizovaná", "Item was successfully updated"),
    ITEM_UPDATED_ERROR("Chyba pri aktualizácii položky", "Error while updating item"),
    ITEM_DELETED_SUCCESS("Položka bola úspešne vymazaná", "Item was successfully deleted"),
    ITEM_DELETED_ERROR("Chyba pri vymazávaní položky", "Error while deleting item"),
    ITEM_NOT_FOUND("Položka nebola nájdená", "Item not found"),
    ITEM_ALREADY_EXISTS("Položka s týmto názvom už existuje", "Item with this name already exists"),
    CATEGORY_ADDED_SUCCESS("Kategória bola úspešne pridaná", "Category was successfully added"),
    CATEGORY_ADDED_ERROR("Chyba pri pridávaní kategórie", "Error while adding category"),
    CATEGORY_UPDATED_SUCCESS("Kategória bola úspešne aktualizovaná", "Category was successfully updated"),
    CATEGORY_UPDATED_ERROR("Chyba pri aktualizácii kategórie", "Error while updating category"),
    CATEGORY_DELETED_SUCCESS("Kategória bola úspešne vymazaná", "Category was successfully deleted"),
    CATEGORY_DELETED_ERROR("Chyba pri vymazávaní kategórie", "Error while deleting category"),
    CATEGORY_NOT_FOUND("Kategória nebola nájdená", "Category not found"),
    CATEGORY_ALREADY_EXISTS("Kategória už existuje", "Category already exists"),
    DETAILS_REQUIRED("Detaily sú povinné", "Details are required"),
    INVALID_REQUEST_FORMAT("Neplatný formát požiadavky", "Invalid request format"),
    ADMIN_REQUIRED("Vyžadujú sa administrátorské práva", "Administrator privileges required"),
    ITEM_IN_USE("Položka je použitá v otvorených objednávkach a nemôže byť vymazaná", "Item is in use in open orders and cannot be deleted"),
    CATEGORY_IN_USE("Kategória je použitá v otvorených objednávkach a nemôže byť vymazaná", "Category is in use in open orders and cannot be deleted");

    private final String skMessage;
    private final String enMessage;

    MenuManagementMessages(String skMessage, String enMessage) {
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
