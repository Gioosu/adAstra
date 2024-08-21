package it.unimib.adastra.data.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import it.unimib.adastra.model.NASA.NASAResponse;

@Dao
public interface NASADao {

    @Query("SELECT * FROM NASAResponse")
    NASAResponse getNASAResponse();

    @Query("SELECT apodTitle FROM NASAResponse")
    String getNASAResponseTitle();

    @Query("SELECT apodDate FROM NASAResponse")
    String getNASAResponseDate();

    @Query("SELECT apodExplanation FROM NASAResponse")
    String getNASAResponseExplanation();

    @Query("SELECT apodUrl FROM NASAResponse")
    String getNASAResponseUrl();

    @Update
    int updateNASA(NASAResponse nasa);

    @Insert
    void insertNASA(NASAResponse nasa);

    @Query("DELETE FROM NASAResponse")
    void deleteNASA();
}
