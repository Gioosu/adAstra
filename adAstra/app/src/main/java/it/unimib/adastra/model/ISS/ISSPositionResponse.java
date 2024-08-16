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

    private boolean isLoading;

    @SerializedName("iss_position")
    @Embedded
    private Coordinates coordinates;

    @PrimaryKey
    private long timestamp;

    public ISSPositionResponse() {
    }

    public ISSPositionResponse(Coordinates coordinates, long timestamp) {
        this.coordinates = coordinates;
        this.timestamp = timestamp;
    }

    protected ISSPositionResponse(Parcel in) {
        isLoading = in.readByte() != 0;
        coordinates = in.readParcelable(Coordinates.class.getClassLoader());
        timestamp = in.readLong();
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public long getTimestamp() {return timestamp;}

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isLoading() { return isLoading; }

    public void setLoading(boolean isLoading) { this.isLoading = isLoading; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.isLoading ? (byte) 1 : (byte) 0);
        dest.writeTypedObject(this.coordinates, flags);
    }

    public void readFromParcel(Parcel source) {
        this.isLoading = source.readByte() != 0;
        this.coordinates = source.readTypedObject(Coordinates.CREATOR);
    }

    @Override
    public String toString() {
        return "ISSPositionResponse{" +
                "isLoading=" + isLoading +
                ", coordinates=" + coordinates +
                ", timestamp=" + timestamp +
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