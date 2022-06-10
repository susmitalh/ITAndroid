package com.locatocam.app.utils;

import java.io.Serializable;

public class ItemData implements Serializable {
    String id,name,cat_id;
    boolean isFreezed=false;
    String isFreezing;
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ItemData(String id, String name) {
        this.id = id;
        this.name = name;
    }
    public ItemData(String id, String name, String cat_id, String isFreezing) {
        this.id = id;
        this.name = name;
        this.isFreezing=isFreezing;
        this.cat_id=cat_id;
    }

    public boolean isFreezed() {
        return isFreezed;
    }

    public void setFreezed(boolean freezed) {
        isFreezed = freezed;
    }

    public String getIsFreezing() {
        return isFreezing;
    }

    public void setIsFreezing(String isFreezing) {
        this.isFreezing = isFreezing;
    }

    public String getCat_id() {
        return cat_id;
    }

    public void setCat_id(String cat_id) {
        this.cat_id = cat_id;
    }
}
