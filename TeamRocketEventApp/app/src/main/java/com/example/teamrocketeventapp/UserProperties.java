/**
 * Class is used to save user info in database
 * Firebase uses Java object to save data
 */

package com.example.teamrocketeventapp;

import java.util.ArrayList;
import java.util.List;

public class UserProperties {
    public String username;
    public String email;
    public String bday;
    public String address;
    public List<String> eventsList;
    public String id;

    public UserProperties(){

    }

    public UserProperties(String username, String email, String bday, String address, String id) {
        this.username = username;
        this.email = email;
        this.bday = bday;
        this.address = address;
        this.eventsList= new ArrayList<>();
        this.id = id;
    }

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

    public String toString() {
        return username;
    }

    public void addEvent(String userId) {

        eventsList.add(userId);
    }
}
