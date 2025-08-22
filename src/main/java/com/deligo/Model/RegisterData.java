package com.deligo.Model;
import java.util.List;

public class RegisterData {
    private String username;
    private String password;
    private List<String> roles;
    private List<String> tag;

    RegisterData(){}

    RegisterData(String username, String password, List<String> roles, List<String> tag) {
        this.username = username;
        this.password = password;
        this.roles = roles;
        this.tag = tag;
    }

    public String getUsername() {
        return username;
    }

    public List<String> getRoles() {
        return roles;
    }

    public List<String> getTag() {
        return tag;
    }

    public void setTag(List<String> tag) {
        this.tag = tag;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "registerData{" +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                "role=" + roles +
                "tag=" + tag +
                '}';
    }
}