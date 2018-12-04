package me.rubeen.apps.android.rubeenshome;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;
import java.net.InetAddress;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.rubeen.apps.android.rubeenshome.adapter.RVAdapter;
import me.rubeen.apps.android.rubeenshome.entities.LightEntity;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;

    private AppCompatActivity activity;

    private RelativeLayout settingsContainer;

    private CardView cardView;

    private RecyclerView recyclerView;

    private List<LightEntity> lightEntities;

    AutoCompleteTextView textView;
    ProgressBar progressBar;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    recyclerView.setVisibility(View.VISIBLE);
                    settingsContainer.setVisibility(View.GONE);
                    return true;
                case R.id.navigation_dashboard:
                    return true;
                case R.id.navigation_notifications:
                    settingsContainer.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
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

        settingsContainer = findViewById(R.id.settings_container);
        cardView = findViewById(R.id.cardView);
        recyclerView = findViewById(R.id.recView);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        textView = findViewById(R.id.input_search_server);
        progressBar = findViewById(R.id.search_urlprogressBar);

        new FindNetworkUrlsTask().execute("192.168.178");

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
                new LightEntity("Lichterkette - Kommode", R.drawable.lichterkette_demo, 0),
                new LightEntity("Lichterkette - Kommode", R.drawable.lichterkette_demo, 0),
                new LightEntity("Lichterkette - Kommode", R.drawable.lichterkette_demo, 0),
                new LightEntity("Lichterkette - Kommode", R.drawable.lichterkette_demo, 0),
                new LightEntity("Lichterkette - Kommode", R.drawable.lichterkette_demo, 0),
                new LightEntity("Lichterkette - Kommode", R.drawable.lichterkette_demo, 0),
                new LightEntity("Lichterkette - Kommode", R.drawable.lichterkette_demo, 0),
                new LightEntity("Lichterkette - Kommode", R.drawable.lichterkette_demo, 0),
                new LightEntity("Lichterkette - Kommode", R.drawable.lichterkette_demo, 0),
                new LightEntity("Lichterkette - Kommode", R.drawable.lichterkette_demo, 0),
                new LightEntity("Lichterkette - Kommode", R.drawable.lichterkette_demo, 0),
                new LightEntity("LED Strip - Spiegel", R.drawable.led_strip_demo, 1)));
    }

    private class FindNetworkUrlsTask extends AsyncTask<String, Integer, List<String>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<String> doInBackground(String... strings) {
            List<String> result = new ArrayList<>();
            progressBar.setMax(strings.length * 255);
            for (String string : strings) {
                findAllUrlsInNetwork(string, result);
            }
            return result;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            int value = 0;
            for (Integer integer : values) {
                value += integer;
            }
            progressBar.setProgress(value);
        }

        @Override
        protected void onPostExecute(List<String> strings) {
            super.onPostExecute(strings);
            progressBar.setVisibility(View.GONE);
            ArrayAdapter<String> arrayAdapter =
                    new ArrayAdapter<>(activity, android.R.layout.simple_dropdown_item_1line, strings);
            textView.setAdapter(arrayAdapter);
        }

        private List<String> findAllUrlsInNetwork(String namespace, List<String> resultList) {
            for (int i = 0; i < 255; i++) {
                String ip = MessageFormat.format("{0}.{1}", namespace, i);
                try {
                    InetAddress inetAddress = InetAddress.getByName(ip);
                    System.out.println("checking " + ip + " now");
                    if (inetAddress.isReachable(30)) {
                        System.out.println("adding " + ip);
                        resultList.add(inetAddress.getHostName());
                    }
                } catch (IOException e) {
                    System.err.println("Can't get host: " + ip);
                }
                publishProgress(i);
            }
            return resultList;
        }
    }

}
