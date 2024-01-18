package it.unimib.adastra.model.ISS;

import androidx.annotation.NonNull;

public class Astronaut {
    private String name;
    private String craft;

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

    @NonNull
    @Override
    public String toString() {
        return "Astronaut{" +
                "name ='" + name + '\'' + ", " +
                "craft ='" + craft + '\'' +
                '}';
    }
}
