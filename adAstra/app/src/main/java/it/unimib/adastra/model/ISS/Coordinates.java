package it.unimib.adastra.model.ISS;

import androidx.room.Ignore;

public class Coordinates {
    private double longitude;
    private double latitude;

    @Ignore
    public Coordinates() {}
    public Coordinates(double longitude, double latitude) {
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

    @Override
    public String toString() {
        return "Coordinate{" +
                "longitude=" + longitude +
                ", latitude=" + latitude +
                '}';
    }
}
