package com.mrserviceman.messenger.Model.ChatFragmentsModels;

public class User {
    private String id;
    private String userName;
    private String name;
    private String ImageUrl;
    private String about;
    private String email;
    private String num;
    public User(){

    }

    public User(String id, String userName, String name, String imageUrl, String about, String email, String num) {
        this.id = id;
        this.userName = userName;
        this.name = name;
        ImageUrl = imageUrl;
        this.about = about;
        this.email = email;
        this.num = num;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }
}
