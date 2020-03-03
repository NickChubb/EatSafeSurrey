package com.example.restaurantinsurrey;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class RestaurantActivity extends AppCompatActivity implements OnMapReadyCallback {

    private String address = "1600 Pennsylvania Ave NW Washington DC 20502";

    GoogleMap mapAPI;
    SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);
        Toolbar toolbar = findViewById(R.id.singleRestaurantToolbar);
        setSupportActionBar(toolbar);

        GeoLocation geoLocation = new GeoLocation();
        geoLocation.getAddress(address, getApplicationContext(), new GeoHandler());

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapAPI);
        mapFragment.getMapAsync(this);


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mapAPI = googleMap;

        LatLng SFUSurrey = new LatLng(49.187077, -122.848889);
        mapAPI.addMarker(new MarkerOptions().position(SFUSurrey).title("SFUSurrey"));
        mapAPI.moveCamera(CameraUpdateFactory.newLatLng(SFUSurrey));
    }


    private class GeoHandler extends Handler {
        @Override
        public  void handleMessage(Message message){

            String locationAddress;

            switch (message.what){
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;

                default:
                    locationAddress = null;
            }


        }

    }

    public static Intent makeLaunchIntent(Context context){
        Intent intent = new Intent(context, RestaurantActivity.class);
        return intent;
    }


}
//Resources:https://www.youtube.com/watch?v=QquRXzJguQM