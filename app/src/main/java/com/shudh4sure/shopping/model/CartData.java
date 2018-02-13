package com.shudh4sure.shopping.model;

import java.io.Serializable;

/**
 * Created by welcome on 14-10-2016.
 */
public class CartData implements Serializable {

    String productID;
    String name;
    String image;
    String price;
    String strPriceId;
    String mrp;
    String weight;
    public int countThis;
    String caption;
    public CartData(String string, String strPriceId, String string1, String string2, String string3, String string4, int countThis, String weight,String caption) {
        this.productID = string;
        this.strPriceId = strPriceId;
        this.name = string1;
        this.image = string2;
        this.price = string3;
        this.mrp = string4;
        this.countThis = countThis;
        this.weight = weight;
        this.caption = caption;

    }

    public int getCountThis() {
        return countThis;
    }

    public void setCountThis(int countThis) {
        this.countThis = countThis;
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getMrp() {
        return mrp;
    }

    public void setMrp(String mrp) {
        this.mrp = mrp;
    }


    public String getStrPriceId() {
        return strPriceId;
    }

    public void setStrPriceId(String strPriceId) {
        this.strPriceId = strPriceId;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }
}
