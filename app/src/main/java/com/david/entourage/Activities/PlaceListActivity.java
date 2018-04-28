package com.david.entourage.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;

import com.david.entourage.Application.AppConfig;
import com.david.entourage.PlaceAdapter;
import com.david.entourage.PlaceInfo;
import com.david.entourage.Tasks.PlaceInfoGetter;
import com.david.entourage.R;

import java.util.ArrayList;
import java.util.Collections;

//TODO Choose PlaceType from this activity

public class PlaceListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Button button_sort;
    private Toolbar toolbar;

    private PlaceAdapter placeAdapter;

    private ArrayList<String> placeIds;
    private ArrayList<PlaceInfo> nearbyPlaces;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_placelist);

        nearbyPlaces = new ArrayList<>();

        recyclerView = findViewById(R.id.recyclerView);
        toolbar = findViewById(R.id.toolbar);
        button_sort = findViewById(R.id.button_sort);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        setSupportActionBar(toolbar);

        button_sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(PlaceListActivity.this,button_sort);
                popup.getMenuInflater()
                        .inflate(R.menu.sort_menu,popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch(menuItem.getTitle().toString()) {
                            case "Distance":
                                Collections.sort(nearbyPlaces, PlaceInfo.PlaceInfoCompDist);
                                placeAdapter.notifyDataSetChanged();
                                return true;
                            case "Rating":
                                Collections.sort(nearbyPlaces, PlaceInfo.PlaceInfoCompRating);
                                placeAdapter.notifyDataSetChanged();
                                return true;
                            case "Name":
                                Collections.sort(nearbyPlaces, PlaceInfo.PlaceInfoCompName);
                                placeAdapter.notifyDataSetChanged();
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popup.show();
            }
        });

        placeAdapter = new PlaceAdapter(nearbyPlaces);
        placeAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
            }
        });
        recyclerView.setAdapter(placeAdapter);

        placeIds = getIntent().getStringArrayListExtra("placesId");
        String placeType = getIntent().getStringExtra("placeType");
        getSupportActionBar().setTitle(placeType);

        PlaceInfoGetter placeInfoGetter = new PlaceInfoGetter(nearbyPlaces,placeAdapter);
        placeInfoGetter.execute(placeIds.toArray(new String[placeIds.size()]));
    }
}
