package com.example.restaurantinsurrey.model;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;

import com.example.restaurantinsurrey.R;

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

public class DataFileProcessor {

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
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }
        return null;
    }

    private static final String IMAGE_URL = "http://www.magicspica.com/files/images/";

    public static Bitmap getImage(String trackingNumber){
        String urlString = IMAGE_URL + trackingNumber + ".jpg";
        return getImageFromInternet(urlString);
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
        else {
            int year = calTargetDate.get(Calendar.YEAR);
            int month = calTargetDate.get(Calendar.MONTH) + 1;
            int day = calTargetDate.get(Calendar.DATE);
            return context.getString(R.string.text_inspection_by_date, month, day, year);
        }
    }

}
