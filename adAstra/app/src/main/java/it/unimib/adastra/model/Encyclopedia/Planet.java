package it.unimib.adastra.model.Encyclopedia;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Planet {

    @PrimaryKey
    int id;

    private String language;

    private String name;
    private String description;
    private String earthDistance;
    private int moons;
    public Planet(int id, String language, String name, String description, String earthDistance, int moons) {
        this.id = id;
        this.language = language;
        this.name = name;
        this.description = description;
        this.earthDistance = earthDistance;
        this.moons = moons;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEarthDistance() {
        return earthDistance;
    }

    public void setEarthDistance(String earthDistance) {
        this.earthDistance = earthDistance;
    }

    public int getMoons() {
        return moons;
    }

    public void setMoons(int moons) {
        this.moons = moons;
    }

    @Override
    public String toString() {
        return "Planet{" +
                "id=" + id +
                ", language='" + language + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", earthDistance='" + earthDistance + '\'' +
                ", moons=" + moons +
                '}';
    }
}
