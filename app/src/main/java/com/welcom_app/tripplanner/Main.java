package com.welcom_app.tripplanner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

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
    private SharedPreferences prefs;
    private static final String TAG = "MainLifecycle";

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

        prefs = getSharedPreferences("trip_prefs", MODE_PRIVATE);

        userTripsRecycler = findViewById(R.id.userTripsRecycler);
        tripsRecycler = findViewById(R.id.tripsRecyclerView);

        String json = loadJson("trips.json");
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<Trip>>() {}.getType();
        defaultTrips = gson.fromJson(json, listType);

        for (Trip trip : defaultTrips) {
            int id = getResources().getIdentifier(trip.getImage(), "drawable", getPackageName());
            trip.setImageResId(id);
        }

        TripAdapter exploreAdapter = new TripAdapter(this, defaultTrips, this::openTripDetails);
        tripsRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        tripsRecycler.setAdapter(exploreAdapter);

        findViewById(R.id.exploreButton).setOnClickListener(v ->
                startActivity(new Intent(Main.this, Guide_Search.class))
        );

        findViewById(R.id.startPlanBtn).setOnClickListener(v ->
                startActivity(new Intent(Main.this, planning.class))
        );

        findViewById(R.id.seeAll).setOnClickListener(v ->
                startActivity(new Intent(Main.this, SeeAllTrips.class))
        );
        Button logoutBtn = findViewById(R.id.logoutBtn);
        logoutBtn.setOnClickListener(v -> {
            // Clear login state
            SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isLoggedIn", false);
            editor.apply();

            // Clear previous user's trips
            deleteFile("user_trips.json");

            // Go back to Login page
            Intent intent = new Intent(Main.this, Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });



    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        prefs.edit().putString("last_screen", "Main").apply();

        userTrips = loadTripsFromInternal();
        ArrayList<Trip> userTripsDisplay = new ArrayList<>();

        for (Trip userTrip : userTrips) {
            Trip match = null;
            for (Trip defaultTrip : defaultTrips) {
                if (defaultTrip.getCityName().equalsIgnoreCase(userTrip.getCityName())) {
                    match = defaultTrip;
                    break;
                }
            }
            if (match != null) userTripsDisplay.add(match);
            else {
                userTrip.setImageResId(0);
                userTripsDisplay.add(userTrip);
            }
        }

        TripAdapter userAdapter = new TripAdapter(this, userTripsDisplay, this::openUserTripDetails);
        userTripsRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        userTripsRecycler.setAdapter(userAdapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        prefs.edit().putBoolean("main_active", true).apply();
    }

    @Override
    protected void onStop() {
        super.onStop();
        prefs.edit().putBoolean("main_visible", false).apply();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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

    private void openUserTripDetails(Trip trip) {
        Intent intent = new Intent(Main.this, userTripDetails.class);
        intent.putExtra("userTripJson", new Gson().toJson(trip));
        startActivityForResult(intent, 102);
    }


    private String loadJson(String filename) {
        try {
            InputStream is = getAssets().open(filename);
            Scanner scanner = new Scanner(is).useDelimiter("\\A");
            return scanner.hasNext() ? scanner.next() : "";
        } catch (Exception e) {
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
        } catch (Exception ignored) {}
        return savedTrips != null ? savedTrips : new ArrayList<>();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 102 && resultCode == RESULT_FIRST_USER && data != null) {
            String cityToDelete = data.getStringExtra("deletedTripCity");
            if (cityToDelete != null) {
                // Remove from internal storage list
                ArrayList<Trip> trips = loadTripsFromInternal();
                Trip toRemove = null;
                for (Trip t : trips) {
                    if (t.getCityName().equalsIgnoreCase(cityToDelete)) {
                        toRemove = t;
                        break;
                    }
                }
                if (toRemove != null) {
                    trips.remove(toRemove);
                    saveTripsToInternal(trips);
                }

                // Refresh RecyclerView
                userTrips = trips;
                ArrayList<Trip> userTripsDisplay = new ArrayList<>();
                for (Trip userTrip : userTrips) {
                    Trip match = null;
                    for (Trip defaultTrip : defaultTrips) {
                        if (defaultTrip.getCityName().equalsIgnoreCase(userTrip.getCityName())) {
                            match = defaultTrip;
                            break;
                        }
                    }
                    if (match != null) userTripsDisplay.add(match);
                    else {
                        userTrip.setImageResId(0);
                        userTripsDisplay.add(userTrip);
                    }
                }
                TripAdapter userAdapter = new TripAdapter(this, userTripsDisplay, this::openUserTripDetails);
                userTripsRecycler.setAdapter(userAdapter);
            }
        }
    }
    private void saveTripsToInternal(ArrayList<Trip> trips) {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(trips);
            openFileOutput("user_trips.json", MODE_PRIVATE).write(json.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
