package com.getprofitam.android.model;

import java.io.Serializable;

/**
 * Created by welcome on 19-01-2017.
 */

public class OfferModel implements Serializable{

    String offer;
    String product;
    String image;
    String message;
    String added_on;

    public OfferModel(String offer, String product, String image, String message, String added_on) {
        this.offer = offer;
        this.product = product;
        this.image = image;
        this.message = message;
        this.added_on = added_on;
    }

    public String getOffer() {
        return offer;
    }

    public void setOffer(String offer) {
        this.offer = offer;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAdded_on() {
        return added_on;
    }

    public void setAdded_on(String added_on) {
        this.added_on = added_on;
    }
}
