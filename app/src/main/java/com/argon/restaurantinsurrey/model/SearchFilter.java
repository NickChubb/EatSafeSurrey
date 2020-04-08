package com.argon.restaurantinsurrey.model;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SearchFilter {

    final public static String TAG = "SearchFilter";


    private DataManager manager;
    private List<RestaurantData> restaurantDataListFull;


    public SearchFilter() {
        this.manager = DataManager.getInstance();
        this.restaurantDataListFull = manager.getAllRestaurants();
    }


    public List<RestaurantData> filterByName(String name, List<RestaurantData> restaurantDataList){
        List<RestaurantData> filteredRestaurantList = new ArrayList<>();
        if(isStringEmpty(name)){
            return restaurantDataList;
        }
        else{
            String filterPattern = name.toLowerCase().trim();
            for(RestaurantData restaurant : restaurantDataList){
                if(restaurant.getName().toLowerCase().contains(filterPattern)){
                    filteredRestaurantList.add(restaurant);
                }
            }
        }

        return filteredRestaurantList;
    }


    public List<RestaurantData> filterByHazardRating(ReportData.HazardRating hazardRating,
                                                     List<RestaurantData> restaurantDataList){

        ArrayList<RestaurantData> filteredRestaurantList = new ArrayList<>();

        if(hazardRating == null){
            return restaurantDataList;
        }
        else {

            for (RestaurantData restaurant : restaurantDataList) {
                String trackingNumber = restaurant.getTrackingNumber();

                List<ReportData> reportData = manager.getReports(trackingNumber);

                if (reportData.isEmpty()) {
                    if (hazardRating.equals(ReportData.HazardRating.LOW)) {
                        filteredRestaurantList.add(restaurant);
                    }
                } else if (reportData.get(0).getHazardRating().equals(hazardRating)) {
                    filteredRestaurantList.add(restaurant);
                }

            }

        }
        return filteredRestaurantList;

    }

    public List<RestaurantData> filterByMinViolation(String numberOfViolation,
                                                     List<RestaurantData> restaurantDataList){

        int minViolations;

        if(isStringEmpty(numberOfViolation)){
            Log.d(TAG, "numberOfViolation is null");
            return restaurantDataList;
        }
        else{
            minViolations = Integer.parseInt(numberOfViolation);
        }

        ArrayList<RestaurantData> filteredRestaurantList = new ArrayList<>();

        for(RestaurantData restaurantData : restaurantDataList){

            int criticalViolationWithinYear = numberOfCriticalViolationsWithinYear(restaurantData);

            if(criticalViolationWithinYear >= minViolations){

                filteredRestaurantList.add(restaurantData);
            }

        }
        return filteredRestaurantList;
    }

    public List<RestaurantData> filterByMaxViolation(String numberOfViolation,
                                                     List<RestaurantData> restaurantDataList){

        int maxViolations;

        if(isStringEmpty(numberOfViolation)){
            Log.d(TAG, "numberOfViolation is null");
            return restaurantDataList;
        }
        else{
            maxViolations = Integer.parseInt(numberOfViolation);
        }

        ArrayList<RestaurantData> filteredRestaurantList = new ArrayList<>();

        for(RestaurantData restaurantData : restaurantDataList){

            int criticalViolationWithinYear = numberOfCriticalViolationsWithinYear(restaurantData);

            if(criticalViolationWithinYear <= maxViolations){

                filteredRestaurantList.add(restaurantData);
            }

        }
        return filteredRestaurantList;
    }

    public List<RestaurantData> filterAll(String filterName, ReportData.HazardRating hazardRating,
                                          String minNumberOfViolation, String maxNumberOfViolation){


        if( isStringEmpty(filterName) == true &&
                hazardRating == null &&
                isStringEmpty(minNumberOfViolation) == true &&
                isStringEmpty(maxNumberOfViolation) == true){

            return restaurantDataListFull;
        }
        else if( isStringEmpty(filterName) == false &&
                hazardRating == null &&
                isStringEmpty(minNumberOfViolation) == true &&
                isStringEmpty(maxNumberOfViolation) == true){

            return filterByName(filterName, restaurantDataListFull);
        }
        else if(isStringEmpty(filterName) == true &&
                hazardRating != null &&
                isStringEmpty(minNumberOfViolation) == true &&
                isStringEmpty(maxNumberOfViolation) == true){
            return filterByHazardRating(hazardRating, restaurantDataListFull);
        }
        else if(isStringEmpty(filterName) == true &&
                hazardRating == null &&
                isStringEmpty(minNumberOfViolation) == false &&
                isStringEmpty(maxNumberOfViolation) == true){

            return filterByMinViolation(minNumberOfViolation, restaurantDataListFull);
        }
        else if(isStringEmpty(filterName) == true &&
                hazardRating == null &&
                isStringEmpty(minNumberOfViolation) == true &&
                isStringEmpty(maxNumberOfViolation) == false){

            return filterByMaxViolation(minNumberOfViolation, restaurantDataListFull);
        }
        else{
            List<RestaurantData> filterNameList = filterByName(filterName, restaurantDataListFull);
            List<RestaurantData> filterHazardRatingList = filterByHazardRating(hazardRating, filterNameList);
            List<RestaurantData> filterMinViolationList = filterByMinViolation(minNumberOfViolation, filterHazardRatingList);
            List<RestaurantData> filterMaxViolationList = filterByMaxViolation(maxNumberOfViolation, filterMinViolationList);

            return filterMaxViolationList;
        }

    }


    public int numberOfCriticalViolationsWithinYear(RestaurantData restaurantData){

        List<ReportData> reports = manager.getReports(restaurantData.getTrackingNumber());

        List<ReportData> reportsWithinYear = new ArrayList<>();

        int criticalViolations = 0;

        for(ReportData reportData : reports){
            Date inspectionDate = reportData.getInspectionDate();

            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

            Date currentDate = new Date();

            long diff = currentDate.getTime() - inspectionDate.getTime();
            long daysApart = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);

            if(daysApart <= 365){
                criticalViolations = criticalViolations + reportData.getNumCritical();
            }

        }

        return criticalViolations;

    }

    private boolean isStringEmpty(String string){
        return (string == null || string.length() == 0);
    }


}
