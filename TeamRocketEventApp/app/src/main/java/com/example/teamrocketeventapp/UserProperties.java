/*
  Class is used to save user info in database
  Firebase uses Java object to save data
 */

package com.example.teamrocketeventapp;

import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class UserProperties {
    public String username;
    public String email;
    public String bday;
    public String address;
    public List<String> eventsList;
    public List<String> preferences;
    public Upload picUrl;
    public String id;

    public UserProperties(){

    }

    public UserProperties(String username, String email, String bday, String address, String id) {
        this.username = username;
        this.email = email;
        this.bday = bday;
        this.address = address;
        this.preferences = new ArrayList<>();
        this.eventsList= new ArrayList<>();
        this.picUrl = new Upload();

        this.id = id;
    }

    public void setUsername(String newName) {  this.username=newName; }
    public void setEmail(String newEmail) { this.email=newEmail;}
    public void setBday(String newBday) { this.bday=newBday;}

    public void setAddress (String newAddress){
        this.address=newAddress;
    }
    public void setId (String newId){
        this.id=newId;
    }
    public void setEventsList(List<String> newEvents) {
        this.eventsList=newEvents;
    }
    public void setPreferences(List<String> newPreferences) { this.preferences=newPreferences; }

    public String getUsername() {
        return this.username;
    }
    public String getEmail() { return this.email;}
    public String getBday() { return this.bday;}

    public String getAddress (){
        return this.address;
    }
    public String getId (){
        return this.id;
    }
    public List<String> getEventsList() {
        return this.eventsList;
    }
    public List<String> getPreferences() { return this.preferences; }

    public void addPic(Upload newUpload) {
        this.picUrl = newUpload;
    }

    public void addPreferences(String newPreference) {
        preferences.add(newPreference);
    }
    public String toString() {
        return username;
    }

    public void addEvent(String eventId) {
        eventsList.add(eventId);
    }

    public void removeEvent(String eventId) {
        eventsList.remove(eventId);
    }

    public void update() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference("users").child(id).setValue(this);
    }
}
