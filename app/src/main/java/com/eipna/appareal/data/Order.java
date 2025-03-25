package com.eipna.appareal.data;

public class Order {
    private int ID;
    private int userID;
    private String status;
    private double total;
    private String date;
    private String items;

    public Order() {
        this.ID = -1;
        this.userID = -1;
        this.status = null;
        this.total = -1;
        this.date = null;
        this.items = null;
    }

    public String getItems() {
        return items;
    }

    public void setItems(String items) {
        this.items = items;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}