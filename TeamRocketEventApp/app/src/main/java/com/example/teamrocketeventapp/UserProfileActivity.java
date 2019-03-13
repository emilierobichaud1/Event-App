package com.example.teamrocketeventapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


public class UserProfileActivity extends AppCompatActivity {

    public static final String userId = "userId";
    public static final String MyPREFERENCES = "MyPrefs";
    SharedPreferences sharedPreferences;
    private UserProperties currentUser;
    private ImageView profilePic;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        profilePic = (ImageView) findViewById(R.id.profilePic);

        //sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        uid = user.getUid();

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference usersRef = database.child("users").child(uid);
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
        if (!(currentUser.picUrl.getImageUrl().isEmpty())) {
            Picasso.with(this).load(currentUser.picUrl.getImageUrl()).into(profilePic);
        }
    }
}
