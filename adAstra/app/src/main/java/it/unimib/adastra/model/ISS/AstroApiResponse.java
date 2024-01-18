package it.unimib.adastra.model.ISS;

import java.util.List;

public class AstroApiResponse {
    private String message;
    private List<Astronaut> people;
    private int number;

    public AstroApiResponse() {}

    public AstroApiResponse(String message, List<Astronaut> people, int number) {
        this.message = message;
        this.people = people;
        this.number = number;
    }

    public String getMessage() {
        return message;
    }

    public List<Astronaut> getPeople() {
        return people;
    }

    public int getNumber() {
        return number;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setPeople(List<Astronaut> people) {
        this.people = people;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return "AstroApiResponse{" +
                "message='" + message + '\'' +
                ", people=" + people +
                ", number=" + number +
                '}';
    }
}
