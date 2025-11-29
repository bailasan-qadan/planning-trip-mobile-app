package com.welcom_app.tripplanner;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Guide_Search extends AppCompatActivity {
    private List<Trip> allCities = new ArrayList<>();
    private List<Trip> filteredCities = new ArrayList<>();
    private TripAdapter tripAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_guide_search);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        RecyclerView recycler = findViewById(R.id.guideRecycler);
        EditText search = findViewById(R.id.guideSearch);

        // Load all trips from JSON
        loadCitiesFromJSON();

        // Convert image names to drawable IDs (like SeeAllTrips)
        for (Trip trip : allCities) {
            int id = getResources().getIdentifier(trip.getImage(), "drawable", getPackageName());
            trip.setImageResId(id);
        }

        // Initialize filtered list
        filteredCities.addAll(allCities);

        // Setup adapter with click listener
        tripAdapter = new TripAdapter(this, new ArrayList<>(filteredCities), trip -> {
            Intent intent = new Intent(Guide_Search.this, TripDetailsGuide.class);
            intent.putExtra("cityName", trip.getCityName());
            intent.putExtra("country", trip.getCountry());
            intent.putExtra("imageResId", trip.getImageResId());
            intent.putStringArrayListExtra("restaurants", new ArrayList<>(trip.getRestaurants()));
            intent.putStringArrayListExtra("hotels", new ArrayList<>(trip.getHotels()));
            intent.putExtra("safety", trip.getSafety());
            intent.putStringArrayListExtra("famous", new ArrayList<>(trip.getFamous()));
            startActivity(intent);
        });

        // Use GridLayoutManager like SeeAllTrips for the same card layout
        recycler.setLayoutManager(new GridLayoutManager(this, 2));
        recycler.setAdapter(tripAdapter);

        // Search filter
        search.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterCities(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void loadCitiesFromJSON() {
        try {
            InputStream is = getAssets().open("trips.json");
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();
            String json = new String(buffer, StandardCharsets.UTF_8);

            allCities = new Gson().fromJson(json, new TypeToken<List<Trip>>(){}.getType());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void filterCities(String query) {
        query = query.toLowerCase().trim();
        filteredCities.clear();

        for (Trip t : allCities) {
            if (t.getCityName().toLowerCase().startsWith(query)) {
                filteredCities.add(t);
            }
        }

        tripAdapter.updateList(filteredCities);
    }
}