package com.david.entourage;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

import static com.david.entourage.AppConfig.*;

public class MainActivity extends AppCompatActivity
    implements SeekBar.OnSeekBarChangeListener,
    OnMapReadyCallback,
    ActivityCompat.OnRequestPermissionsResultCallback,
    GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener,
    Button.OnClickListener{

    private SearchableSpinner estab_spinner;
    private TextView radius_textView;
    private SeekBar radius_seekBar;
    private MapFragment mapFrag;
    private Button search_button;

    private ArrayAdapter<CharSequence> adapter;
    private GoogleMap mGoogleMap;
    private GoogleApiClient mGoogleApiClient;
    private FusedLocationProviderClient mFusedLocationClient;

    private int radius;
    private Location myLocation;
    private Circle mCircle;
    private List<String> placeTypes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        adapter = ArrayAdapter.createFromResource(this, R.array.placeTypes_UF, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        estab_spinner = findViewById(R.id.estab_spinner);
        estab_spinner.setAdapter(adapter);

        radius_seekBar = findViewById(R.id.radius_seekBar);
        radius_seekBar.setOnSeekBarChangeListener(this);

        radius = radius_seekBar.getProgress() / 2 * 1000;

        radius_textView = findViewById(R.id.radius_textView);
        radius_textView.setText(radius/1000 + " " + getString(R.string.Radius));

        placeTypes = Arrays.asList(getResources().getStringArray(R.array.placeTypes));

        search_button = findViewById(R.id.search_button);
        search_button.setOnClickListener(this);

        mapFrag = ((MapFragment) getFragmentManager().findFragmentById(R.id.map));
        mapFrag.getMapAsync(this);

        buildGoogleApiClient();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        radius = seekBar.getProgress() / 2 * 1000;
        radius_textView.setText(radius/1000 + " " + getString(R.string.Radius));

        if (mCircle != null)
            mCircle.setRadius(radius);
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(getZoomLevel(mCircle)-(float)0.5));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mGoogleMap.getUiSettings().setCompassEnabled(true);
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                mGoogleMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        } else {

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        mGoogleMap.setMyLocationEnabled(true);
                    }
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        checkLocationPermission();
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            myLocation = location;
                            LatLng latLng = new LatLng(myLocation.getLatitude(),myLocation.getLongitude());
                            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng,17);
                            mGoogleMap.animateCamera(cameraUpdate);
                            CircleOptions circleOptions = new CircleOptions()
                                    .center(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()))
                                    .radius(radius * 1000)
                                    .strokeWidth(10)
                                    .strokeColor(Color.GREEN)
                                    .fillColor(Color.argb(128, 255, 0, 0));

                            mCircle = mGoogleMap.addCircle(circleOptions);
                        }
                    }
                });
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                        LOCATION_PERMISSION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_PERMISSION);
            }
        }
    }

    public void debugMessage(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        toast.show();
    }

    public void getNearbyPlaces(double latitude, double longitude, int radius, String type) {

        StringBuilder googlePlacesURL = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesURL.append("location=").append(latitude).append(",").append(longitude);
        googlePlacesURL.append("&radius=").append(radius);
        googlePlacesURL.append("&type=").append(type);
        googlePlacesURL.append("&key=").append(GOOGLE_BROWSER_API_KEY);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, googlePlacesURL.toString(), null,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject result){
                        parseLocationResult(result);
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        Log.e(TAG,"onErrorResponse: Error=" + error.getMessage());
                        debugMessage("Request failed, please retry");
                    }
                }
        );
        AppController.getInstance().addToRequestQueue(request);
    }

    public void parseLocationResult(JSONObject result){

    }

    @Override
    public void onClick(View view) {
        if(radius > 0)
            getNearbyPlaces(myLocation.getLatitude(),myLocation.getLongitude(),radius,placeTypes.get((int)estab_spinner.getSelectedItemId()));
        else
            debugMessage("Radius must be greater than 0");
    }

    public float getZoomLevel(Circle circle) {
        float zoomLevel = 0;
        if (circle != null){
            double radius = circle.getRadius();
            double scale = radius / 500;
            zoomLevel =(float) (16 - Math.log(scale) / Math.log(2));
        }
        return zoomLevel;
    }
}
