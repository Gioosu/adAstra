package it.unimib.adastra.model.ISS;

import androidx.room.Entity;
import androidx.room.Ignore;

// Class to represent the source of ISS information
@Entity
public class ISSApiResponse extends ISSResponse{
    private String message;

    @Ignore
    public ISSApiResponse(){
        super();
    }

    public ISSApiResponse(String message, Coordinate coordinate, long timestamp) {
        super(coordinate, timestamp);
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
