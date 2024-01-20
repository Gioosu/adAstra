package it.unimib.adastra.model.ISS;

import androidx.room.Entity;
import androidx.room.Ignore;

// Class to represent the source of ISS information
@Entity
public class ISSPositionApiResponse extends ISSPositionResponse {
    private String message;

    @Ignore
    public ISSPositionApiResponse(){
        super();
    }

    public ISSPositionApiResponse(String message, Coordinates coordinates, long timestamp) {
        super(coordinates, timestamp);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }


    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "ISSApiResponse{" +
                "message='" + message + '\'' +
                '}';
    }

}