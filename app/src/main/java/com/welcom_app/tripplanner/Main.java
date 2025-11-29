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
    private ArrayList<Trip> defaultTrips;
    private ArrayList<userTrip> userTrips;
    private SharedPreferences prefs;

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

        ArrayList<Trip> limitedTrips = new ArrayList<>();
        int maxDisplay = Math.min(defaultTrips.size(), 5);
        for (int i = 0; i < maxDisplay; i++) {
            limitedTrips.add(defaultTrips.get(i));
        }

        TripAdapter exploreAdapter = new TripAdapter(this, limitedTrips, this::openTripDetails);
        tripsRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        tripsRecycler.setAdapter(exploreAdapter);

        findViewById(R.id.exploreButton).setOnClickListener(v ->
                startActivity(new Intent(Main.this, Guide_Search.class))
        );

        findViewById(R.id.startPlanBtn).setOnClickListener(v ->
                startActivity(new Intent(Main.this, planning.class))
        );

        findViewById(R.id.seeAll).setOnClickListener(v -> {
            Intent intent = new Intent(Main.this, SeeAllTrips.class);
            intent.putExtra("allTripsJson", new Gson().toJson(defaultTrips));
            startActivity(intent);
        });

        Button logoutBtn = findViewById(R.id.logoutBtn);
        logoutBtn.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isLoggedIn", false);
            editor.apply();

            deleteFile("user_trips.json");

            Intent intent = new Intent(Main.this, Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        prefs.edit().putString("last_screen", "Main").apply();

        userTrips = loadTripsFromInternal();
        ArrayList<Trip> userTripsDisplay = new ArrayList<>();

        for (userTrip uTrip : userTrips) {
            Trip match = null;
            for (Trip defaultTrip : defaultTrips) {
                if (defaultTrip.getCityName().equalsIgnoreCase(uTrip.getCityName())) {
                    match = defaultTrip;
                    break;
                }
            }

            Trip merged = new Trip();
            merged.setCityName(uTrip.getCityName());
            merged.setStartDate(uTrip.getStartDate());
            merged.setEndDate(uTrip.getEndDate());

            if (match != null) {
                merged.setCountry(match.getCountry());
                merged.setImageResId(getResources().getIdentifier(match.getImage(), "drawable", getPackageName()));
                merged.setRestaurants(match.getRestaurants());
                merged.setHotels(match.getHotels());
                merged.setFamous(match.getFamous());
                merged.setSafety(match.getSafety());
            } else {
                merged.setImageResId(0);
            }

            userTripsDisplay.add(merged);
        }

        TripAdapter userAdapter = new TripAdapter(this, userTripsDisplay, trip -> {
            userTrip uTrip = findUserTripByCity(trip.getCityName());
            if (uTrip != null) {
                Intent intent = new Intent(Main.this, userTripDetails.class);
                intent.putExtra("userTripJson", new Gson().toJson(uTrip));
                startActivityForResult(intent, 102);
            }
        });
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

    private userTrip findUserTripByCity(String cityName) {
        for (userTrip u : userTrips) {
            if (u.getCityName().equalsIgnoreCase(cityName)) return u;
        }
        return null;
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

    private ArrayList<userTrip> loadTripsFromInternal() {
        ArrayList<userTrip> savedTrips = new ArrayList<>();
        try {
            FileInputStream fis = openFileInput("user_trips.json");
            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<userTrip>>() {}.getType();
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
                ArrayList<userTrip> trips = loadTripsFromInternal();
                userTrip toRemove = null;
                for (userTrip t : trips) {
                    if (t.getCityName().equalsIgnoreCase(cityToDelete)) {
                        toRemove = t;
                        break;
                    }
                }
                if (toRemove != null) {
                    trips.remove(toRemove);
                    saveTripsToInternal(trips);
                }

                userTrips = trips;
                ArrayList<Trip> userTripsDisplay = new ArrayList<>();
                for (userTrip uTrip : userTrips) {
                    Trip match = null;
                    for (Trip defaultTrip : defaultTrips) {
                        if (defaultTrip.getCityName().equalsIgnoreCase(uTrip.getCityName())) {
                            match = defaultTrip;
                            break;
                        }
                    }

                    Trip merged = new Trip();
                    merged.setCityName(uTrip.getCityName());
                    merged.setStartDate(uTrip.getStartDate());
                    merged.setEndDate(uTrip.getEndDate());

                    if (match != null) {
                        merged.setCountry(match.getCountry());
                        merged.setImageResId(getResources().getIdentifier(match.getImage(), "drawable", getPackageName()));
                        merged.setRestaurants(match.getRestaurants());
                        merged.setHotels(match.getHotels());
                        merged.setFamous(match.getFamous());
                        merged.setSafety(match.getSafety());
                    } else {
                        merged.setImageResId(0);
                    }

                    userTripsDisplay.add(merged);
                }
                TripAdapter userAdapter = new TripAdapter(this, userTripsDisplay, trip -> {
                    userTrip uTrip = findUserTripByCity(trip.getCityName());
                    if (uTrip != null) {
                        Intent intent = new Intent(Main.this, userTripDetails.class);
                        intent.putExtra("userTripJson", new Gson().toJson(uTrip));
                        startActivityForResult(intent, 102);
                    }
                });
                userTripsRecycler.setAdapter(userAdapter);
            }
        }
    }

    private void saveTripsToInternal(ArrayList<userTrip> trips) {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(trips);
            openFileOutput("user_trips.json", MODE_PRIVATE).write(json.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
