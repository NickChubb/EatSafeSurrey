package com.example.restaurantinsurrey;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.restaurantinsurrey.model.DataManager;
import com.example.restaurantinsurrey.model.RestaurantData;
import com.example.restaurantinsurrey.ui.RestaurantRecyclerAdapter;

import java.util.ArrayList;

public class RestaurantListActivity extends AppCompatActivity {

    final public static String TAG = "RestaurantListActivity";

    private DataManager manager;
    private ArrayList<RestaurantData> restaurants;

    private RecyclerView.LayoutManager layoutManager;
    private RestaurantRecyclerAdapter adapter;


    public static Intent makeLaunchIntent(Context c) {
        Intent intent = new Intent(c, RestaurantListActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_list);
        Toolbar toolbar = findViewById(R.id.singleRestaurantToolbar);
        setSupportActionBar(toolbar);


        DataManager.createInstance(this);

        manager = DataManager.getInstance();



        // Search bar

        populateListView();

    }

    private void populateListView() {

        restaurants = manager.getAllRestaurants();

        RecyclerView list = findViewById(R.id.recRestaurants);
        list.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);

        list.setLayoutManager(layoutManager);

        adapter = new RestaurantRecyclerAdapter(restaurants, this);

        list.setAdapter(adapter);

    }

}
