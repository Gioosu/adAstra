package it.unimib.adastra.model.ISS;

import androidx.room.Ignore;

import java.util.List;

public class AstroResponse {

    private List<Astronaut> people;

    private int number;

    @Ignore
    public AstroResponse() {}

    public AstroResponse(List<Astronaut> people, int number) {
        this.people = people;
        this.number = number;
    }


    public List<Astronaut> getPeople() {
        return people;
    }

    public void setPeople(List<Astronaut> people) {
        this.people = people;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return "AstroResponse{" +
                ", people=" + people +
                ", number=" + number +
                '}';
    }
}
