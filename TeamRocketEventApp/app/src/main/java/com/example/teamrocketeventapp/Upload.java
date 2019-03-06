package com.example.teamrocketeventapp;

public class Upload {
    private String mImageUrl;
    private String name;

    public Upload() {}

    public Upload(String imageUrl, String newName) {
        mImageUrl = imageUrl;
        name = newName;
    }

    public String getName() {
        return name;
    }

    public void setName(String newName) {
        name = newName;
    }

    public String getImageUrl() {
        return mImageUrl;
    }
    public void setImageUrl(String newUrl) {
        mImageUrl = newUrl;
    }
}
