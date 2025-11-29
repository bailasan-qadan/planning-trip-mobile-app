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

import java.util.ArrayList;

public class TripDetailsGuide extends AppCompatActivity {

    private ImageView image;
    private TextView city, country, restaurants, hotels, safety, famous;

    private static final String KEY_CITY = "city";
    private static final String KEY_COUNTRY = "country";
    private static final String KEY_RESTAURANTS = "restaurants";
    private static final String KEY_HOTELS = "hotels";
    private static final String KEY_FAMOUS = "famous";
    private static final String KEY_SAFETY = "safety";
    private static final String KEY_IMAGE = "imageResId";

    private String cityStr, countryStr, safetyStr;
    private ArrayList<String> restaurantList, hotelList, famousList;
    private int imageResId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_trip_details_guide);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        image = findViewById(R.id.detailImage1);
        city = findViewById(R.id.detailCity1);
        country = findViewById(R.id.detailCountry1);
        restaurants = findViewById(R.id.detailRestaurants1);
        hotels = findViewById(R.id.detailHotels1);
        safety = findViewById(R.id.detailSafety1);
        famous = findViewById(R.id.detailFamous1);

        if (savedInstanceState != null) {
            restoreState(savedInstanceState);
        } else {
            loadIntentData();
        }

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
        outState.putString(KEY_CITY, cityStr);
        outState.putString(KEY_COUNTRY, countryStr);
        outState.putInt(KEY_IMAGE, imageResId);
        outState.putStringArrayList(KEY_RESTAURANTS, restaurantList);
        outState.putStringArrayList(KEY_HOTELS, hotelList);
        outState.putStringArrayList(KEY_FAMOUS, famousList);
        outState.putString(KEY_SAFETY, safetyStr);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        restoreState(savedInstanceState);
        updateUI();
    }

    private void loadIntentData() {
        imageResId = getIntent().getIntExtra("imageResId", 0);
        cityStr = getIntent().getStringExtra("cityName");
        countryStr = getIntent().getStringExtra("country");
        restaurantList = getIntent().getStringArrayListExtra("restaurants");
        hotelList = getIntent().getStringArrayListExtra("hotels");
        famousList = getIntent().getStringArrayListExtra("famous");
        safetyStr = "Safety:\n" + getIntent().getStringExtra("safety");
    }

    private void restoreState(Bundle state) {
        imageResId = state.getInt(KEY_IMAGE, 0);
        cityStr = state.getString(KEY_CITY);
        countryStr = state.getString(KEY_COUNTRY);
        restaurantList = state.getStringArrayList(KEY_RESTAURANTS);
        hotelList = state.getStringArrayList(KEY_HOTELS);
        famousList = state.getStringArrayList(KEY_FAMOUS);
        safetyStr = state.getString(KEY_SAFETY);
    }

    private void updateUI() {
        image.setImageResource(imageResId);
        city.setText(cityStr);
        country.setText(countryStr);
        if (restaurantList != null)
            restaurants.setText("Restaurants:\n" + android.text.TextUtils.join(", ", restaurantList));
        if (hotelList != null)
            hotels.setText("Hotels:\n" + android.text.TextUtils.join(", ", hotelList));
        if (famousList != null)
            famous.setText("Famous Places:\n" + android.text.TextUtils.join(", ", famousList));
        safety.setText(safetyStr);
    }
}
