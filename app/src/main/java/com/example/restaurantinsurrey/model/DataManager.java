package com.example.restaurantinsurrey.model;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

public class DataManager {

    final public static String TAG = "DataManager";

    //Making it a singleton.
    static private DataManager instance = null;
    static public void createInstance(Context context){
        instance = new DataManager(context);
    }
    static public DataManager getInstance(){
        if(instance == null){
            Log.e(TAG, "getInstance: Should call createInstance() at least once." );
            return null;
        }
        return instance;
    }
    //----------------------------

    final private static String RESTAURANTS_FILE = "restaurants_itr1.csv";
    final private static String REPORTS_FILE = "inspectionreports_itr1.csv";

    private ArrayList<ReportData> reportData;
    private ArrayList<RestaurantData> restaurantData;

    private DataManager(Context context) {
        ArrayList<String> restaurantStrings = DataFileProcessor.readLines(context, RESTAURANTS_FILE);
        this.restaurantData = RestaurantData.getAllRestaurants(restaurantStrings);

        ArrayList<String> reportStrings = DataFileProcessor.readLines(context, REPORTS_FILE);
        this.reportData = ReportData.getAllReports(reportStrings);
    }

    public int getRestaurantsSize(){
        return restaurantData.size();
    }

    public int getReportsSize(){
        return reportData.size();
    }

    public ReportData getReport(int index){
        return reportData.get(index);
    }

    public RestaurantData getRestaurant(int index){
        return restaurantData.get(index);
    }

    @Override
    public String toString() {
        return "DataManager{" +
                "reportData:" + reportData.size() +
                ", restaurantData:" + restaurantData.size() +
                '}';
    }
}
