package com.welcom_app.tripplanner;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.Gson;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class userTripDetails extends AppCompatActivity {

    private ImageView imageView;
    private TextView cityText, countryText, userDates, userNotes,
            restaurantsText, hotelsText, safetyText, famousText;

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

        Button btn = findViewById(R.id.ETBtn);
        btn.setOnClickListener(v -> {
            Intent intent = new Intent(userTripDetails.this, EditTrip.class);
            intent.putExtra("userTripJson", userTripJson);
            startActivityForResult(intent, 101);
        });

        Button deleteBtn = findViewById(R.id.deleteBtn);
        deleteBtn.setOnClickListener(v -> {
            new androidx.appcompat.app.AlertDialog.Builder(userTripDetails.this)
                    .setTitle("Delete Trip")
                    .setMessage("Are you sure you want to delete this trip?")
                    .setPositiveButton("Yes", (dialog, which) -> deleteTrip())
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    private void loadOriginalTrip() {
        List<Trip> allTrips = loadTripsFromJson();
        originalTrip = null;
        for (Trip t : allTrips) {
            if (t.getCityName().equalsIgnoreCase(userTrip.getCityName().trim())) {
                originalTrip = t;
                originalTrip.setImageResId(getResources().getIdentifier(t.getImage(), "drawable", getPackageName()));
                break;
            }
        }
    }

    private void updateUI() {
        if (userTrip == null) return;

        // Always show user-entered info
        cityText.setText(userTrip.getCityName());
        countryText.setText(userTrip.getCityName()); // optional
        userDates.setText(userTrip.getStartDate() + " → " + userTrip.getEndDate());
        userNotes.setText(userTrip.getNotes() == null || userTrip.getNotes().isEmpty()
                ? "No events" : userTrip.getNotes());

        // Show static guide info if available
        if (originalTrip != null) {
            imageView.setImageResource(originalTrip.getImageResId());
            restaurantsText.setText("Restaurants:\n" + android.text.TextUtils.join(", ", originalTrip.getRestaurants()));
            hotelsText.setText("Hotels:\n" + android.text.TextUtils.join(", ", originalTrip.getHotels()));
            famousText.setText("Famous Places:\n" + android.text.TextUtils.join(", ", originalTrip.getFamous()));
            safetyText.setText("Safety:\n" + originalTrip.getSafety());
        }
    }

    private List<Trip> loadTripsFromJson() {
        try {
            InputStream is = getAssets().open("trips.json");
            Scanner scanner = new Scanner(is).useDelimiter("\\A");
            String json = scanner.hasNext() ? scanner.next() : "";
            Trip[] tripsArray = new Gson().fromJson(json, Trip[].class);
            return java.util.Arrays.asList(tripsArray);
        } catch (Exception e) {
            e.printStackTrace();
            return java.util.Collections.emptyList();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK && data != null) {
            String editedTripJson = data.getStringExtra("editedTripJson");
            if (editedTripJson != null) {
                userTripJson = editedTripJson;
                userTrip = new Gson().fromJson(userTripJson, userTrip.class);
                updateUI();
            }
        }
    }

    private void deleteTrip() {
        Intent result = new Intent();
        result.putExtra("deletedTripCity", userTrip.getCityName());
        setResult(RESULT_FIRST_USER, result);
        finish();
    }
}