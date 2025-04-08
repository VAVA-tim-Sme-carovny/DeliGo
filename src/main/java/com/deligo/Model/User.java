package com.deligo.Model;

import java.util.List;

public class Users {
    private int id;
    private String username;
    private String password;
    private List<String> role;

    public Users() {}

    public Users(String username, String email, String password, List<String> role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getRole() {
        return role;
    }

    public void setRole(List<String> role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "registerData{" +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                "role=" + role +
                '}';
    }
}

