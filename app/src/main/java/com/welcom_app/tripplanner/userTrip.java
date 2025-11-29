package com.welcom_app.tripplanner;

import java.util.ArrayList;

public class userTrip {
    private String cityName;
    private String startDate;
    private String endDate;
    private String notes;

    public userTrip(){}
    public userTrip(String cityName, String startDate, String endDate, String notes) {
        this.cityName = cityName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.notes = notes;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getCityName() { return cityName; }
    public String getStartDate() { return startDate; }
    public String getEndDate() { return endDate; }
    public String getNotes() { return notes; }
}