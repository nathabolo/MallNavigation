package com.local.bdc.models;

public class Zone {

    public String name;
    public String description;
    public String level;
    public Long id;
    public Long cordinates;
    public boolean containposition;
    public int descripeContents;

    public Zone(com.customlbs.model.Zone zone, String name, String description, String level, long id, long cordinates, boolean containposition, int descripeContents) {

        this.name = name;
        this.description = description;
        this.level = level;
        this.id = id;
        this.cordinates = cordinates;
        this.containposition = containposition;
        this.descripeContents = descripeContents;
    }

    public boolean isContainposition() {
        return containposition;
    }

    public void setContainposition(boolean containposition) {
        this.containposition = containposition;
    }

    public int getDescripeContents() {
        return descripeContents;
    }

    public void setDescripeContents(int descripeContents) {
        this.descripeContents = descripeContents;
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

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getCordinates() {
        return cordinates;
    }

    public void setCordinates(long cordinates) {
        this.cordinates = cordinates;
    }
}
