package me.rubeen.apps.android.rubeenshome.entities;

public class LightEntity {
    String name;
    String imageSrc;
    int id;
    boolean automaticActivated;
    String server;

    public LightEntity(String name, String imageSrc, int id, boolean automaticActivated, String server) {
        this.name = name;
        this.imageSrc = imageSrc;
        this.id = id;
        this.automaticActivated = automaticActivated;
        this.server = server;
    }

    public String getName() {
        return name;
    }

    public String getImageSrc() {
        return imageSrc;
    }

    public int getId() {
        return id;
    }

    public boolean isAutomaticActivated() {
        return automaticActivated;
    }

    public String getServer() {
        return server;
    }

}
