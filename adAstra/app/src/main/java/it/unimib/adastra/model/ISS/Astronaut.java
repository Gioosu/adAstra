package it.unimib.adastra.model.ISS;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class Astronaut {

    //User for Room
    @PrimaryKey(autoGenerate = true)
    private long id;
    
    @ColumnInfo(name = "name")
    private String name;

    @Ignore
    public Astronaut() {}

    public Astronaut(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public long getId() {
        return id;
    }

    public void setName(String newName) {
        name = newName;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Astronaut{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
