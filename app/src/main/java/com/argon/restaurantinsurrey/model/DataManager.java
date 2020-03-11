package com.argon.restaurantinsurrey.model;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Comparator;

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

    final private static String RESTAURANTS_FILE = "restaurants_itr1.csv";
    final private static String INSPECTIONS_REPORTS_FILE = "inspectionreports_itr1.csv";
    final private static String ALL_REPORTS_FILE = "AllViolations.txt";

    private Context context;
    private ArrayList<ReportData> reportData;
    private ArrayList<RestaurantData> restaurantData;
    private ArrayList<ViolationData> validViolations;

    private DataManager(Context context) {
        this.context = context;

        ArrayList<String> restaurantStrings = DataFactory.readLines(context, RESTAURANTS_FILE);
        this.restaurantData = RestaurantData.getAllRestaurants(restaurantStrings);

        this.restaurantData.sort(new Comparator<RestaurantData>() {
            @Override
            public int compare(RestaurantData o1, RestaurantData o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });

        ArrayList<String> reportStrings = DataFactory.readLines(context, INSPECTIONS_REPORTS_FILE);
        ArrayList<String> validReportsStrings = DataFactory.readLines(context, ALL_REPORTS_FILE);
        validViolations = ViolationData.getAllViolations(validReportsStrings);

        this.reportData = ReportData.getAllReports(reportStrings, validViolations);
        this.reportData.sort(new Comparator<ReportData>() {
            @Override
            public int compare(ReportData o1, ReportData o2) {
                int ret = o1.getInspectionDate().after(o2.getInspectionDate())? -1: 1;
                return ret;
            }
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

    public ArrayList<RestaurantData> getAllRestaurants(){ return new ArrayList<RestaurantData>(restaurantData); }

    public ArrayList<ReportData> getAllReports(){
        return new ArrayList<ReportData>(reportData);
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
