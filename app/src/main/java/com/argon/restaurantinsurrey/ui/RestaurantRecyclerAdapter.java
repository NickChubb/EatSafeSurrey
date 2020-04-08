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
import com.argon.restaurantinsurrey.activities.RestaurantDetailActivity;
import com.argon.restaurantinsurrey.model.DataFactory;
import com.argon.restaurantinsurrey.model.DataManager;
import com.argon.restaurantinsurrey.model.ReportData;
import com.argon.restaurantinsurrey.model.RestaurantData;

import java.util.ArrayList;
import java.util.List;

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

        restaurants = manager.createRestaurantsList();
        restaurantsListFull = manager.createRestaurantsList();
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
            restaurantImage = itemView.findViewById(R.id.image_restaurant_list_restaurant);
            hazardImage = itemView.findViewById(R.id.image_restaurant_list_hazard);
            name = itemView.findViewById(R.id.text_restaurant_list_name);
            date = itemView.findViewById(R.id.text_restaurant_list_date);
            issues = itemView.findViewById(R.id.text_restaurant_list_issue_num);
            parentLayout = itemView.findViewById(R.id.restaurantRecyclerLayout);
        }
    }




    public void setFilteredRecyclerView(List<RestaurantData> restaurantData){
        restaurants.clear();
        restaurants.addAll(restaurantData);
        notifyDataSetChanged();

    }

}
