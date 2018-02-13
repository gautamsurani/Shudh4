package com.getprofitam.android.model;

import java.io.Serializable;

/**
 * Created by welcome on 14-10-2016.
 */
public class OrderDetailsModel implements Serializable {

    String SR;
    String name;
    String weight;
    String quantity;
    String price;
    String line_total;

    public OrderDetailsModel(String SR, String name, String weight, String quantity, String price, String line_total) {
        this.SR = SR;
        this.name = name;
        this.weight = weight;
        this.quantity = quantity;
        this.price = price;
        this.line_total = line_total;
    }

    public String getSR() {
        return SR;
    }

    public void setSR(String SR) {
        this.SR = SR;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getLine_total() {
        return line_total;
    }

    public void setLine_total(String line_total) {
        this.line_total = line_total;
    }
}

