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

    private String itName;
    private String enName;
    private String itDescription;
    private String enDescription;
    private double earthDistance;
    private String moons;

    public Planet(int id, String itName, String enName, String itDescription, String enDescription, double earthDistance, String moons) {
        this.id = id;
        this.itName = itName;
        this.enName = enName;
        this.itDescription = itDescription;
        this.enDescription = enDescription;
        this.earthDistance = earthDistance;
        this.moons = moons;
    }

    @Ignore
    public Planet(int id, String itName, String enName, String itDescription, String enDescription) {
        this.id = id;
        this.itName = itName;
        this.enName = enName;
        this.itDescription = itDescription;
        this.enDescription = enDescription;
        this.earthDistance = 0;
        this.moons = "";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getItName() {
        return itName;
    }

    public void setItName(String itName) {
        this.itName = itName;
    }

    public String getEnName() {
        return enName;
    }

    public void setEnName(String enName) {
        this.enName = enName;
    }

    public String getItDescription() {
        return itDescription;
    }

    public void setItDescription(String itDescription) {
        this.itDescription = itDescription;
    }

    public String getEnDescription() {
        return enDescription;
    }

    public void setEnDescription(String enDescription) {
        this.enDescription = enDescription;
    }

    public double getEarthDistance() {
        return earthDistance;
    }

    public void setEarthDistance(double earthDistance) {
        this.earthDistance = earthDistance;
    }

    public String getMoons() {
        return moons;
    }

    public void setMoons(String moons) {
        this.moons = moons;
    }
}
