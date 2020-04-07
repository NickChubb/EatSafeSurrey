package com.argon.restaurantinsurrey.model;

import java.util.ArrayList;
import java.util.List;

public class SearchFilter {

    private DataManager manager;
    private List<RestaurantData> restaurantDataListFull;


    public SearchFilter() {
        this.manager = DataManager.getInstance();
        this.restaurantDataListFull = manager.getAllRestaurants();
    }


    public List<RestaurantData> filterByName(String name, List<RestaurantData> restaurantDataList){
        List<RestaurantData> filteredRestaurantList = new ArrayList<>();
        if(name.length() == 0){
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


    public List<RestaurantData> filterByHazardRating(ReportData.HazardRating hazardRating, List<RestaurantData> restaurantDataList){

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

    public List<RestaurantData> filterAll(String filterName, ReportData.HazardRating hazardRating){

        if( (filterName == null || filterName.length() == 0) && hazardRating == null){
            return restaurantDataListFull;
        }
        else if(filterName != null && hazardRating == null){
            return filterByName(filterName, restaurantDataListFull);
        }
        else if( (filterName == null || filterName.length() == 0) && hazardRating != null){
            return filterByHazardRating(hazardRating, restaurantDataListFull);
        }
        else{
            List<RestaurantData> filterNameList = filterByName(filterName, restaurantDataListFull);
            List<RestaurantData> filterHazardRatingList = filterByHazardRating(hazardRating, filterNameList);
            return filterHazardRatingList;
        }

    }


}
