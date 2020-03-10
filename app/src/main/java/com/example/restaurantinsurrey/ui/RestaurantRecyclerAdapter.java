package com.example.restaurantinsurrey.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.restaurantinsurrey.R;
//import com.example.restaurantinsurrey.SingleRestaurant;
import com.example.restaurantinsurrey.model.DataFileProcessor;
import com.example.restaurantinsurrey.model.DataManager;
import com.example.restaurantinsurrey.model.ReportData;
import com.example.restaurantinsurrey.model.RestaurantData;

import java.util.ArrayList;

public class RestaurantRecyclerAdapter extends RecyclerView.Adapter<RestaurantRecyclerAdapter.ImageViewHolder> {

    final public static String TAG = "RestaurantRecyclerAdapter";

    private DataManager manager;

    private Context mContext;

    public RestaurantRecyclerAdapter(Context mContext){
        this.manager = DataManager.getInstance();
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.restaurant_list_view_layout, parent, false);

        ImageViewHolder imageViewHolder = new ImageViewHolder(view);

        return imageViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        ArrayList<RestaurantData> restaurants = manager.getAllRestaurants();
        RestaurantData current_restaurant = restaurants.get(position);
        String trackingNumber = current_restaurant.getTrackingNumber();

        holder.name.setText(current_restaurant.getName());
        ReportData report = manager.getLastInspection(trackingNumber);

        String inspectionDateText;
        int harzardResourceId;
        int issues;
        if(report != null){
            inspectionDateText = DataFileProcessor.getFormattedDate(mContext, report.getInspectionDate());
            harzardResourceId = DataFileProcessor.getHazardRatingImage(report.getHazardRating());
            issues = report.getNumNonCritical() + report.getNumNonCritical();
        } else {
            inspectionDateText = mContext.getString(R.string.text_inspection_no_date);
            harzardResourceId = R.drawable.green_warning_sign;
            issues = 0;
        }
        holder.date.setText(inspectionDateText);
        holder.hazardImage.setImageResource(harzardResourceId);
        String issuesText = mContext.getString(R.string.text_issue_num, issues);
        holder.issues.setText(issuesText);

        Bitmap bitmap = current_restaurant.getImage();
        if(bitmap != null) {
            bitmap = DataFileProcessor.zoomBitmap(bitmap, holder.restaurantImage.getLayoutParams().width, holder.restaurantImage.getLayoutParams().height);
            holder.restaurantImage.setImageBitmap(bitmap);
        }



        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //launches calculate activity on ListView item click

//                Intent i = SingleRestaurant.makeLaunchIntent(RestaurantListActivity.this, position);
//                mContext.startActivity(i);

            }
        });

    }

    @Override
    public int getItemCount() {
        return manager.getRestaurantsSize();
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
            restaurantImage = itemView.findViewById(R.id.imageRestaurant);
            hazardImage = itemView.findViewById(R.id.restaurant_list_image_hazard);
            name = itemView.findViewById(R.id.txtName);
            date = itemView.findViewById(R.id.txtDate);
            issues = itemView.findViewById(R.id.txtIssueNum);
            parentLayout = itemView.findViewById(R.id.restaurantRecyclerLayout);
        }
    }

}
