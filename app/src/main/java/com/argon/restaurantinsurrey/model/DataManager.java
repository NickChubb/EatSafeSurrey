package com.argon.restaurantinsurrey.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/*
 *   This is a singleton manager class to manage all restaurant/violation data.
 *
 */

public class DataManager {

    final public static String TAG = "DataManager";

    //Making it a singleton.
    static private DataManager instance = null;
    static public void createInstance(Context context){
        if(instance == null) {
            instance = new DataManager(context);
        }
    }

    static public DataManager getInstance(){
        if(instance == null){
            Log.e(TAG, "getInstance: Should call createInstance() at least once." );
            return null;
        }
        return instance;
    }


    //----------------------------

    final private static String DEFAULT_RESTAURANTS_FILE = "restaurants_itr1.csv";
    final private static String DEFAULT_INSPECTIONS_REPORTS_FILE = "inspectionreports_itr1.csv";
    final private static String DEFAULT_ALL_REPORTS_FILE = "AllViolations.txt";

    final private static String RESTAURANTS_FILE = "restaurants.csv";
    final private static String INSPECTIONS_REPORTS_FILE = "inspection_reports.csv";

    private ArrayList<ReportData> reportData;
    private ArrayList<RestaurantData> restaurantData;
    private ArrayList<ViolationData> validViolations;

    private DataManager(Context context) {
        File restaurantsFile = new File(context.getFilesDir(), RESTAURANTS_FILE);
        ArrayList<String> restaurantStrings = null;
        if(restaurantsFile.exists()){
            restaurantStrings = DataFactory.readLinesFromFile(restaurantsFile);
        }else {
            restaurantStrings = DataFactory.readLinesFromAssets(context, DEFAULT_RESTAURANTS_FILE);
        }

        this.restaurantData = RestaurantData.getAllRestaurants(context, restaurantStrings);
        this.restaurantData.sort((o1, o2) -> o1.getName().compareTo(o2.getName()));

        File reportsFile = new File(context.getFilesDir(),INSPECTIONS_REPORTS_FILE);
        ArrayList<String> reportStrings = null;
        if(reportsFile.exists()) {
            reportStrings = DataFactory.readLinesFromFile(reportsFile);
        } else {
            reportStrings = DataFactory.readLinesFromAssets(context, DEFAULT_INSPECTIONS_REPORTS_FILE);
        }

        ArrayList<String> validReportsStrings = DataFactory.readLinesFromAssets(context, DEFAULT_ALL_REPORTS_FILE);
        validViolations = ViolationData.getAllViolations(validReportsStrings);

        this.reportData = ReportData.getAllReports(reportStrings, validViolations);
        this.reportData.sort((o1, o2) -> {
            int ret = o1.getInspectionDate().after(o2.getInspectionDate())? -1: 1;
            return ret;
        });
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

    public ArrayList<RestaurantData> getAllRestaurants(){ return new ArrayList<>(restaurantData); }

    public ArrayList<ReportData> getAllReports(){
        return new ArrayList<>(reportData);
    }

    public ArrayList<ReportData> getReports(String trackingNumber){
        ArrayList<ReportData> ret = new ArrayList<>();
        for(int i = 0; i < getReportsSize(); i++){
            ReportData report = getReport(i);
            if(report.getTrackingNumber().equals(trackingNumber)){
                ret.add(report);
            }
        }
        return ret;
    }

    public RestaurantData getRestaurant(String trackingNumber){
        for(int i = 0; i < getRestaurantsSize(); i++){
            RestaurantData restaurant = getRestaurant(i);
            if(restaurant.getTrackingNumber().equals(trackingNumber)){
                return restaurant;
            }
        }
        return null;
    }

    public ReportData getLastInspection(String trackingNumber){
        ReportData ret = null;
        ArrayList<ReportData> reports = getReports(trackingNumber);
        if (reports.size() > 0){
            ret = reports.get(0);
        }
        return ret;
    }

    public ViolationData getViolation(int violationNumber){
        for(ViolationData violationData: validViolations){
            if(violationNumber == violationData.getViolationNumber()){
                return violationData;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "DataManager{" +
                "reportData:" + reportData.size() +
                ", restaurantData:" + restaurantData.size() +
                '}';
    }
}
