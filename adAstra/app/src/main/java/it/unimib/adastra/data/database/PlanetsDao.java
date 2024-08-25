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

    @Insert
    void insert(Planet planet);

    @Insert
    void insertAll(List<Planet> planets);

    @Query("SELECT * FROM Planet")
    List<Planet> getAllPlanets();

    @Query("DELETE FROM Planet")
    void deleteAll();

    @Query("SELECT language FROM Planet WHERE id = 1")
    String getCurrentLanguage();
}
