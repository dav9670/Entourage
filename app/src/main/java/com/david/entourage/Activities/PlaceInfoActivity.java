package com.david.entourage.Activities;

import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.util.Linkify;
import android.widget.TextView;

import com.david.entourage.Application.AppConfig;
import com.david.entourage.Application.AppController;
import com.david.entourage.PhotoAdapter;
import com.david.entourage.PlaceInfo;
import com.david.entourage.R;
import com.david.entourage.Tasks.PlacePhotoGetter;
import com.david.entourage.Utils;

import java.text.DecimalFormat;
import java.util.regex.Pattern;

//TODO Add google maps link
//TODO Add rating, LatLng
//TODO Add more than 10 photos
//TODO Disable photo recyclerview if no photos

public class PlaceInfoActivity extends AppCompatActivity {

    private PlaceInfo placeInfo;

    //private TextView textView_name;
    private TextView textView_address;
    private TextView textView_distance;
    private TextView textView_googleMaps;
    private TextView textView_tel;
    private TextView textView_uri;

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_placeinfo);

        //textView_name = findViewById(R.id.textView_name);
        textView_address = findViewById(R.id.textView_address);
        textView_distance = findViewById(R.id.textView_distance);
        textView_googleMaps = findViewById(R.id.textView_googleMaps);
        textView_tel = findViewById(R.id.textView_tel);
        textView_uri = findViewById(R.id.textView_uri);
        recyclerView = findViewById(R.id.recyclerView_photos);

        placeInfo = getIntent().getParcelableExtra("placeInfo");

        getSupportActionBar().setTitle(placeInfo.getName());

        textView_address.setText(placeInfo.getAddress());
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        textView_distance.setText(df.format(placeInfo.getDistance()/1000) + " km");
        textView_tel.setText(placeInfo.getPhoneNumber());
        textView_uri.setText(placeInfo.getUri() != null ? placeInfo.getUri().toString() : "No website provided");


        recyclerView.setLayoutManager(new LinearLayoutManager(PlaceInfoActivity.this,LinearLayoutManager.HORIZONTAL,false));
        PhotoAdapter photoAdapter = new PhotoAdapter(placeInfo.getPhotos());
        recyclerView.setAdapter(photoAdapter);
        PlacePhotoGetter placePhotoGetter = new PlacePhotoGetter(placeInfo,photoAdapter,(int) Utils.convertDpToPixel(AppConfig.IMAGEVIEW_WIDTH, AppController.getContext()),(int)Utils.convertDpToPixel(AppConfig.IMAGEVIEW_HEIGHT,AppController.getContext()),1 ,10);
        placePhotoGetter.execute();

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("www.google.com")
                .appendPath("maps")
                .appendEncodedPath("dir/")
                .appendQueryParameter("api","1")
                .appendQueryParameter("origin",AppController.getLastKnownLocation().getLatitude() + "," + AppController.getLastKnownLocation().getLongitude())
                .appendQueryParameter("destination",Double.toString(placeInfo.getLatLng().latitude) + "," + Double.toString(placeInfo.getLatLng().longitude))
                .appendQueryParameter("destination_place_id",placeInfo.getId());
        Uri googleMapsUri = builder.build();
        Pattern pattern = Pattern.compile("[a-zA-Z ]");
        Linkify.addLinks(textView_googleMaps,pattern,googleMapsUri.toString());
    }
}
