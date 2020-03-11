package com.example.restaurantinsurrey;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.restaurantinsurrey.model.DataManager;
import com.example.restaurantinsurrey.model.ReportData;
import com.example.restaurantinsurrey.model.RestaurantData;
import com.example.restaurantinsurrey.model.ViolationData;
import com.example.restaurantinsurrey.ui.ViolationRecyclerViewAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*
 *   Displays all information about a single inspection
 *
 */
public class InspectionDetailsActivity extends AppCompatActivity {

    private static final String INSPECTION_DATE = "Inspection date: ";
    private static final String INSPECTION_TYPE = "Inspection type: ";
    private static final String CRITICAL_ISSUES = "critical issues";
    private static final String NON_CRITICAL_ISSUES = "non critical issues";
    private static final String CRITICAL_ISSUE = "critical issue";
    private static final String NON_CRITICAL_ISSUE = "non critical issue";
    private static final String HAZARD_LEVEL = "Hazard Level: ";

    private static final String HIGH = "HIGH";
    private static final String MODERATE = "MODERATE";
    private static final String LOW = "LOW";

    private static final String TRACKING_NUMBER = "tracking_number";
    private static final String SELECTED_REPORT = "selected_report";
    private static final String FOLLOW_UP = "Follow-Up";
    private static final String ROUTINE = "Routine";
    private static final String OTHER = "Other";
    public static final String NO_VIOLATIONS_AVAILABLE = "No violations available";
    private DataManager manager;
    private ArrayList<ReportData> allReports;
    private ArrayList<ReportData> restaurantReports;
    private ArrayList<ViolationData> violationData;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspection_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        manager = DataManager.getInstance();
        allReports = manager.getAllResports();
        restaurantReports = getReports(allReports);
        violationData = getViolations(restaurantReports);

        setUpReportDetails(restaurantReports);

        setUpRecyclerView(violationData);
    }

    private void setUpReportDetails(ArrayList<ReportData> restaurantReports) {
        Intent intent = getIntent();
        int index = intent.getIntExtra(SELECTED_REPORT, 0);
        ReportData report = restaurantReports.get(index);

        TextView dateTV = (TextView) findViewById(R.id.inspectionDetailsDateTV);
        TextView inspectionTypeTV = (TextView) findViewById(R.id.inspectionDetailsTypeTV);
        TextView criticalIssuesTV = (TextView) findViewById(R.id.inspectionDetailsCriticalIssuesTV);
        TextView nonCriticalIssuesTV = (TextView) findViewById(R.id.inspectionDetailsNonCriticalIssuesTV);
        TextView hazardLevelTV = (TextView) findViewById(R.id.inspectionDetailsHazardLevelTV);

        ImageView leftWarningSign = (ImageView) findViewById(R.id.inspectionDetialsHazardSignLeftImageView);
        ImageView rightWarningSign = (ImageView) findViewById(R.id.inspectionDetialsHazardSignRightImageView);

        Date inspectionDate = report.getInspectionDate();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String date = (String)dateFormat.format(inspectionDate);
        dateTV.setText(INSPECTION_DATE + " " + date);

        ReportData.InspType inspectionType = report.getInspType();
        if(inspectionType == ReportData.InspType.FOLLOW_UP){
            inspectionTypeTV.setText(INSPECTION_TYPE + " " + FOLLOW_UP);
        }
        if(inspectionType == ReportData.InspType.ROUTINE){
            inspectionTypeTV.setText(INSPECTION_TYPE + " " + ROUTINE);
        }
        if(inspectionType == ReportData.InspType.OTHER){
            inspectionTypeTV.setText(INSPECTION_TYPE + " " + OTHER);
        }

        int numCritical = report.getNumCritical();
        int numNonCritical = report.getNumNonCritical();

        if(numCritical == 1){
            criticalIssuesTV.setText("" + numCritical + " " + CRITICAL_ISSUE);
        }
        else {
            criticalIssuesTV.setText("" + numCritical + " " + CRITICAL_ISSUES);
        }

        if(numNonCritical == 1){
            nonCriticalIssuesTV.setText("" + numNonCritical + " " + NON_CRITICAL_ISSUE);
        }
        else {
            nonCriticalIssuesTV.setText("" + numNonCritical + " " + NON_CRITICAL_ISSUES);
        }


        ReportData.HazardRating hazardRating = report.getHazardRating();
        if(hazardRating == ReportData.HazardRating.HIGH){
            hazardLevelTV.setText(HAZARD_LEVEL + HIGH);
            leftWarningSign.setImageResource(R.drawable.red_warning_sign);
            rightWarningSign.setImageResource(R.drawable.red_warning_sign);
        }
        if(hazardRating == ReportData.HazardRating.MODERATE){
            hazardLevelTV.setText(HAZARD_LEVEL + MODERATE);
            leftWarningSign.setImageResource(R.drawable.yellow_warning_sign);
            rightWarningSign.setImageResource(R.drawable.yellow_warning_sign);
        }
        if(hazardRating == ReportData.HazardRating.LOW){
            hazardLevelTV.setText(HAZARD_LEVEL + LOW);
            leftWarningSign.setImageResource(R.drawable.green_warning_sign);
            rightWarningSign.setImageResource(R.drawable.green_warning_sign);
        }
        if(hazardRating == ReportData.HazardRating.OTHER){
            hazardLevelTV.setText(HAZARD_LEVEL + OTHER);
            leftWarningSign.setImageResource(R.drawable.grey_warning_sign);
            rightWarningSign.setImageResource(R.drawable.grey_warning_sign);
        }



    }

    private ArrayList<ViolationData> getViolations(ArrayList<ReportData> restaurantReports) {
        Intent intent = getIntent();
        int index = intent.getIntExtra(SELECTED_REPORT, 0);
        ReportData report = restaurantReports.get(index);

        return report.getViolations();
    }

    private ArrayList<ReportData> getReports(ArrayList<ReportData> allReports){
        Intent intent = getIntent();
        String trackingNumber = intent.getStringExtra(TRACKING_NUMBER);

        List<ReportData> reportsOfRestaurant = new ArrayList<>();

        for(ReportData report : allReports){
            if(report.getTrackingNumber().equals(trackingNumber)){
                reportsOfRestaurant.add(report);
            }

        }

        return (ArrayList<ReportData>) reportsOfRestaurant;
    }



    private void setUpRecyclerView(ArrayList<ViolationData> violations) {
        RecyclerView violationsRecyclerView = findViewById(R.id.violationsRecyclerView);
        if(violations.isEmpty()){
            TextView violationsTV = (TextView) findViewById(R.id.inspectionDetailsViolationTV);
            violationsTV.setText(NO_VIOLATIONS_AVAILABLE);
        }

        ViolationRecyclerViewAdapter adapter = new ViolationRecyclerViewAdapter(this, violations);
        violationsRecyclerView.setAdapter(adapter);
        violationsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    public static Intent makeLaunchIntent(Context context, String trackingNumber, int reportIndex){
        Intent intent = new Intent(context, InspectionDetailsActivity.class);
        intent.putExtra(TRACKING_NUMBER, trackingNumber);
        intent.putExtra(SELECTED_REPORT, reportIndex);
        return intent;
    }

}
