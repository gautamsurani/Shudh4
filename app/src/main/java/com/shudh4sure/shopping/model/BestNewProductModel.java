package com.shudh4sure.shopping.model;

import com.shudh4sure.shopping.pojo.PriceList;

import java.util.List;

public class BestNewProductModel {

    String productID;
    String name;
    String caption;
    String image;
    String discount;
    String price;
    String mrp;
    String sold_out;
    List<PriceList> priceList;
    private int listPos = 0;
    private int prodQuan = 0;

    public BestNewProductModel(String productID, String name, String caption, String image, String discount, String price, String mrp, String sold_out, List<PriceList> priceLists) {
        this.productID = productID;
        this.name = name;
        this.caption = caption;
        this.image = image;
        this.discount = discount;
        this.price = price;
        this.mrp = mrp;
        this.sold_out = sold_out;
        this.priceList = priceLists;
    }

    public int getProdQuan() {
        return prodQuan;
    }

    public void setProdQuan(int prodQuan) {
        this.prodQuan = prodQuan;
    }

    public int getListPos() {
        return listPos;
    }

    public void setListPos(int listPos) {
        this.listPos = listPos;
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

    public List<PriceList> getPriceList() {
        return priceList;
    }

    public void setPriceList(List<PriceList> priceList) {
        this.priceList = priceList;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
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

    public String getSold_out() {
        return sold_out;
    }

    public void setSold_out(String sold_out) {
        this.sold_out = sold_out;
    }
}
