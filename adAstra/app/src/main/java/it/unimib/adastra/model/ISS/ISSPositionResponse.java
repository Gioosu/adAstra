package it.unimib.adastra.model.ISS;

//Class to represent the source of ISS information
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity
public class ISSPositionResponse {

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

    @Override
    public String toString() {
        return "ISSResponse{" +
                "coordinate=" + coordinates +
                ", timestamp=" + timestamp +
        '}';
    }
}