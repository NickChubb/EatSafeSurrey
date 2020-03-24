package com.argon.restaurantinsurrey.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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


        // animation of the update screen
        setIconAnim();


        //This is how to update data
        //Those four lines should be implemented in the UpdatingActivity rather than here
        //Please implement the UpdatingActivity, and then move these lines to there.

        UpdateManager updateManager = UpdateManager.getInstance();

        short availableUpdates = updateManager.getAvailableUpdates();

        //AvailableUpdates returns the thing that you need to update

        updateManager.updateData(availableUpdates);


        Button btnCancel = findViewById(R.id.button_cancel_update);
        btnCancel.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


    }

    private void setIconAnim() {
        ImageView icon = findViewById(R.id.icon_update_arrow);
        RotateAnimation animation = new RotateAnimation(0.0f, -10.0f * 360.0f, 0, 0, 40, 0);;
        animation.setDuration(3000); // Change to length of download
        animation.setRepeatCount(0);
        icon.startAnimation(animation);
    }
}
