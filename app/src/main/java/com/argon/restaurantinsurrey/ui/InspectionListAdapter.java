package com.argon.restaurantinsurrey.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.argon.restaurantinsurrey.R;
import com.argon.restaurantinsurrey.model.ReportData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

//Todo: Totally Refactor
public class InspectionListAdapter extends ArrayAdapter<ReportData> {

    private static final String TAG = "InspectionListAdapter";

    private Context mContext;
    int mResource;
    public InspectionListAdapter(Context context, int resource, ArrayList<ReportData> reports) {
        super(context, resource, reports);

        mContext = context;
        mResource = resource;
    }

    private final String CRITICAL_ISSUES =getContext().getString(R.string.critical_issues);
    private final String NON_CRITICAL_ISSUES = getContext().getString(R.string.non_critical_issues);
    private final String CRITICAL_ISSUE = getContext().getString(R.string.critical_issue);
    private final String NON_CRITICAL_ISSUE = getContext().getString(R.string.non_critical_issue);
    private final String ISSUES = getContext().getString(R.string.issues);
    private final String ISSUE = getContext().getString(R.string.issue);
    private final String HIGH = getContext().getString(R.string.high);
    private final String MODERATE = getContext().getString(R.string.moderate);
    private final String LOW = getContext().getString(R.string.low);
    private final String OTHER = getContext().getString(R.string.other);

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        int numberOfCriticalIssues = getItem(position).getNumCritical();
        int numberOfNonCriticalIssues = getItem(position).getNumNonCritical();
        int totalIssues = numberOfCriticalIssues + numberOfNonCriticalIssues;

        Date inspectionDate = getItem(position).getInspectionDate();
        Enum<ReportData.HazardRating> HazardRating = getItem(position).getHazardRating();

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        ImageView warningSign = (ImageView) convertView.findViewById(R.id.inspectionHarzardLevelImageView);
        TextView criticalIssuesTextView = (TextView) convertView.findViewById(R.id.numberOfCriticalIssuesTV);
        TextView nonCriticalIssuesTextView = (TextView) convertView.findViewById(R.id.numberOfNonCriticalIssuesTV);
        TextView totalIssuesTextView = (TextView) convertView.findViewById(R.id.numberOfIssuesTV);
        TextView hazardLevelTextView = (TextView) convertView.findViewById(R.id.levelsTV);
        TextView inspectionDateTextView  = (TextView) convertView.findViewById(R.id.inspectionDetailsDateTV);

        if(numberOfCriticalIssues == 1) {
            criticalIssuesTextView.setText("" + numberOfCriticalIssues + " " + CRITICAL_ISSUE);
        }
        else{
            criticalIssuesTextView.setText("" + numberOfCriticalIssues + " " + CRITICAL_ISSUES);
        }

        if(numberOfNonCriticalIssues == 1) {
            nonCriticalIssuesTextView.setText("" + numberOfNonCriticalIssues + " " + NON_CRITICAL_ISSUE);
        }
        else {
            nonCriticalIssuesTextView.setText("" + numberOfNonCriticalIssues + " " + NON_CRITICAL_ISSUES);
        }

        if(totalIssues == 1){
            totalIssuesTextView.setText("" + totalIssues + " " + ISSUE);

        }
        else {
            totalIssuesTextView.setText("" + totalIssues + " " + ISSUES);
        }


        SimpleDateFormat dateFormat = new SimpleDateFormat(mContext.getString(R.string.basic_date_format));
        String date = (String)dateFormat.format(inspectionDate);
        inspectionDateTextView.setText(""+ date);


        if(HazardRating == ReportData.HazardRating.HIGH){
            hazardLevelTextView.setText(HIGH);
            hazardLevelTextView.setTextColor(mContext.getResources().getColor(R.color.colorRed));
            warningSign.setImageResource(R.drawable.red_warning_sign);
        }
        if(HazardRating == ReportData.HazardRating.MODERATE){
            hazardLevelTextView.setText(MODERATE);
            hazardLevelTextView.setTextColor(mContext.getResources().getColor(R.color.colorYellow));
            warningSign.setImageResource(R.drawable.yellow_warning_sign);
        }
        if(HazardRating == ReportData.HazardRating.LOW){
            hazardLevelTextView.setText(LOW);
            hazardLevelTextView.setTextColor(mContext.getResources().getColor(R.color.colorGreen));
            warningSign.setImageResource(R.drawable.green_warning_sign);
        }
        if(HazardRating == ReportData.HazardRating.OTHER){
            hazardLevelTextView.setText(OTHER);
            hazardLevelTextView.setTextColor(mContext.getResources().getColor(R.color.colorGrey));
            warningSign.setImageResource(R.drawable.grey_warning_sign);
        }

        return convertView;
    }
}
