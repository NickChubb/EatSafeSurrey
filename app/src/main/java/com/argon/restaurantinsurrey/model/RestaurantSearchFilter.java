package com.argon.restaurantinsurrey.model;

import android.util.Log;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

/*
This class is the model class for filtering restaurants.
Setup the requires
If calling updateFilter()
the lister will be called.
 */

public class RestaurantSearchFilter {

    final public static String TAG = "RestaurantSearchFilter";

    public interface RestaurantSearchListener{
        public void notifyFilterUpdated(ArrayList<RestaurantData> restaurantList);
    }

    private RestaurantSearchListener listener;
    private DataManager manager;

    private String filterName;
    private ReportData.HazardRating selectedHazardRating;
    private int minNumOfCritical;
    private int maxNumOfCritical;
    private boolean filterFavourites;

    private ArrayList<RestaurantData> filterRestaurantsList;
    static public boolean changed;


    public RestaurantSearchFilter() {
        this.manager = DataManager.getInstance();
        selectedHazardRating = ReportData.HazardRating.OTHER;
        minNumOfCritical = -1;
        maxNumOfCritical = -1;
        filterName = "";
        filterFavourites = false;
        changed = false;
        filterRestaurantsList = manager.createRestaurantsList();
    }

    public void updateFilter(){
        if(!changed){
            return;
        }

        ArrayList<RestaurantData> filterRestaurantsList = new ArrayList<>();
        for(int i = 0; i < manager.getRestaurantsSize(); i++){
            RestaurantData restaurant = manager.getRestaurant(i);
            ArrayList<ReportData> reports = manager.getReports(restaurant.getTrackingNumber());

            int critical = getNumberOfCriticalViolationsWithinYear(restaurant);

            boolean targetFavorites = !filterFavourites || manager.checkFavorite(restaurant);
            boolean targetMinCritical = minNumOfCritical == -1 || minNumOfCritical <= critical;
            boolean targetMaxCritical = maxNumOfCritical == -1 || maxNumOfCritical >= critical;

            boolean targetHazard = selectedHazardRating == ReportData.HazardRating.LOW;
            if(reports.size() != 0){
                ReportData recentReport = reports.get(0);
                targetHazard = selectedHazardRating == ReportData.HazardRating.OTHER || recentReport.getHazardRating() == selectedHazardRating;
            }

            if(targetFavorites && targetHazard && targetMinCritical && targetMaxCritical){
                filterRestaurantsList.add(restaurant);
            }
        }
        this.filterRestaurantsList = filterRestaurantsList;
        if(!filterName.isEmpty()){
            updateNameFilter();
            changed = false;
            return;
        }

        changed = false;

        if(listener != null) {
            listener.notifyFilterUpdated(filterRestaurantsList);
        }


    }

    public void updateNameFilter(){
        if(!changed){
            return;
        }

        ArrayList<RestaurantData> filterNameRestaurants = new ArrayList<>();
        for(RestaurantData restaurant: filterRestaurantsList){
            if(filterByName(filterName, restaurant.getName())){
                filterNameRestaurants.add(restaurant);
            }
        }

        listener.notifyFilterUpdated(filterNameRestaurants);
    }

    public void setFilterName(String filterName) {
        if(filterName.equals(this.filterName)){
            return;
        }
        changed = true;
        this.filterName = filterName;
    }

    public void setSelectedHazardRating(ReportData.HazardRating selectedHazardRating) {
        if(this.selectedHazardRating == selectedHazardRating){
            return;
        }
        changed = true;
        this.selectedHazardRating = selectedHazardRating;
    }

    public void setMinNumOfCritical(int minNumOfCritical) {
        if(minNumOfCritical == this.minNumOfCritical){
            return;
        }
        changed = true;
        this.minNumOfCritical = minNumOfCritical;
    }

    public void setMaxNumOfCritical(int maxNumOfCritical) {
        if(maxNumOfCritical == this.maxNumOfCritical){
            return;
        }
        changed = true;
        this.maxNumOfCritical = maxNumOfCritical;
    }

    public void setFilterFavourites(boolean filterFavourites) {
        if(filterFavourites == this.filterFavourites){
            return;
        }
        changed = true;
        this.filterFavourites = filterFavourites;
    }

    public void setListener(RestaurantSearchListener listener) {
        this.listener = listener;
    }

    public String getFilterName() {
        return filterName;
    }

    public boolean getFilterFavourites() {
        return filterFavourites;
    }

    public ReportData.HazardRating getSelectedHazardRating() {
        return selectedHazardRating;
    }

    public int getMinNumOfCritical() {
        return minNumOfCritical;
    }

    public int getMaxNumOfCritical() {
        return maxNumOfCritical;
    }

    private boolean filterByName(String name, String targetName){
        String filterPattern = name.toLowerCase().trim();
        if(filterPattern.isEmpty()){
            return true;
        }
        return targetName.toLowerCase().contains(filterPattern);
    }

    private int getNumberOfCriticalViolationsWithinYear(RestaurantData restaurantData){

        List<ReportData> reports = manager.getReports(restaurantData.getTrackingNumber());

        List<ReportData> reportsWithinYear = new ArrayList<>();

        int criticalViolations = 0;

        for(ReportData reportData : reports){
            Date inspectionDate = reportData.getInspectionDate();

            Date currentDate = new Date();
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(inspectionDate);
            calendar.add(Calendar.YEAR, 1);
            Date validDate = calendar.getTime();
            if(validDate.after(currentDate)){
                criticalViolations += reportData.getNumCritical();
            }
        }
        return criticalViolations;
    }

}
