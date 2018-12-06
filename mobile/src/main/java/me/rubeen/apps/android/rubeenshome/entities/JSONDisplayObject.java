package me.rubeen.apps.android.rubeenshome.entities;

import com.fasterxml.jackson.annotation.JsonGetter;

public class JSONDisplayObject {
    private int id;
    private String imageSrc;
    private String name;
    private boolean autoEnabled;
    private String host;

    public JSONDisplayObject(int id, String imageSrc, String name, boolean autoEnabled, String host) {
        this.id = id;
        this.imageSrc = imageSrc;
        this.name = name;
        this.autoEnabled = autoEnabled;
        this.host = host;
    }

    @JsonGetter
    public int getId() {
        return id;
    }

    @JsonGetter
    public String getImageSrc() {
        return imageSrc;
    }

    @JsonGetter
    public String getName() {
        return name;
    }

    @JsonGetter
    public boolean isAutomaticActivated() {
        return autoEnabled;
    }

    public String getHost() {
        return host;
    }
}
