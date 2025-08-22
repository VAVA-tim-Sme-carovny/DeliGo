package com.deligo.DatabaseManager.example;
import com.deligo.DatabaseManager.dao.GenericDAO;
import com.deligo.DatabaseManager.exceptions.DatabaseException;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Scanner;

public class Register {

    private final GenericDAO<Users> userDAO;

    public Register(GenericDAO<Users> userDAO) {
        this.userDAO = userDAO;
    }

    public void registerUser() {
        Scanner scanner = new Scanner(System.in);

        // Získanie údajov o používateľovi
        System.out.println("Registrácia:");
        System.out.print("Zadajte používateľské meno: ");
        String username = scanner.nextLine();
        System.out.print("Zadajte heslo: ");
        String rawPassword = scanner.nextLine();
        String hashedPassword = hashPassword(rawPassword);

        // Vytvorenie používateľa
        Users newUser = new Users(username, hashedPassword, "customer");

        // Vloženie nového používateľa
        try {
            userDAO.insert(newUser);
            System.out.println("Používateľ bol úspešne vytvorený.");
        } catch (DatabaseException e) {
            System.err.println("Chyba pri vytváraní používateľa: " + e.getMessage());
        }
    }

    private String hashPassword(String rawPassword) {
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt());
    }
}
