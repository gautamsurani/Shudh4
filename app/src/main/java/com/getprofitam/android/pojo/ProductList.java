
package com.getprofitam.android.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProductList {

    @SerializedName("productID")
    @Expose
    private String productID;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("caption")
    @Expose
    private String caption;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("price_list")
    @Expose
    private List<PriceList> priceList = null;
    @SerializedName("sold_out")
    @Expose
    private String soldOut;
    @SerializedName("listPos")
    @Expose
    private int listPos;

    @SerializedName("prodQuan")
    @Expose
    private int prodQuan;

    public int getListPos() {
        return listPos;
    }

    public void setListPos(int listPos) {
        this.listPos = listPos;
    }


    public int getProdQuan() {
        return prodQuan;
    }

    public void setProdQuan(int prodQuan) {
        this.prodQuan = prodQuan;
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

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<PriceList> getPriceList() {
        return priceList;
    }

    public void setPriceList(List<PriceList> priceList) {
        this.priceList = priceList;
    }

    public String getSoldOut() {
        return soldOut;
    }

    public void setSoldOut(String soldOut) {
        this.soldOut = soldOut;
    }

}
