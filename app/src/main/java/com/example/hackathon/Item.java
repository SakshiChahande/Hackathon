package com.example.hackathon;

public class Item {
    private String name;
    private int code;
    private int quantity;

    public Item(String name, int code, int quantity) {
        this.name = name;
        this.code = code;
        this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}

