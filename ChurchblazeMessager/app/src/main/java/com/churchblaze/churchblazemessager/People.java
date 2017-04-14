package com.churchblaze.churchblazemessager;

/**
 * Created by John on 31-Oct-16.
 */
public class People {

    private String name, image;

    public People() {

    }

    public People(String name, String image) {
        this.name = name;

        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
