package com.welcom_app.tripplanner;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;

public class planning extends AppCompatActivity {

    private static final String USER_TRIPS_FILE = "user_trips.json";
    private static final String PREF = "trip_prefs";

    private EditText etDestination, etStartDate, etEndDate;
    private Button btnStartTrip;
    private LinearLayout eventsContainer;
    private ImageButton btnAddEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_planning);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etDestination = findViewById(R.id.etDestination);
        etStartDate = findViewById(R.id.etStartDate);
        etEndDate = findViewById(R.id.etEndDate);
        btnStartTrip = findViewById(R.id.btnStartTrip);
        eventsContainer = findViewById(R.id.eventsContainer);
        btnAddEvent = findViewById(R.id.btnAddEvent);

        etStartDate.setOnClickListener(v -> showDatePicker(etStartDate));
        etEndDate.setOnClickListener(v -> showDatePicker(etEndDate));
        btnAddEvent.setOnClickListener(v -> addEventView(null));
        btnStartTrip.setOnClickListener(v -> saveTrip());
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
                .putString("last_screen", "planning")
                .apply();
    }

    @Override
    protected void onPause() {
        super.onPause();
        getSharedPreferences(PREF, MODE_PRIVATE)
                .edit()
                .putString("draft_destination", etDestination.getText().toString())
                .putString("draft_start_date", etStartDate.getText().toString())
                .putString("draft_end_date", etEndDate.getText().toString())
                .apply();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void showDatePicker(EditText target) {
        Calendar c = Calendar.getInstance();
        DatePickerDialog dp = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) ->
                        target.setText(String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year)),
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH)
        );
        dp.show();
    }

    private void addEventView(String text) {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        layout.setPadding(0, 8, 0, 8);

        EditText eventInput = new EditText(this);
        eventInput.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
        ));
        eventInput.setBackgroundResource(R.drawable.text);
        eventInput.setHint("Enter Event");
        if (text != null) eventInput.setText(text);

        ImageButton btnDelete = new ImageButton(this);
        btnDelete.setImageResource(R.drawable.delete);
        btnDelete.setBackground(null);
        btnDelete.setOnClickListener(v -> eventsContainer.removeView(layout));

        layout.addView(eventInput);
        layout.addView(btnDelete);
        eventsContainer.addView(layout);
    }

    private void saveTrip() {
        String destination = etDestination.getText().toString().trim();
        String startDate = etStartDate.getText().toString().trim();
        String endDate = etEndDate.getText().toString().trim();

        if (destination.isEmpty() || startDate.isEmpty() || endDate.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Trip newTrip = new Trip();
        newTrip.setCityName(destination);
        newTrip.setStartDate(startDate);
        newTrip.setEndDate(endDate);

        int defaultImage = getResources().getIdentifier("default_image", "drawable", getPackageName());
        newTrip.setImageResId(defaultImage);

        ArrayList<String> events = new ArrayList<>();
        for (int i = 0; i < eventsContainer.getChildCount(); i++) {
            LinearLayout layout = (LinearLayout) eventsContainer.getChildAt(i);
            EditText et = (EditText) layout.getChildAt(0);
            String text = et.getText().toString().trim();
            if (!text.isEmpty()) events.add(text);
        }
        newTrip.setFamous(events);

        ArrayList<Trip> savedTrips = loadTripsFromStorage();
        savedTrips.add(0, newTrip);
        saveTripsToStorage(savedTrips);

        Toast.makeText(this, "Trip saved!", Toast.LENGTH_SHORT).show();
        finish();
    }

    private ArrayList<Trip> loadTripsFromStorage() {
        try {
            FileInputStream fis = openFileInput(USER_TRIPS_FILE);
            InputStreamReader isr = new InputStreamReader(fis);
            ArrayList<Trip> trips = new Gson().fromJson(isr, new TypeToken<ArrayList<Trip>>() {}.getType());
            isr.close();
            if (trips != null) return trips;
        } catch (Exception ignored) {}
        return new ArrayList<>();
    }

    private void saveTripsToStorage(ArrayList<Trip> trips) {
        try {
            FileOutputStream fos = openFileOutput(USER_TRIPS_FILE, MODE_PRIVATE);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            new Gson().toJson(trips, osw);
            osw.close();
        } catch (Exception ignored) {}
    }
}
