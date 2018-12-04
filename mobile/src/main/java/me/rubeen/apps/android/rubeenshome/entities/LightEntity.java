package me.rubeen.apps.android.rubeenshome.entities;

public class LightEntity {
    String name;
    int photoId;
    int id;

    public LightEntity(String name, int photoId, int id) {
        this.name = name;
        this.photoId = photoId;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getPhotoId() {
        return photoId;
    }

    public int getId() {
        return id;
    }
}
