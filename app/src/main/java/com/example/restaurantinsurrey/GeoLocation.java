package com.example.restaurantinsurrey;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.util.List;
import java.util.Locale;



public class GeoLocation{

    public static void getAddress(final String locationAddress, final Context context, final Handler handler){

        Thread thread = new Thread(){
            @Override
            public void run(){
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                String result = null;
                try {
                    List addressList = geocoder.getFromLocationName(locationAddress,1);

                    if(addressList != null && addressList.size() > 0){
                        Address address = (Address) addressList.get(0);
                        StringBuilder stringBuilder = new StringBuilder();
                        System.out.println(address.getLatitude());
                        stringBuilder.append(address.getLatitude()).append("\n");
                        System.out.println(address.getLongitude());
                        stringBuilder.append(address.getLongitude()).append("\n");
                        result = stringBuilder.toString();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    Message message = Message.obtain();
                    message.setTarget(handler);

                    if( result != null){
                        message.what =1;
                        Bundle bundle = new Bundle();
                        result = "Address   :" + locationAddress +
                                "\n\n\nLatitude And Longtitude\n" + result;
                        bundle.putString("addess", result);
                        message.setData(bundle);
                    }
                    else{
                        message.what = 1;
                        Bundle bundle = new Bundle();
                        result = "Address   :" + locationAddress +
                                "\nUnable to get Coordinates for this address\n";
                        bundle.putString("address", result);
                        message.setData(bundle);
                    }
                    message.sendToTarget();
                }

            }
        };
        thread.start();

    }




}
