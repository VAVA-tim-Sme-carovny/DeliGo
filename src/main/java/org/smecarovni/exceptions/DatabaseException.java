package org.smecarovni.exceptions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DatabaseException extends RuntimeException {

    private static final Logger logger = LogManager.getLogger(DatabaseException.class);

    public DatabaseException(String message) {
        super(message);
        // Logovanie chyby s hláškou
        logger.error(message);
    }

    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
        // Logovanie chyby s hláškou a detailmi výnimky
        logger.error(message, cause);
    }
}
