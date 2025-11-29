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

public class TripDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_trip_details);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView image = findViewById(R.id.detailImage);
        TextView city = findViewById(R.id.detailCity);
        TextView country = findViewById(R.id.detailCountry);
        TextView dates = findViewById(R.id.detailDates);
        TextView restaurants = findViewById(R.id.detailRestaurants);
        TextView hotels = findViewById(R.id.detailHotels);
        TextView safety = findViewById(R.id.detailSafety);
        TextView famous = findViewById(R.id.detailFamous);

        image.setImageResource(getIntent().getIntExtra("imageResId", 0));
        city.setText(getIntent().getStringExtra("cityName"));
        country.setText(getIntent().getStringExtra("country"));

        String start = getIntent().getStringExtra("startDate");
        String end = getIntent().getStringExtra("endDate");
        dates.setText("Trip dates: " + start + " → " + end);

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
