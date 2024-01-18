package it.unimib.adastra.model.ISS;

import androidx.room.Ignore;

import java.util.List;

public class AstroApiResponse extends AstroResponse{
    private String message;

    @Ignore
    public AstroApiResponse() {
        super();
    }

    public AstroApiResponse(String message, List<Astronaut> people, int number) {
        super(people, number);
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
        return "AstroApiResponse{" +
                "message='" + message + '\'' +
                '}';
    }
}
