package com.welcom_app.tripplanner;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class GuideAdapter extends RecyclerView.Adapter<GuideAdapter.ViewHolder> {

    public interface OnCityClick {
        void onClick(Trip trip);
    }

    private List<Trip> list;
    private OnCityClick listener;

    public GuideAdapter(List<Trip> list, OnCityClick listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.trip_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Trip t = list.get(position);

        holder.cityName.setText(t.getCityName());
        holder.country.setText(t.getCountry());
        holder.image.setImageResource(t.getImageResId());

        holder.itemView.setOnClickListener(v -> listener.onClick(t));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public void updateList(List<Trip> newList) {
        list.clear();
        list.addAll(newList);
        notifyDataSetChanged();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView cityName, country;
        ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cityName = itemView.findViewById(R.id.tripDestination);
            country = itemView.findViewById(R.id.detailCountry);
            image = itemView.findViewById(R.id.cardImage);
        }
    }
}
