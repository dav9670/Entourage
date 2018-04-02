package com.david.entourage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
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
import com.google.android.gms.location.places.GeoDataClient;
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

import static com.david.entourage.AppConfig.*;

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
    private GoogleApiClient mGoogleApiClient;
    private FusedLocationProviderClient mFusedLocationClient;
    private GeoDataClient mGeoDataClient;

    private int radius;
    private boolean mLocationPermissionGranted;
    private Circle mCircle;
    private List<String> placeTypes;
    private ArrayList<Marker> markerList;
    private ArrayList<HashMap<String,String>> nearbyPlaces;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        markerList = new ArrayList<>();
        nearbyPlaces = new ArrayList<>();

        spinner_place = findViewById(R.id.place_spinner);
        textView_radius = findViewById(R.id.textView_radius);
        seekBar_radius = findViewById(R.id.seekBar_radius);
        button_search = findViewById(R.id.button_search);
        button_list = findViewById(R.id.button_list);
        fragMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.fragMap));
        fragMap.getMapAsync(this);

        //Sets text inside spinner
        placeTypes_Adapter = ArrayAdapter.createFromResource(this, R.array.placeTypes_UF, android.R.layout.simple_spinner_item);
        placeTypes_Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_place.setAdapter(placeTypes_Adapter);

        seekBar_radius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(markerList != null){
                    for(int x=markerList.size()-1; x>=0; x--){
                        markerList.get(x).remove();
                        markerList.remove(x);
                    }
                }
                if(nearbyPlaces.size() > 0){
                    nearbyPlaces.clear();
                }
                if(button_list.getVisibility() == View.VISIBLE){
                    button_list.setVisibility(View.INVISIBLE);
                }
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
                if(markerList != null){
                    for(int i=markerList.size()-1; i>=0; i--){
                        markerList.get(i).remove();
                        markerList.remove(i);
                    }
                }
                if(nearbyPlaces.size() > 0){
                    nearbyPlaces.clear();
                }
                getNearbyPlaces(AppController.getLastKnownLocation().getLatitude(), AppController.getLastKnownLocation().getLongitude(),radius,placeTypes.get((int) spinner_place.getSelectedItemId()));
            }
        });


        button_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(nearbyPlaces.size() > 0){
                    ArrayList<String> placesId = new ArrayList<>();
                    for(int i=0; i<nearbyPlaces.size(); i++){
                        placesId.add(nearbyPlaces.get(i).get("place_id"));
                    }
                    Intent intent = new Intent(MainActivity.this, PlaceList.class)
                            .putStringArrayListExtra("placesId",placesId);
                    startActivity(intent);
                }
                else{
                    debugMessage("No establishment to show");
                }
            }
        });


        buildGoogleApiClient();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mGeoDataClient = Places.getGeoDataClient(this,null);
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
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
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

                                                mCircle = mGoogleMap.addCircle(circleOptions);
                                                updateCamera();
                                            }
                                        }
                                    }
                                });
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
                .build();
        mGoogleApiClient.connect();
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
                        nearbyPlaces = DataParser.parse(result);
                        if(nearbyPlaces.size()>0){
                            button_list.setVisibility(View.VISIBLE);
                            setMarkers(nearbyPlaces);
                        }
                        else{
                            button_list.setVisibility(View.INVISIBLE);
                        }
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
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected() && mCircle != null){
            mCircle.setRadius(radius);
            LatLng latLng = new LatLng(AppController.getLastKnownLocation().getLatitude(),AppController.getLastKnownLocation().getLongitude());

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLng)
                    .zoom(getZoomLevel(mCircle)-(float)0.5)
                    .build();
            mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
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

    public void debugMessage(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        toast.show();
    }
}
