/*
  Class is used to save user info in database
  Firebase uses Java object to save data
 */

package com.example.teamrocketeventapp;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

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
    public List<Double> coordinates;
    public Upload picUrl;

    public EventProperties() {

    }

    public EventProperties(String name, String date, String time, String location, List<Double> coordinates, String category, String id) {
        this.name = name;
        this.date = date;
        this.time = time;
        this.location = location;
        this.category = category;
        this.coordinates = coordinates;
        this.id = id;
        this.attendees = new ArrayList<>();
        this.picUrl = new Upload();
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

    public void addAttendee(String userId) {
        //need to change to add to firebase
        attendees.add(userId);
    }

    public String toString() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public List<Double> getCoordinates() {
        return coordinates;
    }

    public void removeAttendee(String userId) {
        attendees.remove(userId);
    }

    public void update() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference("events").child(id).setValue(this);
    }

    public void delete() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersReference = database.getReference("users");

        for (String userId : attendees) {
            Query query = usersReference.orderByChild("id").equalTo(userId);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                            UserProperties user = childSnapshot.getValue(UserProperties.class);
                            if (user != null) {
                                user.removeEvent(id);
                                user.update();
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        database.getReference("events").child(id).removeValue();
    }
    public void addPic(Upload newUpload) {
        this.picUrl = newUpload;
    }
}


