package com.argon.restaurantinsurrey;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.argon.restaurantinsurrey.model.DataManager;
import com.argon.restaurantinsurrey.model.ReportData;
import com.argon.restaurantinsurrey.model.RestaurantData;
import com.argon.restaurantinsurrey.ui.InspectionListAdapter;
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

public class RestaurantActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final String INDEX_VALUE = "index_value";
    public static final String NO_INSPECTION_REPORTS_AVAILABLE = "No inspection reports available";
    private String address;
    private String restaurantName;
    private float zoomLevel = 16.0f;

    GoogleMap mapAPI;
    SupportMapFragment mapFragment;

    private ListView inspectionListView;
    private int GREEN_WARNING_SIGN = R.drawable.green_warning_sign;
    private int YELLOW_WARNING_SIGN = R.drawable.yellow_warning_sign;
    private int RED_WARNING_SIGN = R.drawable.red_warning_sign;

    private int NUMBER_OF_INSPECTIONS = 3;
    private double LONGITUDE;
    private double LATITUDE;

    private DataManager manager;
    private ArrayList<RestaurantData> restaurants;
    private RestaurantData restaurantData;
    private ArrayList<ReportData> allReports;
    private ArrayList<ReportData> restaurantReports;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);
        Toolbar toolbar = findViewById(R.id.singleRestaurantToolbar);
        setSupportActionBar(toolbar);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapAPI);
        mapFragment.getMapAsync(this);

        manager = DataManager.getInstance();
        allReports = manager.getAllReports();


        restaurantData = getSingleRestaurant();
        setUpRestaurantInfo(restaurantData);
        restaurantReports = getReports(allReports, restaurantData);

        setUpInspectionListView(restaurantReports, restaurantData);
    }

    private RestaurantData getSingleRestaurant() {
        manager = DataManager.getInstance();
        restaurants = manager.getAllRestaurants();
        Intent intent = getIntent();
        int restaurantIndex = intent.getIntExtra(INDEX_VALUE, 0);
        restaurantData = restaurants.get(restaurantIndex);
        return restaurantData;
    }

    private void setUpRestaurantInfo(RestaurantData restaurant){
        TextView restaurantAddressTV = findViewById(R.id.singleRestaurantAddressTV);
        TextView restaurantNameTV = findViewById(R.id.singleRestaurantNameTV);

        address = restaurant.getAddress();
        restaurantName = restaurant.getName();

        restaurantAddressTV.setText(address);
        restaurantNameTV.setText(restaurantName);
    }

    private void setUpInspectionListView(ArrayList<ReportData> reports, RestaurantData restaurant) {
        String restaurantTrackingNumber = restaurant.getTrackingNumber();
        inspectionListView = (ListView) findViewById(R.id.restaurantInspectionListView);

        if(reports.isEmpty()){
            TextView inspectionTV = (TextView) findViewById(R.id.restaurantInspectionsTV);
            inspectionTV.setText(NO_INSPECTION_REPORTS_AVAILABLE);
        }

        InspectionListAdapter adapter = new InspectionListAdapter(this, R.layout.custom_inspection_list_layout, reports);
        inspectionListView.setAdapter(adapter);

        inspectionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = InspectionDetailsActivity.makeLaunchIntent(RestaurantActivity.this, restaurantTrackingNumber, position);
                startActivity(intent);
            }
        });

    }

    private ArrayList<ReportData> getReports(ArrayList<ReportData> allReports, RestaurantData restaurant){

        String restaurantTrackingNumber = restaurant.getTrackingNumber();
        System.out.println(restaurantTrackingNumber);
        List<ReportData> reportsOfRestaurant = new ArrayList<>();

        for(ReportData report : allReports){
            if(report.getTrackingNumber().equals(restaurantTrackingNumber)){
                reportsOfRestaurant.add(report);
            }

        }

        return (ArrayList<ReportData>) reportsOfRestaurant;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        getCoordinates();
        mapAPI = googleMap;
        LatLng SFUSurrey = new LatLng(LATITUDE, LONGITUDE);
        mapAPI.addMarker(new MarkerOptions().position(SFUSurrey).title(restaurantName));
        mapAPI.moveCamera(CameraUpdateFactory.newLatLng(SFUSurrey));
        mapAPI.moveCamera(CameraUpdateFactory.newLatLngZoom(SFUSurrey,zoomLevel));
    }


    private void getCoordinates() {
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        restaurantData = getSingleRestaurant();
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


    public static Intent makeLaunchIntent(Context context, int index){
        Intent intent = new Intent(context, RestaurantActivity.class);
        intent.putExtra(INDEX_VALUE, index);
        return intent;
    }


}
//Resources:
// https://www.youtube.com/watch?v=tLVz5wmNyrw
// https://www.youtube.com/watch?v=QquRXzJguQM