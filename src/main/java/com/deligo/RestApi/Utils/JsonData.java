package com.deligo.RestApi.Utils;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * A simple class to represent JSON data for validation purposes.
 */
public class JsonData {

    @NotNull(message = "JSON data cannot be null")
    @Size(max = 32768, message = "JSON data exceeds maximum size of 32768 characters")
    @NoSqlInjection(message = "Potential SQL injection detected in JSON data")
    private String content;

    public JsonData() {
        // Default constructor required for validation
    }

    public JsonData(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
