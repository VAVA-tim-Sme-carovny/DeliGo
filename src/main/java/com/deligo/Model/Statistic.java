package com.deligo.Model;

import java.time.LocalDate;
import java.util.Map;

public class Statistic {
    private int id;
    private LocalDate date;
    private double dailyRevenue;
    private Map<String, Integer> itemsSold;
    
    public Statistic() {}
    
    public Statistic(LocalDate date, double dailyRevenue, Map<String, Integer> itemsSold) {
        this.date = date;
        this.dailyRevenue = dailyRevenue;
        this.itemsSold = itemsSold;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public LocalDate getDate() {
        return date;
    }
    
    public void setDate(LocalDate date) {
        this.date = date;
    }
    
    public double getDailyRevenue() {
        return dailyRevenue;
    }
    
    public void setDailyRevenue(double dailyRevenue) {
        this.dailyRevenue = dailyRevenue;
    }
    
    public Map<String, Integer> getItemsSold() {
        return itemsSold;
    }
    
    public void setItemsSold(Map<String, Integer> itemsSold) {
        this.itemsSold = itemsSold;
    }
} 