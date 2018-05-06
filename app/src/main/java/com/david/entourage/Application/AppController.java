package com.david.entourage.Application;

import android.app.Application;
import android.content.Context;
import android.location.Location;
import android.os.Handler;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.david.entourage.Place.Places;
import com.google.android.gms.common.api.GoogleApiClient;


import static com.david.entourage.Application.AppConfig.TAG;

public class AppController extends Application {

    private RequestQueue mRequestQueue;
    private int requestInterval;
    private long lastRequestTime;
    private static AppController sInstance;
    private static Location lastKnownLocation;
    private static GoogleApiClient googleApiClient;
    private static Places places;

    @Override
    public void onCreate(){
        super.onCreate();
        sInstance = this;
        places = new Places();
        requestInterval = 2500;
        lastRequestTime = System.currentTimeMillis();
    }

    public static synchronized AppController getInstance(){
        return sInstance;
    }

    public RequestQueue getRequestQueue(){
        if(mRequestQueue == null){
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag){
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req){
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueueTimer(final Request<T> req){
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                req.setTag(TAG);
                getRequestQueue().add(req);
            }
        };
        long delay = requestInterval - (System.currentTimeMillis() - lastRequestTime);
        handler.postDelayed(runnable,delay > 0 ? delay : 0);
        lastRequestTime = System.currentTimeMillis();
    }

    public void cancelPendingRequestQueue(Object Tag){
        if(mRequestQueue != null){
            mRequestQueue.cancelAll(Tag);
        }
    }

    public static Context getContext(){
        return sInstance.getApplicationContext();
    }

    public static Location getLastKnownLocation() {
        return lastKnownLocation;
    }

    public static void setLastKnownLocation(Location lastKnownLocation) {
        AppController.lastKnownLocation = lastKnownLocation;
    }

    public static GoogleApiClient getGoogleApiClient() {
        return googleApiClient;
    }

    public static void setGoogleApiClient(GoogleApiClient googleApiClient) {
        AppController.googleApiClient = googleApiClient;
    }

    public static Places getPlaces() {
        return places;
    }
}
