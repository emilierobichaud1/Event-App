/**
 * Class is used to save user info in database
 * Firebase uses Java object to save data
 */

package com.example.teamrocketeventapp;

import java.util.ArrayList;

public class EventProperties {
    public String name;
    public String date;
    public String time;
    public String location;
    public String id;
    public ArrayList<String> attendees;

    public EventProperties() {

    }

    public EventProperties(String name, String date, String time, String location, String id) {
        this.name = name;
        this.date = date;
        this.time = time;
        this.location = location;
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

}


