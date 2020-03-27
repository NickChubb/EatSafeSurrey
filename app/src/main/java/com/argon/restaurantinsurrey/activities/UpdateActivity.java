package com.argon.restaurantinsurrey.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.argon.restaurantinsurrey.R;
import com.argon.restaurantinsurrey.model.UpdateManager;

import org.w3c.dom.Text;

public class UpdateActivity extends AppCompatActivity {

    final public static String TAG = "UpdateActivity";

    private Button cancelButton;
    private TextView statusTextView;
    private ProgressBar progressBar;

    public static Intent makeLaunchIntent(Context c) {
        Intent intent = new Intent(c, UpdateActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_data);

        UpdateManager.createInstance(this);
        UpdateManager manager = UpdateManager.getInstance();

        setUpUI();

        short availableUpdates = manager.getAvailableUpdates();
        if (availableUpdates == UpdateManager.AvailableUpdates.NO_UPDATE){
            goToMainPage();
        } else {
            prepareForUpdate();
        }
    }

    private void setUpUI() {
        cancelButton = findViewById(R.id.button_update_cancel);
        statusTextView = findViewById(R.id.text_update_status);
        progressBar = findViewById(R.id.bar_update_progress);
        cancelButton.setVisibility(View.INVISIBLE);
        statusTextView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void prepareForUpdate() {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.title_update_alert)
                .setMessage(R.string.message_update_alert)
                .setPositiveButton(R.string.text_update_alert_update, (dialog, which) -> {

                })

                .setNegativeButton(R.string.text_update_alert_data_only, (dialog, which) -> {

                })

                .setNeutralButton(R.string.text_update_alert_no, (dialog, which) -> {
                    goToMainPage();
                }).create();

        alertDialog.show();
    }

    private void goToMainPage(){
        Intent i = MapAndRestaurantListActivity.makeLaunchIntent(this);
        startActivity(i);
        finish();
    }








}
