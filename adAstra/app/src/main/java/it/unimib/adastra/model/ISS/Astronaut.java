package it.unimib.adastra.model.ISS;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Astronaut {
    //User for Room
    @PrimaryKey
    @ColumnInfo(name = "name")
    @NonNull
    private String name;
    private String craft;

    public Astronaut() {}

    public Astronaut(String name, String craft) {
        this.name = name;
        this.craft = craft;
    }

    public String getName() {
        return name;
    }

    public String getCraft() {
        return craft;
    }

    public void setName(String newName) {
        name = newName;
    }

    public void setCraft(String newCraft) {
        craft = newCraft;
    }

    @Override
    public String toString() {
        return "Astronaut{" +
                "name='" + name + '\'' +
                ", craft='" + craft + '\'' +
                '}';
    }
}