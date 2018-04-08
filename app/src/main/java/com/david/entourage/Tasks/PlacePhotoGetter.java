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
    private int startPhoto;
    private int lastPhoto;

    public PlacePhotoGetter(PlaceInfo placeInfo, RecyclerView.Adapter adapter, int photoWidth, int photoHeight, int startPhoto, int lastPhoto) {
        this.placeInfo = placeInfo;
        this.adapter = adapter;
        this.photoWidth = photoWidth;
        this.photoHeight = photoHeight;
        this.startPhoto = startPhoto;
        this.lastPhoto = lastPhoto;
    }

    @Override
    protected PlacePhotoMetadataBuffer doInBackground(Void... voids) {
        return Places.GeoDataApi.getPlacePhotos(AppController.getGoogleApiClient(),placeInfo.getId()).await().getPhotoMetadata();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(PlacePhotoMetadataBuffer placePhotoMetadataBuffer) {
        super.onPostExecute(placePhotoMetadataBuffer);
        for(int i=startPhoto; i<placePhotoMetadataBuffer.getCount() && i<lastPhoto; i++){
            PlacePhotoSetter placePhotoSetter = new PlacePhotoSetter();
            placePhotoSetter.execute(placePhotoMetadataBuffer.get(i).freeze());
        }
        placePhotoMetadataBuffer.release();
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
