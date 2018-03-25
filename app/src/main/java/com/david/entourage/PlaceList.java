package com.david.entourage;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.android.gms.location.places.Place;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;

public class PlaceList extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PlaceAdapter placeAdapter;

    private ArrayList<Place> nearbyPlaces;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        String nearbyPlacesGson = getIntent().getStringExtra("nearbyPlacesGson");
        Gson gson = new GsonBuilder().registerTypeAdapter(Place.class,new PlaceInstanceCreator()).create();
        nearbyPlaces = gson.fromJson(nearbyPlacesGson, new TypeToken<ArrayList<Place>>(){}.getType());
        placeAdapter = new PlaceAdapter(this, nearbyPlaces);
        recyclerView.setAdapter(placeAdapter);
    }
}
