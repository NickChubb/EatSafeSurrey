package com.argon.restaurantinsurrey;

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

import com.argon.restaurantinsurrey.model.DataFactory;
import com.argon.restaurantinsurrey.model.DataManager;

/*
 *   This is the activity for showing welcome screen and animation.
 *   This is the entrance of application
 *   Data are initialized in this activity
 */
public class MainActivity extends AppCompatActivity {
    Runnable runnable;
    private Handler handler;
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
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {

                Intent i = RestaurantListActivity.makeLaunchIntent(MainActivity.this);
                startActivity(i);
            }
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
        TextView Welcome = (TextView) findViewById(R.id.txt_welcome);
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
        ImageView icon = findViewById(R.id.Icon_Fork);
        Animation animation = AnimationUtils.loadAnimation(this,R.anim.zoomin);
        icon.startAnimation(animation);
    }
}
