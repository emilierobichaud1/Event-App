package com.example.teamrocketeventapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.Calendar;

public class EditEventActivity extends AppCompatActivity {
    private FirebaseDatabase database;
    DatabaseReference eventsRef;
    private String eventId;

    private EventProperties currentEvent;

    EditText eventNameEdit;
    EditText dateTextEdit;
    EditText timeTextEdit;
    EditText locationTextEdit;
    Button confirmButton;
    Button cancelButton;

    private DatePickerDialog dpd;
    private TimePickerDialog tpd;
    private Calendar c;
    private Calendar eventCal;
    private String EventDate;
    private String time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);

        database = FirebaseDatabase.getInstance();

        eventNameEdit = (EditText) findViewById(R.id.editEventName);
        dateTextEdit = (EditText) findViewById(R.id.editEventDate);
        timeTextEdit = (EditText) findViewById(R.id.editEventTime);
        locationTextEdit = (EditText) findViewById(R.id.editEventLocation);
        confirmButton = (Button) findViewById(R.id.confirmButton);
        cancelButton = (Button) findViewById(R.id.cancelButton);

        dateTextEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                c = Calendar.getInstance();
                int day = c.get(Calendar.DAY_OF_MONTH);
                int month = c.get(Calendar.MONTH);
                int year = c.get(Calendar.YEAR);
                eventCal = Calendar.getInstance();

                dpd = new DatePickerDialog(EditEventActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int mYear, int mMonth, int mDay) {

                        EventDate = mDay + "/" + (mMonth + 1) + "/" + mYear;
                        dateTextEdit.setText(EventDate);
                        eventCal.set(mYear, mMonth, mDay);

                    }
                }, year, month, day);
                dpd.show();
            }
        });

        timeTextEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR);
                int minute = c.get(Calendar.MINUTE);
                boolean is24Hour = true;

                tpd = new TimePickerDialog(EditEventActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int mHour, int mMinute) {

                        time = mHour + ":" + new DecimalFormat("00").format(mMinute);
                        timeTextEdit.setText(time);

                    }
                }, hour, minute, is24Hour);
                tpd.show();
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(eventCal.before(c)){
                    Toast.makeText(EditEventActivity.this, "Event date cannot be before current date", Toast.LENGTH_SHORT).show();  //Toast is popup msg at bottom
                    return;
                }
                editEvent(view);

            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUI(view);

            }
        });

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            //method that activates upon query
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        currentEvent = snapshot.getValue(EventProperties.class);
                        timeTextEdit.setText(currentEvent.getTime());
                        locationTextEdit.setText(currentEvent.getLocation());
                        eventNameEdit.setText(currentEvent.getName());
                        dateTextEdit.setText(currentEvent.getDate());

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        Bundle b = getIntent().getExtras();

        eventId = (String) b.get("eventid");

        DatabaseReference eventsRef = database.getReference("events");
        Query query = eventsRef.orderByChild("id").equalTo(eventId);
        query.addListenerForSingleValueEvent(valueEventListener);

    }

    private void editEvent(View view) {
        Bundle b = getIntent().getExtras();
        eventId = (String) b.get("eventid");
        eventsRef = FirebaseDatabase.getInstance().getReference().child("events").child(eventId);
        String eventName = eventNameEdit.getText().toString().trim();
        String date = dateTextEdit.getText().toString().trim();
        String time = timeTextEdit.getText().toString().trim();
        String location = locationTextEdit.getText().toString().trim();
        currentEvent.name = eventName;
        currentEvent.date = date;
        currentEvent.time = time;
        currentEvent.location = location;
        eventsRef.setValue(currentEvent);

        updateUI(view);


    }

    private void updateUI(View view) {
        Intent intent = new Intent(view.getContext(), EventActivity.class);
        intent.putExtra("eventid", eventId);
        startActivity(intent);
    }
}
