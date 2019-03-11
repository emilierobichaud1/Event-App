package com.example.teamrocketeventapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;


public class UserProfileActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private StorageReference myStorageRef;
    private UserProperties currentUser;
    private FirebaseUser user;
    private String userId;
    private String node;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        user = mAuth.getCurrentUser();
        userId = user.getUid();
        node = "users/" + userId;
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference usersRef = database.child("users").child(userId);
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    currentUser = dataSnapshot.getValue(UserProperties.class);
                    updateUserInfo(currentUser);
                //}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
                = new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_profile:
                        return true;
                    case R.id.navigation_events:
                        return true;
                    case R.id.navigation_home:
                        //mTextMessage.setText(R.string.profile);
                        Intent intent1 = new Intent(UserProfileActivity.this, EventIndexActivity.class); //temporary change for search testing
                        startActivity(intent1);
                        return true;
                }
                return false;
            }
        };
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.getMenu().getItem(2).setChecked(true);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }


    public void updateUserInfo(UserProperties currentUser) {
        //Update user information on UI after retrieving data from database
        TextView usernameTextView = findViewById(R.id.display_username);
        TextView addressTextView = findViewById(R.id.display_address);
        TextView numEventTextView = findViewById(R.id.display_number_of_events);
        usernameTextView.setText("Username: " + currentUser.getUsername());
        addressTextView.setText("Address: " + currentUser.getAddress());
        numEventTextView.setText("Number of Events: " + (currentUser.eventsList.size() - 1));
    }
}
