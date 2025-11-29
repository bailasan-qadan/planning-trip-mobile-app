package com.welcom_app.tripplanner;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.TripViewHolder> {

    private ArrayList<Trip> trips;
    private Context context;
    private OnTripClickListener listener;

    public interface OnTripClickListener {
        void onTripClick(Trip trip);
    }

    public TripAdapter(Context context, ArrayList<Trip> trips, OnTripClickListener listener) {
        this.context = context;
        this.trips = trips;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.trip_card, parent, false);
        return new TripViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TripViewHolder holder, int position) {
        Trip trip = trips.get(position);

        holder.cityName.setText(trip.getCityName());
        holder.image.setImageResource(trip.getImageResId());

        holder.itemView.setOnClickListener(v -> listener.onTripClick(trip));
    }

    @Override
    public int getItemCount() {
        return trips.size();
    }
    public void updateList(List<Trip> newList) {
        trips.clear();
        trips.addAll(newList);
        notifyDataSetChanged();
    }
    class TripViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView cityName;

        public TripViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.tripImage);
            cityName = itemView.findViewById(R.id.tripDestination);
        }
    }
}
