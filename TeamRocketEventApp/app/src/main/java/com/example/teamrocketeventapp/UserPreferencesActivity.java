package com.example.teamrocketeventapp;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserPreferencesActivity extends AppCompatActivity {
    private ImageView foodButton;
    private ImageView fitnessButton;
    private ImageView educationalButton;
    private ImageView artButton;
    private ImageView technologyButton;
    private ImageView gamesButton;
    private ImageView filmButton;
    private ImageView socialButton;
    private ImageView religionButton;
    private ImageView literatureButton;
    private ImageView musicButton;
    private ImageView causesButton;
    private ImageView careerButton;
    private ImageView otherButton;
    private TextView username;
    private Button nextButton;
    private UserProperties currentUser;
    private String uid;
    private DatabaseReference myRef;
    private FirebaseDatabase database;
    private String node;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_preferences);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        uid = user.getUid();
        node = "users/" + uid;

        username = (TextView) findViewById(R.id.usernameView);
        foodButton = (ImageView) findViewById(R.id.foodButton);
        fitnessButton = (ImageView) findViewById(R.id.fitnessButton);
        educationalButton = (ImageView) findViewById(R.id.educationalButton);
        artButton = (ImageView) findViewById(R.id.artsButton);
        technologyButton = (ImageView) findViewById(R.id.technologyButton);
        gamesButton = (ImageView) findViewById(R.id.gamesButton);
        filmButton = (ImageView) findViewById(R.id.filmButton);
        socialButton = (ImageView) findViewById(R.id.socialButton);
        religionButton = (ImageView) findViewById(R.id.religionButton);
        literatureButton = (ImageView) findViewById(R.id.literatureButton);
        musicButton = (ImageView) findViewById(R.id.musicButton);
        causesButton = (ImageView) findViewById(R.id.causesButton);
        careerButton = (ImageView) findViewById(R.id.careerButton);
        otherButton = (ImageView) findViewById(R.id.otherButton);
        nextButton = (Button) findViewById(R.id.nextButton);

        DatabaseReference usersRef = database.child("users").child(uid);
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentUser = dataSnapshot.getValue(UserProperties.class);
                username.setText(currentUser.username);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        foodButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                foodButton.setColorFilter(0x76ffffff, PorterDuff.Mode.MULTIPLY);
                addPreferences(currentUser, "Food");
            }
        });

        fitnessButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                fitnessButton.setColorFilter(0x76ffffff, PorterDuff.Mode.MULTIPLY);
                addPreferences(currentUser, "Fitness");
            }
        });

        educationalButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                educationalButton.setColorFilter(0x76ffffff, PorterDuff.Mode.MULTIPLY);
                addPreferences(currentUser, "Educational");
            }
        });

        artButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                artButton.setColorFilter(0x76ffffff, PorterDuff.Mode.MULTIPLY);
                addPreferences(currentUser, "Art");
            }
        });

        technologyButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                technologyButton.setColorFilter(0x76ffffff, PorterDuff.Mode.MULTIPLY);
                addPreferences(currentUser, "Technology");
            }
        });

        gamesButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                gamesButton.setColorFilter(0x76ffffff, PorterDuff.Mode.MULTIPLY);
                addPreferences(currentUser, "Games");
            }
        });

        filmButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                filmButton.setColorFilter(0x76ffffff, PorterDuff.Mode.MULTIPLY);
                addPreferences(currentUser, "Film");
            }
        });

        socialButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                socialButton.setColorFilter(0x76ffffff, PorterDuff.Mode.MULTIPLY);
                addPreferences(currentUser, "Social");
            }
        });

        religionButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                religionButton.setColorFilter(0x76ffffff, PorterDuff.Mode.MULTIPLY);
                addPreferences(currentUser, "Religion");
            }
        });

        literatureButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                literatureButton.setColorFilter(0x76ffffff, PorterDuff.Mode.MULTIPLY);
                addPreferences(currentUser, "Literature");
            }
        });

        musicButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                musicButton.setColorFilter(0x76ffffff, PorterDuff.Mode.MULTIPLY);
                addPreferences(currentUser, "Music");
            }
        });

        causesButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                causesButton.setColorFilter(0x76ffffff, PorterDuff.Mode.MULTIPLY);
                addPreferences(currentUser, "Causes");
            }
        });

        careerButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                careerButton.setColorFilter(0x76ffffff, PorterDuff.Mode.MULTIPLY);
                addPreferences(currentUser, "Career");
            }
        });

        otherButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                otherButton.setColorFilter(0x76ffffff, PorterDuff.Mode.MULTIPLY);
                addPreferences(currentUser, "Other");
            }
        });


    }

    public void addPreferences(UserProperties currentUser, String newPreference) {
        currentUser.addPreferences(newPreference);
        myRef.child(node).setValue(currentUser);
    }

    public void updatePreferencesUI(View view) {
        //go to event page after sucessful login
        //TODO change MainActivity to the userprofile page
        Intent intent = new Intent(this, HomePageActivity.class); //temporary change for search testing
        startActivity(intent);
    }
}
