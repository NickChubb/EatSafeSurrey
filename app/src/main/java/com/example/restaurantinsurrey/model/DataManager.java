package com.example.restaurantinsurrey.model;

import android.content.Context;
import android.util.Log;

import com.example.restaurantinsurrey.R;

import java.util.ArrayList;
import java.util.Date;

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

    private DataManager(Context context) {
        this.context = context;

        ArrayList<String> restaurantStrings = DataFileProcessor.readLines(context, RESTAURANTS_FILE);
        this.restaurantData = RestaurantData.getAllRestaurants(restaurantStrings);

        ArrayList<String> reportStrings = DataFileProcessor.readLines(context, REPORTS_FILE);
        this.reportData = ReportData.getAllReports(reportStrings);
    }
    //----------------------------

    final private static String RESTAURANTS_FILE = "restaurants_itr1.csv";
    final private static String REPORTS_FILE = "inspectionreports_itr1.csv";

    private Context context;
    private ArrayList<ReportData> reportData;
    private ArrayList<RestaurantData> restaurantData;


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

    public ArrayList<RestaurantData> getAllRestaurants(){ return restaurantData; }

    public ArrayList<Integer> getReportsIndexes(String trackingNumber){
        ArrayList<Integer> ret = new ArrayList<>();
        for(int i = 0; i < getReportsSize(); i++){
            ReportData report = getReport(i);
            if(report.getTrackingNumber().equals(trackingNumber)){
                ret.add(i);
            }
        }
        return ret;
    }

    public int getRestaurantIndex(String trackingNumber){
        for(int i = 0; i < getRestaurantsSize(); i++){
            RestaurantData restaurant = getRestaurant(i);
            if(restaurant.getTrackingNumber().equals(trackingNumber)){
                return i;
            }
        }
        return -1;
    }

    public int getRestaurantIssuesLength(String trackingNumber){
        int issues = 0;
        ArrayList<Integer> reportsIndexes = getReportsIndexes(trackingNumber);
        for (int index: reportsIndexes) {
            ReportData report = getReport(index);
            issues += report.getNumCritical();
            issues += report.getNumNonCritical();
        }
        return issues;
    }

    public String getLastInspection(String trackingNumber){
        Date newestDate = null;
        ArrayList<Integer> reportsIndexes = getReportsIndexes(trackingNumber);
        for (int index : reportsIndexes) {
            ReportData report = getReport(index);
            Date inspectionDate = report.getInspectionDate();
            if (newestDate == null || inspectionDate.after(newestDate)){
                newestDate = inspectionDate;
            }
        }
        if (newestDate == null){
            return context.getString(R.string.text_inspection_no_date);
        }
        return DataFileProcessor.getFormattedDate(context, newestDate);
    }

    @Override
    public String toString() {
        return "DataManager{" +
                "reportData:" + reportData.size() +
                ", restaurantData:" + restaurantData.size() +
                '}';
    }
}
