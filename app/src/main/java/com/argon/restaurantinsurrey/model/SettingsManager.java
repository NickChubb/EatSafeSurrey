package com.argon.restaurantinsurrey.model;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import java.util.ArrayList;

public class SettingsManager {

    final public static String TAG = "SettingsManager";

    private DataManager manager;

    private final Context context;

    private ArrayList<RestaurantData> favouriteRestaurants;

    //Making it a singleton.
    static private SettingsManager instance = null;


    static public void createInstance(Context context){
        if(instance == null) {
            instance = new SettingsManager(context);
        }else{
            Log.e(TAG, "createInstance: createInstance has already been called, use getInstance." );
        }
    }

    static public SettingsManager getInstance(){
        if(instance == null){
            Log.e(TAG, "getInstance: Should call createInstance() at least once." );
            return null;
        }
        return instance;
    }

    private SettingsManager(Context context) {
        this.context = context;

        this.manager = DataManager.getInstance();

    }

    public void addFavourite(String trackingNumber){
        // get add restaurant to favourite by tracking number.

        RestaurantData restaurant = manager.getRestaurant(trackingNumber);

        favouriteRestaurants.add(restaurant);

    }


}
