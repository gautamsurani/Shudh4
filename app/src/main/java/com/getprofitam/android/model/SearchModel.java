package com.getprofitam.android.model;

/**
 * Created by welcome on 17-08-2016.
 */
public class SearchModel
{


    String ID;
    String name;
    String type;

    public SearchModel(String ID, String name, String type) {
        this.ID = ID;
        this.name = name;
        this.type = type;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
