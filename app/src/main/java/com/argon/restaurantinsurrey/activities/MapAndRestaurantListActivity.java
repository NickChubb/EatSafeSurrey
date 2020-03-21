package com.argon.restaurantinsurrey.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.argon.restaurantinsurrey.R;

import java.util.ArrayList;
import java.util.List;

public class MapAndRestaurantListActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private RadioGroup  radioGroup;

    private List<Fragment> pages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_and_restaurant_list);

        pages = new ArrayList<>();

        viewPager = (ViewPager)findViewById(R.id.ViewPager_MapAndRestaurantListActivity_vp);
        radioGroup = (RadioGroup)findViewById(R.id.RadioGroup_MapAndRestaurantListActivity_rg);

        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return null;
            }

            @Override
            public int getCount() {
                return 0;
            }
        });

    }
}
