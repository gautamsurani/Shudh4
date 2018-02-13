package com.shudh4sure.shopping.model;

/**
 * Created by welcome on 20-01-2017.
 */

public class HomeCategoryModel {

    String catID;
    String name;
    String icon;

    public HomeCategoryModel(String catID, String name, String icon) {
        this.catID = catID;
        this.name = name;
        this.icon = icon;
    }

    public String getCatID() {
        return catID;
    }

    public void setCatID(String catID) {
        this.catID = catID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
