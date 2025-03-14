package com.frontend.controllers;

import com.frontend.models.User;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.io.OutputStream;

public class MainController {
    @FXML
    private TextField usernameField;

    public void handleSubmit() {
        String username = usernameField.getText();
        if (username.isEmpty()) {
            showAlert("Error", "Username cannot be empty!");
            return;
        }

        User user = new User(username, username + "@example.com", "User");
        sendUserToBackend(user);
    }

    private void sendUserToBackend(User user) {
        try {
            URL url = new URL("http://localhost:8080/api/be/registerUser");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String jsonInputString = "{\"username\": \"" + user.getUsername() + "\", \"email\": \"" + user.getEmail() + "\", \"role\": \"" + user.getRole() + "\"}";
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            if (conn.getResponseCode() == 200) {
                showAlert("Success", "User registered successfully!");
            } else {
                showAlert("Error", "Failed to register user.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.show();
    }
}