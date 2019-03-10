package com.example.teamrocketeventapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

public class EventCategoryListActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "";
    private String categoryType;
    private ArrayList<String> Names = new ArrayList<>();
    private ArrayAdapter adapter;
    private ListView EventlistView;
    private Spinner spin;
    private Calendar c;
    private int SortType = 0; //0: most recent, 1:attendees


    private ListView.OnItemClickListener ClickListener = (parent, view, position, id) -> {
        EventProperties event = (EventProperties) parent.getItemAtPosition(position);
        Intent intent = new Intent(view.getContext(), EventActivity.class);
        intent.putExtra("eventid", event.getId());
        startActivity(intent);
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);
        Bundle b = getIntent().getExtras();

        //navbar stuff
        BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
                = item -> {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    Intent intent1 = new Intent(this, EventIndexActivity.class);
                    startActivity(intent1);
                    return true;
                case R.id.navigation_events:
                    Intent intent3 = new Intent(this, EventIndexActivity.class);
                    startActivity(intent3);
                    return true;
                case R.id.navigation_profile:
                    //mTextMessage.setText(R.string.profile);
                    Intent intent2 = new Intent(this, UserProfileActivity.class);
                    startActivity(intent2);
                    return true;
            }
            return false;
        };
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.getMenu().getItem(0).setChecked(true);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        if(b != null){
            categoryType = (String) b.get("category");
        }

        //spinner stuff
        spin = (Spinner) findViewById(R.id.orderBySpinner);
        String[] arraySpinner = new String[] {"Most recent", "Attendees"};
        ArrayAdapter<String> adapterSp = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arraySpinner);
        adapterSp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(adapterSp);
        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SortType = adapterView.getSelectedItemPosition();
                eventsInCatagory();
                adapter.notifyDataSetChanged();
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });


        //list stuff
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, Names);
        EventlistView = (ListView) findViewById(R.id.eventListView);
        EventlistView.setAdapter(adapter);
        EventlistView.setOnItemClickListener(ClickListener);
        adapter.notifyDataSetChanged();
        eventsInCatagory();
        adapter.notifyDataSetChanged();

    }

    //gets all events in a certain category
    public void eventsInCatagory(){
        Query query;
        query = FirebaseDatabase.getInstance().getReference("events").orderByChild("category").equalTo(categoryType);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    ArrayList<EventProperties> temp = new ArrayList<EventProperties>();
                    adapter.clear();
                    Names = new ArrayList<>();
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        EventProperties event = snapshot.getValue(EventProperties.class);
                        if (event != null) {
                            temp.add(event);
                        }
                    }

                    //sort by number of attendees
                    if(SortType == 1) {
                        Collections.sort(temp, new Comparator<EventProperties>() {
                            public int compare(EventProperties o1, EventProperties o2) {
                                if (o1.attendees.size() == o2.attendees.size()) {
                                    return 0;
                                }
                                return o1.attendees.size() < o2.attendees.size() ? -1 : 1;
                            }
                        });
                        Collections.reverse(temp);
                    }
                    //sort by date
                    else if(SortType == 0){
                        Collections.sort(temp, new Comparator<EventProperties>() {
                            public int compare(EventProperties o1, EventProperties o2) {
                                String[] parts1 = o1.date.split("/");
                                int day1 = Integer.parseInt(parts1[0]);
                                int month1 = Integer.parseInt(parts1[1]);
                                int year1 = Integer.parseInt(parts1[2]);

                                String[] parts2 = o2.date.split("/");
                                int day2 = Integer.parseInt(parts1[0]);
                                int month2 = Integer.parseInt(parts1[1]);
                                int year2 = Integer.parseInt(parts1[2]);

                                boolean check = year1 > year2;

                                if (day1 == day2 && month1 == month2 && year1 == year2) {
                                    return 0;
                                }

                                if(year1 > year2){ check = true; }
                                else if(year1 == year2){
                                    if(month1 > month2){check = true;}
                                    else if(month1 == month2){
                                        if(day1 > day2){check = true;}
                                        else{check = false; }
                                    }
                                }

                                return check ? -1 : 1;
                            }
                        });
                    }

                    for (EventProperties e: temp){
                        Names.add(e.name);
                        adapter.add(e);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }
}