package com.getprofitam.android.model;

import java.io.Serializable;

/**
 * Created by welcome on 21-01-2017.
 */

public class PricelistModel  implements Serializable {

    String sr;
    String price;
    String priceID;
    String mrp;
    String weight;
    String dis;
    private int listPos;

    public PricelistModel(String sr, String priceID, String price, String mrp, String weight, String dis) {
        this.sr = sr;
        this.price = price;
        this.priceID = priceID;
        this.mrp = mrp;
        this.weight = weight;
        this.dis = dis;

    }


    public String getSr() {
        return sr;
    }

    public void setSr(String sr) {
        this.sr = sr;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPriceID() {
        return priceID;
    }

    public void setPriceID(String priceID) {
        this.priceID = priceID;
    }

    public String getMrp() {
        return mrp;
    }

    public void setMrp(String mrp) {
        this.mrp = mrp;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getDis() {
        return dis;
    }

    public void setDis(String dis) {
        this.dis = dis;
    }
    public int getListPos() {
        return listPos;
    }

    public void setListPos(int listPos) {
        this.listPos = listPos;
    }


}
