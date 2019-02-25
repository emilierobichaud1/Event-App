/**
 * Class is used to save user info in database
 * Firebase uses Java object to save data
 */

package com.example.teamrocketeventapp;

import java.util.ArrayList;
import java.util.List;

public class EventProperties {
    public String name;
    public String date;
    public String time;
    public String location;
    public String category;
    public String id;
    public List<String> attendees;

    public EventProperties() {

    }

    public EventProperties(String name, String date, String time, String location, String category, String id) {
        this.name = name;
        this.date = date;
        this.time = time;
        this.location = location;
        this.category=category;
        this.id = id;
        this.attendees = new ArrayList<>();
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

    public String getId() {
        return id;
    }

    public void addAttendee(String userId){
        //need to change to add to firebase
        attendees.add(userId);
    }

    public String toString() {
        return name;
    }

}


