package com.deligo.Model;

public class Order {

    private int id;
    private int user_id;
    private int table_id;
    private String status;
    private String created_at;
    private String order_contain;

    public Order() {}

    public int getId() {
        return id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getTable_id() {
        return table_id;
    }

    public void setTable_id(int table_id) {
        this.table_id = table_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }


    public String getOrder_contain() {
        return order_contain;
    }

    public void setOrder_contain(String order_contain) {
        this.order_contain = order_contain;
    }

}