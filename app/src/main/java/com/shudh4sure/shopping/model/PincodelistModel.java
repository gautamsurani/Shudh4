package com.shudh4sure.shopping.model;

import java.io.Serializable;

/**
 * Created by welcome on 14-10-2016.
 */
public class PincodelistModel implements Serializable {

    String pincodeID;
    String name;

    public PincodelistModel(String pincodeID, String name) {
        this.pincodeID = pincodeID;
        this.name = name;
    }

    public String getPincodeID() {
        return pincodeID;
    }

    public void setPincodeID(String pincodeID) {
        this.pincodeID = pincodeID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
