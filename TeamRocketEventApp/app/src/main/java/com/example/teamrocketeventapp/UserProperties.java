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

    public UserProperties(){

    }

    public UserProperties(String username, String email, String bday, String address) {
        this.username = username;
        this.email = email;
        this.bday = bday;
        this.address = address;
        this.eventsList= new ArrayList<>();
    }

    public String getUsername() {
        return this.username;
    }

    public String getAddress (){
        return this.address;
    }
    public List<String> getEventsList() {
        return this.eventsList;
    }
}
