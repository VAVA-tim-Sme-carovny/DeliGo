package org.smecarovni;

import org.smecarovni.dao.UserDAO;
import org.smecarovni.db.DatabaseConnector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {

    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("Program started");

        UserDAO userDAO = new UserDAO();

        // Test CRUD operácií
        try {
            logger.info("Trying to create user Alice");
            userDAO.createUser("Alice", "alice@example.com");

            logger.info("Fetching users after creation");
            userDAO.getUsers();

            logger.info("Updating user with ID 1");
            userDAO.updateUser(3, "Alice Updated", "alice.updated@example.com");

            logger.info("Fetching users after update");
            userDAO.getUsers();

            logger.info("Deleting user with ID 1");
            userDAO.deleteUser(3);

            logger.info("Fetching users after deletion");
            userDAO.getUsers();

        } catch (Exception e) {
            logger.error("Error during CRUD operations: ", e);
        } finally {
            // Zavrieme spojenie s databázou
            DatabaseConnector.close();
            logger.info("Database connection closed.");
        }
    }
}
