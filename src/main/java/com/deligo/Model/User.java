package com.deligo.Model;

import java.util.List;

public class User {
    private int id;
    private String username;
    private String email;
    private String password;
    private List<String> roles;
    private List<String> tags;
    
    public User() {}
    
    public User(String username, String email, String password, List<String> roles, List<String> tags) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.roles = roles;
        this.tags = tags;
    }
    
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
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public List<String> getRoles() {
        return roles;
    }
    
    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
    
    public List<String> getTags() {
        return tags;
    }
    
    public void setTags(List<String> tags) {
        this.tags = tags;
    }
} 