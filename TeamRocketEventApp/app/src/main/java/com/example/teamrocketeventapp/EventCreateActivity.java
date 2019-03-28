package com.example.teamrocketeventapp;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class EventCreateActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    UserProperties currentUser;
    private static final int PICK_IMAGE = 1;

    private Button submitButton;
    private EditText eventNameText;
    private EditText dateText;
    private EditText timeText;
    private EditText locationText;
    private EditText cityText;
    private EditText provinceText;
    private Spinner categorySpinner;
    private StorageReference myStorageRef;
    private DatePickerDialog dpd;
    private TimePickerDialog tpd;
    private Calendar c;
    private Calendar eventCal;
    private String eventDate;
    private String time;
    private FloatingActionButton buttonLoadPicture;
    private Uri imageUri;
    private ImageView eventImage;
    private String node;
    private EventProperties event;


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
        cityText = (EditText) findViewById(R.id.cityEditText);
        provinceText = (EditText) findViewById(R.id.provinceEditText);
        categorySpinner = (Spinner) findViewById(R.id.categorySpinner);
        buttonLoadPicture = (FloatingActionButton) findViewById(R.id.buttonLoadPicture);
        eventImage = (ImageView) findViewById(R.id.eventImage);

        categorySpinner.setOnItemSelectedListener(this);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getCategoriesList());
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(dataAdapter);

        submitButton.setOnClickListener(this);

        timeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR);
                int minute = c.get(Calendar.MINUTE);
                boolean is24Hour = true;

                tpd = new TimePickerDialog(EventCreateActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int mHour, int mMinute) {

                        time = mHour + ":" + new DecimalFormat("00").format(mMinute);
                        timeText.setText(time);

                    }
                }, hour, minute, is24Hour);
                tpd.show();
            }
        });

        dateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                c = Calendar.getInstance();
                int day = c.get(Calendar.DAY_OF_MONTH);
                int month = c.get(Calendar.MONTH);
                int year = c.get(Calendar.YEAR);
                eventCal = Calendar.getInstance();

                dpd = new DatePickerDialog(EventCreateActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int mYear, int mMonth, int mDay) {

                        eventDate = mDay + "/" + (mMonth + 1) + "/" + mYear;
                        dateText.setText(eventDate);
                        eventCal.set(mYear, mMonth, mDay);

                    }
                }, year, month, day);
                dpd.show();
            }
        });

        buttonLoadPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImage();

            }
        });
    }

    private void createEvent() {
        //Get info from fields
        String eventName = eventNameText.getText().toString().trim();
        String date = dateText.getText().toString().trim();
        String time = timeText.getText().toString().trim();
        String location = locationText.getText().toString().trim();
        String city = cityText.getText().toString().trim();
        String province = provinceText.getText().toString().trim();
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
        if (TextUtils.isEmpty(city)) {
            Toast.makeText(this, "Please enter city", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(province)) {
            Toast.makeText(this, "Please enter province", Toast.LENGTH_SHORT).show();
            return;
        }
        if (category.equals("Category")) {
            Toast.makeText(this, "Please select category", Toast.LENGTH_SHORT).show();
            return;
        }

        String id = UUID.randomUUID().toString();

        location = location + ", " + city + ", " + province; //Update location to include city and province

        List<Double> coordinates = new ArrayList<>();

        try {
            coordinates = addressToCoordinates(location);
        } catch (IOException e) {
            e.printStackTrace();
        }


        event = new EventProperties(eventName, date, time, location, coordinates, category, id);
        event.addPic(new Upload("", "default"));

        node = "events/" + id;

        user = mAuth.getCurrentUser();

        event.addAttendee(user.getUid()); //adds host to first index of attendees list
        addAttendee(null, id);

        myRef.child(node).setValue(event);

        uploadFile();


        Toast.makeText(EventCreateActivity.this, "Event successfully created", Toast.LENGTH_SHORT).show();

        updateView(null, id);

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
            if (eventCal.before(c)) {
                Toast.makeText(EventCreateActivity.this, "Event date cannot be before current date", Toast.LENGTH_SHORT).show();  //Toast is popup msg at bottom
                return;
            }
            createEvent();
        }
    }

    //method id called upon sucessful event creation
    public void updateView(View view, String eventId) {
        Intent intent = new Intent(this, EventActivity.class);
        intent.putExtra("eventid", eventId);
        startActivity(intent);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
        String item = adapterView.getItemAtPosition(pos).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void addAttendee(View view, String eventId) {
        ValueEventListener valueEventListener2 = new ValueEventListener() {
            @Override
            //method that activates upon query
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        // Get user from database and use the values to update the UI
                        currentUser = snapshot.getValue(UserProperties.class);
                        currentUser.addEvent(eventId);
                        changeUser(currentUser);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        Query usersQuery = database.getReference("users").orderByChild("id").equalTo(user.getUid());
        usersQuery.addListenerForSingleValueEvent(valueEventListener2);

    }

    private void changeUser(UserProperties user) {
        database.getReference("users").child(user.getId()).setValue(user);
    }

    public void pickImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();

            Picasso.with(this).load(imageUri).into(eventImage);
        }
    }

    private void uploadFile() {
        myStorageRef = FirebaseStorage.getInstance().getReference(node);

        if (imageUri != null) {
            myStorageRef.putFile(imageUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return myStorageRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        Log.e("logt", "then: " + downloadUri.toString());
                        Upload upload = new Upload(downloadUri.toString(), node);
                        myStorageRef = FirebaseStorage.getInstance().getReference(node);
                        event.addPic(upload);
                        myRef.child(node).setValue(event);
                    } else {
                        Toast.makeText(EventCreateActivity.this, "Upload Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
