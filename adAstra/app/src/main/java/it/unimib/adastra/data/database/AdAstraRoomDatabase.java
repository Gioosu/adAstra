package it.unimib.adastra.data.database;

import static it.unimib.adastra.util.Constants.DATABASE_NAME;
import static it.unimib.adastra.util.Constants.DATABASE_VERSION;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import it.unimib.adastra.model.ISS.ISSPositionResponse;
import it.unimib.adastra.model.NASA.NASAResponse;
import it.unimib.adastra.model.wiki.WikiObj;

@Database(entities = {ISSPositionResponse.class, NASAResponse.class, WikiObj.class}, version = DATABASE_VERSION, exportSchema = false)
public abstract class AdAstraRoomDatabase extends androidx.room.RoomDatabase {
    public abstract ISSDao issDao();
    public abstract NASADao nasaDao();
    public abstract WikiDao wikiDao();
    private static volatile AdAstraRoomDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = Runtime.getRuntime().availableProcessors();
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static AdAstraRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AdAstraRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AdAstraRoomDatabase.class, DATABASE_NAME).build();
                }
            }
        }

        return INSTANCE;
    }
}