package com.example.adityapatel.nearbycafe;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.adityapatel.nearbycafe.models.PlaceData;
import com.example.adityapatel.nearbycafe.models.PlaceDataStore;
import com.example.adityapatel.nearbycafe.models.PlaceDataStoreImpl;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.example.adityapatel.nearbycafe.AppConfig.ADDRESS;
import static com.example.adityapatel.nearbycafe.AppConfig.CAFE_ID;
import static com.example.adityapatel.nearbycafe.AppConfig.GEOMETRY;
import static com.example.adityapatel.nearbycafe.AppConfig.GOOGLE_BROWSER_API_KEY;
import static com.example.adityapatel.nearbycafe.AppConfig.ICON;
import static com.example.adityapatel.nearbycafe.AppConfig.LATITUDE;
import static com.example.adityapatel.nearbycafe.AppConfig.LOCATION;
import static com.example.adityapatel.nearbycafe.AppConfig.LONGITUDE;
import static com.example.adityapatel.nearbycafe.AppConfig.NAME;
import static com.example.adityapatel.nearbycafe.AppConfig.OK;
import static com.example.adityapatel.nearbycafe.AppConfig.PLACE_ID;
import static com.example.adityapatel.nearbycafe.AppConfig.PROXIMITY_RADIUS;
import static com.example.adityapatel.nearbycafe.AppConfig.RATING;
import static com.example.adityapatel.nearbycafe.AppConfig.REFERENCE;
import static com.example.adityapatel.nearbycafe.AppConfig.STATUS;
import static com.example.adityapatel.nearbycafe.AppConfig.VICINITY;
import static com.example.adityapatel.nearbycafe.AppConfig.ZERO_RESULTS;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "Device Location";
    private boolean mLocationPermissionGranted = false;
    private static final int LOCTION_PERMISSION_REQUEST_CODE = 123;
    private static final float DEFAUL_ZOOM = 15f;
    private static final float SHOWING_CAFES_ZOOM = 14f;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    //cafelist
    public PlaceDataStore cafeStore;

    private AutoCompleteTextView searchText;
    private ImageView mgps;
    private ImageView snippest;
    private ImageView cafe_button;
    private ImageView listButton;

    private PlaceAutocompleteAdapter placeAutocompleteAdapter;
    private GoogleApiClient mGoogleApiClient;
    private PlaceData mPlace;
    private Marker mMarker;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(37.398160, -122.180831),
            new LatLng(37.430610, -121.972090));
    private LatLng currentlatlng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cafeStore = PlaceDataStoreImpl.sharedInstance();
        searchText = (AutoCompleteTextView) findViewById(R.id.input_search);
        snippest = (ImageView)findViewById(R.id.ic_snippest);
        mgps = (ImageView)findViewById(R.id.ic_gps);
        cafe_button = (ImageView)findViewById(R.id.ic_cafe);
        listButton = (ImageView)findViewById(R.id.ic_list);
        getLocationPermossion();
    }


    private void init(){
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();
        searchText.setOnItemClickListener(mAutocompleteClickListener);
        placeAutocompleteAdapter = new PlaceAutocompleteAdapter(this, mGoogleApiClient,
                LAT_LNG_BOUNDS,null);

        searchText.setAdapter(placeAutocompleteAdapter);
        searchText.setImeActionLabel("search",KeyEvent.KEYCODE_ENTER);
        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if(actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == KeyEvent.ACTION_DOWN
                        || event.getKeyCode() == KeyEvent.KEYCODE_ENTER){
                    locationFind();

                    return true;

                }
                return false;
            }
        });

        mgps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: gps button clicked");
                getDeviceLocation();
            }
        });

        snippest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(mMarker.isInfoWindowShown()){
                        mMarker.hideInfoWindow();
                    }else {
                        mMarker.showInfoWindow();
                    }

                }catch (NullPointerException e){
                    Log.d(TAG, "onClick: NULLPOINTEREXECUTION "+e.getMessage());
                }
            }
        });

        cafe_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loadNearByPlaces(currentlatlng.latitude,currentlatlng.longitude);
                listButton.setVisibility(View.VISIBLE);
            }
        });

        listButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,RecyclerViewAcitivity.class);
                startActivity(i);
            }
        });

    }

    private void locationFind(){
        hideKeyboard();
        String searchStr = searchText.getText().toString();
        Geocoder geocoder = new Geocoder(MainActivity.this);
        List<Address> addresses = new ArrayList<Address>();
        try {
            addresses = geocoder.getFromLocationName(searchStr,1);
        }catch (IOException e){
            Log.d(TAG, "locationFind: Exception"+ e.getMessage());
        }
        if(addresses.size() > 0){
            Address address = addresses.get(0);
            Log.d(TAG, "locationFind: FOUND loction: "+addresses.toString());
            LatLng tempLatLng = new LatLng(address.getLatitude(),address.getLongitude());
            currentlatlng = tempLatLng;
            moveCameraZoom(currentlatlng, DEFAUL_ZOOM,mPlace);
        }
    }

    private AdapterView.OnItemClickListener mAutocompleteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            hideKeyboard();

            final AutocompletePrediction item = placeAutocompleteAdapter.getItem(position);
            final String placeId = item.getPlaceId();

            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient,placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);


        }
    };
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {
            if(!places.getStatus().isSuccess()){
                Log.d(TAG, "onResult: Place query did not complete successfully");
                places.release();
                return;
            }
            final Place place = places.get(0);
            try {
                mPlace = new PlaceData(place.getName().toString(),place.getAddress().toString(),
                        place.getId().toString(),place.getRating(),place.getLatLng() );

            }catch (NullPointerException e){
                Log.d(TAG, "onResult: NullPointerException "+e.getMessage());
            }
            currentlatlng = mPlace.getLatLng();
            moveCameraZoom(currentlatlng,DEFAUL_ZOOM,mPlace);
            places.release();

        }
    };


    private void getLocationPermossion() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};


        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCTION_PERMISSION_REQUEST_CODE);
            }

        } else {
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCTION_PERMISSION_REQUEST_CODE);
        }
    }


    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MainActivity.this);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case LOCTION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionGranted = false;
                            return;
                        }
                    }
                    mLocationPermissionGranted = true;
                    //initialize map
                }
            }
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (mLocationPermissionGranted) {
            getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);

            init();
        }
    }

    private void getDeviceLocation(){
        Log.d(TAG, "getDeviceLocation: getting the device current location");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try{
            if(mLocationPermissionGranted){
                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "onComplete: found location");
                            Location currentLocation = (Location ) task.getResult();
                            currentlatlng = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
                            moveCameraZoom(currentlatlng,DEFAUL_ZOOM,null);

                        }else {
                            Log.d(TAG, "onComplete: Not found location");
                            Toast.makeText(MainActivity.this,"unable to get current location",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }catch (SecurityException e){
            Log.d(TAG, "getDeviceLocation: Exception "+e.getMessage());
        }
    }

    private void moveCameraZoom(LatLng latLng,float zoom, PlaceData placeData){
        Log.d(TAG, "moveCameraZoom: Latlng  "+latLng.toString());
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));

       // mMap.clear();
        if(placeData != null){
            try {
                String snippest =
                        "Address: "+placeData.getAddress() +"\n" +
                        
                        "Phone Number: "+placeData.getName() +"\n";
                MarkerOptions options = new MarkerOptions().position(latLng)
                        .title(placeData.getName())
                        .snippet(snippest);
                mMarker = mMap.addMarker(options);

            }catch (NullPointerException e){
                Log.d(TAG, "moveCameraZoom: NULLPOINTEREXECUTION "+e.getMessage());
            }
        }else {
            MarkerOptions options = new MarkerOptions().position(latLng)
                    .title("Your Location");
            mMap.addMarker(options);
        }
        hideKeyboard();
    }

    private void hideKeyboard(){
        InputMethodManager imm = (InputMethodManager) MainActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = MainActivity.this.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(MainActivity.this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    

    private void loadNearByPlaces(double latitude, double longitude) {
//YOU Can change this type at your own will, e.g hospital, cafe, restaurant.... and see how it all works

        String type = "cafe";
        StringBuilder cafesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        cafesUrl.append("location=").append(latitude).append(",").append(longitude);
        cafesUrl.append("&radius=").append(PROXIMITY_RADIUS);
        cafesUrl.append("&types=").append(type);
        cafesUrl.append("&sensor=true");
        cafesUrl.append("&key=" + GOOGLE_BROWSER_API_KEY);

        JsonObjectRequest request = new JsonObjectRequest(cafesUrl.toString(),

                new Response.Listener<JSONObject>() {
                    @Override

                    public void onResponse(JSONObject result) {

                        Log.i(TAG, "onResponse: Result= " + result.toString());
                        parseLocationResult(result);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "onErrorResponse: Error= " + error);
                        Log.e(TAG, "onErrorResponse: Error= " + error.getMessage());
                    }
                });

        AppController.getInstance().addToRequestQueue(request);
    }

    private void parseLocationResult(JSONObject result) {

        String id, place_id,placeAddress = "", placeName = null, reference, icon, vicinity = null;
        double placeRating = 0;
        double latitude, longitude;

        try {
            JSONArray jsonArray = result.getJSONArray("results");

            if (result.getString(STATUS).equalsIgnoreCase(OK)) {

                mMap.clear();

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject place = jsonArray.getJSONObject(i);

                    id = place.getString(CAFE_ID);
                    place_id = place.getString(PLACE_ID);
                    if (!place.isNull(NAME)) {
                        placeName = place.getString(NAME);
                    }
                    
                    if(!place.isNull(RATING)){
                        placeRating = place.getDouble(RATING);
                    }

                    if (!place.isNull(VICINITY)) {
                        placeAddress = place.getString(VICINITY);
                    }
                    latitude = place.getJSONObject(GEOMETRY).getJSONObject(LOCATION)

                            .getDouble(LATITUDE);
                    longitude = place.getJSONObject(GEOMETRY).getJSONObject(LOCATION)

                            .getDouble(LONGITUDE);
                    reference = place.getString(REFERENCE);
                    icon = place.getString(ICON);
                    LatLng latLng = new LatLng(latitude, longitude);
                  PlaceData places = new PlaceData(placeName,placeAddress,place_id,placeRating,latLng);
                  cafeStore.addPlace(places);
                  MarkerOptions markerOptions = new MarkerOptions();

                    markerOptions.position(latLng);
                    markerOptions.title(placeName + " : " + vicinity);
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));

                    mMap.addMarker(markerOptions);
                    moveCameraZoom(currentlatlng,SHOWING_CAFES_ZOOM,null);
                }

                Toast.makeText(getBaseContext(), jsonArray.length() + " Cafes found!",

                        Toast.LENGTH_LONG).show();
            } else if (result.getString(STATUS).equalsIgnoreCase(ZERO_RESULTS)) {
                Toast.makeText(getBaseContext(), "No Cafes found in 2KM radius!!!",

                        Toast.LENGTH_LONG).show();
            }

        } catch (JSONException e) {

            e.printStackTrace();
            Log.e(TAG, "parseLocationResult: Error=" + e.getMessage());
        }
    }

}
