package com.getprofitam.android.model;

import java.io.Serializable;

/**
 * Created by welcome on 19-01-2017.
 */

public class WalletModel implements Serializable{


    String OrderID;
    String Remark;
    String symbol;
    String Amount;
    String type;
    String TransactionDate;

    public WalletModel(String orderID, String remark, String symbol, String amount, String type, String transactionDate) {
        OrderID = orderID;
        Remark = remark;
        this.symbol = symbol;
        Amount = amount;
        this.type = type;
        TransactionDate = transactionDate;
    }

    public String getOrderID() {
        return OrderID;
    }

    public void setOrderID(String orderID) {
        OrderID = orderID;
    }

    public String getRemark() {
        return Remark;
    }

    public void setRemark(String remark) {
        Remark = remark;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTransactionDate() {
        return TransactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        TransactionDate = transactionDate;
    }
}
