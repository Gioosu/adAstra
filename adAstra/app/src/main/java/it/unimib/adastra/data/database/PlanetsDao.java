package it.unimib.adastra.data.database;

import androidx.room.Dao;

import java.util.List;

import it.unimib.adastra.model.wiki.Planet;

import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface PlanetsDao {

    @Query("SELECT * FROM Planet")
    List<Planet> getAll();

    @Insert
    void insertAll(List<Planet> planets);

    @Query("SELECT * FROM Planet")
    List<Planet> getAllPlanets();

    @Query("DELETE FROM Planet")
    void deleteAll();

    @Query("SELECT language FROM Planet LIMIT 1")
    String getCurrentLanguage();

    @Update
    int updateAll(List<Planet> planets);
}
