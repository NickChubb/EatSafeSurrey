package com.argon.restaurantinsurrey.activities;
/*
 *   Displays user's current location and other restaurants in a map
 */

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.argon.restaurantinsurrey.model.DataManager;
import com.argon.restaurantinsurrey.model.ReportData;
import com.argon.restaurantinsurrey.model.RestaurantData;
//import com.argon.restaurantinsurrey.ui.ClusterMarker;
//import com.argon.restaurantinsurrey.ui.MyClusterManagerRenderer;
import com.argon.restaurantinsurrey.ui.ClusterMarker;
import com.argon.restaurantinsurrey.ui.MyClusterManagerRenderer;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.argon.restaurantinsurrey.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.clustering.ClusterManager;
//import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "MapActivity";

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final float DEFAULT_ZOOM = 10f;
    private boolean locationPermissionsGranted = false;
    private FusedLocationProviderClient fusedLocationProviderClient;

    private GoogleMap mGoogleMap;
    private DataManager dataManager;
    private List<RestaurantData> restaurantDataList;
    private List<LatLng> restaurantLatLngList = new ArrayList<>();
    private List<ReportData> reportDataList;
    private ClusterManager clusterManager;
    private MyClusterManagerRenderer clusterManagerRenderer;
    private List<ClusterMarker> clusterMarkerList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setUpVariables();
        getLocationPermission();



    }

    private void addMapMarkers(){

        if(mGoogleMap != null ) {

            if (clusterManager == null) {
                clusterManager = new ClusterManager<ClusterMarker>(this, mGoogleMap);
            }
            mGoogleMap.setOnCameraIdleListener(clusterManager);
            if (clusterManagerRenderer == null) {
                clusterManagerRenderer = new MyClusterManagerRenderer(this, mGoogleMap, clusterManager);
                clusterManager.setRenderer(clusterManagerRenderer);
            }


            for(int i = 0; i < restaurantLatLngList.size(); i++){

                LatLng restaurantLatLng = new LatLng(restaurantLatLngList.get(i).latitude, restaurantLatLngList.get(i).longitude);
                String title = restaurantDataList.get(i).getName();
                String snippet = restaurantDataList.get(i).getAddress();
                String trackingNumber = restaurantDataList.get(i).getTrackingNumber();
                reportDataList = dataManager.getReports(trackingNumber);

                ClusterMarker newClusterMarker;

                if(reportDataList.isEmpty()){
                    int image = R.drawable.green_warning_sign;
                        newClusterMarker = new ClusterMarker(
                            restaurantLatLng,
                            title,
                            snippet,
                            image
                    );
                }
                else {
                    ReportData.HazardRating hazardRating = reportDataList.get(0).getHazardRating();

                    switch (hazardRating){
                        case HIGH:
                            newClusterMarker = new ClusterMarker(
                                    restaurantLatLng,
                                    title,
                                    snippet,
                                    R.drawable.red_warning_sign
                            );
                            break;
                        case MODERATE:
                            newClusterMarker = new ClusterMarker(
                                    restaurantLatLng,
                                    title,
                                    snippet,
                                    R.drawable.yellow_warning_sign
                            );
                            break;
                        case LOW:
                            newClusterMarker = new ClusterMarker(
                                    restaurantLatLng,
                                    title,
                                    snippet,
                                    R.drawable.green_warning_sign
                            );
                            break;
                        default:
                            newClusterMarker = new ClusterMarker(
                                    restaurantLatLng,
                                    title,
                                    snippet,
                                    R.drawable.green_warning_sign
                            );
                    }

                }


                clusterManager.addItem(newClusterMarker);
                clusterMarkerList.add(newClusterMarker);

            }
            mGoogleMap.setOnCameraIdleListener(clusterManager);
            mGoogleMap.setOnMarkerClickListener(clusterManager);

            clusterManager.cluster();
        }
    }


    private void setUpVariables() {
        dataManager = DataManager.getInstance();
        restaurantDataList = dataManager.getAllRestaurants();

        for(RestaurantData restaurantData : restaurantDataList){
            LatLng restaurantLatLng = new LatLng(restaurantData.getLat(), restaurantData.getLon());
            restaurantLatLngList.add(restaurantLatLng);
        }

    }

    private void getDeviceLocation(){
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try{
            if(locationPermissionsGranted){
                Task location = fusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "found location");
                            Location currentLocation = (Location) task.getResult();

                            LatLng currentLatLng = new LatLng(currentLocation.getLatitude(),
                                                        currentLocation.getLongitude());

                            moveCamera(currentLatLng, DEFAULT_ZOOM);
                        }
                        else{
                            Log.d(TAG, "location not found");

                        }
                    }
                });
            }
        }catch (SecurityException e){
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }

    private void moveCamera(LatLng latLng, float zoom){

        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));

    }

    private void initializeMap(){
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_map);
        mapFragment.getMapAsync(this);

    }


    private void getLocationPermission(){
        String[] permissions = {FINE_LOCATION,COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this,
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

            if(ContextCompat.checkSelfPermission(this,
                    COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                locationPermissionsGranted = true;
                initializeMap();
            }
            else{
                ActivityCompat.requestPermissions(this,
                        permissions,LOCATION_PERMISSION_REQUEST_CODE);
            }
        }
        else{
            ActivityCompat.requestPermissions(this,
                    permissions,LOCATION_PERMISSION_REQUEST_CODE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        locationPermissionsGranted = false;

        switch (requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {

                    for(int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            locationPermissionsGranted = false;
                            return;
                        }
                    }
                    locationPermissionsGranted = true;
                    initializeMap();
                }
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mGoogleMap = googleMap;

        if(locationPermissionsGranted){
            getDeviceLocation();
            mGoogleMap.setMyLocationEnabled(true);
        }
        addMapMarkers();

    }


    public static Intent makeLaunchIntent(Context context){
        Intent intent = new Intent(context, MapActivity.class);
        return intent;
    }
}
