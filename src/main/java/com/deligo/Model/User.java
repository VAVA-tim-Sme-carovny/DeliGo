package com.deligo.Model;

import java.util.ArrayList;
import java.util.List;
import com.deligo.Model.BasicModels.Roles;

public class User {
    private int id;
    private String username;
    private String password;
    private String role;
//    private List<String> tags;

    public User() {}

    public User(String username, String password, String roles/*, List<String> tags*/) {
        this.username = username;
        this.password = password;
        this.role = roles;
//        this.tags = tags;
        /*for (Roles r : role) {
            if(this.role.isEmpty()) {
                this.role = r.toString();
            }
            else {
                this.role = this.role.concat("," + r.toString());
            }
        }*/
    }
    public String getRoles() {
        return role;
    }

    public void setRoles(String roles) {
        this.role = roles;
    }

//    public List<String> getTags() {
//        return tags;
//    }
//
//    public void setTags(List<String> tags) {
//        this.tags = tags;
//    }

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


    /*public List<Roles> getUserRoles() {
        List<Roles> rolesList = new ArrayList<>();
        if (role == null || role.isEmpty()) {
            return rolesList;
        }

        String[] parts = role.split(",");
        for (String part : parts) {
            Roles r = Roles.valueOf(part.trim());
            if (r != null) {
                rolesList.add(r);
            }
        }

        return rolesList;
    }*/


}