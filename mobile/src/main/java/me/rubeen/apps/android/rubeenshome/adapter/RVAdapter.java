package me.rubeen.apps.android.rubeenshome.adapter;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import me.rubeen.apps.android.rubeenshome.R;
import me.rubeen.apps.android.rubeenshome.entities.LightEntity;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.LightEntityViewHolder> {

    final List<LightEntity> lightEntities;
    Context context;

    public RVAdapter(final List<LightEntity> lightEntities, Context context) {
        this.lightEntities = lightEntities;
        this.context = context;
    }

    @NonNull
    @Override
    public LightEntityViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card, viewGroup, false);
        return new LightEntityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final LightEntityViewHolder lightEntityViewHolder, int i) {
        lightEntityViewHolder.textView.setText(lightEntities.get(i).getName());
        Glide.with(context).load(lightEntities.get(i).getImageSrc()).into(lightEntityViewHolder.imageView);
        lightEntityViewHolder.automatic.setOnClickListener(v -> {
            Toast.makeText(v.getContext(), "Automatic of " + lightEntities.get(i).getId() + " was pressed", Toast.LENGTH_SHORT).show();
            v.setActivated(!v.isActivated());
            new SetLightToAutomaticTask().execute(lightEntities.get(i));
        });
        lightEntityViewHolder.settings.setOnClickListener(v -> Toast.makeText(v.getContext(), "Settings of " + lightEntities.get(i).getId() + " was pressed", Toast.LENGTH_SHORT).show());
        lightEntityViewHolder.off.setOnClickListener(v -> {
            new SetLightOffTask().execute(lightEntities.get(i));
        });
    }

    @Override
    public int getItemCount() {
        return lightEntities.size();
    }

    public static class LightEntityViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView textView;
        ImageView imageView;
        ImageButton settings;
        ImageButton automatic;
        ImageButton off;

        public LightEntityViewHolder(View view) {
            super(view);

            cardView = view.findViewById(R.id.cardView);
            textView = view.findViewById(R.id.info_text);
            imageView = view.findViewById(R.id.imageView);
            automatic = view.findViewById(R.id.button_turnOn);
            settings = view.findViewById(R.id.button_settings);
            off = view.findViewById(R.id.button_off);
        }
    }

    private class SetLightToAutomaticTask extends AsyncTask<LightEntity, Integer, Integer> {

        @Override
        protected Integer doInBackground(LightEntity... lightEntities) {
            Integer status = null;
            for (LightEntity lightEntity : lightEntities) {
                try {
                    status = sendRequest(lightEntity.getServer() + "/set/auto/" + lightEntity.getId());
                } catch (IOException e) {
                    Log.e("Setting automatic", "Unable to reach server " + lightEntity.getServer(), e);
                }
            }
            return status;
        }

        int sendRequest(String url) throws IOException {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            return connection.getResponseCode();
        }
    }

    private class SetLightOffTask extends AsyncTask<LightEntity, Integer, Integer> {

        @Override
        protected Integer doInBackground(LightEntity... lightEntities) {
            Integer status = null;
            for (LightEntity lightEntity : lightEntities) {
                try {
                    status = sendRequest(lightEntity.getServer() + "/set/off/" + lightEntity.getId());
                } catch (IOException e) {
                    Log.e("Setting automatic", "Unable to reach server " + lightEntity.getServer(), e);
                }
            }
            return status;
        }

        int sendRequest(String url) throws IOException {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            return connection.getResponseCode();
        }
    }
}
