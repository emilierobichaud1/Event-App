package com.example.teamrocketeventapp;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Iterator;
import java.util.List;

public class UserProfileActivity extends AppCompatActivity {

    public static final String userId = "userId";
    public static final String MyPREFERENCES = "MyPrefs";
    SharedPreferences sharedPreferences;
    private UserProperties currentUser;
    private EventProperties event;
    private ImageView profilePic;
    private String uid;
    private ScrollView sv;
    private LinearLayout ll;

    DatabaseReference eventsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        profilePic = (ImageView) findViewById(R.id.profilePic);
        sv = (ScrollView)findViewById(R.id.scrollView);
        ll = (LinearLayout)findViewById(R.id.svLinearLayout);

        //sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        uid = user.getUid();

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference usersRef = database.child("users").child(uid);

        //event listener


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
                        Intent intent1 = new Intent(UserProfileActivity.this, EventIndexActivity.class); //temporary change for search testing
                        startActivity(intent1);
                        return true;
                    case R.id.navigation_home:
                        Intent intent2 = new Intent(UserProfileActivity.this, HomePageActivity.class); //temporary change for search testing
                        startActivity(intent2);
                        return true;
                }
                return false;
            }
        };
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.getMenu().getItem(2).setChecked(true);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    public void addEventButtons(List<String> eventList){
        Iterator<String> iter = eventList.iterator();
        while (iter.hasNext()){

            String eventId = iter.next();
            if (eventId.isEmpty() == false) {
                eventsRef = FirebaseDatabase.getInstance().getReference().child("events").child(eventId);

                ValueEventListener eventListener = new ValueEventListener() {
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        event = dataSnapshot.getValue(EventProperties.class);
                        displayEventButtons(event);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                };

                eventsRef.addListenerForSingleValueEvent(eventListener);
            }
        }


    }

    public void displayEventButtons(EventProperties event){
        Button button = new Button(this);
        button.setText(event.getName());
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        button.setLayoutParams(p);
        ll.addView(button);
        View.OnClickListener buttonListener = (view) -> {
            Intent intent = new Intent(view.getContext(), EventActivity.class);
            intent.putExtra("eventid", event.getId());
            startActivity(intent);
        };
        button.setOnClickListener(buttonListener);
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
        addEventButtons(currentUser.eventsList);

    }

    public void signOut(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
