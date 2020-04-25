package com.argon.eatsafesurrey.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.argon.eatsafesurrey.R;
import com.argon.eatsafesurrey.model.UpdateManager;

/*

This activity guides user to update the data from internet.

 */
public class UpdateActivity extends AppCompatActivity {

    final public static String TAG = "UpdateActivity";

    private Button cancelButton;
    private TextView statusTextView;
    private ProgressBar progressBar;
    private UpdateManager manager;

    public static Intent makeLaunchIntent(Context c) {
        Intent intent = new Intent(c, UpdateActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_data);

        manager = UpdateManager.getInstance(this);

        setUpUI();

        manager.setUpdateStatusListener(new UpdateManager.UpdateStatusListener() {
            @Override
            public void onPrepareUpdate(int maxProgress) {
                progressBar.setVisibility(View.VISIBLE);
                cancelButton.setVisibility(View.VISIBLE);
                statusTextView.setVisibility(View.VISIBLE);
                statusTextView.setText(R.string.status_update_waiting);
                progressBar.setProgress(0, false);
                progressBar.setMax(maxProgress);
            }

            @Override
            public void onStatusUpdated(UpdateManager.UpdateTypes type, int progress, int maxProgress) {
                switch (type){
                    case IMAGES:
                        statusTextView.setText(getString(R.string.status_update_downloading_images, progress, maxProgress));
                        break;
                    case REPORTS:
                        statusTextView.setText(R.string.status_update_downloading_reports);
                        break;
                    case RESTAURANTS:
                        statusTextView.setText(R.string.status_update_downloading_restaurants);
                        break;
                }
                progressBar.setProgress(progress, true);
                progressBar.setMax(maxProgress);
            }

            @Override
            public void onUpdateFinished() {
                goToMainPage();
            }

            @Override
            public void onUpdateCancelled() {
                goToMainPage();
            }
        });

        short availableUpdates = manager.getAvailableUpdates();
        if (availableUpdates == UpdateManager.AvailableUpdates.NO_UPDATE){
            goToMainPage();
        } else {
            prepareForUpdate(availableUpdates);
        }
    }

    private void setUpUI() {
        cancelButton = findViewById(R.id.button_update_cancel);
        statusTextView = findViewById(R.id.text_update_status);
        progressBar = findViewById(R.id.bar_update_progress);
        cancelButton.setVisibility(View.INVISIBLE);
        statusTextView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        cancelButton.setOnClickListener(v -> {
            manager.cancel(true);
        });
    }

    private void prepareForUpdate(short updates) {
        final short FINAL_UPDATES = updates;
        AlertDialog alertDialog;
        if(updates == UpdateManager.AvailableUpdates.IMAGES){
            alertDialog = new AlertDialog.Builder(this)
                    .setTitle(R.string.title_update_alert_image_only)
                    .setPositiveButton(R.string.text_update_alert_update, (dialog, which) -> {
                        manager.execute(FINAL_UPDATES);
                    })
                    .setNeutralButton(R.string.text_update_alert_no, (dialog, which) -> {
                        goToMainPage();
                    })
                    .create();
        } else {
            alertDialog = new AlertDialog.Builder(this)
                    .setTitle(R.string.title_update_alert)
                    .setPositiveButton(R.string.text_update_alert_update, (dialog, which) -> {
                        manager.execute(FINAL_UPDATES);
                    })
                    .setNegativeButton(R.string.text_update_alert_data_only, (dialog, which) -> {
                        short updates_without_image = (short) (FINAL_UPDATES & ~UpdateManager.AvailableUpdates.IMAGES);
                        manager.execute(updates_without_image);
                    })
                    .setNeutralButton(R.string.text_update_alert_no, (dialog, which) -> {
                        goToMainPage();
                    })
                    .create();
        }
        alertDialog.setCancelable(false);
        alertDialog.setMessage(getString(R.string.message_update_alert));
        alertDialog.show();
    }

    private void goToMainPage(){
        Intent i = MapAndRestaurantListActivity.makeLaunchIntent(this);
        startActivity(i);
        finish();
    }

}
