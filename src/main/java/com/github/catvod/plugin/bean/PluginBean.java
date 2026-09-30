package com.github.catvod.plugin.bean;


import java.io.Serializable;

public class PluginBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String description;
    private boolean autoRun;

    public PluginBean() {
    }

    public PluginBean(String id, String name, String description, boolean autoRun) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.autoRun = autoRun;
    }

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isAutoRun() {
        return autoRun;
    }

    public void setAutoRun(boolean autoRun) {
        this.autoRun = autoRun;
    }

}
