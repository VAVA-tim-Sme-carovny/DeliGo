package org.smecarovni.example;
import org.mindrot.jbcrypt.BCrypt;
import org.smecarovni.dao.GenericDAO;
import org.smecarovni.exceptions.DatabaseException;

import java.sql.SQLException;
import java.util.Optional;
import java.util.Scanner;

public class Login {

    private final GenericDAO<Users> userDAO;

    public Login(GenericDAO<Users> userDAO) {
        this.userDAO = userDAO;
    }

    public void loginUser() {
        Scanner scanner = new Scanner(System.in);

        // Získanie prihlasovacích údajov
        System.out.println("\nPrihlásenie:");
        System.out.print("Zadajte používateľské meno: ");
        String loginUsername = scanner.nextLine();
        System.out.print("Zadajte heslo: ");
        String loginPassword = scanner.nextLine();

        // Overenie používateľa
        Optional<Users> userOpt = userDAO.findOneByField("username", loginUsername);
        if (userOpt.isPresent()) {
            Users user = userOpt.get();
            if (BCrypt.checkpw(loginPassword, user.getPassword())) {
                System.out.println("Úspešne ste prihlásení.");

                // Ak je používateľ admin, môže meniť rolu iných používateľov
                if (user.getRole().equals("admin")) {
                    handleAdminActions(user);
                }
            } else {
                System.out.println("Nesprávne heslo.");
            }
        } else {
            System.out.println("Používateľ s týmto menom neexistuje.");
        }
    }

    private void handleAdminActions(Users loggedInUser) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Zadajte id používateľa, ktorému chcete zmeniť rolu: ");
        int targetUserId = Integer.parseInt(scanner.nextLine());
        System.out.print("Zadajte novú rolu (admin, manager, customer, cook, waiter): ");
        String newRole = scanner.nextLine();

        try {
            // Najprv získame používateľa z databázy
            Optional<Users> targetUserOpt = userDAO.getById(targetUserId);
            if (targetUserOpt.isPresent()) {
                Users targetUser = targetUserOpt.get();
                targetUser.setRole(newRole);  // Nastavíme novú rolu

                // Aktualizujeme len rolu (ale ponecháme ostatné údaje nezmenené)
                userDAO.update(targetUserId, targetUser);
                System.out.println("Rola bola úspešne zmenená.");
            } else {
                System.out.println("Používateľ s týmto id neexistuje.");
            }
        } catch (DatabaseException e) {
            System.err.println("Chyba pri zmene roly: " + e.getMessage());
        }
    }

}
