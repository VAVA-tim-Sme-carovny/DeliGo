package com.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerConfig {
    private static final Logger logger = LoggerFactory.getLogger(LoggerConfig.class);

    public static Logger getLogger(Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
    }
}