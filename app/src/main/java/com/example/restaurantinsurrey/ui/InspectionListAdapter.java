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
    private static final String CRITICAL_ISSUES = "critical issues";
    private static final String NON_CRITICAL_ISSUES = "non critical issues";
    private static final String ISSUES = "issues";
    private static final String HIGH = "HIGH";
    private static final String MODERATE = "MODERATE";
    private static final String LOW = "LOW";
    private static final String OTHER = "OTHER";


    private Context mContext;
    int mResource;
    public InspectionListAdapter(Context context, int resource, ArrayList<ReportData> reports) {
        super(context, resource, reports);

        mContext = context;
        mResource = resource;
    }


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

        critialIssuesTV.setText(""+ numberOfCriticalIssues + " " + CRITICAL_ISSUES);
        nonCritialIssuesTV.setText(""+ numberOfNonCriticalIssues + " " + NON_CRITICAL_ISSUES);
        totalIssuesTV.setText("" + totalIssues + " " + ISSUES);


        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String date = (String)dateFormat.format(inspectionDate);
//        System.out.println(date);
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
