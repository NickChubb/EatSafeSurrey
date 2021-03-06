package com.argon.eatsafesurrey.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.argon.eatsafesurrey.R;
import com.argon.eatsafesurrey.model.DataFactory;
import com.argon.eatsafesurrey.model.DataManager;
import com.argon.eatsafesurrey.model.ReportData;
import com.argon.eatsafesurrey.model.ViolationData;
import com.argon.eatsafesurrey.ui.ViolationRecyclerViewAdapter;

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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
        TextView dateTextView = findViewById(R.id.text_inspection_date);
        TextView inspectionTypeTextView = findViewById(R.id.text_inspection_details_type);
        TextView criticalIssuesTextView = findViewById(R.id.text_inspection_details_critical_issues);
        TextView nonCriticalIssuesTextView = findViewById(R.id.text_inspection_details_non_critical_issues);
        TextView hazardLevelTextView = findViewById(R.id.text_inspection_details_hazard_level);

        ImageView warningImageView = findViewById(R.id.image_inspection_details_hazard_sign);

        Date inspectionDate = report.getInspectionDate();
        SimpleDateFormat dateFormat = new SimpleDateFormat(getString(R.string.basic_date_format), Locale.getDefault());
        String dateString = dateFormat.format(inspectionDate);
        dateTextView.setText(getString(R.string.text_inspection_date, dateString));

        ReportData.InspType inspectionType = report.getInspType();
        String inspectionTypeString;
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
        int hazardTextColor = DataFactory.getHazardTextColor(hazardRating);
        hazardLevelTextView.setTextColor(getColor(hazardTextColor));

        int hazardRatingImage = DataFactory.getHazardRatingImage(hazardRating);
        warningImageView.setImageResource(hazardRatingImage);
    }

    private void setUpRecyclerView() {
        RecyclerView violationsRecyclerView = findViewById(R.id.list_inspection_details_violations);
        if(violationData.isEmpty()){
            TextView violationsTV = findViewById(R.id.text_inspection_details_violation);
            violationsTV.setText(getString(R.string.title_no_violations));
        }

        ViolationRecyclerViewAdapter adapter = new ViolationRecyclerViewAdapter(this, violationData);
        violationsRecyclerView.setAdapter(adapter);
        violationsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    public static Intent makeLaunchIntent(Context context, String trackingNumber, int reportIndex){
        Intent intent = new Intent(context, InspectionDetailsActivity.class);
        intent.putExtra(TRACKING_NUMBER, trackingNumber);
        intent.putExtra(SELECTED_REPORT, reportIndex);
        return intent;
    }
}
