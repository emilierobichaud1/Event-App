package com.example.teamrocketeventapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
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

public class EventActivity extends AppCompatActivity {

    EventProperties event;
    UserProperties currentUser;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String eventId;
    private String hostName;
    private boolean isHost = false;

    //needed to pull data from the database
    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        //method that activates upon query
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    event = snapshot.getValue(EventProperties.class);
                    getHostName();
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
                case R.id.navigation_home:
                    // Switch to event index when "Events" button is pressed
                    Intent intent = new Intent(this, EventIndexActivity.class);
                    startActivity(intent);
                    return true;
                case R.id.navigation_profile:
                    // Switch to event index when "Events" button is pressed
                    Intent intent2 = new Intent(this, UserProfileActivity.class);
                    startActivity(intent2);
                    return true;
            }
            return false;
        });

        if (b != null) {
            //get the eventproperties object
            eventId = (String) b.get("eventid");

            DatabaseReference eventsRef = FirebaseDatabase.getInstance().getReference("events");
            Query query = eventsRef.orderByChild("id").equalTo(eventId);
            query.addListenerForSingleValueEvent(valueEventListener);
        }

        if (isHost) {
            Button cancelButton = findViewById(R.id.cancelButton);
            cancelButton.setVisibility(View.VISIBLE);
        }
    }

    public void addAttendee(View view) {
        ValueEventListener valueEventListener2 = new ValueEventListener() {
            @Override
            //method that activates upon query
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        // Get user from database and use the values to update the UI
                        currentUser = snapshot.getValue(UserProperties.class);
                        currentUser.addEvent(event.getId());
                        changeUser(currentUser);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        //get the UserProperties object
        Query usersQuery = FirebaseDatabase.getInstance().getReference("users").orderByChild("id").equalTo(user.getUid());
        usersQuery.addListenerForSingleValueEvent(valueEventListener2);

        event.addAttendee(user.getUid());


        FirebaseDatabase.getInstance().getReference("events").child(eventId).setValue(event);


    }


    private void getHostName() {
        Query query2 = FirebaseDatabase.getInstance().getReference("users");
        query2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (event.attendees.get(0).equals(snapshot.getRef().getKey())) {
                            UserProperties usr = snapshot.getValue(UserProperties.class);
                            if (usr != null) {
                                hostName = usr.getUsername();
                                isHost = user.getUid().equals(usr.getId());
                            }
                            loadData();
                        }

                    }
                }
            }

            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void changeUser(UserProperties user) {
        FirebaseDatabase.getInstance().getReference("users").child(user.getId()).setValue(user);
    }

    private void changeEvent(EventProperties event) {
        FirebaseDatabase.getInstance().getReference("events").child(event.getId()).setValue(event);
    }

    private void loadData() {
        //sets textboxex
        TextView nameTextView = findViewById(R.id.eventName);
        TextView dateTextView = findViewById(R.id.date);
        TextView numAttendeesTextView = findViewById(R.id.attendeeCount);
        TextView timeTextView = findViewById(R.id.time);
        TextView locationTextView = findViewById(R.id.location);
        TextView hostTextView = findViewById(R.id.host);

        nameTextView.setText(event.name);
        dateTextView.setText(event.date);
        numAttendeesTextView.setText("# of Attendees: " + event.attendees.size());
        timeTextView.setText("Time: " + event.time);
        locationTextView.setText("Location: " + event.location);
        hostTextView.setText("Host: " + hostName);
    }

    private void unregister(String userId) {
        DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference("users");

        Query usersQuery = usersReference.orderByChild("id").equalTo(userId);
        usersQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        UserProperties user = snapshot.getValue(UserProperties.class);
                        if (user != null) {
                            user.removeEvent(event.getId());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        event.removeAttendee(userId);
    }

    public void cancelEvent(View view) {
        if (view.getVisibility() == View.VISIBLE) {
            new AlertDialog.Builder(this)
                    .setMessage("Do you really want to cancel this event?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        for (String userId : event.attendees) {
                            unregister(userId);
                        }
                        FirebaseDatabase.getInstance().getReference("events").child(eventId).removeValue();
                        Toast.makeText(EventActivity.this, "Event cancelled", Toast.LENGTH_SHORT).show();
                        openEventIndex(null);
                    })
                    .setNegativeButton("No", null)
                    .show();
        }

    }

    public void openEventIndex(View view) {
        Intent intent = new Intent(this, EventIndexActivity.class);
        startActivity(intent);
    }

}
