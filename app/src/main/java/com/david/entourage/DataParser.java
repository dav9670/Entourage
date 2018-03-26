package com.david.entourage;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
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
import java.util.List;

/**
 * Created by David on 3/18/2018.
 */

public class DataParser {

    ArrayList<Marker> markerList;

    GeoDataClient mGeoDataClient;
    GoogleMap mGoogleMap;

    public DataParser(ArrayList<Marker> markerList, GeoDataClient mGeoDataClient, GoogleMap mGoogleMap) {
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
                            Place place = placeBufferResponse.get(i);
                            AppController.nearbyPlaces.add(place);
                            place.freeze();

                            PhotoGetter photoGetter = new PhotoGetter(place, mGeoDataClient);
                            photoGetter.GetPhotos(1);

                            MarkerOptions markerOptions = new MarkerOptions();
                            markerOptions.position(place.getLatLng());
                            markerOptions.title(place.getName() + " : " + place.getAddress());
                            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                            markerList.add(mGoogleMap.addMarker(markerOptions));
                        }
                        //placeBufferResponse.release();
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
