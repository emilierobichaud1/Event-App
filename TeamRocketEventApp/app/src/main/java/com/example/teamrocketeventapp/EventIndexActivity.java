package com.example.teamrocketeventapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class EventIndexActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private SearchView searchView;
    private ListView listView;
    private ArrayAdapter adapter;
    private ArrayList<String> searchNames = new ArrayList<String>();
    DatabaseReference eventsRef;
    private FirebaseDatabase database;
    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        //method that activates upon query
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if(dataSnapshot.exists()){
                searchNames = new ArrayList<String>();
                adapter.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    EventProperties event = snapshot.getValue(EventProperties.class);
                    searchNames.add(event.name);
                    adapter.add(event);
                }
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    private SearchView.OnQueryTextListener searchListener = new SearchView.OnQueryTextListener(){

            @Override
            public boolean onQueryTextSubmit(String query) {
                if(query.length() == 0) {
                    resetSearchView();
                    loadFromDb(null, null);
                }
                else{
                    setSearchView();
                    loadFromDb(null, query);
                }
                adapter.notifyDataSetChanged();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.length() == 0) {
                    resetSearchView();
                    loadFromDb(null, null);
                }
                else{
                    setSearchView();
                    loadFromDb(null, newText);
                }
                adapter.getFilter().filter(newText);
                adapter.notifyDataSetChanged();

                return false;
            }
        };

    private ListView.OnItemClickListener searchResultsClickListener = new ListView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            EventProperties event = (EventProperties) parent.getItemAtPosition(position);
            //event.getId()
            Intent intent = new Intent(view.getContext(), EventActivity.class);
            intent.putExtra("eventid", event.getId());
            startActivity(intent);
        }
    };


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.home);
                    return true;
                case R.id.navigation_events:
                    mTextMessage.setText(R.string.events);
                    return true;
                case R.id.navigation_profile:
                    mTextMessage.setText(R.string.profile);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_index);

        mTextMessage = findViewById(R.id.message);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        loadFromDb(null, null);

        //search by name stuff
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, searchNames);
        listView = (ListView) findViewById(R.id.lv1);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(searchResultsClickListener);
        adapter.notifyDataSetChanged();

        searchView = (SearchView) findViewById(R.id.searchBar);
        searchView.setOnQueryTextListener(searchListener);


    }

    public void create(View view) {
        Intent intent = new Intent(this, EventCreateActivity.class);
        startActivity(intent);
    }

    //pulls loads info from the database into the page
    public void loadFromDb(View view, String searchQuery){

        //query setup
        Query query;
        eventsRef = FirebaseDatabase.getInstance().getReference("events");
        //eventsRef.addListenerForSingleValueEvent(valueEventListener);
        if(searchQuery == null) {
            query = FirebaseDatabase.getInstance().getReference("events").orderByChild("name");
        }
        else {
            query = FirebaseDatabase.getInstance().getReference("events").orderByChild("name").startAt(searchQuery).endAt(searchQuery + "\uf8ff");
        }
        query.addListenerForSingleValueEvent(valueEventListener);

    }


    //has the list view overlap all of the other elemnts by setting it to visible and setting everything else to gone
    private void setSearchView(){
        findViewById(R.id.lv1).setVisibility(View.VISIBLE);
        findViewById(R.id.mapImageView).setVisibility(View.GONE);
        findViewById(R.id.eventListView).setVisibility(View.GONE);
        findViewById(R.id.navigation).setVisibility(View.GONE);
    }

    //restes stuff doen by setSearchView
    private void resetSearchView(){
        findViewById(R.id.lv1).setVisibility(View.GONE);
        findViewById(R.id.mapImageView).setVisibility(View.VISIBLE);
        findViewById(R.id.eventListView).setVisibility(View.VISIBLE);
        findViewById(R.id.navigation).setVisibility(View.VISIBLE);
    }








}
