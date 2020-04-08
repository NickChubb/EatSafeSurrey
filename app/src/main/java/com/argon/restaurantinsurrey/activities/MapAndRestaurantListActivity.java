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
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.SearchView;
import android.widget.Toast;

import com.argon.restaurantinsurrey.R;
import com.argon.restaurantinsurrey.model.DataManager;
import com.argon.restaurantinsurrey.model.ReportData;
import com.argon.restaurantinsurrey.model.RestaurantData;
import com.argon.restaurantinsurrey.model.SearchFilter;
import com.argon.restaurantinsurrey.model.ReportData;
import com.argon.restaurantinsurrey.model.RestaurantData;
import com.argon.restaurantinsurrey.model.SearchFilter;
import com.argon.restaurantinsurrey.ui.ClusterMarker;
import com.argon.restaurantinsurrey.model.RestaurantData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/*
 *
 *  Switch between MapFragment and RestaurantListFragment
 *
 */

public class MapAndRestaurantListActivity extends AppCompatActivity {

    public static final String TAG = "MapAndRestaurantListActivity";

    DataManager dataManager;
    private List<Fragment> pages;


    private AlertDialog filterDialog;
    private ReportData.HazardRating hazardLevelFilterOption;
    private SearchFilter searchFilter;

    private DataManager manager;
    private List<RestaurantData> restaurantsListFull;
    private ReportData.HazardRating filterHazardRating = null;
    private String filterName = null;
    private String filterMinimumViolation = null;
    private String filterMaximumViolation = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_and_restaurant_list);
        Toolbar toolbar = findViewById(R.id.toolbar_map_and_restaurant);
        setSupportActionBar(toolbar);

        Log.i(TAG, "onCreate: ");
        DataManager.createInstance(this);
        dataManager = DataManager.getInstance();
        manager = DataManager.getInstance();
        restaurantsListFull = manager.createRestaurantsList();
        searchFilter = new SearchFilter();

        pages = new ArrayList<>();


        setUpUI();
        setUpSearchBar();
        setUpFilterOptionButton();

        if(dataManager.hasNewUpdatedFavorites()){
            Intent intent = UpdatedNotificationActivity.makeLaunchIntent(this);
            startActivity(intent);
        }
    }

    private void setUpUI() {
        pages = new ArrayList<>();
        pages.add(new MapFragment());
        pages.add(new RestaurantListFragment());

        ViewPager viewPager = (ViewPager)findViewById(R.id.ViewPager_MapAndRestaurantListActivity_vp);
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.RadioGroup_MapAndRestaurantListActivity_rg);
        RadioButton radioButton_Map = findViewById(R.id.RadioButton_MapAndRestaurantListActivity_map);
        RadioButton radioButton_Restaurant = findViewById(R.id.RadioButton_MapAndRestaurantListActivity_restaurant);

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

    }

    private void setUpFilterOptionButton() {
        Button filterButton = findViewById(R.id.button_filter_map_and_list_activity);
        filterButton.setOnClickListener(click ->
                openDialog()
        );
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

            radioButton.setText(getResources().getString(R.string.hazard_level) + level);

            radioButton.setOnClickListener(click->{

                if (level.equals(getResources().getString(R.string.hazard_level_filter_option_low))){
                    hazardLevelFilterOption = ReportData.HazardRating.LOW;
                }
                else if (level.equals(getResources().getString(R.string.hazard_level_filter_option_moderate))){
                    hazardLevelFilterOption = ReportData.HazardRating.MODERATE;
                }
                else if (level.equals(getResources().getString(R.string.hazard_level_filter_option_high))){
                    hazardLevelFilterOption = ReportData.HazardRating.HIGH;
                }
                else {
                    hazardLevelFilterOption = null;
                }
            });

            radioGroup.addView(radioButton);
        }

        builder.setView(view);
        filterDialog = builder.create();
        filterDialog.show();

        EditText minimumViolationEditText = view.findViewById(R.id.edit_text_min_critical_violations_map_and_list_activity);
        EditText maximumViolationEditText = view.findViewById(R.id.edit_text_max_critical_violations_map_and_list_activity);


        Button cancelButton = view.findViewById(R.id.button_filter_dialog_cancel);
        Button okButton = view.findViewById(R.id.button_filter_dialog_ok);

        cancelButton.setOnClickListener(click -> filterDialog.dismiss());
        okButton.setOnClickListener(click-> {
            filterMinimumViolation = minimumViolationEditText.getText().toString();
            filterMaximumViolation = maximumViolationEditText.getText().toString();

            ((MapFragment)pages.get(0)).setFilteredMap(
                    searchFilter.filterAll(filterName,
                            hazardLevelFilterOption,
                            filterMinimumViolation,
                            filterMaximumViolation));

            ((RestaurantListFragment) pages.get(1)).
                    setFilteredRestaurant(
                            searchFilter.filterAll(filterName,
                                    hazardLevelFilterOption,
                                    filterMinimumViolation,
                                    filterMaximumViolation));

            filterDialog.dismiss();
        });

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
                filterName = newText;
                ((MapFragment)pages.get(0)).setFilteredMap(
                        searchFilter.filterAll(filterName,
                                hazardLevelFilterOption,
                                filterMinimumViolation,
                                filterMaximumViolation));

                ((RestaurantListFragment) pages.get(1)).
                        setFilteredRestaurant(
                                searchFilter.filterAll(filterName,
                                        hazardLevelFilterOption,
                                        filterMinimumViolation,
                                        filterMaximumViolation));

                return false;
            }
        });

    }
    public static Intent makeLaunchIntent(Context c) {
        Intent intent = new Intent(c, MapAndRestaurantListActivity.class);
        return intent;
    }

    @Override
    protected void onStop() {
        super.onStop();
        dataManager.saveData();
    }

}
