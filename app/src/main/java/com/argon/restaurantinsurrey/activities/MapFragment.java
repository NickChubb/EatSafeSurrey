package com.argon.restaurantinsurrey.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.argon.restaurantinsurrey.R;
import com.argon.restaurantinsurrey.model.DataManager;
import com.argon.restaurantinsurrey.model.ReportData;
import com.argon.restaurantinsurrey.model.RestaurantData;
import com.argon.restaurantinsurrey.ui.ClusterMarker;
import com.argon.restaurantinsurrey.ui.MyClusterManagerRenderer;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback {

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
    private View viewFrag;

    private AlertDialog dialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewFrag = inflater.inflate(R.layout.fragment_map,container,false);

        setUpVariables();
        getLocationPermission();

        return viewFrag;
    }

    private void addMapMarkers(){

        if(mGoogleMap != null ) {

            if (clusterManager == null) {
                clusterManager = new ClusterManager<ClusterMarker>(getActivity(), mGoogleMap);
            }
            mGoogleMap.setOnCameraIdleListener(clusterManager);
            if (clusterManagerRenderer == null) {
                clusterManagerRenderer = new MyClusterManagerRenderer(getActivity(), mGoogleMap, clusterManager);
                clusterManager.setRenderer(clusterManagerRenderer);
            }
            for(int i = 0; i < restaurantLatLngList.size(); i++){

                LatLng restaurantLatLng = new LatLng(restaurantLatLngList.get(i).latitude,
                        restaurantLatLngList.get(i).longitude);
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
                            "Address: " + snippet,
                            image,
                            ReportData.HazardRating.LOW,
                            i
                    );
                }
                else {
                    ReportData.HazardRating hazardRating = reportDataList.get(0).getHazardRating();
                    int image;
                    switch (hazardRating){
                        case HIGH:
                            image = R.drawable.red_warning_sign;
                            break;
                        case MODERATE:
                            image = R.drawable.yellow_warning_sign;

                            break;
                        case LOW:
                            image = R.drawable.green_warning_sign;
                            break;
                        default:
                            image = R.drawable.grey_warning_sign;
                    }
                    newClusterMarker = new ClusterMarker(
                            restaurantLatLng,
                            title,
                            "Address: " + snippet,
                            image,
                            hazardRating,
                            i
                    );
                }

                clusterManager.addItem(newClusterMarker);
                clusterMarkerList.add(newClusterMarker);

            }
            mGoogleMap.setOnCameraIdleListener(clusterManager);
            mGoogleMap.setOnMarkerClickListener(clusterManager);
            mGoogleMap.setOnInfoWindowClickListener(clusterManager);

            clusterManager.setOnClusterItemInfoWindowClickListener(new ClusterManager.OnClusterItemInfoWindowClickListener() {
                @Override
                public void onClusterItemInfoWindowClick(ClusterItem item) {
                    ClusterMarker marker = (ClusterMarker) item;
                    switch (marker.getHazardRating()){
                        case LOW:
                            showLowHazardDialog(marker.getTitle(),marker.getSnippet(), marker.getIndex());
                            break;
                        case MODERATE:
                            showMediumHazardDialog(marker.getTitle(),marker.getSnippet(), marker.getIndex());
                            break;
                        case HIGH:
                            showHighHazardDialog(marker.getTitle(),marker.getSnippet(), marker.getIndex());
                            break;
                        default:
                            showLowHazardDialog(marker.getTitle(),marker.getSnippet(), marker.getIndex());
                            break;
                    }

                }
            });

            clusterManager.cluster();
        }
    }

    private void showMediumHazardDialog(String name, String address, int index) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlerDialogTheme);  //
        View view = LayoutInflater.from(getActivity()).inflate(                                         //
                R.layout.custom_medium_hazard_dialog,
                (ConstraintLayout) viewFrag.findViewById(R.id.medium_hazard_layout_dialog)
        );
        builder.setView(view);
        ImageView hazardIcon = (ImageView) view.findViewById(R.id.image_medium_hazard_icon);
        TextView hazardRatingTextView = (TextView)view.findViewById(R.id.text_medium_hazard_dialog_hazard_level);
        TextView restaurantNameTextView = (TextView) view.findViewById(R.id.text_medium_hazard_dialog_restaurant_name);
        TextView restaurantAddressTextView = (TextView) view.findViewById(R.id.text_medium_hazard_dialog_restaurant_address);
        Button seeReportsBtn = (Button) view.findViewById(R.id.medium_hazard_dialog_go_to_restaurant_btn);

        restaurantNameTextView.setText(name);
        restaurantAddressTextView.setText(address);
        hazardRatingTextView.setText(R.string.text_map_activity_medium);
        seeReportsBtn.setText(R.string.text_map_activity_see_reports);
        hazardIcon.setImageResource(R.drawable.ic_warning_black_50dp);

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.medium_hazard_dialog_go_to_restaurant_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = RestaurantDetailActivity.makeLaunchIntent(getActivity(), index); //
                startActivity(intent);
            }
        });

        if(alertDialog.getWindow() != null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        alertDialog.show();
    }
    private void showHighHazardDialog(String name, String address, int index) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlerDialogTheme);      //
        View view = LayoutInflater.from(getActivity()).inflate(                                             //
                R.layout.custom_high_hazard_dialog,
                (ConstraintLayout) viewFrag.findViewById(R.id.high_hazard_layout_dialog_container)
        );
        builder.setView(view);
        ImageView hazardIcon = (ImageView) view.findViewById(R.id.image_high_hazard_icon);
        TextView hazardRatingTextView = (TextView)view.findViewById(R.id.text_high_hazard_dialog_hazard_level);
        TextView restaurantNameTextView = (TextView) view.findViewById(R.id.text_high_hazard_dialog_restaurant_name);
        TextView restaurantAddressTextView = (TextView) view.findViewById(R.id.text_high_hazard_dialog_restaurant_address);
        Button seeReportsBtn = (Button) view.findViewById(R.id.high_hazard_dialog_go_to_restaurant_btn);

        restaurantNameTextView.setText(name);
        restaurantAddressTextView.setText(address);
        hazardRatingTextView.setText(R.string.text_map_activity_high);
        seeReportsBtn.setText(R.string.text_map_activity_see_reports);
        hazardIcon.setImageResource(R.drawable.ic_not_interested_black_50dp);

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.high_hazard_dialog_go_to_restaurant_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = RestaurantDetailActivity.makeLaunchIntent(getActivity(), index);        //
                startActivity(intent);
            }
        });

        if(alertDialog.getWindow() != null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        alertDialog.show();
    }

    private void showLowHazardDialog(String name, String address, int index) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlerDialogTheme);      //
        View view = LayoutInflater.from(getActivity()).inflate(                                             //
                R.layout.custom_low_hazard_dialog,
                (ConstraintLayout) viewFrag.findViewById(R.id.low_hazard_layout_dialog_container)
        );
        builder.setView(view);
        ImageView hazardIcon = (ImageView) view.findViewById(R.id.image_low_hazard_icon);
        TextView hazardRatingTextView = (TextView)view.findViewById(R.id.text_low_hazard_dialog_hazard_level);
        TextView restaurantNameTextView = (TextView) view.findViewById(R.id.text_low_hazard_dialog_restaurant_name);
        TextView restaurantAddressTextView = (TextView) view.findViewById(R.id.text_low_hazard_dialog_restaurant_address);
        Button seeReportsBtn = (Button) view.findViewById(R.id.low_hazard_dialog_go_to_restaurant_btn);

        restaurantNameTextView.setText(name);
        restaurantAddressTextView.setText(address);
        hazardRatingTextView.setText(R.string.text_map_activity_low);
        seeReportsBtn.setText(R.string.text_map_activity_see_reports);
        hazardIcon.setImageResource(R.drawable.ic_check_circle_black_50dp);

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.low_hazard_dialog_go_to_restaurant_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = RestaurantDetailActivity.makeLaunchIntent(getActivity(), index);            //
                startActivity(intent);
            }
        });

        if(alertDialog.getWindow() != null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        alertDialog.show();
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
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
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
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fragment_map);      //????????//
        mapFragment.getMapAsync(this);


    }


    private void getLocationPermission(){
        String[] permissions = {FINE_LOCATION,COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(getActivity(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

            if(ContextCompat.checkSelfPermission(getActivity(),
                    COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                locationPermissionsGranted = true;
                initializeMap();
            }
            else{
                ActivityCompat.requestPermissions(getActivity(),
                        permissions,LOCATION_PERMISSION_REQUEST_CODE);
            }
        }
        else{
            ActivityCompat.requestPermissions(getActivity(),
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
