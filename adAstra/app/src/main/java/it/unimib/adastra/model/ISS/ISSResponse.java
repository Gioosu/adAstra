package it.unimib.adastra.model.ISS;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity
public class ISSResponse {

    @SerializedName("iss_position")
    @Embedded
    private Coordinate coordinate;

    @PrimaryKey
    private long timestamp;

    public ISSResponse() {
    }

    public ISSResponse(Coordinate coordinate, long timestamp) {
        this.coordinate = coordinate;
        this.timestamp = timestamp;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "ISSResponse{" +
                "coordinate=" + coordinate +
                ", timestamp=" + timestamp +
                '}';
    }
}
