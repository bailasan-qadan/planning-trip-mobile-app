package com.welcom_app.tripplanner;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Scanner;

public class SeeAllTrips extends AppCompatActivity {

    private static final String PREF = "trip_prefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_see_all_trips);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        RecyclerView recycler = findViewById(R.id.seeAllRecycler);

        String json = loadJson("trips.json");
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<Trip>>() {}.getType();
        ArrayList<Trip> trips = gson.fromJson(json, listType);

        for (Trip trip : trips) {
            int id = getResources().getIdentifier(trip.getImage(), "drawable", getPackageName());
            trip.setImageResId(id);
        }

        recycler.setLayoutManager(new GridLayoutManager(this, 2));

        TripAdapter adapter = new TripAdapter(
                this,
                trips,
                trip -> {
                    Intent intent = new Intent(SeeAllTrips.this, TripDetails.class);
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
        );

        recycler.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSharedPreferences(PREF, MODE_PRIVATE)
                .edit()
                .putString("last_screen", "SeeAllTrips")
                .apply();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
}
