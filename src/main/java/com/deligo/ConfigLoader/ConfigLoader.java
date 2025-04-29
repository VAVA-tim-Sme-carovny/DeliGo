package com.deligo.ConfigLoader;

import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ConfigLoader {

    private final Map<String, Object> config;
    private final String filePath;

    public ConfigLoader(String filePath) {
        this.filePath = filePath;
        try {
            Yaml yaml = new Yaml();
            InputStream inputStream = new FileInputStream(filePath);
            if (inputStream == null) {
                throw new RuntimeException("Config file not found: " + filePath);
            }
            config = yaml.load(inputStream);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load config: " + e.getMessage());
        }
    }

    /**
     * @param mainKey Main branch for e.g. device or login
     * @param elementKey Key of the branch e.g. id,language,...
     * @return Returns generic value from config file based on main branch and element in that branch
     */

    // Pre reťazce:
    // tring language = getConfigValue("device", "language", String.class);

    // Pre pole:
    // List<String> tags = getConfigValue("login", "tags", List.class);
    @SuppressWarnings("unchecked")
    public <T> T getConfigValue(String mainKey, String elementKey, Class<T> type) {
        Map<String, Object> section = (Map<String, Object>) config.get(mainKey);
        if (section != null) {
            Object value = section.get(elementKey);
            if (value == null) {
                return null;
            }
            if (type.isInstance(value)) {
                return (T) value;
            } else {
                throw new RuntimeException("Config value for key " + elementKey + " is not of type " + type.getName());
            }
        }
        return null;
    }

    /**
     * Updates specific value in config file
     * @param mainKey Main branch for e.g. device or login
     * @param elementKey Key of the branch e.g. id,language,...
     * @param newValue New value for selected element
     */

    //    List<String> roles = Arrays.asList("customer", "admin");
    //    configLoader.updateConfigValue("login", "tags", roles);

    public void updateConfigValue(String mainKey, String elementKey, Object newValue) {
        Map<String, Object> section = (Map<String, Object>) config.get(mainKey);
        if (section != null) {
            section.put(elementKey, newValue);
        } else {
            section = new HashMap<>();
            section.put(elementKey, newValue);
            config.put(mainKey, section);
        }
        Yaml yaml = new Yaml();
        try (FileWriter writer = new FileWriter(filePath)) {
            yaml.dump(config, writer);
        } catch (IOException e) {
            throw new RuntimeException("Failed to update config: " + e.getMessage());
        }
    }
}
