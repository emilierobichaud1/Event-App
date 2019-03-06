package com.example.teamrocketeventapp;

import com.google.android.gms.maps.model.BitmapDescriptor;

public class Category {
    private String name;
    private BitmapDescriptor markerIcon;

    public Category(String name, BitmapDescriptor markerIcon) {
        this.name = name;
        this.markerIcon = markerIcon;
    }

    public String getName() {
        return name;
    }

    public BitmapDescriptor getMarkerIcon() {
        return markerIcon;
    }
}
