package com.argon.restaurantinsurrey.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.argon.restaurantinsurrey.R;
import com.argon.restaurantinsurrey.model.ReportData;
import com.argon.restaurantinsurrey.model.RestaurantData;
import com.argon.restaurantinsurrey.ui.RestaurantRecyclerAdapter;

import java.util.List;
import java.util.Map;

/*
Show the list of restaurants

 */
public class RestaurantListFragment extends Fragment {

    final public static String TAG = "RestaurantListFragment";
    private RestaurantRecyclerAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_restaurant_list,container,false);

        RecyclerView list = view.findViewById(R.id.list_restaurant_list);
        list.setHasFixedSize(true);
        list.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        list.setLayoutManager(layoutManager);

        adapter = new RestaurantRecyclerAdapter(getActivity());
        list.setAdapter(adapter);

        return view;
    }


    public void setFilteredRestaurant(List<RestaurantData> restaurantData){
        adapter.setFilteredRecyclerView(restaurantData);
    }


}
