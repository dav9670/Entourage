package com.david.entourage;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by David on 3/26/2018.
 */

public class PlaceInfoGetter {

    private ArrayList<PlaceInfo> nearbyPlaces;
    private GeoDataClient geoDataClient;
    private RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder> recyclerAdapter;

    public PlaceInfoGetter(ArrayList<PlaceInfo> nearbyPlaces, GeoDataClient geoDataClient, RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder> recyclerAdapter) {
        this.nearbyPlaces = nearbyPlaces;
        this.geoDataClient = geoDataClient;
        this.recyclerAdapter = recyclerAdapter;
    }

    public void getPlaces(List<String> placesId){
        geoDataClient.getPlaceById(placesId.toArray(new String[placesId.size()]))
                .addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
                        PlaceBufferResponse placeBufferResponse = task.getResult();
                        for(int i=0; i<placeBufferResponse.getCount(); i++){
                            nearbyPlaces.add(new PlaceInfo((placeBufferResponse.get(i))));
                            recyclerAdapter.notifyDataSetChanged();
                            getPhotos(nearbyPlaces.get(i),0, 1);
                        }
                        placeBufferResponse.release();
                    }
                });
    }

    public void getPhotos(PlaceInfo placeInfo, final int numStartPhoto, final int numLastPhoto){
        final int placeIndex = nearbyPlaces.indexOf(placeInfo);
        geoDataClient.getPlacePhotos(placeInfo.getId())
                .addOnCompleteListener(new OnCompleteListener<PlacePhotoMetadataResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {
                        PlacePhotoMetadataBuffer placePhotoMetadataBuffer = task.getResult().getPhotoMetadata();
                        for(int i=numStartPhoto; i<placePhotoMetadataBuffer.getCount() && i<numLastPhoto; i++) {
                            geoDataClient.getPhoto(placePhotoMetadataBuffer.get(i))
                                    .addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
                                        @Override
                                        public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
                                           nearbyPlaces.get(placeIndex).addPhoto(task.getResult().getBitmap());
                                           recyclerAdapter.notifyDataSetChanged();
                                        }
                                    });
                        }
                        placePhotoMetadataBuffer.release();
                    }
                });
    }
}
