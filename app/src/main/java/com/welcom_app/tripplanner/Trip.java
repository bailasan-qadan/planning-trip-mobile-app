package com.welcom_app.tripplanner;

import java.util.List;

public class Trip {
    private String cityName;
    private String country;
    private String image;
    private int imageResId;

    private String startDate;
    private String endDate;

    private List<String> restaurants;
    private List<String> hotels;
    private String safety;
    private List<String> famous;

    public Trip() {}

    public Trip(String cityName, String country, String image, int imageResId,
                String startDate, String endDate,
                List<String> restaurants, List<String> hotels,
                String safety, List<String> famous) {

        this.cityName = cityName;
        this.country = country;
        this.image = image;
        this.imageResId = imageResId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.restaurants = restaurants;
        this.hotels = hotels;
        this.safety = safety;
        this.famous = famous;
    }

    // Getters
    public String getCityName() { return cityName; }
    public String getCountry() { return country; }
    public String getImage() { return image; }
    public int getImageResId() { return imageResId; }
    public String getStartDate() { return startDate; }
    public String getEndDate() { return endDate; }

    public List<String> getRestaurants() { return restaurants; }
    public List<String> getHotels() { return hotels; }
    public String getSafety() { return safety; }
    public List<String> getFamous() { return famous; }

    // Setters
    public void setCityName(String cityName) { this.cityName = cityName; }
    public void setCountry(String country) { this.country = country; }
    public void setImage(String image) { this.image = image; }
    public void setImageResId(int id) { this.imageResId = id; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }

    public void setRestaurants(List<String> restaurants) { this.restaurants = restaurants; }
    public void setHotels(List<String> hotels) { this.hotels = hotels; }
    public void setSafety(String safety) { this.safety = safety; }
    public void setFamous(List<String> famous) { this.famous = famous; }
}
