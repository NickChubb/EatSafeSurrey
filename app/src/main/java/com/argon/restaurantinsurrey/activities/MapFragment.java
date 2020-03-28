package com.argon.restaurantinsurrey.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.argon.restaurantinsurrey.R;
import com.argon.restaurantinsurrey.model.DataFactory;
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
import com.google.android.gms.tasks.Task;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.List;

/*
    Same as MapActivity but extends fragment for switching between restaurant list and map

 */
public class MapFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = "MapFragment";

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final float DEFAULT_ZOOM = 10f;
    private static final double DEFAULT_RANGE = 0.0001;
    private boolean locationPermissionsGranted = false;

    private GoogleMap mGoogleMap;
    private DataManager dataManager;
    private List<RestaurantData> restaurantDataList;
    private List<LatLng> restaurantLatLngList = new ArrayList<>();
    private List<ReportData> reportDataList;
    private CustomClusterManager<ClusterMarker> clusterManager;
    private MyClusterManagerRenderer clusterManagerRenderer;
    private List<ClusterMarker> clusterMarkerList = new ArrayList<>();
    private View viewFrag;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewFrag = inflater.inflate(R.layout.fragment_map,container,false);
        setUpVariables();
        initializeMap();
        getLocationPermission();
        return viewFrag;
    }

    private void addMapMarkers(){

        if(mGoogleMap != null ) {

            if (clusterManager == null) {
                clusterManager = new CustomClusterManager<>(getActivity(), mGoogleMap);
            }
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

                ReportData.HazardRating hazardRating;
                int image;
                if(reportDataList.isEmpty()) {
                    hazardRating = ReportData.HazardRating.LOW;
                    image = R.drawable.green_warning_sign;
                } else {
                    hazardRating = reportDataList.get(0).getHazardRating();
                    image = DataFactory.getHazardRatingImage(hazardRating);
                }

                ClusterMarker clusterMarker = new ClusterMarker(
                        restaurantLatLng,
                        title,
                        getString(R.string.text_map_activity_address_detail, snippet),
                        image,
                        hazardRating,
                        i
                );
                clusterManager.addItem(clusterMarker);
                clusterMarkerList.add(clusterMarker);

            }
            mGoogleMap.setOnCameraIdleListener(clusterManager);
            mGoogleMap.setOnMarkerClickListener(clusterManager);
            mGoogleMap.setOnInfoWindowClickListener(clusterManager);

            clusterManager.setOnClusterItemInfoWindowClickListener(item -> {
                ClusterMarker marker = item;
                showHazardDialog(marker.getHazardRating(), marker.getTitle(), marker.getSnippet(), marker.getIndex());
            });

            clusterManager.setOnClusterClickListener(cluster -> {
                float maxZoomLevel = mGoogleMap.getMaxZoomLevel();
                float currentZoomLevel = mGoogleMap.getCameraPosition().zoom;

                // only show markers if users is in the max zoom level

                if (currentZoomLevel != maxZoomLevel) {
                    return false;
                }

                if (!clusterManager.itemsInSameLocation(cluster)) {
                    return false;
                }

                // relocate the markers around the current markers position
                int counter = 0;
                float rotateFactor = (360 / (float)cluster.getItems().size());

                List<ClusterMarker> allItems = new ArrayList<>(cluster.getItems());
                for (ClusterMarker item : allItems) {
                    double lat = item.getPosition().latitude + (DEFAULT_RANGE * Math.cos(++counter * rotateFactor));
                    double lng = item.getPosition().longitude + (DEFAULT_RANGE * Math.sin(counter * rotateFactor));
                    LatLng coordinate = new LatLng(lat,lng);
                    ClusterMarker copy = new ClusterMarker(coordinate, item.getTitle(),  item.getSnippet(), item.getIconPicture(),item.getHazardRating(), item.getIndex());
                    clusterManager.removeItem(item);
                    clusterManager.addItem(copy);
                    clusterManager.cluster();
                }
                return true;
            });
            clusterManager.cluster();
        }
    }

    private void showHazardDialog(ReportData.HazardRating rating, String name, String address, int index){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlerDialogTheme);

        int buttonBackground = 0;
        int titleBackground = 0;
        int hazardText = 0;
        int hazardImage = 0;

        switch (rating){
            case HIGH:
                buttonBackground = R.drawable.high_hazard_btn_background;
                titleBackground = R.drawable.high_hazard_background;
                hazardText = R.string.text_high_hazard;
                hazardImage = R.drawable.ic_not_interested_black_50dp;
                break;
            case MODERATE:
                buttonBackground = R.drawable.medium_hazard_btn_background;
                titleBackground = R.drawable.medium_hazard_background;
                hazardText = R.string.text_moderate_hazard;
                hazardImage = R.drawable.ic_warning_black_50dp;
                break;
            case LOW:
                buttonBackground = R.drawable.low_hazard_btn_background;
                titleBackground = R.drawable.low_hazard_background;
                hazardText = R.string.text_low_hazard;
                hazardImage = R.drawable.ic_check_circle_black_50dp;
                break;
            case OTHER:
                buttonBackground = R.drawable.low_hazard_btn_background;
                titleBackground = R.drawable.low_hazard_background;
                hazardText = R.string.text_low_hazard;
                hazardImage = R.drawable.ic_check_circle_black_50dp;
                break;
        }

        View view = LayoutInflater.from(getActivity()).inflate(
                R.layout.custom_hazard_dialog,
                viewFrag.findViewById(R.id.hazard_layout_dialog_container)
        );
        builder.setView(view);
        ImageView hazardIcon = view.findViewById(R.id.image_hazard_icon);
        TextView hazardRatingTextView = view.findViewById(R.id.text_hazard_dialog_hazard_level);
        TextView restaurantNameTextView = view.findViewById(R.id.text_hazard_dialog_restaurant_name);
        TextView restaurantAddressTextView = view.findViewById(R.id.text_hazard_dialog_restaurant_address);
        TextView titleTextView = view.findViewById(R.id.text_hazard_dialog_title);
        Button seeReportsBtn = view.findViewById(R.id.hazard_dialog_go_to_restaurant_btn);

        titleTextView.setBackgroundResource(titleBackground);
        seeReportsBtn.setBackgroundResource(buttonBackground);
        restaurantNameTextView.setText(name);
        restaurantAddressTextView.setText(address);
        hazardRatingTextView.setText(hazardText);
        seeReportsBtn.setText(R.string.text_map_activity_see_reports);
        hazardIcon.setImageResource(hazardImage);

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.hazard_dialog_go_to_restaurant_btn).setOnClickListener(v -> {
            Intent intent = RestaurantDetailActivity.makeLaunchIntent(getActivity(), index);
            startActivity(intent);
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
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        try{
            int locationMode = Settings.Secure.getInt(getContext().getContentResolver(), Settings.Secure.LOCATION_MODE);
            if(locationPermissionsGranted && locationMode != Settings.Secure.LOCATION_MODE_OFF){
                Task location = fusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Log.d(TAG, "found location");
                        Location currentLocation = (Location) task.getResult();

                        if(currentLocation != null) {
                            LatLng currentLatLng = new LatLng(currentLocation.getLatitude(),
                                    currentLocation.getLongitude());

                            moveCamera(currentLatLng);
                        }
                    }
                    else{
                        Log.d(TAG, "location not found");

                    }
                });
                mGoogleMap.setMyLocationEnabled(true);
            }
        }catch (SecurityException e){
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void moveCamera(LatLng latLng){
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, MapFragment.DEFAULT_ZOOM));
    }

    private void initializeMap(){
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fragment_map);      //????????//
        mapFragment.getMapAsync(this);
    }


    private void getLocationPermission(){
        String[] permissions = {FINE_LOCATION,COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(getActivity(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        && ContextCompat.checkSelfPermission(getActivity(), COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            locationPermissionsGranted = true;
        } else {
            this.requestPermissions(permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        locationPermissionsGranted = false;

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {

                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                }
                locationPermissionsGranted = true;
                getDeviceLocation();
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        getDeviceLocation();
        addMapMarkers();
    }

    private static class CustomClusterManager<T extends ClusterItem> extends ClusterManager<T> {
        CustomClusterManager(Context context, GoogleMap map) {
            super(context, map);
        }
         boolean itemsInSameLocation(Cluster<T> cluster) {
            List<T> items = new ArrayList<>(cluster.getItems());
            T item = items.remove(0);
            double longitude = item.getPosition().longitude;
            double latitude = item.getPosition().latitude;
            for (T t : items) {
                if (Double.compare(longitude, t.getPosition().longitude) != 0 && Double.compare(latitude, t.getPosition().latitude) != 0) {
                    return false;
                }
            }
            return true;
        }
    }
}
/*Resources:
* https://github.com/menismu/android-maps-utils/blob/master/demo/src/com/google/maps/android/utils/demo/ClusteringSameLocationActivity.java#L107
* https://www.youtube.com/watch?v=fPFr0So1LmI
* https://www.youtube.com/watch?v=Vt6H9TOmsuo
* https://www.youtube.com/watch?v=U6Z8FkjGEb4&t=610s
*/