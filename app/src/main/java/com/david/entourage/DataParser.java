package com.david.entourage;

import android.util.Log;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by David on 3/18/2018.
 */

public class DataParser {

    public static ArrayList<HashMap<String,String>> parse(JSONObject jsonObject) {
        JSONArray jsonArray = null;
        try {
            Log.d("Places", "parse");
            jsonArray = jsonObject.getJSONArray("results");
        } catch (JSONException e) {
            Log.d("Places", "parse error");
            e.printStackTrace();
        }

        ArrayList<HashMap<String,String>> nearbyPlaces = new ArrayList<>();

        for(int i=0; i<jsonArray.length(); i++){
            try{
                JSONObject jsonPlace = jsonArray.getJSONObject(i);
                HashMap<String,String> place = new HashMap<>();
                place.put("place_id",jsonPlace.getString("place_id"));
                place.put("name",jsonPlace.getString("name"));
                place.put("vicinity",jsonPlace.getString("vicinity"));
                place.put("lat",jsonPlace.getJSONObject("geometry").getJSONObject("location").getString("lat"));
                place.put("lng",jsonPlace.getJSONObject("geometry").getJSONObject("location").getString("lng"));

                nearbyPlaces.add(place);
            }catch (JSONException e){
                Log.e("E","JsonException");
            }
        }
        return nearbyPlaces;
    }
}
