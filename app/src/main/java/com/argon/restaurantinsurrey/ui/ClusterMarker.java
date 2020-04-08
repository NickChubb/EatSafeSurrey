package com.argon.restaurantinsurrey.ui;

import com.argon.restaurantinsurrey.model.ReportData;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.ClusterItem;

public class ClusterMarker implements ClusterItem {

    private LatLng position;
    private String title;
    private String snippet;
    private int iconPicture;
    private ReportData.HazardRating hazardRating;
    private int index;

    public ClusterMarker(LatLng position, String title, String snippet, int iconPicture,
                         ReportData.HazardRating hazardRating,
                         int index) {
        this.position = position;
        this.title = title;
        this.snippet = snippet;
        this.iconPicture = iconPicture;
        this.hazardRating = hazardRating;
        this.index = index;
    }

    public ReportData.HazardRating getHazardRating() {
        return hazardRating;
    }

    public void setHazardRating(ReportData.HazardRating hazardRating) {
        this.hazardRating = hazardRating;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
    public ClusterMarker() {
    }

    @Override
    public LatLng getPosition() {
        return position;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public int getIconPicture() {
        return iconPicture;
    }

    public void setIconPicture(int iconPicture) {
        this.iconPicture = iconPicture;
    }
}
