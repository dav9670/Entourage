package com.david.entourage.Place;

import android.net.Uri;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.david.entourage.Application.AppController;
import com.david.entourage.DataParser;
import com.david.entourage.Tasks.PlaceInfoGetter;
import com.david.entourage.Tasks.PlacePhotoGetter;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.david.entourage.Application.AppConfig.GOOGLE_BROWSER_API_KEY;
import static com.david.entourage.Application.AppConfig.TAG;

public class Places {

    private final int MAX_PLACES_PER_REQUEST = 20;
    private ArrayList<HashMap<String, String>> placeJsonList;
    private ArrayList<PlaceInfo> placeList;
    private StringBuilder next_page_token;

    private int radius;
    private String placeType;
    private boolean isRequestingJson;

    private OnInfoReceivedListener onPlaceJsonReceivedListener;
    private OnInfoReceivedListener onPlaceInfoReceivedListener;
    private OnPhotoReceivedListener onPhotoReceivedListener;
    private OnNoPhotoReceivedListener onNoPhotoReceivedListener;

    public Places() {
        placeJsonList = new ArrayList<>();
        placeList = new ArrayList<>();
        next_page_token = new StringBuilder();
        radius = 1;
        placeType = new String();
        isRequestingJson = false;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public String getPlaceType() {
        return placeType;
    }

    public void setPlaceType(String placeType) {
        this.placeType = placeType;
    }

    public ArrayList<PlaceInfo> getPlaceList() {
        return placeList;
    }

    public ArrayList<HashMap<String, String>> getPlaceJsonList() {
        return placeJsonList;
    }

    public boolean isRequestingJson() {
        return isRequestingJson;
    }

    public void setOnPlaceJsonReceivedListener(OnInfoReceivedListener onPlaceJsonReceivedListener) {
        this.onPlaceJsonReceivedListener = onPlaceJsonReceivedListener;
    }

    public void setOnPlaceInfoReceivedListener(OnInfoReceivedListener onPlaceOnInfoReceivedListenerListener) {
        this.onPlaceInfoReceivedListener = onPlaceOnInfoReceivedListenerListener;
    }

    public void setOnPhotoReceivedListener(OnPhotoReceivedListener onPhotoReceivedListener) {
        this.onPhotoReceivedListener = onPhotoReceivedListener;
    }

    public void setOnNoPhotoReceivedListener(OnNoPhotoReceivedListener onNoPhotoReceivedListener) {
        this.onNoPhotoReceivedListener = onNoPhotoReceivedListener;
    }

    public PlaceInfo getPlaceInfo(String placeId) {
        for (PlaceInfo placeInfo :
                placeList) {
            if (placeInfo.getId().equals(placeId))
                return placeInfo;
        }
        return null;
    }

    public void requestNearbyPlaceJsons() {

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("maps.googleapis.com")
                .appendPath("maps")
                .appendPath("api")
                .appendPath("place")
                .appendPath("nearbysearch")
                .appendPath("json")
                .appendQueryParameter("location", Double.toString(AppController.getLastKnownLocation().getLatitude()) + "," + Double.toString(AppController.getLastKnownLocation().getLongitude()))
                .appendQueryParameter("radius", Integer.toString(radius))
                .appendQueryParameter("type", placeType)
                .appendQueryParameter("key", GOOGLE_BROWSER_API_KEY);
        if (!next_page_token.toString().equals("")) {
            builder.appendQueryParameter("pagetoken", next_page_token.toString());
        }

        Uri googlePlacesUri = builder.build();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, googlePlacesUri.toString(), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject result) {

                        DataParser.parse(result, placeJsonList, next_page_token);
                        isRequestingJson = false;
                        if (onPlaceJsonReceivedListener != null) {
                            onPlaceJsonReceivedListener.onInfoReceived();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "onErrorResponse: Error=" + error.getMessage());
                    }
                }
        );
        AppController.getInstance().addToRequestQueueTimer(request);
        isRequestingJson = true;
    }

    public void requestPlaceInfo(String placeId) {
        PlaceInfoGetter placeInfoGetter = new PlaceInfoGetter(placeList);
        placeInfoGetter.setOnPlaceOnInfoReceivedListenerListener(onPlaceInfoReceivedListener);
        placeInfoGetter.execute(new String[]{placeId});
    }

    public boolean hasPlaces() {
        return (placeJsonList.size() > 0 || placeList.size() > 0);
    }

    public boolean hasMorePlaces() {
        return !next_page_token.toString().equals("");
    }

    public void requestPlaceInfos() {

        ArrayList<HashMap<String, String>> placeListJsonToAdd = new ArrayList<>(placeJsonList);

        for (int i = 0; i < placeListJsonToAdd.size(); i++) {
            boolean found = false;
            for (int x = 0; x < placeList.size() && !found; x++) {
                if (placeListJsonToAdd.get(i).get("place_id").equals(placeList.get(x).getId())) {
                    placeListJsonToAdd.remove(i);
                    found = true;
                    i--;
                }
            }
        }

        if (placeListJsonToAdd.size() > 0) {
            for (int i = 0; i < Math.ceil((float) placeListJsonToAdd.size() / MAX_PLACES_PER_REQUEST); i++) {
                int currentMin = i * MAX_PLACES_PER_REQUEST;
                int currentMax = (placeListJsonToAdd.size() - currentMin > MAX_PLACES_PER_REQUEST ? MAX_PLACES_PER_REQUEST + currentMin : placeListJsonToAdd.size());
                String placeIds[] = new String[currentMax - currentMin];
                for (int x = 0; x < currentMax - currentMin; x++) {
                    placeIds[x] = placeListJsonToAdd.get(currentMin + x).get("place_id");
                }
                PlaceInfoGetter placeInfoGetter = new PlaceInfoGetter(placeList);
                placeInfoGetter.setOnPlaceOnInfoReceivedListenerListener(onPlaceInfoReceivedListener);
                placeInfoGetter.setOnPhotoReceivedListener(onPhotoReceivedListener);
                placeInfoGetter.execute(placeIds);
            }
        }
    }

    public void requestPlacePhotos(PlaceInfo placeInfo, int photoWidth, int photoHeight, int nbPhotos) {
        PlacePhotoGetter placePhotoGetter = new PlacePhotoGetter(placeInfo, photoWidth, photoHeight, nbPhotos);
        placePhotoGetter.setOnPhotoReceivedListener(onPhotoReceivedListener);
        placePhotoGetter.setOnNoPhotoReceivedListener(onNoPhotoReceivedListener);
        placePhotoGetter.execute();
    }

    public void clearPlaces() {
        placeJsonList.clear();
        next_page_token.setLength(0);
        placeList.clear();
    }

    public void clearListeners() {
        onPlaceJsonReceivedListener = null;
        onPlaceInfoReceivedListener = null;
        onPhotoReceivedListener = null;
        onNoPhotoReceivedListener = null;
    }
}


