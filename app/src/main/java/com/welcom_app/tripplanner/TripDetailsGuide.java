package com.welcom_app.tripplanner;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class TripDetailsGuide extends AppCompatActivity {

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

        ImageView image = findViewById(R.id.detailImage1);
        TextView city = findViewById(R.id.detailCity1);
        TextView country = findViewById(R.id.detailCountry1);
        TextView restaurants = findViewById(R.id.detailRestaurants1);
        TextView hotels = findViewById(R.id.detailHotels1);
        TextView safety = findViewById(R.id.detailSafety1);
        TextView famous = findViewById(R.id.detailFamous1);

        // Get simple values
        image.setImageResource(getIntent().getIntExtra("imageResId", 0));
        city.setText(getIntent().getStringExtra("cityName"));
        country.setText(getIntent().getStringExtra("country"));

        String start = getIntent().getStringExtra("startDate");
        String end = getIntent().getStringExtra("endDate");

        ArrayList<String> restaurantList = getIntent().getStringArrayListExtra("restaurants");
        ArrayList<String> hotelList = getIntent().getStringArrayListExtra("hotels");
        ArrayList<String> famousList = getIntent().getStringArrayListExtra("famous");

        if (restaurantList != null)
            restaurants.setText("Restaurants:\n" + android.text.TextUtils.join(", ", restaurantList));

        if (hotelList != null)
            hotels.setText("Hotels:\n" + android.text.TextUtils.join(", ", hotelList));

        if (famousList != null)
            famous.setText("Famous Places:\n" + android.text.TextUtils.join(", ", famousList));

        safety.setText("Safety:\n" + getIntent().getStringExtra("safety"));
    }
}
