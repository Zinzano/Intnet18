package com.kerberosstudios.flappybird;

/**
 * Created by Zinzano on 3/12/2018.
 * Class the will hold the data for a profile parameter list item
 */
public class ProfileParameter {

    private String value;
    private String name;
    private String type;
    private Boolean isEditable;
    private Boolean updated;
    private String idJSON;

    public ProfileParameter(String v, String n, String t, String id) {
        value = v;
        name = n;
        type = t;
        isEditable = false;
        updated = false;
        idJSON = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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

    public Boolean getEditable() {
        return isEditable;
    }

    public void setEditable(Boolean editable) {
        isEditable = editable;
    }

    public Boolean getUpdated() {
        return updated;
    }

    public void setUpdated(Boolean updated) {
        this.updated = updated;
    }

    public String getIdJSON() {
        return idJSON;
    }

    public void setIdJSON(String idJSON) {
        this.idJSON = idJSON;
    }
}
