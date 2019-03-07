package com.example.teamrocketeventapp;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;

public class CategoryFactory {
    public static Category getCategory(String category) {
        switch (category) {
            case "Art":
                return new Category(category,
                        BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            case "Career":
                return new Category(category,
                        BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            case "Causes":
                return new Category(category,
                        BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
            case "Educational":
                return new Category(category,
                        BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            case "Film":
                return new Category(category,
                        BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
            case "Fitness":
                return new Category(category,
                        BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
            case "Food":
                return new Category(category,
                        BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
            case "Games":
                return new Category(category,
                        BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
            case "Literature":
                return new Category(category,
                        BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
            case "Music":
                return new Category(category,
                        BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            case "Religion":
                return new Category(category,
                        BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
            case "Social":
                return new Category(category,
                        BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            default:
                return new Category("Other", BitmapDescriptorFactory.defaultMarker());
        }
    }
}
