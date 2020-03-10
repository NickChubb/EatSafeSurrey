package com.example.restaurantinsurrey.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.restaurantinsurrey.R;
import com.example.restaurantinsurrey.RestaurantListActivity;
//import com.example.restaurantinsurrey.SingleRestaurant;
import com.example.restaurantinsurrey.model.DataFileProcessor;
import com.example.restaurantinsurrey.model.DataManager;
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
        String inspectionDateText = manager.getLastInspection(trackingNumber);
        holder.date.setText(inspectionDateText);
        Bitmap bitmap = current_restaurant.getImage();
        bitmap = DataFileProcessor.zoomBitmap(bitmap, holder.image.getLayoutParams().width, holder.image.getLayoutParams().height);
        holder.image.setImageBitmap(bitmap);
        int issues = manager.getRestaurantIssuesLength(trackingNumber);
        String issuesText = mContext.getString(R.string.text_issue_num, issues);
        holder.issues.setText(issuesText);

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
        ImageView image;
        ConstraintLayout parentLayout;
        TextView issues;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imageRestaurant);
            name = itemView.findViewById(R.id.txtName);
            date = itemView.findViewById(R.id.txtDate);
            issues = itemView.findViewById(R.id.txtIssueNum);
            parentLayout = itemView.findViewById(R.id.restaurantRecyclerLayout);

        }
    }

}
