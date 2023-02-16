package com.example.smarthomeapp.Module;

public class DeviceModel {
    public String name, id, style;
    public Boolean status;

    public DeviceModel(String name, String id, String style, boolean status) {
        this.name = name;
        this.id = id;
        this.style = style;
        this.status = status;
    }

    public DeviceModel() {
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getStyle() {
        return style;
    }

    public boolean isStatus() {
        return status;
    }

}
