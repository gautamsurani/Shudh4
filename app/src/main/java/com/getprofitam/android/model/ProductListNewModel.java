package com.getprofitam.android.model;

/**
 * Created by welcome on 21-01-2017.
 */

public class ProductListNewModel {
    String productID;
    String name;
    String image;
    String sold_out;


    public ProductListNewModel(String productID, String name, String image, String sold_out) {
        this.productID = productID;
        this.name = name;
        this.image = image;
        this.sold_out = sold_out;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSold_out() {
        return sold_out;
    }

    public void setSold_out(String sold_out) {
        this.sold_out = sold_out;
    }
}
