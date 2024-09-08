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

    @Query("SELECT apodMediaType FROM NASAResponse")
    String getNASAResponseMediaType();

    @Query("SELECT apodThumbnailUrl FROM NASAResponse")
    String getNASAResponseThumbnailUrl();

    @Query("SELECT apodCopyright FROM NASAResponse")
    String getNASAResponseCopyright();

    @Query("SELECT apodUrl FROM NASAResponse")
    String getNASAResponseUrl();

    @Insert
    void insertNASA(NASAResponse nasa);

    @Query("DELETE FROM NASAResponse")
    void deleteNASA();
}
