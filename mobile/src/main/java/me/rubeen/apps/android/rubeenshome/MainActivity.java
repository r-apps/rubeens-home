package me.rubeen.apps.android.rubeenshome;

import android.content.Context;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import me.rubeen.apps.android.rubeenshome.adapter.RVAdapter;
import me.rubeen.apps.android.rubeenshome.entities.JSONDisplayObject;
import me.rubeen.apps.android.rubeenshome.entities.LightEntity;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;

    private AppCompatActivity activity;

    private RelativeLayout settingsContainer;

    private CardView cardView;

    private RecyclerView recyclerView;

    private List<LightEntity> lightEntities;

    private NumberPicker portPicker;

    private Button connectionToServerTestButton;

    private Button downloadAllLightsFromServerButton;

    private RVAdapter rvAdapter;

    AutoCompleteTextView serverInput;
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

        serverInput = findViewById(R.id.input_search_server);
        progressBar = findViewById(R.id.search_urlprogressBar);
        portPicker = findViewById(R.id.portPicker);
        initPortPicker();

        connectionToServerTestButton = findViewById(R.id.connectionToServerTest);
        connectionToServerTestButton.setOnClickListener(v -> {
            String host = serverInput.getText().toString().trim();
            if (!host.startsWith("http://") || !host.startsWith("https://")) {
                host = "http://" + host;
            }
            host += ":" + portPicker.getValue();
            new PingNetworkDeviceTask().execute(host);
        });

        downloadAllLightsFromServerButton = findViewById(R.id.downloadAllLightsFromServerBtn);
        downloadAllLightsFromServerButton.setOnClickListener(v -> {
            String host = serverInput.getText().toString().trim();
            if (!host.startsWith("http://") || !host.startsWith("https://")) {
                host = "http://" + host;
            }
            host += ":" + portPicker.getValue();
            new DownloadAllLightsFromServerTask().execute(host);
        });

        //new FindNetworkUrlsTask().execute("192.168.178");

        initLightEntityData();
        createRecyclerView(recyclerView);
    }

    private void initPortPicker() {
        portPicker.setMinValue(1);
        portPicker.setMaxValue(99999);
        portPicker.setValue(8080);
    }

    private void createRecyclerView(final RecyclerView recyclerView) {
        Context context = recyclerView.getContext();
        LinearLayoutManager manager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(manager);
        rvAdapter = new RVAdapter(lightEntities, this);
        recyclerView.setAdapter(rvAdapter);

        RecyclerView.ItemDecoration itemDecoration = new VerticalSpaceItemDecoration(30);
        recyclerView.addItemDecoration(itemDecoration);
    }

    private void initLightEntityData() {
        String img = "https://static1.squarespace.com/static/56c775ad27d4bd3fdb24775d/t/5a8b201324a694d7071662ee/1519067160925/dummy+logo.jpg";
        lightEntities = new ArrayList<>();
        lightEntities.addAll(Arrays.asList(
                new LightEntity("Lichterkette - Kommode", img, 0, true, ""),
                new LightEntity("Lichterkette - Kommode", img, 0, true, ""),
                new LightEntity("Lichterkette - Kommode", img, 0, true, ""),
                new LightEntity("Lichterkette - Kommode", img, 0, true, ""),
                new LightEntity("Lichterkette - Kommode", img, 0, true, ""),
                new LightEntity("Lichterkette - Kommode", img, 0, true, ""),
                new LightEntity("Lichterkette - Kommode", img, 0, true, ""),
                new LightEntity("Lichterkette - Kommode", img, 0, true, ""),
                new LightEntity("Lichterkette - Kommode", img, 0, true, ""),
                new LightEntity("Lichterkette - Kommode", img, 0, true, ""),
                new LightEntity("Lichterkette - Kommode", img, 0, true, ""),
                new LightEntity("Lichterkette - Kommode", img, 0, true, ""),
                new LightEntity("LED Strip - Spiegel", img, 1, true, "")));
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
            serverInput.setAdapter(arrayAdapter);
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

    private class PingNetworkDeviceTask extends AsyncTask<String, Integer, Integer> {

        @Override
        protected void onPostExecute(Integer status) {
            if (status == 200) {
                Toast.makeText(activity, "SUCCESS", Toast.LENGTH_LONG).show();
            } else if (status == -1) {
                Toast.makeText(activity, "URL is malformed", Toast.LENGTH_SHORT).show();
            } else if (status == -2) {
                Toast.makeText(activity, "Connection failure", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(activity, "FAIL: " + status, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected Integer doInBackground(String... hosts) {
            for (String host : hosts) {
                try {
                    URL url = new URL(host + "/ping");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(1000);
                    connection.setReadTimeout(1000);
                    connection.connect();
                    int status = connection.getResponseCode();
                    connection.disconnect();
                    return status;
                } catch (MalformedURLException e) {
                    Log.e("URI", "malformed url.", e);
                    return -1;
                } catch (IOException e) {
                    Log.e("NETWORK", "Network-failure", e);
                    return -2;
                }
            }
            return null;
        }
    }

    private class DownloadAllLightsFromServerTask extends AsyncTask<String, Integer, List<JSONDisplayObject>> {
        @Override
        protected void onPostExecute(List<JSONDisplayObject> jsonDisplayObjects) {
            Toast.makeText(activity, "Downloaded all files", Toast.LENGTH_SHORT).show();
            lightEntities.clear();
            lightEntities.addAll(jsonDisplayObjects.parallelStream()
                    .map(jsonDisplayObject
                            -> new LightEntity(jsonDisplayObject.getName(),
                            jsonDisplayObject.getImageSrc(), jsonDisplayObject.getId(),
                            jsonDisplayObject.isAutomaticActivated(),
                            jsonDisplayObject.getHost()))
                    .collect(Collectors.toList()));
            rvAdapter.notifyDataSetChanged();
        }

        @Override
        protected List<JSONDisplayObject> doInBackground(String... hosts) {
            List<JSONDisplayObject> result = new LinkedList<>();
            for (String host : hosts)
                try {
                    String url = host + "/get/all";
                    String responseMessage = getText(url);

                    JSONArray jsonArray = new JSONArray(responseMessage);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        HashMap<String, String> map = new HashMap<>();
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        JSONDisplayObject displayObject = new JSONDisplayObject(
                                jsonObject.getInt("id"),
                                jsonObject.getString("imageSrc"),
                                jsonObject.getString("name"),
                                jsonObject.getBoolean("autoEnabled"),
                                host);
                        result.add(displayObject);
                    }
                } catch (MalformedURLException e) {
                    Log.e("URI", "malformed url.", e);
                } catch (IOException e) {
                    Log.e("NETWORK", "Network-failure", e);
                } catch (JSONException e) {
                    Log.e("JSON", "unable to parse json", e);
                }
            return result;
        }

        String getText(String url) throws IOException {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            //add headers to the connection, or check the status if desired..

            // handle error response code it occurs
            int responseCode = connection.getResponseCode();
            InputStream inputStream;
            if (200 <= responseCode && responseCode <= 299) {
                inputStream = connection.getInputStream();
            } else {
                inputStream = connection.getErrorStream();
            }

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            inputStream));

            StringBuilder response = new StringBuilder();
            String currentLine;

            while ((currentLine = in.readLine()) != null)
                response.append(currentLine);

            in.close();

            return response.toString();
        }
    }

    private class VerticalSpaceItemDecoration extends RecyclerView.ItemDecoration {
        private final int verticalSpaceHeight;

        private VerticalSpaceItemDecoration(int verticalSpaceHeight) {
            this.verticalSpaceHeight = verticalSpaceHeight;
        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            if (parent.getChildAdapterPosition(view) != parent.getAdapter().getItemCount() - 1)
                outRect.bottom = verticalSpaceHeight;
        }
    }

}
