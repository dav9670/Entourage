package com.david.entourage;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlaceList extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PlaceAdapter placeAdapter;

    private ArrayList<HashMap<String,String>> placeList;

    public PlaceList() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        placeList = (ArrayList<HashMap<String,String>>)getIntent().getSerializableExtra("placeList");
        placeAdapter = new PlaceAdapter(this,placeList);
        recyclerView.setAdapter(placeAdapter);
    }
}
