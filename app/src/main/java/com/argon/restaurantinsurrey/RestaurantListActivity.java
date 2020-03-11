package com.argon.restaurantinsurrey;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.argon.restaurantinsurrey.model.DataFactory;
import com.argon.restaurantinsurrey.model.DataManager;
import com.argon.restaurantinsurrey.ui.RestaurantRecyclerAdapter;

/*
 *   This is the activity for showing all restaurants
 *
 */
public class RestaurantListActivity extends AppCompatActivity {

    final public static String TAG = "RestaurantListActivity";
    private RecyclerView.LayoutManager layoutManager;
    private RestaurantRecyclerAdapter adapter;


    public static Intent makeLaunchIntent(Context c) {
        Intent intent = new Intent(c, RestaurantListActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_list);
        Toolbar toolbar = findViewById(R.id.singleRestaurantToolbar);
        setSupportActionBar(toolbar);

        DataFactory.getDataFromInternet = true;
        DataManager.createInstance(this);
        populateListView();


    }

    private void populateListView() {

        RecyclerView list = findViewById(R.id.recRestaurants);
        list.setHasFixedSize(true);
        list.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        layoutManager = new LinearLayoutManager(this);
        list.setLayoutManager(layoutManager);

        adapter = new RestaurantRecyclerAdapter(this);
        list.setAdapter(adapter);

        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText){
                adapter.getFilter().filter(newText);
                return false;
            }
        });
    }
}
