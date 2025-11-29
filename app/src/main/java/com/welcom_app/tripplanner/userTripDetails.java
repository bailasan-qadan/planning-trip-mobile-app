package com.welcom_app.tripplanner;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.Gson;

import java.io.InputStream;
import java.util.List;
import java.util.Scanner;

public class userTripDetails extends AppCompatActivity {

    private ImageView imageView;
    private TextView cityText, countryText, userDates, userNotes, restaurantsText, hotelsText, safetyText, famousText;

    private String userTripJson;
    private Trip originalTrip;
    private userTrip userTrip;

    private static final String KEY_USER_JSON = "userTripJson";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_trip_details);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        imageView = findViewById(R.id.detailImage2);
        cityText = findViewById(R.id.detailCity2);
        countryText = findViewById(R.id.detailCountry2);
        userDates = findViewById(R.id.detailDates2);
        userNotes = findViewById(R.id.detailEvents2);
        restaurantsText = findViewById(R.id.detailRestaurants2);
        hotelsText = findViewById(R.id.detailHotels2);
        safetyText = findViewById(R.id.detailSafety2);
        famousText = findViewById(R.id.detailFamous2);

        if (savedInstanceState != null) {
            userTripJson = savedInstanceState.getString(KEY_USER_JSON);
        } else {
            userTripJson = getIntent().getStringExtra("userTripJson");
        }

        if (userTripJson == null) {
            finish();
            return;
        }

        userTrip = new Gson().fromJson(userTripJson, userTrip.class);
        loadOriginalTrip();
        updateUI();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
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
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_USER_JSON, userTripJson);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        userTripJson = savedInstanceState.getString(KEY_USER_JSON);
        if (userTripJson != null) {
            userTrip = new Gson().fromJson(userTripJson, userTrip.class);
            loadOriginalTrip();
            updateUI();
        }
    }

    private void loadOriginalTrip() {
        List<Trip> allTrips = loadTripsFromJson();
        originalTrip = null;
        for (Trip t : allTrips) {
            if (t.getCityName().equalsIgnoreCase(userTrip.getCityName())) {
                originalTrip = t;
                int id = getResources().getIdentifier(t.getImage(), "drawable", getPackageName());
                t.setImageResId(id);
                break;
            }
        }
        if (originalTrip == null) {
            finish();
        }
    }

    private void updateUI() {
        if (originalTrip == null || userTrip == null) return;

        imageView.setImageResource(originalTrip.getImageResId());
        cityText.setText(originalTrip.getCityName());
        countryText.setText(originalTrip.getCountry());
        userDates.setText(userTrip.getStartDate() + " → " + userTrip.getEndDate());
        userNotes.setText(userTrip.getNotes());
        restaurantsText.setText("Restaurants:\n" + android.text.TextUtils.join(", ", originalTrip.getRestaurants()));
        hotelsText.setText("Hotels:\n" + android.text.TextUtils.join(", ", originalTrip.getHotels()));
        famousText.setText("Famous Places:\n" + android.text.TextUtils.join(", ", originalTrip.getFamous()));
        safetyText.setText("Safety:\n" + originalTrip.getSafety());
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

    private List<Trip> loadTripsFromJson() {
        try {
            String json = loadJson("trips.json");
            Trip[] tripsArray = new Gson().fromJson(json, Trip[].class);
            return java.util.Arrays.asList(tripsArray);
        } catch (Exception e) {
            e.printStackTrace();
            return java.util.Collections.emptyList();
        }
    }
}
