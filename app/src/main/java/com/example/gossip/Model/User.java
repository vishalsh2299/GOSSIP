package com.example.gossip.Model;

public class User {
    private String id;
    private String username;
    private String imageURL;
    private String status;//for telling wether the user is online or offline
    private String search;

//right click and select generate set constructor for all
    public User(String id, String username, String imageURL, String status, String search) {
        this.id = id;
        this.username = username;
        this.imageURL = imageURL;
        this.status= status;
        this.search = search;
    }
    //right click and select generate set constructor and select none
    public User() {

    }
    //right click and select generate set set and getter

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }
}
