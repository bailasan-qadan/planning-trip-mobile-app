package com.welcom_app.tripplanner;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.Gson;

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

        // Bind views
        editDestination = findViewById(R.id.etDestination);
        editStartDate = findViewById(R.id.etStartDate);
        editEndDate = findViewById(R.id.etEndDate);
        btnSave = findViewById(R.id.btnSaveTrip);
        btnAddEvent = findViewById(R.id.btnAddEvent);

        // Get the userTrip object passed from details
        String userTripJson = getIntent().getStringExtra("userTripJson");
        if (userTripJson != null) {
            userTripData = new Gson().fromJson(userTripJson, userTrip.class);
            populateFields();
        }

        // Date pickers
        editStartDate.setOnClickListener(v -> showDatePicker(editStartDate));
        editEndDate.setOnClickListener(v -> showDatePicker(editEndDate));

        // Save button
        btnSave.setOnClickListener(v -> saveTrip());
    }

    private void populateFields() {
        editDestination.setText(userTripData.getCityName());
        editStartDate.setText(userTripData.getStartDate());
        editEndDate.setText(userTripData.getEndDate());
        // TODO: handle events if you store them as a list
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

    private void saveTrip() {
        userTripData.setCityName(editDestination.getText().toString());
        userTripData.setStartDate(editStartDate.getText().toString());
        userTripData.setEndDate(editEndDate.getText().toString());
        // TODO: handle events

        // Return edited trip back to details page
        Intent result = new Intent();
        result.putExtra("editedTripJson", new Gson().toJson(userTripData));
        setResult(RESULT_OK, result);
        finish();
    }
}
