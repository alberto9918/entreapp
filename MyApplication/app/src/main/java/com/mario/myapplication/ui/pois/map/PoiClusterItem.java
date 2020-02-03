package com.mario.myapplication.ui.pois.map;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class PoiClusterItem implements ClusterItem {
    private String id;
    private LatLng position;
    private String title;
    private String snippet;

    public PoiClusterItem(double lat, double lng) {
        position = new LatLng(lat, lng);
    }

    public PoiClusterItem(String id, double lat, double lng, String title, String snippet) {
        this.id = id;
        this.position = new LatLng(lat, lng);
        this.title = title;
        this.snippet = snippet;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
}