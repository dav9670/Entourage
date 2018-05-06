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

import com.david.entourage.Application.AppController;
import com.david.entourage.Place.OnPhotoReceivedListener;
import com.david.entourage.Place.PlaceInfo;
import com.david.entourage.Place.OnInfoReceivedListener;
import com.david.entourage.Place.Places;
import com.david.entourage.R;
import com.google.android.gms.maps.model.LatLng;

import java.util.Collections;
import java.util.Comparator;

//TODO Choose PlaceType from this activity
//TODO Add loading icon for photos

public class PlaceListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Button button_sort;
    private Toolbar toolbar;

    private PlaceAdapter placeAdapter;

    private Places places;

    private Comparator comparator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_placelist);

        places = AppController.getPlaces();
        places.clearListeners();
        places.setOnPlaceJsonReceivedListener(new OnInfoReceivedListener() {
            @Override
            public void onInfoReceived() {
                places.requestPlaceInfos();
            }
        });
        places.setOnPlaceInfoReceivedListener(new OnInfoReceivedListener() {
            @Override
            public void onInfoReceived() {
                if(comparator != null){
                    Collections.sort(places.getPlaceList(),comparator);
                }
                placeAdapter.notifyDataSetChanged();
            }
        });
        places.setOnPhotoReceivedListener(new OnPhotoReceivedListener() {
            @Override
            public void onPhotoReceived(PlaceInfo placeInfo) {
                if(placeInfo.getPhotos().size() > 0){
                    placeAdapter.notifyDataSetChanged();
                }
            }
        });

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
                                comparator = PlaceInfo.PlaceInfoCompDist;
                                break;
                            case "Rating":
                                comparator = PlaceInfo.PlaceInfoCompRating;
                                break;
                            case "Name":
                                comparator = PlaceInfo.PlaceInfoCompName;
                                break;
                            default:
                                return false;
                        }
                        Collections.sort(places.getPlaceList(), comparator);
                        placeAdapter.notifyDataSetChanged();
                        return true;
                    }
                });
                popup.show();
            }
        });

        placeAdapter = new PlaceAdapter(places.getPlaceList());
        placeAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
            }
        });
        recyclerView.setAdapter(placeAdapter);

        String placeType = getIntent().getStringExtra("placeType");
        getSupportActionBar().setTitle(placeType);

        places.requestPlaceInfos();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(recyclerView != null && recyclerView.getLayoutManager() != null){
                    try{
                        if(recyclerView.getLayoutManager().isViewPartiallyVisible(recyclerView.getLayoutManager().findViewByPosition(recyclerView.getLayoutManager().getItemCount()-1),false,true)){
                            if(places.hasMorePlaces() && !places.isRequestingJson()){
                                places.requestNearbyPlaceJsons();
                            }
                        }
                    }catch (Exception e){

                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        places.clearListeners();
        places.setOnPlaceJsonReceivedListener(new OnInfoReceivedListener() {
            @Override
            public void onInfoReceived() {
                places.requestPlaceInfos();
            }
        });
        places.setOnPlaceInfoReceivedListener(new OnInfoReceivedListener() {
            @Override
            public void onInfoReceived() {
                if(comparator != null){
                    Collections.sort(places.getPlaceList(),comparator);
                }
                placeAdapter.notifyDataSetChanged();
            }
        });
        places.setOnPhotoReceivedListener(new OnPhotoReceivedListener() {
            @Override
            public void onPhotoReceived(PlaceInfo placeInfo) {
                if(placeInfo.getPhotos().size() > 0){
                    placeAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    public void setListeners(){

    }
}
