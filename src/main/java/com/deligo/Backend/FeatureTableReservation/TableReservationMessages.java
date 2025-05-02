package com.deligo.Backend.FeatureTableReservation;

public enum TableReservationMessages {
    PROCESS_NAME("FeatureTableReservation Started", "FeatureTableReservation Started"),
    RESERVATION_CREATION_SUCCESS("Rezervácia bola úspešne vytvorená", "Reservation was successfully created"),
    RESERVATION_CREATION_FAILED("Chyba pri vytváraní rezervácie", "Error while creating reservation"),
    RESERVATION_ALREADY_EXISTS("Rezervácia už existuje", "Reservation already exists"),
    RESERVATION_NOT_FOUND("Rezervácia nebola nájdená", "Reservation not found"),
    RESERVATION_CANCELLED_SUCCESS("Rezervácia bola úspešne zrušená", "Reservation was successfully cancelled"),
    RESERVATION_CANCEL_ERROR("Chyba pri zrušení rezervácie", "Error while cancelling reservation"),
    RESERVATION_UPDATE_SUCCESS("Rezervácia bola úspešne aktualizovaná", "Reservation was successfully updated"),
    RESERVATION_UPDATE_FAILED("Chyba pri aktualizácii rezervácie", "Error while updating reservation"),
    RESERVATION_NOT_AVAILIBlE("Rezervácia nie je k dispozícii", "Reservation is not available"),
    INVALID_RESERVATION_TIME("Neplatný čas rezervácie", "Invalid reservation time"),
    INVALID_RESERVATION_DATE("Neplatný dátum rezervácie", "Invalid reservation date"),
    INVALID_RESERVATION_DETAILS("Neplatné detaily rezervácie", "Invalid reservation details"),
    INVALID_REQUEST_FORMAT("Neplatný formát požiadavky", "Invalid request format"),
    INVALID_RESERVATION_ID("Neplatné ID rezervácie", "Invalid reservation ID");


    private final String skMessage;
    private final String enMessage;

    TableReservationMessages(String skMessage, String enMessage) {
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
