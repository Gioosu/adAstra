package it.unimib.adastra.data.database;

import androidx.room.Dao;

import java.util.List;

import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import it.unimib.adastra.model.wiki.WikiObj;

@Dao
public interface PlanetsDao {

    @Query("SELECT * FROM WikiObj")
    List<WikiObj> getAll();

    @Insert
    void insertAll(List<WikiObj> wikiObjs);

    @Query("SELECT * FROM WikiObj")
    List<WikiObj> getAllPlanets();

    @Query("DELETE FROM WikiObj")
    void deleteAll();

    @Query("SELECT language FROM WikiObj LIMIT 1")
    String getCurrentLanguage();

    @Update
    int updateAll(List<WikiObj> wikiObjs);
}
