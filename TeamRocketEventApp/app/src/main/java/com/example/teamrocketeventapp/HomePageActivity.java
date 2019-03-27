package com.example.teamrocketeventapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class HomePageActivity extends AppCompatActivity {

    private UserProperties currentUser;
    private String uid;
    DatabaseReference eventsRef;
    private LinearLayout ll;
    private LinearLayout ll2;
    private List<EventProperties> upcomingEvents = new ArrayList<EventProperties>();
    private List<EventProperties> recEvents = new ArrayList<EventProperties>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                // TODO: add navigation for other buttons (needs those pages implemented)
                case R.id.navigation_home:
                    return true;
                case R.id.navigation_events:
                    Intent intent3 = new Intent(this, EventIndexActivity.class);
                    startActivity(intent3);
                    return true;
                case R.id.navigation_profile:
                    Intent intent2 = new Intent(this, UserProfileActivity.class);
                    startActivity(intent2);
                    return true;
            }
            return false;
        });

        ll = (LinearLayout)findViewById(R.id.svLinearLayout);
        ll2 = (LinearLayout)findViewById(R.id.sv2LinearLayout);

        //load user info
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference usersRef = database.child("users").child(uid);

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentUser = dataSnapshot.getValue(UserProperties.class);
                getEventsUserIsAttending();
                getRecomendedEvents();
            }
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });


    }


    public void getEventsUserIsAttending() {
        List<String> eventList = currentUser.eventsList;

        eventsRef = FirebaseDatabase.getInstance().getReference().child("events");

        ValueEventListener eventListener = new ValueEventListener() {
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        EventProperties event = snapshot.getValue(EventProperties.class);


                        if(eventList.contains(event.getId())){

                            String[] currentTime = getCurrentTime();
                            int isFuture = compareDates(event.date, currentTime[0]);
                            if(isFuture == 2 && compareTime(event.time, currentTime[1])){
                                upcomingEvents.add(event);
                            }
                            else if(isFuture == 1){
                                upcomingEvents.add(event);
                            }
                        }
                    }
                }
                displayUpcomingEvents();
            }

            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };

        eventsRef.addListenerForSingleValueEvent(eventListener);
    }


    public String[] getCurrentTime(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat date = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat time = new SimpleDateFormat("HH;mm");
        String strDate = date.format(calendar.getTime());
        String strTime = time.format(calendar.getTime());

        String[] ret = {strDate, strTime};
        return ret;
    }

    //returns 1 if date1 occurs after date2, 0 if it occours before and 2 if they occour in the same day
    public int compareDates(String date1, String date2){
        String[] parts1 = date1.split("/");
        String[] parts2 = date2.split("/");

        if(Integer.parseInt(parts1[2]) > Integer.parseInt(parts2[2])){
            return 1;
        }
        else if(Integer.parseInt(parts1[2]) == Integer.parseInt(parts2[2])){
            if(Integer.parseInt(parts1[1]) > Integer.parseInt(parts2[1])){
                return 1;
            }
            else if(Integer.parseInt(parts1[1]) == Integer.parseInt(parts2[1])){
                if(Integer.parseInt(parts1[0]) == Integer.parseInt(parts2[0])){
                    return 2;
                }
                else if(Integer.parseInt(parts1[0]) > Integer.parseInt(parts2[0])){
                    return 1;
                }
                else{
                    return 0;
                }
            }
            else{
                return 0;
            }
        }
        else{
            return 0;
        }
    }

    //returns true if time1 occurs after time2
    public boolean compareTime(String time1, String time2){
        String[] parts1 = time1.split(";|:");
        String[] parts2 = time2.split(";|:");

        if(Integer.parseInt(parts1[0]) > Integer.parseInt(parts2[0])){
            return true;
        }
        else if(Integer.parseInt(parts1[0]) == Integer.parseInt(parts2[0])){
            return Integer.parseInt(parts1[1]) > Integer.parseInt(parts2[1]);
        }
        else{
            return false;
        }
    }


    //returns a sorted version of the input list
    public List<EventProperties> sortByDate(List<EventProperties> eventList){

        Collections.sort(eventList, new Comparator<EventProperties>() {
            public int compare(EventProperties o1, EventProperties o2) {

                boolean flag;

                int dateResult = compareDates(o1.date, o2.date);

                if(dateResult == 2){
                    flag = compareTime(o1.time, o2.time);
                }
                else{
                    flag = dateResult == 1;
                }


                return flag ? -1 : 1;
            }
        });

        Collections.reverse(eventList);

        return eventList;
    }


    public void displayUpcomingEvents(){
        //TODO: sort upcomingEvents by date, display events on page
        upcomingEvents = sortByDate(upcomingEvents);

        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        for(EventProperties e: upcomingEvents){
            Button button = new Button(this);
            button.setText(e.getName());
            button.setLayoutParams(p);
            ll.addView(button);
            View.OnClickListener buttonListener = (view) -> {
                Intent intent = new Intent(view.getContext(), EventActivity.class);
                intent.putExtra("eventid", e.getId());
                startActivity(intent);
            };
            button.setOnClickListener(buttonListener);
        }
    }


    //gets 10 recomended events
    public void getRecomendedEvents(){

        List<String> prefs = currentUser.getPreferences();

        int eventsToGrabPerCatagory = (int) Math.floor(10/ (prefs.size()-1));

        for(String s: prefs){
            Query prefRef = FirebaseDatabase.getInstance().getReference().child("events").orderByChild("category").startAt(s).endAt(s);

            ValueEventListener eventListener = new ValueEventListener() {
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        int counter = 0;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            EventProperties event = snapshot.getValue(EventProperties.class);

                            //only recomend events user is not attending
                            if(!currentUser.getEventsList().contains(event.getId())) {
                                counter++;
                                if (counter <= eventsToGrabPerCatagory) {
                                    recEvents.add(event);
                                    displayRecomendedEvent(event);
                                }
                            }
                        }
                    }
                }

                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            };

            prefRef.addListenerForSingleValueEvent(eventListener);

        }
    }

    public void displayRecomendedEvent(EventProperties event){

        Log.d("DWFEA", recEvents.toString());

        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        Button button = new Button(this);
        button.setText(event.getName());
        button.setLayoutParams(p);
        ll2.addView(button);
        View.OnClickListener buttonListener = (view) -> {
            Intent intent = new Intent(view.getContext(), EventActivity.class);
            intent.putExtra("eventid", event.getId());
            startActivity(intent);
        };
        button.setOnClickListener(buttonListener);


    }
}


