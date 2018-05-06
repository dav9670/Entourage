package com.david.entourage.Tasks;

import android.os.AsyncTask;

import com.david.entourage.Application.AppConfig;
import com.david.entourage.Application.AppController;
import com.david.entourage.Place.OnPhotoReceivedListener;
import com.david.entourage.Place.PlaceInfo;
import com.david.entourage.Place.OnInfoReceivedListener;
import com.david.entourage.Utils;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;

import java.util.ArrayList;

/**
 * Created by David on 3/26/2018.
 */

public class PlaceInfoGetter extends AsyncTask<String,Void,PlaceBuffer>{

    private ArrayList<PlaceInfo> placeList;

    private OnInfoReceivedListener onPlaceOnInfoReceivedListenerListener;
    private OnPhotoReceivedListener onPhotoReceivedListener;

    public PlaceInfoGetter(ArrayList<PlaceInfo> placeList) {
        this.placeList = placeList;
    }

    public void setOnPlaceOnInfoReceivedListenerListener(OnInfoReceivedListener onPlaceOnInfoReceivedListenerListener) {
        this.onPlaceOnInfoReceivedListenerListener = onPlaceOnInfoReceivedListenerListener;
    }

    public void setOnPhotoReceivedListener(OnPhotoReceivedListener onPhotoReceivedListener) {
        this.onPhotoReceivedListener = onPhotoReceivedListener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected PlaceBuffer doInBackground(String... strings) {
        if(strings != null)
            return Places.GeoDataApi.getPlaceById(AppController.getGoogleApiClient(),strings).await();
        else
            return null;
    }

    @Override
    protected void onPostExecute(PlaceBuffer places) {
        super.onPostExecute(places);
        if(places != null){
            for(int i=0; i<places.getCount(); i++){
                placeList.add(new PlaceInfo(places.get(i)));
                PlacePhotoGetter placePhotoGetter = new PlacePhotoGetter(placeList.get(placeList.size()-1),(int)Utils.convertDpToPixel(AppConfig.IMAGEVIEW_WIDTH,AppController.getContext()),(int)Utils.convertDpToPixel(AppConfig.IMAGEVIEW_HEIGHT,AppController.getContext()) ,1);
                placePhotoGetter.setOnPhotoReceivedListener(onPhotoReceivedListener);
                placePhotoGetter.execute();
            }
            places.release();
            if(onPlaceOnInfoReceivedListenerListener != null){
                onPlaceOnInfoReceivedListenerListener.onInfoReceived();
            }
        }
    }
}
