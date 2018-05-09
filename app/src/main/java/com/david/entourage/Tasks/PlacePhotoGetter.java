package com.david.entourage.Tasks;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.david.entourage.Application.AppController;
import com.david.entourage.Place.OnNoPhotoReceivedListener;
import com.david.entourage.Place.OnPhotoReceivedListener;
import com.david.entourage.Place.PlaceInfo;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.Places;


public class PlacePhotoGetter extends AsyncTask<Void, Void, PlacePhotoMetadataBuffer> {

    private PlaceInfo placeInfo;
    private int photoWidth;
    private int photoHeight;
    private int nbPhotos;

    private OnPhotoReceivedListener onPhotoReceivedListener;
    private OnNoPhotoReceivedListener onNoPhotoReceivedListener;

    public PlacePhotoGetter(PlaceInfo placeInfo, int photoWidth, int photoHeight, int nbPhotos) {
        this.placeInfo = placeInfo;
        this.photoWidth = photoWidth;
        this.photoHeight = photoHeight;
        this.nbPhotos = nbPhotos;
    }

    public void setOnPhotoReceivedListener(OnPhotoReceivedListener onPhotoReceivedListener) {
        this.onPhotoReceivedListener = onPhotoReceivedListener;
    }

    public void setOnNoPhotoReceivedListener(OnNoPhotoReceivedListener onNoPhotoReceivedListener) {
        this.onNoPhotoReceivedListener = onNoPhotoReceivedListener;
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
            if(placePhotoMetadataBuffer.getCount() > 0){
                for(int i = placeInfo.getPhotos().size(); i<placePhotoMetadataBuffer.getCount() && i< nbPhotos; i++){
                    PlacePhotoSetter placePhotoSetter = new PlacePhotoSetter();
                    placePhotoSetter.execute(placePhotoMetadataBuffer.get(i).freeze());
                }
            }
            else{
                if(onNoPhotoReceivedListener != null){
                    onNoPhotoReceivedListener.onNoPhotoReceived(placeInfo);
                }
            }
            placePhotoMetadataBuffer.release();
        }
        else{
            if(onNoPhotoReceivedListener != null){
                onNoPhotoReceivedListener.onNoPhotoReceived(placeInfo);
            }
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
            if(onPhotoReceivedListener != null){
                onPhotoReceivedListener.onPhotoReceived(placeInfo);
            }
        }
    }
}
