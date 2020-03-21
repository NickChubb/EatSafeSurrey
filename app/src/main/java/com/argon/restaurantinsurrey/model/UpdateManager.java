package com.argon.restaurantinsurrey.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.util.Log;

import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class UpdateManager {

    final public static String TAG = "UpdateManager";

    //Making it a singleton.
    static private UpdateManager instance = null;
    static public void createInstance(Context context){
        if(instance == null) {
            instance = new UpdateManager(context);
        }
    }

    static public UpdateManager getInstance(){
        if(instance == null){
            Log.e(TAG, "getInstance: Should call createInstance() at least once." );
            return null;
        }
        return instance;
    }

    final private String RESTAURANTS_URL = "http://data.surrey.ca/api/3/action/package_show?id=restaurants";
    final private String REPORTS_URL = " http://data.surrey.ca/api/3/action/package_show?id=fraser-health-restaurant-inspection-reports";
    final private String LAST_UPDATE_DATA_PREFERENCE = "UpdateManager_LastUpdateData";
    final private String LAST_UPDATE_RESTAURANTS_PREFERENCE = "UpdateManager_LastUpdateRestaurants";
    final private String LAST_UPDATE_REPORTS_PREFERENCE = "UpdateManager_LastUpdateReports";
    final private String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS";
    final private String RESTAURANTS_FILE_NAME = "restaurants.csv";
    final private String REPORTS_FILE_NAME = "inspection_reports.csv";

    private SharedPreferences preferences;
    private Date presentDate;
    private JSONObject restaurantsData;
    private JSONObject reportsData;
    private File restaurantsFile;
    private File reportsFile;
    private ConnectivityManager connectivityManager;

    private UpdateManager(Context context){
        preferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        presentDate = new Date();

        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if(!hasNetwork()){
            return;
        }

        String restaurantDataStr = DataFactory.getStringFromInternet(RESTAURANTS_URL);
        String reportsDataStr = DataFactory.getStringFromInternet(REPORTS_URL);
        try {
            restaurantsData = new JSONObject(restaurantDataStr);
            restaurantsData = restaurantsData.getJSONObject("result").getJSONArray("resources").getJSONObject(0);
            reportsData = new JSONObject(reportsDataStr);
            reportsData = reportsData.getJSONObject("result").getJSONArray("resources").getJSONObject(0);
        } catch (Exception e){
            e.printStackTrace();
        }
        restaurantsFile = new File(context.getFilesDir(), RESTAURANTS_FILE_NAME);
        reportsFile = new File(context.getFilesDir(), REPORTS_FILE_NAME);
    }
    
    public interface AvailableUpdates {
        short NO_UPDATE = 0;
        short RESTAURANTS = 1 << 0;
        short REPORTS = 1 << 1;
    }

    private enum UpdateTypes{
        RESTAURANTS, REPORTS
    }

    public short getAvailableUpdates(){
        if(!hasNetwork()){
            return AvailableUpdates.NO_UPDATE;
        }

        String lastUpdateStr = preferences.getString(LAST_UPDATE_DATA_PREFERENCE, "2015-08-14T10:50:38.365988");
        Date lastUpdateDate = DataFactory.getDate(lastUpdateStr, DATE_FORMAT);
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(lastUpdateDate);
        calendar.add(Calendar.HOUR, 20);
        Date validDate = calendar.getTime();

        if(validDate.after(presentDate)){
            return AvailableUpdates.NO_UPDATE;
        }

        short ret = AvailableUpdates.NO_UPDATE;
        if (isUpdateAvailable(UpdateTypes.RESTAURANTS)){
            ret |= AvailableUpdates.RESTAURANTS;
        }
        if (isUpdateAvailable(UpdateTypes.REPORTS)){
            ret |= AvailableUpdates.REPORTS;
        }
        return ret;
    }

    public void updateData(short updates){
        if (!hasNetwork()){
            return;
        }
        if (updates == AvailableUpdates.NO_UPDATE){
            return;
        }
        SharedPreferences.Editor editor = preferences.edit();
        if ((updates & AvailableUpdates.REPORTS) > 0){
            writeLatestData(UpdateTypes.REPORTS);
            String latestUpdateTime = getLatestUpdateTime(UpdateTypes.REPORTS);
            editor.putString(LAST_UPDATE_REPORTS_PREFERENCE, latestUpdateTime);
        }
        if ((updates & AvailableUpdates.RESTAURANTS) > 0){
            writeLatestData(UpdateTypes.RESTAURANTS);
            String latestUpdateTime = getLatestUpdateTime(UpdateTypes.RESTAURANTS);
            editor.putString(LAST_UPDATE_RESTAURANTS_PREFERENCE, latestUpdateTime);
        }

        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        String presentDateStr = format.format(presentDate);
        editor.putString(LAST_UPDATE_DATA_PREFERENCE, presentDateStr);
    }

    public boolean hasNetwork(){
        Network activeNetwork = connectivityManager.getActiveNetwork();
        if(activeNetwork == null){
            return false;
        }
        NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork);
        return networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI);
    }

    private void writeLatestData(UpdateTypes updateTypes){
        String dataUrl = null;
        File file = null;
        try {
            switch (updateTypes){
                case RESTAURANTS:
                    dataUrl = restaurantsData.getString("url");
                    file = restaurantsFile;
                    break;
                case REPORTS:
                    dataUrl = reportsData.getString("url");
                    file = reportsFile;
                    break;
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        String dataStr = DataFactory.getStringFromInternet(dataUrl);
        DataFactory.writeToFile(dataStr, file);
    }

    private String getLatestUpdateTime(UpdateTypes updateTypes){
        JSONObject data = null;
        switch (updateTypes){
            case REPORTS:
                data = reportsData;
                break;
            case RESTAURANTS:
                data = restaurantsData;
                break;
        }
        String newestDataStr = null;
        try {
            newestDataStr = data.getString("last_modified");
        } catch (Exception e){
            e.printStackTrace();
        }
        return newestDataStr;
    }


    private boolean isUpdateAvailable(UpdateTypes updateTypes){
        String lastUpdateStr = null;
        JSONObject data = null;
        switch (updateTypes){
            case REPORTS:
                lastUpdateStr = preferences.getString(LAST_UPDATE_REPORTS_PREFERENCE, "2015-08-14T10:50:38.365988");
                data = reportsData;
                break;
            case RESTAURANTS:
                lastUpdateStr = preferences.getString(LAST_UPDATE_RESTAURANTS_PREFERENCE, "2015-08-14T10:50:38.365988");
                data = restaurantsData;
                break;
        }

        Date lastUpdateDate = DataFactory.getDate(lastUpdateStr, DATE_FORMAT);

        String newestDataStr = getLatestUpdateTime(updateTypes);
        Date newestDataDate = DataFactory.getDate(newestDataStr, DATE_FORMAT);
        Log.i(TAG, "isUpdateAvailable: " + lastUpdateDate);
        Log.i(TAG, "isUpdateAvailable: " + newestDataDate);
        return newestDataDate.after(lastUpdateDate);
    }
}
