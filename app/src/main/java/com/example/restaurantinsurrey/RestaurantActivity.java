package com.example.restaurantinsurrey;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class RestaurantActivity extends AppCompatActivity implements OnMapReadyCallback {

    private String address = "15-228 Schoolhouse St, Coquitlam, BC V3K 6V7";
    private String restaurantName = "Big Chicken Town";
    private float zoomLevel = 16.0f;

    GoogleMap mapAPI;
    SupportMapFragment mapFragment;

    private ListView inspectionListView;
    private int GREEN_WARNING_SIGN = R.drawable.green_warning_sign;
    private int YELLOW_WARNING_SIGN = R.drawable.yellow_warning_sign;
    private int RED_WARNING_SIGN = R.drawable.red_warning_sign;



    private int NUMBER_OF_INSPECTIONS = 3;
    private double LONGITUDE;
    private double LATITUDE ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);
        Toolbar toolbar = findViewById(R.id.singleRestaurantToolbar);
        setSupportActionBar(toolbar);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapAPI);
        mapFragment.getMapAsync(this);

        setUpInspectionListView();
    }

    private void setUpInspectionListView() {
        inspectionListView = (ListView) findViewById(R.id.restaurantInspectionListView);

        CustomAdaptor customAdaptor = new CustomAdaptor();
        inspectionListView.setAdapter(customAdaptor);

    }

    private class CustomAdaptor extends BaseAdapter{

        @Override
        public int getCount() {
            return NUMBER_OF_INSPECTIONS;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view = getLayoutInflater().inflate(R.layout.custom_inspection_list_layout,null);


            ImageView warningSign = view.findViewById(R.id.inspectionHarzardLevelImageView);
            TextView inspectionDate = view.findViewById(R.id.inspectionDetailsDateTV);

            if(position == 0) {
                warningSign.setImageResource(GREEN_WARNING_SIGN);
                inspectionDate.setText("2 days ago");
            }
            if(position == 1) {
                warningSign.setImageResource(YELLOW_WARNING_SIGN);
                inspectionDate.setText("4 days ago");
            }
            if(position == 2) {
                warningSign.setImageResource(RED_WARNING_SIGN);
                inspectionDate.setText("6 days ago");
            }
            return view;
        }
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
        String result = null;
        try {
            List addressList = geocoder.getFromLocationName(address,1);

            if(addressList != null && addressList.size() > 0){
                Address address = (Address) addressList.get(0);

                LATITUDE = address.getLatitude();

                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(address.getLatitude()).append("\n");

                LONGITUDE = address.getLongitude();

                stringBuilder.append(address.getLongitude()).append("\n");
                result = stringBuilder.toString();
            }
        } catch (IOException e) {
            Log.d("failed", "ERROR in geolocation\n");
            e.printStackTrace();
        }
    }



    public static Intent makeLaunchIntent(Context context){
        Intent intent = new Intent(context, RestaurantActivity.class);
        return intent;
    }


}
//Resources:
// https://www.youtube.com/watch?v=tLVz5wmNyrw
// https://www.youtube.com/watch?v=QquRXzJguQM
