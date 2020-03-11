package com.argon.restaurantinsurrey;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import com.argon.restaurantinsurrey.model.DataManager;
import com.argon.restaurantinsurrey.model.RestaurantData;

public class InspectionActivity extends AppCompatActivity {

    // Class Data
    private int index;
    private DataManager manager;
    private RestaurantData restaurant;


    public static Intent makeLaunchIntent(Context c) {
        Intent intent = new Intent(c, RestaurantInfoActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspection);

        // Get the int index passed through the intent
        // Default value of -1 for error checking if intent is created without putExtra
        index = getIntent().getIntExtra("index", -1);


        // Get instance of the RestaurantManager singleton
        manager = DataManager.getInstance();


        // Get restaurant object from the RestaurantManager object
        restaurant = manager.getRestaurant(index);

        /*
            Add way to get inspections from the RestaurantData

            - InspectionManager?

         */

        ListView violationList = findViewById(R.id.listViolations);



    }
}
