package com.example.adityapatel.nearbycafe.models;

import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

public class PlaceData {
    private String name;
    private String address;
    private String id;

    private double rating;
    private LatLng latLng;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }





    public double getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public PlaceData(String name, String address, String id,double rating, LatLng latLng) {
        this.name = name;
        this.address = address;
        this.id = id;
        this.rating = rating;
        this.latLng = latLng;
    }

    @Override
    public String toString() {
        return "PlaceDetail{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", id='" + id + '\'' +
                ", rating=" + rating +
                ", latLng=" + latLng +
                '}';
    }
}
