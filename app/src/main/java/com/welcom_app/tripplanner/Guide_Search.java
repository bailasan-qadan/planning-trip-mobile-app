package com.welcom_app.tripplanner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Guide_Search extends AppCompatActivity {

    private List<Trip> allCities = new ArrayList<>();
    private List<Trip> filteredCities = new ArrayList<>();
    private TripAdapter tripAdapter;

    private EditText search;

    private SharedPreferences prefs;

    private static final String TAG = "GuideSearchLifecycle";

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

        Log.d(TAG, "onCreate called");

        prefs = getSharedPreferences("trip_prefs", MODE_PRIVATE);

        RecyclerView recycler = findViewById(R.id.guideRecycler);
        search = findViewById(R.id.guideSearch);

        loadCitiesFromJSON();

        for (Trip trip : allCities) {
            int id = getResources().getIdentifier(trip.getImage(), "drawable", getPackageName());
            trip.setImageResId(id);
        }

        filteredCities.addAll(allCities);

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

        recycler.setLayoutManager(new GridLayoutManager(this, 2));
        recycler.setAdapter(tripAdapter);

        String lastSearch = prefs.getString("guide_last_search", "");
        search.setText(lastSearch);
        filterCities(lastSearch);

        search.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterCities(s.toString());
            }

            @Override public void afterTextChanged(Editable s) {}
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume called");

        // Save that the user opened this screen
        prefs.edit().putString("last_screen", "Guide_Search").apply();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause called");

        prefs.edit().putString("guide_last_search", search.getText().toString()).apply();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy called");
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
