package it.unimib.adastra.model.wiki;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Planet {

    @PrimaryKey
    int id;

    private String name;
    private String description;
    private String earthDistance;
    private int moons;

    private String language;

    public Planet(int id, String name, String description, String earthDistance, int moons, String language) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.earthDistance = earthDistance;
        this.moons = moons;
        this.language = language;
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
                "id = " + id +
                ", name = '" + name + '\'' +
                ", description = '" + description + '\'' +
                ", earthDistance = '" + earthDistance + '\'' +
                ", moons =" + moons +
                ", language = '" + language + '\'' +
                '}';
    }
}
