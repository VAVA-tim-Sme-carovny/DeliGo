package com.deligo.Backend.FeatureUserRegister;

public enum MyNewProcessMessages {
    PROCESS_NAME("FeatureUserRegister Started", "FeatureUserRegister Started");


    private final String skMessage;
    private final String enMessage;

    MyNewProcessMessages(String skMessage, String enMessage) {
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
