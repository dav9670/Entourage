package com.david.entourage.Tasks;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.widget.BaseAdapter;

import com.david.entourage.Application.AppConfig;
import com.david.entourage.Application.AppController;
import com.david.entourage.PlaceAdapter;
import com.david.entourage.PlaceInfo;
import com.david.entourage.Utils;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by David on 3/26/2018.
 */

public class PlaceInfoGetter extends AsyncTask<String,Void,PlaceBuffer>{

    private ArrayList<PlaceInfo> nearbyPlaces;
    private RecyclerView.Adapter adapter;
    private Comparator comparator;

    public PlaceInfoGetter(ArrayList<PlaceInfo> nearbyPlaces, RecyclerView.Adapter adapter, Comparator comparator) {
        this.nearbyPlaces = nearbyPlaces;
        this.adapter = adapter;
        this.comparator = comparator;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected PlaceBuffer doInBackground(String... strings) {
        return Places.GeoDataApi.getPlaceById(AppController.getGoogleApiClient(),strings).await();
    }

    @Override
    protected void onPostExecute(PlaceBuffer places) {
        super.onPostExecute(places);
        for(int i=0; i<places.getCount(); i++){
            nearbyPlaces.add(new PlaceInfo(places.get(i)));
            PlacePhotoGetter placePhotoGetter = new PlacePhotoGetter(nearbyPlaces.get(nearbyPlaces.size()-1),adapter,(int)Utils.convertDpToPixel(AppConfig.IMAGEVIEW_WIDTH,AppController.getContext()),(int)Utils.convertDpToPixel(AppConfig.IMAGEVIEW_HEIGHT,AppController.getContext()),0 ,1);
            placePhotoGetter.execute();
        }
        places.release();
        Collections.sort(nearbyPlaces,comparator);
        adapter.notifyDataSetChanged();
    }
}
