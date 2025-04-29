package com.deligo.Backend.FeatureOrderProcessing;

public enum OrderProcessingMessages {
    PROCESS_NAME("Spracovanie objednávky a položiek", "Order and items processing"),
    INVALID_JSON("Neplatný formát JSON: %s", "Invalid JSON format: %s"),
    ORDER_NOT_FOUND("Objednávka s ID %s nebola nájdená", "Order with ID %s not found"),
    ORDER_ITEM_NOT_FOUND("Položka objednávky s ID %s nebola nájdená", "Order item with ID %s not found"),
    ORDER_CONFIRMED("Objednávka bola potvrdená", "Order has been confirmed"),
    ORDER_REJECTED("Objednávka bola zamietnutá: %s", "Order has been rejected: %s"),
    ORDER_UPDATED("Objednávka bola aktualizovaná", "Order has been updated"),
    ITEM_PREPARED("Položka bola pripravená", "Item has been prepared"),
    ITEM_DELIVERED("Položka bola doručená", "Item has been delivered"),
    NEW_ORDER_NOTIFICATION("Nová objednávka čaká na spracovanie", "New order waiting for processing"),
    DRINKS_AUTO_PREPARING("Nápoje boli automaticky nastavené na stav 'pripravuje sa'", "Drinks were automatically set to 'preparing' state"),
    DB_ERROR("Chyba pri práci s databázou: %s", "Database error: %s");

    private final String skMessage;
    private final String enMessage;

    OrderProcessingMessages(String skMessage, String enMessage) {
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