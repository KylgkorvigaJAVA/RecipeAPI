package com.recipeapi;

public class Recipe {
    private int id;
    private String savedName;
    private String imageUrl;
    private String savedLink;

//    public Recipe() {
//
//    }

    public Recipe(String savedName, String imageUrl, String savedLink) {
        this.savedName = savedName;
        this.imageUrl = imageUrl;
        this.savedLink = savedLink;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public String getSavedName() {
        return savedName;
    }

    public void setSavedName(String savedName) {
        this.savedName = savedName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSavedLink() {
        return savedLink;
    }

    public void setSavedLink(String savedLink) {
        this.savedLink = savedLink;
    }
}
