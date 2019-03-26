package com.example.teamrocketeventapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class HomePageActivity extends AppCompatActivity {

    private UserProperties currentUser;
    private String uid;
    DatabaseReference eventsRef;
    private LinearLayout ll;
    private List<EventProperties> upcomingEvents = new ArrayList<EventProperties>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                // TODO: add navigation for other buttons (needs those pages implemented)
                case R.id.navigation_home:
                    return true;
                case R.id.navigation_events:
                    Intent intent3 = new Intent(this, EventIndexActivity.class);
                    startActivity(intent3);
                    return true;
                case R.id.navigation_profile:
                    Intent intent2 = new Intent(this, UserProfileActivity.class);
                    startActivity(intent2);
                    return true;
            }
            return false;
        });

        ll = (LinearLayout)findViewById(R.id.svLinearLayout);

        //load user info
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference usersRef = database.child("users").child(uid);

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentUser = dataSnapshot.getValue(UserProperties.class);
                getEventsUserIsAttending();
            }
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });


    }






    public void getEventsUserIsAttending() {


        List<String> eventList = currentUser.eventsList;

        eventsRef = FirebaseDatabase.getInstance().getReference().child("events");

        ValueEventListener eventListener = new ValueEventListener() {
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        EventProperties event = snapshot.getValue(EventProperties.class);


                        if(eventList.contains(event.getId())){
                            upcomingEvents.add(event);
                        }
                    }
                }
                displayUpcomingEvents();
            }

            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };

        eventsRef.addListenerForSingleValueEvent(eventListener);
    }


    public void sortByDate(){

    }


    public void displayUpcomingEvents(){
        //TODO: sort upcomingEvents by date, display events on page
        Log.d("DAKGFAI", upcomingEvents.toString());
    }
}


