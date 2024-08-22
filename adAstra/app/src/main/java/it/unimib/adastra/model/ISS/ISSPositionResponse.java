package it.unimib.adastra.model.ISS;

//Class to represent the source of ISS information
import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity
public class ISSPositionResponse implements Parcelable {

    @PrimaryKey
    private int id;

    private double latitude;
    private double longitude;
    private double altitude;
    private double velocity;
    private String visibility;
    private double footprint;
    private long timestamp;
    private double daynum;
    private double solar_lat;
    private double solar_lon;
    private String units;

    public ISSPositionResponse() {}

    protected ISSPositionResponse(Parcel in) {
       id = in.readInt();
       latitude = in.readDouble();
       longitude = in.readDouble();
       altitude = in.readDouble();
       velocity = in.readDouble();
       visibility = in.readString();
       footprint = in.readDouble();
       timestamp = in.readLong();
       daynum = in.readDouble();
       solar_lat = in.readDouble();
       solar_lon = in.readDouble();
       units = in.readString();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public double getVelocity() {
        return velocity;
    }

    public void setVelocity(double velocity) {
        this.velocity = velocity;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public double getFootprint() {
        return footprint;
    }

    public void setFootprint(double footprint) {
        this.footprint = footprint;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public double getDaynum() {
        return daynum;
    }

    public void setDaynum(double daynum) {
        this.daynum = daynum;
    }

    public double getSolar_lat() {
        return solar_lat;
    }

    public void setSolar_lat(double solar_lat) {
        this.solar_lat = solar_lat;
    }

    public double getSolar_lon() {
        return solar_lon;
    }

    public void setSolar_lon(double solar_lon) {
        this.solar_lon = solar_lon;
    }

    public String getUnits() {
        if (units.equals("kilometers")) {
            units = "km";
        }
        else if (units.equals("miles")) {
            units = "mi";
        }
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
       dest.writeInt(id);
       dest.writeDouble(latitude);
       dest.writeDouble(longitude);
       dest.writeDouble(altitude);
       dest.writeDouble(velocity);
       dest.writeString(visibility);
       dest.writeDouble(footprint);
       dest.writeLong(timestamp);
       dest.writeDouble(daynum);
       dest.writeDouble(solar_lat);
       dest.writeDouble(solar_lon);
       dest.writeString(units);
    }

    public void readFromParcel(Parcel source) {
        this.id = source.readInt();
        this.latitude = source.readDouble();
        this.longitude = source.readDouble();
        this.altitude = source.readDouble();
        this.velocity = source.readDouble();
        this.visibility = source.readString();
        this.footprint = source.readDouble();
        this.timestamp = source.readLong();
        this.daynum = source.readDouble();
        this.solar_lat = source.readDouble();
        this.solar_lon = source.readDouble();
        this.units = source.readString();
    }

    @Override
    public String toString() {
        return "ISSPositionResponse{" +
                "id=" + id +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", altitude=" + altitude +
                ", velocity=" + velocity +
                ", visibility='" + visibility + '\'' +
                ", footprint=" + footprint +
                ", timestamp=" + timestamp +
                ", daynum=" + daynum +
                ", solar_lat=" + solar_lat +
                ", solar_lon=" + solar_lon +
                ", units='" + units + '\'' +
                '}';
    }

    public static final Creator<ISSPositionResponse> CREATOR = new Creator<ISSPositionResponse>() {
        @Override
        public ISSPositionResponse createFromParcel(Parcel in) {
            return new ISSPositionResponse(in);
        }

        @Override
        public ISSPositionResponse[] newArray(int size) {
            return new ISSPositionResponse[size];
        }
    };
}