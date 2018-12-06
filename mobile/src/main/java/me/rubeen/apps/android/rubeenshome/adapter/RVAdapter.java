package me.rubeen.apps.android.rubeenshome.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import me.rubeen.apps.android.rubeenshome.R;
import me.rubeen.apps.android.rubeenshome.entities.LightEntity;

import static java.text.MessageFormat.format;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.LightEntityViewHolder> {

    final List<LightEntity> lightEntities;
    private Activity activity;

    public RVAdapter(final List<LightEntity> lightEntities, Activity activity) {
        this.lightEntities = lightEntities;
        this.activity = activity;
    }

    @NonNull
    @Override
    public LightEntityViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card, viewGroup, false);
        return new LightEntityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final LightEntityViewHolder lightEntityViewHolder, int i) {
        LightEntity lightEntity = lightEntities.get(i);

        lightEntityViewHolder.textView.setText(lightEntity.getName());
        Glide.with(activity).load(lightEntity.getImageSrc()).into(lightEntityViewHolder.imageView);
        lightEntityViewHolder.automatic.setOnClickListener(v -> {
            Toast.makeText(v.getContext(), "Automatic of " + lightEntity.getId() + " was pressed", Toast.LENGTH_SHORT).show();
            v.setActivated(!v.isActivated());
            new SetLightToAutomaticTask().execute(lightEntity);
        });
        lightEntityViewHolder.settings.setOnClickListener(v -> {
            //new SetColorToLightTask().execute(lightEntity);
            /*if (lightEntityViewHolder.imageView.getVisibility() == View.VISIBLE) {
                lightEntityViewHolder.imageView.setVisibility(View.GONE);
                lightEntityViewHolder.settingsLayout.setVisibility(View.VISIBLE);
            } else {
                lightEntityViewHolder.imageView.setVisibility(View.VISIBLE);
                lightEntityViewHolder.settingsLayout.setVisibility(View.GONE);
            }*/
            showColorDialog(activity, lightEntity);
        });
        lightEntityViewHolder.off.setOnClickListener(v -> {
            new SetLightOffTask().execute(lightEntity);
        });
        lightEntityViewHolder.chooseColorButton.setOnClickListener(v -> {
            showColorDialog(activity, lightEntity);
        });
    }

    private void showColorDialog(Activity activity, LightEntity lightEntity) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.color_dialog);

        ImageView headline = dialog.findViewById(R.id.colorDialog_imageView);

        TextView text = dialog.findViewById(R.id.colorDialog_text);
        text.setText(format("Change the color for {0}", lightEntity.getName()));

        Button dialogButton = dialog.findViewById(R.id.btn_dialog);

        SeekBar redBar = dialog.findViewById(R.id.colorDialog_redSlider);
        SeekBar greenBar = dialog.findViewById(R.id.colorDialog_greenSlider);
        SeekBar blueBar = dialog.findViewById(R.id.colorDialog_blueSlider);
        redBar.getProgressDrawable().setColorFilter(new PorterDuffColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY));
        greenBar.getProgressDrawable().setColorFilter(new PorterDuffColorFilter(Color.GREEN, PorterDuff.Mode.MULTIPLY));
        blueBar.getProgressDrawable().setColorFilter(new PorterDuffColorFilter(Color.BLUE, PorterDuff.Mode.MULTIPLY));

        SeekBar.OnSeekBarChangeListener changedListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                headline.setBackgroundColor(getIntFromColor(redBar.getProgress(), greenBar.getProgress(), blueBar.getProgress()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            private int getIntFromColor(int Red, int Green, int Blue) {
                Red = (Red << 16) & 0x00FF0000; //Shift red 16-bits and mask out other stuff
                Green = (Green << 8) & 0x0000FF00; //Shift Green 8-bits and mask out other stuff
                Blue = Blue & 0x000000FF; //Mask out anything not blue.

                return 0xFF000000 | Red | Green | Blue; //0xFF000000 for 100% Alpha. Bitwise OR everything together.
            }

        };

        redBar.setOnSeekBarChangeListener(changedListener);
        greenBar.setOnSeekBarChangeListener(changedListener);
        blueBar.setOnSeekBarChangeListener(changedListener);


        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                lightEntity.setColor(redBar.getProgress(), greenBar.getProgress(), blueBar.getProgress());
                new SetColorToLightTask().execute(lightEntity);
            }
        });

        dialog.show();

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
        RelativeLayout settingsLayout;
        Button chooseColorButton;

        public LightEntityViewHolder(View view) {
            super(view);

            cardView = view.findViewById(R.id.cardView);
            textView = view.findViewById(R.id.info_text);
            imageView = view.findViewById(R.id.imageView);
            automatic = view.findViewById(R.id.button_turnOn);
            settings = view.findViewById(R.id.button_settings);
            off = view.findViewById(R.id.button_off);
            settingsLayout = view.findViewById(R.id.settingsLayout);
            chooseColorButton = view.findViewById(R.id.chooseColorButton);
        }
    }

    private static class SetLightToAutomaticTask extends AsyncTask<LightEntity, Integer, Integer> {

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

    private static class SetLightOffTask extends AsyncTask<LightEntity, Integer, Integer> {

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

    private static class SetColorToLightTask extends AsyncTask<LightEntity, Integer, Integer> {
        @Override
        protected Integer doInBackground(LightEntity... lightEntities) {
            Integer status = null;
            for (LightEntity lightEntity : lightEntities) {
                try {
                    status = sendRequest(lightEntity.getServer() + "/set/manual/" + lightEntity.getId(), "red", lightEntity.getRed());
                    status += sendRequest(lightEntity.getServer() + "/set/manual/" + lightEntity.getId(), "green", lightEntity.getGreen());
                    status += sendRequest(lightEntity.getServer() + "/set/manual/" + lightEntity.getId(), "blue", lightEntity.getBlue());
                } catch (IOException e) {
                    Log.e("Network-Failure", "Unable to reach server " + lightEntity.getServer(), e);
                }
            }
            return status;
        }

        private Integer sendRequest(String url, String color, int brightness) throws IOException {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestProperty("color", color);
            connection.setRequestProperty("brightness", String.valueOf(brightness));
            return connection.getResponseCode();
        }
    }
}
