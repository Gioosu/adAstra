package it.unimib.adastra.util;

import static it.unimib.adastra.util.Constants.ISS_API_BASE_URL;
import static it.unimib.adastra.util.Constants.NASA_API_BASE_URL;

import android.app.Application;

import it.unimib.adastra.data.database.AdAstraRoomDatabase;
import it.unimib.adastra.data.repository.wiki.WikiResponseRepository;
import it.unimib.adastra.data.repository.wiki.IWikiRepository;
import it.unimib.adastra.data.repository.ISSPosition.IISSPositionRepository;
import it.unimib.adastra.data.repository.ISSPosition.ISSPositionResponseRepository;
import it.unimib.adastra.data.repository.NASA.INASARepository;
import it.unimib.adastra.data.repository.NASA.NASAResponseRepository;
import it.unimib.adastra.data.repository.user.IUserRepository;
import it.unimib.adastra.data.service.ISSApiService;
import it.unimib.adastra.data.service.NASAApiService;
import it.unimib.adastra.data.source.wiki.BaseWikiLocalDataSource;
import it.unimib.adastra.data.source.wiki.BaseWikiRemoteDataSource;
import it.unimib.adastra.data.source.wiki.WikiLocalDataSource;
import it.unimib.adastra.data.source.wiki.WikiRemoteDataSource;
import it.unimib.adastra.data.source.ISS.BaseISSPositionLocalDataSource;
import it.unimib.adastra.data.source.ISS.BaseISSPositionRemoteDataSource;
import it.unimib.adastra.data.source.ISS.ISSPositionLocalDataSource;
import it.unimib.adastra.data.source.ISS.ISSPositionRemoteDataSource;
import it.unimib.adastra.data.source.NASA.BaseNASALocalDataSource;
import it.unimib.adastra.data.source.NASA.BaseNASARemoteDataSource;
import it.unimib.adastra.data.source.NASA.NASALocalDataSource;
import it.unimib.adastra.data.source.NASA.NASARemoteDataSource;
import it.unimib.adastra.data.source.user.BaseUserAuthenticationRemoteDataSource;
import it.unimib.adastra.data.source.user.BaseUserDataRemoteDataSource;
import it.unimib.adastra.data.source.user.UserAuthenticationRemoteDataSource;
import it.unimib.adastra.data.source.user.UserDataRemoteDataSource;
import it.unimib.adastra.data.repository.user.UserRepository;


import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceLocator {
    private static volatile ServiceLocator INSTANCE = null;
    private static Retrofit issRetrofit = null;
    private static Retrofit nasaRetrofit = null;

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

    public ISSApiService getISSApiService() {
        if(issRetrofit == null){
            issRetrofit = new Retrofit.Builder()
                    .baseUrl(ISS_API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return issRetrofit.create(ISSApiService.class);
    }

    public NASAApiService getNASAApiService() {
        if (nasaRetrofit == null) {
            nasaRetrofit = new Retrofit.Builder()
                    .baseUrl(NASA_API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return nasaRetrofit.create(NASAApiService.class);
    }

    public AdAstraRoomDatabase getISSDao(Application application) {
        return AdAstraRoomDatabase.getDatabase(application);
    }

    public AdAstraRoomDatabase getNASADao(Application application) {
        return AdAstraRoomDatabase.getDatabase(application);
    }

    public AdAstraRoomDatabase getWikiDao(Application application) {
        return AdAstraRoomDatabase.getDatabase(application);
    }

    public IISSPositionRepository getISSRepository(Application application) {
        BaseISSPositionRemoteDataSource issPositionRemoteDataSource =
                new ISSPositionRemoteDataSource();

        BaseISSPositionLocalDataSource issPositionLocalDataSource =
                new ISSPositionLocalDataSource(getISSDao(application));

        return new ISSPositionResponseRepository(issPositionRemoteDataSource,
                issPositionLocalDataSource);
    }

    public INASARepository getNASARepository(Application application) {
        BaseNASARemoteDataSource nasaRemoteDataSource =
                new NASARemoteDataSource();

        BaseNASALocalDataSource nasaLocalDataSource =
                new NASALocalDataSource(getNASADao(application));

        return new NASAResponseRepository(nasaRemoteDataSource, nasaLocalDataSource);
    }

    public IUserRepository getUserRepository(Application application) {
        BaseUserAuthenticationRemoteDataSource userRemoteAuthenticationDataSource =
                new UserAuthenticationRemoteDataSource();

        BaseUserDataRemoteDataSource userDataRemoteDataSource =
                new UserDataRemoteDataSource();

        return new UserRepository(userRemoteAuthenticationDataSource,
                userDataRemoteDataSource);
    }

    public IWikiRepository getWikiRepository(Application application) {
        BaseWikiRemoteDataSource wikiRemoteDataSource =
                new WikiRemoteDataSource();

        BaseWikiLocalDataSource wikiLocalDataSource =
                new WikiLocalDataSource(getWikiDao(application));

        return new WikiResponseRepository(wikiLocalDataSource, wikiRemoteDataSource);
    }
}