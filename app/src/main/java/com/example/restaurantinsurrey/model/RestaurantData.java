package com.example.restaurantinsurrey.model;

import androidx.appcompat.app.AppCompatActivity;

public class RestaurantData extends AppCompatActivity {

    private String name;
    private String address;

    /* Need to add way to parse inspections */

    // Singleton Support
    private static RestaurantData instance;

    // Constructor private, must initialize by calling getInstance()
    private RestaurantData() {

    }

    // If an instance of the object already exists, return it;
    // Else -> create a new instance
    public static RestaurantData getInstance(){
        if(instance == null){
            instance = new RestaurantData();
        }

        return instance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
