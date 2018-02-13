package com.getprofitam.android.model;

/**
 * Created by welcome on 19-01-2017.
 */

public class OfferBannerModel {

    String sr;
    String name;
    String image;
    String catID;

    public OfferBannerModel(String sr, String name, String image, String catID) {
        this.sr = sr;
        this.name = name;
        this.image = image;
        this.catID = catID;
    }

    public String getSr() {
        return sr;
    }

    public void setSr(String sr) {
        this.sr = sr;
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

    public String getCatID() {
        return catID;
    }

    public void setCatID(String catID) {
        this.catID = catID;
    }
}
