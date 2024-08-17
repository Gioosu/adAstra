package it.unimib.adastra.model.ISS;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Coordinates implements Parcelable {
    private String longitude;
    private String latitude;

    public Coordinates() {}

    public Coordinates(String longitude, String latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public Coordinates(Parcel parcel) {

    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
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
        dest.writeString(this.longitude);
        dest.writeString(this.latitude);
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
