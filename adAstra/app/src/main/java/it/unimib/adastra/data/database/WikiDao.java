package it.unimib.adastra.data.database;

import androidx.room.Dao;

import java.util.List;

import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import it.unimib.adastra.model.wiki.WikiObj;

@Dao
public interface WikiDao {

    @Insert()
    void insertAll(List<WikiObj> wikiObj);

    @Query("SELECT * FROM wikiObj WHERE type = :type")
    List<WikiObj> getAllWikiObj(String type);

    @Query("SELECT language FROM wikiObj WHERE type = :type LIMIT 1 ")
    String getCurrentLanguage(String type);

    @Update
    int updateAll(List<WikiObj> wikiObj);
}
