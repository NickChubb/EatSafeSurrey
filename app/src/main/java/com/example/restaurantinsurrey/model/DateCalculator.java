package com.example.restaurantinsurrey.model;

import java.util.Date;
import java.util.GregorianCalendar;

public class DateCalculator {

    final public static String TAG = "DateCalculator";

    public static long dateCalculator(Date date){
        GregorianCalendar cal1 = new GregorianCalendar();
        GregorianCalendar cal2 = new GregorianCalendar();
        Date curDate = new Date(System.currentTimeMillis());
        cal1.setTime(date);
        cal2.setTime(curDate);
        long dayCount = (cal2.getTimeInMillis()-cal1.getTimeInMillis())/(1000*3600*24);
        if (dayCount <= 30){
            return dayCount;
        }
        else
            return -1;

    }
}
