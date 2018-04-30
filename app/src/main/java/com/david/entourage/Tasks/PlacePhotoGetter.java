package com.david.entourage.Tasks;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;

import com.david.entourage.Application.AppController;
import com.david.entourage.PlaceInfo;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.Places;


public class PlacePhotoGetter extends AsyncTask<Void, Void, PlacePhotoMetadataBuffer> {

    private PlaceInfo placeInfo;
    private RecyclerView.Adapter adapter;
    private int photoWidth;
    private int photoHeight;
    private int nbPhotos;

    public PlacePhotoGetter(PlaceInfo placeInfo, RecyclerView.Adapter adapter, int photoWidth, int photoHeight, int nbPhotos) {
        this.placeInfo = placeInfo;
        this.adapter = adapter;
        this.photoWidth = photoWidth;
        this.photoHeight = photoHeight;
        this.nbPhotos = nbPhotos;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected PlacePhotoMetadataBuffer doInBackground(Void... voids) {
        if(placeInfo.getPhotos().size() < nbPhotos){
            return Places.GeoDataApi.getPlacePhotos(AppController.getGoogleApiClient(),placeInfo.getId()).await().getPhotoMetadata();
        }
        else
            return null;
    }

    @Override
    protected void onPostExecute(PlacePhotoMetadataBuffer placePhotoMetadataBuffer) {
        super.onPostExecute(placePhotoMetadataBuffer);
        if(placePhotoMetadataBuffer != null) {
            for(int i = placeInfo.getPhotos().size(); i<placePhotoMetadataBuffer.getCount() && i< nbPhotos; i++){
                PlacePhotoSetter placePhotoSetter = new PlacePhotoSetter();
                placePhotoSetter.execute(placePhotoMetadataBuffer.get(i).freeze());
            }
            placePhotoMetadataBuffer.release();
        }
    }

    private class PlacePhotoSetter extends AsyncTask<PlacePhotoMetadata,Void,Bitmap>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Bitmap doInBackground(PlacePhotoMetadata... placePhotoMetadata) {
            return placePhotoMetadata[0].getScaledPhoto(AppController.getGoogleApiClient(),photoWidth,photoHeight).await().getBitmap();
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            placeInfo.addPhoto(bitmap);
            adapter.notifyDataSetChanged();
        }
    }
}
