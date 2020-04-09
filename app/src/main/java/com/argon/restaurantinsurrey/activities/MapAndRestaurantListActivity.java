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
import android.content.res.Resources;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SearchView;

import com.argon.restaurantinsurrey.R;
import com.argon.restaurantinsurrey.model.DataFactory;
import com.argon.restaurantinsurrey.model.DataManager;
import com.argon.restaurantinsurrey.model.ReportData;
import com.argon.restaurantinsurrey.model.RestaurantData;
import com.argon.restaurantinsurrey.model.RestaurantSearchFilter;

import java.util.ArrayList;
import java.util.List;

/*
 *
 *  Switch between MapFragment and RestaurantListFragment
 *
 */

public class MapAndRestaurantListActivity extends AppCompatActivity {

    public static final String TAG = "MapAndRestaurantListActivity";

    private DataManager dataManager;
    private List<Fragment> pages;

    private SearchView searchView;
    private RestaurantSearchFilter restaurantSearchFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_and_restaurant_list);
        Toolbar toolbar = findViewById(R.id.toolbar_map_and_restaurant);
        setSupportActionBar(toolbar);

        DataManager.createInstance(this);
        dataManager = DataManager.getInstance();

        restaurantSearchFilter = new RestaurantSearchFilter();

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
        MapFragment mapFragment = new MapFragment();
        RestaurantListFragment restaurantListFragment = new RestaurantListFragment();

        restaurantSearchFilter.setListener(restaurantList -> {
            restaurantListFragment.setFilteredRestaurant(restaurantList);
            mapFragment.setFilteredMap(restaurantList);
        });

        pages.add(mapFragment);
        pages.add(restaurantListFragment);

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
        View view = LayoutInflater.from(this).inflate(
                R.layout.custom_filter_dialog,
                findViewById(R.id.filter_dialog_container)
        );

        RadioGroup radioGroup = view.findViewById(R.id.radio_group_hazard_level_filter_map_and_list_activity);

        ArrayList<ReportData.HazardRating> ratings = new ArrayList<>();
        ratings.add(ReportData.HazardRating.OTHER);
        ratings.add(ReportData.HazardRating.LOW);
        ratings.add(ReportData.HazardRating.MODERATE);
        ratings.add(ReportData.HazardRating.HIGH);
        
        for(ReportData.HazardRating rating: ratings){
            String name = DataFactory.getHazardsName(this, rating);
            RadioButton radioButton = new RadioButton(this);
            radioButton.setText(name);
            radioButton.setChecked(rating == restaurantSearchFilter.getSelectedHazardRating());
            radioGroup.addView(radioButton);
            if(rating == restaurantSearchFilter.getSelectedHazardRating()){
                radioGroup.check(radioButton.getId());
            }
        }



        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            int position = group.indexOfChild(group.findViewById(checkedId));
            ReportData.HazardRating selectedHazard = ratings.get(position);
            restaurantSearchFilter.setSelectedHazardRating(selectedHazard);
        });

        EditText minimumViolationEditText = view.findViewById(R.id.edit_text_min_critical_violations_map_and_list_activity);
        EditText maximumViolationEditText = view.findViewById(R.id.edit_text_max_critical_violations_map_and_list_activity);
        if(restaurantSearchFilter.getMinNumOfCritical() >= 0){
            minimumViolationEditText.setText(Integer.toString(restaurantSearchFilter.getMinNumOfCritical()));
        }
        if(restaurantSearchFilter.getMaxNumOfCritical() >= 0){
            maximumViolationEditText.setText(Integer.toString(restaurantSearchFilter.getMaxNumOfCritical()));
        }

        CheckBox favouritesCheckBox = view.findViewById(R.id.checkbox_favourites_map_and_list_activity);
        favouritesCheckBox.setChecked(restaurantSearchFilter.getFilterFavourites());

        favouritesCheckBox.setOnClickListener(click -> {
            restaurantSearchFilter.setFilterFavourites(favouritesCheckBox.isChecked());
        });

        AlertDialog filterDialog = new AlertDialog.Builder(this, R.style.AlerDialogTheme)
                .setView(view)
                .setPositiveButton(R.string.text_ok, (dialog, which) -> {
                    String minCriticalStr = minimumViolationEditText.getText().toString();
                    int minCritical = minCriticalStr.length() > 0? Integer.parseInt(minCriticalStr) : -1;
                    String maxCriticalStr = maximumViolationEditText.getText().toString();
                    int maxCritical = maxCriticalStr.length() > 0? Integer.parseInt(maxCriticalStr): -1;
                    restaurantSearchFilter.setMinNumOfCritical(minCritical);
                    restaurantSearchFilter.setMaxNumOfCritical(maxCritical);
                    restaurantSearchFilter.updateFilter();
                    dialog.dismiss();
                    searchView.setQuery(restaurantSearchFilter.getFilterName(), false);
                    searchView.clearFocus();
                })
                .setNegativeButton(R.string.text_cancel, (dialog, which) -> dialog.dismiss())
                .create();
        filterDialog.show();
    }

    private void setUpSearchBar() {
        searchView = findViewById(R.id.search_bar_map_and_list_activity);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                restaurantSearchFilter.setFilterName(newText);
                restaurantSearchFilter.updateNameFilter();
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
