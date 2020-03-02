package com.example.restaurantinsurrey.model;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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


}
