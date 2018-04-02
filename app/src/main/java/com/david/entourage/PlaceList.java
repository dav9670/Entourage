package com.david.entourage;

import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.android.gms.location.places.Places;

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

        nearbyPlaces = new ArrayList<>();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        placeAdapter = new PlaceAdapter(nearbyPlaces);
        placeAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
            }
        });
        recyclerView.setAdapter(placeAdapter);

        placeIds = getIntent().getStringArrayListExtra("placesId");

        PlaceInfoGetter placeInfoGetter = new PlaceInfoGetter(nearbyPlaces, Places.getGeoDataClient(getApplicationContext(),null), placeAdapter, (int)Utils.convertDpToPixel(AppConfig.IMAGEVIEW_WIDTH,getApplicationContext()),(int)Utils.convertDpToPixel(AppConfig.IMAGEVIEW_HEIGHT,getApplicationContext()));
        placeInfoGetter.getPlaces(placeIds);
    }
}
