package com.david.entourage.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.david.entourage.Application.AppController;
import com.david.entourage.DataParser;
import com.david.entourage.R;
import com.david.entourage.Utils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static com.david.entourage.Application.AppConfig.CONNECTION_FAILURE_RESOLUTION_REQUEST;
import static com.david.entourage.Application.AppConfig.GOOGLE_BROWSER_API_KEY;
import static com.david.entourage.Application.AppConfig.LOCATION_PERMISSION;
import static com.david.entourage.Application.AppConfig.TAG;

//TODO Add more than 20 establishments
//TODO Open PlaceInfoActivity from marker
// Vendredi 16h30

public class MainActivity extends AppCompatActivity
    implements OnMapReadyCallback,
    ActivityCompat.OnRequestPermissionsResultCallback{

    private SearchableSpinner spinner_place;
    private TextView textView_radius;
    private SeekBar seekBar_radius;
    private Button button_search;
    private Button button_list;

    private ArrayAdapter<CharSequence> placeTypes_Adapter;
    private MapFragment fragMap;
    private GoogleMap mGoogleMap;
    private FusedLocationProviderClient mFusedLocationClient;

    private int radius;
    private boolean mLocationPermissionGranted;
    private Circle radiusCircle;
    private List<String> placeTypes;
    private ArrayList<Marker> markerList;
    private ArrayList<HashMap<String,String>> nearbyPlacesJsons;

    private StringBuilder next_page_token_builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        markerList = new ArrayList<>();
        nearbyPlacesJsons = new ArrayList<>();
        next_page_token_builder = new StringBuilder();

        spinner_place = findViewById(R.id.place_spinner);
        textView_radius = findViewById(R.id.textView_radius);
        seekBar_radius = findViewById(R.id.seekBar_radius);
        button_search = findViewById(R.id.button_search);
        button_list = findViewById(R.id.button_sort);
        fragMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.fragMap));
        fragMap.getMapAsync(this);

        //Sets text inside spinner
        placeTypes_Adapter = ArrayAdapter.createFromResource(this, R.array.placeTypes_UF, android.R.layout.simple_spinner_item);
        placeTypes_Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_place.setAdapter(placeTypes_Adapter);
        spinner_place.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                resetNearbyPlaces();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                resetNearbyPlaces();
            }
        });

        seekBar_radius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                resetNearbyPlaces();
                radius = (seekBar.getProgress() + 1) * 1000;
                textView_radius.setText(radius/1000 + " " + getString(R.string.Radius));
                updateCamera();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekBar_radius.setMax(49);

        placeTypes = Arrays.asList(getResources().getStringArray(R.array.placeTypes));


        button_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetNearbyPlaces();
                if(AppController.getLastKnownLocation() != null){
                    getNearbyPlaces(AppController.getLastKnownLocation().getLatitude(), AppController.getLastKnownLocation().getLongitude(),radius,placeTypes.get((int) spinner_place.getSelectedItemId()));
                    AppController.getNearbyPlaces().clear();
                }
                else{
                    getLastLocation();
                }

            }
        });


        button_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(nearbyPlacesJsons.size() > 0){
                    ArrayList<String> placesId = new ArrayList<>();
                    for(int i=0; i<nearbyPlacesJsons.size(); i++){
                        placesId.add(nearbyPlacesJsons.get(i).get("place_id"));
                    }
                    Intent intent = new Intent(MainActivity.this, PlaceListActivity.class)
                            .putStringArrayListExtra("placesId",placesId)
                            .putExtra("placeType", spinner_place.getSelectedItem().toString());
                    startActivity(intent);
                }
                else{
                    Snackbar snackbar = Snackbar.make(findViewById(R.id.constraint_layout_main),"No establishment to show", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }
            }
        });


        buildGoogleApiClient();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
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
                getLocationPermission();
            }
        } else {

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case LOCATION_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION);
        }
    }

    private void buildGoogleApiClient() {
         AppController.setGoogleApiClient(new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        getLocationPermission();
                        getLastLocation();
                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed( ConnectionResult connectionResult ){
                        if( connectionResult.hasResolution() ){
                            try {
                                // Start an Activity that tries to resolve the error
                                connectionResult.startResolutionForResult(MainActivity.this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
                            }catch( IntentSender.SendIntentException e ){
                                e.printStackTrace();
                            }
                        }else{
                            Log.e("E:","Location services connection failed with code " + connectionResult.getErrorCode());
                        }
                    }
                })
                .addApi(LocationServices.API)
                 .addApi(Places.GEO_DATA_API)
                .build());
         AppController.getGoogleApiClient().connect();
    }

    @SuppressLint("MissingPermission")
    public void getLastLocation(){
        getLocationPermission();
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            AppController.setLastKnownLocation(location);
                            if(mGoogleMap!= null){
                                CircleOptions circleOptions = new CircleOptions()
                                        .center(new LatLng(AppController.getLastKnownLocation().getLatitude(), AppController.getLastKnownLocation().getLongitude()))
                                        .radius(radius * 1000)
                                        .strokeWidth(10)
                                        .strokeColor(Color.GREEN)
                                        .fillColor(Color.argb(128, 255, 0, 0));

                                radiusCircle = mGoogleMap.addCircle(circleOptions);
                                updateCamera();
                            }
                        }
                    }
                });
    }

    private void updateLocationUI() {
        if (mGoogleMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mGoogleMap.setMyLocationEnabled(true);
                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mGoogleMap.setMyLocationEnabled(false);
                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
                AppController.setLastKnownLocation(null);
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
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
                        nearbyPlacesJsons = DataParser.parse(result, next_page_token_builder);
                        if(nearbyPlacesJsons.size()>0){
                            button_list.setVisibility(View.VISIBLE);
                            setMarkers(nearbyPlacesJsons);
                        }
                        else{
                            button_list.setVisibility(View.INVISIBLE);
                            Snackbar snackbar = Snackbar.make(findViewById(R.id.constraint_layout_main),"No places to show", Snackbar.LENGTH_SHORT);
                            snackbar.show();
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        Log.e(TAG,"onErrorResponse: Error=" + error.getMessage());
                        Snackbar snackbar = Snackbar.make(findViewById(R.id.constraint_layout_main),"Request failed, please retry", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    }
                }
        );
        AppController.getInstance().addToRequestQueue(request);
    }

    private void setMarkers(ArrayList<HashMap<String,String>> nearbyPlaces){
        for(int i=0; i<nearbyPlaces.size(); i++){
            HashMap<String,String> place = nearbyPlaces.get(i);
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(new LatLng(Double.parseDouble(place.get("lat")),Double.parseDouble(place.get("lng"))))
                    .title(place.get("name") + " : " + place.get("vicinity"))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            markerList.add(mGoogleMap.addMarker(markerOptions));
        }
    }

    private void updateCamera() {
        if(AppController.getGoogleApiClient() != null && radiusCircle != null && AppController.getGoogleApiClient().isConnected()) {
            radiusCircle.setRadius(radius);
            LatLng latLng = new LatLng(AppController.getLastKnownLocation().getLatitude(),AppController.getLastKnownLocation().getLongitude());

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLng)
                    .zoom(getZoomLevel(radiusCircle)-(float)0.75)
                    .build();
            mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    public float getZoomLevel(Circle circle) {
        float zoomLevel = 0;
        if (circle != null){
            double radius = circle.getRadius();
            double scale = radius / 500;
            zoomLevel = (float) (16 - Math.log(scale) / Math.log(2));
        }
        return zoomLevel;
    }

    public void resetNearbyPlaces(){
        for(int x=markerList.size()-1; x>=0; x--){
            markerList.get(x).remove();
            markerList.remove(x);
        }
        if(nearbyPlacesJsons.size() > 0){
            nearbyPlacesJsons.clear();
        }
        if(button_list.getVisibility() == View.VISIBLE){
            button_list.setVisibility(View.INVISIBLE);
        }
    }
}
