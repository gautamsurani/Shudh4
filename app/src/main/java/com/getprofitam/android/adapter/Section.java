package com.getprofitam.android.adapter;


public class Section {

    private final String catID;
    private final String name;
    private final String icon;
    private final String subcat;

    public boolean isExpanded;

    public Section(String catID, String name, String icon, String subcat) {
        this.catID = catID;
        this.name = name;
        this.icon = icon;
        this.subcat = subcat;
    }

    public String getCatID() {
        return catID;
    }

    public String getIcon() {
        return icon;
    }

    public String getSubcat() {
        return subcat;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }

    public String getName() {
        return name;
    }
}
