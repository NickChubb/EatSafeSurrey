package com.example.restaurantinsurrey;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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
    private float zoomLevel = 16.0f;

    GoogleMap mapAPI;
    SupportMapFragment mapFragment;

    private ListView inspectionListView;
    private int[] warningSigns = {R.drawable.green_warning_sign};
    private int NUMBER_OF_INSPECTIONS = 1;

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
            TextView inspectionDate = view.findViewById(R.id.inspectionDateTV);

            warningSign.setImageResource(warningSigns[position]);
            inspectionDate.setText("A date/ days ago");
            return view;
        }
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {

        mapAPI = googleMap;

        LatLng SFUSurrey = new LatLng(49.187077, -122.848889);
        mapAPI.addMarker(new MarkerOptions().position(SFUSurrey).title("SFUSurrey"));
        mapAPI.moveCamera(CameraUpdateFactory.newLatLng(SFUSurrey));
        mapAPI.moveCamera(CameraUpdateFactory.newLatLngZoom(SFUSurrey,zoomLevel));
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