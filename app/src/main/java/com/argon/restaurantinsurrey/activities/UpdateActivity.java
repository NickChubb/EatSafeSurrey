package com.argon.restaurantinsurrey.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.argon.restaurantinsurrey.R;
import com.argon.restaurantinsurrey.model.UpdateManager;

public class UpdateActivity extends AppCompatActivity {

    public static Intent makeLaunchIntent(Context c) {
        Intent intent = new Intent(c, RestaurantListActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_data);


        //This is how to update data
        //Those four lines should be implemented in the UpdatingActivity rather than here
        //Please implement the UpdatingActivity, and then move these lines to there.

        UpdateManager updateManager = UpdateManager.getInstance();

        short availableUpdates = updateManager.getAvailableUpdates();

        //AvailableUpdates returns the thing that you need to update

        updateManager.updateData(availableUpdates);


        Button btnCancel = findViewById(R.id.buttonCancelUpdate);
        btnCancel.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                
            }
        });


    }
}
