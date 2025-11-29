package com.welcom_app.tripplanner;

import java.util.ArrayList;

public class userTrip {
    private String cityName;     // Destination chosen by the user
    private String startDate;    // User’s start date
    private String endDate;      // User’s end date
    private String notes;        // Description or events user writes

    public userTrip(String cityName, String startDate, String endDate, String notes) {
        this.cityName = cityName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.notes = notes;
    }

    public String getCityName() { return cityName; }
    public String getStartDate() { return startDate; }
    public String getEndDate() { return endDate; }
    public String getNotes() { return notes; }
}