package com.eipna.appareal.data;

public class CartItem {

    private int ID;
    private int userID;
    private int productID;
    private int quantity;

    public CartItem() {
        this.ID = -1;
        this.userID = -1;
        this.productID = -1;
        this.quantity = -1;
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

    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}