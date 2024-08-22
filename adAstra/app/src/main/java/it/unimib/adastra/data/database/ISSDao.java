package it.unimib.adastra.data.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import it.unimib.adastra.model.ISS.ISSPositionResponse;

@Dao
public interface ISSDao {

    @Query("SELECT * FROM ISSPositionResponse")
    ISSPositionResponse getISS();

    @Query("SELECT latitude FROM ISSPositionResponse")
    double getLatitude();

    @Query("SELECT longitude FROM ISSPositionResponse")
    double getLongitude();

    @Query("SELECT altitude FROM ISSPositionResponse")
    double getAltitude();

    @Query("SELECT velocity FROM ISSPositionResponse")
    double getVelocity();

    @Query("SELECT visibility FROM ISSPositionResponse")
    String getVisibility();

    @Query("SELECT footprint FROM ISSPositionResponse")
    double getFootprint();

    @Query("SELECT timestamp FROM ISSPositionResponse")
    long getTimestamp();

    @Query("SELECT daynum FROM ISSPositionResponse")
    double getDaynum();

    @Query("SELECT solar_lat FROM ISSPositionResponse")
    double getSolarLat();

    @Query("SELECT solar_lon FROM ISSPositionResponse")
    double getSolarLon();

    @Query("SELECT units FROM ISSPositionResponse")
    String getUnits();

    @Update
    int updateIss(ISSPositionResponse issPosition);

    @Insert
    void insertIss(ISSPositionResponse issPosition);

    @Delete
    void deleteISSPositionWithoutQuery(ISSPositionResponse issPosition);

    @Query("DELETE FROM ISSPositionResponse")
    void deleteISSPosition();
}