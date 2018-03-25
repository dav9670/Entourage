package com.david.entourage;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlaceTypes;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by David on 3/18/2018.
 */

public class DataParser {

    ArrayList<Place> nearbyPlaces;
    ArrayList<Marker> markerList;

    GeoDataClient mGeoDataClient;
    GoogleMap mGoogleMap;

    public DataParser(ArrayList<Place> nearbyPlaces, ArrayList<Marker> markerList, GeoDataClient mGeoDataClient, GoogleMap mGoogleMap) {
        this.nearbyPlaces = nearbyPlaces;
        this.markerList = markerList;
        this.mGeoDataClient = mGeoDataClient;
        this.mGoogleMap = mGoogleMap;
    }

    public void setPlaces(JSONObject jsonObject) {
        JSONArray jsonArray = parse(jsonObject);
        List<String> place_Ids = new ArrayList<>();
        for(int i=0; i<jsonArray.length(); i++){
            try{
                JSONObject jsonPlace = jsonArray.getJSONObject(i);
                place_Ids.add(jsonPlace.getString("place_id"));
            }catch (JSONException e){
                Log.e("E","JsonException");
            }
        }
        mGeoDataClient.getPlaceById(place_Ids.toArray(new String[place_Ids.size()]))
                .addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
                        PlaceBufferResponse placeBufferResponse = task.getResult();
                        for(int i=0; i<placeBufferResponse.getCount(); i++){
                            nearbyPlaces.add(placeBufferResponse.get(i));
                            MarkerOptions markerOptions = new MarkerOptions();
                            Place place = nearbyPlaces.get(i);
                            markerOptions.position(place.getLatLng());
                            markerOptions.title(place.getName() + " : " + place.getAddress());
                            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                            markerList.add(mGoogleMap.addMarker(markerOptions));
                        }
                    }
                });
    }

    private JSONArray parse(JSONObject jsonObject) {

        JSONArray jsonArray = null;
        try {
            Log.d("Places", "parse");
            jsonArray = jsonObject.getJSONArray("results");
        } catch (JSONException e) {
            Log.d("Places", "parse error");
            e.printStackTrace();
        }
        return jsonArray;
    }
}
