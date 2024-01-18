package it.unimib.adastra.model.ISS;

import androidx.annotation.NonNull;

public class Coordinate {
    private double longitude;
    private double latitude;

    public Coordinate(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLongitude(double newLongitude) {
        longitude = newLongitude;
    }

    public void setLatitude(double newLatitude) {
        latitude = newLatitude;
    }

    @NonNull
    @Override
    public String toString() {
        return "Coordinate{" +
                "longitude ='" + longitude + '\'' + ", " +
                "latitude ='" + latitude + '\'' +
                '}';
    }
}
