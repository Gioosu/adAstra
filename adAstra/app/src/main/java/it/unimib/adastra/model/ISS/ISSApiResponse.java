package it.unimib.adastra.model.ISS;

//Class to represent the source of ISS information

import com.google.gson.annotations.SerializedName;

public class ISSApiResponse {
    private int timestamp;
    @SerializedName("iss_position")
    private Coordinate coordinate;
    private String message;

    public ISSApiResponse(){}

    public ISSApiResponse(int timestamp, Coordinate coordinate, String message) {
        this.timestamp = timestamp;
        this.coordinate = coordinate;
        this.message = message;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public String getMessage() {
        return message;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "ISSApiResponse{" +
                "timestamp=" + timestamp +
                ", coordinate=" + coordinate +
                ", message='" + message + '\'' +
                '}';
    }
}
