package com.getprofitam.android.model;

/**
 * Created by welcome on 17-08-2016.
 */
public class SubCategoryModel
{

    // For Header..
    String catID = "";
    String name = "";
    String icon = "";

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


    // For Child..

    String childcatId = "";
    String childcatName = "";
    String childcaticon = "";


    public String getChildcatId() {
        return childcatId;
    }

    public void setChildcatId(String childcatId) {
        this.childcatId = childcatId;
    }

    public String getChildcatName() {
        return childcatName;
    }

    public void setChildcatName(String childcatName) {
        this.childcatName = childcatName;
    }

    public String getChildcaticon() {
        return childcaticon;
    }

    public void setChildcaticon(String childcaticon) {
        this.childcaticon = childcaticon;
    }
}
