package com.example.restaurantinsurrey.ui;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.restaurantinsurrey.R;
import com.example.restaurantinsurrey.model.ReportData;
import com.example.restaurantinsurrey.model.ViolationData;

import java.util.ArrayList;

public class ViolationRecyclerViewAdapter extends RecyclerView.Adapter<ViolationRecyclerViewAdapter.ViewHolder>{

    private static final String TAG = "ViolationRecyclerViewAd";

    private Context mContext;
    private ArrayList<ViolationData> violationData;

    private int FOOD_ICON = R.drawable.food_icon;
    private int CRITICAL_ICON = R.drawable.critical;
    private int NON_CRITICAL_ICON = R.drawable.non_critical;
    private int RED_WARNING_SIGN = R.drawable.red_warning_sign;

    public ViolationRecyclerViewAdapter(Context mContext, ArrayList<ViolationData> violationData) {
        this.mContext = mContext;
        this.violationData = violationData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.custom_violation_list,parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, " onBindViewHolder: called.");

        ViolationData violation = violationData.get(position);
        String violationDetails = violation.getDetail();

        if(violation.isCritical() == true){
            holder.violationImage.setImageResource(CRITICAL_ICON);
        }
        else {
            holder.violationImage.setImageResource(NON_CRITICAL_ICON);
        }


        holder.natureOfViolationImage.setImageResource(FOOD_ICON);

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, violationDetails, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return violationData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView violationImage;
        ImageView natureOfViolationImage;
        ConstraintLayout parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            violationImage = itemView.findViewById(R.id.violationItemImageView);
            natureOfViolationImage = itemView.findViewById(R.id.natureOfViolationImageView);
            parentLayout = itemView.findViewById(R.id.violationParentLayout);
        }
    }



}
