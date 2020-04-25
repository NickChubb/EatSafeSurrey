package com.argon.eatsafesurrey.model;

import android.util.Log;

import java.util.ArrayList;

/*
 *   Basic class for each violation
 *
 */

public class ViolationData {

    final public static String TAG = "ViolationData";

    private int violationNumber;
    private boolean critical;
    private boolean repeat;
    private String detail;

    public ViolationData(int violationNumber, boolean critical, boolean repeat, String detail) {
        this.violationNumber = violationNumber;
        this.critical = critical;
        this.repeat = repeat;
        this.detail = detail;
    }

    public int getViolationNumber() {
        return violationNumber;
    }

    public void setViolationNumber(int violationNumber) {
        this.violationNumber = violationNumber;
    }

    public boolean isCritical() {
        return critical;
    }

    public void setCritical(boolean critical) {
        this.critical = critical;
    }

    public boolean isRepeat() {
        return repeat;
    }

    public void setRepeat(boolean repeat) {
        this.repeat = repeat;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public static ViolationData getViolation(String line){
        String[] splitString = line.split(",");
        if(splitString.length != 4){
            Log.i(TAG, "getViolation: UnavaliableData: " + line);
            return null;
        }
        try {
            int violationNumber = Integer.parseInt(splitString[0]);
            boolean critical = !splitString[1].equals("Not Critical");
            String detail = splitString[2];
            boolean repeat = !splitString[3].equals("Not Repeat");
            ViolationData violation = new ViolationData(violationNumber, critical, repeat, detail);
            return violation;
        } catch (Exception e){
            Log.e(TAG, "getViolation: Cannot convert to violation data.");
            return null;
        }
    }

    public static ArrayList<ViolationData> getAllViolations(ArrayList<String> lines){
        ArrayList<ViolationData> data = new ArrayList<>();
        for (String line: lines) {
            ViolationData violationData = getViolation(line);
            if(violationData != null){
                data.add(violationData);
            }
        }
        return data;
    }

    public static ViolationData getViolationByNumber(int violationNumber, ArrayList<ViolationData> violationDataList){
        for(ViolationData violationData: violationDataList){
            if(violationData.getViolationNumber() == violationNumber){
                return violationData;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "ViolationData{" +
                "violationNumber=" + violationNumber +
                ", critical=" + critical +
                ", repeat=" + repeat +
                ", detail='" + detail + '\'' +
                '}';
    }
}
