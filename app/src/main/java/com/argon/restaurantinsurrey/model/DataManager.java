package com.argon.restaurantinsurrey.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.argon.restaurantinsurrey.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

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

    final private static String IMAGES_PATHS_FILE = "dir.csv";
    final private static String RESTAURANTS_FILE = "restaurants.csv";
    final private static String INSPECTIONS_REPORTS_FILE = "inspection_reports.csv";
    final private static String FAVORITE_LIST_FILE = "favorites.txt";

    private ArrayList<ReportData> reportData;
    private ArrayList<RestaurantData> restaurantData;
    private ArrayList<ViolationData> validViolations;
    private ArrayList<RestaurantData> favoriteList;
    private ArrayList<RestaurantData> violationUpdatedFavorites;
    private HashMap<String, String> imagePathMap;
    private Context context;

    private DataManager(Context context) {
        this.context = context;

        readRestaurantFile();
        readReportAndViolationFile();
        readImagePathsFile();
        readFavoriteListFile();
    }

    private void readFavoriteListFile() {
        File favoriteListFile = new File(context.getFilesDir(), FAVORITE_LIST_FILE);
        ArrayList<String> favoriteListString = null;
        if(favoriteListFile.exists()){
            favoriteListString = DataFactory.readLinesFromFile(favoriteListFile);
        } else {
            favoriteListString = new ArrayList<>();
        }
        favoriteList = new ArrayList<>();
        violationUpdatedFavorites = new ArrayList<>();
        for (String line: favoriteListString) {
            String[] split = line.split(",");
            String trackingNumber = split[0];
            Date savedViolateTime = DataFactory.getDate(split[1], context.getString(R.string.basic_date_format));
            RestaurantData restaurant = getRestaurant(trackingNumber);
            favoriteList.add(restaurant);
            ArrayList<ReportData> reports = getReports(trackingNumber);
            if(reports.size() != 0){
                if (reports.get(0).getInspectionDate().after(savedViolateTime)){
                    violationUpdatedFavorites.add(restaurant);
                }
            }
        }
    }

    private void saveFavoriteListFile(){
        File file = new File(context.getFilesDir(), FAVORITE_LIST_FILE);
        StringBuilder stringBuilder = new StringBuilder();
        for(RestaurantData restaurantData: favoriteList){
            String trackingNumber = restaurantData.getTrackingNumber();
            String latestInspectionDate = "01/01/1970";
            ArrayList<ReportData> reports = getReports(trackingNumber);
            if(reports.size() != 0){
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(context.getString(R.string.basic_date_format), Locale.US);
                latestInspectionDate = simpleDateFormat.format(reports.get(0).getInspectionDate());
            }
            stringBuilder.append(trackingNumber).append(",").append(latestInspectionDate).append("\n");
        }
        DataFactory.writeToFile(stringBuilder.toString(), file);
    }

    private void readRestaurantFile(){
        File restaurantsFile = new File(context.getFilesDir(), RESTAURANTS_FILE);
        ArrayList<String> restaurantStrings = null;
        if(restaurantsFile.exists()){
            restaurantStrings = DataFactory.readLinesFromFile(restaurantsFile);
        }else {
            restaurantStrings = DataFactory.readLinesFromAssets(context, DEFAULT_RESTAURANTS_FILE);
        }

        this.restaurantData = RestaurantData.getAllRestaurants(context, restaurantStrings);
        this.restaurantData.sort((o1, o2) -> o1.getName().compareTo(o2.getName()));
    }

    private void readReportAndViolationFile(){
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

    private void readImagePathsFile() {
        File imagePathsFile = new File(context.getFilesDir(), IMAGES_PATHS_FILE);
        imagePathMap = new HashMap<>();
        if (!imagePathsFile.exists()){
            return;
        }
        ArrayList<String> lines = DataFactory.readLinesFromFile(imagePathsFile);
        for(String line: lines){
            String[] split = line.split(",");
            imagePathMap.put(split[0], split[1]);
        }
    }


    public void saveData(){
        saveFavoriteListFile();
    }

    public Bitmap getImageByTrackingNumber(String trackingNumber){
        String imagePath = imagePathMap.getOrDefault(trackingNumber, "Default");
        return DataFactory.getImage(context, imagePath);
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

    public ArrayList<RestaurantData> createRestaurantsList(){ return new ArrayList<>(restaurantData); }

    public ArrayList<RestaurantData> createFavoriteList(){
        return new ArrayList<>(favoriteList);
    }

    public ArrayList<RestaurantData> createViolationUpdatedFavoriteList(){
        return new ArrayList<>(violationUpdatedFavorites);
    }

    public void addFavorite(RestaurantData restaurant){
        if(!favoriteList.contains(restaurant)) {
            favoriteList.add(restaurant);
        }
    }

    public void removeFavorite(RestaurantData restaurant){
        if(favoriteList.contains(restaurant)) {
            favoriteList.remove(restaurant);
        }
    }

    public boolean checkFavorite(RestaurantData restaurant){
        return favoriteList.contains(restaurant);
    }

    public int getFavoriteListSize(){
        return favoriteList.size();
    }

    public RestaurantData getFavoriteRestaurant(int index){
        return favoriteList.get(index);
    }

    public void swapFavoriteListElements(int firstIndex, int secondIndex){
        Collections.swap(favoriteList, firstIndex, secondIndex);
    }

    public boolean hasNewUpdatedFavorites(){
        return violationUpdatedFavorites.size() > 0;
    }

    public int getIndexForRestaurant(RestaurantData restaurant){
        return restaurantData.indexOf(restaurant);
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
