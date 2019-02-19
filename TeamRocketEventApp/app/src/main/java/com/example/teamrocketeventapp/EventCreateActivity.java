package com.example.teamrocketeventapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.UUID;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EventCreateActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseDatabase database;
    private DatabaseReference myRef;

    private Button submitButton;
    private EditText eventNameText;
    private EditText dateText;
    private EditText timeText;
    private EditText locationText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

        submitButton.setOnClickListener(this);
    }

    private void createEvent(){
        //Get info from fields
        String eventName = eventNameText.getText().toString().trim();
        String date = dateText.getText().toString().trim();
        String time = timeText.getText().toString().trim();
        String location = locationText.getText().toString().trim();

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

        String id = UUID.randomUUID().toString();

        EventProperties eventProperties = new EventProperties(eventName,date,time,location, id);

        String node = "events/" + id;

        myRef.child(node).setValue(eventProperties);


        Toast.makeText(EventCreateActivity.this, "Event successfully created", Toast.LENGTH_SHORT).show();

        updateView(null);

    }

    @Override
    public void onClick(View view) {
        if (view == submitButton) {
            createEvent();
        }
    }

    //method id called upon sucessful event creation
    public void updateView (View view){
        Intent intent = new Intent(this, EventActivity.class);
        startActivity(intent);
    }
}
