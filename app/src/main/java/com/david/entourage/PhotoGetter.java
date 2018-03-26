package com.david.entourage;

import android.support.annotation.NonNull;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

/**
 * Created by David on 3/24/2018.
 */

public class PhotoGetter {
    private GeoDataClient mGeoDataClient;
    private Place place;

    public PhotoGetter(Place place, GeoDataClient geoDataClient) {
        this.mGeoDataClient = geoDataClient;
        this.place = place;
    }

    public void GetPhotos(final int numberOfPhotos){
        mGeoDataClient.getPlacePhotos(place.getId())
                .addOnCompleteListener(new OnCompleteListener<PlacePhotoMetadataResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {
                        PlacePhotoMetadataBuffer placePhotoMetadataBuffer = task.getResult().getPhotoMetadata();
                        for(int i=0; i<placePhotoMetadataBuffer.getCount() && i<numberOfPhotos; i++) {
                            mGeoDataClient.getPhoto(placePhotoMetadataBuffer.get(i))
                                    .addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
                                        @Override
                                        public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
                                            AppController.addPhotoPlace(place, task.getResult().getBitmap());
                                        }
                                    });
                        }
                        placePhotoMetadataBuffer.release();
                    }
                });
    }
}
