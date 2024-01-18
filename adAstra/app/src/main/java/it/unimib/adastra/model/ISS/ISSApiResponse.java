package it.unimib.adastra.model.ISS;

//Class to represent the source of ISS information

import com.google.gson.annotations.SerializedName;

public class ISSApiResponse {

    private int timestamp;

    @SerializedName("iss_position")
    private Coordinate coordinate;

    private String message;

}
