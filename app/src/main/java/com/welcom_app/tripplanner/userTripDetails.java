package com.welcom_app.tripplanner;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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

    ImageView imageView;
    TextView cityText, countryText, userDates, userNotes, restaurantsText, hotelsText, safetyText, famousText;

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

        String userTripJson = getIntent().getStringExtra("userTripJson");
        if (userTripJson == null) {
            finish();
            return;
        }
        userTrip userTrip = new Gson().fromJson(userTripJson, userTrip.class);

        // Load all Trips from JSON
        List<Trip> allTrips = loadTripsFromJson();
        Trip originalTrip = null;
        for (Trip t : allTrips) {
            if (t.getCityName().equalsIgnoreCase(userTrip.getCityName())) {
                originalTrip = t;
                // Set image resource here
                int id = getResources().getIdentifier(t.getImage(), "drawable", getPackageName());
                t.setImageResId(id);
                break;
            }
        }


        if (originalTrip == null) {
            finish();
            return;
        }

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

    private String formatList(List<String> list) {
        StringBuilder sb = new StringBuilder();
        for (String s : list) sb.append("• ").append(s).append("\n");
        return sb.toString();
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
            Gson gson = new Gson();
            Trip[] tripsArray = gson.fromJson(json, Trip[].class);
            return java.util.Arrays.asList(tripsArray);
        } catch (Exception e) {
            e.printStackTrace();
            return java.util.Collections.emptyList();
        }
    }

}