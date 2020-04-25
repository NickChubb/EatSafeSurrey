package com.argon.eatsafesurrey.ui;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.argon.eatsafesurrey.R;
import com.argon.eatsafesurrey.model.ViolationData;

import java.util.ArrayList;

/*
 *   This is the adapter for showing each violation in the RecyclerView of InspectionDetailsActivity
 *
 */

public class ViolationRecyclerViewAdapter extends RecyclerView.Adapter<ViolationRecyclerViewAdapter.ViewHolder>{

    private static final String TAG = "ViolationRecyclerViewAd";

    private Context mContext;
    private ArrayList<ViolationData> violationData;

    private final int FOOD_ICON = R.drawable.food_icon;
    private final int DOCUMENTATION_ICON = R.drawable.documentation_icon;
    private final int UTENSILS_ICON = R.drawable.utensiles_icon;
    private final int UNSANITARY_ICON = R.drawable.unsanitary_icon;

    public ViolationRecyclerViewAdapter(Context mContext, ArrayList<ViolationData> violationData) {
        this.mContext = mContext;
        this.violationData = violationData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.custom_violation_list,parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ViolationData violation = violationData.get(position);
        String violationDetails = violation.getDetail();

        if(violation.isCritical()){
            holder.itemView.setBackgroundColor(mContext.getColor(R.color.colorLightRed));
        }
        else {
            holder.itemView.setBackgroundColor(mContext.getColor(R.color.colorLightGreen));
        }

        Resources r = mContext.getResources();

        int violationIndex = 0;
        int violationNumber = violation.getViolationNumber();

        int[] violationCodesArray = r.getIntArray(R.array.violation_codes);
        String[] violationDescriptionArray = r.getStringArray(R.array.violation_short_descriptions);
        for(int i = 0; i < violationCodesArray.length; i++){
            if (violationNumber == violationCodesArray[i]){
                violationIndex = i;
                break;
            }
        }

        holder.violationDescription.setText(violationDescriptionArray[violationIndex]);

        if(violationNumber >= 100 && violationNumber < 200){
            holder.natureOfViolationImage.setImageResource(DOCUMENTATION_ICON);
        }
        if(violationNumber >= 200 && violationNumber < 300){
            holder.natureOfViolationImage.setImageResource(FOOD_ICON);
        }
        if(violationNumber >= 300 && violationNumber < 400){
            holder.natureOfViolationImage.setImageResource(UTENSILS_ICON);
        }
        if(violationNumber >= 400 && violationNumber < 500){
            holder.natureOfViolationImage.setImageResource(UNSANITARY_ICON);
        }
        if(violationNumber >= 500){
            holder.natureOfViolationImage.setImageResource(DOCUMENTATION_ICON);
        }

        holder.parentLayout.setOnClickListener(v -> Toast.makeText(mContext, violationDetails, Toast.LENGTH_SHORT).show());
    }

    @Override
    public int getItemCount() {
        return violationData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

//        ImageView violationImage;
        ImageView natureOfViolationImage;
        TextView violationDescription;
        ConstraintLayout parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //violationImage = itemView.findViewById(R.id.violationItemImageView);
            violationDescription = itemView.findViewById(R.id.text_violation_cell_violation);
            natureOfViolationImage = itemView.findViewById(R.id.image_violation_cell_nature);
            parentLayout = itemView.findViewById(R.id.violationParentLayout);
        }
    }




}
