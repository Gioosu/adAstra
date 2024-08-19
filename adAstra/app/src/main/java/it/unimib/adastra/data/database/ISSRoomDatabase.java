package it.unimib.adastra.data.database;

import static it.unimib.adastra.util.Constants.DATABASE_NAME;
import static it.unimib.adastra.util.Constants.DATABASE_VERSION;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import it.unimib.adastra.model.ISS.ISSPositionResponse;

@Database(entities = {ISSPositionResponse.class}, version = DATABASE_VERSION, exportSchema = false)
public abstract class ISSRoomDatabase extends androidx.room.RoomDatabase {
    public abstract ISSDao issDao();
    private static volatile ISSRoomDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = Runtime.getRuntime().availableProcessors();
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static ISSRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (ISSRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            ISSRoomDatabase.class, DATABASE_NAME).build();
                }
            }
        }

        return INSTANCE;
    }
}