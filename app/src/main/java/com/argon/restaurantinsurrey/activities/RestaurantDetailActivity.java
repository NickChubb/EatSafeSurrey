package com.argon.restaurantinsurrey.activities;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.argon.restaurantinsurrey.R;
import com.argon.restaurantinsurrey.model.DataManager;
import com.argon.restaurantinsurrey.model.ReportData;
import com.argon.restaurantinsurrey.model.RestaurantData;
import com.argon.restaurantinsurrey.ui.InspectionRecyclerAdapter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/*
 *   This is the activity for showing the details of each restaurant
 *
 */

public class RestaurantDetailActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final String INDEX_VALUE = "index_value";

    private String address;
    private String restaurantName;
    private final float INITIAL_ZOOM_LEVEL = 16.0f;

    private GoogleMap mapAPI;
    private SupportMapFragment mapFragment;

    private double LONGITUDE;
    private double LATITUDE;

    private RestaurantData restaurantData;
    private ArrayList<ReportData> restaurantReports;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_detail);
        Toolbar toolbar = findViewById(R.id.singleRestaurantToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setUpVariables();

        setUpUI();

        populateListView();
    }

    private void setUpVariables() {
        DataManager manager = DataManager.getInstance();

        ArrayList<ReportData> allReports = manager.getAllReports();
        ArrayList<RestaurantData> restaurants = manager.getAllRestaurants();

        Intent intent = getIntent();
        int restaurantIndex = intent.getIntExtra(INDEX_VALUE, 0);
        restaurantData = restaurants.get(restaurantIndex);

        address = restaurantData.getAddress();
        restaurantName = restaurantData.getName();

        String trackingNumber = restaurantData.getTrackingNumber();
        restaurantReports = manager.getReports(trackingNumber);
    }

    private void setUpUI(){
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_restaurant_detail);
        mapFragment.getMapAsync(this);

        TextView restaurantAddressTextView = findViewById(R.id.text_restaurant_detail_address);
        TextView restaurantNameTextView = findViewById(R.id.text_restaurant_detail_name);

        restaurantAddressTextView.setText(address);
        restaurantNameTextView.setText(restaurantName);

        if(restaurantReports.isEmpty()){
            TextView inspectionTextView = findViewById(R.id.text_restaurant_detail_inspections);
            inspectionTextView.setText(getString(R.string.title_no_inspections));
        }
    }

    private void populateListView() {
        String restaurantTrackingNumber = restaurantData.getTrackingNumber();
        RecyclerView list = findViewById(R.id.list_restaurant_detail_inspection);

        list.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        list.setLayoutManager(new LinearLayoutManager(this));

        InspectionRecyclerAdapter adapter = new InspectionRecyclerAdapter(this, restaurantTrackingNumber);
        list.setAdapter(adapter);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        getCoordinates();
        mapAPI = googleMap;
        LatLng SFUSurrey = new LatLng(LATITUDE, LONGITUDE);
        mapAPI.addMarker(new MarkerOptions().position(SFUSurrey).title(restaurantName));
        mapAPI.moveCamera(CameraUpdateFactory.newLatLng(SFUSurrey));
        mapAPI.moveCamera(CameraUpdateFactory.newLatLngZoom(SFUSurrey,INITIAL_ZOOM_LEVEL));
    }


    private void getCoordinates() {
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        address = restaurantData.getAddress();

        try {
            List addressList = geocoder.getFromLocationName(address,1);

            if(addressList != null && addressList.size() > 0){
                Address address = (Address) addressList.get(0);
                LATITUDE = address.getLatitude();
                LONGITUDE = address.getLongitude();
            }
        } catch (IOException e) {
            Log.d("failed", "ERROR in geolocation\n");
            e.printStackTrace();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    public static Intent makeLaunchIntent(Context context, int index){
        Intent intent = new Intent(context, RestaurantDetailActivity.class);
        intent.putExtra(INDEX_VALUE, index);
        return intent;
    }


}
//Resources:
// https://www.youtube.com/watch?v=tLVz5wmNyrw
// https://www.youtube.com/watch?v=QquRXzJguQM
