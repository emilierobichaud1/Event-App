package com.example.teamrocketeventapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EventActivity extends AppCompatActivity {

    EventProperties event;
    UserProperties currentUser;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private String eventId;
    private List<Double> coordinates = new ArrayList<>();
    private String hostName;
    private UserProperties hostUser;
    private ImageView eventPic;

    //needed to pull data from the database
    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        //method that activates upon query
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    event = snapshot.getValue(EventProperties.class);
                    getHostName();
                    if (currentUserIsHost()) {
                        Button cancelButton = findViewById(R.id.cancelButton);
                        cancelButton.setVisibility(View.VISIBLE);
                        Button editButton = findViewById(R.id.editButton);
                        editButton.setVisibility(View.VISIBLE);
                    }
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
        eventPic = (ImageView) findViewById(R.id.eventHeaderImage);

        //navbar stuff
        BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
                = item -> {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    Intent intent1 = new Intent(this, HomePageActivity.class);
                    startActivity(intent1);
                    return true;
                case R.id.navigation_events:
                    Intent intent3 = new Intent(this, EventIndexActivity.class);
                    startActivity(intent3);
                    return true;
                case R.id.navigation_profile:
                    //mTextMessage.setText(R.string.profile);
                    Intent intent2 = new Intent(this, UserProfileActivity.class);
                    startActivity(intent2);
                    return true;
            }
            return false;
        };
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.getMenu().getItem(1).setChecked(true);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        if (b != null) {
            //get the eventproperties object
            eventId = (String) b.get("eventid");

            DatabaseReference eventsRef = database.getReference("events");
            Query query = eventsRef.orderByChild("id").equalTo(eventId);
            query.addListenerForSingleValueEvent(valueEventListener);
        }
    }

    private boolean currentUserIsHost() {
        String hostId = event.attendees.get(0);
        return user.getUid().equals(hostId);
    }

    //activates upon button click
    public void addAttendee(View view) {
        ValueEventListener attendeeListener = new ValueEventListener() {
            @Override
            //method that activates upon query
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        // Get user from database and use the values to update the UI
                        currentUser = snapshot.getValue(UserProperties.class);

                        if (currentUser == null) {
                            continue;
                        }

                        if(!currentUser.eventsList.contains(event.getId())){ //attend event
                            event.addAttendee(user.getUid());
                            event.update();
                            currentUser.addEvent(event.getId());
                            currentUser.update();
                        }
                        else{ //unattend event
                            event.removeAttendee(currentUser.getId());
                            event.update();
                            currentUser.removeEvent(event.getId());
                            currentUser.update();
                        }
                        loadData();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        //get the UserProperties object
        Query usersQuery = database.getReference("users").orderByChild("id").equalTo(user.getUid());
        usersQuery.addListenerForSingleValueEvent(attendeeListener);

        database.getReference("events").child(eventId).setValue(event);
    }


    private void getHostName() {
        Query query2 = database.getReference("users");
        query2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (event.attendees.get(0).equals(snapshot.getRef().getKey())) {
                            UserProperties usr = snapshot.getValue(UserProperties.class);
                            if (usr != null) {
                                hostName = usr.getUsername();
                                hostUser = usr;
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

        if (!(event.picUrl.getImageUrl().isEmpty())) {
            Picasso.with(this).load(event.picUrl.getImageUrl()).into(eventPic);
        }

        if (currentUserIsHost()) {
            try {
                coordinates = addressToCoordinates(event.location);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (coordinates.get(0) == 0 && coordinates.get(1) == 0) {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
                builder.setMessage("Warning: Location could not be found on map. Edit location on event page if needed.")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        });
                android.app.AlertDialog alert = builder.create();
                alert.show();
            }
            Button cancelButton = findViewById(R.id.cancelButton);
            cancelButton.setVisibility(View.VISIBLE);
            Button editButton = findViewById(R.id.editButton);
            editButton.setVisibility(View.VISIBLE);
        }
        if(event.attendees.contains(user.getUid())){
            Button unattendButton = findViewById(R.id.signUpButton);
            unattendButton.setText("unattend event");
        }
        else{
            Button unattendButton = findViewById(R.id.signUpButton);
            unattendButton.setText("Sign up for this event");
        }

    }

    public void cancelEvent(View view) {
        if (view.getVisibility() == View.VISIBLE && event != null) {
            new AlertDialog.Builder(this)
                    .setMessage("Do you really want to cancel this event?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        event.delete();
                        Toast.makeText(EventActivity.this, "Event cancelled", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .setNegativeButton("No", null)
                    .show();
        }

    }

    public void editEvent(View view) {
        Intent intent = new Intent(this, EditEventActivity.class);
        intent.putExtra("eventid", eventId);
        startActivity(intent);
    }

    //Converts address to coordinates for easier plotting on map
    public List<Double> addressToCoordinates(String address) throws IOException {
        Geocoder geocoder = new Geocoder(this);
        List<Address> addresses = geocoder.getFromLocationName(address, 1);
        double latitude = 0, longitude = 0;

        if (addresses.size() > 0) {
            latitude = addresses.get(0).getLatitude();
            longitude = addresses.get(0).getLongitude();
        }
        List<Double> coordinates = new ArrayList<>();
        coordinates.add(latitude);
        coordinates.add(longitude);
        return coordinates;
    }


}
