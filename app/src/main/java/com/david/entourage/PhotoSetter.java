package com.david.entourage;

import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

/**
 * Created by David on 3/24/2018.
 */

public class PhotoSetter {
    private ImageView imageView;
    private GeoDataClient mGeoDataClient;

    public PhotoSetter(ImageView imageView, GeoDataClient mGeoDataClient) {
        this.imageView = imageView;
        this.mGeoDataClient = mGeoDataClient;
    }

    public void setImage(Place place){
        mGeoDataClient.getPlacePhotos(place.getId())
                .addOnCompleteListener(new OnCompleteListener<PlacePhotoMetadataResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {
                        PlacePhotoMetadataBuffer placePhotoMetadataBuffer = task.getResult().getPhotoMetadata();
                        mGeoDataClient.getPhoto(placePhotoMetadataBuffer.get(0))
                                .addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
                                    @Override
                                    public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
                                        imageView.setImageBitmap(task.getResult().getBitmap());
                                    }
                                });
                    }
                });
    }
}
