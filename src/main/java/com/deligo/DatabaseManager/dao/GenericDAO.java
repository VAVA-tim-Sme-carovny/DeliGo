package com.deligo.DatabaseManager.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.deligo.DatabaseManager.db.DatabaseConnector;
import com.deligo.DatabaseManager.exceptions.DatabaseException;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class GenericDAO<T> {
    private final Class<T> entityClass;
    private static final Logger logger = LogManager.getLogger(Class.class);
    private final String tableName;


    public GenericDAO(Class<T> entityClass, String tableName) {
        this.entityClass = entityClass;
        this.tableName = tableName.toLowerCase();
    }

    // Získaj pripojenie z DatabaseConnector
    private Connection getConnection() {
        try {
//            logger.info("👉 Attempting to get a database connection...");
            Connection conn = DatabaseConnector.getConnection();
//            logger.info("✅ Successfully obtained a database connection: {}", conn);
            return conn;
        } catch (SQLException e) {
//            logger.error("❌ SQL Exception while obtaining database connection:", e);
            throw new DatabaseException("SQL error while obtaining database connection", e);
        } catch (Exception e) {
//            logger.error("❌ Unexpected error while obtaining database connection:", e);
            throw new DatabaseException("Unexpected error while obtaining database connection", e);
        }
    }

    // Vloženie nového záznamu (INSERT)
    public int insert(T entity) {
        Field[] fields = entityClass.getDeclaredFields();

        List<String> fieldNames = getFieldNames(fields).stream()
                .filter(name -> !name.equals("id"))
                .collect(Collectors.toList());

        String columnNames = String.join(", ", fieldNames);
        String placeholders = fieldNames.isEmpty() ? "" : "?" + ", ?".repeat(fieldNames.size() - 1);

        String sql = "INSERT INTO " + tableName + " (" + columnNames + ") VALUES (" + placeholders + ") RETURNING id";

        logger.info("Pripravujeme SQL INSERT pre entitu: {}. SQL príkaz: {}", tableName, sql);

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            setStatementParams(stmt, fields, entity, fieldNames);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int id = rs.getInt(1);
                logger.info("✅ INSERT úspešný. Nové ID: {}", id);
                return id;
            }
        } catch (SQLException e) {
            logger.error("Chyba pri vkladaní do tabuľky: {}. SQL: {}. Chyba: {}", tableName, sql, e.getMessage());
            throw new DatabaseException("Chyba pri vkladaní entity", e);
        } catch (Exception e) {
            logger.error("Neočakávaná chyba pri vkladaní do tabuľky: {}. SQL: {}. Chyba: {}", tableName, sql, e.getMessage());
            throw new DatabaseException("Neočakávaná chyba pri vkladaní entity", e);
        }
        return -1; // Ak sa insert nepodarí
    }

    // Získanie všetkých záznamov (SELECT *)
    public List<T> getAll() {
        List<T> results = new ArrayList<>();
        String sql = "SELECT * FROM " + tableName;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                results.add(mapResultSetToEntity(rs));
            }
        } catch (SQLException e) {
            logger.error("Chyba pri získavaní záznamov: {}. SQL príkaz: {}. Chyba: {}", entityClass.getSimpleName(), sql, e.getMessage());
            throw new DatabaseException("Chyba pri získavaní záznamov", e);
        } catch (Exception e) {
            logger.error("Neočakávaná chyba pri získavaní záznamov: {}. SQL príkaz: {}. Chyba: {}", entityClass.getSimpleName(), sql, e.getMessage());
            throw new DatabaseException("Neočakávaná chyba pri získavaní záznamov", e);
        }
        return results;
    }

    // Nájdite záznam podľa ID
    public Optional<T> getById(int id) {
        String sql = "SELECT * FROM " + tableName + " WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToEntity(rs));
            }
        } catch (SQLException e) {
            logger.error("Chyba pri získavaní entity podľa ID: {}. SQL príkaz: {}. Chyba: {}", id, sql, e.getMessage());
            throw new DatabaseException("Chyba pri získavaní entity podľa ID", e);
        } catch (Exception e) {
            logger.error("Neočakávaná chyba pri získavaní entity podľa ID: {}. SQL príkaz: {}. Chyba: {}", id, sql, e.getMessage());
            throw new DatabaseException("Neočakávaná chyba pri získavaní entity podľa ID", e);
        }
        return Optional.empty();
    }

    // Aktualizácia záznamu (UPDATE)
    public void update(int id, T entity) {
        Field[] fields = entityClass.getDeclaredFields();

        List<String> fieldNames = getFieldNames(fields).stream()
                .filter(name -> !name.equals("id"))
                .collect(Collectors.toList());

        String setClause = fieldNames.stream()
                .map(name -> name + " = ?")
                .collect(Collectors.joining(", "));

        String sql = "UPDATE " + tableName + " SET " + setClause + " WHERE id = ?";

        logger.info("Pripravujeme SQL UPDATE pre entitu: {}. SQL príkaz: {}", tableName, sql);

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            setStatementParams(stmt, fields, entity, fieldNames);
            stmt.setInt(fieldNames.size() + 1, id); // ID pridávame na koniec ako WHERE parameter

            stmt.executeUpdate();
            logger.info("Úspešne vykonaný UPDATE do tabuľky: {}. Počet ovplyvnených riadkov: {}", tableName, stmt.getUpdateCount());
        } catch (SQLException e) {
            logger.error("Chyba pri aktualizácii tabuľky: {}. SQL príkaz: {}. Chyba: {}", tableName, sql, e.getMessage());
            throw new DatabaseException("Chyba pri aktualizácii entity", e);
        } catch (Exception e) {
            logger.error("Neočakávaná chyba pri aktualizácii tabuľky: {}. SQL príkaz: {}. Chyba: {}", tableName, sql, e.getMessage());
            throw new DatabaseException("Neočakávaná chyba pri aktualizácii entity", e);
        }
    }

    // Odstránenie záznamu (DELETE)
    public void delete(int id) {
        String sql = "DELETE FROM " + tableName + " WHERE id = ?";

        logger.info("Pripravujeme SQL DELETE pre tabuľku: {}. SQL príkaz: {}", tableName, sql);

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            logger.info("Úspešne vykonaný DELETE do tabuľky: {}. Počet ovplyvnených riadkov: {}", tableName, stmt.getUpdateCount());
        } catch (SQLException e) {
            logger.error("Chyba pri vymazávaní z tabuľky: {}. SQL príkaz: {}. Chyba: {}", tableName, sql, e.getMessage());
            throw new DatabaseException("Chyba pri vymazávaní entity", e);
        } catch (Exception e) {
            logger.error("Neočakávaná chyba pri vymazávaní z tabuľky: {}. SQL príkaz: {}. Chyba: {}", tableName, sql, e.getMessage());
            throw new DatabaseException("Neočakávaná chyba pri vymazávaní entity", e);
        }
    }

    // Získanie počtu záznamov v tabuľke
    public int count() {
        String sql = "SELECT COUNT(*) FROM " + tableName;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            logger.error("Chyba pri získavaní počtu záznamov: {}. SQL príkaz: {}. Chyba: {}", entityClass.getSimpleName(), sql, e.getMessage());
            throw new DatabaseException("Chyba pri získavaní počtu záznamov", e);
        } catch (Exception e) {
            logger.error("Neočakávaná chyba pri získavaní počtu záznamov: {}. SQL príkaz: {}. Chyba: {}", entityClass.getSimpleName(), sql, e.getMessage());
            throw new DatabaseException("Neočakávaná chyba pri získavaní počtu záznamov", e);
        }
        return 0;
    }

    // Vyhľadanie podľa konkrétneho poľa (napr. rola)
    public List<T> findByField(String fieldName, Object value) {
        List<T> results = new ArrayList<>();
        String sql = "SELECT * FROM " + tableName + " WHERE " + fieldName + " = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, value);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                results.add(mapResultSetToEntity(rs));
            }
        } catch (SQLException e) {
            logger.error("Chyba pri vyhľadávaní podľa {} = {}. SQL: {}. Chyba: {}", fieldName, value, sql, e.getMessage());
            throw new DatabaseException("Chyba pri vyhľadávaní entity podľa poľa", e);
        } catch (Exception e) {
            logger.error("Neočakávaná chyba pri vyhľadávaní podľa {} = {}. SQL: {}. Chyba: {}", fieldName, value, sql, e.getMessage());
            throw new DatabaseException("Neočakávaná chyba pri vyhľadávaní entity podľa poľa", e);
        }
        return results;
    }

    // Vyhľadanie unikátneho záznamu podľa konkrétneho poľa (napr. username)
    public Optional<T> findOneByField(String fieldName, Object value) {
        String sql = "SELECT * FROM " + tableName + " WHERE " + fieldName + " = ? LIMIT 1";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, value);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToEntity(rs));
            }
        } catch (SQLException e) {
            logger.error("Chyba pri vyhľadávaní entity podľa {} = {}. SQL: {}. Chyba: {}", fieldName, value, sql, e.getMessage());
            throw new DatabaseException("Chyba pri vyhľadávaní entity podľa poľa", e);
        } catch (Exception e) {
            logger.error("Neočakávaná chyba pri vyhľadávaní entity podľa {} = {}. SQL: {}. Chyba: {}", fieldName, value, sql, e.getMessage());
            throw new DatabaseException("Neočakávaná chyba pri vyhľadávaní entity podľa poľa", e);
        }
        return Optional.empty();
    }

    // Pomocné metódy
    private List<String> getFieldNames(Field[] fields) {
        return List.of(fields).stream().map(Field::getName).toList();
    }

    private void setStatementParams(PreparedStatement stmt, Field[] fields, T entity, List<String> fieldNames) throws IllegalAccessException, SQLException {
        int index = 1;
        for (Field field : fields) {
            field.setAccessible(true);
            // Skip the 'id' field from the parameters
            if ("id".equals(field.getName())) {
                continue;
            }

            Object paramValue = field.get(entity);
            if (fieldNames.contains(field.getName())) {
                stmt.setObject(index++, paramValue);
                logger.debug("Nastavujeme parameter {} = {}", field.getName(), paramValue);
            }
        }
    }

    private T mapResultSetToEntity(ResultSet rs) throws Exception {
        T entity = entityClass.getDeclaredConstructor().newInstance();
        for (Field field : entityClass.getDeclaredFields()) {
            field.setAccessible(true);
            field.set(entity, rs.getObject(field.getName()));
        }
        return entity;
    }
}