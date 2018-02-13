package com.shudh4sure.shopping.model;

import java.io.Serializable;

/**
 * Created by welcome on 23-08-2016.
 */
public class MyOrderModel implements Serializable {



    String orderID = "";
    String orderNo = "";
    String date = "";
    String status = "";
    String amount = "";

    public MyOrderModel(String orderID, String orderNo, String date, String status, String amount) {

        this.orderID = orderID;
        this.orderNo = orderNo;
        this.date = date;
        this.status = status;
        this.amount = amount;
    }


    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
