package me.rubeen.apps.android.rubeenshome;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;

import java.io.IOException;
import java.net.InetAddress;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends Activity {
    Activity activity;
    AutoCompleteTextView textView;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        activity = this;
        textView = findViewById(R.id.input_search_server);
        progressBar = findViewById(R.id.search_urlprogressBar);
        new FindNetworkUrlsTask().execute("192.168.178");
    }

    @Override
    public boolean onCreateThumbnail(Bitmap outBitmap, Canvas canvas) {
        return super.onCreateThumbnail(outBitmap, canvas);
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
