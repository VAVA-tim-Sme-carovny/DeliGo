package org.smecarovni.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.smecarovni.db.DatabaseConnector;
import org.smecarovni.exceptions.DatabaseException;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.*;

public class UserDAO {

    private static final Logger logger = LogManager.getLogger(UserDAO.class);

    // CREATE
    public void createUser(String username, String email, String password) {
        String sql = "INSERT INTO users (username, email, password, role) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, email);
            stmt.setString(3, password); // Predpokladáme, že heslo je už hashované
            stmt.setString(4, "customer");

            stmt.executeUpdate();

            logger.info("✅ User created: {}", username);
        } catch (SQLException e) {
            logger.error("❌ Error inserting user: {}", username, e);
            throw new DatabaseException("Error inserting user", e);
        }
    }

    // READ
    public void getUsers() {
        String sql = "SELECT id, username, email FROM users";
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {


            while (rs.next()) {
                logger.info("👤 ID: {} | Username: {} | Email: {}",
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("email"));
            }
        } catch (SQLException e) {
            logger.error("❌ Error fetching users", e);
            throw new DatabaseException("Error fetching users", e);
        }
    }

    // UPDATE
    public void updateUser(int id, String username, String email) {
        String sql = "UPDATE users SET username = ?, email = ? WHERE id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {


            stmt.setString(1, username);
            stmt.setString(2, email);
            stmt.setInt(3, id);


            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                logger.info("✅ User updated: ID {}", id);
            } else {
                logger.warn("⚠️ No user found with ID: {}", id);
            }
        } catch (SQLException e) {
            logger.error("❌ Error updating user with ID: {}", id, e);
            throw new DatabaseException("Error updating user", e);
        }
    }

    // DELETE
    public void deleteUser(int id) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted > 0) {
                logger.info("✅ User deleted: ID {}", id);
            } else {
                logger.warn("⚠️ No user found with ID: {}", id);
            }
        } catch (SQLException e) {
            logger.error("❌ Error deleting user with ID: {}", id, e);
            throw new DatabaseException("Error deleting user", e);
        }
    }

    public void updateUserRole(int userId, String newRole, int currentUserId) {
        // Skontrolujeme, či je prihlásený používateľ administrátor
        if (!isAdmin(currentUserId)) {
            throw new SecurityException("Only admins can update user roles.");
        }

        String sql = "UPDATE users SET role = ? WHERE id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newRole);  // Nová rola
            stmt.setInt(2, userId);      // ID používateľa, ktorému chceme zmeniť rolu
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Error updating user role", e);
        }
    }

    // Funkcia, ktorá skontroluje, či je prihlásený používateľ admin
    private boolean isAdmin(int userId) {
        String sql = "SELECT role FROM users WHERE id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return "admin".equalsIgnoreCase(rs.getString("role"));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error checking user role", e);
        }
        return false;
    }


    public int authenticateUser(String username, String password) {
        String sql = "SELECT id, password FROM users WHERE username = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("password");
                if (BCrypt.checkpw(password, storedPassword)) {
                    return rs.getInt("id");  // Vrátime ID používateľa
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error authenticating user", e);
        }
        return -1; // -1 znamená, že prihlásenie zlyhalo
    }

}
