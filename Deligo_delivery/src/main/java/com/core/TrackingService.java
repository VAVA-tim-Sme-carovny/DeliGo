package com.core;

import com.service.LoggingService;
import org.apache.logging.log4j.Logger;

public class TrackingService {
    private static final Logger logger = LoggingService.getLogger(TrackingService.class);

    public String getTrackingStatus() {
        logger.info("Fetching tracking status...");
        return "Tracking is active";
    }
}