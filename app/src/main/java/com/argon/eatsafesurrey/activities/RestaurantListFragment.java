package com.argon.eatsafesurrey.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.argon.eatsafesurrey.R;
import com.argon.eatsafesurrey.model.RestaurantData;
import com.argon.eatsafesurrey.ui.RestaurantRecyclerAdapter;

import java.util.List;

/*
Show the list of restaurants

 */
public class RestaurantListFragment extends Fragment {

    final public static String TAG = "RestaurantListFragment";
    private RestaurantRecyclerAdapter adapter;
    private boolean isActivityPause = false;

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
