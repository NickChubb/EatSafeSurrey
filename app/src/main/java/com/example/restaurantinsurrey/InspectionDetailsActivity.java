package com.example.restaurantinsurrey;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.restaurantinsurrey.ui.ViolationRecyclerViewAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/*
 *   Displays all information about a single inspection
 *
 */
public class InspectionDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspection_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        setUpRecyclerView();


    }

    private void setUpRecyclerView() {
        RecyclerView violationsRecyclerView = findViewById(R.id.violationsRecyclerView);
        ViolationRecyclerViewAdapter adapter = new ViolationRecyclerViewAdapter(this);
        violationsRecyclerView.setAdapter(adapter);
        violationsRecyclerView.setLayoutManager(new LinearLayoutManager(this));


    }

    public static Intent makeLaunchIntent(Context context){
        Intent intent = new Intent(context, InspectionDetailsActivity.class);
        return intent;
    }

}
