package com.david.entourage;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.android.gms.location.places.Place;

import java.util.ArrayList;

public class PlaceList extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PlaceAdapter placeAdapter;

    private ArrayList<String> placeIds;
    private ArrayList<PlaceInfo> nearbyPlaces;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        placeIds = getIntent().getStringArrayListExtra("placesId");
        nearbyPlaces = new ArrayList<>();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        placeAdapter = new PlaceAdapter(this);
        recyclerView.setAdapter(placeAdapter);
    }
}
