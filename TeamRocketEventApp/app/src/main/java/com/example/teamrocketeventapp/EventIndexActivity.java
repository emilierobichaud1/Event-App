package com.example.teamrocketeventapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class EventIndexActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private String userId;
    public static final String EXTRA_MESSAGE = "";
    private TextView mTextMessage;
    private SearchView searchView;
    private ListView listView;
    private ArrayAdapter adapter;
    private ArrayList<String> searchNames = new ArrayList<>();

    DatabaseReference eventsRef;
    private FirebaseDatabase database;
    ValueEventListener valueEventListener = new ValueEventListener() {

        private void addEventToMap(EventProperties event) {
            List<Double> eventCoordinates = event.getCoordinates();
            LatLng eventPosition = new LatLng(eventCoordinates.get(0), eventCoordinates.get(1));
            mMap.addMarker(new MarkerOptions().position(eventPosition).title(event.getName()));
        }

        @Override
        //method that activates upon query
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if(dataSnapshot.exists()){
                searchNames = new ArrayList<>();
                adapter.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    EventProperties event = snapshot.getValue(EventProperties.class);
                    if (event != null) {
                        searchNames.add(event.name);
                        adapter.add(event);
                        addEventToMap(event);
                    }
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

    private ListView.OnItemClickListener searchResultsClickListener = (parent, view, position, id) -> {
        EventProperties event = (EventProperties) parent.getItemAtPosition(position);
        //event.getId()
        Intent intent = new Intent(view.getContext(), EventActivity.class);
        intent.putExtra("eventid", event.getId());
        startActivity(intent);
    };

    private boolean inArea(EventProperties event) {
        LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds;
        List<Double> coordinateList = event.getCoordinates();
        LatLng coordinates = new LatLng(coordinateList.get(0), coordinateList.get(1));
        return bounds.contains(coordinates);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_index);

        mTextMessage = findViewById(R.id.message);
        BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
                = item -> {
                    switch (item.getItemId()) {
                        case R.id.navigation_home:
                            return true;
                        case R.id.navigation_events:
                            return true;
                        case R.id.navigation_profile:
                            //mTextMessage.setText(R.string.profile);
                            Intent intent2 = new Intent(EventIndexActivity.this, UserProfileActivity.class); //temporary change for search testing
                            intent2.putExtra(EXTRA_MESSAGE, userId);
                            startActivity(intent2);
                            return true;
                    }
                    return false;
                };
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.getMenu().getItem(0).setChecked(true);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        loadFromDb(null, null);

        Intent intent = getIntent();
        userId = intent.getStringExtra(LoginActivity.EXTRA_MESSAGE);

        //search by name stuff
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, searchNames);
        listView = (ListView) findViewById(R.id.searchList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(searchResultsClickListener);
        adapter.notifyDataSetChanged();

        searchView = findViewById(R.id.searchBar);
        searchView.setOnQueryTextListener(searchListener);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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


    //has the list view overlap all of the other elements by setting it to visible and setting everything else to gone
    private void setSearchView(){
        findViewById(R.id.searchList).setVisibility(View.VISIBLE);
        findViewById(R.id.map).setVisibility(View.GONE);
        findViewById(R.id.eventListView).setVisibility(View.GONE);
        findViewById(R.id.navigation).setVisibility(View.GONE);
    }

    // resets stuff done by setSearchView
    private void resetSearchView(){
        findViewById(R.id.searchList).setVisibility(View.GONE);
        findViewById(R.id.map).setVisibility(View.VISIBLE);
        findViewById(R.id.eventListView).setVisibility(View.VISIBLE);
        findViewById(R.id.navigation).setVisibility(View.VISIBLE);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng toronto = new LatLng(43.6532, -79.3832);

        mMap.addMarker(new MarkerOptions().position(toronto).title("Toronto"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(toronto));
    }
}
