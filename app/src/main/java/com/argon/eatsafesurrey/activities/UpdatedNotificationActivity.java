package com.argon.eatsafesurrey.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.argon.eatsafesurrey.R;
import com.argon.eatsafesurrey.ui.UpdatedFavoritesAdapter;

/*
If the user's favorite restaurant has new update, this activity will be shown.
It tells user what restaurant has been updated.
 */
public class UpdatedNotificationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updated_notification);
        Toolbar toolbar = findViewById(R.id.updated_notification_tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        populateList();
    }

    private void populateList() {
        RecyclerView list = findViewById(R.id.list_updated_notification);

        list.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        list.setLayoutManager(new LinearLayoutManager(this));

        UpdatedFavoritesAdapter adapter = new UpdatedFavoritesAdapter(this);
        list.setAdapter(adapter);
    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    public static Intent makeLaunchIntent(Context context){
        Intent intent = new Intent(context, UpdatedNotificationActivity.class);
        return intent;
    }
}
