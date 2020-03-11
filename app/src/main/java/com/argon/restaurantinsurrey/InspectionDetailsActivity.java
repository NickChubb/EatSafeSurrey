package com.argon.restaurantinsurrey;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.argon.restaurantinsurrey.model.DataFactory;
import com.argon.restaurantinsurrey.model.DataManager;
import com.argon.restaurantinsurrey.model.ReportData;
import com.argon.restaurantinsurrey.model.ViolationData;
import com.argon.restaurantinsurrey.ui.ViolationRecyclerViewAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/*
 *   Displays all information about a single inspection
 *
 */
public class InspectionDetailsActivity extends AppCompatActivity {

    final public static String TAG = "InspectionDetailsActivity";

    private static final String TRACKING_NUMBER = "tracking_number";
    private static final String SELECTED_REPORT = "selected_report";

    private ArrayList<ViolationData> violationData;
    private ReportData report;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspection_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setUpVariables();

        setUpUI();

        setUpRecyclerView();
    }

    private void setUpVariables() {
        DataManager manager = DataManager.getInstance();

        Intent intent = getIntent();
        String trackingNumber = intent.getStringExtra(TRACKING_NUMBER);
        int reportIndex = intent.getIntExtra(SELECTED_REPORT, 0);

        ArrayList<ReportData> restaurantReports = manager.getReports(trackingNumber);
        report = restaurantReports.get(reportIndex);
        violationData = report.getViolations();
    }



    private void setUpUI() {
        TextView dateTextView = findViewById(R.id.inspectionDetailsDateTV);
        TextView inspectionTypeTextView = findViewById(R.id.inspectionDetailsTypeTV);
        TextView criticalIssuesTextView = findViewById(R.id.inspectionDetailsCriticalIssuesTV);
        TextView nonCriticalIssuesTextView = findViewById(R.id.inspectionDetailsNonCriticalIssuesTV);
        TextView hazardLevelTextView = findViewById(R.id.text_inspection_details_hazard_level);

        ImageView warningImageView = findViewById(R.id.inspectionDetailsHazardSignLeftImageView);

        Date inspectionDate = report.getInspectionDate();
        SimpleDateFormat dateFormat = new SimpleDateFormat(getString(R.string.basic_date_format), Locale.getDefault());
        String dateString = dateFormat.format(inspectionDate);
        dateTextView.setText(getString(R.string.text_inspection_date, dateString));

        ReportData.InspType inspectionType = report.getInspType();
        String inspectionTypeString = "";
        switch (inspectionType){
            case ROUTINE:
                inspectionTypeString = getString(R.string.text_routine_inspection);
                break;
            case FOLLOW_UP:
                inspectionTypeString = getString(R.string.text_follow_up_inspection);
                break;
            default:
                inspectionTypeString = getString(R.string.text_other);
        }
        inspectionTypeTextView.setText(getString(R.string.text_inspection_type, inspectionTypeString));

        int numCritical = report.getNumCritical();
        int numNonCritical = report.getNumNonCritical();

        criticalIssuesTextView.setText(getString(R.string.text_critical_issues, numCritical));
        nonCriticalIssuesTextView.setText(getString(R.string.text_non_critical_issues, numNonCritical));

        ReportData.HazardRating hazardRating = report.getHazardRating();
        String hazardLevelString;
        switch (hazardRating){
            case HIGH:
                hazardLevelString = getString(R.string.text_high_hazard);
                break;
            case MODERATE:
                hazardLevelString = getString(R.string.text_moderate_hazard);
                break;
            case LOW:
                hazardLevelString = getString(R.string.text_low_hazard);
                break;
            default:
                hazardLevelString = getString(R.string.text_other);
        }
        hazardLevelTextView.setText(hazardLevelString);

        int hazardRatingImage = DataFactory.getHazardRatingImage(hazardRating);
        warningImageView.setImageResource(hazardRatingImage);
    }

    private void setUpRecyclerView() {
        RecyclerView violationsRecyclerView = findViewById(R.id.violationsRecyclerView);
        if(violationData.isEmpty()){
            TextView violationsTV = findViewById(R.id.inspectionDetailsViolationTV);
            violationsTV.setText(getString(R.string.title_no_violations));
        }

        ViolationRecyclerViewAdapter adapter = new ViolationRecyclerViewAdapter(this, violationData);
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
