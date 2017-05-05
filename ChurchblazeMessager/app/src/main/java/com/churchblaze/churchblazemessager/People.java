package com.churchblaze.churchblazemessager;

/**
 * Created by John on 31-Oct-16.
 */
public class People {

    private String name, image, date, message, status, reciever_name, reciever_image;

    public People() {

    }

    public People(String name, String image, String date, String message, String status, String reciever_name, String reciever_image) {
        this.name = name;

        this.image = image;
        this.date = date;
        this.message = message;
        this.status = status;
        this.reciever_name = reciever_name;
        this.reciever_image = reciever_image;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReciever_name() {
        return reciever_name;
    }

    public void setReciever_name(String reciever_name) {
        this.reciever_name = reciever_name;
    }

    public String getReciever_image() {
        return reciever_image;
    }

    public void setReciever_image(String reciever_image) {
        this.reciever_image = reciever_image;
    }
}
