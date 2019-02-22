package com.example.teamrocketeventapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class EventActivity extends AppCompatActivity {

    EventProperties event;
    private String eventId;

    //needed to pull data from the database
    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        //method that activates upon query
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if(dataSnapshot.exists()){
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    event = snapshot.getValue(EventProperties.class);
                    Log.d("AAAAAZZZ", event.getName());
                    loadData();
                }
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        Bundle b = getIntent().getExtras();

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                // TODO: add navigation for other buttons (needs those pages implemented)
                case R.id.navigation_events:
                    // Switch to event index when "Events" button is pressed
                    Intent intent = new Intent(this, EventIndexActivity.class);
                    startActivity(intent);
                    return true;
            }
            return false;
        });

        if(b != null){
            //get the eventproperties object
            eventId = (String) b.get("eventid");

            DatabaseReference eventsRef = FirebaseDatabase.getInstance().getReference("events");
            Query query = FirebaseDatabase.getInstance().getReference("events").orderByChild("id").equalTo(eventId);
            query.addListenerForSingleValueEvent(valueEventListener);
        }
    }


    private void loadData(){
        //sets name textbox
        TextView usernameTextView = findViewById(R.id.eventName);
        usernameTextView.setText(event.name);
    }

}
