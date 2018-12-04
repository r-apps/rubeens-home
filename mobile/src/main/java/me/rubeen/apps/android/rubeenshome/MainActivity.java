package me.rubeen.apps.android.rubeenshome;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.rubeen.apps.android.rubeenshome.adapter.RVAdapter;
import me.rubeen.apps.android.rubeenshome.entities.LightEntity;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;

    private AppCompatActivity activity;

    private CardView cardView;

    private RecyclerView recyclerView;

    private List<LightEntity> lightEntities;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    return true;
                case R.id.navigation_dashboard:
                    return true;
                case R.id.navigation_notifications:
                    startActivity(new Intent(activity, SettingsActivity.class));
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        setContentView(R.layout.activity_main);

        cardView = findViewById(R.id.cardView);
        recyclerView = findViewById(R.id.recView);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        initLightEntityData();
        createRecyclerView(recyclerView);
    }

    private void createRecyclerView(final RecyclerView recyclerView) {
        Context context = recyclerView.getContext();
        LinearLayoutManager manager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(manager);
        RVAdapter rvAdapter = new RVAdapter(lightEntities);
        recyclerView.setAdapter(rvAdapter);
    }

    private void initLightEntityData() {
        lightEntities = new ArrayList<>();
        lightEntities.addAll(Arrays.asList(
                new LightEntity("Lichterkette - Kommode", R.drawable.lichterkette_demo, 0),
                new LightEntity("LED Strip - Spiegel", R.drawable.led_strip_demo, 1)));
    }

}
