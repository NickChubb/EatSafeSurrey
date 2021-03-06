package com.argon.eatsafesurrey.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.argon.eatsafesurrey.R;
import com.argon.eatsafesurrey.activities.RestaurantDetailActivity;
import com.argon.eatsafesurrey.model.DataFactory;
import com.argon.eatsafesurrey.model.DataManager;
import com.argon.eatsafesurrey.model.ReportData;
import com.argon.eatsafesurrey.model.RestaurantData;

import java.util.ArrayList;

/*
 *   This is the adapter for showing each restaurant in the RecyclerView of UpdatedNotificationActivity.
 *
 */

public class UpdatedFavoritesAdapter extends RecyclerView.Adapter<UpdatedFavoritesAdapter.ImageViewHolder> {

    final public static String TAG = "UpdatedFavoritesAdapter";

    private Context mContext;

    private DataManager manager;
    private ArrayList<RestaurantData> restaurants;

    public UpdatedFavoritesAdapter(Context mContext) {
        this.mContext = mContext;

        manager = DataManager.getInstance();
        restaurants = manager.createFavoriteList();
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.restaurant_list_view_layout, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        RestaurantData current_restaurant = restaurants.get(position);
        String trackingNumber = current_restaurant.getTrackingNumber();

        holder.name.setText(current_restaurant.getName());
        ReportData report = manager.getLastInspection(trackingNumber);

        String inspectionDateText;
        int hazardResourceId;
        int issues;
        if(report != null){
            inspectionDateText = DataFactory.getFormattedDate(mContext, report.getInspectionDate());
            hazardResourceId = DataFactory.getHazardRatingImage(report.getHazardRating());
            issues = report.getNumNonCritical() + report.getNumCritical();
        } else {
            inspectionDateText = mContext.getString(R.string.text_inspection_no_date);
            hazardResourceId = R.drawable.green_warning_sign;
            issues = 0;
        }
        holder.date.setText(inspectionDateText);
        holder.hazardImage.setImageResource(hazardResourceId);
        String issuesText = mContext.getString(R.string.text_issues, issues);
        holder.issues.setText(issuesText);
        Bitmap bitmap = manager.getImageByTrackingNumber(trackingNumber);
        if(bitmap != null) {
            bitmap = DataFactory.zoomBitmap(bitmap, holder.restaurantImage.getLayoutParams().width, holder.restaurantImage.getLayoutParams().height);
            holder.restaurantImage.setImageBitmap(bitmap);
        }

        int index = manager.getIndexForRestaurant(current_restaurant);
        holder.itemView.setOnClickListener(v -> {
            //launches calculate activity on ListView item click
            Intent intent = RestaurantDetailActivity.makeLaunchIntent(mContext, index);
            mContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return restaurants.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder{
        TextView name;
        TextView date;
        ImageView restaurantImage;
        ImageView hazardImage;
        ConstraintLayout parentLayout;
        TextView issues;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            restaurantImage = itemView.findViewById(R.id.image_restaurant_list_restaurant);
            hazardImage = itemView.findViewById(R.id.image_restaurant_list_hazard);
            name = itemView.findViewById(R.id.text_restaurant_list_name);
            date = itemView.findViewById(R.id.text_restaurant_list_date);
            issues = itemView.findViewById(R.id.text_restaurant_list_issue_num);
            parentLayout = itemView.findViewById(R.id.list_updated_notification);
        }
    }

}
