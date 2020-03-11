package com.example.restaurantinsurrey.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.restaurantinsurrey.R;
import com.example.restaurantinsurrey.model.ReportData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
        TextView critialIssuesTV = (TextView) convertView.findViewById(R.id.numberOfCriticalIssuesTV);
        TextView nonCritialIssuesTV = (TextView) convertView.findViewById(R.id.numberOfNonCriticalIssuesTV);
        TextView totalIssuesTV = (TextView) convertView.findViewById(R.id.numberOfIssuesTV);
        TextView hazardLevel = (TextView) convertView.findViewById(R.id.levelsTV);
        TextView inspectionDateTV = (TextView) convertView.findViewById(R.id.inspectionDetailsDateTV);

        if(numberOfCriticalIssues == 1) {
            critialIssuesTV.setText("" + numberOfCriticalIssues + " " + CRITICAL_ISSUE);
        }
        else{
            critialIssuesTV.setText("" + numberOfCriticalIssues + " " + CRITICAL_ISSUES);
        }

        if(numberOfNonCriticalIssues == 1) {
            nonCritialIssuesTV.setText("" + numberOfNonCriticalIssues + " " + NON_CRITICAL_ISSUE);
        }
        else {
            nonCritialIssuesTV.setText("" + numberOfNonCriticalIssues + " " + NON_CRITICAL_ISSUES);
        }

        if(totalIssues == 1){
            totalIssuesTV.setText("" + totalIssues + " " + ISSUE);

        }
        else {
            totalIssuesTV.setText("" + totalIssues + " " + ISSUES);
        }


        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String date = (String)dateFormat.format(inspectionDate);
        inspectionDateTV.setText(""+ date);


        if(HazardRating == ReportData.HazardRating.HIGH){
            hazardLevel.setText(HIGH);
            hazardLevel.setTextColor(mContext.getResources().getColor(R.color.colorRed));
            warningSign.setImageResource(R.drawable.red_warning_sign);
        }
        if(HazardRating == ReportData.HazardRating.MODERATE){
            hazardLevel.setText(MODERATE);
            hazardLevel.setTextColor(mContext.getResources().getColor(R.color.colorYellow));
            warningSign.setImageResource(R.drawable.yellow_warning_sign);
        }
        if(HazardRating == ReportData.HazardRating.LOW){
            hazardLevel.setText(LOW);
            hazardLevel.setTextColor(mContext.getResources().getColor(R.color.colorGreen));
            warningSign.setImageResource(R.drawable.green_warning_sign);
        }
        if(HazardRating == ReportData.HazardRating.OTHER){
            hazardLevel.setText(OTHER);
            hazardLevel.setTextColor(mContext.getResources().getColor(R.color.colorGrey));
            warningSign.setImageResource(R.drawable.grey_warning_sign);
        }

        return convertView;
    }
}
