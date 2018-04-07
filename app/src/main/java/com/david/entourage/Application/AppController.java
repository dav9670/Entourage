package com.david.entourage.Application;

import android.app.Application;
import android.content.Context;
import android.location.Location;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import static com.david.entourage.Application.AppConfig.TAG;

public class AppController extends Application {

    private RequestQueue mRequestQueue;
    private static AppController sInstance;
    private static Location lastKnownLocation;

    @Override
    public void onCreate(){
        super.onCreate();
        sInstance = this;
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
}
