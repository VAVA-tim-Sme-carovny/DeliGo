package com.deligo.Backend.FeatureOrderManagement;

public enum OrderManagementMessages {
    PROCESS_NAME("FeatureOrderManagement Started", "FeatureOrderManagement Started"),
    ORDER_CREATED_SUCCESS("Objednávka bola úspešne vytvorená", "Order was successfully created"),
    ORDER_CREATED_FAILED("Chyba pri vytváraní objednávky", "Error while creating order"),
    ORDER_UPDATED_SUCCESS("Objednávka bola úspešne aktualizovaná", "Order was successfully updated"),
    ORDER_UPDATED_FAILED("Chyba pri aktualizácii objednávky", "Error while updating order"),
    ORDER_DELETED_SUCCESS("Objednávka bola úspešne vymazaná", "Order was successfully deleted"),
    ORDER_DELETED_FAILED("Chyba pri vymazávaní objednávky", "Error while deleting order"),
    ORDER_NOT_FOUND("Objednávka nebola nájdená", "Order not found"),
    INVALID_ORDER_ID("Neplatné ID objednávky", "Invalid order ID"),
    INVALID_ORDER_STATUS("Neplatný stav objednávky", "Invalid order status"),
    INVALID_ORDER_ITEMS("Neplatné položky objednávky", "Invalid order items"),
    ORDER_ITEM_ADDED_SUCCESS("Položka bola úspešne pridaná do objednávky", "Item was successfully added to order"),
    ORDER_ITEM_ADDED_FAILED("Chyba pri pridávaní položky do objednávky", "Error while adding item to order"),
    ORDER_ITEM_REMOVED_SUCCESS("Položka bola úspešne odstránená z objednávky", "Item was successfully removed from order"),
    ORDER_ITEM_REMOVED_FAILED("Chyba pri odstraňovaní položky z objednávky", "Error while removing item from order"),
    INVALID_REQUEST_FORMAT("Neplatný formát požiadavky", "Invalid request format"),
    ORDER_UPDATE_DEADLINE_EXPIRED("Čas na úpravu objednávky vypršal", "Order modification deadline has expired"),
    ORDER_UPDATE_SUCCESS("Objednávka bola úspešne upravená", "Order was successfully modified"),
    ORDER_UPDATE_FAILED("Objednávku sa nepodarilo upraviť", "Failed to modify order");

    private final String skMessage;
    private final String enMessage;

    OrderManagementMessages(String skMessage, String enMessage) {
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
