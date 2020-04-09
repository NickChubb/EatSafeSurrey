package com.argon.restaurantinsurrey.model;

import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/*
 *   Basic class for each inspection report
 *
 */

public class ReportData {

    public enum InspType{FOLLOW_UP, ROUTINE, OTHER}
    public enum HazardRating{OTHER, LOW, MODERATE, HIGH}

    final public static String TAG = "ReportData";

    private String trackingNumber;
    private Date inspectionDate;
    private InspType inspType;
    private int numCritical;
    private int numNonCritical;
    private HazardRating hazardRating;
    private ArrayList<ViolationData> violations;


    public ReportData(String trackingNumber, Date inspectionDate, InspType inspType, int numCritical, int numNonCritical, HazardRating hazardRating, ArrayList<ViolationData> violations) {
        this.trackingNumber = trackingNumber;
        this.inspectionDate = inspectionDate;
        this.inspType = inspType;
        this.numCritical = numCritical;
        this.numNonCritical = numNonCritical;
        this.hazardRating = hazardRating;
        this.violations = violations;
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

    public ArrayList<ViolationData> getViolations() {
        return violations;
    }

    public void setViolations(ArrayList<ViolationData> violations) {
        this.violations = violations;
    }

    public static class ReportsIndexes{
        private int trackingNumber = 0;
        private int date = 1;
        private int inspType = 2;
        private int numCritical = 3;
        private int numNonCritical = 4;
        private int hazardRating = 5;
        private int violation = 6;
    }

    static public ReportData getReport(String line, ArrayList<ViolationData> validViolations, ReportsIndexes indexes){
        String[] splitString = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);
        String[] noQuotesSplitString = DataFactory.removeQuotesMark(splitString);

        if(noQuotesSplitString.length < 6 || noQuotesSplitString.length > 7){
            Log.i(TAG, "getReport: UnavaliableData");
            return null;
        }

        if(noQuotesSplitString.length == 6){
            for(String str: noQuotesSplitString){
                Log.i(TAG, "getReport: " + str);
            }
        }

        try {
            String trackingNumber = noQuotesSplitString[indexes.trackingNumber];
            if(trackingNumber.toUpperCase().equals("TRACKINGNUMBER")){
                return null;
            }

            String dateAsString = noQuotesSplitString[indexes.date];
            Date date = DataFactory.getDate(dateAsString, "yyyyMMdd");
            String inspTypeAsString = noQuotesSplitString[indexes.inspType];

            InspType inspType;
            if(inspTypeAsString.equals("Routine")){
                inspType = InspType.ROUTINE;
            } else if(inspTypeAsString.equals("Follow-Up")){
                inspType = InspType.FOLLOW_UP;
            } else {
                inspType = InspType.OTHER;
            }

            int numCritical = Integer.parseInt(noQuotesSplitString[indexes.numCritical]);
            int numNonCritical = Integer.parseInt(noQuotesSplitString[indexes.numNonCritical]);

            String hazardRatingAsString = noQuotesSplitString[indexes.hazardRating];
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

            String violationsAsString = "";
            if(noQuotesSplitString.length == 7) {
                violationsAsString = noQuotesSplitString[indexes.violation];
            }
            String violationNumberRegex = "(\\d\\d\\d)";
            Pattern pattern = Pattern.compile(violationNumberRegex);
            Matcher matcher = pattern.matcher(violationsAsString);

            ArrayList<ViolationData> violations = new ArrayList<>();
            while (matcher.find()){
                String match = matcher.group();
                int violationNumber = Integer.valueOf(match);
                ViolationData violationData = ViolationData.getViolationByNumber(violationNumber, validViolations);
                if(violationData == null){
                    Log.i(TAG, "getReport: Cannot find violation number: " + violationNumber);
                    continue;
                }
                violations.add(violationData);
            }

            ReportData report = new ReportData(trackingNumber, date, inspType, numCritical, numNonCritical, hazardRating, violations);
            return report;
        } catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, "getReport: " + line + " Cannot convert to ReportData");
            return null;
        }
    }

    static private ReportsIndexes getReportsIndexes(String titles){
        String[] splitTitles = titles.split(",");
        splitTitles = DataFactory.removeQuotesMark(splitTitles);
        for (int i = 0; i < splitTitles.length; i++){
            splitTitles[i] = splitTitles[i].toUpperCase();
        }
        ArrayList<String> titlesList = new ArrayList<>(Arrays.asList(splitTitles));

        ReportsIndexes reportsIndexes = new ReportsIndexes();
        reportsIndexes.trackingNumber = titlesList.indexOf("TRACKINGNUMBER");
        reportsIndexes.date = titlesList.indexOf("INSPECTIONDATE");
        reportsIndexes.inspType = titlesList.indexOf("INSPTYPE");
        reportsIndexes.numCritical = titlesList.indexOf("NUMCRITICAL");
        reportsIndexes.numNonCritical = titlesList.indexOf("NUMNONCRITICAL");
        reportsIndexes.violation = titlesList.indexOf("VIOLLUMP");
        reportsIndexes.hazardRating = titlesList.indexOf("HAZARDRATING");

        return reportsIndexes;
    }

    static public ArrayList<ReportData> getAllReports(ArrayList<String> lines, ArrayList<ViolationData> validViolations){
        ArrayList<ReportData> data = new ArrayList<>();

        ReportsIndexes reportsIndexes = getReportsIndexes(lines.get(0));

        for (String line: lines) {
            ReportData report = getReport(line, validViolations,reportsIndexes);
            if(report != null){
                data.add(report);
            }
        }

        return data;
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
                ", violations=" + violations +
                '}'+"\n";
    }

}
