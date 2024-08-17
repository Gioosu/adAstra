package it.unimib.adastra.model.ISS;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Coordinates implements Parcelable {
    private double longitude;
    private double latitude;

    public Coordinates() {}

    public Coordinates(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public Coordinates(Parcel parcel) {

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int Flags) {
        dest.writeDouble(this.longitude);
        dest.writeDouble(this.latitude);
    }

    public static final Creator<Coordinates> CREATOR = new Creator<Coordinates>() {
        @Override
        public Coordinates createFromParcel(Parcel source) {
            return new Coordinates(source);
        }

        @Override
        public Coordinates[] newArray(int i) {
            return new Coordinates[i];
        }
    };
}
