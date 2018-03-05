package com.david.entourage;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

public class MainActivity extends FragmentActivity implements SeekBar.OnSeekBarChangeListener, OnMapReadyCallback {

    private Spinner estab_spinner;
    private ArrayAdapter<CharSequence> adapter;
    private SeekBar radius_seekBar;
    private TextView radius_textView;
    private GoogleMap googleMap;

    private int radius;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        radius = 0;

        estab_spinner = findViewById(R.id.estab_spinner);
        radius_seekBar = findViewById(R.id.radius_seekBar);
        radius_textView = findViewById(R.id.radius_textView);

        adapter = ArrayAdapter.createFromResource(this, R.array.placesTypes_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        estab_spinner.setAdapter(adapter);

        radius_seekBar.setOnSeekBarChangeListener(this);

        radius_textView.setText(radius + " " + getString(R.string.Radius));

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        radius = seekBar.getProgress() / 2;
        radius_textView.setText(radius + " " + getString(R.string.Radius));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }
}
