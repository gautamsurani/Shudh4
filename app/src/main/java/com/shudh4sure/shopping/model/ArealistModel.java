package com.shudh4sure.shopping.model;

import java.io.Serializable;

/**
 * Created by welcome on 14-10-2016.
 */
public class ArealistModel implements Serializable {
    String areaID;
    String name;
    String shipping;
    String on_order;

    public ArealistModel(String areaID, String name, String shipping, String on_order) {
        this.areaID = areaID;
        this.name = name;
        this.shipping = shipping;
        this.on_order = on_order;
    }

    public String getAreaID() {
        return areaID;
    }

    public void setAreaID(String areaID) {
        this.areaID = areaID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShipping() {
        return shipping;
    }

    public void setShipping(String shipping) {
        this.shipping = shipping;
    }

    public String getOn_order() {
        return on_order;
    }

    public void setOn_order(String on_order) {
        this.on_order = on_order;
    }
}
