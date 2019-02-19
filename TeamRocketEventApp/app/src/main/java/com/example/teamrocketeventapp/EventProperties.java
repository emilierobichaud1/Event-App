/**
 * Class is used to save user info in database
 * Firebase uses Java object to save data
 */

package com.example.teamrocketeventapp;

public class EventProperties {
    public String name;
    public String date;
    public String time;
    public String location;
    public String id;

    public EventProperties() {

    }

    public EventProperties(String name, String date, String time, String location, String id) {
        this.name = name;
        this.date = date;
        this.time = time;
        this.location = location;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getLocation() {
        return location;
    }

}


