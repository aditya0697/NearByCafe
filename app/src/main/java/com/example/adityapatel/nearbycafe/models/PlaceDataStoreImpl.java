package com.example.adityapatel.nearbycafe.models;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PlaceDataStoreImpl implements PlaceDataStore {

    private static PlaceDataStoreImpl sInstance;
    private  List<PlaceData> cafeList;

    synchronized public static PlaceDataStore sharedInstance() {
        if (sInstance == null) {
            sInstance = new PlaceDataStoreImpl();
        }
        return sInstance;
    }

    private PlaceDataStoreImpl() {
        cafeList = new ArrayList<>();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public List<PlaceData> getPlaces() {
        cafeList.sort(new Comparator<PlaceData>() {
            @Override
            public int compare(PlaceData o1, PlaceData o2) {
                if(o1.getRating() > o2.getRating()){return -1;}
                if(o1.getRating() < o2.getRating()){return 1;}
                return 0;
            }
        });
        return cafeList;
    }

    @Override
    public void addPlace(PlaceData newPlace) {
        cafeList.add(newPlace);

    }
}
