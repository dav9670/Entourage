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

    public static void parse(JSONObject jsonObject,ArrayList<HashMap<String,String>> nearbyPlacesJson, StringBuilder next_page_token_builder) {

        try {
            String next_page_token = jsonObject.getString("next_page_token");
            next_page_token_builder.setLength(0);
            next_page_token_builder.append(next_page_token);
            Log.d("next_page_token", next_page_token);

        } catch (JSONException e) {
            next_page_token_builder.setLength(0);
            //Log.d("Places", "parse error");
            //e.printStackTrace();
        }

        JSONArray jsonArray = null;
        try {
            Log.d("Places", "parse");
            jsonArray = jsonObject.getJSONArray("results");
        } catch (JSONException e) {
            Log.d("Places", "parse error");
            e.printStackTrace();
        }

        for(int i=0; i<jsonArray.length(); i++){
            try{
                JSONObject jsonPlace = jsonArray.getJSONObject(i);
                HashMap<String,String> place = new HashMap<>();
                place.put("place_id",jsonPlace.getString("place_id"));
                place.put("name",jsonPlace.getString("name"));
                place.put("vicinity",jsonPlace.getString("vicinity"));
                place.put("lat",jsonPlace.getJSONObject("geometry").getJSONObject("location").getString("lat"));
                place.put("lng",jsonPlace.getJSONObject("geometry").getJSONObject("location").getString("lng"));

                nearbyPlacesJson.add(place);
            }catch (JSONException e){
                Log.e("E","JsonException");
            }
        }
    }
}
