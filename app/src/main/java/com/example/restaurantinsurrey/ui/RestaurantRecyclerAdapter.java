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
import com.example.restaurantinsurrey.RestaurantActivity;
import com.example.restaurantinsurrey.RestaurantListActivity;
//import com.example.restaurantinsurrey.SingleRestaurant;
import com.example.restaurantinsurrey.model.DataFileProcessor;
import com.example.restaurantinsurrey.model.RestaurantData;

import java.util.ArrayList;

public class RestaurantRecyclerAdapter extends RecyclerView.Adapter<RestaurantRecyclerAdapter.ImageViewHolder> {

    final public static String TAG = "RestaurantRecyclerAdapter";


    private ArrayList<RestaurantData> restaurants;
    private Context mContext;

    public RestaurantRecyclerAdapter(ArrayList<RestaurantData> restaurants, Context mContext){

        this.restaurants = restaurants;
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
    public void onBindViewHolder(@NonNull ImageViewHolder holder, final int position) {

        RestaurantData current_restaurant = restaurants.get(position);

        holder.name.setText(current_restaurant.getName());
        holder.date.setText("28 days ago");
        Bitmap bitmap = current_restaurant.getImage();
        bitmap = DataFileProcessor.zoomBitmap(bitmap, holder.image.getLayoutParams().width, holder.image.getLayoutParams().height);
        holder.image.setImageBitmap(bitmap);

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




    public static class ImageViewHolder extends RecyclerView.ViewHolder{


        TextView name;
        TextView date;
        ImageView image;
        ConstraintLayout parentLayout;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imageRestaurant);
            name = itemView.findViewById(R.id.txtName);
            date = itemView.findViewById(R.id.txtDate);

            parentLayout = itemView.findViewById(R.id.restaurantRecyclerLayout);

        }
    }

}
