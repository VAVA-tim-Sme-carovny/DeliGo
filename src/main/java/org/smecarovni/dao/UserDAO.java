package org.smecarovni.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.smecarovni.db.DatabaseConnector;
import org.smecarovni.exceptions.DatabaseException;
import java.sql.*;

public class UserDAO {

    // Vytvorenie loggera pre túto triedu
    private static final Logger logger = LogManager.getLogger(UserDAO.class);

    // CREATE
    public void createUser(String username, String email) {
        String sql = "INSERT INTO users (username, email) VALUES (?, ?)";
        try (PreparedStatement stmt = DatabaseConnector.getConnection().prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, email);
            stmt.executeUpdate();
            // Logovanie vytvorenia používateľa
            logger.info("✅ User created: " + username);
        } catch (SQLException e) {
            // Logovanie chyby pri vkladaní používateľa
            logger.error("Error inserting user: " + username, e);
            throw new DatabaseException("Error inserting user", e);
        }
    }

    // READ
    public void getUsers() {
        String sql = "SELECT * FROM users";
        try (Statement stmt = DatabaseConnector.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                // Logovanie získaného používateľa
                logger.info("👤 " + rs.getInt("id") + " | " +
                        rs.getString("username") + " | " +
                        rs.getString("email"));
            }
        } catch (SQLException e) {
            // Logovanie chyby pri získavaní používateľov
            logger.error("Error fetching users", e);
            throw new DatabaseException("Error fetching users", e);
        }
    }

    // UPDATE
    public void updateUser(int id, String username, String email) {
        String sql = "UPDATE users SET username = ?, email = ? WHERE id = ?";
        try (PreparedStatement stmt = DatabaseConnector.getConnection().prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, email);
            stmt.setInt(3, id);
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                // Logovanie úspešnej aktualizácie
                logger.info("✅ User updated: " + id);
            } else {
                // Logovanie, ak používateľ s týmto ID neexistuje
                logger.warn("⚠️ No user found with ID: " + id);
            }
        } catch (SQLException e) {
            // Logovanie chyby pri aktualizovaní používateľa
            logger.error("Error updating user with ID: " + id, e);
            throw new DatabaseException("Error updating user", e);
        }
    }

    // DELETE
    public void deleteUser(int id) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (PreparedStatement stmt = DatabaseConnector.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted > 0) {
                // Logovanie úspešného vymazania
                logger.info("✅ User deleted: " + id);
            } else {
                // Logovanie, ak používateľ s týmto ID neexistuje
                logger.warn("⚠️ No user found with ID: " + id);
            }
        } catch (SQLException e) {
            // Logovanie chyby pri vymazávaní používateľa
            logger.error("Error deleting user with ID: " + id, e);
            throw new DatabaseException("Error deleting user", e);
        }
    }
}
