package com.deligo.Model;

import java.util.List;

public class OrgDetails {
    private int id;
    private String opening_hours;
    private String phone;
    private String email;

    public OrgDetails() {
    }

    public OrgDetails(String opening_hours, String phone, String email) {
        this.opening_hours = opening_hours;
        this.phone = phone;
        this.email = email;
    }

    public String getOpeningTimes() {
        return opening_hours;
    }

    public void setOpeningTimes(String opening_hours) {
        this.opening_hours = opening_hours;
    }

    public String getPhoneNumber() {
        return phone;
    }

    public void setPhoneNumber(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    public int getId() {
        return id;
    }

    public String getMail() {
        return email;
    }

    public void setMail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "OrgDetails{" +
                "opening_hours=" + opening_hours +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
