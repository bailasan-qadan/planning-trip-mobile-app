package com.welcom_app.tripplanner;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

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

public class EditTrip extends AppCompatActivity {

    EditText editDestination, editStartDate, editEndDate;
    Button btnSave;
    ImageButton btnAddEvent;
    userTrip userTripData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_trip);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        editDestination = findViewById(R.id.etDestination);
        editStartDate = findViewById(R.id.etStartDate);
        editEndDate = findViewById(R.id.etEndDate);
        btnSave = findViewById(R.id.btnSaveTrip);
        btnAddEvent = findViewById(R.id.btnAddEvent);

        String userTripJson = getIntent().getStringExtra("userTripJson");
        if (userTripJson != null) {
            userTripData = new Gson().fromJson(userTripJson, userTrip.class);
            populateFields();
        }

        editStartDate.setOnClickListener(v -> showDatePicker(editStartDate));
        editEndDate.setOnClickListener(v -> showDatePicker(editEndDate));

        btnAddEvent.setOnClickListener(v -> addEventView(null));

        btnSave.setOnClickListener(v -> saveTrip());
    }

    private void populateFields() {
        editDestination.setText(userTripData.getCityName());
        editStartDate.setText(userTripData.getStartDate());
        editEndDate.setText(userTripData.getEndDate());

        if (userTripData.getNotes() != null && !userTripData.getNotes().isEmpty()) {
            String[] events = userTripData.getNotes().split("\n");
            for (String e : events) {
                addEventView(e);
            }
        }
    }

    private void showDatePicker(EditText editText) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog picker = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    String date = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year);
                    editText.setText(date);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        picker.show();
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
        btnDelete.setOnClickListener(v -> ((LinearLayout) findViewById(R.id.eventsContainer)).removeView(layout));

        layout.addView(eventInput);
        layout.addView(btnDelete);
        ((LinearLayout) findViewById(R.id.eventsContainer)).addView(layout);
    }

    private String collectEvents() {
        StringBuilder notes = new StringBuilder();
        LinearLayout container = findViewById(R.id.eventsContainer);
        for (int i = 0; i < container.getChildCount(); i++) {
            LinearLayout layout = (LinearLayout) container.getChildAt(i);
            EditText et = (EditText) layout.getChildAt(0);
            String text = et.getText().toString().trim();
            if (!text.isEmpty()) {
                notes.append(text).append("\n");
            }
        }
        return notes.toString().trim();
    }

    private void saveTrip() {
        userTripData.setCityName(editDestination.getText().toString());
        userTripData.setStartDate(editStartDate.getText().toString());
        userTripData.setEndDate(editEndDate.getText().toString());
        userTripData.setNotes(collectEvents());

        // ✅ Update storage so Main sees changes
        ArrayList<userTrip> trips = loadUserTrips();
        boolean replaced = false;
        for (int i = 0; i < trips.size(); i++) {
            if (trips.get(i).getCityName().equalsIgnoreCase(userTripData.getCityName())) {
                trips.set(i, userTripData);
                replaced = true;
                break;
            }
        }
        if (!replaced) {
            trips.add(userTripData);
        }
        saveUserTrips(trips);

        Intent result = new Intent();
        result.putExtra("editedTripJson", new Gson().toJson(userTripData));
        setResult(RESULT_OK, result);
        finish();
    }

    private ArrayList<userTrip> loadUserTrips() {
        try {
            FileInputStream fis = openFileInput("user_trips.json");
            InputStreamReader isr = new InputStreamReader(fis);
            Type listType = new TypeToken<ArrayList<userTrip>>(){}.getType();
            ArrayList<userTrip> list = new Gson().fromJson(isr, listType);
            isr.close();
            return list != null ? list : new ArrayList<>();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private void saveUserTrips(ArrayList<userTrip> list) {
        try {
            FileOutputStream fos = openFileOutput("user_trips.json", MODE_PRIVATE);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            new Gson().toJson(list, osw);
            osw.close();
        } catch (Exception ignored) {}
    }
}
