package org.smecarovni;

import org.smecarovni.dao.UserDAO;
import org.smecarovni.exceptions.DatabaseException;
import org.mindrot.jbcrypt.BCrypt;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        UserDAO userDAO = new UserDAO();
        Scanner scanner = new Scanner(System.in);

        // Registrácia používateľa
        System.out.println("Registrácia:");
        System.out.print("Zadajte používateľské meno: ");
        String username = scanner.nextLine();
        System.out.print("Zadajte email: ");
        String email = scanner.nextLine();
        System.out.print("Zadajte heslo: ");
        String rawPassword = scanner.nextLine();
        String hashedPassword = hashPassword(rawPassword);

        // Vytvorenie používateľa
        try {
            userDAO.createUser(username, email, hashedPassword);
            System.out.println("Používateľ bol úspešne vytvorený.");
        } catch (DatabaseException e) {
            System.err.println("Chyba pri vytváraní používateľa: " + e.getMessage());
        }

        // Prihlásenie používateľa
        System.out.println("\nPrihlásenie:");
        System.out.print("Zadajte používateľské meno: ");
        String loginUsername = scanner.nextLine();
        System.out.print("Zadajte heslo: ");
        String loginPassword = scanner.nextLine();

        // Overenie prihlasenia
        int userId = userDAO.authenticateUser(loginUsername, loginPassword);
        if (userId != -1) {
            System.out.println("Úspešne ste prihlásení.");
            // Admin môže upravovať roly iných používateľov
            System.out.print("Zadajte id používateľa, ktorému chcete zmeniť rolu: ");
            int targetUserId = Integer.parseInt(scanner.nextLine());
            System.out.print("Zadajte novú rolu (admin, manager, customer, cook, waiter): ");
            String newRole = scanner.nextLine();

            try {
                userDAO.updateUserRole(targetUserId, newRole, userId);  // Práva na zmenu roly len ak je admin
                System.out.println("Rola bola úspešne zmenená.");
            } catch (SecurityException e) {
                System.err.println("Chyba: " + e.getMessage());
            }
        } else {
            System.out.println("Nesprávne používateľské meno alebo heslo.");
        }

        scanner.close();
    }

    public static String hashPassword(String rawPassword) {
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt());
    }
}
