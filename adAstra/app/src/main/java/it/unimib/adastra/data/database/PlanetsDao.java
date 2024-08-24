package it.unimib.adastra.data.database;

import androidx.room.Dao;

import java.util.List;

import it.unimib.adastra.model.Encyclopedia.Planet;

import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface PlanetsDao {

    @Query("SELECT * FROM Planet")
    List<Planet> getAll();

    @Query("SELECT * FROM Planet WHERE id = :id")
    Planet getById(int id);

    @Query("SELECT * FROM Planet WHERE enName = :name")
    Planet getByName(String name);

    @Insert
    void insert(Planet planet);

    @Insert
    void insertAll(List<Planet> planets);

    @Query("SELECT * FROM Planet")
    List<Planet> getAllPlanets();

    @Query("DELETE FROM Planet")
    void deleteAll();
}
