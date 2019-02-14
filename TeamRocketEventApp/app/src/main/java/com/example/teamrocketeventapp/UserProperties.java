/**
 * Class is used to save user info in database
 * Firebase uses Java object to save data
 */

package com.example.teamrocketeventapp;

public class UserProperties {
    public String username;
    public String email;
    public String bday;
    public String address;

    public UserProperties(){

    }

    public UserProperties(String username, String email, String bday, String address) {
        this.username = username;
        this.email = email;
        this.bday = bday;
        this.address = address;
    }

    public String getUsername() {
        return this.username;
    }

    public String getAddress (){
        return this.address;
    }
}
