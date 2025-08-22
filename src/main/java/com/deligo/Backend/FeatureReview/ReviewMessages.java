package com.deligo.Backend.FeatureReview;

public enum ReviewMessages {
    PROCES_NAME("FeatureReview Started", "FeatureReview Started"),
    REVIEW_SUCCESS("Recenzia bola úspešne pridaná", "Review was successfully added"),
    REVIEW_FAILED("Chyba pri pridávaní recenzie", "Error while adding review"),
    REVIEW_NOT_FOUND("Recenzia nebola nájdená", "Review not found"),
    REVIEW_UPDATE_SUCCESS("Recenzia bola úspešne aktualizovaná", "Review was successfully updated"),
    REVIEW_UPDATE_ERROR("Chyba pri aktualizácii recenzie", "Error while updating review"),
    REVIEW_DELETE_SUCCESS("Recenzia bola úspešne vymazaná", "Review was successfully deleted"),
    REVIEW_DELETE_ERROR("Chyba pri vymazávaní recenzie", "Error while deleting review"),
    INVALID_REVIEW_ID("Neplatné ID recenzie", "Invalid review ID"),
    INVALID_REVIEW_CONTENT("Neplatný obsah recenzie", "Invalid review content"),
    INVALID_REVIEW_RATING("Neplatné hodnotenie recenzie", "Invalid review rating"),
    REVIEW_ALREADY_EXISTS("Recenzia už existuje", "Review already exists"),
    REVIEW_NOT_FOUND_FOR_USER("Recenzia nebola nájdená pre používateľa", "Review not found for user"),
    REVIEW_DELETED_SUCCESS("Recenzia bola úspešne vymazaná", "Review deleted successfully"),
    REVIEW_DELETED_FAILED("Chyba pri vymazávaní recenzie","Error deleting review"),
    REVIEW_UPDATED_SUCCESS("Recenzia bola úspešne aktualizovaná","Review updated successfully"),
    REVIEW_UPDATED_FAILED("Error pri aktualizácii recenzie", "Error updating review"),
    INVALID_REQUEST_FORMAT("Neplatný formát požiadavky", "Invalid request format");

    private final String skMessage;
    private final String enMessage;

    ReviewMessages(String skMessage, String enMessage) {
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
