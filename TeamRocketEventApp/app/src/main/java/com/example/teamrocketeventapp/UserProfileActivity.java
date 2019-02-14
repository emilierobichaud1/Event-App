package com.example.teamrocketeventapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class UserProfileActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        UserProperties currentUser = new UserProperties();
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String userId = intent.getStringExtra(LoginActivity.EXTRA_MESSAGE);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        String node = "users/" + userId;
        myRef.child(node).addValueEventListener(valueListener);
        setContentView(R.layout.activity_user_profile);


    }

    public void updateUserInfo(UserProperties currentUser){
        //Update user information on UI after retrieving data from database
        TextView usernameTextView = findViewById(R.id.display_username);
        TextView addressTextView = findViewById(R.id.display_address);
        usernameTextView.setText("Username: " + currentUser.getUsername());
        addressTextView.setText("Address: " + currentUser.getAddress());
    }

    ValueEventListener valueListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            // Get user from database and use the values to update the UI
            UserProperties value = dataSnapshot.getValue(UserProperties.class);
            updateUserInfo(value);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

}
