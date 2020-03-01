package com.example.restaurantinsurrey;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class SingleRestaurant extends AppCompatActivity {

    private String address = "1600 Pennsylvania Ave NW Washington DC 20502";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_restaurant);
        Toolbar toolbar = findViewById(R.id.singleRestaurantToolbar);
        setSupportActionBar(toolbar);

        GeoLocation geoLocation = new GeoLocation();
        geoLocation.getAddress(address, getApplicationContext(), new GeoHandler());

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
        Intent intent = new Intent(context, SingleRestaurant.class);
        return intent;
    }


}
