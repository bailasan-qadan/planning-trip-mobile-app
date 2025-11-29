package com.welcom_app.tripplanner;

import android.content.Intent;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Scanner;

public class Main extends AppCompatActivity {

    private RecyclerView userTripsRecycler, tripsRecycler;
    private ArrayList<Trip> defaultTrips, userTrips;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        userTripsRecycler = findViewById(R.id.userTripsRecycler);
        tripsRecycler = findViewById(R.id.tripsRecyclerView);

        // Load default trips from assets
        String json = loadJson("trips.json");
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<Trip>>() {}.getType();
        defaultTrips = gson.fromJson(json, listType);

        for (Trip trip : defaultTrips) {
            int id = getResources().getIdentifier(trip.getImage(), "drawable", getPackageName());
            trip.setImageResId(id);
        }

        // Setup default trips RecyclerView
        TripAdapter exploreAdapter = new TripAdapter(this, defaultTrips, this::openTripDetails);
        tripsRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        tripsRecycler.setAdapter(exploreAdapter);

        // Buttons
        findViewById(R.id.exploreButton).setOnClickListener(v ->
                startActivity(new Intent(Main.this, Guide_Search.class))
        );
        findViewById(R.id.startPlanBtn).setOnClickListener(v ->
                startActivity(new Intent(Main.this, planning.class))
        );
        findViewById(R.id.seeAll).setOnClickListener(v ->
                startActivity(new Intent(Main.this, SeeAllTrips.class))
        );

    }

    @Override
    protected void onResume() {
        super.onResume();

        // Load user trips
        userTrips = loadTripsFromInternal();

        // Build display list for user trips
        ArrayList<Trip> userTripsDisplay = new ArrayList<>();
        for (Trip userTrip : userTrips) {
            Trip match = null;
            for (Trip defaultTrip : defaultTrips) {
                if (defaultTrip.getCityName().equalsIgnoreCase(userTrip.getCityName())) {
                    match = defaultTrip; // image + country
                    break;
                }
            }
            if (match != null) userTripsDisplay.add(match);
            else {
                userTrip.setImageResId(0);
                userTripsDisplay.add(userTrip);
            }
        }

        // Use openUserTripDetails()
        TripAdapter userAdapter = new TripAdapter(this, userTripsDisplay, this::openUserTripDetails);
        userTripsRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        userTripsRecycler.setAdapter(userAdapter);
    }


    private void openTripDetails(Trip trip) {
        Intent intent = new Intent(Main.this, TripDetails.class);
        intent.putExtra("cityName", trip.getCityName());
        intent.putExtra("country", trip.getCountry());
        intent.putExtra("imageResId", trip.getImageResId());
        intent.putExtra("startDate", trip.getStartDate());
        intent.putExtra("endDate", trip.getEndDate());
        intent.putStringArrayListExtra("restaurants", new ArrayList<>(trip.getRestaurants()));
        intent.putStringArrayListExtra("hotels", new ArrayList<>(trip.getHotels()));
        intent.putExtra("safety", trip.getSafety());
        intent.putStringArrayListExtra("famous", new ArrayList<>(trip.getFamous()));
        startActivity(intent);
    }

    private String loadJson(String filename) {
        try {
            InputStream is = getAssets().open(filename);
            Scanner scanner = new Scanner(is).useDelimiter("\\A");
            return scanner.hasNext() ? scanner.next() : "";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private ArrayList<Trip> loadTripsFromInternal() {
        ArrayList<Trip> savedTrips = new ArrayList<>();
        try {
            FileInputStream fis = openFileInput("user_trips.json");
            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<Trip>>() {}.getType();
            savedTrips = gson.fromJson(new InputStreamReader(fis), listType);
            fis.close();
        } catch (Exception e) {
            // File may not exist yet
        }
        return savedTrips != null ? savedTrips : new ArrayList<>();
    }
    private void openUserTripDetails(Trip trip) {
        Intent intent = new Intent(Main.this, userTripDetails.class);
        intent.putExtra("userTripJson", new Gson().toJson(trip)); // <-- key must match
        startActivity(intent);
    }



}
