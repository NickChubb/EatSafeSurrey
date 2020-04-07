package com.argon.restaurantinsurrey.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SearchView;

import com.argon.restaurantinsurrey.R;
import com.argon.restaurantinsurrey.model.DataManager;
import com.argon.restaurantinsurrey.ui.ClusterMarker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/*
 *
 *  Switch between MapFragment and RestaurantListFragment
 *
 */

public class MapAndRestaurantListActivity extends AppCompatActivity{

    public static final String TAG = "MapAndRestaurantListActivity";

    public static Intent makeLaunchIntent(Context c) {
        Intent intent = new Intent(c, MapAndRestaurantListActivity.class);
        return intent;
    }
    private ViewPager viewPager;
    private RadioGroup  radioGroup;
    private List<Fragment> pages;
    private RadioButton radioButton_Map;
    private RadioButton radioButton_Restaurant;
    private Fragment mapFragment;
    private Fragment restaurantListFragment;
    private AlertDialog filterDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_and_restaurant_list);
        Toolbar toolbar = findViewById(R.id.toolbar_map_and_restaurant);
        setSupportActionBar(toolbar);

        Log.i(TAG, "onCreate: ");
        DataManager.createInstance(this);
        
        
        pages = new ArrayList<>();

        mapFragment = new MapFragment();
        restaurantListFragment = new RestaurantListFragment();

        pages.add(mapFragment);

        pages.add(restaurantListFragment);

        viewPager = (ViewPager)findViewById(R.id.ViewPager_MapAndRestaurantListActivity_vp);
        radioGroup = (RadioGroup)findViewById(R.id.RadioGroup_MapAndRestaurantListActivity_rg);
        radioButton_Map = findViewById(R.id.RadioButton_MapAndRestaurantListActivity_map);
        radioButton_Restaurant = findViewById(R.id.RadioButton_MapAndRestaurantListActivity_restaurant);

        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return pages.get(position);
            }

            @Override
            public int getCount() {
                return pages.size();
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                FragmentTransaction fragmentTransaction;
                switch (position){
                    case 0:
                        radioButton_Map.setChecked(true);
                        break;
                    case 1:
                        radioButton_Restaurant.setChecked(true);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
                switch (checkedId) {
                    case R.id.RadioButton_MapAndRestaurantListActivity_map:
                        viewPager.setCurrentItem(0,true);
                        break;
                    case R.id.RadioButton_MapAndRestaurantListActivity_restaurant:
                        viewPager.setCurrentItem(1,true);
                        break;
                }
        });

        setUpSearchBar();
        setUpFilterOptionButton();
    }

    private void setUpFilterOptionButton() {
        Button filterButton = findViewById(R.id.button_filter_map_and_list_activity);
        filterButton.setOnClickListener(click -> openDialog());

    }

    private void openDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlerDialogTheme);
        View view = LayoutInflater.from(this).inflate(
                R.layout.custom_filter_dialog,
                findViewById(R.id.filter_dialog_container)
        );
        RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.radio_group_hazard_level_filter_map_and_list_activity);

        String[] hazardLevels = getResources().getStringArray(R.array.hazard_levels);

        for(int i = 0; i < hazardLevels.length; i++){

            String level = hazardLevels[i];

            RadioButton radioButton = new RadioButton(this);

            radioButton.setText(level);

            // TODO: set on-click callbacks
            radioGroup.addView(radioButton);
        }






        builder.setView(view);
        filterDialog = builder.create();
        filterDialog.show();
    }

    private void setUpSearchBar() {
        SearchView searchView = (SearchView) findViewById(R.id.search_bar_map_and_list_activity);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ((MapFragment)pages.get(0)).refreshMap(newText);
                ((RestaurantListFragment)pages.get(1)).refreshList(newText);
                return false;
            }
        });

    }

}
