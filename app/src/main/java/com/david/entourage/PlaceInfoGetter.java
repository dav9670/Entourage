package com.david.entourage;

import android.support.annotation.NonNull;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.List;

/**
 * Created by David on 3/26/2018.
 */

public class PlaceInfoGetter {

    private PlaceInfo placeInfo;
    private GeoDataClient geoDataClient;

    public PlaceInfoGetter(PlaceInfo placeInfo, GeoDataClient geoDataClient) {
        this.placeInfo = placeInfo;
        this.geoDataClient = geoDataClient;
    }

    public void getPlaces(List<String> placesId){
        geoDataClient.getPlaceById(placesId.toArray(new String[placesId.size()]))
                .addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
                        PlaceBufferResponse placeBufferResponse = task.getResult();
                        for(int i=0; i<placeBufferResponse.getCount(); i++){
                            placeInfo.setPlaceInfos(placeBufferResponse.get(i));
                        }
                        placeBufferResponse.release();
                    }
                });
    }
}
