package com.argon.restaurantinsurrey.model;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;

import com.argon.restaurantinsurrey.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/*
 *   This is a factory class to process all data from text/internet to an usable format
 *
 */
public class DataFactory {

    final public static String TAG = "DataFileProcessor";

    public static ArrayList<String> readLines(Context context, String filename){
        AssetManager assetManager = context.getAssets();
        ArrayList<String> lines = new ArrayList<>();
        try{
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(assetManager.open(filename)));
            String buffer;
            while ((buffer = bufferedReader.readLine()) != null){
                lines.add(buffer);
            }
            return lines;
        } catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }

    public static String[] removeQuotesMark(String[] strings){
        String[] ret = strings.clone();
        for(int i = 0; i < strings.length; i++){
            ret[i] = strings[i].replace("\"", "");
        }
        return ret;
    }



    public static Date getDate(String dateAsString) {
        DateFormat format = new SimpleDateFormat("yyyyMMdd");
        Date date;
        try {
            date = format.parse(dateAsString);
        } catch (Exception e) {
            Log.i(TAG, "stringToDate: Parsing date failed");
            return null;
        }
        return date;
    }

    public static boolean getDataFromInternet = false;

    private static Bitmap getImageFromInternet(String urlString){
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(2000);
            connection.setReadTimeout(2000);
            connection.connect();
            InputStream inputStream = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            return bitmap;
        } catch (Exception e){
            getDataFromInternet = false;
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }
        return null;
    }

    private static final String IMAGE_URL = "http://www.magicspica.com/files/images/";

    public static Bitmap getImage(String trackingNumber){
        if(getDataFromInternet) {
            String urlString = IMAGE_URL + trackingNumber + ".jpg";
            return getImageFromInternet(urlString);
        } else {
            return null;
        }
    }


    public static Bitmap zoomBitmap(Bitmap bitmap, int w, int h){
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) w / width);
        float scaleHeight = ((float) h / height);
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap ret = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        return ret;
    }

    public static String getFormattedDate(Context context, Date date){
        GregorianCalendar calTargetDate = new GregorianCalendar();
        GregorianCalendar calCurrentDate = new GregorianCalendar();
        Date curDate = new Date(System.currentTimeMillis());
        calTargetDate.setTime(date);
        calCurrentDate.setTime(curDate);
        long dayCount = (calCurrentDate.getTimeInMillis()-calTargetDate.getTimeInMillis())/(1000*3600*24);
        if (dayCount <= 30){
            return context.getString(R.string.text_inspection, dayCount);
        }
        else if(dayCount >30 && dayCount <= 365){
//          int month = calTargetDate.get(Calendar.MONTH) + 1;
            String month =calTargetDate.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault());
            int day = calTargetDate.get(Calendar.DATE);
            return context.getString(R.string.text_inspection_by_date_less_one_year, month, day);
        }
        else {
            int year = calTargetDate.get(Calendar.YEAR);
            String month =calTargetDate.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault());
//            int month = calTargetDate.get(Calendar.MONTH) + 1;
//            int day = calTargetDate.get(Calendar.DATE);
            return context.getString(R.string.text_inspection_by_date, month, year);
        }
    }

    public static int getHazardRatingImage(ReportData.HazardRating hazardRating){
        switch (hazardRating){
            case LOW:
                return R.drawable.green_warning_sign;
            case MODERATE:
                return R.drawable.yellow_warning_sign;
            case HIGH:
                return R.drawable.red_warning_sign;
            default:
                return R.drawable.green_warning_sign;
        }
    }

    public static int getHazardRatingBackgroundColor(ReportData.HazardRating hazardRating){
        switch (hazardRating){
            case LOW:
                return R.color.hazardBackgroundLow;
            case MODERATE:
                return R.color.hazardBackgroundModerate;
            case HIGH:
                return R.color.hazardBackgroundHigh;
            default:
                return R.color.hazardBackgroundLow;
        }
    }

    public static int getHazardTextColor(ReportData.HazardRating hazardRating){
        switch (hazardRating){
            case LOW:
                return R.color.colorGreen;
            case MODERATE:
                return R.color.colorYellow;
            case HIGH:
                return R.color.colorRed;
            default:
                return R.color.colorGrey;
        }
    }
}
