package com.deligo.Model;

import java.util.List;

public class OrgDetails {
    private int id;
    private List<List<String>> openingTimes;
    private String phoneNumber;
    private String mail;

    public OrgDetails() {
    }

    public OrgDetails(List<List<String>> openingTimes, String phoneNumber, String mail) {
        this.openingTimes = openingTimes;
        this.phoneNumber = phoneNumber;
        this.mail = mail;
    }

    public List<List<String>> getOpeningTimes() {
        return openingTimes;
    }

    public void setOpeningTimes(List<List<String>> openingTimes) {
        this.openingTimes = openingTimes;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setEmail(String email) {
        this.mail = email; // Make sure 'email' field exists in your class
    }
    
    public int getId() {
        return id; // Make sure 'id' field exists in your class
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    @Override
    public String toString() {
        return "OrgDetails{" +
                "openingTimes=" + openingTimes +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", mail='" + mail + '\'' +
                '}';
    }
}
