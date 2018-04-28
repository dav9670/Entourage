package com.david.entourage.Activities;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.david.entourage.Application.AppConfig;
import com.david.entourage.Application.AppController;
import com.david.entourage.PhotoAdapter;
import com.david.entourage.PlaceInfo;
import com.david.entourage.R;
import com.david.entourage.Tasks.PlacePhotoGetter;
import com.david.entourage.Utils;

import java.text.DecimalFormat;

//TODO Add google maps link
//TODO Add rating, LatLng
//TODO Add more than 10 photos
//TODO Disable photo recyclerview if no photos

public class PlaceInfoActivity extends AppCompatActivity {

    private PlaceInfo placeInfo;

    //private TextView textView_name;
    private TextView textView_address;
    private TextView textView_distance;
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
        textView_tel = findViewById(R.id.textView_tel);
        textView_uri = findViewById(R.id.textView_uri);

        placeInfo = getIntent().getParcelableExtra("placeInfo");

        getSupportActionBar().setTitle(placeInfo.getName());

        //textView_name.setText(placeInfo.getName());
        textView_address.setText(placeInfo.getAddress());
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        textView_distance.setText(df.format(placeInfo.getDistance()/1000) + " km");
        textView_tel.setText(placeInfo.getPhoneNumber());
        textView_uri.setText(placeInfo.getUri() != null ? placeInfo.getUri().toString() : "No website provided");

        recyclerView = findViewById(R.id.recyclerView_photos);
        recyclerView.setLayoutManager(new LinearLayoutManager(PlaceInfoActivity.this,LinearLayoutManager.HORIZONTAL,false));
        PhotoAdapter photoAdapter = new PhotoAdapter(placeInfo.getPhotos());
        recyclerView.setAdapter(photoAdapter);
        PlacePhotoGetter placePhotoGetter = new PlacePhotoGetter(placeInfo,photoAdapter,(int) Utils.convertDpToPixel(AppConfig.IMAGEVIEW_WIDTH, AppController.getContext()),(int)Utils.convertDpToPixel(AppConfig.IMAGEVIEW_HEIGHT,AppController.getContext()),1 ,10);
        placePhotoGetter.execute();
    }
}
