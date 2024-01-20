package it.unimib.adastra.data.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import it.unimib.adastra.model.ISS.Astronaut;
import it.unimib.adastra.model.ISS.Coordinates;
import it.unimib.adastra.model.ISS.ISSPositionResponse;

@Dao
public interface ISSDao {

    @Query("SELECT * FROM ISSPositionResponse")
    ISSPositionResponse getISS();

    @Query("SELECT longitude, latitude FROM ISSPositionResponse")
    Coordinates getISSPosition();

    @Query("SELECT timestamp FROM ISSPositionResponse")
    long getTimestamp();

    @Query("SELECT * FROM Astronaut")
    List<Astronaut> getAllAstronauts();

    @Insert
    void insertAstronaut(Astronaut astronaut);

    @Insert
    void insertAllAstronauts(Astronaut... astronaut);

    @Insert
    void Astronauts(List<Astronaut> astronauts);

    @Update
    int updateIss(ISSPositionResponse issPosition);

    @Update
    int updateAstronaut(Astronaut astronaut);

    @Update
    int updateAllAstronauts(List<Astronaut> astronauts);

    @Delete
    void deleteISSPositionWithoutQuery(ISSPositionResponse issPosition);

    @Query("DELETE FROM ISSPositionResponse")
    void deleteISSPosition();

    @Delete
    void deleteAllAstronautsWithoutQuery(Astronaut... astronaut);

    @Delete
    void deleteAstronautWithoutQuery(Astronaut astronaut);

    @Query("DELETE FROM Astronaut")
    void deleteAllAstronauts();

    @Query("DELETE FROM Astronaut WHERE name = :name")
    void deleteAstronaut(String name);

}