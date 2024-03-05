package it.unimib.adastra.util;
import android.app.Application;

import it.unimib.adastra.data.repository.user.IUserRepository;
import it.unimib.adastra.data.service.ISSApiService;
import it.unimib.adastra.data.database.ISSRoomDatabase;
import it.unimib.adastra.data.source.user.BaseUserAuthenticationRemoteDataSource;
import it.unimib.adastra.data.source.user.BaseUserDataRemoteDataSource;
import it.unimib.adastra.data.source.user.UserAuthenticationRemoteDataSource;
import it.unimib.adastra.data.source.user.UserDataRemoteDataSource;
import it.unimib.adastra.data.repository.user.UserRepository;


import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceLocator {

    private static volatile ServiceLocator INSTANCE = null;
    private static Retrofit retrofit = null;


    private ServiceLocator() {}


    public static ServiceLocator getInstance() {
        if (INSTANCE == null) {
            synchronized(ServiceLocator.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ServiceLocator();
                }
            }
        }
        return INSTANCE;
    }

    public ISSApiService getISSApiService(String id) {


        if(retrofit==null){
            retrofit = new Retrofit.Builder()
                    .baseUrl("https://perenual.com/api/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(ISSApiService.class);
    }

    public ISSRoomDatabase getISSDao(Application application) {
        return ISSRoomDatabase.getDatabase(application);
    }

    public IUserRepository getUserRepository(Application application) {

        BaseUserAuthenticationRemoteDataSource userRemoteAuthenticationDataSource =
                new UserAuthenticationRemoteDataSource();


        BaseUserDataRemoteDataSource userDataRemoteDataSource =
                new UserDataRemoteDataSource();


        return new UserRepository(userRemoteAuthenticationDataSource,
                userDataRemoteDataSource);
    }

}
