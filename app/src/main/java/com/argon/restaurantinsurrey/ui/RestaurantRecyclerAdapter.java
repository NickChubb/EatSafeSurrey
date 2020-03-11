package com.argon.restaurantinsurrey.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.argon.restaurantinsurrey.R;
import com.argon.restaurantinsurrey.RestaurantActivity;
//import com.argon.restaurantinsurrey.SingleRestaurant;
import com.argon.restaurantinsurrey.model.DataFactory;
import com.argon.restaurantinsurrey.model.DataManager;
import com.argon.restaurantinsurrey.model.ReportData;
import com.argon.restaurantinsurrey.model.RestaurantData;

import java.util.ArrayList;

/*
 *   This is the adapter for showing each restaurant in the RecyclerView of RestaurantListActivity.
 *
 */

public class RestaurantRecyclerAdapter extends RecyclerView.Adapter<RestaurantRecyclerAdapter.ImageViewHolder> implements Filterable {

    final public static String TAG = "RestaurantRecyclerAdapter";

    private DataManager manager;

    private Context mContext;

    private ArrayList<RestaurantData> restaurants;
    private ArrayList<RestaurantData> restaurantsListFull;

    public RestaurantRecyclerAdapter(Context mContext){
        this.manager = DataManager.getInstance();
        this.mContext = mContext;

        restaurants = manager.getAllRestaurants();
        restaurantsListFull = manager.getAllRestaurants();
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.restaurant_list_view_layout, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, final int position) {

        RestaurantData current_restaurant = restaurants.get(position);
        String trackingNumber = current_restaurant.getTrackingNumber();

        holder.name.setText(current_restaurant.getName());
        ReportData report = manager.getLastInspection(trackingNumber);

        String inspectionDateText;
        int hazardResourceId;
//        int hazardBackgroundColorId;
        int issues;
        if(report != null){
            inspectionDateText = DataFactory.getFormattedDate(mContext, report.getInspectionDate());
            hazardResourceId = DataFactory.getHazardRatingImage(report.getHazardRating());
//            hazardBackgroundColorId = DataFileProcessor.getHazardRatingBackgroundColor(report.getHazardRating());
            issues = report.getNumNonCritical() + report.getNumCritical();
        } else {
            inspectionDateText = mContext.getString(R.string.text_inspection_no_date);
            hazardResourceId = R.drawable.green_warning_sign;
//            hazardBackgroundColorId = R.color.hazardBackgroundLow;
            issues = 0;
        }
//        int hazardBackgroundColor = mContext.getColor(hazardBackgroundColorId);
        holder.date.setText(inspectionDateText);
        holder.hazardImage.setImageResource(hazardResourceId);
        String issuesText = mContext.getString(R.string.text_issue_num, issues);
        holder.issues.setText(issuesText);
//        holder.parentLayout.setBackgroundColor(hazardBackgroundColor);
        Bitmap bitmap = current_restaurant.getImage();
        if(bitmap != null) {
            bitmap = DataFactory.zoomBitmap(bitmap, holder.restaurantImage.getLayoutParams().width, holder.restaurantImage.getLayoutParams().height);
            holder.restaurantImage.setImageBitmap(bitmap);
        }

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //launches calculate activity on ListView item click
                Intent intent = RestaurantActivity.makeLaunchIntent(mContext, position);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return restaurants.size();
}

    @Override
    public Filter getFilter() {
        return restaurantFilter;
    }

    private Filter restaurantFilter = new Filter(){
        @Override
        protected FilterResults performFiltering(CharSequence constraint){
            ArrayList<RestaurantData> filteredRestaurantList = new ArrayList<>();

            if(constraint == null || constraint.length() == 0){
                filteredRestaurantList.addAll(restaurantsListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for(RestaurantData restaurant : restaurantsListFull){
                        if(restaurant.getName().toLowerCase().contains(filterPattern)){
                            filteredRestaurantList.add(restaurant);
                        }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredRestaurantList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            restaurants.clear();
            restaurants.addAll((ArrayList)results.values);
            notifyDataSetChanged();
        }
    };

    public static class ImageViewHolder extends RecyclerView.ViewHolder{


        TextView name;
        TextView date;
        ImageView restaurantImage;
        ImageView hazardImage;
        ConstraintLayout parentLayout;
        TextView issues;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            restaurantImage = itemView.findViewById(R.id.imageRestaurant);
            hazardImage = itemView.findViewById(R.id.restaurant_list_image_hazard);
            name = itemView.findViewById(R.id.txtName);
            date = itemView.findViewById(R.id.txtDate);
            issues = itemView.findViewById(R.id.txtIssueNum);
            parentLayout = itemView.findViewById(R.id.restaurantRecyclerLayout);
        }
    }

}