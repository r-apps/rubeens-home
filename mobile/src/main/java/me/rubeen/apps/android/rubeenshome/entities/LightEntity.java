package me.rubeen.apps.android.rubeenshome.entities;

public class LightEntity {
    String name;
    String imageSrc;
    int id;
    boolean automaticActivated;
    String server;
    int red;
    int green;
    int blue;

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

    public void setRed(int red) {
        this.red = red;
    }

    public void setGreen(int green) {
        this.green = green;
    }

    public void setBlue(int blue) {
        this.blue = blue;
    }

    public void setColor(int red, int green, int blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public int getRed() {

        return red;
    }

    public int getGreen() {
        return green;
    }

    public int getBlue() {
        return blue;
    }

    public String getServer() {
        return server;
    }

}
