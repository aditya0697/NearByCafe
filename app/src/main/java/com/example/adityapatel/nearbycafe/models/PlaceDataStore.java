package com.example.adityapatel.nearbycafe.models;

import java.util.List;

public interface PlaceDataStore {
    List<PlaceData> getPlaces();
    void addPlace(PlaceData newPlace);
}
