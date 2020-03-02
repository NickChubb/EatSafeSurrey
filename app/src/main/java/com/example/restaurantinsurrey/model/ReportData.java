package com.example.restaurantinsurrey.model;

import android.util.Log;

import java.util.Date;

//TODO: Not Finished Yet
public class ReportData {

    public enum InspType{FOLLOW_UP, ROUTINE, OTHER};
    public enum HazardRating{HIGH, MODERATE, LOW, OTHER};

    final public static String TAG = "ReportData";

    private String trackingNumber;
    private Date inspectionDate;
    private InspType inspType;
    private int numCritical;
    private int numNonCritical;
    private HazardRating hazardRating;
    private ViolationData violation;

    public ReportData(String trackingNumber, Date inspectionDate, InspType inspType, int numCritical, int numNonCritical, HazardRating hazardRating, ViolationData violation) {
        this.trackingNumber = trackingNumber;
        this.inspectionDate = inspectionDate;
        this.inspType = inspType;
        this.numCritical = numCritical;
        this.numNonCritical = numNonCritical;
        this.hazardRating = hazardRating;
        this.violation = violation;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public Date getInspectionDate() {
        return inspectionDate;
    }

    public void setInspectionDate(Date inspectionDate) {
        this.inspectionDate = inspectionDate;
    }

    public InspType getInspType() {
        return inspType;
    }

    public void setInspType(InspType inspType) {
        this.inspType = inspType;
    }

    public int getNumCritical() {
        return numCritical;
    }

    public void setNumCritical(int numCritical) {
        this.numCritical = numCritical;
    }

    public int getNumNonCritical() {
        return numNonCritical;
    }

    public void setNumNonCritical(int numNonCritical) {
        this.numNonCritical = numNonCritical;
    }

    public HazardRating getHazardRating() {
        return hazardRating;
    }

    public void setHazardRating(HazardRating hazardRating) {
        this.hazardRating = hazardRating;
    }

    public ViolationData getViolation() {
        return violation;
    }

    public void setViolation(ViolationData violation) {
        this.violation = violation;
    }

    static public ReportData getReport(String line){
        String[] splitString = line.split(",");

        String[] noQuotesSplitString = DataFileProcessor.removeQuotesMark(splitString);

        if(noQuotesSplitString.length < 7){
            Log.i(TAG, "getReport: UnavaliableData");
            return null;
        }

        try {

            String trackingNumber = noQuotesSplitString[0];
            if(trackingNumber.equals("TrackingNumber")){
                return null;
            }

            String dateAsString = noQuotesSplitString[1];
            Date date = DataFileProcessor.getDate(dateAsString);
            String inspTypeAsString = noQuotesSplitString[2];

            InspType inspType;
            if(inspTypeAsString.equals("Routine")){
                inspType = InspType.ROUTINE;
            } else if(inspTypeAsString.equals("Follow-Up")){
                inspType = InspType.FOLLOW_UP;
            } else {
                inspType = InspType.OTHER;
            }

            int numCritical = Integer.parseInt(noQuotesSplitString[3]);
            int numNonCritical = Integer.parseInt(noQuotesSplitString[4]);

            String hazardRatingAsString = noQuotesSplitString[5];
            HazardRating hazardRating;
            if(hazardRatingAsString.equals("Low")){
                hazardRating = HazardRating.LOW;
            } else if(hazardRatingAsString.equals("Moderate")){
                hazardRating = HazardRating.MODERATE;
            } else if(hazardRatingAsString.equals("High")){
                hazardRating = HazardRating.HIGH;
            } else {
                hazardRating = HazardRating.OTHER;
            }

            //Combine the violation string together
            StringBuilder stringBuilder = new StringBuilder();
            for(int i = 6; i < noQuotesSplitString.length; i++){
                stringBuilder.append(noQuotesSplitString[i]);
                if(i != noQuotesSplitString.length - 1){
                    stringBuilder.append(",");
                }
            }

            String violationAsString = stringBuilder.toString();

            ViolationData violationData;
            if(violationAsString.length() >= 0){
                violationData = ViolationData.getViolation(violationAsString);
            } else {
                violationData = null;
            }

            ReportData report = new ReportData(trackingNumber, date, inspType, numCritical, numNonCritical, hazardRating, violationData);
            return report;
        } catch (Exception e){
            Log.i(TAG, "getReport: Cannot convert to ReportData");
            return null;
        }
    }

    @Override
    public String toString() {
        return "ReportData{" +
                "trackingNumber='" + trackingNumber + '\'' +
                ", inspectionDate=" + inspectionDate +
                ", inspType=" + inspType +
                ", numCritical=" + numCritical +
                ", numNonCritical=" + numNonCritical +
                ", hazardRating=" + hazardRating +
                ", violation=" + violation +
                '}';
    }
}
