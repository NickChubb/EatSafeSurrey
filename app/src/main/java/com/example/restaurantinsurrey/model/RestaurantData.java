package com.example.restaurantinsurrey.model;

import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class RestaurantData {

    final public static String TAG = "RestaurantData";

    private String name;
    private String address;
    private String trackingNumber;
    private String city;
    private String type;
    private double lat;
    private double lon;

    public RestaurantData(String name, String address, String trackingNumber, String city, String type, double lat, double lon) {
        this.name = name;
        this.address = address;
        this.trackingNumber = trackingNumber;
        this.city = city;
        this.type = type;
        this.lat = lat;
        this.lon = lon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public static RestaurantData getRestaurant(String line){
        String[] splitString = line.split(",");
        String[] noQuotesSplitString = DataFileProcessor.removeQuotesMark(splitString);

        if(noQuotesSplitString.length != 7){
            Log.i(TAG, "getRestaurant: UnavaliableData");
            return null;
        }

        try {
            String trackingNumber = noQuotesSplitString[0];
            if(trackingNumber.equals("TRACKINGNUMBER")){
                return null;
            }
            String name = noQuotesSplitString[1];
            String address = noQuotesSplitString[2];
            String city = noQuotesSplitString[3];
            String type = noQuotesSplitString[4];
            double lat = Double.valueOf(noQuotesSplitString[5]);
            double lon = Double.valueOf(noQuotesSplitString[6]);
            RestaurantData restaurant = new RestaurantData(name, address, trackingNumber, city, type, lat, lon);
            return restaurant;
        } catch (Exception e){
            Log.e(TAG, "getRestaurant: Cannot convert to restaurant data.");
            return null;
        }
    }

    public static ArrayList<RestaurantData> getAllRestaurants(ArrayList<String> lines){

        ArrayList<RestaurantData> data = new ArrayList<>();

        for (String line: lines) {
            RestaurantData restaurantData = getRestaurant(line);
            if(restaurantData != null){
                data.add(restaurantData);
            }
        }
        return data;
    }

    @Override
    public String toString() {
        return "RestaurantData{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", trackingNumber='" + trackingNumber + '\'' +
                ", city='" + city + '\'' +
                ", type='" + type + '\'' +
                ", lat=" + lat +
                ", lon=" + lon +
                '}';
    }
}
