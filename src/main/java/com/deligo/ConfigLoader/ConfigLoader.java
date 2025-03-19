package com.deligo.ConfigLoader;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;

public class ConfigLoader {

    private Map<String, Object> config;

    public ConfigLoader(String filePath) {
        try {
            Yaml yaml = new Yaml();
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filePath);

            if (inputStream == null) {
                throw new RuntimeException("Config file not found: " + filePath);
            }

            config = yaml.load(inputStream);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load config: " + e.getMessage());
        }
    }

    public String getDeviceId() {
        Map<String, Object> deviceMap = (Map<String, Object>) config.get("device");
        return deviceMap.get("id").toString();
    }

    public String getLoginRole() {
        Map<String, Object> loginMap = (Map<String, Object>) config.get("login");
        return loginMap.get("roles").toString();
    }

    public boolean getStatus() {
        Map<String, Object> deviceMap = (Map<String, Object>) config.get("device");
        return Boolean.parseBoolean(deviceMap.get("status").toString());
    }
}
