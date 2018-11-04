package com.example.adityapatel.nearbycafe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.adityapatel.nearbycafe.models.PlaceDataStore;
import com.example.adityapatel.nearbycafe.models.PlaceDataStoreImpl;

public class RecyclerViewAcitivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    public static final String MyPreference = "MyPref";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recyclerview);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Log.d(TAG, "onCreate: hooooooooooooooooooooo");
        //ListView listView1 = findViewById(R.id.list1);

        initRecyclerView();

    }

    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    private void initRecyclerView(){
        Log.d(TAG, "initRecyclerView: init recyclerview 12222");
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }
}



