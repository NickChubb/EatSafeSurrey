package com.argon.restaurantinsurrey.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class UpdateManager extends AsyncTask<Short, Integer, Boolean> {

    public interface UpdateStatusListener{
        public void onPrepareUpdate(int maxProgress);
        public void onStatusUpdated(UpdateTypes type, int progress, int maxProgress);
        public void onUpdateFinished();
        public void onUpdateCancelled();
    }

    final public static String TAG = "UpdateManager";

    //Making it a singleton.
    static public UpdateManager getInstance(Context context){
        return new UpdateManager(context);
    }

    final private String RESTAURANTS_URL = "http://data.surrey.ca/api/3/action/package_show?id=restaurants";
    final private String REPORTS_URL = "http://data.surrey.ca/api/3/action/package_show?id=fraser-health-restaurant-inspection-reports";
    final private String IMAGE_LIST_URL = "http://www.magicspica.com/files/dir.csv";
    final private String LAST_UPDATE_DATA_PREFERENCE = "UpdateManager_LastUpdateData";
    final private String LAST_UPDATE_RESTAURANTS_PREFERENCE = "UpdateManager_LastUpdateRestaurants";
    final private String LAST_UPDATE_REPORTS_PREFERENCE = "UpdateManager_LastUpdateReports";
    final private String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS";
    final private String RESTAURANTS_FILE_NAME = "restaurants.csv";
    final private String REPORTS_FILE_NAME = "inspection_reports.csv";

    private Context context;
    private UpdateStatusListener updateStatusListener;
    private SharedPreferences preferences;
    private Date presentDate;
    private JSONObject restaurantsData;
    private JSONObject reportsData;
    private File restaurantsFile;
    private File reportsFile;
    private File imagesDir;
    private ConnectivityManager connectivityManager;
    private int maxProgress;
    private UpdateTypes presentUpdateStatu;

    private UpdateManager(Context context){
        this.context = context;

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
        imagesDir = context.getFilesDir();

        maxProgress = 0;
    }

    @Override
    protected Boolean doInBackground(Short... shorts) {
        short updates = shorts[0];
        maxProgress = getNumOfUpdates(updates);
        updateStatusListener.onPrepareUpdate(maxProgress);
        updateData(updates);
        return true;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        updateStatusListener.onStatusUpdated(presentUpdateStatu, values[0], values[1]);
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        if(aBoolean){
            updateStatusListener.onUpdateFinished();
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        updateStatusListener.onUpdateCancelled();
    }

    public void setUpdateStatusListener(UpdateStatusListener updateStatusListener) {
        this.updateStatusListener = updateStatusListener;
    }


    public interface AvailableUpdates {
        short NO_UPDATE = 0;
        short RESTAURANTS = 1 << 0;
        short REPORTS = 1 << 1;
        short IMAGES = 1 << 2;
    }

    public enum UpdateTypes{
        RESTAURANTS, REPORTS, IMAGES;
    }

    private int getNumOfUpdates(short updates){
        int i = 0;
        if ((updates & AvailableUpdates.REPORTS) > 0){
            i++;
        }
        if ((updates & AvailableUpdates.RESTAURANTS) > 0){
            i++;
        }
        if ((updates & AvailableUpdates.IMAGES) > 0){
            i++;
        }
        return i;
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
        if (isUpdateAvailable(UpdateTypes.IMAGES)){
            ret |= AvailableUpdates.IMAGES;
        }
        return ret;
    }

    public void updateData(short updates){
        int presentProgress = 0;
        if (!hasNetwork()){
            return;
        }
        if (updates == AvailableUpdates.NO_UPDATE){
            return;
        }
        SharedPreferences.Editor editor = preferences.edit();
        if ((updates & AvailableUpdates.REPORTS) > 0){
            presentUpdateStatu = UpdateTypes.REPORTS;
            publishProgress(presentProgress++, maxProgress);
            writeLatestData(UpdateTypes.REPORTS);
            String latestUpdateTime = getLatestUpdateTime(UpdateTypes.REPORTS);
            editor.putString(LAST_UPDATE_REPORTS_PREFERENCE, latestUpdateTime);
        }

        if ((updates & AvailableUpdates.RESTAURANTS) > 0){
            presentUpdateStatu = UpdateTypes.RESTAURANTS;
            publishProgress(presentProgress++, maxProgress);
            writeLatestData(UpdateTypes.RESTAURANTS);
            String latestUpdateTime = getLatestUpdateTime(UpdateTypes.RESTAURANTS);
            editor.putString(LAST_UPDATE_RESTAURANTS_PREFERENCE, latestUpdateTime);

        }
        if ((updates &AvailableUpdates.IMAGES) > 0){
            presentUpdateStatu = UpdateTypes.IMAGES;
            downloadImages();
        }

        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        String presentDateStr = format.format(presentDate);
        editor.putString(LAST_UPDATE_DATA_PREFERENCE, presentDateStr);
        editor.apply();
    }

    private void downloadImages() {
        String string = DataFactory.getStringFromInternet(IMAGE_LIST_URL);
        //TODO: CSV MANAGER
        String[] lines = string.split("\n");
        ArrayList<String> imagePaths = new ArrayList<>();
        for(String line: lines){
            String path = line.split(",")[1];
            File file = new File(imagesDir, path);
            if(!file.exists()){
                imagePaths.add(path);
            }
        }
        int presentProgress = 0;
        publishProgress(presentProgress, imagePaths.size());
        for (String imagePath: imagePaths){
            DataFactory.downloadImageFromServer(context, imagePath);
            publishProgress(++presentProgress, imagePaths.size());
        }
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
                case IMAGES:
                    return;
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
            case IMAGES:
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
            case IMAGES:
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
