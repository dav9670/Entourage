package com.david.entourage.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.BaseAdapter;

import com.david.entourage.Application.AppConfig;
import com.david.entourage.PlaceAdapter;
import com.david.entourage.PlaceInfo;
import com.david.entourage.Tasks.PlaceInfoGetter;
import com.david.entourage.R;
import com.david.entourage.Utils;
import com.google.android.gms.location.places.Places;

import java.util.ArrayList;

public class PlaceListActivity extends AppCompatActivity {

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

        PlaceInfoGetter placeInfoGetter = new PlaceInfoGetter(nearbyPlaces,placeAdapter);
        placeInfoGetter.execute(placeIds.toArray(new String[placeIds.size()]));
    }
}
