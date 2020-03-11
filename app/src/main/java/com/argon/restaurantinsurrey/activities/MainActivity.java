package com.argon.restaurantinsurrey.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.os.StrictMode;
import android.view.Menu;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.argon.restaurantinsurrey.R;
import com.argon.restaurantinsurrey.model.DataFactory;
import com.argon.restaurantinsurrey.model.DataManager;

/*
 *   This is the activity for showing welcome screen and animation.
 *   This is the entrance of application
 *   Data are initialized in this activity
 */
public class MainActivity extends AppCompatActivity {
    private Runnable runnable;
    final public static String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.activity_main);

        // animation of the splash screen
        setIconAnim();
        setWelcomeAnim();

        //Auto jump to the restaurant activity
        Handler handler = new Handler();
        runnable = () -> {
            DataFactory.getDataFromInternet = true;
            DataManager.createInstance(this);
            Intent i = RestaurantListActivity.makeLaunchIntent(MainActivity.this);
            startActivity(i);
        };
        handler.postDelayed(runnable,3000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void setWelcomeAnim() {
        TextView Welcome = findViewById(R.id.text_welcome_title);
        Animation animation = AnimationUtils.loadAnimation(this,R.anim.fadein);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        Welcome.startAnimation(animation);
    }

    private void setIconAnim() {
        ImageView icon = findViewById(R.id.icon_welcome_fork);
        Animation animation = AnimationUtils.loadAnimation(this,R.anim.zoomin);
        icon.startAnimation(animation);
    }
}