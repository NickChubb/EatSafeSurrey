package com.example.restaurantinsurrey;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    Runnable runnable;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.singleRestaurantToolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // animation of the splash screen
        setIconAnim();
        setWelcomeAnim();

        //Auto jump to the restaurant activity
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                Intent i = restaurant_list.makeLaunchIntent(MainActivity.this);
                startActivity(i);
            }
        };
        handler.postDelayed(runnable,3000);

        return true;
    }

    private void setWelcomeAnim() {
        TextView Welcome = (TextView) findViewById(R.id.txt_welcome);
        Animation animation = AnimationUtils.loadAnimation(this,R.anim.fadein);
        Welcome.startAnimation(animation);
    }

    private void setIconAnim() {
        ImageView icon = (ImageView)findViewById(R.id.Icon_Fork);
        Animation animation = AnimationUtils.loadAnimation(this,R.anim.zoomin);
        icon.startAnimation(animation);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
