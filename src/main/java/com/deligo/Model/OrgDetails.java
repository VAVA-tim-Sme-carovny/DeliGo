package com.deligo.Model;

import java.util.List;

public class OrgDetails {
    private int id;
    private String openingTimes;
    private String phone;
    private String email;

    public OrgDetails() {
    }

    public OrgDetails(String openingTimes, String phone, String email) {
        this.openingTimes = openingTimes;
        this.phone = phone;
        this.email = email;
    }

    public String getOpeningTimes() {
        return openingTimes;
    }

    public void setOpeningTimes(String openingTimes) {
        this.openingTimes = openingTimes;
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
                "openingTimes=" + openingTimes +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
