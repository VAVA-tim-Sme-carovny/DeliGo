package com.deligo.Backend.FeatureStatistics;

public enum StatisticsMessages {
    PROCESS_NAME("FeatureStatistics Started", "FeatureStatistics Started"),
    STATS_LOADED_SUCCESS("Štatistiky úspešne načítané", "Statistics successfully loaded"),
    STATS_LOADED_ERROR("Chyba pri načítaní štatistík", "Error while loading statistics"),
    NO_STATISTICS_FOUND("Žiadne štatistiky nenájdené pre zadané obdobie", "No statistics found for the given period"),
    INVALID_DATE_RANGE("Neplatný rozsah dátumov", "Invalid date range");

    private final String skMessage;
    private final String enMessage;

    StatisticsMessages(String skMessage, String enMessage) {
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