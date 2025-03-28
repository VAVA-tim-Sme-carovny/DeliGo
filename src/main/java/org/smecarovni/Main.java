package org.smecarovni;

import org.smecarovni.dao.GenericDAO;
import org.smecarovni.db.DatabaseConnector;
import org.smecarovni.example.Login;
import org.smecarovni.example.Register;
import org.smecarovni.example.Users;
import org.smecarovni.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
            // Vytvorenie generického DAO pre User entitu
            GenericDAO<Users> userDAO = new GenericDAO<>(Users.class, "users");

            // Inštancia triedy Register a Login
            Register register = new Register(userDAO);
            Login login = new Login(userDAO);

            // Interaktívny výber akcie
            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.println("\nVyberte akciu:");
                System.out.println("1. Registrácia");
                System.out.println("2. Prihlásenie");
                System.out.println("3. Konec");
                System.out.print("Zadajte voľbu: ");
                int choice = Integer.parseInt(scanner.nextLine());

                if (choice == 1) {
                    // Volanie registrácie
                    register.registerUser();
                } else if (choice == 2) {
                    // Volanie prihlásenia
                    login.loginUser();
                } else if (choice == 3) {
                    System.out.println("Ukončovanie aplikácie...");
                    break;
                } else {
                    System.out.println("Neplatná voľba, skúste to znova.");
                }
            }
    }
}
