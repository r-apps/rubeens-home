package me.rubeen.apps.android.rubeenshome.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import me.rubeen.apps.android.rubeenshome.R;
import me.rubeen.apps.android.rubeenshome.entities.LightEntity;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.LightEntityViewHolder> {

    final List<LightEntity> lightEntities;

    public RVAdapter(final List<LightEntity> lightEntities) {
        this.lightEntities = lightEntities;
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
        lightEntityViewHolder.imageView.setImageResource(lightEntities.get(i).getPhotoId());
        lightEntityViewHolder.automatic.setOnClickListener(v -> Toast.makeText(v.getContext(), "Automatic of " + lightEntities.get(i).getId() + " was pressed", Toast.LENGTH_SHORT).show());
        lightEntityViewHolder.settings.setOnClickListener(v -> Toast.makeText(v.getContext(), "Settings of " + lightEntities.get(i).getId() + " was pressed", Toast.LENGTH_SHORT).show());
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

        public LightEntityViewHolder(View view) {
            super(view);

            cardView = view.findViewById(R.id.cardView);
            textView = view.findViewById(R.id.info_text);
            imageView = view.findViewById(R.id.imageView);
            automatic = view.findViewById(R.id.button_turnOn);
            settings = view.findViewById(R.id.button_settings);
        }
    }
}
