package com.argon.restaurantinsurrey.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.argon.restaurantinsurrey.InspectionDetailsActivity;
import com.argon.restaurantinsurrey.R;
import com.argon.restaurantinsurrey.RestaurantActivity;
import com.argon.restaurantinsurrey.model.DataFactory;
import com.argon.restaurantinsurrey.model.DataManager;
import com.argon.restaurantinsurrey.model.ReportData;
import com.argon.restaurantinsurrey.model.RestaurantData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class InspectionRecyclerAdapter extends RecyclerView.Adapter<InspectionRecyclerAdapter.InspectionViewHolder>{
    final public String TAG = "InspectionRecyclerAdapter";

    private Context mContext;
    private String restaurantTrackingNumber;
    private ArrayList<ReportData> reports;

    public InspectionRecyclerAdapter(Context mContext, String restaurantTrackingNumber) {
        this.mContext = mContext;
        this.restaurantTrackingNumber = restaurantTrackingNumber;
        DataManager manager = DataManager.getInstance();
        reports = manager.getReports(restaurantTrackingNumber);
    }

    @NonNull
    @Override
    public InspectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_inspection_list_layout, parent, false);
        return new InspectionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InspectionViewHolder holder, int position) {
        ReportData currentReport = reports.get(position);

        int numOfCritical = currentReport.getNumCritical();
        holder.criticalIssuesTextView.setText(mContext.getString(R.string.text_critical_issues, numOfCritical));

        int numOfNonCritical = currentReport.getNumNonCritical();
        holder.nonCriticalIssuesTextView.setText(mContext.getString(R.string.text_non_critical_issues, numOfNonCritical));

        int numOfAllIssues = numOfCritical + numOfNonCritical;
        holder.totalIssuesTextView.setText(mContext.getString(R.string.text_issues, numOfAllIssues));

        String hazardLevelString;
        ReportData.HazardRating hazardRating = currentReport.getHazardRating();
        switch (hazardRating){
            case HIGH:
                hazardLevelString = mContext.getString(R.string.text_high_hazard);
                break;
            case MODERATE:
                hazardLevelString = mContext.getString(R.string.text_moderate_hazard);
                break;
            case LOW:
                hazardLevelString = mContext.getString(R.string.text_low_hazard);
                break;
            default:
                hazardLevelString = mContext.getString(R.string.text_other);
        }
        holder.hazardLevelTextView.setText(hazardLevelString);
        int hazardColor = mContext.getColor(DataFactory.getHazardTextColor(hazardRating));
        holder.hazardLevelTextView.setTextColor(hazardColor);

        int hazardRatingImage = DataFactory.getHazardRatingImage(hazardRating);
        holder.warningSignImageView.setImageResource(hazardRatingImage);

        SimpleDateFormat dateFormat = new SimpleDateFormat(mContext.getString(R.string.basic_date_format), Locale.getDefault());
        Date inspectionDate = currentReport.getInspectionDate();
        String dateString = dateFormat.format(inspectionDate);
        holder.inspectionDateTextView.setText(dateString);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Intent intent = InspectionDetailsActivity.makeLaunchIntent(mContext, restaurantTrackingNumber, position);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return reports.size();
    }

    public static class InspectionViewHolder extends RecyclerView.ViewHolder{

        ImageView warningSignImageView;
        TextView criticalIssuesTextView;
        TextView nonCriticalIssuesTextView;
        TextView totalIssuesTextView;
        TextView hazardLevelTextView;
        TextView inspectionDateTextView;

        public InspectionViewHolder(@NonNull View itemView) {
            super(itemView);
            warningSignImageView = itemView.findViewById(R.id.inspectionHarzardLevelImageView);
            criticalIssuesTextView = itemView.findViewById(R.id.numberOfCriticalIssuesTV);
            nonCriticalIssuesTextView = itemView.findViewById(R.id.numberOfNonCriticalIssuesTV);
            totalIssuesTextView = itemView.findViewById(R.id.numberOfIssuesTV);
            hazardLevelTextView = itemView.findViewById(R.id.levelsTV);
            inspectionDateTextView = itemView.findViewById(R.id.inspectionDetailsDateTV);
        }
    }
}
