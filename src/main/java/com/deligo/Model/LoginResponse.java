package com.deligo.Model;

import java.util.List;

public class LoginResponse extends Response {
    private String username;
    private List<String> roles;

    public LoginResponse(String username, List<String> roles, String message, int status) {
        super(message, status);
        this.username = username;
        this.roles = roles;
    }

    public String getUsername() {
        return username;
    }

    public List<String> getRoles() {
        return roles;
    }
}
