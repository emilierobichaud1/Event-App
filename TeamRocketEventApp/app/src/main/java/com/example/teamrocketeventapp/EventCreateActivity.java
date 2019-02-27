package com.example.teamrocketeventapp;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EventCreateActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    private Button submitButton;
    private EditText eventNameText;
    private EditText dateText;
    private EditText timeText;
    private EditText locationText;
    private Spinner categorySpinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_create);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        //Get parts of the layout
        submitButton = (Button) findViewById(R.id.submitButton);
        eventNameText = (EditText) findViewById(R.id.eventNameEditText);
        dateText = (EditText) findViewById(R.id.dateEditText);
        timeText = (EditText) findViewById(R.id.timeEditText);
        locationText = (EditText) findViewById(R.id.locationEditText);
        categorySpinner = (Spinner) findViewById(R.id.categorySpinner);

        categorySpinner.setOnItemSelectedListener(this);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getCategoriesList());
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(dataAdapter);

        submitButton.setOnClickListener(this);
    }

    private void createEvent() {
        //Get info from fields
        String eventName = eventNameText.getText().toString().trim();
        String date = dateText.getText().toString().trim();
        String time = timeText.getText().toString().trim();
        String location = locationText.getText().toString().trim();
        String category = categorySpinner.getSelectedItem().toString();

        //Error check user input here
        if (TextUtils.isEmpty(eventName)) {
            Toast.makeText(this, "Please enter event name", Toast.LENGTH_SHORT).show();  //Toast is popup msg at bottom
            return; //Return to stop registration
        }
        if (TextUtils.isEmpty(date)) {
            Toast.makeText(this, "Please enter date", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(time)) {
            Toast.makeText(this, "Please enter time", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(location)) {
            Toast.makeText(this, "Please enter location", Toast.LENGTH_SHORT).show();
            return;
        }
        if (category.equals("Category")) {
            Toast.makeText(this, "Please select category", Toast.LENGTH_SHORT).show();
            return;
        }

        String id = UUID.randomUUID().toString();

        List<Double> coordinates = new ArrayList<>();

        try {
            coordinates = addressToCoordinates(location);
        } catch (IOException e) {
            e.printStackTrace();
        }

        EventProperties eventProperties = new EventProperties(eventName, date, time, location, coordinates, category, id);

        String node = "events/" + id;

        user = mAuth.getCurrentUser();

        eventProperties.addAttendee(user.getUid()); //adds host to first index of attendees list

        myRef.child(node).setValue(eventProperties);


        Toast.makeText(EventCreateActivity.this, "Event successfully created", Toast.LENGTH_SHORT).show();

        updateView(null);

    }

    //Create an array of all categories
    private List<String> getCategoriesList() {
        List<String> categoriesList = Arrays.asList("Category",
                "Art",
                "Career",
                "Causes",
                "Educational",
                "Film",
                "Fitness",
                "Food",
                "Games",
                "Literature",
                "Music",
                "Religion",
                "Social",
                "Tech",
                "Other");
        return categoriesList;

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

    @Override
    public void onClick(View view) {
        if (view == submitButton) {
            createEvent();
        }
    }

    //method id called upon sucessful event creation
    public void updateView(View view) {
        Intent intent = new Intent(this, EventActivity.class);
        startActivity(intent);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
        String item = adapterView.getItemAtPosition(pos).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
