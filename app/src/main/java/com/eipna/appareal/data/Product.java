package com.eipna.appareal.data;

public class Product {
    private int ID;
    private String description;
    private double price;
    private int stock;
    private byte[] image;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public Product() {
        this.ID = -1;
        this.description = null;
        this.price = -1;
        this.stock = -1;
        this.image = null;
    }
}